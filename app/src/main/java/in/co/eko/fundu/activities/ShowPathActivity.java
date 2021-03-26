package in.co.eko.fundu.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.co.eko.fundu.FunduApplication;
import in.co.eko.fundu.R;
import in.co.eko.fundu.activities.upi.YesBankUPIClient;
import in.co.eko.fundu.adapters.MerchantsAtmsAdapter;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.database.tables.TransactionStatusTable;
import in.co.eko.fundu.interfaces.MerchantAtmOptions;
import in.co.eko.fundu.models.Contact;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.models.Neighbour;
import in.co.eko.fundu.models.TransactionPair;
import in.co.eko.fundu.requests.GetMerchantProfile;
import in.co.eko.fundu.requests.GoogleDirectionsRequest;
import in.co.eko.fundu.requests.SaveQRCodeTransactionRequest;
import in.co.eko.fundu.requests.SendPushNotificationToMerchant;
import in.co.eko.fundu.utils.AppPreferences;
import in.co.eko.fundu.utils.Fog;
import in.co.eko.fundu.utils.FunduAnalytics;
import in.co.eko.fundu.utils.GoogleMapUtils;
import in.co.eko.fundu.utils.Utils;
import in.co.eko.fundu.views.slidinguppanel.SlidingUpPanelLayout;

import static in.co.eko.fundu.activities.upi.YesBankUPIClient.YES_BANK_CLIENT_ACTION.SCAN_QR_CODE;


public class ShowPathActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<LocationSettingsResult>, GoogleMap.OnMarkerClickListener, GetMerchantProfile.MerchantProfileResult{
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2;
    private static final int REQUEST_CHECK_SETTINGS = 5;
    private View progressBar;

    private static final String TAG = ShowPathActivity.class.getSimpleName();
    private GoogleMap googleMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private SupportMapFragment mapFragment;
    private LatLng mRequestLocation;
    private SlidingUpPanelLayout slidingUpPanelLayout;
    private List<TransactionPair> pairs;
    TextView placeTextView;
    String mobile = "";
    private RecyclerView mOptionsList;
    public AppPreferences pref;
    private Map<String,JSONObject> profiles = new HashMap<>();
    private View mProfileView;
    private Neighbour mSelectedItem;
    private TextView mInfo;
    /**
     * Activity Result request code
     */
    private final int QR_CODE_TRANSACTION = 1;
    private final int TRANSACTION_STATUS_ACTION = 2;



