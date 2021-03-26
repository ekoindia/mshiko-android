package in.co.eko.fundu.constants;


import in.co.eko.fundu.R;

/* * Created by Zartha */


public final class Constants {


    public static String mid = "your_mid";
    public static String merchantKey = "your_merchantKey";
    public static String appName = "in.co.eko.fundu";
    public static String ekoVPA = "your_vpa";


            /**     * Telegram support message     */

    public static String telegramChatId = "-your_telegram_id";

    public static boolean debug = false;
    public static boolean dummyUPI = true;
    public static boolean dbEncrypted = true;

    public static final UPI_PROVIDER upiProvider = UPI_PROVIDER.YOUR_BANK;


    public static final String PHONENUMBER = "PHONENUMBER";
    public static final String EXISTINGUSERDB = "Update_DB_For_Exixting_User";
    public static final String COUNTRYCODE = "COUNTRYCODE";
    public static final String USERCONTACTTABLE = "USERCONTACTTABLE";
    public static final String VERSION_CODE = "VERSION_CODE";
    public static final String PROVIDER_NUMBER = "PROVIDERNUMBER";
    public static final String DONT_SHOW_QRCODE_POPUP = "DONT_SHOW_QRCODE_POPUP";
    public static final String SEC_QUESTION = "SEC_QUESTION";
    public static final String SEC_ANSWER = "SEC_ANSWER";
    public static String SimName = "SimName";
    public static String SimNumber = "SimNumber";
    public static final String NEED_CASH_TYPE = "needcash";
    public static final String SEND_MONEY_TYPE = "sendmoney";
    public static final String GET_CASH_TYPE = "getcash";
            /**     * This for social login     */
    public static final String IS_USER_LOGGED_IN = "is_user_logged_in";
    public static final String IS_MOBILE_VERIFIED = "is_mobile_verified";
    public static final String NAME = "name";
    public static final String ID = "id";
    public static final String ALLOW_WITHDRAW = "allow_withdraw";
    public static final String ID_TYPE = "id_type";
    public static final String PROFILE_PIC_URL = "image_url";
    public static final String OTP = "otp";
    public static final String EKOIND = "EKOIND";
    public static final String KENSWITCH = "KENSWITCH";
    public static final String FundU = "FundU";
    public static final String IMWAYSMS = "IM-WAYSMS";
    public static final String COUNTRY_SHORTCODE = "country_shortname";
    public static final String COUNTRY_MOBILE_CODE = "country_code_r";
    public static final String COUNTRY_CODE = "countryCodeA3";
    public static final String CUSTOMERID = "custid";
    public static final String MOBILE = "mobile";
    public static final String CONTACTS = "contacts";
    public static final String CONTACT_RESPONSE_LIST = "contact_response_list";
    // Splash Screen time limit
    public static final long SPLASH_TIME = 3000;
    public static final String SENT_TOKEN_TO_SERVER = "is_token_sent";
    public static final int REQUEST_TIMEOUT_TIME = 90000;
    public static final String GCM_TOKEN = "gcm_token";
    public static final String ALERT = "alert";
    public static final String PUSH_JSON_DATA = "push_json_data";
    public static final String DECISION = "decision";
    public static final String DEVICE_ID = "device_id";
    public static final String PUSH_TYPE = "push_type";
    public static final String NOTIFICATION_ID = "notification_id";
    public static final String TRANSACTION_ID = "transaction_id";
    public static final String FUNDU_TRANSACTION_ID = "fundu_transaction_id";
    public static final String RECIPIENT_ID = "recipient_id";
    public static final String RECIPIENT_NUMBER = "recipient_number";
    public static final String TOKEN_REFRESH = "token_refresh";
    public static final String TOTAL_AMOUNT = "total_amount";
    public static final long PROGRESS_OVERLAY_TIMEOUT = 150000;
    public static final String RATING_TYPE = "rating_type";
    public static final String UPDATED_AMOUNT = "updated_amount";
    public static final String DRAWER_RECEIVER_ACTION_KEY = "drawer-event";
    public static final String LOADED_AMOUNT = "loaded_amount";
    public static final String DISMISS_PATH_SCREEN_ACTION = "path_action";
    public static final String SHOW_PATH_SCREEN_INTENT = "show_path_intent";
    public static final String UPDATE_ACCOUNT_NO_INTENT = "update_account_no_intent";
    public static final String HOME_ACTIVITY_ACTION = "home_activity_action";
    public static final String CONTACT_ID_TYPE = "contact_id_type";
    public static final String CONTACT_ID = "contact_id";
    public static final String DELETED = "deleted";
    public static final String CONTACT_TYPE_PA = "contact_typepa";
    public static final String AMOUNT = "amount";
    public static final String ACCOUNT_NUMBER = "account_number";
    public static final String AVERAGE_RATING = "Average Rating";
    public static final String USER_PROFILE_ACTIVITY_ACTION = "user_profile_activity_action";
    public static final String UPDATE_CONTACT_ACTION = "update_contact_action";
    public static final String OPENING_TIME = "opening_time";
    public static final String CLOSING_TIME = "closing_time";
    public static final String DAYS = "days";
    public static final String SHOP_CLOSED = "Shop is closed! Try when shop is open.";
    public static final String TRANSACTION_STATUS = "transaction_status";
    public static final String DONT_CHECK_PERMISSION_ACTION = "dont_check_permission";
    //*** lgoin types ***//
    public static final String MOBILE_TYPE = "mobile_type";

