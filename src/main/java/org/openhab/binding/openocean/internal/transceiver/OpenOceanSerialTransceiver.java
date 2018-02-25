/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.openocean.internal.transceiver;

import java.util.TooManyListenersException;

import org.openhab.binding.openocean.internal.OpenOceanException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

/**
 *
 * @author Daniel Weber - Initial contribution
 */
public class OpenOceanSerialTransceiver extends OpenOceanTransceiver implements SerialPortEventListener {

    SerialPort serialPort;
    private static final int ENOCEAN_DEFAULT_BAUD = 57600;

    private Logger logger = LoggerFactory.getLogger(OpenOceanSerialTransceiver.class);

    public OpenOceanSerialTransceiver(String path) {
        super(path);
    }

    @Override
    public void Initialize() throws OpenOceanException {

        if (serialPort == null) {

            try {
                CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(path);
                CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);

                serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(ENOCEAN_DEFAULT_BAUD, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
                        SerialPort.PARITY_NONE);
                serialPort.enableReceiveThreshold(1);
                serialPort.enableReceiveTimeout(100); // In ms. Small values mean faster shutdown but more cpu usage.

            } catch (Exception e) {

                logger.info("Exception while trying to connect to serial port {}: {}", path, e.getMessage());
                ShutDown();
                throw new OpenOceanException(e.getMessage());
            }

            try {

                inputStream = serialPort.getInputStream();
                outputStream = serialPort.getOutputStream();

                serialPort.addEventListener(this);
                serialPort.notifyOnDataAvailable(true);

                logger.info("OpenOceanSerialTransceiver initialized");

            } catch (TooManyListenersException e) {

                ShutDown();
                logger.info("Serial port {} already in use: {}", path, e.getMessage());
                throw new OpenOceanException("Serial port already in use");
            } catch (Exception e) {
                ShutDown();
                logger.info("Exception while trying to access streams: {}", e.getMessage());
                throw new OpenOceanException(e.getMessage());
            }
        }
    }

    @Override
    public void ShutDown() {

        super.ShutDown();

        if (serialPort != null) {

            serialPort.removeEventListener();

            serialPort = null;
        }

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
