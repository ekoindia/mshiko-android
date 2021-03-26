package in.co.eko.fundu.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by divyanshu.jain on 7/6/2016.
 */
public class ContactsNearByModel extends Contact implements Parcelable {

    private double distance;
    private double distanceInTime;
    private String customer_id;

    public ContactsNearByModel() {

    }

    protected ContactsNearByModel(Parcel in) {
        distance = in.readDouble();
        distanceInTime = in.readDouble();
        customer_id = in.readString();
    }

    public static final Creator<ContactsNearByModel> CREATOR = new Creator<ContactsNearByModel>() {
        @Override
        public ContactsNearByModel createFromParcel(Parcel in) {
            return new ContactsNearByModel(in);
        }

        @Override
        public ContactsNearByModel[] newArray(int size) {
            return new ContactsNearByModel[size];
        }
    };

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getDistanceInTime() {
        return distanceInTime;
    }


    public String getCustomer_id() {
        return customer_id;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(distance);
        dest.writeDouble(distanceInTime);
        dest.writeString(customer_id);
    }


}