    public static String FromContact = "from_contact";

    public static final String PROFILE_DATA = "profile_data";
    public static final String LINKED_ACC = "linked_acc";

    public static final String CAMPAIGN_LINK = "campaign_link";
    public static final String IS_ICON_AVAIL = "is_icon_avail";
    public static final String ICON_LINK = "icon_link";

    public static final int ICONS_IND[] = {
//            R.drawable.get_cash,

            R.drawable.ic_profile,
            R.drawable.ic_qr_code,
            R.drawable.ic_facphm,
            R.drawable.ic_history,
            R.drawable.ic_alarm,
            R.drawable.ic_settings,
            R.drawable.ic_about

    };
    public static final int ICONS_DEFAULT[] = {
            R.drawable.ic_get_cash,
            R.drawable.ic_qr_code,
            R.drawable.ic_history,
            R.drawable.ic_alarm,
            R.drawable.ic_settings,
            R.drawable.ic_about

    };
    public static final int ICONS_KEN[] = {
            R.drawable.ic_get_cash,
            R.drawable.ic_qr_code,
            R.drawable.ic_history,
            R.drawable.ic_alarm,
            R.drawable.ic_settings,
            R.drawable.ic_about

    };

    public enum UPI_PROVIDER{
        ICICI,
        YESBANK,
        YOUR_BANK
    };

    public static final class PrefKey {
        public static final String CONTACT_NUMBER = "contact_number";
        public static final String NAME = "name";
        public static final String IS_VERIFIED = "is_verified";
        public static final String IS_LOGIN_REG = "is_login_reg";

        public static final String CONTACT_TYPE = "contact_type";
        public static final String CONTACT_ID_TYPE = "contact_id_type";
        public static final String USER_AMOUNT = "user_amount";
        public static final String NEED_AMOUNT = "need_amount";
        public static final String UUID = "uuid";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String INVITATION_INCENTIVE = "i_incentive";
        public static final String VPA = "vpa";
        public static final String USERIMAGE = "person_img_url";
        public static final String MASKED_ACCOUNT_NUMBER = "massked_account";
        public static final String IFSC_CODE = "ifsc";
        public static final String LOGIN_TYPE = "login_type";
        public static final String EMAIL = "email";
        public static final String AUTH_TOKEN = "fundu_auth_token";
        public static final String BANK_NAME = "bank_name";
        public static final String RECIPIENT_ID = "recipient_id";
        public static final String INVITATION_LINK = "invitation_link";
        public static final String INVITATION_MESSAGE = "invitation_message";
        public static final String INVITATION_ID = "ii";
        public static final String RATING = "rating";
    }

