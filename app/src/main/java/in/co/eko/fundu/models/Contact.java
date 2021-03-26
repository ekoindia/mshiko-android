package in.co.eko.fundu.models;/*
 * Created by Bhuvnesh
 */

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import in.co.eko.fundu.constants.Constants;

public class Contact implements Serializable {
    @SerializedName("rating")
    private String rating;
    @SerializedName("autocashout")
    private boolean autoCashOut;
    @SerializedName("name")
    private String name;
    @SerializedName("mobile")
    private String mobile;
    @SerializedName("country_code")
    private String countryCode;
    @SerializedName("postalCode")
    private String postalCode;
    @SerializedName("location_short_code")
    private String locationShortCode;
    @SerializedName("place_name")
    private String placeName;
    @SerializedName("contact_id_type")
    private String contactIdType;
    @SerializedName("contact_id")
    private String contactId;
    @SerializedName("contact_type")
    private String contactType;
    @SerializedName("device_id")
    private String deviceId;
    @SerializedName("device_type")
    private String deviceType;
    @SerializedName("device_token")
    private String deviceToken;
    @SerializedName("parse_installation_id")
    private String parseInstallationId;
    @SerializedName("postal_address")
    private String postalAddress;
    @SerializedName("verified")
    private boolean verified;
    @SerializedName("deleted")
    private boolean deleted;
    @SerializedName("gsm_sender_id")
    private String gcmSenderId;
    @SerializedName("location")
    private Location location;
    @SerializedName("additional_identities")
    private AdditionalIdentities additionalIdentities;
    @SerializedName("countryShortname")
    private String country_shortname;
    @SerializedName("incorp_businessNo")
    private String incorp_businessNo;
    @SerializedName("vertical_market")
    private String vertical_market;
    @SerializedName("business_name")
    private String business_name;
    @SerializedName("business_type")
    private String business_type;
    @SerializedName("physical_location")
    private String physical_location;
    @SerializedName("merchant_img_url")
    private String merchant_img_url;
    @SerializedName("days")
    private String days;
    @SerializedName(Constants.OPENING_TIME)
    private String opening_time;
    @SerializedName(Constants.CLOSING_TIME)
    private String closing_time;


    @SerializedName("ii")
    private String ii;

    public void setIi(String ii) {
        this.ii = ii;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getOpening_time() {
        return opening_time;
    }

    public void setOpening_time(String opening_time) {
        this.opening_time = opening_time;
    }

    public String getClosing_time() {
        return closing_time;
    }

    public void setClosing_time(String closing_time) {
        this.closing_time = closing_time;
    }

    public String getMerchant_img_url() {
        return merchant_img_url;
    }

    public void setMerchant_img_url(String merchant_img_url) {
        this.merchant_img_url = merchant_img_url;
    }
    public String getPhysical_location() {
        return physical_location;
    }

    public void setPhysical_location(String physical_location) {
        this.physical_location = physical_location;
    }

    public String getIncorp_businessNo() {
        return incorp_businessNo;
    }

    public void setIncorp_businessNo(String incorp_businessNo) {
        this.incorp_businessNo = incorp_businessNo;
    }

    public String getVertical_market() {
        return vertical_market;
    }

    public void setVertical_market(String vertical_market) {
        this.vertical_market = vertical_market;
    }

    public String getBusiness_name() {
        return business_name;
    }

    public void setBusiness_name(String business_name) {
        this.business_name = business_name;
    }

    public String getBusiness_type() {
        return business_type;
    }

    public void setBusiness_type(String business_type) {
        this.business_type = business_type;
    }

    public String getAllow_withdraw() {
        return allow_withdraw;
    }

    public void setAllow_withdraw(String allow_withdraw) {
        this.allow_withdraw = allow_withdraw;
    }

    @SerializedName("allow_withdraw")
    private String allow_withdraw;





    public String getCountry_shortname() {
        return country_shortname;
    }

    public void setCountry_shortname(String country_shortname) {
        this.country_shortname = country_shortname;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCountryCode() {
        return countryCode;
    }


    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getContactIdType() {
        return contactIdType;
    }

    public void setContactIdType(String contactIdType) {
        this.contactIdType = contactIdType;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getContactType() {
        return contactType;
    }

    public void setContactType(String contactType) {
        this.contactType = contactType;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public void setGcmSenderId(String gcmSenderId) {
        this.gcmSenderId = gcmSenderId;
    }

    public static class Location implements Serializable {
        public Location(double[] coordinates) {
            this.coordinates = coordinates;
        }
        @SerializedName("coordinates")
        public double[] coordinates;
        @SerializedName("type")
        public String type;
    }
    public static class AdditionalIdentities implements Serializable {
        public AdditionalIdentities(ArrayList<Identities> identities) {
            this.identities = identities;
        }

        @SerializedName("identities")

        public ArrayList<Identities> identities;
    }

    public static class Identities implements Serializable {
        public Identities(String recipientId, String deviceToken, String additionalIdValue, String additionalIdType) {
            this.recipientId = recipientId;
            this.deviceToken = deviceToken;
            this.additionalIdValue = additionalIdValue;
            this.additionalIdType = additionalIdType;
        }
        @SerializedName("additional_id_type")
        public String additionalIdType;
        @SerializedName("additional_id_value")
        public String additionalIdValue;
        @SerializedName("device_token")
        public String deviceToken;
        @SerializedName("recipient_id")
        public String recipientId;

    }

    @Override
    public String toString() {
        return "Contact{" +
                "name='" + name + '\'' +
                ", mobile='" + mobile + '\'' +
                ", contactId='" + contactId + '\'' +
                ", location=" + (location!=null?location.coordinates[1]:"") + ", " + (location!=null?location.coordinates[0]:"") +
                '}';
    }
}
