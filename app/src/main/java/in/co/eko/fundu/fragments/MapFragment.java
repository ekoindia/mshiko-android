package in.co.eko.fundu.fragments;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.GpsStatus;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.co.eko.fundu.R;
import in.co.eko.fundu.activities.HomeActivity;
import in.co.eko.fundu.activities.ShowPathActivity;
import in.co.eko.fundu.adapters.FunduMapAdapter;
import in.co.eko.fundu.adapters.MapAdapter;
import in.co.eko.fundu.adapters.MapSearchAdapter;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.constants.V1API;
import in.co.eko.fundu.models.Contact;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.models.Neighbour;
import in.co.eko.fundu.parser.GetNeighborsParserTask;
import in.co.eko.fundu.requests.GetNeighborsRequest;
import in.co.eko.fundu.requests.UpdateLocationRequest;
import in.co.eko.fundu.utils.Fog;
import in.co.eko.fundu.utils.FunduAnalytics;
import in.co.eko.fundu.utils.Utils;
import in.co.eko.fundu.views.CustomMapFragment;
import in.co.eko.fundu.views.slidinguppanel.SlidingUpPanelLayout;

public class MapFragment extends BaseFragment implements LocationListener, OnMapReadyCallback,
        SearchView.OnQueryTextListener, View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, GpsStatus.Listener, GetNeighborsRequest.OnGetNeighborsResults,
        GoogleMap.OnMarkerClickListener, ResultCallback<LocationSettingsResult>, GoogleMap.OnMarkerDragListener,
        GoogleMap.OnCameraIdleListener, GoogleMap.OnCameraMoveStartedListener {

    private static final String TAG = MapFragment.class.getSimpleName();
    private static final int REQUEST_CHECK_SETTINGS = 909;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private View imageButton;
    ImageButton userimg;
    private GoogleMap map;
    private CustomMapFragment mapFragment;
    private TextView placeTextView;
    private SlidingUpPanelLayout slidingUpPanelLayout, gpsAuthenticationPopup;
    private Location mLastLocation;
    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;
    private LocationRequest mLocationRequest;
    private GetNeighborsRequest request;
    private View progressOverlay;
    private AlertDialog.Builder dialog;
    private RecyclerView recyclerViewMap;
    ArrayList<Neighbour> filtercontacts = new ArrayList<>();
    private MapSearchAdapter mapadapter;
    private Neighbour mSelectedMarker;
    MenuItem searchItem;
    RelativeLayout nc;
    ImageView floatingActionButton, nav_icon;
    public static boolean focusCamera = false;
    public static LatLng selectedLatLong;
    private TextView myLocation,requestedLocation;
    private View mnavigateLl, mCallLl;
    private MAP_STATE state;
    public enum MAP_STATE{
        DEFAULT,
        MERCHANT_ATMS_FOR_REQUESTED_LOCATION;
    };
    private ProgressDialog progressDialog;
    private JSONArray neighbourList;



    public static MapFragment newInstance(MAP_STATE state) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putSerializable("state", state);
        fragment.setArguments(args);
        return fragment;
    }

    public MapFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialog = new AlertDialog.Builder(getActivity());
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait");
//        neighbourList = new ArrayList<>();

        buildGoogleApiClient();
        //lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        // First we need to check availability of play services
        // gpsTracker = new GPSTracker(getActivity());
