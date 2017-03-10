package org.openhab.binding.openocean.eep;

import org.openhab.binding.openocean.messages.Response;
import org.openhab.binding.openocean.transceiver.Helper;

public class RDVersionResponse extends Response implements EEP {

    protected String appVersion;
    protected String apiVersion;
    protected String chipId;
    protected String description;

    protected boolean _isValid = false;

    public RDVersionResponse(Response response) {
        this(response.getData().length, 0, response.getData());
    }

    public RDVersionResponse(int dataLength, int optionalDataLength, int[] payload) {
        super(dataLength, optionalDataLength, payload);

        if (data.length < 33) {
            return;
        }

        try {
            appVersion = String.format("%d.%d.%d.%d", data[1] & 0xff, data[2] & 0xff, data[3] & 0xff, data[4] & 0xff);
            apiVersion = String.format("%d.%d.%d.%d", data[5] & 0xff, data[6] & 0xff, data[7] & 0xff, data[8] & 0xff);

            int[] chip = new int[4];
            System.arraycopy(data, 9, chip, 0, 4);
            chipId = Helper.bytesToHexString(chip);

            StringBuffer sb = new StringBuffer();
            for (int i = 17; i < data.length; i++) {
                sb.append((char) (data[i] & 0xff));
            }
            description = sb.toString();
            _isValid = true;

        } catch (Exception e) {
            responseType = ResponseType.RET_ERROR;
        }
    }

    public String getAPPVersion() {
        return appVersion;
    }

    public String getAPIVersion() {
        return apiVersion;
    }

    public String getChipID() {
        return chipId;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean isValid() {
        return _isValid;
    }

}
