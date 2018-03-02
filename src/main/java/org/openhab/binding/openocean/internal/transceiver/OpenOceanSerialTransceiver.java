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
import java.util.TooManyListenersException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

/**
 *
 * @author Daniel Weber - Initial contribution
 */
public class OpenOceanSerialTransceiver extends OpenOceanTransceiver implements SerialPortEventListener {

    SerialPort serialPort;
    private static final int ENOCEAN_DEFAULT_BAUD = 57600;

    private Logger logger = LoggerFactory.getLogger(OpenOceanSerialTransceiver.class);

    public OpenOceanSerialTransceiver(String path, TransceiverErrorListener errorListener) {
        super(path, errorListener);
    }

    @Override
    public void Initialize() throws UnsupportedCommOperationException, NoSuchPortException, PortInUseException,
            IOException, TooManyListenersException {

        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(path);
        CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);

        serialPort = (SerialPort) commPort;
        serialPort.setSerialPortParams(ENOCEAN_DEFAULT_BAUD, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
                SerialPort.PARITY_NONE);
        serialPort.enableReceiveThreshold(1);
        serialPort.enableReceiveTimeout(100); // In ms. Small values mean faster shutdown but more cpu usage.

        inputStream = serialPort.getInputStream();
        outputStream = serialPort.getOutputStream();

        serialPort.addEventListener(this);
        serialPort.notifyOnDataAvailable(true);

        logger.info("OpenOceanSerialTransceiver initialized");
    }

    @Override
    public void ShutDown() {

        logger.debug("shutting down transceiver");

        if (serialPort != null) {
            logger.debug("removing serial event listener");
            serialPort.removeEventListener();
        }

        super.ShutDown();

        if (outputStream != null) {
            logger.debug("Closing serial output stream");
            IOUtils.closeQuietly(outputStream);
        }
        if (inputStream != null) {
            logger.debug("Closeing serial input stream");
            IOUtils.closeQuietly(inputStream);
        }

        if (serialPort != null) {
            logger.debug("Closing serial port");
            serialPort.close();
        }

        serialPort = null;
        outputStream = null;
        inputStream = null;

        logger.info("Transceiver shutdown");

    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        try {
            logger.trace("RXTX library CPU load workaround, sleep forever");
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
        }
    }
}
