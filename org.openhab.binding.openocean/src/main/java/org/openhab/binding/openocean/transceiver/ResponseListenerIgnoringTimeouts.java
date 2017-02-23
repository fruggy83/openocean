package org.openhab.binding.openocean.transceiver;

public abstract class ResponseListenerIgnoringTimeouts implements ResponseListener {

    @Override
    public void responseTimeOut() {

    }
}
