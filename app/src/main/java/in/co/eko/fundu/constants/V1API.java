package in.co.eko.fundu.constants;

/**
 * Created by divyanshu.jain on 6/21/2016.
 */

public class V1API {

    public static final String BASE_URL = "http://your_backend_url";

    public static final String LOCALE = "?locale=en_US";
    //    public static final String CONTACT_API = BASE_URL + "/v1/customers/";
    public static final String COUNTRY_API = BASE_URL + "/v1/countries";//get api for coutries
    public static final String VERIFY_FUNDUPIN_API = BASE_URL + "/v1/customers/verify/fundupin"; //post service
    public static final String KEN_CONFIRMATION_API = BASE_URL + "/v1/customers/confirm";
    public static final String QUESTION_API = BASE_URL + "/v1/customers/security/question";//get api for questions
    public static final String CHANGE_FUNDU_PIN_API = BASE_URL + "/v1/customers/change/fundupin"; // post api for changing Fundu Pin
    public static final String RESET_FUNDU_PIN_API = BASE_URL + "/v1/customers/reset/fundupin"; // post api for Checking Security Answers
    public static final String CHECK_ANSWER_RESET_PIN_API = BASE_URL + "/v1/customers/reset/fundupin/check"; // post api for Checking Security Answers
    public static final String CHANGE_ACCOUNT_NO_API = BASE_URL + "/v2/customers/transactions/changeAccountNo";
    //    localhost:9999/v2/customers/verifyAgent/mobile_type:+254755555511?become_merchant=true
    public static final String VERIFY_AGENT_API = BASE_URL + "/v2/customers/verifyAgent/%s:%s?become_merchant=%s";
    public static final String ISMERCHANT = BASE_URL + "/v2/customers/isRegisterdAgent/contact_id_type/%s/contact_id/%s";
    public static final String UPDATEMERCHANTTIMING = BASE_URL + "/v2/customers/updateMerchantTimings";
    public static final String HAS_FUND_API = BASE_URL + "/v1/customers/checkfunds/balance";
    public static final String TRANSACTION_HISTORY = BASE_URL + "/v1/customers/transaction/history" + LOCALE + "&country_shortname=%s&custid=%s&start=%s&end=%s";
    public static final String FINALREGISTRATION_API = BASE_URL + "/v1/customers/finalreg"; //FinalRegistrationAPI POST
    public static final String SECURITY_QUESTION_CARD = BASE_URL + "/v1/customers/security/question/";

    public static final String CONTACT_API = BASE_URL + "/v1/customers/verify";
    public static final String GET_CONTACT_API = BASE_URL + "/v1/customers/%s:%s";
    public static final String GET_NEIGHBORS_API = BASE_URL + "/v1/neighbors?latitude=%s&longitude=%s";// get service
    public static final String UPDATE_LOCATION = BASE_URL + "/v1/customers/%s:%s/locations";// put service
    public static final String TRANSACTION_INITIATE = BASE_URL + "/v1/customers/%s:%s/transactions"; // post service
    public static final String TRANSACTION_CONFIRM = TRANSACTION_INITIATE + "/confirm"; // post service
    public static final String TRANSACTION_STATUS = TRANSACTION_INITIATE +"/%s";//transaction status
    public static final String TRANSACTION = BASE_URL + "/v1/customers/%s:%s/transactions/%s";

    //public static final String TRANSACTION_COMMIT = BASE_URL + "/v1/customers/%s:%s/transactions?TransactionId=%s&needcash=%s"; // put service
    //public static final String TRANSACTION_REVERT = BASE_URL + "/v1/customers/%s:%s/transactions?TransactionId=%s&cancel_reason=%s"; // delete service

    public static final String TRANSACTION_COMMIT = BASE_URL + "/v1/customers/%s:%s/transactions?TransactionId=%s&needcash=%s&bankTransactionId=%s"; // put service
    public static final String TRANSACTION_REVERT = BASE_URL + "/v1/customers/%s:%s/transactions/%s?cancel_reason=%s"; // delete service

    public static final String UPDATE_RATING = BASE_URL + "/v1/customers/%s:%s/ratings";
    //public static final String SAVE_CONTACTS_ON_SERVER = BASE_URL + "/v1/customers/%s:%s/contacts/"+ FunduUser.getCountryShortName()+"?locale=en_US"; // post service
    public static final String USER_CONTACTS = BASE_URL + "/v1/customers/%s:%s/contacts";
    public static final String USER_ACCOUNTS = BASE_URL + "/v1/customers/%s:%s/accounts";
    public static final String GET_NEAR_BY_CONTACTS = BASE_URL + "/v1/customers/%s:%s/neighbors"; // get service
    //public static final String VERIFY_OTP_API = BASE_URL + "/v1/customers/%s:%s"; //put service
    public static final String VERIFY_OTP_API = BASE_URL + "/v1/customers/verify/%s:%s"; //put service
    public static final String UPDATE_AND_POST_SETTING = BASE_URL + "/v1/customers/%s:%s/settings?locale=en_US"; // put,post,get service
    public static final String CUSTOMER = BASE_URL + "/v1/customers/%s:%s"; // put service
    public static final String GET_CONTACT_INFORMATION = BASE_URL + "/v1/customers/%s:%s/contacts/%s:%s";// GET SERVICE
    public static final String FIND_TRANSACTION_PAIR_API = BASE_URL + "/v1/customers/%s:%s/transactions?findTransactionPair=true";
    public static final String INVITE_FRIEND = BASE_URL + "/v1/customers/%s:%s/contacts/%s:%s?invite=true";
    public static final String GET_BANKS_NAME = BASE_URL + "/v1/banks";
    public static final String KEN_TRANSFER_API = BASE_URL + "/v1/customers/transactions/transfer";
    public static final String CONTACTS_STATUS = USER_CONTACTS + "?type=%s";
    public static final String VERIFY_TRANX_CODE = BASE_URL + "/v1/customers/mobile_type:%s/transactions/verify";
    public static final String UPDATE_SINGOUT = BASE_URL + "/v1/customers/mobile_type:%s/logout";
    public static final String MASK_CALL = BASE_URL + "/v1/customers/mobile_type:%s/transactions/call";
    public static final String LOCATIONS = BASE_URL + "/v1/customers/mobile_type:%s/locations";
    public static final String MERCHANTS = BASE_URL+"/v1/merchants/%s";
    public static final String PUSH_MESSAGE_TO_MERCHANT = MERCHANTS+"/notification";
    public static final String REPORT_ISSUE = CUSTOMER+"/issue";
    public static final String INVITE_MESSAGE = CUSTOMER +"/invite";

    public static final String CAMPAIGN_API = BASE_URL + "/v1/campaign";//get api for campaigns


}
