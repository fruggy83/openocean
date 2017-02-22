package org.openhab.binding.openocean.transceiver;

import java.util.TooManyListenersException;

import org.openhab.binding.openocean.internal.OpenOceanException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gnu.io.NRSerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

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

            serialPort = new NRSerialPort(path, ENOCEAN_DEFAULT_BAUD);
            serialPort.connect();

            try {

                inputStream = serialPort.getInputStream();
                outputStream = serialPort.getOutputStream();

                serialPort.addEventListener(this);

                logger.debug("OpenOceanSerialTransceiver initialized");

            } catch (TooManyListenersException e) {

                e.printStackTrace();
                ShutDown();

                throw new OpenOceanException("port already in use");
            }
        }
    }

    @Override
    public void ShutDown() {

        super.ShutDown();

        if (serialPort != null) {
            serialPort.removeEventListener();
            serialPort.disconnect();
            serialPort = null;

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
