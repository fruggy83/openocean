/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.openocean.internal.transceiver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.TooManyListenersException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;

import org.openhab.binding.openocean.internal.OpenOceanException;
import org.openhab.binding.openocean.internal.messages.ERP1Message;
import org.openhab.binding.openocean.internal.messages.ESP3Packet;
import org.openhab.binding.openocean.internal.messages.ESP3Packet.ESPPacketType;
import org.openhab.binding.openocean.internal.messages.ESP3PacketFactory;
import org.openhab.binding.openocean.internal.messages.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

/**
 *
 * @author Daniel Weber - Initial contribution
 */
public abstract class OpenOceanTransceiver {

    // Thread management
    private Future<?> readingTask;
    private Future<?> timeOut;

    protected String path;

    private Logger logger = LoggerFactory.getLogger(OpenOceanTransceiver.class);

    class Request {
        ESP3Packet RequestPacket;

        Response ResponsePacket;
        ResponseListener<? extends Response> ResponseListener;
    }

    private class RequestQueue {
        private Queue<Request> queue = new LinkedBlockingQueue<>();
        private ScheduledExecutorService scheduler;

        public RequestQueue(ScheduledExecutorService scheduler) {
            this.scheduler = scheduler;
        }

        public synchronized void enqueRequest(Request request) throws IOException {
            boolean wasEmpty = queue.isEmpty();

            if (queue.offer(request)) {
                if (wasEmpty) {
                    send();
                }
            } else {
                logger.error("Transmit queue overflow. Lost message: {}", request);
            }
        }

        public synchronized void sendNext() throws IOException {
            // if (timeOut != null) {
            // timeOut.cancel(true);
            // }
            queue.poll();
            send();
        }

        private synchronized void send() throws IOException {
            if (!queue.isEmpty()) {

                currentRequest = queue.peek();
                try {
                    if (currentRequest != null && currentRequest.RequestPacket != null) {
                        synchronized (currentRequest) {

                            logger.trace("sending request");

                            byte[] b = currentRequest.RequestPacket.serialize();

                            if (logger.isDebugEnabled()) {
                                int[] i = new int[b.length];
                                // array copy with conversion byte -> int
                                for (int j = 0; j < b.length; i[j] = b[j++]) {
                                    ;
                                }
                                logger.debug("{}", Helper.bytesToHexString(i));
                            }

                            /*
                             * timeOut = scheduler.schedule(() -> {
                             * try {
                             * sendNext();
                             * } catch (IOException e) {
                             * errorListener.ErrorOccured(e);
                             * return;
                             * }
                             * }, 2000, TimeUnit.MILLISECONDS);
                             */

                            outputStream.write(b);
                            outputStream.flush();

                        }
                    } else {
                        sendNext();
                    }
                } catch (OpenOceanException e) {
                    logger.error("exception while sending data {}", e);
                }
            }
        }
    }

    RequestQueue requestQueue;
    Request currentRequest = null;

    protected Map<Long, ESP3PacketListener> listeners;
    protected ESP3PacketListener teachInListener;

    // Input and output streams, must be created by transceiver implementations
    protected InputStream inputStream;
    protected OutputStream outputStream;

    private int[] filteredDeviceId;
    TransceiverErrorListener errorListener;

    enum ReadingState {
        WaitingForSyncByte,
        ReadingHeader,
        ReadingData
    }

    public OpenOceanTransceiver(String path, TransceiverErrorListener errorListener,
            ScheduledExecutorService scheduler) {
        this.path = path;

        requestQueue = new RequestQueue(scheduler);
        listeners = new HashMap<Long, ESP3PacketListener>();
        teachInListener = null;
        this.errorListener = errorListener;
    }

    public abstract void Initialize() throws UnsupportedCommOperationException, NoSuchPortException, PortInUseException,
            IOException, TooManyListenersException;

    public void StartReceiving(ScheduledExecutorService scheduler) {

        if (readingTask == null || readingTask.isCancelled()) {
            readingTask = scheduler.submit(new Runnable() {

                @Override
                public void run() {
                    receivePackets();
                }

            });
        }
    }