//        request = new GetNeighborsRequest(getActivity());
//        request.setParserCallback(this);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        setHasOptionsMenu(true);
        Fog.d("onCreate", "MapFragment");
        recyclerViewMap = (RecyclerView) view.findViewById(R.id.RecyclerViewMap);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewMap.setLayoutManager(mLayoutManager);
        placeTextView = ((TextView) view.findViewById(R.id.placeName));
        userimg = (ImageButton) view.findViewById(R.id.imageButton);
        nav_icon = (ImageView) view.findViewById(R.id.nav_icon);
        progressOverlay = view.findViewById(R.id.progressOverlay);
        myLocation = (TextView)view.findViewById(R.id.mylocation);
        requestedLocation = (TextView)view.findViewById(R.id.requestedlocation);
        mnavigateLl = view.findViewById(R.id.navigatell);
        mCallLl = view.findViewById(R.id.callll);
        // nc = (TextView) view.findViewById(R.id.tv_need_cash);
        nc = (RelativeLayout) view.findViewById(R.id.tv_need_cash);
        // TextView loadWallet = (TextView) view.findViewById(R.id.tv_load_wallet);
        if (savedInstanceState == null) {
            mapFragment = new CustomMapFragment();
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.googleMap, mapFragment);
            fragmentTransaction.commitAllowingStateLoss();
        }
        Fog.d("ALLOW  ", pref.getString(Constants.ALLOW_WITHDRAW));
        if (pref.getString(Constants.ALLOW_WITHDRAW).equalsIgnoreCase("No")) {
            nc.setVisibility(View.GONE);
        } else
            nc.setVisibility(View.VISIBLE);
        // nc.setTypeface(typeface.getOpenSansSemiBold());
        nc.setOnClickListener(this);
        // loadWallet.setOnClickListener(this);
        gpsAuthenticationPopup = (SlidingUpPanelLayout) view.findViewById(R.id.gpsSlidePanel);
        floatingActionButton = (ImageView) view.findViewById(R.id.fab);
        ImageView floatingActionButton = (ImageView) view.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    moveBackToCurrentLocation();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
        mnavigateLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mSelectedMarker != null && mSelectedMarker.getContactType() != null){

                    openMapIntent();
//                    if(mSelectedMarker.getContactType().equalsIgnoreCase("ATM"))
//                        openMapIntent();
//                    else{
//                        openMerchantProfile(mSelectedMarker);
//                    }
                }

            }
        });

        mCallLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.call(mSelectedMarker.getMobile(),getActivity());
            }
        });

        slidingUpPanelLayout = (SlidingUpPanelLayout) view.findViewById(R.id.slidePanel);
       /* if (!gpsTracker.isGPSEnabled()) {
            // gpsTracker.showSettingsAlert();
            gpsAuthenticationPopup.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);

        } else {
           *//* gpsAuthenticationPopup.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);*//*
        }*/
        ImageView closePopup = (ImageView) gpsAuthenticationPopup.findViewById(R.id.close_popup);
        Button activateGPS = (Button) gpsAuthenticationPopup.findViewById(R.id.activateGPSButton);
        //TextView notNow = (TextView) gpsAuthenticationPopup.findViewById(R.id.not_now);
        activateGPS.setOnClickListener(this);
        closePopup.setOnClickListener(this);
        view.findViewById(R.id.markerDetail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSelectedMarker != null && mSelectedMarker.getContactType() != null){
                    if(mSelectedMarker.getContactType().equalsIgnoreCase(Constants.CustomerType.MERCHANT.toString())){
                        openMerchantProfile(mSelectedMarker);
                    }
                    else if(mSelectedMarker.getContactType().equalsIgnoreCase(Constants.CustomerType.ATM.toString())){
                        openMapIntent();
                    }
                }
            }
        });
        imageButton = view.findViewById(R.id.imageButtonClose);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });
        if (checkPlayServices()) {
            // Building the GoogleApi client
            buildGoogleApiClient();
        }
        FunduAnalytics.getInstance(getActivity()).sendScreenName("Map");
        return view;
    }

    @Override
    public void onGpsStatusChanged(int event) {
        switch (event) {
            case GpsStatus.GPS_EVENT_STARTED:
                /*if (gpsTracker.isGPSEnabled())
                    gpsAuthenticationPopup.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);*/
                break;
            case GpsStatus.GPS_EVENT_STOPPED:
               /* if (!gpsTracker.isGPSEnabled())
                    gpsAuthenticationPopup.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);*/
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Fog.i("FragCreateList", "onCreateOptionsMenu called");
        super.onCreateOptionsMenu(menu, inflater);
        if (FunduUser.getContactId() != null) {
            inflater.inflate(R.menu.menu_contacts_fragment, menu);
            searchItem = menu.findItem(R.id.action_search);
            final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            searchItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    recyclerViewMap.setVisibility(View.VISIBLE);
                    return false;
                }
            });
            customizeSearchView(searchView);
            searchView.setOnQueryTextListener(this);
        } else {
        }
    }

    private void customizeSearchView(SearchView searchView) {
        if (searchView.getQuery().toString().equalsIgnoreCase("")) {
            recyclerViewMap.setVisibility(View.GONE);
        } else
            recyclerViewMap.setVisibility(View.VISIBLE);
        searchView.setQueryHint(getString(R.string.map_search_hint_text));
        View searchplate = searchView.findViewById(android.support.v7.appcompat.R.id.search_plate);
        searchplate.setBackgroundResource(R.drawable.green_button_disable_background);
        EditText searchEditText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        if (searchEditText != null) {
            searchEditText.setTextColor(getResources().getColor(R.color.White));
            searchEditText.setHintTextColor(getResources().getColor(R.color.White));
            searchEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 1565 );
        }

    }

    public GoogleMap getMap(){
        return this.map;
    }

    private MapAdapter<ArrayList<Neighbour>> adapter;


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Fog.d("onMapReady", "onMapReady");
        mapFragment.InitializeMap(googleMap);
        this.map = googleMap;
        //map.setOnCameraChangeListener(this);
        map.setOnCameraIdleListener(this);
        map.setOnCameraMoveStartedListener(this);

        this.adapter = new FunduMapAdapter(getActivity(), map,mLastLocation);
        this.map.setOnMarkerClickListener(this);
        try {
            // Customise the styling of the base google
            this.map.setOnMarkerDragListener(this);
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getActivity(), R.raw.fundu_map_style));

            if (!success) {
                Fog.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Fog.e(TAG, "Can't find style. Error: ", e);
        }
        if (FunduUser.isUserMobileVerified()) {
            LatLng location = new LatLng(FunduUser.getLatitude(), FunduUser.getLongitude());
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
            //googleMap.moveCamera(CameraUpdateFactory.zoomBy(1.0f));
        }
        checkAndRequestPermission();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity != null && activity instanceof HomeActivity) {
            ((HomeActivity) activity).showHamburgerIcon();
        }
    }

    private void checkAndRequestPermission() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                showExplanationDialog(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissions(
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                    }
                });
            } else {
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        } else {
            //mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            displayLocation();
            if (mLastLocation != null) {
                updateCamera();
            }
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        needCash.onDestroyMapFragment();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close_popup:
                gpsAuthenticationPopup.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                break;
            case R.id.activateGPSButton:
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                gpsAuthenticationPopup.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                break;
            case R.id.tv_need_cash:
                if (null != needCash) {
                    if (Utils.isNetworkAvailable(getActivity())) {
                        FunduAnalytics.getInstance(getActivity()).sendAction("Map","Get Cash");
                        if (mLastLocation != null) {
//                        FunduUser.setLocation(mLastLocation);
                            if(neighbourList!=null) {
                                takeAction();
                            } else {
                                progressDialog.show();
                            }

                        } else {
                            Toast.makeText(getActivity(), "Please turn on your GPS first.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
        }
    }

    private void takeAction() {
        if(neighbourList!=null && neighbourList.length()>0) {
            try {

                JSONObject object = (JSONObject) neighbourList.get(0);

                if (object!=null && object.get("contact_type")!=null && String.valueOf(object.get("contact_type")).equalsIgnoreCase("PERSON")) {
                    needCash.onClickNeedCash(progressOverlay);
                } else {
                    openNoNearByUser();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            openNoNearByUser();
        }
    }

    private void openNoNearByUser() {
        LatLng midLatLng = this.map.getCameraPosition().target;
        Intent intent1 = new Intent(getContext(), ShowPathActivity.class);
        intent1.putExtra("no_user", true);
        intent1.putExtra("pinlogitude",midLatLng.longitude);
        intent1.putExtra("pinlatitude",midLatLng.latitude);
        if (neighbourList == null ) {
            neighbourList = new JSONArray();
            intent1.putExtra("no_data",true);
        } else if(neighbourList.length()==0) {
            intent1.putExtra("no_data",true);
        }
        intent1.putExtra(Constants.PUSH_JSON_DATA, neighbourList.toString());


        startActivity(intent1);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {

            mLastLocation = location;
            myLocation.setText(""+mLastLocation.getLatitude()+","+mLastLocation.getLongitude());
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();
            FunduUser.setLocation(location);
            if (adapter != null)
                adapter.onMyLocationUpdate(new LatLng(latitude, longitude));
            Fog.d("Fused", "Fused" + latitude + longitude);
            if (FunduUser.isUserLogin()) {
                if (FunduUser.getAppPreferences().getBoolean(Constants.SettingsPref.SHARE_LOCATION, true)) {
                    try {
                        double coordinates[] = {location.getLongitude(), location.getLatitude()};
                        //double coordinates[] = {location.getLatitude(), location.getLongitude()};
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        Gson gson = gsonBuilder.create();
                        JSONObject locationObject = new JSONObject(gson.toJson(new Contact.Location(coordinates)));
                        if (Utils.isNetworkAvailable(getActivity())){
                            UpdateLocationRequest request = new UpdateLocationRequest(getActivity());
                            request.setLocation(locationObject);
                            request.start();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void onGetNeighborsResponse(JSONArray jcontacts) {
        neighbourList = jcontacts;

        if (neighbourList == null  || neighbourList.length()==0) {
            neighbourList = new JSONArray();
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
                takeAction();
            }

        } else {
            new GetNeighborsParserTask() {
                @Override
                protected void onPostExecute(ArrayList<Neighbour> contacts) {
                    super.onPostExecute(contacts);
                    if(getActivity() == null)
                        return;
                    for (int i = 0; i < contacts.size(); i++) {
                        Fog.d("MapData", "" + contacts.get(i).getPhysical_location() + " " + contacts.get(i).getName());
                    }
                    if (mLastLocation != null) {
                        adapter.getGoogleMap().clear();
                        adapter.onMarkersUpdate(contacts, null);
                        filtercontacts.clear();
//                    neighbourList.;
                        for (int i = 0; i < contacts.size(); i++) {
                            if (contacts.get(i).getContactType().equalsIgnoreCase("AGENT") && contacts.get(i).getBusiness_name() != null) {
                                if (contacts.get(i).getMerchant_img_url() != null)
                                    contacts.get(i).setMerchant_img_url(V1API.BASE_URL + "/v2/customers/getMerchantImage/" + contacts.get(i).getContactId());
                                filtercontacts.add(contacts.get(i));
                                Fog.i("IMAGE MERCHANT", contacts.get(i).getMerchant_img_url() + "");
                            }
                        }
                        mapadapter = new MapSearchAdapter(getContext(), filtercontacts);
                        recyclerViewMap.setAdapter(mapadapter);

                    }
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        takeAction();
                    }

                }
            }.execute(jcontacts.toString());
        }

    }


    @Override
    public void onGetNeighborsError(VolleyError error) {
//        Toast.makeText(getActivity(), "Neighbors request error", Toast.LENGTH_SHORT).show();
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
            takeAction();
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        mSelectedMarker = (Neighbour) marker.getTag();
        selectedLatLong = marker.getPosition();
        if(mSelectedMarker == null)
            mSelectedMarker = new Neighbour();
        String title = marker.getTitle();
        if (title.equalsIgnoreCase("You")){
            return true;
        }

        placeTextView.setText(title.split(";")[0]);
        if (title.split(";")[0].equalsIgnoreCase("Person")) {
            userimg.setImageResource(R.drawable.ic_user);
            placeTextView.setText("Fundu User");
            mnavigateLl.setVisibility(View.GONE);
            mCallLl.setVisibility(View.GONE);

            FunduAnalytics.getInstance(getActivity()).sendAction("Map","FunduUserTap");
        } else if (title.split(";")[0].contains("ATM")) {
            userimg.setImageResource(R.drawable.ic_atm);
            mnavigateLl.setVisibility(View.VISIBLE);
            nav_icon.setImageResource(R.drawable.ic_navigate_atm);
            mCallLl.setVisibility(View.GONE);
            FunduAnalytics.getInstance(getActivity()).sendAction("Map","AtmTap");

        }
        else if(mSelectedMarker.getContactType() != null && mSelectedMarker.getContactType().equalsIgnoreCase("MERCHANT")){
            userimg.setImageResource(R.drawable.merchant);
            mCallLl.setVisibility(View.VISIBLE);
            mnavigateLl.setVisibility(View.VISIBLE);
            FunduAnalytics.getInstance(getActivity()).sendAction("Map","MerchantTap");
        }
        else if (title.split(";")[0].equalsIgnoreCase("Person") || title.equalsIgnoreCase("You")) {
            userimg.setImageResource(R.drawable.ic_user);
            placeTextView.setText("You");
            nav_icon.setVisibility(View.GONE);
            return true;
        } else if (title.split(";")[0].equalsIgnoreCase("You")) {
            return false;
        } else /*if(title.split(";")[0].equalsIgnoreCase("Agent")){*/ {
            if (pref.getString(Constants.COUNTRY_SHORTCODE) == null) {
                userimg.setImageResource(R.drawable.ic_merchant);
            } else if (pref.getString(Constants.COUNTRY_SHORTCODE).equalsIgnoreCase("KEN")) {
                userimg.setImageResource(R.drawable.ic_merchant_ken);
            }
            mSelectedMarker.setBusiness_name(title.split(";")[0]);
            if (title.split(";").length == 2)
                mSelectedMarker.setRating(title.split(";")[1]);
            else
                mSelectedMarker.setRating("0");

            if (marker.getSnippet() != null && !marker.getSnippet().equalsIgnoreCase("")) {
                mSelectedMarker.setContactId(marker.getSnippet().split(";")[0]);
                mSelectedMarker.setPhysical_location(marker.getSnippet().split(";")[1]);
                String dp = marker.getSnippet().split(";")[2];
                if (!(dp.equalsIgnoreCase("No Image"))) {
                    mSelectedMarker.setMerchant_img_url(dp);
                }
                for (int i = 0; i < filtercontacts.size(); i++) {
                    if (marker.getSnippet() != null && marker.getSnippet().contains(";")) {
                        if (filtercontacts.get(i).getMobile().equalsIgnoreCase(marker.getSnippet().split(";")[0])) {
                            mSelectedMarker = filtercontacts.get(i);
                        }
                    }
                }
            }
        }
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
//        LatLngBounds.Builder builder = new LatLngBounds.Builder();
//        builder.include(marker.getPosition());
//        builder.include(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
//        LatLngBounds bounds = builder.build();
//        final CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 50);
//        GoogleDirectionsRequest request = new GoogleDirectionsRequest(makeURL(String.valueOf(marker.getPosition().latitude), String.valueOf(marker.getPosition().longitude)), new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                GoogleMapUtils.drawPath(getActivity(), map, response);
//                // map.animateCamera(cameraUpdate);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//        });
//        FunduApplication.getInstance().addToRequestQueue(request);
        return true;
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    public String makeURL(String destlat, String destlog) {
        return "https://maps.googleapis.com/maps/api/directions/json" + "?origin=" + String.valueOf(mLastLocation.getLatitude()) + "," + String.valueOf(mLastLocation.getLongitude()) + "&destination=" + destlat + "," + destlog + "&sensor=false&mode=walking&alternatives=true&key=" + getResources().getString(R.string.google_map_key_for_server);
    }

    /**
     * Method to display the location
     */
    private void getLocation() {

    }

    private void moveBackToCurrentLocation(){
        if(mLastLocation != null && map != null) {
            neighbourList = null;
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();
            float zoom = this.map.getCameraPosition().zoom;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), zoom);
            map.animateCamera(cameraUpdate);
//            hitNeighboursApi(mLastLocation);
        }
    }

    private void displayLocation() {
//        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            if (shouldShowRequestPermissionRationale(
//                    Manifest.permission.ACCESS_FINE_LOCATION)) {
//                showExplanationDialog(new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        requestPermissions(
//                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
//                                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
//                    }
//                });
//            } else {
//                requestPermissions(
//                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
//                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
//            }
//        } else {

            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (!mRequestingLocationUpdates) {
                startLocationUpdates();
            }
            if (mLastLocation != null) {
                double latitude = mLastLocation.getLatitude();
                double longitude = mLastLocation.getLongitude();
                if (adapter != null) {
                    adapter.onMyLocationUpdate(new LatLng(latitude, longitude));
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15.0f);
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 20.0f));
                    map.animateCamera(cameraUpdate);

                }
                FunduUser.setLocation(mLastLocation);
                Fog.d("MapData", "GetNeighbourService" + "displayLocation");
//                hitNeighboursApi(mLastLocation);
                Fog.d(TAG, "Location Saved in Pref and NearByRequest Started -->" + latitude + ", " + longitude);
            } else {
                Fog.d(TAG, "Location Null by Google Api Client");
            }
//        }
    }

    private boolean checkLocationsPermission() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                showExplanationDialog(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissions(
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                    }
                });
            } else {
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
            return false;
        }
        return true;
    }

    /**
     * Creating google api client object
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
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


    @Override
    public void onStart() {
        super.onStart();
        Fog.d(TAG, "onStart");

        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
            Fog.d(TAG, "onStartConnect");
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (searchItem != null) {
            searchItem.collapseActionView();
            recyclerViewMap.setVisibility(View.GONE);
//            mapadapter.setList(null);
//            mapadapter.notifyDataSetChanged();
        }
        Fog.i("Stop Map", "STOPPED");
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            stopLocationUpdates();
        }
        mGoogleApiClient.disconnect();

    }


    @Override
    public void onPause() {
        super.onPause();
        Fog.i("Stop Map", "Paused");
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

//        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
//            stopLocationUpdates();
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (pref.getString(Constants.ALLOW_WITHDRAW).equalsIgnoreCase("No")) {
            nc.setVisibility(View.GONE);
        } else
            nc.setVisibility(View.VISIBLE);
        Activity parentActivity = getActivity ();
        if(parentActivity != null && parentActivity instanceof HomeActivity){
            ((HomeActivity)parentActivity).showHamburgerIcon ();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        if (checkLocationsPermission()) {
                            displayLocation();
                            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                            hitNeighboursApi(mLastLocation);
                        }

                        Fog.d(TAG, "Activity.RESULT_OK");
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        Fog.d(TAG, "Activity.RESULT_CANCELED");
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Fog.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {
        Fog.i(TAG, "Connected");
        if(mapFragment != null)
            mapFragment.getMapAsync(this);
        if (!mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    /**
     * Creating location request object
     */
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

    /**
     * Starting the location updates
     */
    protected void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            /*if (shouldShowRequestPermissionRationale(
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                showExplanationDialog(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissions(
                                new String[]{ Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                    }
                });
            } else {
                requestPermissions(
                        new String[]{ Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }*/
        } else {
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        mGoogleApiClient, mLocationRequest, this);
                mRequestingLocationUpdates = true;
                Fog.d(TAG, "requestedLocationUpdates");
            }
        }
    }

    /**
     * Stopping location updates
     */
    protected void stopLocationUpdates() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
            mRequestingLocationUpdates = false;
            Fog.d(TAG, "removedLocationUpdates");
        }
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
                            getActivity(),
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

    private void showExplanationDialog(DialogInterface.OnClickListener okListener) {
        dialog.setMessage("You need to allow access to GPS");
        dialog.setPositiveButton("Ok", okListener);
        dialog.setNegativeButton("Cancel", null);
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Fog.d(TAG, "onRequestPermissionsResult");

        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Fog.d(TAG, "Permission Granted");
                    displayLocation();
                    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    hitNeighboursApi(mLastLocation);
                } else {
                    Fog.d(TAG, "Permission Granted");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
    }

    @Override
    public void onMarkerDrag(Marker marker) {
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        mLastLocation.setLatitude(marker.getPosition().latitude);
        mLastLocation.setLongitude(marker.getPosition().longitude);
//        FunduUser.setLocation(mLastLocation);
        updateCamera();
        Fog.d("MapData", "GetNeighbourService" + "onMarkerDragEnd");

        hitNeighboursApi(mLastLocation);
    }



    private void hitNeighboursApi(Location location) {
        if (Utils.isNetworkAvailable(getActivity())) {
            neighbourList = null;
            request = new GetNeighborsRequest(getActivity());
            request.setDistance();
            request.setParserCallback(this);
            request.setLocation(location);
            request.start();
        } else {
            Toast.makeText(getContext(), getString(R.string.no_network), Toast.LENGTH_SHORT).show();
        }
    }

    private void setMyLatLong() {
//        myLatitude = mLastLocation.getLatitude();
//        myLongitude = mLastLocation.getLongitude();
    }

    private void updateCamera() {

        if (focusCamera && selectedLatLong != null) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLong, 18));
            focusCamera = false;
        } else {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 15);
            map.moveCamera(cameraUpdate);
        }
    }

    @Override
    public void onCameraIdle() {
//        if (mLastLocation == null)
//            mLastLocation = new Location("");
//        mLastLocation.setLatitude(cameraPosition.target.latitude);
//        mLastLocation.setLongitude(cameraPosition.target.longitude);
        /*Fog.d("MapData","GetNeighbourService"+"onCameraChange");
        hitNeighboursApi(mLastLocation);*/
        Location centerLocation = new Location("");
        LatLng midLatLng = this.map.getCameraPosition().target;
        centerLocation.setLatitude(midLatLng.latitude);
        centerLocation.setLongitude(midLatLng.longitude);
        hitNeighboursApi(centerLocation);
        HomeActivity activity = (HomeActivity) getActivity();
        activity.setmRequestLocation(midLatLng);
        requestedLocation.setText(""+centerLocation.getLatitude()+","+centerLocation.getLongitude());

    }

    @Override
    public void onCameraMoveStarted(int i) {
        neighbourList = null;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public boolean onQueryTextChange(String query) {
        // Here is where we are going to implement the filter logic
        filterList(query);
        return false;
    }

    private void filterList(String query) {
        ArrayList<Neighbour> filtercontact = new ArrayList<>();

        final ArrayList<Neighbour> contactItemList = filter(query.length() > 0 ? filtercontacts : filtercontact, query);
        Fog.e("Query", query + " F LIST => " + contactItemList.size());
        if (contactItemList.size() > 0) {
            for (int i = 0; i < contactItemList.size(); i++) {
                if (contactItemList.get(i).getContactId().equals(FunduUser.getContactId())) {
                    contactItemList.remove(i);
                }
            }
            if (query.length() > 0) {
                recyclerViewMap.setVisibility(View.VISIBLE);
                mapadapter.setList(contactItemList);
                mapadapter.notifyDataSetChanged();
            } else
                recyclerViewMap.setVisibility(View.GONE);

        } else
            recyclerViewMap.setVisibility(View.GONE);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private static ArrayList<Neighbour> filter(List<Neighbour> models, String query) {
        Fog.e("Query", query + " FILETER2222 => " + models.size());
        final String lowerCaseQuery = query.toLowerCase();

        final ArrayList<Neighbour> filteredModelList = new ArrayList<>();
        for (Neighbour model : models) {
            Fog.e("Query", query + " BN " + model.getBusiness_name());
            final String name = model.getBusiness_name().toLowerCase();
            final String number = model.getMobile().toLowerCase();

            if (name.contains(lowerCaseQuery) || number.startsWith(lowerCaseQuery)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }



    private void openMapIntent() {

        FunduAnalytics.getInstance(getActivity()).sendAction("Map","NavigatetoAtm");
        Location selectedMarkerLocation  = new Location("Selected Location");
        selectedMarkerLocation.setLatitude(selectedLatLong.latitude);
        selectedMarkerLocation.setLongitude(selectedLatLong.longitude);
        Utils.openMapIntent(mLastLocation,selectedMarkerLocation,getActivity());

    }
    private void openMerchantProfile(Neighbour contact){
        FunduAnalytics.getInstance(getActivity()).sendAction("Map","ViewMerchantProfile");
        Intent intent = new Intent(getActivity(), ShowPathActivity.class);
        intent.putExtra("no_user", false);
        intent.putExtra("custid",contact.getId());
        intent.putExtra("logitude",contact.getLocation().coordinates[0]);
        intent.putExtra("latitude",contact.getLocation().coordinates[1]);

        //Pin location
        LatLng midLatLng = this.map.getCameraPosition().target;
        intent.putExtra("pinlogitude",midLatLng.longitude);
        intent.putExtra("pinlatitude",midLatLng.latitude);

        startActivity(intent);
    }


}
