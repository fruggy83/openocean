package org.openhab.binding.openocean.messages;

public class _RPSMessage extends ESP3Packet {

    protected int[] senderId;
    protected int byte0;
    protected int status;
    protected boolean t21;
    protected boolean nu;

    static final int T21Flag = 0x04;
    static final int NUFlag = 0x08;

    public _RPSMessage(int dataLength, int optionalDataLength, int[] payload) {
        super(dataLength, optionalDataLength, ESPPacketType.RADIO_ERP1, payload);

        if (payload != null && payload.length > 6) {
            senderId = new int[4];
            System.arraycopy(payload, 2, senderId, 0, 4);

            byte0 = payload[1] & 0xff;
            status = payload[6] & 0xff;
            t21 = (status & T21Flag) != 0;
            nu = (status & NUFlag) != 0;
        }
    }

    @Override
    public int[] getSenderId() {
        return senderId.clone();
    }

    public int getByte() {
        return byte0;
    }

    public boolean isT21() {
        return t21;
    }

    public boolean isNU() {
        return nu;
    }
}
