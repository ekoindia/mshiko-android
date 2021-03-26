package in.co.eko.fundu.event;

/**
 * Created by zartha on 4/25/18.
 */

public class LocationEvent {
    private String mobile;
    private Double logitude;
    private Double latitude;

    public LocationEvent(String mobile,double latitude,double longitude){
        this.mobile = mobile;
        this.logitude = longitude;
        this.latitude = latitude;
    }

    public String getMobile() {
        return mobile;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLogitude() {
        return logitude;
    }
}
