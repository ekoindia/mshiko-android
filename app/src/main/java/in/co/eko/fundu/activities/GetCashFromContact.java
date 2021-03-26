package in.co.eko.fundu.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.co.eko.fundu.R;
import in.co.eko.fundu.constants.API;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.database.greendao.FunduTransaction;
import in.co.eko.fundu.fragments.ContactsNearBy;
import in.co.eko.fundu.fragments.EnterAmount;
import in.co.eko.fundu.gcm.FunduNotificationManager;
import in.co.eko.fundu.interfaces.OnFragmentInteractionListener;
import in.co.eko.fundu.models.ContactItem;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.models.UserProfileItem;
import in.co.eko.fundu.parser.UniversalParser;
import in.co.eko.fundu.requests.CallWebService;
import in.co.eko.fundu.requests.FindTransactionPairRequest;
import in.co.eko.fundu.requests.HasFundRequest;
import in.co.eko.fundu.requests.TransactionInitiateRequest;
import in.co.eko.fundu.utils.Fog;
import in.co.eko.fundu.utils.FunduAnalytics;
import in.co.eko.fundu.utils.GreenDaoHelper;
import in.co.eko.fundu.utils.UserTransactions;
import in.co.eko.fundu.utils.Utils;
import in.co.eko.fundu.views.ProgressOverlay;

