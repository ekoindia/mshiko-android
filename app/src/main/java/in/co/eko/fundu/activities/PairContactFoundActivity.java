package in.co.eko.fundu.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Date;

import in.co.eko.fundu.R;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.database.greendao.FunduTransaction;
import in.co.eko.fundu.event.SeekerTransactionEvent;
import in.co.eko.fundu.fragments.SupportFragment;
import in.co.eko.fundu.interfaces.OnAzimuthChangedListener;
import in.co.eko.fundu.interfaces.OnFragmentInteractionListener;
import in.co.eko.fundu.interfaces.OnLocationChangedListener;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.models.User;
import in.co.eko.fundu.requests.CallMaskingRequest;
import in.co.eko.fundu.requests.GetTransactionStatus;
import in.co.eko.fundu.requests.KenConfirmationRequest;
import in.co.eko.fundu.requests.KenTransferRequest;
import in.co.eko.fundu.requests.TransactionCancelRequest;
import in.co.eko.fundu.requests.TransactionCommitRequest;
import in.co.eko.fundu.requests.TransactionConfirmRequest;
import in.co.eko.fundu.requests.TransactionInitiateRequest;
import in.co.eko.fundu.requests.UpdateRatingRequest;
import in.co.eko.fundu.utils.ContactsUtils;
import in.co.eko.fundu.utils.DateUtils;
import in.co.eko.fundu.utils.Fog;
import in.co.eko.fundu.utils.FunduAnalytics;
import in.co.eko.fundu.utils.GreenDaoHelper;
import in.co.eko.fundu.utils.MyCurrentAzimuth;
import in.co.eko.fundu.utils.MyCurrentLocation;
import in.co.eko.fundu.utils.Utils;
import in.co.eko.fundu.views.Pinview;

import static in.co.eko.fundu.R.id.user_rating;
import static in.co.eko.fundu.R.id.whenYouMeet;
import static in.co.eko.fundu.constants.Constants.appName;
import static in.co.eko.fundu.constants.Constants.ekoVPA;
import static in.co.eko.fundu.constants.Constants.merchantKey;
import static in.co.eko.fundu.constants.Constants.mid;
import static in.co.eko.fundu.dialogs.TotpDialog.entertotpedit;

/**
 * ICICI Bank UPI Imports
 */
//import com.icicibank.isdk.ISDK;
//import com.icicibank.isdk.listner.ISDKUPIPaymentStatusListner;