    public interface ApiType {
        int GET_CONTACT_INFORMATION = 100;

        int GET_CUSTOMER_INFO = 101;
        int INVITE_FRIEND = 102;
        int SAVE_FINAL_ADDITIONAL_IDENTITIES = 104;
        int SAVE_ADD_MORE_CLICKED_IDENTITES = 105;
        int GET_BANKS_NAME = 106;
    }
    public static boolean FromSettings;
    public static int count=0;

    public enum TransactionStatus{
        FAILURE,
        SUCCESS
    }
    public static final class PrivateKey {

        // API key for connecting to backend used in all API requests made to backend
        public static final String SWAGGER_AUTHENTICATION_KEY = "Basic " + "Z29vZC1ndXk6c2VjcmV0";

    }
    public class SettingsPref {
        public static final String AUTOCASHOUT = "auto_cash_out";
        public static final String NOTIFICATION = "notification";
        public static final String SHARE_LOCATION = "share_location";
        public static final String USER_REQUEST = "user_location";
        public static final String BLOCKED_USER = "blocked_user";
        public static final String DISABLE_UNTIL = "disable_until";
    }

    public class PushNotificationKeys{
        public static final String PROVIDER_CHARGE = "provider_charge";
        public static final String FEE = "fee";
        public static final String LOCATION = "location";
        public static final String IMAGEURL = "imageUrl";
        public static final String RATING  = "rating";
        public static final String TID = "tid";
        public static final String AMOUNT = "amount";
        public static final String NAME = "name";
        public static final String ACTION = "action";
        public static final String PAIR_REQUEST_ID = "pair_contact_rid";
        public static final String PHONENUMBER = "phone_number";
        public static final String SEEKER = "seeker";
        public static final String PROVIDER = "provider";
        public static final String CUST_ID = "custid";
        public static final String PUSH_TYPE ="push_type";
        public static final String REQUEST_LOCATION = "request_location";
        public static final String MERCHANT_ATM = "merchantAndAtms";
        public static final String EXTRA_NOTIFI = "extra_notifi";
    }

    public enum PUSH_TYPE_ENUM{
        ACCEPT_REJECT(1),
        PAIR_FOUND(2),
        NEEDCASH_TRANSACTION_COMPLETED(3),
        NO_PAIR_FOUND(4),
        W2W_TRANSACTION_COMPLETED(5),
        SHOP_CLOSED(6),
        USER_DIDNOT_ACCEPT(7),
        VERIFY_TRANSACTION_CODE(8),
        CREDIT_SUCCESS(11),
        TRANSACTION_CANCELLED(12),
        TRANSACTION_INITIATED(13),
        TRANSACTION_FAILED(14),
        MERCHANT_ATM_FOUND(15),
        REFUND_SUCCESSFUL(16),
        MESSAGE_TO_USER(17);

        private int code;
        PUSH_TYPE_ENUM(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    };

    public enum TRANSACTION_STATE{
        SEEKER_INITIATED(1),
        SEEKER_ACCOUNT_DEBITED(2),
        PROVIDER_ACCEPTED(3),
        PROVIDER_VERIFY_CODE(4),
        RATING_PENDING(5),
        TRANSACTION_CANCELLED(6);


        private int code;
        TRANSACTION_STATE(int code){
            this.code = code;

        }

        public int getCode() {
            return code;
        }
    };

    public enum CustomerType{
        ATM(1), BANK(1), MERCHANT(1), AGENT(1), PERSON(2), DEFAULT(0);

        int code;
        CustomerType(int code){
            this.code = code;
        }
    };

    public static String getState(int code) {
        switch (code) {
            case 1 :
                return "SEEKER_INITIATED";
            case 2 :
                return "SEEKER_ACCOUNT_DEBITED";
            case 3 :
                return "PROVIDER_ACCEPTED";
            case 4 :
                return "PROVIDER_VERIFY_CODE";
            case 5 :
                return "RATING_PENDING";
            case 6 :
                return "TRANSACTION_CANCELLED";
        }
        return "";
    }
}




