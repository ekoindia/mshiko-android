package in.co.eko.fundu.constants;
/*
 * Created by Bhuvnesh
 */

public class API extends V1API{

    // Accepts requestid, clientid & and decision. Decision's value can be either 1(accepted) or 2(rejected).
    public static final String TRANSACTION_PAIR_RESPONSE_API = BASE_URL + "/contact/transaction-pair-response?requestid=%s&contactid=%s&decision=%s";
    public static final String LOAD_WALLET_API = BASE_URL + "/transaction/loadwallet";
    // Contact id
    public static final String CHECK_BALANCE_API = BASE_URL + "/transaction/customer/%s/balance";
    // contact id type and contact id
    public static final String UPDATE_GCM_TOKEN = BASE_URL + "/contact/additional_identities/contact_id_type/%s/contact_id/%s";






















    //  public static final String SERVER = "http://59.162.104.10";
    //    public static final String SERVER = "http://10.106.31.24";
//      public static final String SERVER = "http://fundu.mobi";//***fundu Prod****//


//    public static final String PORT = "27002"; // Port 27001, 2700

//    public static final String SERVER = "http://connecvtbeta.eko.co.in"; //***fundu QA****//

//    public static final String SERVER = "http://59.162.104.10"; //***fundu QA****//
//    public static final String PORT = "27003";    // New Port 19/Dec/16

//    public static final String PORT = "9999"; // Local Udit
//    public static final String SERVER = "http://192.168.60.230";//Local Udit 192.168.60.48

//    public static final String SERVER = "http://192.168.60.48";//Local Saurav
//    public static final String SERVER = "http://192.168.60.46";//Local Saurav
//    public static final String BASE_URL = SERVER + ":" + PORT;






}
