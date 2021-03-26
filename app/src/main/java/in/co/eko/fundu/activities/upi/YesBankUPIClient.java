package in.co.eko.fundu.activities.upi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import in.co.eko.fundu.R;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.utils.Fog;

import static in.co.eko.fundu.activities.upi.YesBankUPIClient.YES_BANK_CLIENT_ACTION.ADD_ACCOUNT;
import static in.co.eko.fundu.activities.upi.YesBankUPIClient.YES_BANK_CLIENT_ACTION.CHECK_BALANCE;
import static in.co.eko.fundu.activities.upi.YesBankUPIClient.YES_BANK_CLIENT_ACTION.FETCH_PROFILE;
import static in.co.eko.fundu.activities.upi.YesBankUPIClient.YES_BANK_CLIENT_ACTION.MANAGE_ACCOUNT;
import static in.co.eko.fundu.activities.upi.YesBankUPIClient.YES_BANK_CLIENT_ACTION.SCAN_QR_CODE;
import static in.co.eko.fundu.activities.upi.YesBankUPIClient.YES_BANK_CLIENT_ACTION.SET_UPI_PIN;
import static in.co.eko.fundu.constants.Constants.merchantKey;
import static in.co.eko.fundu.constants.Constants.mid;

public class YesBankUPIClient extends AppCompatActivity {
    private String TAG = this.getClass().getName();
    private YES_BANK_CLIENT_ACTION action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yes_bank_upiclient);
        action = (YES_BANK_CLIENT_ACTION) getIntent().getSerializableExtra("action");
        doAction();
    }
    @Override
    protected void onStart(){
        super.onStart();
    }
    private void doAction() {
        switch(action) {
            case FETCH_PROFILE:
                fetchProfile();
                break;
            case CHECK_BALANCE:
                checkUserBalance();
                break;
            case ADD_ACCOUNT:
                addAccount();
                break;
            case SET_UPI_PIN:
                setUpiPin();
                break;
            case MANAGE_ACCOUNT:
                manageAccount();
                break;
            case SCAN_QR_CODE:
                scanQRCode();
                break;
        }
    }


    private void scanQRCode(){
        Bundle bundle = new Bundle();
        bundle.putString("mid", mid);
        bundle.putString("merchantKey", merchantKey);

        // Scn QR Code // add code to scan QRcode
        Toast.makeText(this, R.string.sqr_add, Toast.LENGTH_SHORT).show();



//        Intent intent = new Intent(getApplicationContext(), QRScanActivity.class);
//        intent.putExtras(bundle);
//        startActivityForResult(intent, SCAN_QR_CODE.getCode());
    }
    private void setUpiPin(){
        String accountId,accountNo,bankCode,bankName;
        accountId = getIntent().getStringExtra("recipient_id");
        accountNo = getIntent().getStringExtra("accno");
        bankCode = getIntent().getStringExtra("bank_code");
        bankName = getIntent().getStringExtra("bank_name");
        Bundle bundle = new Bundle(); bundle.putString("mid", mid);
        bundle.putString("merchantKey", merchantKey);
        bundle.putString("merchantTxnId", ""+new Date().getTime());
        bundle.putString("virtualAddress", FunduUser.getVpa());
        bundle.putString("mobileNo", FunduUser.getContactId());
        bundle.putString("accountId", accountId);
        bundle.putString("accountNumber", accountNo);
        bundle.putString("bankCode", bankCode);
        bundle.putString("bankName", bankName);
        bundle.putString("add1", "");
        bundle.putString("add2","");
        bundle.putString("add3", "");
        bundle.putString("add4", "");
        bundle.putString("add5", "");
        bundle.putString("add6","");
        bundle.putString("add7", "");
        bundle.putString("add8", "");
        bundle.putString("add9", "NA");
        bundle.putString("add10", "NA");

        Toast.makeText(this, R.string.upi_pin_add, Toast.LENGTH_SHORT).show();


//        Intent intent = new Intent(getApplicationContext(), SetMpinBankActivity.class);
//        intent.putExtras(bundle);
//        startActivityForResult(intent, SET_UPI_PIN.getCode());
    }
    private void fetchProfile() {
        Bundle bundle = new Bundle();
        bundle.putString("merchantId", mid);
        bundle.putString("merchantTxnId", new Date().getTime() + "");
        bundle.putString("enckey", merchantKey);
        bundle.putString("virtualAddress", FunduUser.getVpa());
        bundle.putString("bankCode", "YESB");
        bundle.putString("reqFlag", "T");
        bundle.putString("add1", "");
        bundle.putString("add2", "");
        bundle.putString("add3", "");
        bundle.putString("add4", "");
        bundle.putString("add5", "");
        bundle.putString("add6", "");
        bundle.putString("add7", "");
        bundle.putString("add8", "");
        bundle.putString("add9", "NA");
        bundle.putString("add10", "NA");

        Toast.makeText(this, R.string.fetch_profile_add, Toast.LENGTH_SHORT).show();

//        Intent intent = new Intent(getApplicationContext(), RegistrationAccountList.class);
//        intent.putExtras(bundle);
//        startActivityForResult(intent, FETCH_PROFILE.getCode());
    }

    private void checkUserBalance() {
        String accountId = FunduUser.getRecipientId();
        if(getIntent().getStringExtra("recipient_id") != null){
            accountId = getIntent().getStringExtra("recipient_id");
        }
        Bundle bundle = new Bundle();
        bundle.putString("merchantId", mid);
        bundle.putString("merchantTxnId", "" + new Date().getTime());
        bundle.putString("accId", accountId);
        bundle.putString("virtualAddress", FunduUser.getVpa());
        bundle.putString("enckey", merchantKey);
        bundle.putString("add1", "");
        bundle.putString("add2", "");
        bundle.putString("add3", "");
        bundle.putString("add4", "");
        bundle.putString("add5", "");
        bundle.putString("add6", "");
        bundle.putString("add7", "");
        bundle.putString("add8", "");
        bundle.putString("add9", "NA");
        bundle.putString("add10", "NA");

        Toast.makeText(this, R.string.check_balance_add, Toast.LENGTH_SHORT).show();


//        Intent intent = new Intent(this, BalanceEnquiryActivity.class);
//        intent.putExtras(bundle);
//        startActivityForResult(intent, CHECK_BALANCE.getCode());
    }
    private void addAccount(){
        Date now = new Date();
        Bundle bundle = new Bundle();
        bundle.putString("mid", mid);
        bundle.putString("merchantKey", merchantKey);
        bundle.putString("merchantTxnID", ""+now.getTime());
        bundle.putString("virtualAddress", FunduUser.getVpa());
        //bundle.putString("theme_color","AppTheme");
        bundle.putString("add1", "");
        bundle.putString("add2", "");
        bundle.putString("add3", "");
        bundle.putString("add4", "");
        bundle.putString("add5", "");
        bundle.putString("add6", "");
        bundle.putString("add7", "");
        bundle.putString("add8", "");
        bundle.putString("add9", "NA" );
        bundle.putString("add10", "NA" );
            /*Yes bank UPI Account Management*/

        Toast.makeText(this, R.string.user_account_add, Toast.LENGTH_SHORT).show();


//        Intent intent = new Intent(this, AddAccountActivity.class);
//        intent.putExtras(bundle); startActivityForResult(intent, ADD_ACCOUNT.getCode());


    }
    private void manageAccount(){
        Date now = new Date();
        Bundle bundle = new Bundle();
        bundle.putString("mid", mid);
        bundle.putString("merchantKey", merchantKey);
        bundle.putString("merchantTxnID", ""+now.getTime());
        bundle.putString("virtualAddress", FunduUser.getVpa());
        //bundle.putString("theme_color","AppTheme");
        bundle.putString("add1", "");
        bundle.putString("add2", "");
        bundle.putString("add3", "");
        bundle.putString("add4", "");
        bundle.putString("add5", "");
        bundle.putString("add6", "");
        bundle.putString("add7", "");
        bundle.putString("add8", "");
        bundle.putString("add9", "NA" );
        bundle.putString("add10", "NA" );
            /*Yes bank UPI Account Management*/

        Toast.makeText(this, R.string.manage_account_add, Toast.LENGTH_SHORT).show();


//        Intent intent = new Intent(this, AccountManagementActivity.class);
//        intent.putExtras(bundle); startActivityForResult(intent, MANAGE_ACCOUNT.getCode());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        Fog.i(TAG,"onActivityResult: "+requestCode+" "+resultCode);
        if(data != null) {
            if(resultCode == Activity.RESULT_OK && requestCode == FETCH_PROFILE.getCode() && getIntent().getBooleanExtra("defaultAccount",false)){
                Bundle bundle = data.getExtras();
                String statusCode = bundle.getString("status");
                if(statusCode.equalsIgnoreCase("S")) {
                    String accList = bundle.getString("accList");
                    try {
                        JSONArray jAccList = new JSONArray(accList);
                        for(int i = 0;i<jAccList.length();i++){
                            JSONObject defaultAccount = jAccList.getJSONObject(i);
                            if(defaultAccount.getString("defAccFlag").equalsIgnoreCase("T")){
                                data.putExtra("defAcc",defaultAccount.toString());
                                break;
                            }
                        }
                    } catch(JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            Bundle extras = data.getExtras();
            Fog.logBundle(TAG,extras);
        }
        setResult(resultCode, data);
        finish();
    }
    public enum YES_BANK_CLIENT_ACTION {
        REGISTRATION(1),
        PAYMENT(2),
        CHECK_BALANCE(3),
        FETCH_PROFILE(4),
        ADD_ACCOUNT(5),
        SET_UPI_PIN(6),
        MANAGE_ACCOUNT(7),
        SCAN_QR_CODE(8);
        int code;

        YES_BANK_CLIENT_ACTION(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }


}
