package org.openhab.binding.openocean.transceiver;

import org.openhab.binding.openocean.messages.Response;

public interface ResponseListener {

    public void responseReceived(Response response);

    public void responseTimeOut();
}