package org.openhab.binding.openocean.internal;

public enum OpenOceanConfigStatusMessage {
    PORT_MISSING("missing-port-configuration");

    private String messageKey;

    private OpenOceanConfigStatusMessage(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return messageKey;
    }
}