public class PairContactFoundActivity extends BaseActivity implements SensorEventListener, View.OnClickListener, TransactionCancelRequest.OnTransactionCancelResults, AppCompatDialog.OnDismissListener, View.OnKeyListener, TransactionCommitRequest.OnTransactionCommitResults,
        /*ISDKUPIPaymentStatusListner,*/OnFragmentInteractionListener, OnAzimuthChangedListener, OnLocationChangedListener, TransactionConfirmRequest.onTransactionConfirmRequestResult {

    private String TAG = PairContactFoundActivity.class.getName();
    private MyCurrentLocation myCurrentLocation;
    private double mMyLatitude, mMyLongitude;
    private MyCurrentAzimuth myCurrentAzimuth;
    private LinearLayout llCall, progressbarKenya, progressbar;
    private TextView help, cancelTransaction, note, userRating, txtTranxCode, transactionCodeInfo,
            userName, pairContactDesc, okButton, title, distance, cancelTransaction1;
    View  enterTransactionCodeView, llCompassView;
    ImageButton icon;
    ImageView  needle, navIcon, userImage, dot1,
            dot2, dot3, dot4, callIcon, kenyadot1, kenyadot2, kenyadot3, mCompass;
    RelativeLayout relativeLayoutmain;
    private Pinview fPin, confirmPin;
    private String fPinNum, confirmPinNum;
    private ProgressDialog dialog;
    private boolean isdialogsuccess = false, cancel_transaction = false;
    private String seekerType = "PERSON";
    private SensorManager mSensorManager;
    private String transactionType = Constants.NEED_CASH_TYPE;
    private RatingBar ratingBar;
    private FunduTransaction currentTransaction;
    private double theta;

    /**
     * Volley requests
     */
    private TransactionConfirmRequest transactionConfirmRequest;
    private KenTransferRequest kenTransferRequest;
    private UpdateRatingRequest ratingRequest;
    private CallMaskingRequest callMaskingRequest;
    private KenConfirmationRequest confirmrequest;

    public PairContactFoundActivity() {

    }

    public void receivedTOTP(String totp) {
        Fog.e("recived Totp", totp);
        try {
            entertotpedit.setText(totp);
        } catch(Exception e) {
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pair_contact_found);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        View view = this.getCurrentFocus();
        if(view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        init();
        populateTransactionInfo();
        showInfoBasedOnState();
        setOnClickListeners();
        EventBus.getDefault().register(this);
        sync();

    }


    private void setOnClickListeners() {
//        floatingActionButton.setOnClickListener(this);
//        imageButton.setOnClickListener(this);
        //activateGPS.setOnClickListener(this);
        //closePopup.setOnClickListener(this);
        navIcon.setOnClickListener(this);
        callIcon.setOnClickListener(this);
        okButton.setOnClickListener(this);
        userImage.setOnClickListener(this);
        llCall.setOnClickListener(this);
        cancelTransaction.setOnClickListener(this);
        //close.setOnClickListener(this);
    }


    private void populateTransactionInfo() {
        long fTid = getIntent().getLongExtra(Constants.FUNDU_TRANSACTION_ID, -1);
        if(fTid == -1) {

            throw new NullPointerException("populateTransactionInfo Something bungled seriously");
        }

        transactionType = getIntent().getStringExtra("tt");
        if(transactionType == null)
            transactionType = "needcash";
        currentTransaction = GreenDaoHelper.getInstance(this).getTransaction(fTid);


    }

    private void showInfoBasedOnState() {
        if(currentTransaction.getState() == Constants.TRANSACTION_STATE.SEEKER_INITIATED.getCode()) {
            displayInfo();
        } else if(currentTransaction.getState() == Constants.TRANSACTION_STATE.SEEKER_ACCOUNT_DEBITED.getCode()) {
            updateReceiveCashUI();
        } else if(currentTransaction.getState() == Constants.TRANSACTION_STATE.RATING_PENDING.getCode()) {
            showRatingView();
        }

    }

    private void getNewIntent(Bundle savedInstanceState) {
//        Constants.TransactionStatus status = (Constants.TransactionStatus)getIntent().getSerializableExtra(Constants.TRANSACTION_STATUS);
//        if(status != null) {
//            if (status == Constants.TransactionStatus.SUCCESS) {
//                onIntent(getIntent());
//            }
//        }
//        else {
//            pFees = FunduUser.getChargesKen();
//            confirmrequest = new KenConfirmationRequest(getApplicationContext());
//            if (getIntent().getStringExtra(Constants.ACTION) != null) {
//                fetchingItem();
//            }
//            else
//            {
//                alerts = getIntent().getStringArrayListExtra(Constants.ALERT);
//                jdata =  getIntent().getStringArrayListExtra(Constants.DATA);
//                Fog.d("pAmount", "********" + pAmount);
//                if (alerts.get(1).equalsIgnoreCase("ATM")||
//                        alerts.get(2).equalsIgnoreCase("ATM")) {
//                    updateAtmUi(savedInstanceState);
//                    String location[] = jdata.get(1).split(",");
//                    lat = Double.parseDouble(location[0].replace("[", ""));
//                    lng = Double.parseDouble(location[1].replace("]", ""));
//
//                }
//                else{
//
//                    transactionType = getIntent().getStringExtra("tt");
//                    int i = alerts.size();
//
//                     String location[] = jdata.get(1).split(",");
//                     lat = Double.parseDouble(location[0].replace("[", ""));
//                     lng = Double.parseDouble(location[1].replace("]", ""));
//
//                    if (!TextUtils.isEmpty(currentTransaction.getImage())&&!currentTransaction.getImage().equalsIgnoreCase("null")){
//                        Picasso.with(this).load(currentTransaction.getImage()).fit().into(userImage);
//                    }
//
//
//                    if (transactionType.equalsIgnoreCase(Constants.GET_CASH_TYPE))
//
//                        if (currentTransaction.getTid() == null) {
//                            if (null != getSupportActionBar()) {
//                                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//                                getSupportActionBar().setHomeButtonEnabled(true);
//                            }
//                            findViewById(R.id.linearLayout).setVisibility(View.GONE);
//                        }
//                    myCurrentLocation = new MyCurrentLocation(this,this);
//                    myCurrentLocation.buildGoogleApiClient(this);
//                    myCurrentLocation.start();
//                    userName.setText(currentTransaction.getName());
//                    int amt = (int) pAmount;
//                    if(FunduUser.getCountryShortName().equalsIgnoreCase("IND")){
//                        progressbar.setVisibility(View.VISIBLE);
//                        progressbarKenya.setVisibility(View.GONE);
//                        pairContactDesc.setText("Meet " + pName + " to collect " + getString(R.string.ruppee_symbol) + amt + " cash.");
//                        progressIcon = (ImageView)Utils.makeMeBlink(progressIcon,500,10);
//                    }
//                    else{
//                        progressbar.setVisibility(View.GONE);
//                        progressbarKenya.setVisibility(View.VISIBLE);
//                        pairContactDesc.setText("Meet " + pName + " to collect " + getString(R.string.ksh_symbol) + amt + " cash.");
//                        kenyadot1 = (ImageView)Utils.makeMeBlink(kenyadot1,500,10);
//                    }
//                    title.setText(getString(R.string.meet) + " " + pName);
//                    note.setText("Use compass to find " + pName + " .");
//                    if(currentTransaction.getRating()!=null){
//                        double d = Double.parseDouble(currentTransaction.getRating());
//                        String rating1 = String.format("%.1f",d);
//
//                        Fog.d("d","d****"+rating1);
//                        if(!TextUtils.isEmpty(rating1)){
//                            userRating.setText(rating1);
//                        }
//                        else{
//                            userRating.setText("0.0");
//                        }
//                    }
//                }
//                callGoogleDirectionRequest();
//
//            }
//        }
    }

    private void showRating(Double rating) {
        if(rating > 0) {
            String rating1 = String.format("%.1f", rating);
            userRating.setText(rating1);
        } else {
            userRating.setText(R.string.new_user);
        }
    }

    private void displayInfo() {
        userName.setText(currentTransaction.getName());
        double d = Double.parseDouble(currentTransaction.getRating());
        showRating(d);

        needle.setVisibility(View.VISIBLE);
        title.setText("Meet " + currentTransaction.getName());
        int amount = (int) Double.parseDouble(currentTransaction.getAmount());
        if(FunduUser.getCountryShortName().equalsIgnoreCase("IND")) {
            pairContactDesc.setText("Meet " + currentTransaction.getName() + " to collect " + getString(R.string.ruppee_symbol) + amount + " cash.");
        } else {
            pairContactDesc.setText("Meet " + currentTransaction.getName() + " to collect " + getString(R.string.ksh_symbol) + amount + " cash.");
        }

        note.setText("Use compass to find " + currentTransaction.getName() + " .");
        String imageUrl = currentTransaction.getImage();
        if(!imageUrl.equalsIgnoreCase("") && !imageUrl.equalsIgnoreCase("null")) {
            Picasso.with(this).load(imageUrl).into(userImage);
        }
        calculateNeedleAngle();
    }


    private void showRatingView() {
        if(myCurrentLocation != null)
            myCurrentLocation.stop();
        currentTransaction.setState(Constants.TRANSACTION_STATE.RATING_PENDING.getCode());
        double d = Double.parseDouble(currentTransaction.getRating());
        showRating(d);
        userName.setText(currentTransaction.getName());
        String imageUrl = currentTransaction.getImage();
        if(!TextUtils.isEmpty(imageUrl) && !imageUrl.equalsIgnoreCase("null"))
            Picasso.with(this).load(imageUrl).fit().into(userImage);
        findViewById(R.id.kenVerification).setVisibility(View.GONE);
        findViewById(R.id.llCompassView).setVisibility(View.GONE);
        findViewById(R.id.enterTransactionCodeView).setVisibility(View.GONE);
        findViewById(R.id.trancation_success).setVisibility(View.VISIBLE);
        TextView transactionIncentive = (TextView) findViewById(R.id.transaction_incentive);
        transactionIncentive.setVisibility(View.VISIBLE);
        String temp[] = currentTransaction.getAmount().split("\\.");
        //String incentive[] = FunduTransaction.getIncentive().substring(1);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                updateUserRating(1);
            }
        });
        String nameInitials[] = currentTransaction.getName().split(" ");
        title.setText(getString(R.string.rate) + " " + nameInitials[0]);
        TextView transactionDesc = (TextView) findViewById(R.id.transaction_desc);
        transactionDesc.setText(getString(R.string.amount_debitted));
        TextView amountT = (TextView) findViewById(R.id.amountValue);
        String pAmount = currentTransaction.getAmount();
        pairContactDesc.setVisibility(View.GONE);
        int totalAmt = Integer.parseInt(temp[0]) + (int) Double.parseDouble(currentTransaction.getFee());
        int currencyRes = R.string.ruppee_symbol;
        if(FunduUser.getCountryShortName().equalsIgnoreCase("KEN")) {
            currencyRes = R.string.ksh_symbol;
            kenyadot2.clearAnimation();
            Utils.makeMeBlink(kenyadot3, 500, 10);
        } else {
            dot3.clearAnimation();
            dot4 = (ImageView) Utils.makeMeBlink(dot4, 500, 10);
        }
        transactionIncentive.setText("You have been charged " + getString(currencyRes) + " " + totalAmt + ".");
        amountT.setText(getString(currencyRes) + " " + pAmount);

    }

    public static double round(double value, int places) {
        if(places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    private void openMapIntent() {

        double srcLat = currentTransaction.getRequestLatitude();
        double srcLng = currentTransaction.getRequestLongitude();
        double desLat = currentTransaction.getLatitude();
        double desLng = currentTransaction.getLongitude();

        Location locationA = new Location("point A");
        locationA.setLatitude(srcLat);
        locationA.setLongitude(srcLng);


        Location locationB = new Location("point B");
        locationB.setLatitude(desLat);
        locationB.setLongitude(desLng);

        Utils.openMapIntent(locationA, locationB, this);

    }


    public String makeURL(Double destlat, Double destlog) {

        return "https://maps.googleapis.com/maps/api/directions/json" + "?origin=" + String.valueOf(FunduUser.getUser().getLatitude()) + "," + String.valueOf(FunduUser.getUser().getLongitude()) + "&destination=" + destlat + "," + destlog + "&sensor=false&mode=walking&alternatives=true&key=" + getResources().getString(R.string.google_map_key_for_server);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);

    }

    private void calculateNeedleAngle() {
        double myLat = FunduUser.getLatitude();
        double myLong = FunduUser.getLongitude();
        double oLat = currentTransaction.getLatitude();
        double oLong = currentTransaction.getLongitude();
        if(oLong == myLong) {
            theta = 90;
        } else {
            double value = (oLat - myLat) / (oLong - myLong);
            theta = Math.toDegrees(Math.atan(value));
        }

        theta = 90 - theta;

    }

    public void onClickInfomationButton(View view) {

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case whenYouMeet:
                if(FunduUser.getCountryShortName().equalsIgnoreCase("Ken")) {
                    if((int) v.getTag() == 2) {
                        //Verify
                        verifyKenTransaction();
                    }
                } else {
                    if(v.getTag() instanceof Integer) {
                        //  updateUserRating(1);
                        showUPIPopup();
                    } else
                        showUPIPopup();
                    return;
                }
                if(currentTransaction.getTid() != null) {
                    if(FunduUser.getCountryShortName().equals("KEN")) {

                        int amount = (int) Double.parseDouble(currentTransaction.getAmount());
                        confirmrequest.setData(FunduUser.getCustomerId(), amount, FunduUser.getCountryShortName(), transactionType/*Constants.NEED_CASH_TYPE*/);
                        confirmrequest.setParserCallback(new KenConfirmationRequest.KenConfirmationResults() {
                            @Override
                            public void onKenConfirmationResponse(String object) {
                                dialog.dismiss();
                                String message = "", status = "";
                                try {
                                    JSONObject job = new JSONObject(object);
                                    status = job.optString("status");
                                    message = job.optString("message");
                                } catch(Exception e) {
                                    e.printStackTrace();
                                }

                                if(status.equalsIgnoreCase("ERROR")) {
                                    Utils.showLongToast(getApplicationContext(), message);
                                } else {
                                    //totpDialog.setData(pName, pMobile, pTId, pAmount, Integer.parseInt(pFees), pCustid, transactionType/*Constants.NEED_CASH_TYPE*/);
                                    //totpDialog.show();
                                    //Authorize Transaction
                                    if(FunduUser.getCountryShortName().equalsIgnoreCase("KEN")) {
                                        authorizeTransaction();
                                    } else {

                                    }

                                }
                            }

                            @Override
                            public void onKenConfirmationError(VolleyError error) {
                                dialog.dismiss();
                                Utils.showShortToast(getApplicationContext(), "Error!");
//                            totpDialog.show();
                            }
                        });
                        if(Utils.isNetworkAvailable(getApplicationContext())) {
                            dialog.show();
                            confirmrequest.start();
                        }
                    }
                }
                break;
            case R.id.cancelTransaction:
                //Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show();
                FunduAnalytics.getInstance(this).sendAction("Transaction", "NeedCash", "Cancel");
                popUpCancelDialog();

                break;
            case R.id.nav_icon:
                openMapIntent();
                break;

            case R.id.ll_call:
            case R.id.call:
                callPairedUser(true);
//                dialog.show();
//                new GetContactsTask().execute();
                break;
            case R.id.user_image:
                popUpImageviewDialog();
                break;


        }
    }

    private MarkerOptions mo;


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onTransactionCancelResponse(JSONObject response) {
        if(dialog.isShowing()) {
            dialog.dismiss();
        }
        FunduAnalytics.getInstance(PairContactFoundActivity.this).sendAction("Transaction", "Cancelled", (int) Double.parseDouble(currentTransaction.getAmount()));
        if(currentTransaction.getTid()==null || currentTransaction.getTid().length()==0) {
            Fog.logEvent(false, currentTransaction.getPairRequestId(), "PairContactFoundActivity","onTransactionCancelResponse", "onTransactionCancelResponse", DateUtils.getCurrentUTCtime(), Constants.getState(currentTransaction.getState()));
        } else {
            Fog.logEvent(true, currentTransaction.getTid(), "PairContactFoundActivity","onTransactionCancelResponse", "onTransactionCancelResponse", DateUtils.getCurrentUTCtime(), Constants.getState(currentTransaction.getState()));
        }
        GreenDaoHelper.getInstance(this).deleteFunduTransaction(currentTransaction.getId());
        Toast.makeText(this, "Transaction canceled successfully.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();

    }

    @Override
    public void onTransactionCancelError(VolleyError error) {
        if(dialog.isShowing()) {
            dialog.dismiss();
        }
        try {
            String errorN = new String(error.networkResponse.data);
            if(errorN.contains("Already cancelled")) {
                if(currentTransaction.getTid()==null || currentTransaction.getTid().length()==0) {
                    Fog.logEvent(false, currentTransaction.getPairRequestId(), "PairContactFoundActivity","onTransactionCancelError", "already_cancelled", DateUtils.getCurrentUTCtime(), Constants.getState(currentTransaction.getState()));
                } else {
                    Fog.logEvent(true, currentTransaction.getTid(), "PairContactFoundActivity","onTransactionCancelError", "already_cancelled", DateUtils.getCurrentUTCtime(), Constants.getState(currentTransaction.getState()));
                }
                GreenDaoHelper.getInstance(PairContactFoundActivity.this).deleteFunduTransaction(currentTransaction.getId());
                finish();
                return;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        if(error.getMessage() != null)
            Toast.makeText(this, "" + error.getMessage()
                    , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {

        Fog.e("DISMISS", "" + isdialogsuccess);
        if(cancel_transaction) {
            cancel_transaction = true;
            finish();
        }
        if(isdialogsuccess) {
            isdialogsuccess = false;
            Intent intent = new Intent(this, TransactionSuccessActivity.class);
            intent.putExtra(Constants.RATING_TYPE, 1);
            intent.putExtra(Constants.TRANSACTION_ID, currentTransaction.getTid());
            intent.putExtra(Constants.TOTAL_AMOUNT, currentTransaction.getAmount());
            intent.putExtra("pname", currentTransaction.getName());
            intent.putExtra("pmobile", currentTransaction.getPhoneNumber());
            startActivity(intent);
            finish();
        }
    }

    private void init() {
        relativeLayoutmain = (RelativeLayout) findViewById(R.id.fragmentContainer);
        enterTransactionCodeView = (View) findViewById(R.id.enterTransactionCodeView);
        llCompassView = (View) findViewById(R.id.llCompassView);
        llCall = (LinearLayout) findViewById(R.id.ll_call);
        progressbar = (LinearLayout) findViewById(R.id.progress_bar_4);
        progressbarKenya = (LinearLayout) findViewById(R.id.progress_bar_3);
        llCall = (LinearLayout) findViewById(R.id.ll_call);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        userName = (TextView) findViewById(R.id.user_name);
        pairContactDesc = (TextView) findViewById(R.id.pair_contact_desc);
        title = (TextView) findViewById(R.id.title);
        distance = (TextView) findViewById(R.id.distance);
        needle = (ImageView) findViewById(R.id.needle);
        mCompass = (ImageView) findViewById(R.id.main_compass);
        userImage = (ImageView) findViewById(R.id.user_image);
        dot1 = (ImageView) findViewById(R.id.dot1_4);
        dot2 = (ImageView) findViewById(R.id.dot2_4);
        dot3 = (ImageView) findViewById(R.id.dot3_4);
        dot4 = (ImageView) findViewById(R.id.dot4_4);
        kenyadot1 = (ImageView) findViewById(R.id.dot1_3);
        kenyadot2 = (ImageView) findViewById(R.id.dot2_3);
        kenyadot3 = (ImageView) findViewById(R.id.dot3_3);
        userImage = (ImageView) findViewById(R.id.user_image);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        txtTranxCode = ((TextView) findViewById(R.id.txtTranxCode));
        cancelTransaction1 = (TextView) findViewById(R.id.cancelTransaction1);
        transactionCodeInfo = ((TextView) findViewById(R.id.transactionCodeInfo));
        note = ((TextView) findViewById(R.id.note));
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        userRating = ((TextView) findViewById(user_rating));
        /*cancelTransaction1 = (TextView) enterTransactionCodeView.findViewById(R.id.cancelTransaction1);*/
        cancelTransaction = (TextView) findViewById(R.id.cancelTransaction);
        okButton = (TextView) findViewById(whenYouMeet);
        help = (TextView) findViewById(R.id.help);
        navIcon = (ImageView) findViewById(R.id.nav_icon);
        //floatingActionButton = (ImageView) findViewById(R.id.fab);
        icon = (ImageButton) findViewById(R.id.imageButton);
        //imageButton = findViewById(R.id.imageButtonClose);
        llCompassView.setVisibility(View.VISIBLE);
        callIcon = (ImageView) findViewById(R.id.call);
        dialog = new ProgressDialog(PairContactFoundActivity.this);
        dialog.setMessage("Committing transaction...");
        dialog.setCancelable(false);
        myCurrentAzimuth = new MyCurrentAzimuth(this, this);
        myCurrentAzimuth.start();

        if(FunduUser.getCountryShortName().equalsIgnoreCase("KEN")) {
            okButton.setText(getString(R.string.initiateTranx));
            okButton.setTag(1);
            progressbarKenya.setVisibility(View.VISIBLE);
            progressbar.setVisibility(View.GONE);
            kenyadot1.setImageResource(R.drawable.meet_circle_complete);
            kenyadot2 = (ImageView) Utils.makeMeBlink(kenyadot2, 500, 10);
        } else {
            progressbarKenya.setVisibility(View.GONE);
            progressbar.setVisibility(View.VISIBLE);
            dot1.setImageResource(R.drawable.meet_circle_complete);
            dot1 = (ImageView) Utils.makeMeBlink(dot1, 500, 10);
        }
        confirmrequest = new KenConfirmationRequest(this);
        findViewById(R.id.feedback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = getWindow().getDecorView().getRootView();
                v.setDrawingCacheEnabled(true);
                Bitmap bmp = Bitmap.createBitmap(v.getDrawingCache());
                v.setDrawingCacheEnabled(false);
                Utils.takeFeedback(bmp, PairContactFoundActivity.this);
            }
        });

    }

    private void authorizeTransaction() {
        title.setText(getString(R.string.authorize_transaction));
        help.setVisibility(View.VISIBLE);
        fPin = (Pinview) findViewById(R.id.fPin);
        confirmPin = (Pinview) findViewById(R.id.confirmPin);

        if(FunduUser.getCountryShortName().equalsIgnoreCase("KEN")) {
            pairContactDesc.setText(getString(R.string.authorize_transaction_desc_ken));
            findViewById(R.id.kenVerification).setVisibility(View.VISIBLE);
            findViewById(R.id.wholeCompass).setVisibility(View.GONE);
            findViewById(R.id.navigate).setVisibility(View.GONE);
            findViewById(R.id.nav_icon).setVisibility(View.GONE);
            findViewById(R.id.note).setVisibility(View.GONE);
            fPin.setVisibility(View.VISIBLE);
            confirmPin.setVisibility(View.VISIBLE);
            okButton.setText(getString(R.string.confirm));
            okButton.setTag(2);
            fPin.requestFocus();
            dot2.setImageResource(R.drawable.meet_circle_complete);

            fPin.setPinViewEventListener(new Pinview.PinViewEventListener() {
                @Override
                public void onDataEntered(Pinview pinview, boolean fromUser) {
                    fPinNum = pinview.getValue();
                    confirmPin.requestFocus();
                    Fog.d("pin", "******" + fPinNum);
                }
            });

            confirmPin.setPinViewEventListener(new Pinview.PinViewEventListener() {
                @Override
                public void onDataEntered(Pinview pinview, boolean fromUser) {
                    confirmPinNum = pinview.getValue();
                    Fog.d("pin", "******" + confirmPinNum);
                }
            });

        } else {

        }

        kenyadot1.setImageResource(R.drawable.meet_circle_complete);
        kenyadot2.setImageResource(R.drawable.meet_circle_complete);
        kenyadot1.clearAnimation();
        kenyadot2 = (ImageView) Utils.makeMeBlink(kenyadot2, 500, 10);


    }

    private void verifyIndTransaction() {
        if(Constants.upiProvider == Constants.UPI_PROVIDER.ICICI) {
            //ISDK.makeUPIPayment(this,transactionId,"1.0","INR","","","",this);
        } else if(Constants.upiProvider == Constants.UPI_PROVIDER.YESBANK) {
            if(Constants.dummyUPI) {
                sendYesBankDummy();
                return;
            }
            startTransaction();
        }
        /*ISDK.makePayment(this,transactionId,String.valueOf("1.0"),"INR","","","",this);*/
        // onUPIResult(true, transactionId, "1111111");
        // sendYesBankDummy();
    }

    private void showUPIPopup() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.upi_transaction_info, null);
        TextView tv = (TextView) dialogView.findViewById(R.id.upi_auth_details_text);

        String upiAmount = String.format("%1.0f", Double.parseDouble(currentTransaction.getAmount()) + Double.parseDouble(currentTransaction.getFee()));
        tv.setText(String.format(getString(R.string.upi_auth_details), "₹" + upiAmount));
        dialogBuilder.setView(dialogView);
        final Dialog upiDialog = dialogBuilder.show();
        Button iUnderstand = (Button) dialogView.findViewById(R.id.i_understand);
        iUnderstand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyIndTransaction();
                upiDialog.dismiss();
            }
        });
    }

    private void verifyKenTransaction() {

        findViewById(R.id.kenVerification).setVisibility(View.VISIBLE);
        findViewById(R.id.wholeCompass).setVisibility(View.GONE);
        title.setText("Authorize Transaction");
        help.setVisibility(View.VISIBLE);
        findViewById(R.id.helptext).setVisibility(View.VISIBLE);
        String error = null;
        fPinNum = fPin.getValue();
        confirmPinNum = confirmPin.getValue();

        Fog.d("fPinNum", "fPinNum" + fPinNum);
        Fog.d("fPinNum", "confirmPinNum" + confirmPinNum);
        if(fPinNum.length() < 4) {
            error = "Incomplete pin.";
        } else if(confirmPinNum.length() < 4) {
            error = "Incomplete verification code.";
        }
        if(error != null) {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            return;
        }
        kenTransferRequest = new KenTransferRequest(this);
        if(transactionType == null)
            transactionType = "needcash";
        kenTransferRequest.setData(FunduUser.getCustomerId(), currentTransaction.getCustid(), (int) Double.parseDouble(currentTransaction.getAmount()), FunduUser.getCountryShortName(), confirmPinNum, Utils.md5(fPinNum), transactionType);
        kenTransferRequest.setParserCallback(new KenTransferRequest.OnKenTransferResults() {
            @Override
            public void onKenTransferResponse(String object) {
                dialog.dismiss();
                try {
                    Fog.d("Fundu Resp", object);
                    JSONObject response = new JSONObject(object);
                    if(response.has("status")) {
                        if(response.getString("status").equalsIgnoreCase("ERROR")) {
                            if(response.getString("message").contains("Incorrect one time password")) {
                                Utils.showLongToast(PairContactFoundActivity.this, "Wrong verification Code");
                            } else if(response.optString("message").equalsIgnoreCase("No universal account")) {
                                new AlertDialog.Builder(PairContactFoundActivity.this)
                                        .setTitle("Alert")
                                        .setMessage(currentTransaction.getName() + " has entered wrong Account Details. Wait for him correct his " +
                                                "account details and then you can initiate transaction again.")
                                        .setCancelable(false)
                                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // Whatever...
                                                //cdt.cancel();
                                                fPin.resetValues();
                                                confirmPin.resetValues();

                                            }
                                        }).show();
                            } else
                                Utils.showLongToast(PairContactFoundActivity.this, response.getString("message"));

                            new CountDownTimer((5) * 1000, 1000) {

                                public void onTick(long millisUntilFinished) {

                                    fPin.setPinBackgroundRes(R.drawable.code_mismatch);
                                    confirmPin.setPinBackgroundRes(R.drawable.code_mismatch);

                                    //here you can have your logic to set text to edittext
                                }

                                public void onFinish() {
                                    //fPin.requestFocus();
                                    confirmPin.resetValues();
                                    fPin.resetValues();
                                    confirmPinNum = "";
                                    fPinNum = "";
                                    fPin.requestFocus();
                                    fPin.setPinBackgroundRes(R.drawable.code_letter_back);
                                    confirmPin.setPinBackgroundRes(R.drawable.code_letter_back);
                                }

                            }.start();
                        } else if(response.getString("status").equalsIgnoreCase("SUCCESS")) {
                            if(response.has("message")) {

                                if(response.optString("message").equalsIgnoreCase("Funds transfered successfully!")
                                        || response.optString("message").equalsIgnoreCase("SUCCESS")) {

                                   /* enterFunduPin.setText("");
                                    entertotpedit.setText("");*/
                                    fPin.resetValues();
                                    confirmPin.resetValues();

                                    if(transactionType.equalsIgnoreCase(Constants.NEED_CASH_TYPE) || transactionType.equalsIgnoreCase(Constants.GET_CASH_TYPE)) {
//
                                        onTransactionSuccess(object);
                                    }

                                } else if(response.optString("message").contains("Money send successfully!")
                                        || response.optString("message").contains("transaction was successfull")) {

                                    fPin.resetValues();
                                    confirmPin.resetValues();

                                    Toast.makeText(PairContactFoundActivity.this, "Money sent successfully!", Toast.LENGTH_SHORT).show();
                                    if(transactionType.equalsIgnoreCase(Constants.SEND_MONEY_TYPE)) {
//

                                        onTransactionSuccess(object);

                                    }

                                } else if(response.optString("message").equalsIgnoreCase("Incorrect FundU Pin")) {

                                    //   Toast.makeText(PairContactFoundActivity.this,"clicked",Toast.LENGTH_SHORT).show();
                                    new CountDownTimer((5) * 1000, 1000) {

                                        public void onTick(long millisUntilFinished) {
                                            confirmPin.resetValues();
                                            fPin.resetValues();
                                            confirmPinNum = "";
                                            fPinNum = "";
                                            fPin.setPinBackgroundRes(R.drawable.code_mismatch);
                                            confirmPin.setPinBackgroundRes(R.drawable.code_mismatch);
                                            fPin.requestFocus();

                                        }

                                        public void onFinish() {
                                            fPin.requestFocus();
                                            fPin.setPinBackgroundRes(R.drawable.code_letter_back);
                                            confirmPin.setPinBackgroundRes(R.drawable.code_letter_back);
                                        }

                                    }.start();


                                    Toast.makeText(PairContactFoundActivity.this, "Incorrect Fundu PIN entered!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Utils.showLongToast(PairContactFoundActivity.this, response.getString("message"));
                                }
                            }
                        }
                    } else {
                        Utils.showLongToast(PairContactFoundActivity.this, "Error!");
                    }

                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onKenTransferError(VolleyError error) {
                Fog.d("Fundu Resp E", "Error");
                dialog.dismiss();
                Toast.makeText(PairContactFoundActivity.this, "Transaction Error!", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.setMessage("Verifying...");
        dialog.show();
        kenTransferRequest.start();

    }

    private void onTransactionSuccess(String response) {

        if(FunduUser.getCountryShortName().equalsIgnoreCase("KEN")) {
            GreenDaoHelper.getInstance(this).updateTransactionState(currentTransaction.getId(), Constants.TRANSACTION_STATE.RATING_PENDING.getCode());
            showRatingView();
        } else {
            GreenDaoHelper.getInstance(this).updateTransactionState(currentTransaction.getId(), Constants.TRANSACTION_STATE.SEEKER_ACCOUNT_DEBITED.getCode());
            try {
                JSONObject jRes = new JSONObject(response);
                String jMessage = jRes.optString("message");
                String code = new JSONObject(jMessage).optString("code");
                currentTransaction.setState(Constants.TRANSACTION_STATE.PROVIDER_VERIFY_CODE.getCode());
                currentTransaction.setCode(code);
                GreenDaoHelper.getInstance(this).updateTransaction(currentTransaction);
                updateReceiveCashUI();

            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateReceiveCashUI() {

        enterTransactionCodeView.setVisibility(View.VISIBLE);
        llCompassView.setVisibility(View.GONE);
        cancelTransaction1.setVisibility(View.VISIBLE);
        cancelTransaction1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popUpCancelDialog();
                //Toast.makeText(PairContactFoundActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
            }
        });
        String code = currentTransaction.getCode();
        txtTranxCode.setText(code);
        userName.setText(currentTransaction.getName());
        title.setText("Meet " + currentTransaction.getName());
        String imageUrl = currentTransaction.getImage();
        if(imageUrl != null && !imageUrl.equalsIgnoreCase("null")) {
            Fog.d("Picasso", "" + imageUrl);
            Picasso.with(this).load(imageUrl).into(userImage);
        }
        double d = Double.parseDouble(currentTransaction.getRating());
        showRating(d);
        String temp[] = currentTransaction.getAmount().split("\\.");
        int amt = Integer.parseInt(temp[0]);

        String firstName[] = currentTransaction.getName().split(" ");
        transactionCodeInfo.setText("Please " + "collect " + getString(R.string.rs_symbol) + " " + amt + " cash from"
                + " " +
                firstName[0] + " and provide the code below to " + firstName[0]
                + " to complete the transaction.");

        help.setVisibility(View.VISIBLE);
        findViewById(R.id.helptext).setVisibility(View.VISIBLE);

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SupportFragment supportFragment = new SupportFragment();
                addFragment(supportFragment, true);
            }
        });
        dot1.setImageResource(R.drawable.meet_circle_complete);
        dot2.setImageResource(R.drawable.meet_circle_complete);
        dot3.setImageResource(R.drawable.meet_circle_complete);
        dot1.clearAnimation();
        dot2.clearAnimation();
        dot3 = (ImageView) Utils.makeMeBlink(dot3, 500, 10);


    }

    private void updateUserRating(int ratingType) {

        final String id = currentTransaction.getTid();
        Fog.d("ratingBar", "ratingBar" + ratingBar.getRating());
        User user = FunduUser.getUser();
        if(user != null) {
            ratingRequest = new UpdateRatingRequest(this);
            if(ratingBar.getRating() < 1) {
                Utils.showShortToast(this, "Kindly fill atleast 1 rating");
                return;
            }
            JSONObject object = null;
            try {
                object = new JSONObject();
                object.put("rated_by_id", FunduUser.getContactIDType() + ":" + FunduUser.getContactId());
                object.put("transaction_id", id);
                object.put("rating", ratingBar.getRating());
            } catch(JSONException e) {
                e.printStackTrace();
            }
            ratingRequest.setData(object, currentTransaction.getPhoneNumber() /*list.get(1)*/);
            if(dialog != null) {
                dialog.show();
            }

            ratingRequest.setParserCallback(new UpdateRatingRequest.OnUpdateRatingResults() {
                @Override
                public void onUpdateRatingResponse(JSONObject response) {
                    if(dialog != null && dialog.isShowing())
                        dialog.dismiss();
                    // String id = FunduTransaction.getTid();
                    if(currentTransaction.getTid()==null || currentTransaction.getTid().length()==0) {
                        Fog.logEvent(false, currentTransaction.getPairRequestId(), "PairContactFoundActivity","updateUserRating", "updateRatingSuccessful", DateUtils.getCurrentUTCtime(), Constants.getState(currentTransaction.getState()));
                    } else {
                        Fog.logEvent(true, currentTransaction.getTid(), "PairContactFoundActivity","updateUserRating", "updateRatingSuccessful", DateUtils.getCurrentUTCtime(), Constants.getState(currentTransaction.getState()));
                    }
                    GreenDaoHelper.getInstance(PairContactFoundActivity.this).deleteFunduTransaction(currentTransaction.getId());
                    Toast.makeText(PairContactFoundActivity.this, "Sucessfully updated.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PairContactFoundActivity.this, HomeActivity.class);
                    // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onUpdateRatingError(VolleyError error) {
                    if(dialog != null && dialog.isShowing())
                        dialog.dismiss();
                    Intent intent = new Intent(PairContactFoundActivity.this, HomeActivity.class);
                    // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                }
            });
            ratingRequest.start();

        }
    }

    public void onClickHelpButton(View view) {
        startActivity(new Intent(PairContactFoundActivity.this, HelpActivity.class));
    }

    public void popUpImageviewDialog() {

        try {
            final Dialog imagedialog = new Dialog(PairContactFoundActivity.this);
            imagedialog.setCancelable(true);
            imagedialog.setCanceledOnTouchOutside(true);
            imagedialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            imagedialog.setContentView(R.layout.imageview_dialog);
            //canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            ImageView profilepic = (ImageView) imagedialog.findViewById(R.id.profilepic);
            ImageView call = (ImageView) imagedialog.findViewById(R.id.call);
            ImageView navigate = (ImageView) imagedialog.findViewById(R.id.navigate);
            TextView name = (TextView) imagedialog.findViewById(R.id.name);
            name.setText(currentTransaction.getName());
            //Fog.d("Picasso",""+alerts.get(alerts.size() - 2));
            String imageUrl = currentTransaction.getImage();
            if(!TextUtils.isEmpty(imageUrl) && !imageUrl.equalsIgnoreCase("null")) {
                Picasso.with(this).load(imageUrl).into(profilepic);
            }

            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // dialog.setMessage("Verifying...");
//                    dialog.show();
//                    new GetContactsTask().execute();
                    callPairedUser(true);
                    imagedialog.dismiss();
                }
            });
            navigate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openMapIntent();
                    imagedialog.dismiss();
                }
            });

            final Window window = dialog.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.CENTER;
            wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);
            imagedialog.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void callPairedUser(boolean hasNumber) {
        FunduAnalytics.getInstance(PairContactFoundActivity.this).sendAction("PairedContact", "Call");
        // String number = pMobile;/*Utils.appendCountryCodeToNumber(getApplicationContext(),mobile)*/
        String number = Utils.appendCountryCodeToNumber(getApplicationContext(), currentTransaction.getPhoneNumber());
        if(hasNumber || FunduUser.getCountryShortName().equalsIgnoreCase("KEN")) {

            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE},
                        10);
                return;
            }
            Uri call = Uri.parse("tel:" + number);
            Intent surf = new Intent(Intent.ACTION_DIAL, call);
            startActivity(surf);
        } else {
            callMaskingRequest = new CallMaskingRequest(this);
            callMaskingRequest.setData(currentTransaction.getTid());
            callMaskingRequest.setParserCallback(new CallMaskingRequest.OnCallMaskingRequestResult() {
                @Override
                public void onCallMaskingResponse(JSONObject object) {
                    dialog.dismiss();
                    Toast.makeText(PairContactFoundActivity.this, object.optString("message"), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCallMaskingError(VolleyError error) {
                    dialog.dismiss();
                }
            });
            callMaskingRequest.start();
            dialog.setMessage("Initiating call");
            dialog.show();
        }


    }

    public void onClickCancelTransaction(View view) {
        popUpCancelDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();
        myCurrentAzimuth.start();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);

    }

    private void sync() {
        if(currentTransaction.getTid() != null) {
            GetTransactionStatus status = new GetTransactionStatus(this, currentTransaction.getTid());
            status.setParserCallback(new GetTransactionStatus.GetTransactionStatusResult() {
                @Override
                public void OnTransactionStatusResponse(JSONObject response) {
                    Fog.i(TAG, "OnTransactionStatusResponse " + response);
                    String transactionId = response.optString("transaction_id");
                    if(transactionId == null || !transactionId.equalsIgnoreCase(currentTransaction.getTid())){
                        //This result is for some other transaction
                        return;
                    }
                    try {
                        int state = response.getInt("transaction_state");

                        switch(state) {
                            case 0:
                                //Transaction Cancelled
                            {
                                if(currentTransaction.getTid()==null || currentTransaction.getTid().length()==0) {
                                    Fog.logEvent(false, currentTransaction.getPairRequestId(), "PairContactFoundActivity","sync", "transaction_cancelled", DateUtils.getCurrentUTCtime(), Constants.getState(currentTransaction.getState()));
                                } else {
                                    Fog.logEvent(true, currentTransaction.getTid(), "PairContactFoundActivity","sync", "transaction_cancelled", DateUtils.getCurrentUTCtime(), Constants.getState(currentTransaction.getState()));
                                }
                                GreenDaoHelper.getInstance(PairContactFoundActivity.this).deleteFunduTransaction(currentTransaction.getId());
                                Intent intent = new Intent(PairContactFoundActivity.this, TransactionStatusActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);
                                finish();
                            }
                            break;
                            case 1:
                                //Transaction Finished
                                if(currentTransaction.getState() != Constants.TRANSACTION_STATE.RATING_PENDING.getCode()) {
                                    showRatingView();
                                }
                                break;
                            case 2:
                                //Transaction initiated
                                break;
                            case 3:
                                //Code screen
                                break;
                            case 4:
                                //Debit Failed
                                showRetryPopup("U30");
                                break;
                            case 5:
                                //Credit Failed
                            {
//                                GreenDaoHelper.getInstance(PairContactFoundActivity.this).deleteFunduTransaction(currentTransaction.getId());
//                                Intent intent = new Intent(PairContactFoundActivity.this, TransactionStatusActivity.class);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                                intent.setAction("transaction_failed");
//                                startActivity(intent);
//                                finish();
                                showRatingView();
                            }
                            break;

                            case 7: {
                                //Transaction failed due to too many incorrect code attempts
                                if(currentTransaction.getTid()==null || currentTransaction.getTid().length()==0) {
                                    Fog.logEvent(false, currentTransaction.getPairRequestId(), "PairContactFoundActivity","sync", "incorrectAttempts", DateUtils.getCurrentUTCtime(), Constants.getState(currentTransaction.getState()));
                                } else {
                                    Fog.logEvent(true, currentTransaction.getTid(), "PairContactFoundActivity","sync", "incorrectAttempts", DateUtils.getCurrentUTCtime(), Constants.getState(currentTransaction.getState()));
                                }
                                GreenDaoHelper.getInstance(PairContactFoundActivity.this).deleteFunduTransaction(currentTransaction.getId());
                                Intent intent = new Intent(PairContactFoundActivity.this, TransactionStatusActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                intent.setAction("transaction_failed");
                                startActivity(intent);
                                finish();

                            }
                            break;
                        }
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void OnTransactionStatusError(VolleyError error) {
                    Fog.i(TAG, "OnTransactionStatusError " + error);
                }
            });
            status.start();
        }


    }

    public void popUpCancelDialog() {

        try {
            final Dialog canceldialog = new Dialog(PairContactFoundActivity.this);
            canceldialog.setCancelable(false);
            canceldialog.setContentView(R.layout.cancel_transaction_reasons);

            final RadioButton[] radioReason = new RadioButton[1];
            final RadioGroup radioGroup = (RadioGroup) canceldialog.findViewById(R.id.cancel_reasons);
            RadioButton radioButton1 = (RadioButton) canceldialog.findViewById(R.id.radioButton1);
            RadioButton radioButton2 = (RadioButton) canceldialog.findViewById(R.id.radioButton2);
            RadioButton radioButton3 = (RadioButton) canceldialog.findViewById(R.id.radioButton3);
            RadioButton radioButton4 = (RadioButton) canceldialog.findViewById(R.id.radioButton4);
            RadioButton radioButton5 = (RadioButton) canceldialog.findViewById(R.id.radioButton5);
            RadioButton radioButton6 = (RadioButton) canceldialog.findViewById(R.id.radioButton6);
            final TextView submit = (TextView) canceldialog.findViewById(R.id.submit);
            TextView cancel = (TextView) canceldialog.findViewById(R.id.cancel);
            Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Muli-Light.ttf");
            radioButton1.setTypeface(font);
            radioButton2.setTypeface(font);
            radioButton3.setTypeface(font);
            radioButton4.setTypeface(font);
            radioButton5.setTypeface(font);
            radioButton6.setTypeface(font);
            cancel.setSelected(true);
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                    if(checkedId != -1) {
                        submit.setEnabled(true);
                    }
                }
            });
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   /*int selectedId=radioGroup.getCheckedRadioButtonId();
                   radioReason[0] =(RadioButton)dialog.findViewById(selectedId);*/
                    String selection;
                    int id = radioGroup.getCheckedRadioButtonId();
                    if(id == -1) {
                        return;
                    } else {
                        View radioButton = radioGroup.findViewById(id);
                        int radioId = radioGroup.indexOfChild(radioButton);
                        RadioButton btn = (RadioButton) radioGroup.getChildAt(radioId);
                        selection = (String) btn.getText();
                    }
                    selection = selection.replace(" ", "");
                    selection = selection.replace("\'", "");
                    Fog.d("selection", "" + selection);
                    if(TextUtils.isEmpty(selection)) {
                        Toast.makeText(PairContactFoundActivity.this, getString(R.string.cancelReason), Toast.LENGTH_SHORT).show();
                    } else {
                        cancelCurrentTransaction(selection);
                        canceldialog.dismiss();

                    }

                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    canceldialog.dismiss();
                }
            });
            final Window window = canceldialog.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.CENTER;
            wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);
            canceldialog.show();
        } catch(Exception e) {
            e.printStackTrace();
        }


    }

    private void retryCurrentTransaction() {
        dialog.setMessage(getString(R.string.please_wait));
        dialog.show();
        final TransactionInitiateRequest initiateRequest = new TransactionInitiateRequest(PairContactFoundActivity.this);

        String recipientId = "1234565";//pushData.optString("recipient_id");
        Fog.d("recipientId", "" + recipientId);
        initiateRequest.setData(getString(R.string.upi), FunduUser.getContactId(), FunduUser.getContactIDType(), recipientId, FunduUser.getContactIDType(), (int) Double.parseDouble(currentTransaction.getAmount()), 1000, currentTransaction.getPhoneNumber(), currentTransaction.getProviderCharge(),
                currentTransaction.getFee(), currentTransaction.getPairRequestId());

//            setData(String alias, String sender_id, String sender_id_type, String recipient_id, Stringa recipient_id_type, int amount, int hold_timeout) {
        initiateRequest.setParserCallback(new TransactionInitiateRequest.OnTransactionInitiateResults() {
            @Override
            public void onTransactionInitiateResponse(JSONObject response) {
                Fog.d("TransactionInitiateRequest", "****" + response);
                try {
                    currentTransaction.setTid(response.getJSONObject("data").getJSONObject("data").getString("tid"));
                    GreenDaoHelper.getInstance(PairContactFoundActivity.this).updateTransaction(currentTransaction);
                    verifyIndTransaction();
                } catch(JSONException e) {
                    e.printStackTrace();
                }
                if(dialog.isShowing()) {
                    dialog.dismiss();
                }

            }

            @Override
            public void onTransactionInitiateError(VolleyError error) {
                //TODO: Check what to do if initiate failed
            }
        });
        initiateRequest.start();
    }

    private void cancelCurrentTransaction(String selection) {
        TransactionCancelRequest cancelRequest = new TransactionCancelRequest(PairContactFoundActivity.this);

        if(FunduUser.getCountryShortName().equalsIgnoreCase("KEN")) {
            cancelRequest.setData(currentTransaction.getTid(), currentTransaction.getPairRequestId(), selection, FunduUser.getContactId(), currentTransaction.getPhoneNumber(), FunduUser.getCountryShortName());
        } else {
            cancelRequest.setData(currentTransaction.getTid(), currentTransaction.getPairRequestId(), selection, FunduUser.getContactId(), currentTransaction.getPhoneNumber(), FunduUser.getCountryShortName());

        }
        cancelRequest.setParserCallback(PairContactFoundActivity.this);
        cancelRequest.start();
        dialog.setMessage("Cancelling transaction...");
        dialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    private float currentDegree = 0;
    private float currentNorthDegree = 0;

    @Override
    public void onSensorChanged(SensorEvent event) {
        double nDegree = event.values[0]; //Math.round(event.values[0]);
        double degree = event.values[0];
        // Fog.d("degree",""+degree+" ---- usernangle "+theta);
        if(degree > theta) {
            degree = 360 - degree + (float) theta;
        } else {
            degree = theta - degree;
        }

        RotateAnimation rn = new RotateAnimation(currentNorthDegree, (float) nDegree, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rn.setDuration(210);
        rn.setFillAfter(true);
        mCompass.setAnimation(rn);
        currentNorthDegree = (float) nDegree;
        //Fog.d("final rotation",""+degree);
        RotateAnimation ra = new RotateAnimation(currentDegree, (float) degree, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(210);
        ra.setFillAfter(true);
        needle.startAnimation(ra);
        currentDegree = (float) degree;
        //Fog.e(TAG,"Needle "+currentDegree+" to "+degree +" theta"+theta);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        return false;
    }

    @Override
    public void onTransactionCommitResponse(String response) {
        Fog.i(TAG, "onTransactionCommitResponse " + response);
        onTransactionSuccess(response);

    }

    @Override
    public void onTransactionCommitError(VolleyError error) {
        Fog.i(TAG, "onTransactionCommitError " + error);

    }


    @Override
    public void onTransactionConfirmResponse(JSONObject response) {
        Fog.i(TAG, "onTransactionConfirmResponse " + response);
        if(dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        FunduAnalytics.getInstance(this).sendAction("Transaction", "DebitSuccess", (int) Double.parseDouble(currentTransaction.getAmount()));
        onTransactionSuccess(response.toString());

    }

    @Override
    public void onTransactionConfirmError(VolleyError error) {
        try {
            if(dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            Fog.i(TAG, "onTransactionCommitError " + error);
            String errorN = null;
            if(null != error.networkResponse) {
                errorN = new String(error.networkResponse.data);
            }
            if(errorN != null) {
                JSONObject jError = new JSONObject(new JSONObject(errorN).getString("message"));
                String error1 = jError.getString("error");
                String errorCode = jError.getString("errorcode");

                if(error1.contains("Retry")) {
                    FunduAnalytics.getInstance(this).sendAction("Transaction", "DebitFailed", (int) Double.parseDouble(currentTransaction.getAmount()));
                    showRetryPopup(errorCode);
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void showRetryPopup(String code) {
        if(currentTransaction.getState() == Constants.TRANSACTION_STATE.PROVIDER_VERIFY_CODE.getCode()){
            return;
        }
        int message = R.string.transaction_failed_retry;
        int title = R.string.failed;
        int icon = R.drawable.ic_processing;
        if(code.equalsIgnoreCase("MT08")){
            message = R.string.upi_invalin_pin;
            title = R.string.invalid_pin;
            icon = R.drawable.ic_red_cross;
        }
        else if(code.equalsIgnoreCase("T")){
            message = R.string.upi_timeout;
            title = R.string.timedout;
        }
//        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
//
//        dialogBuilder.setMessage(message);
//        dialogBuilder.setNegativeButton(getString(R.string.cancel_C), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                cancelCurrentTransaction("DONT_WANT_TO_RETRY");
//
//            }
//        });
//        dialogBuilder.setPositiveButton(R.string.retry_C, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                retryCurrentTransaction();
//            }
//        });
//        dialogBuilder.setCancelable(false);
//        dialogBuilder.show();



        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.debit_transaction_cg, null);
        TextView tv = (TextView) dialogView.findViewById(R.id.title_tv);
        TextView desc = (TextView) dialogView.findViewById(R.id.desc);
        ImageView iconImage = (ImageView)dialogView.findViewById(R.id.icon);
        iconImage.setImageResource(icon);
        tv.setText(title);
        desc.setText(message);


        dialogBuilder.setView(dialogView);
        final Dialog retryDialog = dialogBuilder.show();
        dialogView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelCurrentTransaction("DONT_WANT_TO_RETRY");
                retryDialog.dismiss();
            }
        });
        dialogView.findViewById(R.id.retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retryCurrentTransaction();
                retryDialog.dismiss();
            }
        });


    }

    private void onUPIResult(boolean success, String orderID, String bankTransactionId) {
        TransactionCommitRequest request = new TransactionCommitRequest(this);
        request.setData(orderID, bankTransactionId);
        Fog.d("onUPIResult", "orderID" + orderID);
        request.setParserCallback(this);
        dialog.setMessage(getString(R.string.verifying));
        request.start();
    }

    private void onUPIResult(String orderID, JSONObject status) {

        transactionConfirmRequest = new TransactionConfirmRequest(this, this);
        transactionConfirmRequest.setData(orderID, currentTransaction.getPairRequestId(), status);
        Fog.d("onUPIResult", "orderID" + orderID);
        //transactionConfirmRequest.setParserCallback(this);
        if(dialog != null) {
            dialog.setMessage(getString(R.string.verifying));
            dialog.show();
        }
        transactionConfirmRequest.start();
    }


    @Override
    public void onFragmentInteraction(Bundle bundle) {

    }

    @Override
    public void onAzimuthChanged(float azimuthFrom, float azimuthTo) {
        //Toast.makeText(this," "+azimuthTo, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location currentLocation) {
        mMyLatitude = currentLocation.getLatitude();
        mMyLongitude = currentLocation.getLongitude();
        getDistance(mMyLatitude, mMyLongitude, currentTransaction.getLatitude(), currentTransaction.getLongitude());

    }

    public class GetContactsTask extends AsyncTask<Object, Object, Object> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected Object doInBackground(Object... params) {


            //contactItems = UserContactsTable.getContacts(PairContactFoundActivity.this);/*getUnRegisteredContacts*/
            boolean exist = ContactsUtils.getInstance(PairContactFoundActivity.this).contactExists(currentTransaction.getPhoneNumber());
            return exist;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if(dialog.isShowing()) {
                dialog.hide();
            }
            boolean hasNumber = (Boolean) o;
            callPairedUser(hasNumber);
//            boolean hasNumber = false;
//            if(contactItems != null){
//                for(int i=0;i<contactItems.size();i++){
//                    if(contactItems.get(i).getContactNumber().equalsIgnoreCase(currentTransaction.getPhoneNumber())){
//                        hasNumber = true;
//
//                        Fog.d("hasNumber",""+hasNumber);
//                        break;
//
//                    }
//                }
//                callPairedUser(hasNumber);
//
//            }


        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(myCurrentLocation != null)
            myCurrentLocation.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(myCurrentLocation != null)
            myCurrentLocation.stop();

        EventBus.getDefault().unregister(this);
    }

    private double distanceBetween(double lat1, double lat2, double lon1,
                                   double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) +
                Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }

    private double distanceBetween1(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = dist * 180.0 / Math.PI;
        dist = dist * 60 * 1.1515 * 1000;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }


    public String getDistance(final double lat1, final double lon1, final double lat2, final double lon2) {


        final String parsedDistance = "";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://maps.googleapis.com/maps/api/directions/json?origin=" + lat1 + "," + lon1 + "&destination=" + lat2 + "," + lon2 + "&ensor=false&units=metric&mode=walk");

                    final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    String response = org.apache.commons.io.IOUtils.toString(in, "UTF-8");
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray array = jsonObject.getJSONArray("routes");
                    JSONObject routes = array.getJSONObject(0);
                    JSONArray legs = routes.getJSONArray("legs");
                    JSONObject steps = legs.getJSONObject(0);
                    final JSONObject distances = steps.getJSONObject("distance");
                    JSONObject duration = steps.getJSONObject("duration");
                    final String parsedDuration = duration.getString("text");
                    final String parsedDistance = distances.getString("text");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            distance.setText(parsedDistance + " away.");

                            /*Toast.makeText(PairContactFoundActivity.this, "Distance: "+parsedDistance+
                                    "Time: "+parsedDuration, Toast.LENGTH_SHORT).show();*/
                        }
                    });


                    Fog.d("parsedDuration", "" + parsedDuration);
                    Fog.d("parsedDuration", "parsedDistance" + parsedDistance);

                } catch(ProtocolException e) {
                    e.printStackTrace();
                } catch(MalformedURLException e) {
                    e.printStackTrace();
                } catch(IOException e) {
                    e.printStackTrace();
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        return parsedDistance;
    }

    private double bearing(Location startPoint, Location endPoint) {
        double longitude1 = startPoint.getLongitude();
        double latitude1 = Math.toRadians(startPoint.getLatitude());

        double longitude2 = endPoint.getLongitude();
        double latitude2 = Math.toRadians(endPoint.getLatitude());

        double longDiff = Math.toRadians(longitude2 - longitude1);

        double y = Math.sin(longDiff) * Math.cos(latitude2);
        double x = Math.cos(latitude1) * Math.sin(latitude2) - Math.sin(latitude1) * Math.cos(latitude2) * Math.cos(longDiff);

        return Math.toDegrees(Math.atan2(y, x));
    }


    /**
     * ICICI Bank UPI Transaction
     */

