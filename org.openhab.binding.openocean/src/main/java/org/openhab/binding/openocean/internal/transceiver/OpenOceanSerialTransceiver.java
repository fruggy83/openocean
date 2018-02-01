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

import gnu.io.NRSerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

/**
 *
 * @author Daniel Weber - Initial contribution
 */
public class OpenOceanSerialTransceiver extends OpenOceanTransceiver implements SerialPortEventListener {

    NRSerialPort serialPort;
    private static final int ENOCEAN_DEFAULT_BAUD = 57600;

    private Logger logger = LoggerFactory.getLogger(OpenOceanSerialTransceiver.class);

    public OpenOceanSerialTransceiver(String path) {
        super(path);
    }

    @Override
    public void Initialize() throws OpenOceanException {

        if (serialPort == null) {

            try {
                serialPort = new NRSerialPort(path, ENOCEAN_DEFAULT_BAUD);
                if (!serialPort.connect()) {
                    logger.info("Could not connect");
                    return;
                }
            } catch (Exception e) {

                logger.info("Exception while trying to connect");
                // e.printStackTrace();
                ShutDown();
            }

            try {

                inputStream = serialPort.getInputStream();
                outputStream = serialPort.getOutputStream();

                serialPort.addEventListener(this);

                logger.info("OpenOceanSerialTransceiver initialized");

            } catch (TooManyListenersException e) {

                // e.printStackTrace();
                ShutDown();

                throw new OpenOceanException("port already in use");
            }
        }
    }

    @Override
    public void ShutDown() {

        super.ShutDown();

        if (serialPort != null) {
            if (serialPort.isConnected()) {
                serialPort.removeEventListener();
                serialPort.disconnect();
                serialPort = null;
            }

            logger.debug("Transceiver shutdown");
        }
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
