package in.co.eko.fundu.models;

/**
 * Created by divyanshu.jain on 7/5/2016.
 */
public class ContactResponseItem {
    boolean dummy_customer = true;
    String customer_id;
    String contact_id_type;
    String contact_id;
    String contact;
    boolean active;
    boolean dummy;
    String device_id;
    boolean deleted;
    boolean autocashout;
    String ii;
    Double rating;
    int i_incentive;
    int list_specific_id;


    public boolean isDummyCustomer() {
        return dummy_customer;
    }


    public String getId_type() {
        return contact_id_type;
    }

    public void setId_type(String id_type) {
        this.contact_id_type = id_type;
    }

    public String getId() {
        return contact_id;
    }

    public void setId(String id) {
        this.contact_id = id;
    }
    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getIi() {
        return ii;
    }
}