//    @Override
//    public void paymentCanceledByCustomer(){
//        Fog.i(TAG,"paymentCanceledByCustomer");
//
//    }
//
//
//
//    @Override
//    public void paymentFailed(int errorCode){
//        if(errorCode == ISDKConstants.ISDKPAYMENT_INITERROR){
//            ISDK.initSDK(this, "661086f1261a811c1e4bff96e4f31c03", "MID001", "1001", null);
//            return;
//        }
//        try{
//            JSONObject status = new JSONObject();
//            status.put("status", UPIConstant.getErrorCode(errorCode));
//            status.put("responsecode",""+errorCode);
//            status.put("orderNo",currentTransaction.getTid());
//            onUPIResult(currentTransaction.getTid(),status);
//
//        }catch (JSONException e){
//            e.printStackTrace();
//        }
//        Fog.i(TAG,"paymentFailed");
//
//    }
//
//
//    @Override
//    public void paymentSuccessful(String orderID,String
//            transactionDateTime,String transactionNo,String paymentType) {
//
//        Fog.i(TAG, "paymentSuccessful orderID:" + orderID + " transactionNo:" + transactionNo +
//                " transactionDateTime:" + transactionDateTime + " paymentType:" + paymentType);
//        //onUPIResult(true, transactionId, transactionNo);
//        try{
//            JSONObject status = new JSONObject();
//            status.put("pgMeTrnRefNo",transactionNo);
//            status.put("status","S");
//            status.put("orderNo",orderID);
    //         status.put("tranAuthdate",transactionDateTime);
    //         status.put("additional","paymentType|"+paymentType);
