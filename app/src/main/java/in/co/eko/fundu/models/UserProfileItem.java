package in.co.eko.fundu.models;

import java.util.ArrayList;

/**
 * Created by divyanshu.jain on 7/25/2016.
 */
public class UserProfileItem {
    String id;
    String name;
    String mobile;
    String contact_id;
    String contact_type;
    double rating;
    int ratingcount;
    String additional_identities;
    String custid;
    ArrayList<LinkAccountItem> identities;


    public String getCustid() {return custid;}
    public void setCustid(String custid){ this.custid = custid;}
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getContact_id() {
        return contact_id;
    }

    public void setContact_id(String contact_id) {
        this.contact_id = contact_id;
    }

    public String getContact_type() {
        return contact_type;
    }

    public void setContact_type(String contact_type) {
        this.contact_type = contact_type;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getAdditional_identities() {
        return additional_identities;
    }

    public void setAdditional_identities(String additional_identities) {
        this.additional_identities = additional_identities;
    }

    public ArrayList<LinkAccountItem> getIdentities() {
        return identities;
    }

    public void setIdentities(ArrayList<LinkAccountItem> identities) {
        this.identities = identities;
    }
}
