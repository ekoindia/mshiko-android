package in.co.eko.fundu.models;/*
 * Created by Bhuvnesh
 */

import android.os.Parcel;
import android.os.Parcelable;

public class ContactItem implements Parcelable {
    private int _id;

    public String getContactCountryCode() {
        return contactCountryCode;
    }

    public void setContactCountryCode(String contactCountryCode) {
        this.contactCountryCode = contactCountryCode;
    }

    private String contactName;
    private String contactImage;
    private String contactNumber;
    private String contactCountryCode;
    private boolean isAddedInNetwork;
    private String subTitle;
    private String version;
    private int isUnregisterd = -1;
    private double distance;
    private double distanceInTime;
    private String customer_id;
    private int deleted = 0;
    private String callType = "";

    private boolean isSelectType;

    public boolean isSelectType() {
        return isSelectType;
    }

    public void setSelectType(boolean selectType) {
        isSelectType = selectType;
    }

    public ContactItem() {

    }

    public ContactItem(String contactName, String contactNumber, String contactImage,String contactCountryCode) {
        this.contactName = contactName;
        this.contactNumber = contactNumber;
        this.contactImage = contactImage;
        this.contactCountryCode = contactCountryCode;
    }

    protected ContactItem(Parcel in) {
        _id = in.readInt();
        contactName = in.readString();
        contactImage = in.readString();
        contactNumber = in.readString();
        contactCountryCode = in.readString();
        isAddedInNetwork = in.readByte() != 0;
        subTitle = in.readString();
        version = in.readString();
        isUnregisterd = in.readInt();
        distance = in.readDouble();
        distanceInTime = in.readDouble();
        customer_id = in.readString();
    }

    public static final Creator<ContactItem> CREATOR = new Creator<ContactItem>() {
        @Override
        public ContactItem createFromParcel(Parcel in) {
            return new ContactItem(in);
        }

        @Override
        public ContactItem[] newArray(int size) {
            return new ContactItem[size];
        }
    };

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactImage() {
        return contactImage;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public boolean isAddedInNetwork() {
        return isAddedInNetwork;
    }

    public void setIsAddedInNetwork(boolean isAddedInNetwork) {
        this.isAddedInNetwork = isAddedInNetwork;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int isUnregisterd() {
        return isUnregisterd;
    }

    public void setIsUnregisterd(int isUnregisterd) {
        this.isUnregisterd = isUnregisterd;
    }

    public int getIsUnregisterd() {
        return isUnregisterd;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getDistanceInTime() {
        return distanceInTime;
    }

    public void setDistanceInTime(double distanceInTime) {
        this.distanceInTime = distanceInTime;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(_id);
        dest.writeString(contactName);
        dest.writeString(contactImage);
        dest.writeString(contactNumber);
        dest.writeString(contactCountryCode);
        dest.writeByte((byte) (isAddedInNetwork ? 1 : 0));
        dest.writeString(subTitle);
        dest.writeString(version);
        dest.writeInt(isUnregisterd);
        dest.writeDouble(distance);
        dest.writeDouble(distanceInTime);
        dest.writeString(customer_id);

    }

    public int isDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }


    @Override
    public boolean equals(Object o) {

        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        if (!(o instanceof ContactItem)) {
            return false;
        }

        // typecast o to Complex so that we can compare data members
        ContactItem c = (ContactItem) o;
        if(c.getContactNumber() == null || this.contactNumber == null){
            return false;
        }

        return  c.getContactNumber().equalsIgnoreCase(this.getContactNumber());
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + contactNumber.hashCode();
        result = 31* result + contactName.hashCode();
        return result;
    }
}
