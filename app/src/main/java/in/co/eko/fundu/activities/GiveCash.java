package in.co.eko.fundu.activities;


import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import in.co.eko.fundu.FunduApplication;
import in.co.eko.fundu.R;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.database.greendao.FunduTransaction;
import in.co.eko.fundu.database.tables.UserContactsTable;
import in.co.eko.fundu.event.LocationEvent;
import in.co.eko.fundu.event.ProviderTransactionEvent;
import in.co.eko.fundu.event.SeekerTransactionEvent;
import in.co.eko.fundu.gcm.FunduNotificationManager;
import in.co.eko.fundu.models.ContactItem;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.models.User;
import in.co.eko.fundu.requests.CallMaskingRequest;
import in.co.eko.fundu.requests.GetTransactionStatus;
import in.co.eko.fundu.requests.GoogleDirectionsRequest;
import in.co.eko.fundu.requests.TransactionCancelRequest;
import in.co.eko.fundu.requests.UpdateRatingRequest;
import in.co.eko.fundu.requests.VerifyTransactionCodeRequest;
import in.co.eko.fundu.services.ProviderLocationService;
import in.co.eko.fundu.utils.DateUtils;
import in.co.eko.fundu.utils.Fog;
import in.co.eko.fundu.utils.FunduAnalytics;
import in.co.eko.fundu.utils.GoogleMapUtils;
import in.co.eko.fundu.utils.GreenDaoHelper;
import in.co.eko.fundu.utils.Utils;

import static in.co.eko.fundu.R.id.mobile;
import static in.co.eko.fundu.R.id.profilepic;
import static in.co.eko.fundu.R.raw.google_map;