    public void ShutDown() {

        logger.debug("Interrupt rx Thread");
        if (readingTask != null) {
            readingTask.cancel(true);
        }

        synchronized (this) {
            this.notify();
        }

        readingTask = null;
        listeners.clear();
        teachInListener = null;
        errorListener = null;
    }

    private void receivePackets() {

        byte[] readingBuffer = new byte[Helper.ENOCEAN_MAX_DATA];
        int bytesRead = -1;
        int _byte;

        int[] dataBuffer = new int[Helper.ENOCEAN_MAX_DATA];
        ReadingState state = ReadingState.WaitingForSyncByte;
        int currentPosition = 0;
        int dataLength = -1;
        int optionalLength = -1;
        int packetType = -1;

        logger.debug("Listening on port: {}", path);

        while (readingTask != null && !readingTask.isCancelled()) {
            try {

                synchronized (this) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        break;
                    }
                }

                if (readingTask.isCancelled()) {
                    return;
                }

                bytesRead = this.inputStream.read(readingBuffer, 0, inputStream.available());
                if (bytesRead == -1) {
                    throw new IOException("could not read from inputstream");
                }

                for (int p = 0; p < bytesRead; p++) {
                    _byte = (readingBuffer[p] & 0xff);

                    switch (state) {
                        case WaitingForSyncByte:
                            if (_byte == Helper.ENOCEAN_SYNC_BYTE) {
                                state = ReadingState.ReadingHeader;
                                logger.trace("Received Sync Byte");
                            }
                            break;
                        case ReadingHeader:
                            if (currentPosition == Helper.ENOCEAN_HEADER_LENGTH) {
                                if (Helper.checkCRC8(dataBuffer, Helper.ENOCEAN_HEADER_LENGTH, _byte)
                                        && ((dataBuffer[0] << 8) | dataBuffer[1]) + dataBuffer[2] > 0) {

                                    state = ReadingState.ReadingData;

                                    dataLength = ((dataBuffer[0] << 8) | dataBuffer[1]);
                                    optionalLength = dataBuffer[2];
                                    packetType = dataBuffer[3];
                                    currentPosition = 0;

                                    if (packetType == 3) {
                                        logger.trace("Received sub_msg");
                                    }

                                    logger.trace("Received header, data length {} optional length {} packet type {}",
                                            dataLength, optionalLength, packetType);
                                } else {
                                    // check if we find a sync byte in current buffer
                                    int copyFrom = -1;
                                    for (int i = 0; i < Helper.ENOCEAN_HEADER_LENGTH; i++) {
                                        if (dataBuffer[i] == Helper.ENOCEAN_SYNC_BYTE) {
                                            copyFrom = i + 1;
                                            break;
                                        }
                                    }

                                    if (copyFrom != -1) {
                                        System.arraycopy(dataBuffer, copyFrom, dataBuffer, 0,
                                                Helper.ENOCEAN_HEADER_LENGTH - copyFrom);
                                        state = ReadingState.ReadingHeader;
                                        currentPosition = Helper.ENOCEAN_HEADER_LENGTH - copyFrom;
                                        dataBuffer[currentPosition++] = _byte;
                                    } else {
                                        currentPosition = 0;
                                        state = _byte == Helper.ENOCEAN_SYNC_BYTE ? ReadingState.ReadingHeader
                                                : ReadingState.WaitingForSyncByte;
                                    }
                                    logger.trace("CrC8 header check not successful");
                                }
                            } else {
                                dataBuffer[currentPosition++] = _byte;
                            }
                            break;
                        case ReadingData:
                            if (currentPosition == dataLength + optionalLength) {
                                if (Helper.checkCRC8(dataBuffer, dataLength + optionalLength, _byte)) {
                                    state = ReadingState.WaitingForSyncByte;
                                    ESP3Packet packet = ESP3PacketFactory.BuildPacket(dataLength, optionalLength,
                                            packetType, dataBuffer);

                                    if (packet != null) {
                                        if (packet.getPacketType() == ESPPacketType.RESPONSE) {
                                            logger.trace("publish response");


                                            if (currentRequest != null) {
                                                if (currentRequest.ResponseListener != null) {

                                                    logger.trace("response received");
                                                    currentRequest.ResponsePacket = (Response) packet;
                                                    try {
                                                        currentRequest.ResponseListener
                                                                .handleResponse(currentRequest.ResponsePacket);
                                                    } catch (Exception e) {
                                                    }

                                                    logger.trace("handled request");
                                                } else {
                                                    logger.trace("request without listener");
                                                }
                                            }
                                        } else if (packet instanceof ERP1Message) {

                                            ERP1Message msg = (ERP1Message) packet;

                                            logger.trace("publish event for: {}",
                                                    Helper.bytesToHexString(msg.getSenderId()));

                                            int[] d = new int[dataLength + optionalLength];
                                            System.arraycopy(dataBuffer, 0, d, 0, d.length);
                                            logger.trace("{}", Helper.bytesToHexString(d));

                                            informListeners(msg);
                                        }
                                    } else {
                                        logger.trace("Unknown ESP3Packet");
                                        int[] d = new int[dataLength + optionalLength];
                                        System.arraycopy(dataBuffer, 0, d, 0, d.length);
                                        logger.trace("{}", Helper.bytesToHexString(d));
                                    }

                                    requestQueue.sendNext();

                                } else {
                                    state = _byte == Helper.ENOCEAN_SYNC_BYTE ? ReadingState.ReadingHeader
                                            : ReadingState.WaitingForSyncByte;
                                    logger.trace("esp packet malformed");
                                }

                                currentPosition = 0;
                                dataLength = optionalLength = packetType = -1;
                            } else {
                                dataBuffer[currentPosition++] = _byte;
                            }
                            break;
                        default:
                            break;
                    }
                }
            } catch (IOException ioexception) {
                errorListener.ErrorOccured(ioexception);
                return;
            }
        }

        logger.debug("finished listening");
    }

    public void sendESP3Packet(ESP3Packet packet, ResponseListener<? extends Response> responseCallback)
            throws IOException {

        if (packet == null) {
            return;
        }

        logger.debug("new request arrived");
        Request r = new Request();
        r.RequestPacket = packet;
        r.ResponseListener = responseCallback;

        requestQueue.enqueRequest(r);
    }

    protected void informListeners(ERP1Message msg) {

        try {
            int[] senderId = msg.getSenderId();

            if (senderId != null) {

                if (filteredDeviceId != null && senderId[0] == filteredDeviceId[0] && senderId[1] == filteredDeviceId[1]
                        && senderId[2] == filteredDeviceId[2]) {
                    // filter away own messages which are received through a repeater
                    return;
                }

                if (msg.getIsTeachIn()) {
                    if (teachInListener != null) {
                        logger.info("Received teach in message from {}", Helper.bytesToHexString(msg.getSenderId()));
                        teachInListener.espPacketReceived(msg);
                        return;
                    }
                }

                long s = Long.parseLong(Helper.bytesToHexString(senderId), 16);
                ESP3PacketListener listener = listeners.get(s);
                if (listener != null) {
                    listener.espPacketReceived(msg);
                }
            }
        } catch (Exception e) {

        }
    }

    public void addPacketListener(ESP3PacketListener listener) {
        listeners.putIfAbsent(listener.getSenderIdToListenTo(), listener);
        logger.debug("Listener added: {}", listener.getSenderIdToListenTo());
    }

    public void removePacketListener(ESP3PacketListener listener) {
        listeners.remove(listener.getSenderIdToListenTo());
    }

    public void startDiscovery(ESP3PacketListener teachInListener) {
        this.teachInListener = teachInListener;
    }

    public void stopDiscovery() {
        this.teachInListener = null;
    }

    public void setFilteredDeviceId(int[] filteredDeviceId) {
        if (filteredDeviceId != null) {
            System.arraycopy(filteredDeviceId, 0, filteredDeviceId, 0, filteredDeviceId.length);
        }
    }
}
