package in.co.eko.fundu.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.ClientError;
import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.co.eko.fundu.R;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.database.greendao.FunduTransaction;
import in.co.eko.fundu.database.tables.TransactionStatusTable;
import in.co.eko.fundu.models.Contact;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.requests.TransactionPairResponseRequest;
import in.co.eko.fundu.utils.Fog;
import in.co.eko.fundu.utils.FunduAnalytics;
import in.co.eko.fundu.utils.GreenDaoHelper;
import in.co.eko.fundu.utils.Utils;
import in.co.eko.fundu.views.CircleProgressBar;

public class CashRequestAction extends AppCompatActivity {

    ArrayList<String> alerts;
    private TextView cashRequestDesc,mRating,mAmount,howfar,incentive;
    private int pStatus;
    private CountDownTimer downTimer;
    private ImageView user_image;
    private int time = 30000;
    private CircleProgressBar mCircularProgressOuter,mCircularProgressInner;
    JSONObject jdata;
    private FunduTransaction currentTransaction;
    private View loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_request_action);
        alerts = getIntent().getStringArrayListExtra(Constants.ALERT);
        try {
            jdata = new JSONObject(getIntent().getStringExtra(Constants.PUSH_JSON_DATA));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Fog.d("bundle",""+jdata);
        init();
    }
    private void init(){
        cashRequestDesc = (TextView)findViewById(R.id.cash_request_desc);
        mRating = (TextView)findViewById(R.id.user_rating);
        mAmount = (TextView)findViewById(R.id.amount);
        howfar = (TextView)findViewById(R.id.howfar);
        incentive = (TextView)findViewById(R.id.incentive);
        user_image = (ImageView) findViewById(R.id.user_image);
        mCircularProgressOuter = (CircleProgressBar) findViewById(R.id.progress_outer);
        mCircularProgressInner = (CircleProgressBar) findViewById(R.id.progress_inner);
        loader = findViewById(R.id.loader);
        //mProgress = (ProgressBar)findViewById(R.id.circularProgressbar);
        try {

            jdata.put(Constants.PushNotificationKeys.PROVIDER,FunduUser.getContactId());
            currentTransaction = GreenDaoHelper.getInstance(this).populateTransaction(alerts,jdata);
            currentTransaction.setState(Constants.TRANSACTION_STATE.PROVIDER_ACCEPTED.getCode());
            double rating = Double.valueOf(currentTransaction.getRating());
            cashRequestDesc.setText(currentTransaction.getName()+ " is requesting cash.\nHurry! Time is running out.");
            if(rating > 0){
                mRating.setText(String.format("%.1f", rating));
            }
            else
                mRating.setText(R.string.new_user);
            double d = Double.valueOf(alerts.get(8));
            if(d>=1000){
                metersToKm(alerts.get(8));
            }
            else{
                howfar.setText(d+" m"+"\n"+"away");
            }

            if(currentTransaction.getProviderCharge() != null && currentTransaction.getProviderCharge().equalsIgnoreCase("0")){
                incentive.setText("");
            }
            else if(FunduUser.getCountryShortName().equalsIgnoreCase("IND")){
                incentive.setText("Earn "+getResources().getString(R.string.ruppee_symbol)+currentTransaction.getProviderCharge()
                        +" when you complete this request.");
            }
            else{
                incentive.setText("Earn "+getResources().getString(R.string.ksh_symbol)+currentTransaction.getProviderCharge()
                        +" when you complete this request.");
            }
            String imageUrl = currentTransaction.getImage();
            if(jdata!=null&&!TextUtils.isEmpty(imageUrl)&&!imageUrl.equalsIgnoreCase("null")){
                Picasso.with(this).load(imageUrl).into(user_image);
            }
            int amount = (int)Double.parseDouble(currentTransaction.getAmount());
            if(FunduUser.getCountryShortName().equalsIgnoreCase("IND")) {
                //spannableString.setSpan(new RelativeSizeSpan(1.5f), 1, spannableString.length(), 0);
                mAmount.setText(getString(R.string.ruppee_symbol)+amount);
            }
            else{
                //spannableString.setSpan(new RelativeSizeSpan(1.5f), 1, spannableString.length(), 0);
                mAmount.setText(getString(R.string.ksh_symbol)+amount);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        showProgress();

    }
    public void onAcceptClick(View view){
        FunduAnalytics.getInstance(CashRequestAction.this).sendAction("Transaction","Accepted",(int)Double.parseDouble(currentTransaction.getAmount()));
        updateDecision(true);
    }
    public void onDismissClick(View view){
        FunduAnalytics.getInstance(CashRequestAction.this).sendAction("Transaction","Rejected",(int)Double.parseDouble(currentTransaction.getAmount()));
        updateDecision(false);
    }
    public void updateDecision(final boolean decision){
        if(loader.getVisibility()==View.VISIBLE)
            return;
        loader.setVisibility(View.VISIBLE);
        final Intent intent = getIntent();

        if (intent.getIntExtra(Constants.PUSH_TYPE, -1) == Constants.PUSH_TYPE_ENUM.ACCEPT_REJECT.getCode()) {
            Fog.e("NOTI FRR", intent.getStringArrayListExtra(Constants.ALERT).toString());
            TransactionPairResponseRequest transactionPairRequest = new TransactionPairResponseRequest(this);
            transactionPairRequest.setData(alerts.get(1), alerts.get(2), decision ? "1" : "2");
            transactionPairRequest.setParserCallback(new TransactionPairResponseRequest.OnTransactionPairResults() {

                @Override
                public void onTransactionPairResponse(Contact contact) {
                    loader.setVisibility(View.GONE);
                    if(downTimer != null ){
                        downTimer.cancel();
                    }
                    if (decision) {
                       /* Gson gson = new Gson();
                        String json = gson.toJson(contact);*/
                        FunduTransaction transaction = GreenDaoHelper.getInstance(CashRequestAction.this).addTransaction(currentTransaction);
                        TransactionStatusTable.addTransaction(CashRequestAction.this, alerts);
                        Intent notificationIntent = new Intent(CashRequestAction.this, GiveCash.class);
                        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        notificationIntent.putStringArrayListExtra(Constants.ALERT, alerts);
                        notificationIntent.putExtra(Constants.PUSH_JSON_DATA, jdata.toString());
                        notificationIntent.putExtra(Constants.FUNDU_TRANSACTION_ID,transaction.getId());
                        startActivity(notificationIntent);
                        //Toast.makeText(CashRequestAction.this, "Transaction Pair request ACCEPTED.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        //Toast.makeText(CashRequestAction.this, "Request cancelled.", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                }

                @Override
                public void onTransactionPairError(VolleyError error) {
                    loader.setVisibility(View.GONE);
                    if(error instanceof ClientError) {
                        if(decision) {
                            Toast.makeText(CashRequestAction.this, R.string.cra, Toast.LENGTH_SHORT).show();
                        }
                        finish();
                    } else if (error.networkResponse != null) {
                        if (error.networkResponse.statusCode == 408){
                            Toast.makeText(CashRequestAction.this, R.string.cra, Toast.LENGTH_SHORT).show();
                            finish();
                        }

                    } else {
                        Toast.makeText(CashRequestAction.this,  R.string.cra, Toast.LENGTH_SHORT).show();
                    }
                }
            });
            if (Utils.isNetworkAvailable(CashRequestAction.this))
                transactionPairRequest.start();
        }
    }
    public void showProgress() {

        // activity.getSupportActionBar().setHomeButtonEnabled(false);
//        updateDrawerState(false);
        downTimer = new CountDownTimer(time, 300) {
            public void onTick(long millisUntilFinished) {
                pStatus+=1;
               // mProgress.setProgress(pStatus);
                mCircularProgressOuter.setProgress(pStatus);
                mCircularProgressInner.setProgress(pStatus);
                //String songsFound = getResources().getQuantityString(R.plurals.numberOfSeconds, (int) millisUntilFinished / 1000, (int) millisUntilFinished / 1000);

            }

            public void onFinish() {
                hideProgress();
                //Utils.showShortToast(CashRequestAction.this,getString(R.string.user_not_accept_request));
            }
        }.start();
    }
    public void hideProgress() {
        pStatus = 0;
        //mProgress.setProgress(0);
        mCircularProgressInner.setProgress(0);
        mCircularProgressOuter.setProgress(0);
        /*activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
        if (downTimer != null) {
            downTimer.cancel();
        }
        finish();

    }


    public void metersToKm(String distance){

       double dist = Double.valueOf(distance);
       dist = dist/1000;
       Fog.d("dist",""+String.valueOf(dist));
       howfar.setText(dist+" Km"+"\n Away");

    }
}