public class GiveCash extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener, GoogleApiClient.ConnectionCallbacks,
        ResultCallback<LocationSettingsResult>,UpdateRatingRequest.OnUpdateRatingResults,
        GoogleMap.OnMapClickListener, View.OnClickListener,/*OnLocationChangedListener,*/
        TransactionCancelRequest.OnTransactionCancelResults, View.OnKeyListener {
    private String TAG = GiveCash.class.getName();
    private SupportMapFragment mapFragment;
    private GoogleMap googleMap;
    private ArrayList<ContactItem> contactItems;
    private AlertDialog.Builder dialog;
    private TextView userName, title, userRating,cancelTransaction,transaction_incentive;
    private RatingBar ratingBar;
    boolean isFirstTime;
    private LatLng startLatLng;
    private IntentFilter mIntentFilter;
    public static final String mBroadcastStringAction = "RECEIVER_REQUEST";
    private VerifyTransactionCodeRequest transactionCodeRequest;
    private ProgressDialog progressDialog;
    ArrayList markerPoints= new ArrayList();
    private LinearLayout transactionCode,progressbar,progressbarKenya;
    private UpdateRatingRequest ratingRequest;
    private GetTransactionStatus statusRequest;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private ImageView userImage,callIcon,dot2,dot3,dot1,kenyadot1,kenyadot2;
    double latitude,longitutde;
    private CallMaskingRequest callMaskingRequest;
    private String seekerPhn;
    private FunduTransaction currentTransaction;
    EditText etTranxCode1,etTranxCode2,etTranxCode3;
    private GoogleDirectionsRequest request;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give_cash);
        EventBus.getDefault().register(this);
        progressDialog = new ProgressDialog(GiveCash.this);
        progressDialog.setMessage("updating...");
        progressDialog.setCancelable(false);
        latitude   = FunduUser.getUser().getLatitude();
        longitutde = FunduUser.getUser().getLongitude();
        Fog.d("alerts",""+getIntent().getStringArrayListExtra(Constants.ALERT));
        extractDetails();
        if(currentTransaction == null){
            finish();
            return;
        }
        init();
        showInfoBasedOnState();

    }
    private void showInfoBasedOnState(){
        if(currentTransaction.getState() == Constants.TRANSACTION_STATE.PROVIDER_ACCEPTED.getCode()){
            displayInfo();
        }
        else if(currentTransaction.getState() == Constants.TRANSACTION_STATE.PROVIDER_VERIFY_CODE.getCode()){
            updateVerifyCodeUI();
        }
        else if(currentTransaction.getState() == Constants.TRANSACTION_STATE.RATING_PENDING.getCode()){
            showRatingView();
        }
    }
    private void showRating(Double rating) {
        if(rating > 0) {
            String rating1 = String.format("%.1f", rating);
            userRating.setText(rating1);
        } else {
            userRating.setText(R.string.new_user);
        }
    }
    private void displayInfo(){
        String profilepic = currentTransaction.getImage();
        try {
            if(!TextUtils.isEmpty(profilepic)&&!profilepic.equalsIgnoreCase("null")) {
                Picasso.with(this).load(profilepic).fit().into(userImage);
            }
            userName.setText(currentTransaction.getName());
            double a = Double.valueOf(currentTransaction.getRating());
            showRating(a);
            title.setText(getString(R.string.meet) + " " + currentTransaction.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(currentTransaction.getPhoneNumber () != null)
            startService(new Intent(this, ProviderLocationService.class).putExtra(Constants.PROVIDER_NUMBER,currentTransaction.getPhoneNumber()));

    }


    private void extractDetails() {

        long fTid = getIntent().getLongExtra(Constants.FUNDU_TRANSACTION_ID,-1);
        if(fTid == -1){
            throw new NullPointerException();
        }
        currentTransaction = GreenDaoHelper.getInstance(this).getTransaction(fTid);
    }
    private void init() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        dialog = new AlertDialog.Builder(this);
        userName = (TextView) findViewById(R.id.user_name);
        userImage = (ImageView) findViewById(R.id.user_image);
        userRating = (TextView) findViewById(R.id.user_rating);
        cancelTransaction = (TextView) findViewById(R.id.cancelTransaction);
        transaction_incentive = (TextView) findViewById(R.id.transaction_incentive);
        transactionCode = (LinearLayout) findViewById(R.id.transactionCode);
        progressbar = (LinearLayout) findViewById(R.id.progress_bar_3);
        progressbarKenya = (LinearLayout) findViewById(R.id.progress_bar_2);
        dot2 = (ImageView) findViewById(R.id.dot2_3);
        dot3 = (ImageView) findViewById(R.id.dot3_3);
        dot1 = (ImageView) findViewById(R.id.dot1_3);
        kenyadot1 = (ImageView) findViewById(R.id.dot1_2);
        kenyadot2 = (ImageView) findViewById(R.id.dot2_2);
        title = (TextView) findViewById(R.id.title);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        callIcon = (ImageView)findViewById(R.id.call);
        cancelTransaction.setOnClickListener(this);

        callIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FunduAnalytics.getInstance(GiveCash.this).sendAction("PairedContact", "Call");
                if(FunduUser.getCountryShortName().equalsIgnoreCase("KEN")){
                    String number = Utils.appendCountryCodeToNumber(getApplicationContext(),currentTransaction.getPhoneNumber());
                    Uri call = Uri.parse("tel:" + number);
                    Intent surf = new Intent(Intent.ACTION_DIAL, call);
                    startActivity(surf);
                }
                else{
                    callPairedUser(true);
                    //new GetContactsTask().execute();
                }
            }
        });
        if(FunduUser.getCountryShortName().equalsIgnoreCase("KEN")){
            cancelTransaction.setVisibility(View.GONE);
            progressbar.setVisibility(View.GONE);
            progressbarKenya.setVisibility(View.VISIBLE);
            kenyadot1.setImageResource(R.drawable.meet_circle_complete);
            kenyadot1 = (ImageView) Utils.makeMeBlink(kenyadot1,500,10);
        }
        else{
            cancelTransaction.setVisibility(View.VISIBLE);
            progressbar.setVisibility(View.VISIBLE);
            progressbarKenya.setVisibility(View.GONE);
            dot1.setImageResource(R.drawable.meet_circle_complete);
            dot1 = (ImageView) Utils.makeMeBlink(dot1,500,10);
        }
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popUpImageviewDialog();
            }
        });
        findViewById(R.id.feedback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentTransaction.getState() == Constants.TRANSACTION_STATE.PROVIDER_ACCEPTED.getCode()){
                    GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {

                        @Override
                        public void onSnapshotReady(Bitmap snapshot) {
                            View v = getWindow().getDecorView().getRootView();
                            v.setDrawingCacheEnabled(true);
                            Bitmap bmp = v.getDrawingCache().copy(Bitmap.Config.ARGB_8888, true);
                            v.setDrawingCacheEnabled(false);
                            Bitmap bmOverlay = Bitmap.createBitmap(
                                    bmp.getWidth(), bmp.getHeight(),
                                    bmp.getConfig());

                            Canvas canvas = new Canvas(bmOverlay);
                            canvas.drawBitmap(bmp, 0, 0, null);
                            View view1 = findViewById(R.id.infomationrl);
                            int[] location = new int[2];
                            view1.getLocationOnScreen(location);
                            RectF rectF = new RectF(location[0],location[1],location[0]+view1.getWidth(),view1.getHeight()+location[1]);
                            //RectF rectF = new RectF(view1.getX(), view1.getHeight()+view1.getY(), view1.getWidth(),cancelTransaction.getY()-view1.getHeight()-view1.getY());
                            canvas.drawBitmap(snapshot,null,rectF,null);
                            Utils.takeFeedback(bmOverlay,GiveCash.this);

                        }
                    };
                    googleMap.snapshot(callback);
                    return;
                }
                View v = getWindow().getDecorView().getRootView();
                v.setDrawingCacheEnabled(true);
                Bitmap bmp = Bitmap.createBitmap(v.getDrawingCache());
                v.setDrawingCacheEnabled(false);
                Utils.takeFeedback(bmp,GiveCash.this);
            }
        });

    }


    private void callPairedUser(boolean hasNumber){

        Fog.e(TAG,"callPairedUser TransactionId : "+currentTransaction.getTid());

        if(currentTransaction.getTid() == null || currentTransaction.getTid().length() == 0){
            return;
        }

        String number = Utils.appendCountryCodeToNumber(getApplicationContext(),currentTransaction.getPhoneNumber ());
        if(hasNumber||FunduUser.getCountryShortName().equalsIgnoreCase("KEN")){
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE},
                        10);
                return;
            }
            Uri call = Uri.parse("tel:" + number);
            Intent surf = new Intent(Intent.ACTION_DIAL, call);
            startActivity(surf);
        }
        else{
            dialog.setTitle("Initiating call");

            final AlertDialog alertDialog = dialog.show();
            FunduAnalytics.getInstance(GiveCash.this).sendAction("PairedContact","Call");
            callMaskingRequest = new CallMaskingRequest(this);
            callMaskingRequest.setData(currentTransaction.getTid());
            callMaskingRequest.setParserCallback(new CallMaskingRequest.OnCallMaskingRequestResult() {
                @Override
                public void onCallMaskingResponse(JSONObject object) {
                    alertDialog.dismiss();
                    Toast.makeText(GiveCash.this,object.optString("message"),Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCallMaskingError(VolleyError error) {
                    alertDialog.dismiss();
                }
            });
            callMaskingRequest.start();
        }



    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
    Marker mMarker;
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;

        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.setOnMapClickListener(this);
        boolean success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, google_map));

        this.googleMap.setOnMarkerClickListener(this);

        googleMap.addMarker(new MarkerOptions().
                icon(BitmapDescriptorFactory.fromResource(R.drawable.my_location)).
                position(new LatLng(latitude, longitutde)).
                title("You"));
        mMarker =  googleMap.addMarker(new MarkerOptions().
                icon(BitmapDescriptorFactory.fromResource(R.drawable.usermap)).
                position(new LatLng(currentTransaction.getLatitude(),currentTransaction.getLongitude())).
                title("Reciver"));

        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitutde), 150);
                googleMap.moveCamera(cu);
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
            }
        });




        request = new GoogleDirectionsRequest(makeURL(String.valueOf(currentTransaction.getLatitude()),
                String.valueOf(currentTransaction.getLongitude())), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                final JSONObject json;
                try {
                    json = new JSONObject(response);
                    JSONArray routeArray = json.getJSONArray("routes");
                    JSONObject routes = routeArray.getJSONObject(0);
                    JSONObject legs = routes.getJSONObject("distance");
                    String distance = legs.getString("text");
                    Fog.d("legs",""+distance);
                    GoogleMapUtils.drawPath(GiveCash.this, googleMap, response);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        FunduApplication.getInstance().addToRequestQueue(request);
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {


        return true;
    }



    public void onClickInfomationButton(View view){

    }
    public void onClickHelpButton(View view){

    }
    public void showRatingView(){

        try
        {
            currentTransaction.setState(Constants.TRANSACTION_STATE.RATING_PENDING.getCode());
            int amount= (int)Double.parseDouble(currentTransaction.getAmount());
            Fog.d("amount",""+amount);
            final String tid=currentTransaction.getTid();
            TextView transactionDesc = (TextView)findViewById(R.id.transaction_desc);
            TextView amountTv = (TextView)findViewById(R.id.amountValue);
            findViewById(R.id.trancation_success).setVisibility(View.VISIBLE);
            findViewById(R.id.map).setVisibility(View.GONE);
            findViewById(R.id.cancelTransaction).setVisibility(View.GONE);
            findViewById(R.id.transactionCode).setVisibility(View.GONE);
            findViewById(R.id.transaction_incentive).setVisibility(View.VISIBLE);
            findViewById(R.id.ratingBar).setVisibility(View.VISIBLE);
            if(FunduUser.getCountryShortName().equalsIgnoreCase("KEN")){
                kenyadot1.setImageResource(R.drawable.meet_circle_complete);
                kenyadot2.setImageResource(R.drawable.meet_circle_complete);
                kenyadot1.clearAnimation();
                kenyadot2 = (ImageView)Utils.makeMeBlink(kenyadot2,500,10);

            }
            else{
                dot1.setImageResource(R.drawable.meet_circle_complete);
                dot2.setImageResource(R.drawable.meet_circle_complete);
                dot3.setImageResource(R.drawable.meet_circle_complete);
                dot2.clearAnimation();
                dot3 = (ImageView)Utils.makeMeBlink(dot3,500,10);

            }

            title.setText(getString(R.string.rate)+" "+currentTransaction.getName());
            double a = Double.valueOf(currentTransaction.getRating());
            showRating(a);
            userName.setText(currentTransaction.getName());
            seekerPhn = currentTransaction.getPhoneNumber();
            int i = amount+Integer.parseInt(currentTransaction.getProviderCharge());
            if(Utils.getCurrency(this).equalsIgnoreCase(getString(R.string.ruppee_symbol))){
                amountTv.setText(getResources().getString(R.string.rs_symbol)+" "+amount);
                transaction_incentive.setText("You have earned "+getString(R.string.ruppee_symbol)+currentTransaction.getProviderCharge()+".");
                FunduNotificationManager.createElectronicCreditNotification(String.valueOf(i));
            }
            else{
                amountTv.setText(getResources().getString(R.string.ksh_symbol)+" "+amount);
                transaction_incentive.setText("You have earned "+getString(R.string.ksh_symbol)+" "+currentTransaction.getProviderCharge()+".");
            }
            String imageURL = currentTransaction.getImage();
            if(!imageURL.isEmpty()&&!imageURL.equalsIgnoreCase("null"))
                Picasso.with(this).load(imageURL).into(userImage);

            transactionDesc.setText(getString(R.string.amount_creditted));
            ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    updateRating(tid);
                }
            });
        }catch(Exception e){
            e.getMessage();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (FunduUser.isUserMobileVerified())
            onIntent(intent);
    }

    private void onIntent(final Intent intent) {


        Constants.TransactionStatus status = ( Constants.TransactionStatus)intent.getSerializableExtra(Constants.TRANSACTION_STATUS);
        if(intent.getIntExtra(Constants.PUSH_TYPE,-1)== Constants.PUSH_TYPE_ENUM.VERIFY_TRANSACTION_CODE.getCode()){
            updateVerifyCodeUI();
        }
    }
    private void updateVerifyCodeUI() {

        etTranxCode1 = (EditText) findViewById(R.id.etTranxCode1);
        etTranxCode2 = (EditText) findViewById(R.id.etTranxCode2);
        etTranxCode3 = (EditText) findViewById(R.id.etTranxCode3);
        dot1.setImageResource(R.drawable.meet_circle_complete);
        dot2.setImageResource(R.drawable.meet_circle_complete);
        dot1.clearAnimation();
        dot2 = (ImageView)Utils.makeMeBlink(dot2,500,10);
        final TextView givecashTranxInfo = (TextView) findViewById(R.id.givecash_tranxInfo);

        etTranxCode1.addTextChangedListener(new MyTextWatcher(null,etTranxCode1,etTranxCode2));
        etTranxCode2.addTextChangedListener(new MyTextWatcher(etTranxCode1,etTranxCode2,etTranxCode3));
        etTranxCode3.addTextChangedListener(new MyTextWatcher(etTranxCode2,etTranxCode3,null));

        givecashTranxInfo.setText("Please give cash to "+currentTransaction.getName()+" and get the code to enter below. Your money is safe with us and we will send money to your account once the code is verified.");
        findViewById(R.id.transactionCode).setVisibility(View.VISIBLE);
        findViewById(R.id.trancation_success).setVisibility(View.GONE);
        findViewById(R.id.infomationrl).setVisibility(View.GONE);
        findViewById(R.id.map).setVisibility(View.GONE);
        title.setText("Meet "+currentTransaction.getName());
        double a = Double.valueOf(currentTransaction.getRating());
        showRating(a);
        userName.setText(currentTransaction.getName());
        seekerPhn = currentTransaction.getPhoneNumber();
        if(seekerPhn==null){
            seekerPhn = currentTransaction.getPhoneNumber();
        }
        findViewById(R.id.btnConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(etTranxCode1.getText().toString())
                        ||TextUtils.isEmpty(etTranxCode2.getText().toString())||
                        TextUtils.isEmpty(etTranxCode3.getText().toString())){

                    Toast.makeText(GiveCash.this, "Please enter complete code.", Toast.LENGTH_SHORT).show();
                    return;
                }
                else{

                    String code = etTranxCode1.getText().toString().
                            concat(etTranxCode2.getText().toString()).concat(etTranxCode3.getText().toString());

                    if(Utils.isNetworkAvailable(GiveCash.this)){
                        progressDialog.show();
                        progressDialog.setMessage("Confirming....");
                        LatLng location= new LatLng(latitude, longitutde);
                        transactionCodeRequest = new VerifyTransactionCodeRequest(GiveCash.this);
                        transactionCodeRequest.setData(currentTransaction.getTid(),currentTransaction.getPairRequestId(),code, location);
                        transactionCodeRequest.setParserCallback(new VerifyTransactionCodeRequest.OnVerifyTransactionCodeResults() {
                            @Override
                            public void onVerifyTransactionCodeResponse(String object) {
                                progressDialog.dismiss();
                                FunduAnalytics.getInstance(GiveCash.this).sendAction("Transaction","CreditSuccess",(int)Double.parseDouble(currentTransaction.getAmount()));
                                showRatingView();
                                // Toast.makeText(GiveCash.this, ""+object, Toast.LENGTH_SHORT);
                            }

                            @Override
                            public void onVerifyTransactionCodeError(VolleyError error) {
                                progressDialog.dismiss();

                                try{
                                    String errorN =new String( error.networkResponse.data);
                                    if(errorN.contains("Credit Failed")){
                                        FunduAnalytics.getInstance(GiveCash.this).sendAction("Transaction","CreditFailed",(int)Double.parseDouble(currentTransaction.getAmount()));
                                        if(currentTransaction.getTid()==null || currentTransaction.getTid().length()==0) {
                                            Fog.logEvent(false, currentTransaction.getPairRequestId(), "GiveCash","updateVerifyCodeUI", "credit_failed", DateUtils.getCurrentUTCtime(), Constants.getState(currentTransaction.getState()));
                                        } else {
                                            Fog.logEvent(true, currentTransaction.getTid(), "GiveCash","updateVerifyCodeUI", "credit_failed", DateUtils.getCurrentUTCtime(), Constants.getState(currentTransaction.getState()));
                                        }
                                        Intent intent = new Intent(GiveCash.this,TransactionStatusActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        intent.setAction("credit_failed");
                                        intent.putExtra("orderNo",currentTransaction.getTid());
                                        GreenDaoHelper.getInstance(GiveCash.this).deleteFunduTransaction(currentTransaction.getId());
                                        startActivity(intent);
                                        finish();
                                        return;
                                    }
                                    else if(errorN.contains("Credit Timeout")){
                                        if(currentTransaction.getTid()==null || currentTransaction.getTid().length()==0) {
                                            Fog.logEvent(false, currentTransaction.getPairRequestId(), "GiveCash","updateVerifyCodeUI", "credit_timeout", DateUtils.getCurrentUTCtime(), Constants.getState(currentTransaction.getState()));
                                        } else {
                                            Fog.logEvent(true, currentTransaction.getTid(), "GiveCash","updateVerifyCodeUI", "credit_timeout", DateUtils.getCurrentUTCtime(), Constants.getState(currentTransaction.getState()));
                                        }
                                        Intent intent = new Intent(GiveCash.this,TransactionStatusActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        intent.setAction("credit_timeout");
                                        intent.putExtra("orderNo",currentTransaction.getTid());
                                        GreenDaoHelper.getInstance(GiveCash.this).deleteFunduTransaction(currentTransaction.getId());
                                        startActivity(intent);
                                        finish();
                                        return;
                                    }
                                    else if(errorN.contains ( "limit exhausted" )){
                                        if(currentTransaction.getTid()==null || currentTransaction.getTid().length()==0) {
                                            Fog.logEvent(false, currentTransaction.getPairRequestId(), "GiveCash","updateVerifyCodeUI", "limit_exhausted", DateUtils.getCurrentUTCtime(), Constants.getState(currentTransaction.getState()));
                                        } else {
                                            Fog.logEvent(true, currentTransaction.getTid(), "GiveCash","updateVerifyCodeUI", "limit_exhausted", DateUtils.getCurrentUTCtime(), Constants.getState(currentTransaction.getState()));
                                        }

                                        GreenDaoHelper.getInstance(GiveCash.this).deleteFunduTransaction(currentTransaction.getId());
                                        Intent intent = new Intent(GiveCash.this,TransactionStatusActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        intent.setAction("code_limit_exhausted");
                                        startActivity(intent);
                                        finish();
                                        return;
                                    }
                                    else{
                                        onInvalidCode();
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                            }
                        });
                        transactionCodeRequest.start();
                    }
                    else{
                        Toast.makeText(GiveCash.this, "Please try again.", Toast.LENGTH_SHORT).show();
                    }


                }
            }
        });

    }

    private void onInvalidCode(){
        etTranxCode1.setText("");
        etTranxCode2.setText("");
        etTranxCode3.setText("");
        findViewById(R.id.invalidCode).setVisibility(View.VISIBLE);
        etTranxCode1.getBackground().
                setColorFilter(ContextCompat.getColor(GiveCash.this, R.color.warm_pink), PorterDuff.Mode.SRC_ATOP);
        etTranxCode2.getBackground().
                setColorFilter(ContextCompat.getColor(GiveCash.this, R.color.warm_pink), PorterDuff.Mode.SRC_ATOP);
        etTranxCode3.getBackground().
                setColorFilter(ContextCompat.getColor(GiveCash.this, R.color.warm_pink), PorterDuff.Mode.SRC_ATOP);
        etTranxCode1.requestFocus();
        new CountDownTimer(1000,500) { // adjust the milli seconds here


            @Override
            public void onTick(long millisUntilFinished) {

            }
            public void onFinish() {

                findViewById(R.id.invalidCode).setVisibility(View.GONE);
                etTranxCode1.getBackground().
                        setColorFilter(ContextCompat.getColor(GiveCash.this, R.color.Black), PorterDuff.Mode.SRC_ATOP);
                etTranxCode2.getBackground().
                        setColorFilter(ContextCompat.getColor(GiveCash.this, R.color.Black), PorterDuff.Mode.SRC_ATOP);
                etTranxCode3.getBackground().
                        setColorFilter(ContextCompat.getColor(GiveCash.this, R.color.Black), PorterDuff.Mode.SRC_ATOP);
                etTranxCode1.requestFocus();

            }
        }.start();
    }


    private void updateRating(final String tid){

        final String id = currentTransaction.getTid();
        Fog.d("tid","tid"+tid);
        User user = FunduUser.getUser();
        if (user != null) {
            ratingRequest = new UpdateRatingRequest(this);
            if (ratingBar.getRating() < 1) {
                Utils.showShortToast(this, "Kindly fill atleast 1 rating");
                return;
            }
            JSONObject object = null;
            try {
                object = new JSONObject();
                object.put("rated_by_id", FunduUser.getContactIDType() + ":" + FunduUser.getContactId());
                object.put("transaction_id", tid);
                object.put("rating", ratingBar.getRating());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String mobile = currentTransaction.getPhoneNumber();
            if (FunduUser.getCountryShortName().equalsIgnoreCase("KEN")) {

                mobile = mobile.replace("+254","");
                //ratingRequest.setData(object, pMobile/*list.get(1)*/);
                ratingRequest.setData(object,mobile /*list.get(1)*/);
            } else {
                mobile = mobile.replace("+91","");
                //ratingRequest.setData(object, pMobile/*list.get(1)*/);
                ratingRequest.setData(object, mobile/*list.get(1)*/);
            }
            if (progressDialog != null) {
                progressDialog.show();
            }

            ratingRequest.setParserCallback(new UpdateRatingRequest.OnUpdateRatingResults() {
                @Override
                public void onUpdateRatingResponse(JSONObject response) {
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    // String id = FunduTransaction.getTid();
                    if(currentTransaction.getTid()==null || currentTransaction.getTid().length()==0) {
                        Fog.logEvent(false, currentTransaction.getPairRequestId(), "GiveCash","updateRating", "transaction_completed", DateUtils.getCurrentUTCtime(), Constants.getState(currentTransaction.getState()));
                    } else {
                        Fog.logEvent(true, currentTransaction.getTid(), "GiveCash","updateRating", "transaction_completed", DateUtils.getCurrentUTCtime(), Constants.getState(currentTransaction.getState()));
                    }
                    GreenDaoHelper.getInstance(GiveCash.this).deleteFunduTransaction(currentTransaction.getId());
                    Toast.makeText(GiveCash.this, "Sucessfully updated.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(GiveCash.this, HomeActivity.class);
                    // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onUpdateRatingError(VolleyError error) {
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    Toast.makeText(GiveCash.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
            ratingRequest.start();

        }

    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (googleMap == null) {
            mapFragment.getMapAsync(this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();

    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API).build();

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(this);
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        int UPDATE_INTERVAL = 1000 * 60 * 5;
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        int FASTEST_INTERVAL = 5000;
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        int DISPLACEMENT = 20;
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {

    }

    public String makeURL(String destlat, String destlog) {
        return "https://maps.googleapis.com/maps/api/directions/json" + "?origin=" + String.valueOf(latitude) + "," + String.valueOf(longitutde) + "&destination=" + destlat + "," + destlog + "&sensor=false&mode=walking&alternatives=true&key=" + getResources().getString(R.string.google_map_key_for_server);
    }

    @Override
    public void onMapClick(LatLng latLng) {

        if (markerPoints.size() > 1) {
            markerPoints.clear();
            googleMap.clear();
        }
        markerPoints.add(latLng);
        MarkerOptions options = new MarkerOptions();
        options.position(latLng);
        googleMap.addMarker(options);
        if (markerPoints.size() >= 2) {
            LatLng origin = (LatLng) markerPoints.get(0);
            LatLng dest   = (LatLng) markerPoints.get(1);
            GoogleDirectionsRequest request = new GoogleDirectionsRequest(makeURL(String.valueOf(latitude), String.valueOf(longitutde)), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    GoogleMapUtils.drawPath(GiveCash.this, googleMap, response);
                    // google_map.animateCamera(cameraUpdate);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            FunduApplication.getInstance().addToRequestQueue(request);// Getting URL to the Google Directions API

        }

    }

    public void popUpImageviewDialog(){

        try{

            final Dialog imagedialog = new Dialog(GiveCash.this);
            imagedialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            imagedialog.setCancelable(true);
            imagedialog.setCanceledOnTouchOutside(true);
            imagedialog.setContentView(R.layout.imageview_dialog);
            //canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


            ImageView imgProfilePic = (ImageView) imagedialog.findViewById(profilepic);
            ImageView call = (ImageView) imagedialog.findViewById(R.id.call);
            ImageView navigate = (ImageView) imagedialog.findViewById(R.id.navigate);
            String profilepic = currentTransaction.getImage();
            if(profilepic!=null||profilepic.equalsIgnoreCase("null"))
                Picasso.with(this).load(profilepic).fit().into(imgProfilePic);

            TextView name = (TextView) imagedialog.findViewById(R.id.name);
            name.setText(currentTransaction.getName());



            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FunduAnalytics.getInstance(GiveCash.this).sendAction("PairedContact","Call");
                    String number = FunduUser.getCountryMobileCode()+currentTransaction.getPhoneNumber();/*Utils.appendCountryCodeToNumber(getApplicationContext(),mobile)*/
                    Uri call = Uri.parse("tel:" + number);
                    Intent surf = new Intent(Intent.ACTION_DIAL, call);
                    startActivity(surf);
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


            final Window window = imagedialog.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.CENTER;
            wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);
            imagedialog.show();
        }
        catch (Exception e){
            e.printStackTrace();
        }



    }

    private void openMapIntent() {


        double srcLat = FunduUser.getLatitude();
        double srcLong = FunduUser.getLongitude();
        double destLat = currentTransaction.getLatitude();
        double destLong = currentTransaction.getLongitude();

        Location locationA = new Location("point A");
        locationA.setLatitude(srcLat);
        locationA.setLongitude(srcLong);


        Location locationB = new Location("point B");
        locationB.setLatitude(destLat);
        locationB.setLongitude(destLong);

        Utils.openMapIntent(locationA, locationB, this);

    }


    @Override
    public void onUpdateRatingResponse(JSONObject response) {
        if(progressDialog.isShowing()){
            progressDialog.hide();
        }
        if(currentTransaction.getTid()==null || currentTransaction.getTid().length()==0) {
            Fog.logEvent(false, currentTransaction.getPairRequestId(), "GiveCash","onUpdateRatingResponse", "onUpdateRatingResponse", DateUtils.getCurrentUTCtime(), Constants.getState(currentTransaction.getState()));
        } else {
            Fog.logEvent(true, currentTransaction.getTid(), "GiveCash","onUpdateRatingResponse", "onUpdateRatingResponse", DateUtils.getCurrentUTCtime(), Constants.getState(currentTransaction.getState()));
        }
        GreenDaoHelper.getInstance(this).deleteFunduTransaction(currentTransaction.getId());
        Toast.makeText(this, "Sucessfully updated.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
    }

    @Override
    public void onUpdateRatingError(VolleyError error) {
        if(progressDialog.isShowing()){
            progressDialog.hide();
        }
        Toast.makeText(this, "Sucessfully updated.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancelTransaction:
                //Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show();
                FunduAnalytics.getInstance(this).sendAction("Transaction", "NeedCash", "Cancel");
                popUpCancelDialog();

                break;
        }
    }

    public void popUpCancelDialog(){

        try{
            final Dialog canceldialog = new Dialog(GiveCash.this);

            canceldialog.setCancelable(false);
            canceldialog.setContentView(R.layout.cancel_transaction_reasons);

            final RadioGroup radioGroup = (RadioGroup)canceldialog.findViewById(R.id.cancel_reasons);
            RadioButton radioButton1 = (RadioButton)canceldialog.findViewById(R.id.radioButton1);
            radioButton1.setText ( R.string.cancel_reason_8 );
            RadioButton radioButton2 = (RadioButton)canceldialog.findViewById(R.id.radioButton2);
            radioButton2.setText ( R.string.cancel_reason_10 );
            RadioButton radioButton3 = (RadioButton)canceldialog.findViewById(R.id.radioButton3);
            radioButton3.setText (  R.string.cancel_reason_7 );
            RadioButton radioButton4 = (RadioButton)canceldialog.findViewById(R.id.radioButton4);
            RadioButton radioButton5 = (RadioButton)canceldialog.findViewById(R.id.radioButton5);
            radioButton5.setText (  R.string.cancel_reason_9 );
            RadioButton radioButton6 = (RadioButton)canceldialog.findViewById(R.id.radioButton6);
            final TextView submit = (TextView) canceldialog.findViewById(R.id.submit);
            TextView cancel = (TextView) canceldialog.findViewById(R.id.cancel);
            Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Muli-Light.ttf");
            radioButton1.setTypeface(font);
            radioButton2.setTypeface(font);
            radioButton3.setTypeface(font);
            radioButton4.setTypeface(font);
            radioButton5.setTypeface(font);
            radioButton6.setTypeface(font);
            cancel.setSelected ( true );
            radioGroup.setOnCheckedChangeListener ( new RadioGroup.OnCheckedChangeListener () {
                @Override
                public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                    if(checkedId != -1){
                        submit.setEnabled ( true );
                    }
                }
            } );

            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   /*int selectedId=radioGroup.getCheckedRadioButtonId();
                   radioReason[0] =(RadioButton)dialog.findViewById(selectedId);*/
                    canceldialog.dismiss();
                    String selection = "No Reason";
                    int id= radioGroup.getCheckedRadioButtonId();
                    if(id == -1){
                        return;
                    }
                    else {
                        View radioButton = radioGroup.findViewById(id);
                        int radioId = radioGroup.indexOfChild(radioButton);
                        RadioButton btn = (RadioButton) radioGroup.getChildAt(radioId);
                        selection = (String) btn.getText();
                    }
                    selection = selection.replace(" ","");
                    selection = selection.replace("\'","");
                    if (FunduUser.getCountryShortName().equalsIgnoreCase("KEN")){
                        Toast.makeText(GiveCash.this, "Transaction canceled successfully.", Toast.LENGTH_SHORT).show();
                        GreenDaoHelper.getInstance(GiveCash.this).deleteFunduTransaction(currentTransaction.getId());
                        finish();
                    }
                    else {
                        if(currentTransaction.getPairRequestId() == null || currentTransaction.getPairRequestId().length() == 0){

                            Fog.logEvent(true, currentTransaction.getTid(), "GiveCash","popUpCancelDialog", "pairRequestIdNull", DateUtils.getCurrentUTCtime(), Constants.getState(currentTransaction.getState()));
                            GreenDaoHelper.getInstance(GiveCash.this).deleteFunduTransaction(currentTransaction.getId());
                            finish();
                            return;
                        }
                        progressDialog.show();
                        TransactionCancelRequest cancelRequest = new TransactionCancelRequest(GiveCash.this);
                        cancelRequest.setData(currentTransaction.getTid(),currentTransaction.getPairRequestId(),selection,currentTransaction.getPhoneNumber(),FunduUser.getContactId(),FunduUser.getCountryShortName());
                        cancelRequest.setParserCallback(GiveCash.this);
                        cancelRequest.start();
                        dialog.show();
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
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onTransactionCancelResponse(JSONObject response) {
        if (progressDialog.isShowing()){
            progressDialog.dismiss();
        }
        FunduAnalytics.getInstance(GiveCash.this).sendAction("Transaction","Cancelled",(int)Double.parseDouble(currentTransaction.getAmount()));
        if(currentTransaction.getTid()==null || currentTransaction.getTid().length()==0) {
            Fog.logEvent(false, currentTransaction.getPairRequestId(), "GiveCash","onTransactionCancelResponse", "onTransactionCancelResponse", DateUtils.getCurrentUTCtime(), Constants.getState(currentTransaction.getState()));
        } else {
            Fog.logEvent(true, currentTransaction.getTid(), "GiveCash","onTransactionCancelResponse", "onTransactionCancelResponse", DateUtils.getCurrentUTCtime(), Constants.getState(currentTransaction.getState()));
        }
        GreenDaoHelper.getInstance(this).deleteFunduTransaction(currentTransaction.getId());
        stopService(new Intent(this, ProviderLocationService.class));
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

        try{
            String errorN =new String( error.networkResponse.data);
            if(errorN.contains("Already cancelled")){
                if(currentTransaction.getTid()==null || currentTransaction.getTid().length()==0) {
                    Fog.logEvent(false, currentTransaction.getPairRequestId(), "GiveCash","onTransactionCancelError", "already_cancelled", DateUtils.getCurrentUTCtime(), Constants.getState(currentTransaction.getState()));
                } else {
                    Fog.logEvent(true, currentTransaction.getTid(), "GiveCash","onTransactionCancelError", "already_cancelled", DateUtils.getCurrentUTCtime(), Constants.getState(currentTransaction.getState()));
                }
                GreenDaoHelper.getInstance(GiveCash.this).deleteFunduTransaction(currentTransaction.getId());
                finish();
                return;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        if (progressDialog.isShowing()){
            progressDialog.dismiss();
        }
        Toast.makeText(this, "Transaction cancellation error.", Toast.LENGTH_SHORT).show();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTransactionInitiated(ProviderTransactionEvent event) {

        if(event.getPushType() == Constants.PUSH_TYPE_ENUM.TRANSACTION_INITIATED.getCode() && event.getTranxInitatedData() != null){
            Fog.d("onOtpReceived","onOtpReceived"+event.getTranxInitatedData());
            JSONObject jData = event.getTranxInitatedData();
            currentTransaction.setTid(jData.optString(Constants.PushNotificationKeys.TID));
            //GreenDaoHelper.getInstance(this).updateTransaction(currentTransaction);
        }
        else if(event.getPushType() == Constants.PUSH_TYPE_ENUM.W2W_TRANSACTION_COMPLETED.getCode()){
            showRatingView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(request != null){
            request.cancel();
        }
        if(statusRequest != null )
            statusRequest.stop();
        stopService(new Intent(this, ProviderLocationService.class));
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {

        if (keyEvent.getAction() != KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_0:
                case KeyEvent.KEYCODE_1:
                case KeyEvent.KEYCODE_2:
                case KeyEvent.KEYCODE_3:
                case KeyEvent.KEYCODE_4:
                case KeyEvent.KEYCODE_5:
                case KeyEvent.KEYCODE_6:
                case KeyEvent.KEYCODE_7:
                case KeyEvent.KEYCODE_8:
                case KeyEvent.KEYCODE_9:
                    if (((EditText)view).hasFocus()) {
                        if (((EditText)view).length() != 0) {
                            ((EditText)view).setText("" + (keyCode - 7));
                            ((EditText)view).setSelection(((EditText)view).length());
                            View next = ((EditText)view).focusSearch(View.FOCUS_RIGHT);
                            if (next != null){
                                next.requestFocus();
                            }
                        }
                        return true;
                    }

            }
        }

        if (keyCode == KeyEvent.KEYCODE_BACK||keyCode == KeyEvent.KEYCODE_DEL) {
            if (((EditText)view).length()!=0) {
                ((EditText)view).setText("");
                EditText next = (EditText) view.focusSearch(View.FOCUS_LEFT); // or FOCUS_BACKWARD
                if (next != null){
                    next.requestFocus();
                }
            } else if (((EditText)view).length()==0) {
                EditText next = (EditText) view.focusSearch(View.FOCUS_LEFT); // or FOCUS_BACKWARD
                if (next != null){
                    next.requestFocus();
                }
            }
        }

        return true;
    }


    public class MyTextWatcher implements TextWatcher {

        EditText previous,current, after;

        public MyTextWatcher(EditText previous,EditText current, EditText after ) {
            this.previous = previous;
            this.current = current;
            this.after = after;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(after == null){
                Utils.hideSoftKeyboard(GiveCash.this);
            }
            if (current.length()> 0 && after != null) {
                after.requestFocus();
            }
            if(before == 1 && count == 0 && previous != null){
                this.previous.requestFocus();
            }


        }

        @Override
        public void afterTextChanged(Editable s) {

        }

    }
    //This method is used to move the marker of each car smoothly when there are any updates of their position
    public void animateMarker(final LatLng startPosition, final LatLng toPosition,
                              final boolean hideMarker) {


        // mMarker.remove();
        googleMap.clear();

        googleMap.addMarker(new MarkerOptions().
                icon(BitmapDescriptorFactory.fromResource(R.drawable.my_location)).
                position(new LatLng(latitude, longitutde)).
                title("You"));


        mMarker =  googleMap.addMarker(new MarkerOptions().
                icon(BitmapDescriptorFactory.fromResource(R.drawable.usermap)).
                position(startPosition).
                title("Reciver"));



        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();

        final long duration = 1000;
        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startPosition.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startPosition.latitude;

                mMarker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        mMarker.setVisible(false);
                    } else {
                        mMarker.setVisible(true);
                    }
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        sync();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopService(new Intent(this, ProviderLocationService.class));
    }

    private void sync(){
        String id = currentTransaction.getTid();
        if(id == null)
            id = currentTransaction.getPairRequestId();
        if(id!= null){
            statusRequest = new GetTransactionStatus(this,id);
            statusRequest.setParserCallback(new GetTransactionStatus.GetTransactionStatusResult() {
                @Override
                public void OnTransactionStatusResponse(JSONObject response) {
                    Fog.i(TAG,"OnTransactionStatusResponse "+response);
                    String transactionId = response.optString("transaction_id");
                    if(transactionId == null || !transactionId.equalsIgnoreCase(currentTransaction.getTid())){
                        //This result is for some other transaction
                        return;
                    }
                    try{
                        if(TextUtils.isEmpty(currentTransaction.getTid()) && currentTransaction.getPairRequestId()
                                .equalsIgnoreCase(response.optString("request_id"))){
                            currentTransaction.setTid(response.optString("transaction_id"));
                            GreenDaoHelper.getInstance(GiveCash.this).updateTransaction(currentTransaction);
                        }
                        int state = response.getInt("transaction_state");
                        switch (state){
                            case 0:
                                //Transaction Cancelled
                            {
                                if(currentTransaction.getTid()==null || currentTransaction.getTid().length()==0) {
                                    Fog.logEvent(false, currentTransaction.getPairRequestId(), "GiveCash","sync", "transaction_cancelled", DateUtils.getCurrentUTCtime(), Constants.getState(currentTransaction.getState()));
                                } else {
                                    Fog.logEvent(true, currentTransaction.getTid(), "GiveCash","sync", "transaction_cancelled", DateUtils.getCurrentUTCtime(), Constants.getState(currentTransaction.getState()));
                                }
                                GreenDaoHelper.getInstance(GiveCash.this).deleteFunduTransaction(currentTransaction.getId());
                                Intent intent = new Intent(GiveCash.this, TransactionStatusActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);
                                finish();
                            }
                                break;
                            case 1:
                                //Transaction Finished
                                if(currentTransaction.getState() != Constants.TRANSACTION_STATE.RATING_PENDING.getCode()){
                                    showRatingView();
                                }
                                break;
                            case 2:
                                //Transaction initiated
                                break;
                            case 3:
                                //Code screen
                                if(currentTransaction.getState() != Constants.TRANSACTION_STATE.PROVIDER_VERIFY_CODE.getCode()){
                                    updateVerifyCodeUI();
                                }
                                break;
                            case 4:
                                //Debit Failed
                                break;
                            case 5:
                                //Credit Failed
                            {
                                if(currentTransaction.getTid()==null || currentTransaction.getTid().length()==0) {
                                    Fog.logEvent(false, currentTransaction.getPairRequestId(), "GiveCash","sync", "credit_failed", DateUtils.getCurrentUTCtime(), Constants.getState(currentTransaction.getState()));
                                } else {
                                    Fog.logEvent(true, currentTransaction.getTid(), "GiveCash","sync", "credit_failed", DateUtils.getCurrentUTCtime(), Constants.getState(currentTransaction.getState()));
                                }
                                Intent intent = new Intent(GiveCash.this,TransactionStatusActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                intent.setAction("credit_failed");
                                intent.putExtra("orderNo",currentTransaction.getTid());
                                GreenDaoHelper.getInstance(GiveCash.this).deleteFunduTransaction(currentTransaction.getId());
                                startActivity(intent);
                                finish();
                            }
                            break;
                            case 7:
                            {
                                if(currentTransaction.getTid()==null || currentTransaction.getTid().length()==0) {
                                    Fog.logEvent(false, currentTransaction.getPairRequestId(), "GiveCash","sync", "incorrect_attempts", DateUtils.getCurrentUTCtime(), Constants.getState(currentTransaction.getState()));
                                } else {
                                    Fog.logEvent(true, currentTransaction.getTid(), "GiveCash","sync", "incorrect_attempts", DateUtils.getCurrentUTCtime(), Constants.getState(currentTransaction.getState()));
                                }
                                //Transaction failed due to too many incorrect code attempts
                                GreenDaoHelper.getInstance(GiveCash.this).deleteFunduTransaction(currentTransaction.getId());
                                Intent intent = new Intent(GiveCash.this,TransactionStatusActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                intent.setAction("code_limit_exhausted");
                                startActivity(intent);
                                finish();

                            }
                            break;
                        }
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }
                @Override
                public void OnTransactionStatusError(VolleyError error) {
                    Fog.i(TAG,"OnTransactionStatusError "+error);
                }
            });
            statusRequest.start();
        }
    }

    public class GetContactsTask extends AsyncTask<Object, Object, Object> {
        @Override
        protected Object doInBackground(Object... params) {

            contactItems = UserContactsTable.getContacts(GiveCash.this);
            Collections.sort(contactItems, new Comparator<ContactItem>() {
                @Override
                public int compare(ContactItem lhs, ContactItem rhs) {
                    return lhs.getContactName().compareTo(rhs.getContactName());
                }
            });
            return null;
        }
        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if(progressDialog!=null){
                progressDialog.hide();
            }
            Fog.d("onPostExecute","seekerPhn*******"+seekerPhn);
            Fog.d("onPostExecute","mobile****"+mobile);
            boolean hasNumber = false;
            if(contactItems != null){
                for(int i=0;i<contactItems.size();i++){
                    if(contactItems.get(i).getContactNumber().equalsIgnoreCase(seekerPhn)){
                        hasNumber = true;
                        callPairedUser(hasNumber);
                        Fog.d("hasNumber",""+hasNumber);
                        break;
                    }
                }
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * EventBus handling
     */

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTransactionInitiated(SeekerTransactionEvent event) {

        if(event.getPushType() == Constants.PUSH_TYPE_ENUM.NEEDCASH_TRANSACTION_COMPLETED.getCode()){
            showRatingView();
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocationEvent(LocationEvent event) {

        if(event.getMobile() != null && event.getMobile().equalsIgnoreCase(currentTransaction.getPhoneNumber())){
            double latitude= event.getLatitude();
            double longitude= event.getLogitude();
            LatLng destLatLng=new LatLng(latitude,longitude);
            if(!isFirstTime){
                animateMarker(mMarker.getPosition(),destLatLng,false);
                isFirstTime=true;
            }else {
                animateMarker(startLatLng,destLatLng,false);
            }
            startLatLng=destLatLng;
        }
    }
}


