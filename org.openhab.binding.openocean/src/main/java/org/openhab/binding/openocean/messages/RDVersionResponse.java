package org.openhab.binding.openocean.messages;

import org.openhab.binding.openocean.transceiver.Helper;

public class RDVersionResponse extends Response {

    protected String appVersion;
    protected String apiVersion;
    protected String chipId;
    protected String description;

    public RDVersionResponse(Response response) {
        super(response.packet);

        try {
            int[] data = response.getData();
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

}