public class GetCashFromContact extends BaseActivity implements  TransactionInitiateRequest.OnTransactionInitiateResults, OnFragmentInteractionListener, FunduNotificationManager.OnPairResult,CallWebService.ObjectResponseCallBack,FindTransactionPairRequest.OnFindTransactionPairResults {
    private ContactItem selectedContact;
    private int amountP;
    private ProgressDialog dialog;
    private String TAG = GetCashFromContact.class.getName();
    private ArrayList<String> alertArray;
    private String username,fee;
    private JSONObject jdata;
    @BindView(R.id.feedback)
    View mFeedback;
    @BindView(R.id.progressOverlay)
    ProgressOverlay progressOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_cash_from_contact);
        ButterKnife.bind(this);
        init();

        //ButterKnife.inject(this);
        addFragment(ContactsNearBy.newInstance(),true);

        onIntent(getIntent());
    }

    private void init(){
        dialog = new ProgressDialog(this);
        mFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = getWindow().getDecorView().getRootView();
                v.setDrawingCacheEnabled(true);
                Bitmap bmp = Bitmap.createBitmap(v.getDrawingCache());
                v.setDrawingCacheEnabled(false);
                Utils.takeFeedback(bmp,GetCashFromContact.this);
            }
        });
    }


    public void setSelectedContact(ContactItem selectedContact) {
        this.selectedContact = selectedContact;
        getUserInfoApiCall();
        addFragment(EnterAmount.newInstance(),true);
    }
    public void submitNeedCashRequest(String amountNeeded,String charges){
        fee = charges;
        try {
            amountP = Integer.parseInt ( amountNeeded );
            checkWallet(amountP,charges);

        }catch (NumberFormatException e){
            e.printStackTrace ();
        }
    }

    private void checkWallet(int amount, final String charges) {
        dialog.setMessage(getString(R.string.text_check_balance));
        dialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("custid", FunduUser.getCustomerId());
            object.put("mobile", pref.getString(Constants.PrefKey.CONTACT_NUMBER));
            object.put("country_shortname", pref.getString(Constants.COUNTRY_SHORTCODE));
            object.put("amount", String.valueOf(amount));
//            if (transactionType == SEND_MONEY_TO_WALLET)
            //object.put("type", Constants.SEND_MONEY_TYPE);
//            else
            object.put("type", Constants.NEED_CASH_TYPE);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        HasFundRequest request = new HasFundRequest(getApplicationContext(), object);
        request.setParserCallback(new HasFundRequest.OnHasFundResults() {
            @Override
            public void onHasFundResponse(String response) {
                dialog.dismiss();
                progressOverlay.hideProgress();
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    String walletAmount = jsonObject.optString("Balance Amount");
                    if (jsonObject.has("Balance Amount")) {
                        if (walletAmount.length() == 0 || walletAmount.equals("0.0") || walletAmount.equals("0.00")) {
                            float currentAmount = Float.parseFloat(walletAmount);
                            float requestedAmount = amountP;
                            if(pref.getString(Constants.COUNTRY_SHORTCODE).equalsIgnoreCase("IND")){
                                currentAmount = 10000;
                            }
                            if (requestedAmount <= currentAmount) {
                                FunduNotificationManager.setOnPairResult(GetCashFromContact.this);
                                  progressOverlay.setTime(30000, 30);
                                  startFindTransactionPairRequest(charges);

                            } else {
                                Toast.makeText(GetCashFromContact.this, "Insufficient Amount. Please load your wallet first.", Toast.LENGTH_SHORT).show();
                                float neededAmount = requestedAmount - currentAmount;
                                //refillWallet(neededAmount);
                            }
                        } else {
                            float currentAmount = Float.parseFloat(walletAmount);
                            float requestedAmount = amountP;
                            if (requestedAmount <= currentAmount) {

                            } else {
                                Toast.makeText(GetCashFromContact.this, "Insufficient Amount. Please load your wallet first.", Toast.LENGTH_SHORT).show();
                                float neededAmount = requestedAmount - currentAmount;
                                //refillWallet(neededAmount);
                            }
                        }
                    } else {
                        if (jsonObject.has("Error"))
                            Toast.makeText(GetCashFromContact.this, jsonObject.optString("Error"), Toast.LENGTH_SHORT).show();
                    }
                    if(jsonObject.optString("status").equalsIgnoreCase("Success")){

                        int charges = jsonObject.optInt("charges");
                        FunduUser.setChargesKen(String.valueOf(charges));
                        FunduNotificationManager.setOnPairResult(GetCashFromContact.this);
                        startFindTransactionPairRequest(""+charges);

                    }
                    else if (jsonObject.optString("status").equalsIgnoreCase("ERROR")){
                        if (jsonObject.optString("message").equalsIgnoreCase("Customer doesn't exist")
                                || jsonObject.optString("message").equalsIgnoreCase("Customer not registered with us")) {
                            HomeActivity.Signout(getApplicationContext());
                            finish();
                        }
                        Toast.makeText(getApplicationContext(), jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Fog.d(TAG, "Exception - Due to unexpected key-value");
                }
            }

            @Override
            public void onHasFundError(VolleyError error) {
                dialog.dismiss();
            }
        });
        request.start();
    }
    private void startFindTransactionPairRequest(String mfee) {
        UserTransactions.getInstance().findPair(this, getString(R.string.wallet), FunduUser.getContactId(),
                FunduUser.getContactIDType(), selectedContact.getContactNumber(), FunduUser.getContactIDType(),
                amountP, 1000, false,mfee,null);
        if (progressOverlay != null) {
            progressOverlay.setName(selectedContact.getContactName());
            progressOverlay.showProgress();
        }
    }

    @Override
    public void onTransactionInitiateResponse(JSONObject response) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }

        try {
            if (response.getString("status").equals("SUCCESS")) {
                String s = response.getJSONObject("data").getJSONObject("data").getString("tid");
                try{
                    jdata.put(Constants.PushNotificationKeys.TID,s);
                    jdata.put(Constants.PushNotificationKeys.SEEKER,FunduUser.getContactId());
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
                FunduTransaction transaction = GreenDaoHelper.getInstance(GetCashFromContact.this).addTransaction(alertArray,jdata);
                FunduAnalytics.getInstance(GetCashFromContact.this).sendAction("Transaction","Initiated",(int)Double.parseDouble(transaction.getAmount()));
                GreenDaoHelper.getInstance(GetCashFromContact.this).updateTransactionState(transaction.getId(),
                        Constants.TRANSACTION_STATE.SEEKER_INITIATED.getCode());
                Fog.d("TransactionID", s);
                Intent intent1 = new Intent(GetCashFromContact.this, PairContactFoundActivity.class);
                intent1.putExtras(getIntent());
                intent1.putExtra(Constants.FUNDU_TRANSACTION_ID,transaction.getId());
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent1.putExtra("tt",Constants.GET_CASH_TYPE);
                startActivity(intent1);
                finish();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            try {
                Toast.makeText(this, response.getString("message"), Toast.LENGTH_SHORT).show();
                if (response.getString("message").equalsIgnoreCase("Insufficient Balance.")) {
                }
            } catch (JSONException e1) {
                e1.printStackTrace();
                Toast.makeText(this, "message key not found", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onTransactionInitiateError(VolleyError error) {

    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        onIntent(intent);
    }

    private void onIntent(final Intent intent) {
        if (intent.hasExtra(Constants.ALERT) && intent.hasExtra(Constants.PUSH_TYPE)) {
            hideProgressOverlay();
            this.alertArray = intent.getStringArrayListExtra(Constants.ALERT);
            try {
                this.jdata = new JSONObject(intent.getStringExtra(Constants.PUSH_JSON_DATA));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (intent.getIntExtra(Constants.PUSH_TYPE, -1) == Constants.PUSH_TYPE_ENUM.PAIR_FOUND.getCode() && FunduUser.getUser() != null) {
                try{
                    amountP =  jdata.getInt(Constants.PushNotificationKeys.AMOUNT);
                    String contactNumber = jdata.getString(Constants.PushNotificationKeys.PHONENUMBER);
                    initTransaction(contactNumber, amountP,alertArray,jdata);
                }
                catch (JSONException e){
                    e.printStackTrace();
                }



            } else if (intent.getIntExtra(Constants.PUSH_TYPE, -1) == Constants.PUSH_TYPE_ENUM.NO_PAIR_FOUND.getCode()) {
                Toast.makeText(GetCashFromContact.this, username + " is not available at this moment. Please try again later."/*alertArray.get(0)*/, Toast.LENGTH_SHORT).show();
                hideProgressOverlay();
            } else if (intent.getIntExtra(Constants.PUSH_TYPE, -1) == Constants.PUSH_TYPE_ENUM.USER_DIDNOT_ACCEPT.getCode()) {
                Toast.makeText(GetCashFromContact.this, username + " did not accept your request. Please try again later.", Toast.LENGTH_SHORT).show();
                hideProgressOverlay();
                finish();
            }
//            else if (intent.getIntExtra(Constants.PUSH_TYPE, -1) == 6)
//                Utils.showShortToast(getApplicationContext(), Constants.SHOP_CLOSED);
        }
        if (intent.hasExtra(Constants.PUSH_TYPE)) {
            hideProgressOverlay();
            if (intent.getIntExtra(Constants.PUSH_TYPE, -1) == Constants.PUSH_TYPE_ENUM.SHOP_CLOSED.getCode())
                Utils.showShortToast(getApplicationContext(), Constants.SHOP_CLOSED);
        }
    }
    private void hideProgressOverlay() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressOverlay != null)
                    progressOverlay.hideProgress();
            }
        });

    }
    @Override
    public void onFragmentInteraction(Bundle bundle) {

    }

    @Override
    public void onAccepted(ArrayList<String> alertArray,JSONObject jData) {
//        hideProgressOverlay();
//        if (FunduUser.getCountryShortName().equalsIgnoreCase("KEN")){
//            Intent intent1 = new Intent(this, PairContactFoundActivity.class);
//            intent1.putStringArrayListExtra(Constants.ALERT, alertArray);
//            intent1.putExtra(Constants.TRANSACTION_ID, "KENTID");
//            intent1.putExtra("amount", amountP);
//            intent1.putExtra("tt",Constants.GET_CASH_TYPE);
//            startActivity(intent1);
//            finish();
//        }
//        this.alertArray = alertArray;
//        initTransaction(selectedContact.getContactNumber(), amountP,alertArray,jData);
    }
    private void initTransaction(String recipient_mobile, float amount,ArrayList<String> alertArray,JSONObject jData) {

        //String recipentId = alertArray.get(11);
        String recipientId = "1234565";

        Fog.d("GetCash","recipentId"+recipientId);
        if (!(username==null || username.equalsIgnoreCase(""))) {
             {
                if (FunduUser.getCountryShortName().equalsIgnoreCase("KEN"))
                {
                        FunduTransaction funduTransaction = GreenDaoHelper.getInstance(this).addTransaction(alertArray,jData);
                    GreenDaoHelper.getInstance(GetCashFromContact.this).updateTransactionState(funduTransaction.getId(),
                            Constants.TRANSACTION_STATE.SEEKER_INITIATED.getCode());
                        Intent intent1 = new Intent(GetCashFromContact.this, PairContactFoundActivity.class);
                        intent1.putStringArrayListExtra(Constants.ALERT, alertArray);
                        intent1.putExtra(Constants.TRANSACTION_ID, "KENTID");
                        intent1.putExtra(Constants.PUSH_JSON_DATA, jData.toString());
                        intent1.putExtra("amount", amountP);
                        intent1.putExtra("tt",Constants.GET_CASH_TYPE);
                        intent1.putExtra(Constants.FUNDU_TRANSACTION_ID,funduTransaction.getId());
                        startActivity(intent1);
                        finish();
                }
                else {
                    dialog.show();
                    pref.putInt(Constants.PrefKey.NEED_AMOUNT, amountP);

                    UserTransactions.getInstance().initiateTransactions(this, getString(R.string.upi), FunduUser.getContactId(),
                            FunduUser.getContactIDType(), recipient_mobile, FunduUser.getContactIDType(),
                            pref.getInt(Constants.PrefKey.NEED_AMOUNT, -1), 1000,recipientId,jData.optString("provider_charge"),jData.optString(Constants.PushNotificationKeys.FEE),jData.optString(Constants.PushNotificationKeys.PAIR_REQUEST_ID));

                }
            }
        }
        else{
            Utils.showShortToast(getApplicationContext(),"Customer doesn't exist!");
            finish();
        }
    }
    private void getUserInfoApiCall() {

        CallWebService.getInstance(this, true, Constants.ApiType.GET_CONTACT_INFORMATION).hitJsonObjectRequestAPI(CallWebService.GET, createUrl(), null, this);
    }

    private String createUrl() {

            return String.format(API.GET_CONTACT_INFORMATION, FunduUser.getContactIDType(), FunduUser.getContactId(), FunduUser.getContactIDType(), selectedContact.getContactNumber());
    }


    @Override
    public void onJsonObjectSuccess(JSONObject response, int apiType) throws JSONException {
        Fog.e("UserProfile", response.toString());
        if (response.has("name")){
            UserProfileItem userProfileItem = UniversalParser.getInstance().parseJsonObject(response, UserProfileItem.class);
            username = userProfileItem.getName();
        }
        else{
            //onBackPressed();
        }

    }
    @Override
    public void onNoPairFound(ArrayList<String> alertArray) {
        hideProgressOverlay();
        Toast.makeText(this, "Transaction Not Accepted!", Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Transaction Not Accepted!", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onFailure(String str, int apiType) {
        try{
            JSONObject job = new JSONObject(str);
            String error = job.optString("ERROR");
            Utils.showShortToast(this, error);
        }
        catch (Exception e){
            Utils.showShortToast(this, str);
        }
        finish();
    }
    @Override
    public void onFindTransactionPairResponse(JSONObject contact) {

    }

    @Override
    public void onFindTransactionPairError(VolleyError error) {

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }



}
