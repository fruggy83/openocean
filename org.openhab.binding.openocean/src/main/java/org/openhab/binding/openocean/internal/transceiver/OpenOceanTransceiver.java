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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import org.openhab.binding.openocean.internal.OpenOceanException;
import org.openhab.binding.openocean.internal.messages.ERP1Message;
import org.openhab.binding.openocean.internal.messages.ESP3Packet;
import org.openhab.binding.openocean.internal.messages.ESP3Packet.ESPPacketType;
import org.openhab.binding.openocean.internal.messages.ESP3PacketFactory;
import org.openhab.binding.openocean.internal.messages.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Daniel Weber - Initial contribution
 */
public abstract class OpenOceanTransceiver {

    // Thread management
    private Future<?> readingTask;
    protected Object syncObj;

    protected String path;

    private Logger logger = LoggerFactory.getLogger(OpenOceanTransceiver.class);

    class Request {
        ESP3Packet RequestPacket;

        Response ResponsePacket;
        ResponseListener<? extends Response> ResponseListener;
    }

    private Queue<Request> requests;
    Request currentRequest = null;
    private Future<?> sendingTask;

    protected Map<Long, List<ESP3PacketListener>> listeners;
    protected ESP3PacketListener teachInListener;

    // Input and output streams, must be created by transceiver implementations
    protected InputStream inputStream;
    protected OutputStream outputStream;

    enum ReadingState {
        WaitingForSyncByte,
        ReadingHeader,
        ReadingData
    }

    public OpenOceanTransceiver(String path) {
        this.path = path;
        this.syncObj = new Object();

        requests = new LinkedList<Request>();
        listeners = new HashMap<Long, List<ESP3PacketListener>>();
        teachInListener = null;
    }

    public abstract void Initialize() throws OpenOceanException;

    public void StartSendingAndReading(ScheduledExecutorService scheduler) {
        if (readingTask == null || readingTask.isCancelled()) {
            readingTask = scheduler.submit(new Runnable() {

                @Override
                public void run() {
                    receivePackets();
                }

            });
        }

        if (sendingTask == null || sendingTask.isCancelled()) {
            sendingTask = scheduler.submit(new Runnable() {

                @Override
                public void run() {
                    sendPackets();
                }

            });
        }
    }

