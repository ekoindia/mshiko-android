package in.co.eko.fundu.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Rahul on 1/22/17.
 */

//{
//        "tx_history_id": "8BGY-JZNC-I3UO-EATB",
//        "tx_id": "FJAN7000000087",
//        "tx_status": "ERROR",
//        "tx_amount": 200,
//        "seeker_custid": "5009-2334-60C7-E3A3",
//        "provider_custid": "50F7-6C92-2002-BD6D",
//        "countryShortname": "KEN",
//        "created_at": 1484754099987,
//        "created_by": "System",
//        "customer_name": "Symon Ndirangu",
//        "customer_mobile": "724216592",
//        "tx_type": "need_cash",
//        "role": "provider"
//        },

public class TransactionHistoryModel implements Serializable{

    @SerializedName("tx_history_id")
    public String tx_history_id;
    @SerializedName("tx_id")
    public String tx_id;
    @SerializedName("tx_status")
    public String tx_status;
    @SerializedName("tx_amount")
    public double tx_amount;
    @SerializedName("country_shortname")
    public String country_shortname;
    @SerializedName("created_at")
    public String created_at;
    @SerializedName("created_by")
    public String created_by;
    @SerializedName("customer_name")
    public String customer_name;
    @SerializedName("customer_mobile")
    public String customer_mobile;
    @SerializedName("tx_type")
    public String tx_type;
    @SerializedName("role")
    public String role;
    @SerializedName("user_image")
    public String user_image;
    @SerializedName("seeker")
    private HistoryUser seeker;
    @SerializedName("provider")
    private HistoryUser provider;
    @SerializedName("seeker_charge")
    private double seeker_charge;
    @SerializedName("provider_charge")
    private double provider_charge;


//    public double getSeeker_rating() {
//        return seeker_rating;
//    }
//
//    public void setSeeker_rating(double seeker_rating) {
//        this.seeker_rating = seeker_rating;
//    }
//
//    public double getProvider_rating() {
//        return provider_rating;
//    }
//
//    public void setProvider_rating(double provider_rating) {
//        this.provider_rating = provider_rating;
//    }
//
//    public String getSeeker_location() {
//        return seeker_location;
//    }
//
//    public void setSeeker_location(String seeker_location) {
//        this.seeker_location = seeker_location;
//    }

//    public double[] getProvider_location() {
//        return provider_location;
//    }
//
//    public void setProvider_location(double[] provider_location) {
//        this.provider_location = provider_location;
//    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public double getSeeker_charge() {
        return seeker_charge;
    }


    public double getProvider_charge() {
        return provider_charge;
    }



    public String getTx_id() {
        return tx_id;
    }

    public String getTx_status() {
        return tx_status;
    }

    public double getTx_amount() {
        return tx_amount;
    }


//    public String getSeeker_custid() {
//        return seeker_custid;
//    }
//
//    public void setSeeker_custid(String seeker_custid) {
//        this.seeker_custid = seeker_custid;
//    }
//
//    public String getProvider_custid() {
//        return provider_custid;
//    }
//
//    public void setProvider_custid(String provider_custid) {
//        this.provider_custid = provider_custid;
//    }

    public String getCountry_shortname() {
        return country_shortname;
    }

    public void setCountry_shortname(String country_shortname) {
        this.country_shortname = country_shortname;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public String getRole() {
        return role;
    }


    public HistoryUser getSeeker() {
        return seeker;
    }

    public HistoryUser getProvider() {
        return provider;
    }

    public void setProvider(HistoryUser provider) {
        this.provider = provider;
    }

    public String getTx_type() {
        return tx_type;
    }

    public void setTx_type(String tx_type) {
        this.tx_type = tx_type;
    }

    @Override
    public boolean equals(Object o) {

        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        /* Check if o is an instance of Complex or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof TransactionHistoryModel)) {
            return false;
        }

        // typecast o to Complex so that we can compare data members
        TransactionHistoryModel c = (TransactionHistoryModel) o;

        // Compare the data members and return accordingly
        return  this.tx_id.equalsIgnoreCase(c.tx_id);
    }
}