    public static void start(Context context) {
        Intent starter = new Intent(context, ShowPathActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_path);
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        mLocalBroadcastManager.registerReceiver(mBroadcastReceiver, new IntentFilter(Constants.DISMISS_PATH_SCREEN_ACTION));
        if (getIntent()!=null && getIntent().getBooleanExtra("no_user", false)) {
            googleMap = null;
        } else {
            buildGoogleApiClient();
        }
        pref = FunduUser.getAppPreferences();
        slidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.slidePanel);
        findViewById(R.id.iFeedback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFeedbackClick();
            }
        });
        findViewById(R.id.feedback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFeedbackClick();
            }
        });
        mOptionsList = (RecyclerView)findViewById(R.id.optionsList);
        mOptionsList.setLayoutManager(new LinearLayoutManager(this));
        //slidingUpPanelLayout.setScrollableView(mOptionsList);
        slidingUpPanelLayout.setScrollableView(mOptionsList);
        placeTextView = ((TextView) findViewById(R.id.placeName));
        mInfo = (TextView)findViewById(R.id.info);
        progressBar = findViewById(R.id.loader);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        ImageView floatingActionButton = (ImageView) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    LatLng latLng = new LatLng(mRequestLocation.latitude, mRequestLocation.longitude);
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                    googleMap.animateCamera(cameraUpdate);

                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
        pairs = TransactionStatusTable.getTransactionsByStatus(this, "false");
        TransactionStatusTable.deleteTransactionPairs(this);

        if (getIntent()!=null && getIntent().getBooleanExtra("no_user", false)) {
            findViewById(R.id.no_user_around).setVisibility(View.VISIBLE);
            findViewById(R.id.map_layout).setVisibility(View.GONE);
            mInfo.setText(getString(R.string.no_fundu_user_found)+" "+getString(R.string.ntam));
            double pinlogitude = getIntent().getDoubleExtra("pinlogitude",0);
            double pinlatitude = getIntent().getDoubleExtra("pinlatitude",0);
            mRequestLocation = new LatLng(pinlatitude,pinlogitude);
            displayAtmsAndMerchants();
        } else {
            findViewById(R.id.no_user_around).setVisibility(View.GONE);
            findViewById(R.id.map_layout).setVisibility(View.VISIBLE);
        }
        if(getIntent() != null && getIntent().getStringExtra("custid") != null){
            //Show profile for custid
            mSelectedItem = new Neighbour();
            mSelectedItem.setId(getIntent().getStringExtra("custid"));
            double lng = getIntent().getDoubleExtra("logitude",0);
            double lat = getIntent().getDoubleExtra("latitude",0);
            double pinlogitude = getIntent().getDoubleExtra("pinlogitude",0);
            double pinlatitude = getIntent().getDoubleExtra("pinlatitude",0);
            mRequestLocation = new LatLng(pinlatitude,pinlogitude);
            Contact.Location location = new Contact.Location(new double[]{lng,lat});
            mSelectedItem.setContactType("merchant");
            mSelectedItem.setLocation(location);
            findViewById(R.id.map_layout).setVisibility(View.INVISIBLE);
            slidingUpPanelLayout.setVisibility(View.INVISIBLE);
            showMerchant();
            return;
        }


        try {
            JSONObject jData = new JSONObject(getIntent().getStringExtra(Constants.PUSH_JSON_DATA));
            mRequestLocation = new LatLng(jData.getJSONArray(Constants.PushNotificationKeys.REQUEST_LOCATION).getDouble(1),jData.getJSONArray(Constants.PushNotificationKeys.REQUEST_LOCATION).getDouble(0));
            String action = jData.optString(Constants.PushNotificationKeys.ACTION);
            if(action != null && action.contains("Contact not available")){
                mInfo.setText(getString(R.string.una)+" "+getString(R.string.ntam));
            }
            else if(action != null && action.contains("Contact did not accept")){

                mInfo.setText(getString(R.string.user_not_accept_request)+" "+getString(R.string.ntam));
            }
            else if(action != null && action.contains("nearby cashpoints atm")){
                mInfo.setText(getString(R.string.ncpatm));
            }
            else{
                mInfo.setText(getString(R.string.no_fundu_user_found)+" "+getString(R.string.ntam));
            }

        }
        catch (JSONException e){
            e.printStackTrace();
        }



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
//            finish();
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(mProfileView != null && mProfileView.getVisibility() == View.VISIBLE){
            //Hide profile
            if(getIntent()!= null && getIntent().getStringExtra("custid")!=null)
            {
                super.onBackPressed();
                return;
            }
            mProfileView.setVisibility(View.GONE);
            return;
        }
        else if(slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED){
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            return;
        }
        else if(progressBar.getVisibility()==View.VISIBLE){
            progressBar.setVisibility(View.GONE);
            mSelectedItem = null;
            return;
        }
        super.onBackPressed();

    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient!=null && !mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient!=null && mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if(getIntent() != null && getIntent().getStringExtra("custid") != null){
            return;
        }
        this.googleMap = googleMap;
        googleMap.setMyLocationEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        boolean success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.fundu_map_style));

        this.googleMap.setOnMarkerClickListener(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                showExplanationDialog(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(ShowPathActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                    }
                });
            } else {
                ActivityCompat.requestPermissions(ShowPathActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        } else {
           // displayLocation();
            displayAtmsAndMerchants();
            displayRequestedLocation();

        }
    }
    private void displayAtmsAndMerchants(){
        String jDataS = getIntent().getStringExtra(Constants.PUSH_JSON_DATA);
        JSONArray merchantAtms = null;
        try {
            if (getIntent()!=null && getIntent().getBooleanExtra("no_user", false)) {
                if (getIntent()!=null && getIntent().getBooleanExtra("no_data", false)) {
                    findViewById(R.id.listofatmsmerchants).setVisibility(View.GONE);
                    return;
                } else {
                    merchantAtms = new JSONArray(getIntent().getStringExtra(Constants.PUSH_JSON_DATA));
                }
            } else {
                JSONObject jData = new JSONObject(jDataS);
                merchantAtms = jData.optJSONArray(Constants.PushNotificationKeys.MERCHANT_ATM);
            }

            ArrayList<Neighbour> list = new ArrayList<>();
            ArrayList<Neighbour> atms = new ArrayList<>();
            if(merchantAtms != null && merchantAtms.length() >0)
            {
                for(int i = 0;i<merchantAtms.length();i++){
                    JSONObject jsonObject = (JSONObject)merchantAtms.get(i);
                    Neighbour contact  = new Neighbour();
                    contact.setContactType(jsonObject.optString("contact_type"));
                    contact.setName(jsonObject.optString("name"));
                    JSONArray jLocation = ((JSONObject)jsonObject.opt("location")).optJSONArray("coordinates");
                    Contact.Location location = new Contact.Location(new double[]{(Double) jLocation.get(0),(Double) jLocation.get(1)});
                    contact.setLocation(location);
                    contact.setId(jsonObject.optString("custid"));
                    contact.setDistance((float) (jsonObject.optDouble("distance")));
                    MarkerOptions options = new MarkerOptions();
                    options.position(new LatLng(location.coordinates[1],location.coordinates[0]));
                    options.title(contact.getName());
                    if(contact.getContactType().equalsIgnoreCase("MERCHANT")) {
                        contact.setMobile(jsonObject.optString("mobile"));
                        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.merchant));
                        list.add(contact);
                    }
                    else{
                        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_atm));
                        atms.add(contact);
                    }
                    if (getIntent()!=null && !getIntent().getBooleanExtra("no_user", false)) {
                        this.googleMap.addMarker(options);
                    }
                }
                if(list.size() > 0){
                    findViewById(R.id.eko_cash_point_info).setVisibility(View.VISIBLE);
                }
                else{
                    //Change text to for only atm
                    findViewById(R.id.eko_cash_point_info).setVisibility(View.GONE);
                }
                if(atms.size()>0){

                    list.addAll(atms);
                }
                if(list.size()>0){

                    Collections.sort(list, new Comparator<Neighbour>() {
                        @Override
                        public int compare(Neighbour o1, Neighbour o2) {
                            return (int)(o1.getDistance()-o2.getDistance());
                        }
                    });

                    MerchantsAtmsAdapter atmsAdapter = new MerchantsAtmsAdapter(list, new MerchantAtmOptions() {
                        @Override
                        public void navigate(Neighbour item) {
                            if(item.getContactType().equalsIgnoreCase("merchant")){
                                FunduAnalytics.getInstance(ShowPathActivity.this).sendAction("List","ViewMerchantProfile","NavIcon");
                            }
                            else
                                FunduAnalytics.getInstance(ShowPathActivity.this).sendAction("List","NavigatetoAtm","NavIcon");

                            navigateOnGoogleMaps(item.getLocation());
                        }
                        @Override
                        public void onItemClick(Neighbour item) {
                            if(mProfileView != null && mProfileView.getVisibility()== View.VISIBLE)
                                return;
                            mSelectedItem = item;
                           if(item.getContactType().equalsIgnoreCase("merchant")){
                               FunduAnalytics.getInstance(ShowPathActivity.this).sendAction("List","ViewMerchantProfile");
                                showMerchant();
                           }
                           else{
                                FunduAnalytics.getInstance(ShowPathActivity.this).sendAction("List","NavigatetoAtm");
                                navigateOnGoogleMaps(item.getLocation());
                           }
                        }
                    });
                    mOptionsList.setItemViewCacheSize(0);
                    mOptionsList.setAdapter(atmsAdapter);
                }
            }
            else
                finish();

        }catch(JSONException e){
            e.printStackTrace();
        }

    }
    private void showMerchant(){
        //show merchant profile
        if(profiles.get(mSelectedItem.getId())==null){
            //Get from server
            progressBar.setVisibility(View.VISIBLE);
            GetMerchantProfile request = new GetMerchantProfile(ShowPathActivity.this,mSelectedItem.getId());
            request.setParserCallback(ShowPathActivity.this);
            request.start();
        }
        else{
            JSONObject profile = profiles.get(mSelectedItem.getId());
            showEkoCashPointProfile(profile);
        }
    }
    private void navigateOnGoogleMaps(Contact.Location location){
        Location locationA = new Location("point A");
        locationA.setLatitude(mRequestLocation.latitude);
        locationA.setLongitude(mRequestLocation.longitude);


        Location locationB = new Location("point B");
        locationB.setLatitude(location.coordinates[1]);
        locationB.setLongitude(location.coordinates[0]);

        Utils.openMapIntent(locationA,locationB,this);
    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        createLocationRequest();
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
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT); // 20 meters
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        //final LocationSettingsStates= locationSettingsResult.getLocationSettingsStates();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                // All location settings are satisfied. The client can initialize location
                // requests here.
                Fog.d(TAG, "LocationSettingsStatusCodes.SUCCESS");

                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                // Location settings are not satisfied. But could be fixed by showing the user
                // a dialog.
                Fog.d(TAG, "LocationSettingsStatusCodes.RESOLUTION_REQUIRED");

                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    status.startResolutionForResult(
                            this,
                            REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    // Ignore the error.
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Fog.d(TAG, "LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE");
                // Location settings are not satisfied. However, we have no way to fix the
                // settings so we won't show the dialog.
                break;
        }
    }
    private void showEkoCashPointProfile(final JSONObject profile){
        if(mSelectedItem == null || profile == null)
            return;
        if(!mSelectedItem.getId().equalsIgnoreCase(profile.optString("customer_id"))){
            return;
        }
        //slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        mProfileView = findViewById(R.id.eko_cash_point);
        mProfileView.setBackgroundResource(R.color.white);
        mProfileView.setVisibility(View.VISIBLE);
        mProfileView.findViewById(R.id.progress_bar_2).setVisibility(View.VISIBLE);
        TextView title = (TextView) mProfileView.findViewById(R.id.title);
        ImageView icon = (ImageView)mProfileView.findViewById(R.id.user_image);
        TextView userName = (TextView)mProfileView.findViewById(R.id.user_name);
        TextView desc = (TextView)mProfileView.findViewById(R.id.pair_contact_desc);
        ImageView dot1 = (ImageView)mProfileView.findViewById(R.id.dot1_2);
        TextView note = (TextView)mProfileView.findViewById(R.id.note);
        View cancelTransaction = mProfileView.findViewById(R.id.cancelTransaction);
        View navIcon = mProfileView.findViewById(R.id.nav_icon);
        View call = mProfileView.findViewById(R.id.ll_call);
        mProfileView.findViewById(R.id.ratingll).setVisibility(View.GONE);

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FunduAnalytics.getInstance(ShowPathActivity.this).sendAction("MerchantProfile","Call");
                String number = profile.optString("mobile");
                if(TextUtils.isEmpty(number))
                    return;
                number = Utils.appendCountryCodeToNumber(ShowPathActivity.this,number);
                Uri call = Uri.parse("tel:" + number);
                Intent surf = new Intent(Intent.ACTION_DIAL, call);
                startActivity(surf);
            }
        });
        navIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Naviage to location
                if(mSelectedItem != null){
                    FunduAnalytics.getInstance(ShowPathActivity.this).sendAction("MerchantProfile","NavIcon");
                    navigateOnGoogleMaps(mSelectedItem.getLocation());
                }

            }
        });

        cancelTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FunduAnalytics.getInstance(ShowPathActivity.this).sendAction("MerchantProfile","Cancel");
                onBackPressed();
            }
        });


        Utils.makeMeBlink(dot1,500,10);
        desc.setText("");
        try {
            JSONObject address = profile.getJSONArray("address").getJSONObject(0);
            String ekoCashPointAddress = "\n\nAddress :\n"+profile.optString("name")+"\n"+address.optString("Line1")+" "+
                    address.optString("Line2")+"\n"+address.optString("city")+" "+
                    address.optString("state")+"\n"+address.optString("zip");
            desc.setText(ekoCashPointAddress);

        } catch(JSONException e) {
            e.printStackTrace();
        }
        mProfileView.findViewById(R.id.rl_compass).setVisibility(View.GONE);
        Button whenYouMeet = (Button)mProfileView.findViewById(R.id.whenYouMeet);
        whenYouMeet.setText("I've Reached");
        whenYouMeet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showQRCodePopup();
            }
        });

        String name = profile.optString("name");
        try{
            String[] parts = name.split(" ");
            if(parts.length > 1){
                name = parts[0];
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        userName.setText(name);
        icon.setImageResource(R.drawable.merchant);
        title.setText(R.string.gtecp);
        note.setText(R.string.ecp_note);

    }
    private void showQRCodePopup(){
        if(pref.getBoolean(Constants.DONT_SHOW_QRCODE_POPUP,false)){
            onReachingMerchant();
        }
        else{
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.overlay_scan_qrcode, null);
            TextView tv = (TextView)dialogView.findViewById(R.id.desc);
            tv.setText(R.string.sqr_desc_m);
            dialogBuilder.setView(dialogView);
            final Dialog upiDialog = dialogBuilder.show();
            upiDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT);
            Button iUnderstand = (Button)dialogView.findViewById(R.id.button);
            final CheckBox dontShow = (CheckBox)dialogView.findViewById(R.id.dontshowagain);
            iUnderstand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onReachingMerchant();
                    upiDialog.dismiss();
                    if(dontShow.isChecked())
                        pref.putBoolean(Constants.DONT_SHOW_QRCODE_POPUP,true);
                }
            });
        }
    }
    private void onReachingMerchant(){
        //Send push notification to the merchant
        FunduAnalytics.getInstance(ShowPathActivity.this).sendAction("MerchantProfile","ScanQrCode");
        SendPushNotificationToMerchant request = new SendPushNotificationToMerchant(this);
        request.setData(mSelectedItem.getId());
        request.start();
        openScanQRCodeActivity();

    }
    private void openScanQRCodeActivity(){
        Intent intent = new Intent(this, YesBankUPIClient.class);
        intent.putExtra ( "action",SCAN_QR_CODE);
        startActivityForResult(intent,QR_CODE_TRANSACTION);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return true;
    }

    private void showExplanationDialog(DialogInterface.OnClickListener okListener) {

    }
    private void drawPath(LatLng location){
        GoogleDirectionsRequest request = new GoogleDirectionsRequest(makeURL(location.latitude,location.longitude), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    drawPath(response);
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        FunduApplication.getInstance().addToRequestQueue(request);

    }

    private void displayRequestedLocation(){
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mRequestLocation, 15);
        googleMap.moveCamera(cameraUpdate);
        MarkerOptions marker = new MarkerOptions();
        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.my_location_exact));
        marker.flat(true);
        marker.position(mRequestLocation);
        this.googleMap.addMarker(marker);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == Activity.RESULT_OK){
            switch (requestCode) {
                case QR_CODE_TRANSACTION:
                    if(data != null){
                        Bundle bundle = data.getExtras();
                        String statusCode = bundle.getString("status");
                        if(statusCode!=null && statusCode.equalsIgnoreCase("MC07"))
                            return;
                        String statusDesc = bundle.getString("statusDesc");
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
                        try{
                            JSONObject status = new JSONObject();
                            status.put("pgMeTrnRefNo",pgMeTrnRefNo);
                            status.put("status",statusCode);
                            status.put("payerVA",payerVA);
                            status.put("statusDesc",statusDesc);
                            status.put("txnAmount",txnAmount);
                            status.put("npciTxnId",npciTxnId);
                            status.put("payerAccountNo",payerAccountNo);
                            status.put("tranAuthdate",tranAuthdate);
                            status.put("additional",add1+"|"+add2+"|"+add3+"|"+add4+"|"+add5+"|"+add6+"|"+add7+"|"+add8+"|"+add9+"|"+add10);
                            status.put("refId",refId);
                            status.put("payerIfsc",payerIfsc);
                            status.put("payerAccName",payerAccName);
                            status.put("approvalCode",approvalCode);
                            status.put("orderNo", orderNo);
                            status.put("responsecode",responsecode);
                            SaveQRCodeTransactionRequest request = new SaveQRCodeTransactionRequest(ShowPathActivity.this);
                            request.setData(orderNo,txnAmount,status);
                            request.start();
                            if(statusCode !=null){
                                if(statusCode.equalsIgnoreCase("S")){
                                    //show payment successful
                                    FunduAnalytics.getInstance(ShowPathActivity.this).sendAction("QRTransaction","Success",(int)Double.parseDouble(txnAmount));

                                }
                                Intent intent = new Intent(ShowPathActivity.this,TransactionStatusActivity.class);
                                intent.setAction("qrcode_status");
                                intent.putExtras(bundle);
                                startActivityForResult(intent,TRANSACTION_STATUS_ACTION);
                            }

                        }
                        catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                    break;
                case TRANSACTION_STATUS_ACTION:
                {
                    String action = data.getAction();
                    if(action != null && action.equalsIgnoreCase("retry")){
                        openScanQRCodeActivity();
                    }
                }
                break;
            }
        }

    }


    public String makeURL(double destlat, double destlog) {

        return "https://maps.googleapis.com/maps/api/directions/json" + "?origin=" + String.valueOf(mRequestLocation.latitude) + "," + String.valueOf(mRequestLocation.longitude) + "&destination=" + destlat + "," + destlog + "&sensor=false&mode=w&alternatives=true&key=" + getResources().getString(R.string.google_map_key_for_server);
    }

    private Polyline line;

    public void drawPath(String result) {
        try {
            // Transform the string into a json object
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = GoogleMapUtils.decodePoly(encodedString);
            PolylineOptions options = new PolylineOptions().width(10).color(getResources().getColor(R.color.colorPrimary)).geodesic(true);
            for (int z = 0; z < list.size(); z++) {
                LatLng point = list.get(z);
                options.add(point);
            }
            line = googleMap.addPolyline(options);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    LocalBroadcastManager mLocalBroadcastManager;
    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.DISMISS_PATH_SCREEN_ACTION)) {
                finish();
            }
        }
    };

    private void onFeedbackClick(){
        if (getIntent()!=null && getIntent().getBooleanExtra("no_user", false)) {
            FunduAnalytics.getInstance(ShowPathActivity.this).sendAction("Feedback","NoUserAround");
            View v = getWindow().getDecorView().getRootView();
            v.setDrawingCacheEnabled(true);
            Bitmap bmp = Bitmap.createBitmap(v.getDrawingCache());
            v.setDrawingCacheEnabled(false);
            Utils.takeFeedback(bmp,this);
            return;
        }
        if(!(mProfileView != null && mProfileView.getVisibility() == View.VISIBLE) && slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED){
            FunduAnalytics.getInstance(ShowPathActivity.this).sendAction("Feedback","MerchantAtmMap");
            GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {

                @Override
                public void onSnapshotReady(Bitmap snapshot) {
                    View v = getWindow().getDecorView().getRootView();
                    v.setDrawingCacheEnabled(true);
                    Bitmap bmp = v.getDrawingCache().copy(Bitmap.Config.RGB_565, true);
                    Bitmap bmOverlay = Bitmap.createBitmap(
                            bmp.getWidth(), bmp.getHeight(),
                            bmp.getConfig());

                    Canvas canvas = new Canvas(bmOverlay);
                    canvas.drawBitmap(bmp, 0, 0, null);
                    View view1 = findViewById(R.id.listofatmsmerchants);
                    int[] location = new int[2];
                    view1.getLocationOnScreen(location);
                    View frameContainer = findViewById(R.id.map);
                    int windowLocation[] = new int[2];
                    frameContainer.getLocationOnScreen(windowLocation);
                    Rect mapRect = new Rect(0,windowLocation[1],view1.getWidth(),location[1]);
                    canvas.drawBitmap(snapshot,null,mapRect,null);
                    v.setDrawingCacheEnabled(false);
                    Utils.takeFeedback(bmOverlay,ShowPathActivity.this);

                }
            };
            this.googleMap.snapshot(callback);
            return;
        }
        else if(mProfileView != null && mProfileView.getVisibility()==View.VISIBLE)
            FunduAnalytics.getInstance(ShowPathActivity.this).sendAction("Feedback","MerchantProfile");
        else
            FunduAnalytics.getInstance(ShowPathActivity.this).sendAction("Feedback","MerchantAtmList");
        View v = getWindow().getDecorView().getRootView();
        v.setDrawingCacheEnabled(true);
        Bitmap bmp = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false);
        Utils.takeFeedback(bmp,this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocalBroadcastManager.unregisterReceiver(mBroadcastReceiver);
    }



    @Override
    public void OnMerchantProfileResponse(String custid, JSONObject response) {
        progressBar.setVisibility(View.GONE);
        profiles.put(custid,response);
        showEkoCashPointProfile(response);
    }

    @Override
    public void OnMerchantProfileError(VolleyError error) {
        progressBar.setVisibility(View.GONE);
        Fog.logException(error);
    }


}
