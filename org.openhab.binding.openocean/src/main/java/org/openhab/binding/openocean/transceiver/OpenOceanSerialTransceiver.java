package org.openhab.binding.openocean.transceiver;

import java.io.IOException;
import java.util.TooManyListenersException;

import gnu.io.NRSerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

public class OpenOceanSerialTransceiver extends OpenOceanTransceiver implements SerialPortEventListener {

    NRSerialPort serialPort;
    private static final int ENOCEAN_DEFAULT_BAUD = 57600;

    public OpenOceanSerialTransceiver(String path) {
        super(path);
    }

    @Override
    public void Initialize() {
        // TODO Auto-generated method stub

        if (serialPort == null) {

            serialPort = new NRSerialPort(path, ENOCEAN_DEFAULT_BAUD);
            serialPort.connect();
            try {

                inputStream = serialPort.getInputStream();
                outputStream = serialPort.getOutputStream();

                serialPort.addEventListener(this);

            } catch (TooManyListenersException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

                ShutDown();
            }
        }
    }

    @Override
    public void run() {

        int[] dataBuffer = new int[1024];
        int currentPosition = 0;
        ReadingState state = ReadingState.WaitingForSyncByte;

        int dataLength = -1;
        int optionalLength = -1;

        while (!this.stopReading) {
            try {
                synchronized (this.syncObj) {
                    if (this.inputStream.available() == 0) {
                        this.syncObj.wait();
                    }
                }
                if (this.stopReading) {
                    return;
                }

                int _byte = inputStream.read();
                if (_byte == -1) {
                    throw new IOException("buffer end was reached");
                }

                switch (state) {
                    case WaitingForSyncByte:
                        if (_byte == ENOCEAN_SYNC_BYTE) {
                            state = ReadingState.ReadingHeader;
                            currentPosition = 0;
                        }
                        break;
                    case ReadingHeader:
                        if (currentPosition == ENOCEAN_HEADER_LENGTH) {
                            if (checkCRC8(dataBuffer, 0, ENOCEAN_HEADER_LENGTH, _byte)
                                    && ((dataBuffer[0] << 8) | dataBuffer[1]) + dataBuffer[2] > 0) {
                                dataBuffer[currentPosition++] = _byte;
                                state = ReadingState.ReadingData;

                                dataLength = ((dataBuffer[0] << 8) | dataBuffer[1]);
                                optionalLength = dataBuffer[2];

                            } else {
                                // check if we find a sync byte in current buffer
                                int copyFrom = -1;
                                for (int i = 0; i < ENOCEAN_HEADER_LENGTH; i++) {
                                    if (dataBuffer[i] == ENOCEAN_SYNC_BYTE) {
                                        copyFrom = i + 1;
                                        break;
                                    }
                                }

                                if (copyFrom != -1) {
                                    System.arraycopy(dataBuffer, copyFrom, dataBuffer, 0,
                                            ENOCEAN_HEADER_LENGTH - copyFrom);
                                    state = ReadingState.ReadingHeader;
                                    currentPosition = ENOCEAN_HEADER_LENGTH - copyFrom;
                                    dataBuffer[currentPosition++] = _byte;
                                } else if (_byte == ENOCEAN_SYNC_BYTE) {
                                    state = ReadingState.ReadingHeader;
                                    currentPosition = 0;
                                    dataLength = -1;
                                    optionalLength = -1;
                                } else {
                                    state = ReadingState.WaitingForSyncByte;
                                    currentPosition = 0;
                                    dataLength = -1;
                                    optionalLength = -1;
                                }
                            }
                        } else {
                            dataBuffer[currentPosition++] = _byte;
                        }
                        break;
                    case ReadingData:
                        if (currentPosition == ENOCEAN_HEADER_LENGTH + 1 + dataLength + optionalLength) {
                            if (checkCRC8(dataBuffer, ENOCEAN_HEADER_LENGTH + 1, dataLength + optionalLength, _byte)) {
                                state = ReadingState.WaitingForSyncByte;
                                currentPosition = 0;
                                dataLength = -1;
                                optionalLength = -1;
                            }
                        } else {
                            dataBuffer[currentPosition++] = _byte;
                        }
                        break;
                    default:
                        break;
                }

                if (_byte == ENOCEAN_SYNC_BYTE) {
                    // EspPacket packet = readPacket();
                    // if (packet.getPacketType() == EspPacket.TYPE_RADIO) {
                    // dispatchToListeners(packet.getFullData());
                    // }
                }
            } catch (IOException ioexception) {
                ioexception.printStackTrace();
            } catch (InterruptedException interruptedexception) {
                interruptedexception.printStackTrace();
            } finally {
                try {
                    inputStream.skip(inputStream.available());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void ShutDown() {

        this.stopReading = true;
        this.syncObj.notify();
        serialPort.removeEventListener();
        serialPort.disconnect();
        serialPort = null;

    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        if (event.getEventType() == 1) {
            synchronized (this.syncObj) {
                this.syncObj.notify();
            }
        }
    }
}