//            onUPIResult(transactionId,status);
//        }
//        catch (JSONException e){
//            e.printStackTrace();
//        }
//    }

    /**
     * Yes bank UPI Transaction
     */
    private final int TRANSACTION_REQUEST_CODE = 2;

    public void startTransaction() {

        Bundle bundle = new Bundle();
        bundle.putString("mid", mid);
        bundle.putString("merchantKey", merchantKey);
        bundle.putString("merchantTxnID", String.valueOf(currentTransaction.getTid()));
        bundle.putString("transactionDesc", "Test Payment");
        bundle.putString("currency", "INR");
        bundle.putString("appName", appName);
        bundle.putString("paymentType", "P2M");
        bundle.putString("transactionType", "PAY");
        bundle.putString("payeePayAddress", ekoVPA);
        bundle.putString("payeeAccntNo", "");
        bundle.putString("payeeIFSC", "");
        bundle.putString("payeeAadhaarNo", "");
        bundle.putString("payeeMobileNo", "");
        bundle.putString("merchantCatCode", "7399");
        bundle.putString("expiryTime", "");
        bundle.putString("payerAccntNo", "");
        bundle.putString("payerIFSC", "");
        bundle.putString("payerAadhaarNo", "");
        bundle.putString("payerMobileNo", "");
        bundle.putString("payerPaymentAddress", "");
        bundle.putString("subMerchantID", "");
        bundle.putString("whitelistedAccnts", "");
        bundle.putString("payerMMID", "");
        bundle.putString("payeeMMID", "");
        bundle.putString("payeeName", "Eko India");
        bundle.putString("refurl", "");
        String upiAmount = String.format("%1.2f", Double.parseDouble(currentTransaction.getAmount()) + Double.parseDouble(currentTransaction.getFee()));
        bundle.putString("amount", upiAmount);
        bundle.putString("add1", "NA");
        bundle.putString("add2", "NA");
        bundle.putString("add3", "NA");
        bundle.putString("add4", "NA");
        bundle.putString("add5", "NA");
        bundle.putString("add6", "NA");
        bundle.putString("add7", "NA");
        bundle.putString("add8", "NA");
        bundle.putString("add9", "NA");
        bundle.putString("add10", "NA");
        /**
         * Yes Bank UPI Payment Activity
         */

        Toast.makeText(this, R.string.user_registeration_add, Toast.LENGTH_SHORT).show();

//        Intent intent = new Intent(getApplicationContext(), TransactionActivity.class);
//        intent.putExtras(bundle);
//        startActivityForResult(intent, TRANSACTION_REQUEST_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == TRANSACTION_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            Bundle bundle = data.getExtras();
            String statusCode = bundle.getString("status");
            String statusDesc = bundle.getString("statusDesc");
            //Toast.makeText(this,statusDesc,Toast.LENGTH_SHORT).show();
            String pgMeTrnRefNo = bundle.getString("pgMeTrnRefNo");
            String orderNo = bundle.getString("orderNo");
            String txnAmount = bundle.getString("txnAmount");
            String tranAuthdate = bundle.getString("tranAuthdate");
            String responsecode = bundle.getString("responsecode");
            String approvalCode = bundle.getString("approvalCode");
            String payerVA = bundle.getString("payerVA");
            String npciTxnId = bundle.getString("npciTxnId");
            String refId = bundle.getString("refId");
            String payerAccountNo = bundle.getString("payerAccountNo");
            String payerIfsc = bundle.getString("payerIfsc");
            String payerAccName = bundle.getString("payerAccName");
            String add1 = bundle.getString("add1");
            String add2 = bundle.getString("add2");
            String add3 = bundle.getString("add3");
            String add4 = bundle.getString("add4");
            String add5 = bundle.getString("add5");
            String add6 = bundle.getString("add6");
            String add7 = bundle.getString("add7");
            String add8 = bundle.getString("add8");
            String add9 = bundle.getString("add9");
            String add10 = bundle.getString("add10");
            try {
                JSONObject status = new JSONObject();
                status.put("pgMeTrnRefNo", pgMeTrnRefNo);
                status.put("status", statusCode);
                status.put("payerVA", payerVA);
                status.put("statusDesc", statusDesc);
                status.put("txnAmount", txnAmount);
                status.put("npciTxnId", npciTxnId);
                status.put("payerAccountNo", payerAccountNo);
                status.put("tranAuthdate", tranAuthdate);
                status.put("additional", add1 + "|" + add2 + "|" + add3 + "|" + add4 + "|" + add5 + "|" + add6 + "|" + add7 + "|" + add8 + "|" + add9 + "|" + add10);
                status.put("refId", refId);
                status.put("payerIfsc", payerIfsc);
                status.put("payerAccName", payerAccName);
                status.put("approvalCode", approvalCode);
                status.put("orderNo", orderNo == null ? currentTransaction.getTid() : orderNo);
                status.put("responsecode", responsecode);
                onUPIResult(currentTransaction.getTid(), status);
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
    }

    String dummyStatus = "S";

    private void sendYesBankDummy() {
        try {
            JSONObject object = new JSONObject();
            object.put("pgMeTrnRefNo", "111111");
            object.put("orderNo", currentTransaction.getTid());
            object.put("status", dummyStatus);

            object.put("payerVA", FunduUser.getVpa());
            object.put("payerIfsc", "YESB0000542");
            object.put("address", "15379");
            object.put("statusDesc", "Your transaction was successful");
            object.put("txnAmount", "1.0");
            object.put("npciTxnId", "111111111111111");
            object.put("payerAccountNo", "054290100001631");
            object.put("tranAuthdate", new Date().toString());
            object.put("address", "dummyaddress");
            onUPIResult(currentTransaction.getTid(), object);

        } catch(JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * EventBus handling
     */

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTransactionInitiated(SeekerTransactionEvent event) {

        if(event.getPushType() == Constants.PUSH_TYPE_ENUM.NEEDCASH_TRANSACTION_COMPLETED.getCode()) {
            showRatingView();
        }
    }


}