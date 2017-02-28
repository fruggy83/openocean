package org.openhab.binding.openocean.internal;

public enum OpenOceanConfigStatusMessage {
    PORT_MISSING("missing-port-configuration"),
    SENDERID_MISSING("missing-senderId-configuration"),
    SENDERID_MALFORMED("malformed-senderId-configuration");

    private String messageKey;

    private OpenOceanConfigStatusMessage(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return messageKey;
    }
}