    public void ShutDown() {
        if (readingTask != null && !readingTask.isCancelled()) {
            readingTask.cancel(true);
        }

        if (sendingTask != null && !sendingTask.isCancelled()) {
            sendingTask.cancel(true);
        }
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

        while (!readingTask.isCancelled()) {
            try {
                synchronized (this.syncObj) {
                    if (this.inputStream.available() == 0) {
                        this.syncObj.wait();
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
                                logger.debug("Received Sync Byte");
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

                                    logger.debug("Received header, data length {} optional length {} packet type {}",
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
                                    logger.debug("CrC8 header check not successful");
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
                                        if (packet.getPacketType() == ESPPacketType.RESPONSE
                                                && currentRequest != null) {
                                            logger.debug("publish response");
                                            synchronized (currentRequest) {
                                                currentRequest.ResponsePacket = (Response) packet;
                                                currentRequest.notify();
                                            }
                                        } else if (packet instanceof ERP1Message) {

                                            ERP1Message msg = (ERP1Message) packet;

                                            logger.debug("publish event for: {}",
                                                    Helper.bytesToHexString(msg.getSenderId()));

                                            int[] d = new int[dataLength + optionalLength];
                                            System.arraycopy(dataBuffer, 0, d, 0, d.length);
                                            logger.info("{}", Helper.bytesToHexString(d));

                                            informListeners(msg);
                                        }
                                    } else {
                                        logger.debug("Unknown ESP3Packet");
                                        int[] d = new int[dataLength + optionalLength];
                                        System.arraycopy(dataBuffer, 0, d, 0, d.length);
                                        logger.debug("{}", Helper.bytesToHexString(d));
                                    }
                                } else {
                                    state = _byte == Helper.ENOCEAN_SYNC_BYTE ? ReadingState.ReadingHeader
                                            : ReadingState.WaitingForSyncByte;
                                    logger.debug("esp packet malformed");
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
                logger.debug("{}", ioexception.getMessage()); // ioexception.printStackTrace();
            } catch (InterruptedException interruptedexception) {
                logger.debug("receiving packets interrupted");
            }
        }

        logger.debug("finished listening");

    }

    public void sendESP3Packet(ESP3Packet packet, ResponseListener<? extends Response> responseCallback) {

        synchronized (requests) {
            logger.debug("new request arrived");
            Request r = new Request();
            r.RequestPacket = packet;
            r.ResponseListener = responseCallback;

            requests.offer(r);
            requests.notify();
        }
    }

    protected void sendPackets() {

        logger.debug("start sending packets");

        while (!sendingTask.isCancelled()) {
            try {

                synchronized (requests) {
                    if (requests.isEmpty()) {
                        logger.debug("awaiting packets to send");
                        requests.wait();
                    }
                    currentRequest = requests.poll();
                }

                if (sendingTask.isCancelled()) {
                    return;
                }

                if (currentRequest != null) {
                    synchronized (currentRequest) {

                        if (currentRequest.RequestPacket == null) {
                            continue;
                        }

                        logger.debug("sending request");

                        try {
                            byte[] b = currentRequest.RequestPacket.serialize();
                            int[] i = new int[b.length];
                            for (int j = 0; j < b.length; i[j] = b[j++]) {
                                ;
                            }
                            logger.debug("{}", Helper.bytesToHexString(i));
                            outputStream.write(b);
                            outputStream.flush();
                        } catch (IOException e) {
                            logger.debug("IO exception while sending data {}", e.getMessage());
                            currentRequest = null;
                            continue;
                        } catch (Exception e) {
                            logger.debug("exception while sending data {}", e.getMessage());
                            currentRequest = null;
                            continue;
                        }

                        logger.debug("awaiting response");
                        currentRequest.wait(2000);

                        if (currentRequest.ResponseListener != null) {
                            if (currentRequest.ResponsePacket == null) {
                                logger.debug("response timeout");
                                currentRequest.ResponseListener.responseTimeOut();
                            } else {
                                logger.debug("response received");
                                currentRequest.ResponseListener.handleResponse(currentRequest.ResponsePacket);
                            }
                        } else {
                            logger.debug("request without listener");
                        }

                        logger.debug("handeled request");
                        currentRequest = null;
                    }
                }
            } catch (InterruptedException e) {
                logger.debug("sending packets interrupted");
            }
        }
    }

    protected void informListeners(ERP1Message msg) {

        try {
            int[] senderId = msg.getSenderId();

            // todo ignore own msg
            if (senderId != null) {

                if (msg.getIsTeachIn()) {
                    logger.info("Received teach in message from {}", Helper.bytesToHexString(msg.getSenderId()));
                    if (teachInListener != null) {
                        teachInListener.espPacketReceived(msg);
                        return;
                    }
                }

                long s = Long.parseLong(Helper.bytesToHexString(senderId), 16);
                List<ESP3PacketListener> list = listeners.get(s);
                if (list != null) {
                    logger.debug("inform listener");
                    for (ESP3PacketListener listener : list) {
                        listener.espPacketReceived(msg);
                    }
                }
            }
        } catch (Exception e) {

        }
    }

    public void addPacketListener(ESP3PacketListener listener) {
        listeners.putIfAbsent(listener.getSenderIdToListenTo(), new LinkedList<ESP3PacketListener>());
        listeners.get(listener.getSenderIdToListenTo()).add(listener);
        logger.debug("Listener added: {}", listener.getSenderIdToListenTo());
    }

    public void startDiscovery(ESP3PacketListener teachInListener) {
        this.teachInListener = teachInListener;
    }

    public void stopDiscovery() {
        this.teachInListener = null;
    }
}
