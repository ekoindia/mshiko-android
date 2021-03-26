package in.co.eko.fundu.models;/*
 * Created by Bhuvnesh
 */

public class User extends Contact {
    public static final int FACEBOOK_TYPE=1;
    public static final int GOOGLE_PLUS_TYPE=2;
    public static final int EMAIL_TYPE=3;
    public static final int OTHER_TYPE=4;
    private String email;
    private String profileUrl;
    private int loginType;
    private String amount;

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    private double latitude;
    private double longitude;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAmount() {
        return amount;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public int getLoginType() {
        return loginType;
    }

    public void setLoginType(int loginType) {
        this.loginType = loginType;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}

