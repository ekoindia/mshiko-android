package in.co.eko.fundu.models;

/**
 * Created by divyanshu.jain on 7/21/2016.
 */
public class LinkAccountItem {
    String sb_status;
    String sb_message;
    String additionalIdentity;
    String additional_id_type;
    String additional_id_value;
    String updated_by;
    String name;
    boolean visible;
    String fundu_db_status;
    String fundu_db_message;
    int recipient_id;

    public int getRecipient_id() {
        return recipient_id;
    }

    public void setRecipient_id(int recipient_id) {
        this.recipient_id = recipient_id;
    }

    public String getSb_status() {
        return sb_status;
    }

    public void setSb_status(String sb_status) {
        this.sb_status = sb_status;
    }

    public String getSb_message() {
        return sb_message;
    }

    public void setSb_message(String sb_message) {
        this.sb_message = sb_message;
    }

    public String getAdditionalIdentity() {
        return additionalIdentity;
    }

    public void setAdditionalIdentity(String additionalIdentity) {
        this.additionalIdentity = additionalIdentity;
    }

    public String getAdditional_id_type() {
        return additional_id_type;
    }

    public void setAdditional_id_type(String additional_id_type) {
        this.additional_id_type = additional_id_type;
    }

    public String getAdditional_id_value() {
        return additional_id_value;
    }

    public void setAdditional_id_value(String additional_id_value) {
        this.additional_id_value = additional_id_value;
    }

    public String getUpdated_by() {
        return updated_by;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getFundu_db_status() {
        return fundu_db_status;
    }

    public String getFundu_db_message() {
        return fundu_db_message;
    }

}
