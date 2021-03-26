package in.co.eko.fundu.models;

/**
 * Created by divyanshu.jain on 7/20/2016.
 */
public class UpdateSettingItem {
    private String customer_id;
    private boolean share_location;
    private boolean notification;
    private boolean auto_cash_out;


    public boolean isShare_location() {
        return share_location;
    }

    public boolean isNotification() {
        return notification;
    }

    public boolean isAuto_cash_out() {
        return auto_cash_out;
    }

}

