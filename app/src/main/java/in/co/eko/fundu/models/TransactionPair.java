package in.co.eko.fundu.models;/*
 * Created by Bhuvnesh
 */

public class TransactionPair {

    private String requestId;
    private String pairContactId;
    private String requesterId;
    private String latitude;
    private String longitude;
    private String request_type;

    public void setRequest_type(String request_type) {
        this.request_type = request_type;
    }
    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public void setPairContactId(String pairContactId) {
        this.pairContactId = pairContactId;
    }

    public void setRequesterId(String requesterId) {
        this.requesterId = requesterId;
    }
}
