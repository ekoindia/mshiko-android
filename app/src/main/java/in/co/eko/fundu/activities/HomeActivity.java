package in.co.eko.fundu.activities;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.crashlytics.android.Crashlytics;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.co.eko.fundu.FunduApplication;
import in.co.eko.fundu.R;
import in.co.eko.fundu.activities.upi.YesBankUPIClient;
import in.co.eko.fundu.adapters.NavigationDrawerAdapter;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.database.greendao.FunduTransaction;
import in.co.eko.fundu.database.tables.UserAllContactsTable;
import in.co.eko.fundu.database.tables.UserContactsTable;
import in.co.eko.fundu.event.ReplaceFragment;
import in.co.eko.fundu.event.ShowFragment;
import in.co.eko.fundu.fragments.BaseFragment;
import in.co.eko.fundu.fragments.CalendarFragment;
import in.co.eko.fundu.fragments.ChangeFunduPinFragment;
import in.co.eko.fundu.fragments.ContactsFragment;
import in.co.eko.fundu.fragments.EnterAmount;
import in.co.eko.fundu.fragments.HistoryDetailFragment;
import in.co.eko.fundu.fragments.InviteDialogFragment;
import in.co.eko.fundu.fragments.InviteFriends;
import in.co.eko.fundu.fragments.MapFragment;
import in.co.eko.fundu.fragments.MyProfile;
import in.co.eko.fundu.fragments.ResetFunduPin;
import in.co.eko.fundu.fragments.SettingsFragment;
import in.co.eko.fundu.fragments.SupportFragment;
import in.co.eko.fundu.gcm.FunduJobService;
import in.co.eko.fundu.gcm.FunduNotificationManager;
import in.co.eko.fundu.interfaces.NeedCash;
import in.co.eko.fundu.interfaces.OnFragmentInteractionListener;
import in.co.eko.fundu.models.Contact;
import in.co.eko.fundu.models.DynamicMenuItem;
import in.co.eko.fundu.models.FixMenuItem;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.models.SideMenuItem;
import in.co.eko.fundu.requests.CheckBalanceRequest;
import in.co.eko.fundu.requests.FindTransactionPairRequest;
import in.co.eko.fundu.requests.GetCampaignDataRequest;
import in.co.eko.fundu.requests.GetInviteMessage;
import in.co.eko.fundu.requests.GetNeighborsRequest;
import in.co.eko.fundu.requests.GetUserProfileRequest;
import in.co.eko.fundu.requests.HasFundRequest;
import in.co.eko.fundu.requests.LoadWalletRequest;
import in.co.eko.fundu.requests.SaveQRCodeTransactionRequest;
import in.co.eko.fundu.requests.TransactionCommitRequest;
import in.co.eko.fundu.requests.TransactionInitiateRequest;
import in.co.eko.fundu.requests.UpdateCustomer;
import in.co.eko.fundu.services.NearByContactsService;
import in.co.eko.fundu.services.SyncContactsIntentService;
import in.co.eko.fundu.utils.AppPreferences;
import in.co.eko.fundu.utils.DateUtils;
import in.co.eko.fundu.utils.Fog;
import in.co.eko.fundu.utils.FunduAnalytics;
import in.co.eko.fundu.utils.GreenDaoHelper;
import in.co.eko.fundu.utils.PermissionUtil;
import in.co.eko.fundu.utils.SignoutHelper;
import in.co.eko.fundu.utils.UserTransactions;
import in.co.eko.fundu.utils.Utils;
import in.co.eko.fundu.views.ProgressOverlay;
import in.co.eko.fundu.views.slidinguppanel.SlidingUpPanelLayout;

import static in.co.eko.fundu.R.id.fragmentContainer;
import static in.co.eko.fundu.activities.upi.YesBankUPIClient.YES_BANK_CLIENT_ACTION.FETCH_PROFILE;
import static in.co.eko.fundu.activities.upi.YesBankUPIClient.YES_BANK_CLIENT_ACTION.MANAGE_ACCOUNT;
import static in.co.eko.fundu.activities.upi.YesBankUPIClient.YES_BANK_CLIENT_ACTION.SCAN_QR_CODE;


public class HomeActivity extends BaseActivity implements NeedCash, AppCompatDialog.OnDismissListener, OnFragmentInteractionListener, Runnable, NavigationDrawerAdapter.OnNavigationItemClickListener, SlidingUpPanelLayout.PanelSlideListener, FindTransactionPairRequest.OnFindTransactionPairResults, CheckBalanceRequest.OnCheckBalanceResults, LoadWalletRequest.OnLoadWalletResults, FunduNotificationManager.OnPairResult{
    private static final String TAG = "HomeActivity";
    public Toolbar toolbar;
    public static boolean dialogsuccess = false;
    public static String userType = "PERSON",afterAction;
    protected AppPreferences pref;
    int selectedPosition = 1;
    boolean needclicked = false;
    String needCashAmount;
    private RecyclerView mRecyclerView;                           // Declaring RecyclerView
    private NavigationDrawerAdapter mAdapter;                     // Declaring Adapter For Recycler View
    private DrawerLayout drawer;                                  // Declaring DrawerLayout
    private ProgressDialog dialog;
    private AppCompatSeekBar amountSelectorSeekBar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private CheckBalanceRequest checkBalanceRequest;
    private TextView amountstart, amountend;
    private int amountP = 0;
    private JSONArray mLinkedAccountList;
    private ProgressOverlay progressOverLay;
    private LatLng mRequestLocation;
    private ArrayList<Object> sideMenuItems;
    private String actionFromDL;
    private BroadcastReceiver drawerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Fog.e("HOME BROADCAST", "HOME ACTIVITY");
            if (userType == null)
                userType = "PERSON";
//            mAdapter.getTitleIcon(getApplicationContext(), userType);

            if (intent.hasExtra(Constants.UPDATED_AMOUNT) && mAdapter != null) {
                if (FunduUser.isUserMobileVerified())
                    mAdapter.setProfileData(pref.getString(Constants.PROFILE_PIC_URL),
                            FunduUser.getFullName(),
                            intent.getStringExtra(Constants.UPDATED_AMOUNT),FunduUser.getContactId());
            }
            if (intent.hasExtra(Constants.IS_USER_LOGGED_IN) && mAdapter != null) {
                if (!intent.getBooleanExtra(Constants.IS_USER_LOGGED_IN, false))
                    mAdapter.setProfileData("", "", "","");
                else
                    mAdapter.setProfileData(pref.getString(Constants.PROFILE_PIC_URL),
                            FunduUser.getFullName(), intent.getStringExtra(Constants.UPDATED_AMOUNT),
                            FunduUser.getContactId());

            }
            if (intent.hasExtra(Constants.SHOW_PATH_SCREEN_INTENT)) {
                ShowPathActivity.start(HomeActivity.this);
            }
        }
    };
    public void getTitleIcon(Context context) {
        int[] ICONS;
        String[] TITLES;
        Fog.d("CUST_ID_HM", FunduUser.isUserLoginorRegister() + " CNTRY " + FunduUser.getCountryShortName());
        if (FunduUser.isUserLoginorRegister()) {
            if (FunduUser.getCountryShortName().equalsIgnoreCase("KEN")) {
                if (userType.equalsIgnoreCase("AGENT")) {
                    ICONS = Constants.ICONS_DEFAULT;/*ICONS_KEN*/
                    TITLES = context.getResources().getStringArray(R.array.nav_drawer_array_default);/*nav_drawer_array_ken*/
                } else {
                    ICONS = Constants.ICONS_KEN;
                    TITLES = context.getResources().getStringArray(R.array.nav_drawer_array_ken);
                }
            } else if (FunduUser.getCountryShortName().equalsIgnoreCase("IND")) {
                ICONS = Constants.ICONS_IND;
                TITLES = context.getResources().getStringArray(R.array.nav_drawer_array_ind);

            } else {
                ICONS = Constants.ICONS_DEFAULT;
                TITLES = context.getResources().getStringArray(R.array.nav_drawer_array_default);
            }
        } else {
            ICONS = Constants.ICONS_DEFAULT;
            TITLES = context.getResources().getStringArray(R.array.nav_drawer_array_default);
        }
        sideMenuItems.clear();
        FixMenuItem fixItem;
        for(int i=0;i<ICONS.length;i++) {
            fixItem = new FixMenuItem();
            fixItem.setTitle(TITLES[i]);
            fixItem.setIcon(ICONS[i]);
            fixItem.setOnClick("fragment");
            sideMenuItems.add(fixItem);
        }
//        iconsList.clear();
//        for(int i : ICONS) {
//            iconsList.add(i);
//        }
//        titlesList.clear();
//        titlesList.addAll(Arrays.asList(TITLES));
    }

    public static void Signout(final Context context) {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                FirebaseInstanceId instanceID = FirebaseInstanceId.getInstance();
                try {

                    instanceID.deleteToken(context.getString(R.string.gcm_defaultSenderId), FirebaseMessaging.INSTANCE_ID_SCOPE);
                    FunduUser.signOut();
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);

                if (aBoolean) {

                    Intent intent = new Intent(Constants.HOME_ACTIVITY_ACTION);
                    intent.putExtra(Constants.IS_USER_LOGGED_IN, false);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    Toast.makeText(context, "Sign out successfully.", Toast.LENGTH_SHORT).show();
                    context.stopService(new Intent(context, NearByContactsService.class));
                    UserContactsTable.deleteAllContact(context);
                    UserAllContactsTable.deleteAllContact(context);

                } else {
                    Toast.makeText(context, "Sign out unsuccessful. Please check your internet connection and try again.", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();

    }
    public ActionBarDrawerToggle getActionBarDrawerToggle() {
        return actionBarDrawerToggle;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fog.d("onCreate","onCreate");
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        LocalBroadcastManager.getInstance(this).registerReceiver(drawerReceiver, new IntentFilter(Constants.HOME_ACTIVITY_ACTION));
        setContentView(R.layout.activity_home);
        EventBus.getDefault().register(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        pref= FunduUser.getAppPreferences();
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("Please wait...");
        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        mRecyclerView.setHasFixedSize(true);
        sideMenuItems = new ArrayList<>();
        getTitleIcon(getApplicationContext());
        mAdapter = new NavigationDrawerAdapter(this, this, sideMenuItems);
        mRecyclerView.setAdapter(mAdapter);

//        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.cancelAll();
//        notificationManager.getActiveNotifications();
        ArrayList<Integer> list = pref.getListInt(Constants.PushNotificationKeys.EXTRA_NOTIFI);
        if(list!=null && list.size()!=0) {
            NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            for(int id : list) {
                try {
                    notificationManager.cancel(id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            pref.putListInt(Constants.PushNotificationKeys.EXTRA_NOTIFI, null);
        }

        drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.app_name, R.string.app_name){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                Fog.d("onDrawerClosed","onDrawerClosed");
                Fragment f = getSupportFragmentManager().findFragmentById(fragmentContainer);
                if(f instanceof MapFragment) toolbar.setVisibility(View.VISIBLE);
            } };
        drawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        mRecyclerView.post(this);
        if (!FunduUser.isUserLogin()) {
            //User is not logged in
            SignoutHelper.getInstance().callSingoutService(this);
            finish();
            return;
        }

        onIntent(getIntent());
        if(FunduUser.getCountryShortName() != null && FunduUser.getCountryShortName().equalsIgnoreCase("IND")){

            String vpa = FunduUser.getVpa();
            Fog.d("vpa",""+vpa);
            Fog.d("getAccountNo",""+FunduUser.getAccountNo());
            if(vpa == null ||  vpa.length() == 0){
                Fog.d("vpa",""+vpa);
                Intent intent = new Intent(this, UserOnboardingActivity.class);
                intent.putExtra(BaseFragment.FRAGMENT_NAME,"IntroductionToUPI");
                startActivity(intent);
                finish();
                return;
            }
            else if(Constants.upiProvider == Constants.UPI_PROVIDER.ICICI && (FunduUser.getAccountNo() == null || FunduUser.getAccountNo().length() == 0)){
                Intent intent = new Intent(this, LinkAccountActivity.class);
                startActivity(intent);
                finish();
                return;
            }
            if(Constants.upiProvider == Constants.UPI_PROVIDER.YESBANK){
                if(!Constants.dummyUPI)
                    fetchYesBankProfile();
            }
        }

        MapFragment mapFragment = MapFragment.newInstance(MapFragment.MAP_STATE.DEFAULT);
        addFragment(mapFragment,true);
        checkForPendingTransaciton();
        startLocationUpdateTask();
        findViewById(R.id.feedback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onFeedBackClick();
            }
        });

        updateFCMToken();
        getInvitationLink();



        //SmsUpdate.fetchInbox(this);
    }

    private void getCampaignData() {
        GetCampaignDataRequest request = new GetCampaignDataRequest(this);
        request.setParserCallback(new GetCampaignDataRequest.OnGetCampaignDataRequestResult() {
            @Override
            public void onCampaignDataResponse(String response) {
                try {
                    JSONArray array = new JSONArray(response);
                    if (array.length()>0) {
                        DynamicMenuItem item;
                        for(int i=0;i<array.length();i++) {
                            JSONObject obj = array.getJSONObject(i);
                            item = new DynamicMenuItem();
                            item.setTitle(obj.getString("campaign_title"));
                            item.setOnClick(obj.getString("campaign_link"));
                            item.setIcon(obj.getString("icon_link"));
                            if(!sideMenuItems.contains(item)) {
                                sideMenuItems.add(item);
                            }

                        }
                        mAdapter.notifyDataSetChanged();
                        if(actionFromDL != null){
                            actionOnLink(actionFromDL);
                            actionFromDL = null;
                        }
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }

            @Override
            public void onCampaignDataError(VolleyError error) {
                error.printStackTrace();
            }
        });
        request.start();
    }

    private void getInvitationLink(){
//        if(FunduUser.getInvitationLink() != null && FunduUser.getInvitationLink().length() >0)
//            return;
        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://fundu.co.in?source="+FunduUser.getCustomerId()+"&t=invite&a=share"))
                .setDynamicLinkDomain("hn34u.app.goo.gl")
                // Open links with this app on Android
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder()
                        .setMinimumVersion(21)
                        .build())
                .setSocialMetaTagParameters(
                        new DynamicLink.SocialMetaTagParameters.Builder()
                                .setTitle("Fundu - Get Cash Anywhere!")
                                .setDescription("Install now!")
                                .setImageUrl(Uri.parse("https://s31.postimg.cc/3web0okfv/Fundu_Invite.png"))
                                .build())
                .buildDynamicLink();

        Uri dynamicLinkUri = dynamicLink.getUri();
        Fog.wtf("","Invite url "+dynamicLinkUri);

        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(dynamicLinkUri)
                .buildShortDynamicLink()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Fog.logException(e);
                    }
                })

                .addOnCompleteListener(new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            // Short link created
                            Uri shortLink = task.getResult().getShortLink();
                            //Uri flowchartLink = task.getResult().getPreviewLink();
                            FunduUser.setInvitationLink(shortLink.toString());
                        }
                        else{
                            Exception e = task.getException();
                            if(e == null){
                                Date date = new Date();
                                Throwable dynamicLinkError = new Throwable("Dynamic Link Error:"+date.toString());
                                Fog.logException(dynamicLinkError);
                            }
                            else{
                                Fog.logException(e);
                            }

                        }
                    }
                });
    }
    private void updateFCMToken(){

        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }
            @Override
            protected Boolean doInBackground(Void... params) {
                FirebaseInstanceId instanceID = FirebaseInstanceId.getInstance();
                try {

                    String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                            FirebaseMessaging.INSTANCE_ID_SCOPE);
                    String oldToken = pref.getString(Constants.GCM_TOKEN);
                    if(oldToken != null && !oldToken.equalsIgnoreCase(token)){
                        pref.putString(Constants.GCM_TOKEN,token);
                        UpdateCustomer request = new UpdateCustomer(FunduApplication.getAppContext());
                        request.setData(new String[]{"device_token","gsm_sender_id"},new String[]{token,FunduApplication.getAppContext().getString(R.string.gcm_defaultSenderId)});
                        request.start();
                    }
                    Fog.i(TAG,"FCM token: "+token);
                } catch(Exception e) {
                    e.printStackTrace();
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
            }
        }.execute();
    }



    private void testShowPathActivity(){
        String response = "{'push_type':15,'merchantAndAtms':[{'distance':174.12227296955578,'mobile':'9300489836','rating':0.0,'active':true,'contact_id':'9300489836','dummy':false,'i_incentive':0,'contact_type':'MERCHANT','deleted':false,'name':'Eko Cash Point','custid':'383284','location':{'type':'Point','coordinates':[77.0730596,28.456208]},'weightedScore':0.0,'autocashout':false,'list_specific_id':0},{'distance':14.013104436576368,'mobile':'9650930658','rating':0.0,'active':true,'contact_id':'9650930658','dummy':false,'i_incentive':0,'contact_type':'MERCHANT','deleted':false,'name':'Eko Cash Point','custid':'13094157','location':{'type':'Point','coordinates':[77.0718122,28.4550599]},'weightedScore':0.0,'autocashout':false,'list_specific_id':0},{'distance':15.503925616206624,'mobile':'9987536668','rating':0.0,'active':true,'contact_id':'9987536668','dummy':false,'i_incentive':0,'contact_type':'MERCHANT','deleted':false,'name':'Eko Cash Point','custid':'1430573','location':{'type':'Point','coordinates':[77.0718259,28.455052]},'weightedScore':0.0,'autocashout':false,'list_specific_id':0},{'distance':13.295014921080872,'mobile':'9899246466','rating':0.0,'active':true,'contact_id':'9899246466','dummy':false,'i_incentive':0,'contact_type':'MERCHANT','deleted':false,'name':'Eko Cash Point','custid':'1430553','location':{'type':'Point','coordinates':[77.0717951,28.4550584]},'weightedScore':0.0,'autocashout':false,'list_specific_id':0},{'distance':15.780749290322454,'mobile':'9024488000','rating':0.0,'active':true,'contact_id':'9024488000','dummy':false,'i_incentive':0,'contact_type':'MERCHANT','deleted':false,'name':'Eko Cash Point','custid':'694051','location':{'type':'Point','coordinates':[77.0718279,28.4550502]},'weightedScore':0.0,'autocashout':false,'list_specific_id':0},{'distance':503.48184686234447,'rating':0.0,'active':true,'contact_id':'Standard Chartered ATM','dummy':false,'i_incentive':0,'contact_type':'ATM','deleted':false,'name':'Standard Chartered ATM','location':{'type':'Point','coordinates':[77.0750248,28.4516826]},'weightedScore':0.0,'autocashout':false,'list_specific_id':0},{'distance':436.3161268659,'rating':0.0,'active':true,'contact_id':'Yes Bank ATM','dummy':false,'i_incentive':0,'contact_type':'ATM','deleted':false,'name':'Yes Bank ATM','location':{'type':'Point','coordinates':[77.0729793,28.4589319]},'weightedScore':0.0,'autocashout':false,'list_specific_id':0},{'distance':193.83119374152218,'rating':0.0,'active':true,'contact_id':'HDFC Bank ATM','dummy':false,'i_incentive':0,'contact_type':'ATM','deleted':false,'name':'HDFC Bank ATM','location':{'type':'Point','coordinates':[77.069757,28.455321]},'weightedScore':0.0,'autocashout':false,'list_specific_id':0},{'distance':265.1680610115359,'rating':0.0,'active':true,'contact_id':'Axis Bank ATM','dummy':false,'i_incentive':0,'contact_type':'ATM','deleted':false,'name':'Axis Bank ATM','location':{'type':'Point','coordinates':[77.07297899999999,28.457282]},'weightedScore':0.0,'autocashout':false,'list_specific_id':0},{'distance':435.7364509892708,'rating':0.0,'active':true,'contact_id':'Yes Bank ATM','dummy':false,'i_incentive':0,'contact_type':'ATM','deleted':false,'name':'Yes Bank ATM','location':{'type':'Point','coordinates':[77.07298229999999,28.4589257]},'weightedScore':0.0,'autocashout':false,'list_specific_id':0}],'action':'merchants and atms found','request_location':[77.07173176109791,28.455164214622275],'pair_contact_rid':'2xvTU2KBTY'}";
        try{
            JSONObject jData = new JSONObject(response);
            Intent intent1 = new Intent(HomeActivity.this, ShowPathActivity.class);
            intent1.putExtra(Constants.PUSH_JSON_DATA, jData.toString());
            startActivity(intent1);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }


    private void replace(Bitmap bitmap, Bitmap bitmap2, int[] fragmentLoctaion){


        int [] allpixels = new int [bitmap.getHeight() * bitmap.getWidth()];

        int [] allpixels2 = new int [bitmap2.getHeight() * bitmap.getWidth()];

        bitmap.getPixels(allpixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        bitmap2.getPixels(allpixels2, 0, bitmap2.getWidth(), 0, 0, bitmap2.getWidth(), bitmap2.getHeight());

        for(int i = 0; i < allpixels.length; i++)
        {
            if(allpixels[i] == Color.BLACK)
            {
                int k = i+fragmentLoctaion[0]+fragmentLoctaion[1];
                if(k>=allpixels2.length){
                    allpixels[i] = Color.WHITE;
                }
                else
                    allpixels[i] = allpixels2[k];
            }
        }

        bitmap.setPixels(allpixels,0,bitmap.getWidth(),0, 0, bitmap.getWidth(),bitmap.getHeight());

    }
    private void drawViewOnCanvas(Canvas canvas,View v){
        if(!v.isShown() ){
            return;
        }
        if (!(v instanceof ViewGroup)) {
            try {
                if(v instanceof ViewStub || (v.getContentDescription() != null && v.getContentDescription().toString().contains("Google"))){
                    return;
                }
                int[] locationOnScreen = new int[2];
                v.setDrawingCacheEnabled(true);
                if(v.getDrawingCache() == null)
                    return;
                v.getLocationOnScreen(locationOnScreen);
                Bitmap bmp = v.getDrawingCache().copy(Bitmap.Config.ARGB_8888, true);
                RectF rectF = new RectF(locationOnScreen[0], locationOnScreen[1], locationOnScreen[0] + v.getWidth(), locationOnScreen[1] + v.getHeight());
                canvas.drawBitmap(bmp, null, rectF, null);
                v.setDrawingCacheEnabled(false);

            }
            catch(Exception e){
                e.printStackTrace();
            }
            return;
        }
        ViewGroup viewGroup = (ViewGroup) v;
        if(v.getBackground() != null && v instanceof RelativeLayout){
            int[] locationOnScreen = new int[2];
            v.getLocationOnScreen(locationOnScreen);
            Bitmap bmp = bitmapToDrawable(v.getBackground());
            if(bmp != null){
                RectF rectF = new RectF(locationOnScreen[0], locationOnScreen[1], locationOnScreen[0] + v.getWidth(), locationOnScreen[1] + v.getHeight());
                canvas.drawBitmap(bmp, null, rectF, null);
            }
        }
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            //Do not add any parents, just add child elements
            drawViewOnCanvas(canvas,child);
        }
    }
    private Bitmap bitmapToDrawable(Drawable drawable1){
        Drawable drawable = drawable1.getConstantState().newDrawable();
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            return null;
        } else {

            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
    private Bitmap drawViewsonMap(Bitmap mapScreenshot, View rootView,int[] fragmentLoctaion){
        Bitmap bmOverlay = Bitmap.createBitmap(
                mapScreenshot.getWidth(), mapScreenshot.getHeight()+fragmentLoctaion[1],
                mapScreenshot.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(mapScreenshot,null,new RectF(fragmentLoctaion[0],fragmentLoctaion[1],mapScreenshot.getWidth(), mapScreenshot.getHeight()+fragmentLoctaion[1]),null);
        drawViewOnCanvas(canvas,rootView);
        return bmOverlay;
    }
    private void onFeedBackClick(){
        //Take screen shot

        Fragment f = getSupportFragmentManager().findFragmentById(fragmentContainer);
        String whichFragment = f.getClass().getSimpleName();
        FunduAnalytics.getInstance(HomeActivity.this).sendAction("Feedback",whichFragment);
        if(f instanceof MapFragment){
            MapFragment fragment = (MapFragment)f;
            GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {

                @Override
                public void onSnapshotReady(Bitmap snapshot) {
                    View v = getWindow().getDecorView().getRootView();
                    v.setDrawingCacheEnabled(true);
                    View frameContainer = findViewById(R.id.fragmentContainer);
                    int windowLocation[] = new int[2];
                    frameContainer.getLocationOnScreen(windowLocation);
                    Bitmap bmOverlay = drawViewsonMap(snapshot,v,windowLocation);
                    v.setDrawingCacheEnabled(false);
                    Utils.takeFeedback(bmOverlay,HomeActivity.this);

                }
            };
            fragment.getMap().snapshot(callback);
            return;
        }
        View v = getWindow().getDecorView().getRootView();
        v.setDrawingCacheEnabled(true);
        Bitmap bmp = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false);
        Utils.takeFeedback(bmp,this);

    }

    private void startLocationUpdateTask(){
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(FunduApplication.getAppContext()));

        Job myJob = dispatcher.newJobBuilder()
                // the JobService that will be called
                .setService(FunduJobService.class)
                // uniquely identifies the job
                .setTag(FunduJobService.SEND_LOCATION)
                // repeating job
                .setRecurring(true)
                // don't persist past a device reboot
                .setLifetime(Lifetime.FOREVER)
                // start between 0 and 60 seconds from now
                .setTrigger(Trigger.executionWindow(0,15))
                // overwrite an existing job with the same tag
                .setReplaceCurrent(true)
                // retry with Linear backoff
                .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                // constraints that need to be satisfied for the job to run
                .setConstraints(
                        // run on any network
                        Constraint.ON_ANY_NETWORK
                )
                .build();
        dispatcher.mustSchedule(myJob);

    }

    @Override
    protected void onResume() {
        super.onResume();

        userType = pref.getString(Constants.CONTACT_TYPE_PA);


        if (pref.getString(Constants.ALLOW_WITHDRAW).equalsIgnoreCase("Yes")) {
            if (needclicked && FunduUser.isUserLoginorRegister()) {
                TransactionCommitRequest.needCash = true;
                enterAmountScreen();
                /*layout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                amountBox.setSelection(amountBox.getText().length());*/
                needclicked = false;
            }
        }
        Utils.hideSoftKeyboard(this);
        if (FunduUser.isUserLogin()) {

            mAdapter.setProfileData(pref.getString(Constants.PROFILE_PIC_URL), FunduUser.getFullName(), FunduUser.getWalletAmount(),FunduUser.getUser().getContactId());
            Fog.d("Mobile","Mobile"+FunduUser.getUser().getContactId()+FunduUser.getUser()
                    .getCountryCode());
        }
//        getTitleIcon(this);
//        mAdapter.getTitleIcon(getApplicationContext(), userType);
//        mRecyclerView.setAdapter(mAdapter);
        getCampaignData();
        sync();
    }

    private boolean  checkForPendingTransaciton(){
        FunduTransaction funduTransaction = GreenDaoHelper.getInstance(this).getPendingTransaction();
        if(funduTransaction != null && (funduTransaction.getState() == null || funduTransaction.getProvider() == null || funduTransaction.getProvider().length() == 0)){
            if(funduTransaction.getTid()==null || funduTransaction.getTid().length()==0) {
                Fog.logEvent(false, funduTransaction.getPairRequestId(), "HomeActivity","checkForPendingTransaciton", "nullStateORproviderNull", DateUtils.getCurrentUTCtime(), Constants.getState(funduTransaction.getState()));
            } else {
                Fog.logEvent(true, funduTransaction.getTid(), "HomeActivity","checkForPendingTransaciton", "nullStateORproviderNull", DateUtils.getCurrentUTCtime(), Constants.getState(funduTransaction.getState()));
            }
            GreenDaoHelper.getInstance(this).deleteFunduTransaction(funduTransaction.getId());
            checkForPendingTransaciton();
            return false;
        }
        if(funduTransaction == null){
            return false;
        }
        if(funduTransaction.getState() == Constants.TRANSACTION_STATE.SEEKER_INITIATED.getCode()
                || funduTransaction.getState() == Constants.TRANSACTION_STATE.SEEKER_ACCOUNT_DEBITED.getCode()){
            Intent intent = new Intent();
            intent.setClass(this,PairContactFoundActivity.class);
            intent.putExtra(Constants.FUNDU_TRANSACTION_ID,funduTransaction.getId());
            startActivity(intent);
            return true;
        }
        else if(funduTransaction.getState() == Constants.TRANSACTION_STATE.PROVIDER_ACCEPTED.getCode() || funduTransaction.getState() ==
                Constants.TRANSACTION_STATE.PROVIDER_VERIFY_CODE.getCode()){
            Intent intent = new Intent();
            intent.setClass(this,GiveCash.class);
            intent.putExtra(Constants.FUNDU_TRANSACTION_ID,funduTransaction.getId());
            startActivity(intent);
            return true;
        }
        else if(funduTransaction.getState() == Constants.TRANSACTION_STATE.RATING_PENDING.getCode()){
            Intent intent = new Intent();
            if(funduTransaction.getProvider().equalsIgnoreCase(FunduUser.getContactId())){
                intent.setClass(this,GiveCash.class);
            }
            else if(funduTransaction.getSeeker().equalsIgnoreCase(FunduUser.getContactId())){
                intent.setClass(this,PairContactFoundActivity.class);
            }
            intent.putExtra(Constants.FUNDU_TRANSACTION_ID,funduTransaction.getId());
            startActivity(intent);
            return true;
        }
        return false;
    }

    private void sync(){

        //Check permissions
        if(getIntent().getAction() != null && getIntent().getAction().equalsIgnoreCase(Constants.DONT_CHECK_PERMISSION_ACTION)){
            Fog.i(TAG,"Dont check permission");
        }
        else if (!PermissionUtil.hasSelfPermission(this, PermissionsActivity.mPermissions)) {
            Intent intent = new Intent(this,PermissionsActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        //check if user is logged in and verified
        if(FunduUser.getUser() != null && FunduUser.isUserMobileVerified()){


            GetInviteMessage inviteMessage = new GetInviteMessage(this);
            inviteMessage.setParserCallback(new GetInviteMessage.OnInviteMessageResult() {
                @Override
                public void onInviteMessage(JSONObject response) {
                    try {
                        FunduUser.setInvitationMessage(response.toString());
                    }
                    catch(Exception e){
                        Crashlytics.logException(e);
                    }
                }

                @Override
                public void onInviteMessageError(VolleyError error) {

                }
            });
            inviteMessage.start();

            GetUserProfileRequest myProfile = new GetUserProfileRequest(this);
            myProfile.setParserCallback(new GetUserProfileRequest.OnUserProfileRequestResult() {
                @Override
                public void onUserProfileResponse(JSONObject response) {
                    try{
                        Fog.d("PRofileRespponse","**********"+response);
                        //int totalIncetives = response.optInt("i_incentive");
                        double rating  = response.optDouble("rating");
                        FunduUser.setRating(rating);
                        if(mAdapter != null)
                            mAdapter.notifyItemChanged(0);
//                        FunduUser.setTotalIncentiveFromInvitaion(totalIncetives);
////                        DataUpdated event = new DataUpdated();
////                        event.type = DataUpdated.DataUpdatedType.InvitationIncentive;
////                        EventBus.getDefault().post(event);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                        Fog.logException(e);
                    }
                }

                @Override
                public void onUserProfileError(VolleyError error) {

                }
            });
            myProfile.start();
            //startService(new Intent(this, NearByContactsService.class));
            SyncContactsIntentService.startService(this,Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
        }
        else
            Fog.i(TAG,"User is not logged in, no point updating");
    }


    private void onIntent(final Intent intent) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(intent.getIntExtra(Constants.NOTIFICATION_ID, -1));

        if (intent.hasExtra(Constants.PUSH_TYPE)) {
            //Fog.e("NOTIFICATION", intent.getStringArrayListExtra(Constants.ALERT).toString()+" ALERT TYPE : "+intent.getIntExtra(Constants.PUSH_TYPE, -1));
            hideProgressOverlay();
            if (intent.getIntExtra(Constants.PUSH_TYPE, -1) == Constants.PUSH_TYPE_ENUM.CREDIT_SUCCESS.getCode())
            {
                CalendarFragment calendarFragment = new CalendarFragment();
                addFragment(calendarFragment,false);
                return;
            }
            JSONObject jData = null;
            final ArrayList<String> strings = intent.getStringArrayListExtra(Constants.ALERT);
            try{
                jData = new JSONObject(intent.getStringExtra(Constants.PUSH_JSON_DATA));
            }
            catch (JSONException e){
                e.printStackTrace();
            }
            if(intent.getIntExtra(Constants.PUSH_TYPE, -1) == Constants.PUSH_TYPE_ENUM.PAIR_FOUND.getCode()){
                initiateTransaction(strings,jData);
            }
            else if (intent.getIntExtra(Constants.PUSH_TYPE, -1) == Constants.PUSH_TYPE_ENUM.NEEDCASH_TRANSACTION_COMPLETED.getCode() && FunduUser.getUser() != null) {
                Thread splashTread = new Thread() {
                    @Override
                    public void run() {
                        try {

                            sleep(1);
                        } catch (InterruptedException e) {
                            // do nothing
                        } finally {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    // Intent intent1 = new Intent(HomeActivity.this, GiveCash.class);
                                    // AppPreferences.getInstance(HomeActivity.this).clear();
                                    // AppPreferences.getInstance(HomeActivity.this).setArrayList(HomeActivity.this,"Arraylist",strings);
                                    Intent intent1 = new Intent(HomeActivity.this, PairContactFoundActivity.class);
                                    intent1.putStringArrayListExtra(Constants.ALERT, strings);
                                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent1.putExtras(intent);
                                    startActivity(intent1);
                                }
                            });

                        }
                    }
                };
                splashTread.start();

            }
            else if (intent.getIntExtra(Constants.PUSH_TYPE, -1) == Constants.PUSH_TYPE_ENUM.VERIFY_TRANSACTION_CODE.getCode() && FunduUser.getUser() != null) {
                Thread splashTread = new Thread() {
                    @Override
                    public void run() {
                        try {

                            sleep(1);
                        } catch (InterruptedException e) {
                            // do nothing
                        } finally {
                            runOnUiThread(new Runnable() {
                                public void run() {

                                    Intent intent1 = new Intent(HomeActivity.this, GiveCash.class);
                                    intent1.putExtras(intent);
                                    intent1.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    startActivity(intent1);
                                }
                            });

                        }
                    }
                };
                splashTread.start();
            }
            else if (intent.getIntExtra(Constants.PUSH_TYPE, -1) == Constants.PUSH_TYPE_ENUM.NO_PAIR_FOUND.getCode()) {
                if (FunduUser.getCountryShortName().equalsIgnoreCase("KEN")){
                    Toast.makeText(HomeActivity.this, "Sorry "+FunduUser.getFullName()
                            +", we didn't get someone to assist you. Try Again Later", Toast.LENGTH_SHORT).show();

                }
                else
                    Toast.makeText(HomeActivity.this, "No Transaction Pair Found for this request", Toast.LENGTH_SHORT).show();

            }
            else if (intent.getIntExtra(Constants.PUSH_TYPE, -1) == Constants.PUSH_TYPE_ENUM.W2W_TRANSACTION_COMPLETED.getCode()) {
                Intent intent1 = new Intent(HomeActivity.this, MoneyToAccountSuccess.class);
                intent1.putExtra("caller","HomeActivity");
                intent1.putExtra(Constants.ACCOUNT_NUMBER,strings.get(9));
                intent1.putExtra(Constants.AMOUNT,Float.parseFloat(strings.get(1)));
                intent1.putExtra(Constants.RATING_TYPE, 2);
                startActivity(intent1);

            }
            else if (intent.getIntExtra(Constants.PUSH_TYPE, -1) == Constants.PUSH_TYPE_ENUM.MERCHANT_ATM_FOUND.getCode()) {
                //hide everything and show map fragment

                Intent intent1 = new Intent(HomeActivity.this, ShowPathActivity.class);
                intent1.putExtra(Constants.PUSH_JSON_DATA, jData.toString());
                startActivity(intent1);
                hideProgressOverlay();
            }
            else if (intent.getIntExtra(Constants.PUSH_TYPE, -1) == Constants.PUSH_TYPE_ENUM.SHOP_CLOSED.getCode())
                Utils.showShortToast(getApplicationContext(), Constants.SHOP_CLOSED);

        }
        processDynamicLink();

    }

    public void onChangeFunduPin(){
        if (userType.equalsIgnoreCase("AGENT")) {
            Fragment fragment = new SettingsFragment();
            addFragment(fragment,true);
        } else {
            Fragment fragment = new ChangeFunduPinFragment();
            addFragment(fragment,true);
        }
    }
    public void onForgotFunduPin(){
        Fragment fragment = new ResetFunduPin();
        addFragment(fragment,true);
    }
    public void onInviteFriends(){
        Fragment fragment = new ContactsFragment();
        addFragment(fragment,true);
    }
    public void displayView(Object item) {
        Utils.hideSoftKeyboard(this);

        try{
            hideHamburgerIcon ();

            if(item instanceof FixMenuItem) {
                FixMenuItem item1 = (FixMenuItem)item;
                String whichItem = item1.getTitle();
                showFragment(whichItem);
            } else if (item instanceof DynamicMenuItem){
                DynamicMenuItem item1 = (DynamicMenuItem)item;
                String url = item1.getOnClick();
                Intent intent  = new Intent(this, WebViewActivity.class);
                intent.putExtra("url", url);
                startActivity(intent);
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    private void showFragment(String whichItem){
        Fragment fragment;
        switch (whichItem) {
            case "Get Cash from Friends":
                if(checkForPendingTransaciton()){
                    //There is a pending transaction, finish that first.
                    return;
                }
                startActivity(new Intent(HomeActivity.this,GetCashFromContact.class));
                break;
            case "Scan and Get Cash":
                showQRCodePopup();
                break;
            case "Find ATM/Cash Point":
                showNearestCashPointAndAtms();
                break;
            case "Manage Account":
                manageVPAAccount();
                break;
            case "":

                if (FunduUser.getUser() == null) {
                    startActivity(new Intent(HomeActivity.this,Tutorial.class));
                    finish();
                } else if (!FunduUser.isUserMobileVerified()) {
                    String androidId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
                    Contact contact = new Contact();
                    contact.setName(pref.getString(Constants.NAME));
                    contact.setDeviceId(androidId);
                    contact.setDeviceToken(pref.getString(Constants.GCM_TOKEN));
                    Intent intent = new Intent(this, UserOnboardingActivity.class);
                    intent.putExtra(Contact.class.getSimpleName(), contact);
                    startActivity(intent);
                } else {
                    //  startActivity(new Intent(this, MyProfile.class));
                }
                break;

            case "Link Account":
                startActivity(new Intent(this, LinkAccountActivity.class));
                break;

            case "Change PIN":

                if (FunduUser.getUser() != null) {
                    if (FunduUser.getCountryShortName().equalsIgnoreCase("KEN")) {
                        if (userType.equalsIgnoreCase("AGENT")) {
                            fragment = new SettingsFragment();
                            addFragment(fragment,true);
                        } else {

                            fragment = new ChangeFunduPinFragment();
                            addFragment(fragment,true);
                        }
                    }

                } else {
                    if (Utils.isNetworkAvailable(getApplicationContext()))
                        startActivity(new Intent(HomeActivity.this,Tutorial.class));
                    finish();
                }
                break;

            case "Forgot PIN":

                if (FunduUser.getUser() != null) {
                    {
                        fragment = new ResetFunduPin();
                        addFragment(fragment,true);
                    }

                } else
                    fragment = new ContactsFragment();
                addFragment(fragment,true);
                break;

            case "History":

                CalendarFragment calendarFragment = new CalendarFragment();

                if (FunduUser.getContactId()!=null) {
                    addFragment(calendarFragment,true);
                }

                break;
            case "Invite Friends":

//                        fragment = new ContactsFragment();
//                        addFragment(fragment,true);
                showInviteScreen();


                break;
            case "Settings":

                if (FunduUser.getUser() == null) {
                    startActivity(new Intent(HomeActivity.this,Tutorial.class));
                    finish();
                } else if (!FunduUser.isUserMobileVerified()) {
                    String androidId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
                    Contact contact = new Contact();
                    contact.setName(pref.getString(Constants.NAME));
                    contact.setDeviceId(androidId);
                    contact.setDeviceToken(pref.getString(Constants.GCM_TOKEN));
                    Intent intent = new Intent(this, UserOnboardingActivity.class);
                    intent.putExtra(Contact.class.getSimpleName(), contact);
                    startActivity(intent);
                } else {
                    fragment = new SettingsFragment();
                    addFragment(fragment,true);
                }
                break;
            case "Support":

                fragment = new SupportFragment();
                addFragment(fragment,true);
                break;
            default:
                break;
        }
    }
    private void showNearestCashPointAndAtms(){
        dialog.setMessage("Fetching..");
        dialog.show();
        GetNeighborsRequest request = new GetNeighborsRequest(this);
        Location location  = new Location("");
        location.setLongitude(FunduUser.getLongitude());
        location.setLatitude(FunduUser.getLatitude());
        request.setData(location,"atm+cashpoint",true);
        request.setParserCallback(new GetNeighborsRequest.OnGetNeighborsResults() {
            @Override
            public void onGetNeighborsResponse(JSONArray contacts) {

                try {
                    dialog.dismiss();
                    JSONObject object = new JSONObject();
                    object.put(Constants.PushNotificationKeys.MERCHANT_ATM,contacts);
                    object.put(Constants.PushNotificationKeys.ACTION,"nearby cashpoints atm");
                    JSONArray location = new JSONArray();
                    location.put(FunduUser.getLongitude());
                    location.put(FunduUser.getLatitude());
                    object.put(Constants.PushNotificationKeys.REQUEST_LOCATION,location);
                    Intent intent1 = new Intent(HomeActivity.this, ShowPathActivity.class);
                    intent1.putExtra(Constants.PUSH_JSON_DATA, object.toString());

                    startActivity(intent1);
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onGetNeighborsError(VolleyError error) {
                dialog.dismiss();
            }
        });
        request.start();
    }
    private void showQRCodePopup(){
        if(pref.getBoolean(Constants.DONT_SHOW_QRCODE_POPUP,false)){
            scanQRCode();
        }
        else{
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.overlay_scan_qrcode, null);
            TextView tv = (TextView)dialogView.findViewById(R.id.desc);
            tv.setText(R.string.sqr_desc);
            dialogBuilder.setView(dialogView);
            final Dialog upiDialog = dialogBuilder.show();
            upiDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT);
            Button iUnderstand = (Button)dialogView.findViewById(R.id.button);
            final CheckBox dontShow = (CheckBox)dialogView.findViewById(R.id.dontshowagain);
            iUnderstand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    scanQRCode();
                    upiDialog.dismiss();
                    if(dontShow.isChecked())
                        pref.putBoolean(Constants.DONT_SHOW_QRCODE_POPUP,true);
                }
            });
        }

    }
    private void scanQRCode(){

        Intent intent = new Intent(this, YesBankUPIClient.class);
        intent.putExtra ( "action",SCAN_QR_CODE);
        startActivityForResult(intent,QR_CODE_TRANSACTION);
    }

    private void manageVPAAccount(){

        Intent intent = new Intent(this, YesBankUPIClient.class);
        intent.putExtra ( "action",MANAGE_ACCOUNT);
        startActivityForResult(intent,MANAGE_ACCOUNT_T);
    }
    private void fetchYesBankProfile(){
        Intent intent = new Intent(this, YesBankUPIClient.class);
        intent.putExtra ( "action",FETCH_PROFILE);
        intent.putExtra("defaultAccount",true);
        startActivityForResult(intent,FETCH_YESBANK_PROFILE);
    }

    @Override
    public void onBackPressed() {
        try {

            if(progressOverLay != null && progressOverLay.getVisibility() == View.VISIBLE){
                return;
            }

            Fog.d("onBackPressed","onBackPressed");
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment f = getSupportFragmentManager().findFragmentById(fragmentContainer);
            if(f instanceof BaseFragment && ((BaseFragment)f).onBackPressed()){
                return;
            }
            if(fragmentManager.getBackStackEntryCount()==1){
                finish();
            }
            else if(fragmentManager.getBackStackEntryCount()>1){
                fragmentManager.popBackStackImmediate();
            }
            Utils.hideSoftKeyboard(this);

        } catch (NullPointerException e) {
            super.onBackPressed();
        }
    }

    @Override
    public void run() {
        Resources r = getResources();
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        float screenWidth = width / r.getDisplayMetrics().density;
        float navWidth = screenWidth - 56;
        navWidth = Math.min(navWidth, 320);
        int newWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, navWidth, r.getDisplayMetrics());
        DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) mRecyclerView.getLayoutParams();
        params.width = newWidth;
        mRecyclerView.setLayoutParams(params);
    }

    @Override
    public void onClickNavItem(final Object item) {
        drawer.closeDrawers();

        getWindow().getDecorView().getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                Fog.d("OnDrawerClose","position"+item.getTitle());
                String whichItem = ((SideMenuItem)item).getTitle();
                FunduAnalytics.getInstance(HomeActivity.this).sendAction("SideMenu",whichItem);
                displayView(item);
            }
        }, 200);
    }

    @Override
    public void onClickHeader() {
        drawer.closeDrawers();
//        getWindow().getDecorView().getHandler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                addFragment(MyProfile.newInstance(),true);
//            }
//        }, 200);
        addFragment(MyProfile.newInstance(),true);
    }

    @Override
    public void onFragmentInteraction(Bundle bundle) {

        String extras = bundle.getString("needHelp");
        if(extras.equalsIgnoreCase("needHelp")){
            SupportFragment supportFragment = SupportFragment.newInstance(bundle);
            addFragment(supportFragment,true);
        } else if(extras.equalsIgnoreCase("no")) {
            HistoryDetailFragment historyDetailFragment = new HistoryDetailFragment();
            historyDetailFragment.setArguments(bundle);
            addFragment(historyDetailFragment, true);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
//        if (id == R.id.action_search) {
//            Intent intent = new Intent(this, SearchActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//            startActivityForResult(intent, 5);
//        }
        if (id == R.id.action_database) {
            //DatabaseManager.start(this);
        }
        return super.onOptionsItemSelected(item);
    }

    private final int FETCH_YESBANK_PROFILE = 1;
    private final int QR_CODE_TRANSACTION =2;
    private final int MANAGE_ACCOUNT_T = 3;
    private final int TRANSACTION_STATUS_ACTION = 4;

    private void showInviteScreen(){
        InviteDialogFragment dialog = new InviteDialogFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        dialog.show(ft, InviteDialogFragment.TAG);

    }
    //private final int REQUEST_INVITE = 4;
    private void processDynamicLink(){
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                            String type = deepLink.getQueryParameter("t");
                            String a = deepLink.getQueryParameter("a");
                            if(type == null)
                                return;
                            switch(type){
                                case "invite":
                                    FunduAnalytics.getInstance(HomeActivity.this).sendAction("UserRetention","invite",a,1);
                                    if(a != null && a.equalsIgnoreCase("share")){
                                        actionFromDL = a;
                                        actionOnLink(a);
                                    }
                                    break;
                                case "fpn":
                                    FunduAnalytics.getInstance(HomeActivity.this).sendAction("UserRetention","fpn",a,1);
                                    if(a != null){
                                        actionFromDL = a;
                                        actionOnLink(a);
                                    }
                                    break;
                                case "facebook" :
                                    FunduAnalytics.getInstance(HomeActivity.this).sendAction("UserRetention", "facebook", a,1);
                                    break;
                                case "instagram" :
                                    FunduAnalytics.getInstance(HomeActivity.this).sendAction("UserRetention", "instagram",a, 1);
                                    break;
                                default:
                                    FunduAnalytics.getInstance(HomeActivity.this).sendAction("UserRetention", "default",a,1);
                                    break;

                            }

                        }

                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("", "getDynamicLink:onFailure", e);
                    }
                });
    }

    private void actionOnLink(String action){

        if(action.equalsIgnoreCase("transactions")){
            action = "History";
        }
        SideMenuItem item = new SideMenuItem();
        item.setTitle(action);
        int index = sideMenuItems.indexOf(item);

        if(index >= 0){
            actionFromDL = null;
            final Object sideMenuItem = sideMenuItems.get(index);

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    displayView(sideMenuItem);
                }
            }, 1000);
        }


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK){
            switch(requestCode){
                case FETCH_YESBANK_PROFILE:
                    if(data != null){
                        Bundle bundle = data.getExtras();
                        String statusCode = bundle.getString("status");
                        String statusDesc = bundle.getString("statusDesc");
                        if(statusCode != null && statusCode.equalsIgnoreCase("S")) {
                            String defAcc = bundle.getString("defAcc");
                            pref.putString(Constants.PROFILE_DATA, defAcc);
                            pref.putString(Constants.LINKED_ACC, bundle.getString("accList"));
                            try {
                                JSONObject jDefAcc = new JSONObject(defAcc);
                                String accno = "", bankname = "", recipientId = "", ifsc = "";
                                accno = jDefAcc.getString("accountNumber");
                                bankname = jDefAcc.getString("bankName");
                                recipientId = jDefAcc.getString("accountId");
                                ifsc = jDefAcc.getString("ifscCode");
                                FunduUser.setAccountNo(accno);
                                FunduUser.setIFSC(ifsc);
                                FunduUser.setRecipientId(recipientId);
                                FunduUser.setBankName(bankname);
                                JSONArray jsonArray = new JSONArray(bundle.getString("accList"));
                                setmLinkedAccountList(jsonArray);
                                String name = jDefAcc.getString("accountName");
                                if (!FunduUser.getFullName().equalsIgnoreCase(name)) {
                                    name = Utils.toCamelCase(name);
                                    FunduUser.saveFullName(name);
                                    UpdateCustomer request = new UpdateCustomer(HomeActivity.this);
                                    request.setData(new String[]{"name"}, new String[]{name});
                                    request.start();
                                }

                            } catch(JSONException e) {
                                e.printStackTrace();
                            } catch(NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                        else if(statusCode != null && statusCode.equalsIgnoreCase("MC09")){
                            FunduUser.setRecipientId("");
                            FunduUser.setVpa("");
                            FunduUser.setAccountNo("");
                            FunduUser.setIFSC("");
                            UpdateCustomer request = new UpdateCustomer(HomeActivity.this);
                            request.setData(new String[]{"vpa","accno","ifsc","recipient_id"},new String[]{"","","",""});
                            request.start();
                            finish();
                            Intent intent = new Intent(HomeActivity.this,HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);

                        }
                    }
                    break;
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
                            SaveQRCodeTransactionRequest request = new SaveQRCodeTransactionRequest(HomeActivity.this);
                            request.setData(orderNo,txnAmount,status);
                            request.start();
                            if(statusCode !=null){
                                if(statusCode.equalsIgnoreCase("S")){
                                    //show payment successful

                                    FunduAnalytics.getInstance(HomeActivity.this).sendAction("QRTransaction","Success",(int)Double.parseDouble(txnAmount));

                                }
                                Intent intent = new Intent(HomeActivity.this,TransactionStatusActivity.class);
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
                case MANAGE_ACCOUNT_T:
//                    fetchYesBankProfile();
                    break;
                case TRANSACTION_STATUS_ACTION:
                {
                    String action = data.getAction();
                    if(action != null && action.equalsIgnoreCase("retry")){
                        scanQRCode();
                    }

                }
                break;
            }
        }
        else{
            switch(requestCode)
            {
                case MANAGE_ACCOUNT_T:
//                    fetchYesBankProfile();
                    break;
                case FETCH_YESBANK_PROFILE :
                    String profile = pref.getString(Constants.PROFILE_DATA);
                    String linkedAcc = pref.getString(Constants.LINKED_ACC);
                    try {
                        JSONObject jDefAcc = new JSONObject(profile);
                        String accno = "", bankname = "", recipientId = "", ifsc = "";
                        accno = jDefAcc.getString("accountNumber");
                        bankname = jDefAcc.getString("bankName");
                        recipientId = jDefAcc.getString("accountId");
                        ifsc = jDefAcc.getString("ifscCode");
                        FunduUser.setAccountNo(accno);
                        FunduUser.setIFSC(ifsc);
                        FunduUser.setRecipientId(recipientId);
                        FunduUser.setBankName(bankname);
                        JSONArray jsonArray = new JSONArray(linkedAcc);
                        setmLinkedAccountList(jsonArray);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            }
        }
    }
    @Override
    public void onClickNeedCash(View progressOverLay) {
        this.progressOverLay = (ProgressOverlay) progressOverLay;
        if (FunduUser.getUser() == null) {
            if (progressOverLay != null)
                needclicked = true;
        }
        else if (!FunduUser.isUserMobileVerified()) {
            String androidId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
            Contact contact = new Contact();
            contact.setName(pref.getString(Constants.NAME));
            contact.setDeviceId(androidId);
            contact.setDeviceToken(pref.getString(Constants.GCM_TOKEN));
            Intent intent = new Intent(this, UserOnboardingActivity.class);
            intent.putExtra(Contact.class.getSimpleName(), contact);
            startActivity(intent);
            if (progressOverLay != null)
                needclicked = true;
        } else if (FunduUser.getLatitude() ==0 && FunduUser.getLongitude() == 0) {
            Toast.makeText(this, "Please turn on your GPS first.", Toast.LENGTH_SHORT).show();
        } else {
            //Show enter amount fragment
            enterAmountScreen();
            TransactionCommitRequest.needCash = true;
        }
    }

    @Override
    public void onDestroyMapFragment() {

    }
    public void setProgressOverLay(ProgressOverlay progressOverLay) {

        this.progressOverLay = progressOverLay;
    }


    private void checkWallet(double amount) {
        dialog.setMessage(getString(R.string.text_check_balance));
        dialog.show();
        JSONObject object = new JSONObject();
        try {

            object.put("custid", FunduUser.getCustomerId());
            object.put("mobile", pref.getString(Constants.PrefKey.CONTACT_NUMBER));
            object.put("country_shortname", pref.getString(Constants.COUNTRY_SHORTCODE));
            object.put("amount", String.valueOf(amount));
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
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Fog.d("onHasFundResponse",""+response);
//                    needCashAmount = amountBox.getText().toString().trim();
                    String walletAmount = jsonObject.optString("Balance Amount");
                    if (jsonObject.has("Balance Amount")) {
                        progressOverLay.setTime(150000, 150);

                        if (walletAmount.length() == 0 || walletAmount.equals("0.0") || walletAmount.equals("0.00")) {
                            float currentAmount = Float.parseFloat(walletAmount);
                            //  float requestedAmount = Float.parseFloat(amountBox.getText().toString().trim());
                            float requestedAmount = Float.parseFloat(needCashAmount);

                            if(pref.getString(Constants.COUNTRY_SHORTCODE).equalsIgnoreCase("IND")){
                                currentAmount = 10000;
                            }

                            if (requestedAmount <= currentAmount) {
                                // Start Find pair request here
                                FunduNotificationManager.setOnPairResult(HomeActivity.this);
                                startFindTransactionPairRequest(needCashAmount,"");
                            } else {
                                Toast.makeText(HomeActivity.this, "Insufficient Amount. Please load your wallet first.", Toast.LENGTH_SHORT).show();
                                float neededAmount = requestedAmount - currentAmount;


                            }
                        } else {
                            float currentAmount = Float.parseFloat(walletAmount);
                            float requestedAmount = Float.parseFloat(needCashAmount);
                            if (requestedAmount <= currentAmount) {
                                FunduNotificationManager.setOnPairResult(HomeActivity.this);
                                startFindTransactionPairRequest(needCashAmount,"");
                            } else {
                                Toast.makeText(HomeActivity.this, "Insufficient Amount. Please load your wallet first.", Toast.LENGTH_SHORT).show();
                                float neededAmount = requestedAmount - currentAmount;

                            }
                        }
                    } else {
                        if (jsonObject.has("Error"))
                            Toast.makeText(HomeActivity.this, jsonObject.optString("Error"), Toast.LENGTH_SHORT).show();
                    }
                    if(jsonObject.optString("status").equalsIgnoreCase("Success")){

                        int charges = jsonObject.optInt("charges");
                        FunduUser.setChargesKen(String.valueOf(charges));
                        FunduNotificationManager.setOnPairResult(HomeActivity.this);
                        startFindTransactionPairRequest(needCashAmount,"");
                    }
                    else if (jsonObject.optString("status").equalsIgnoreCase("ERROR")){

                        if (jsonObject.optString("message").equalsIgnoreCase("Customer doesn't exist")
                                || jsonObject.optString("message").equalsIgnoreCase("Customer not registered with us")){
                            Signout(getApplicationContext());
                        }
                        Toast.makeText(getApplicationContext(), jsonObject.optString("message"), Toast.LENGTH_SHORT).show();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                }

            }

            @Override
            public void onHasFundError(VolleyError error) {
                dialog.dismiss();
            }
        });
        request.start();
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {

    }

    @Override
    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        onIntent(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Fog.d(TAG,"onDestroy");
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(drawerReceiver);
        drawer.removeDrawerListener(actionBarDrawerToggle);
        stopService(new Intent(this, NearByContactsService.class));
    }

    @Override
    public void onFindTransactionPairResponse(JSONObject contact) {

        // Toast.makeText(this, "Let see who can help you.", Toast.LENGTH_SHORT).show();
        // Toast.makeText(this, "Request sent error.", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onFindTransactionPairError(VolleyError error) {
        hideProgressOverlay();
        Toast.makeText(this, "Request sent error.", Toast.LENGTH_SHORT).show();
    }

    public void onPaymentSuccess(String razorpayPaymentID) {

    }

    /**
     * The name of the function has to be
     * onPaymentError
     * Wrap your code in try catch, as shown, to ensure that this method runs correctly
     */
    public void onPaymentError(int code, String response) {
        try {
            Toast.makeText(this, "Payment failed: " + Integer.toString(code) + " " + response, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {

        }
    }

    @Override
    public void onCheckBalanceResponse(String response) {
        if (dialog.isShowing()) {
            dialog.hide();
        }

        try {
            JSONObject jsonObject = new JSONObject(response);
            String walletAmount = jsonObject.optString("Balance Amount");
            if (jsonObject.has("Balance Amount")) {
                progressOverLay.setTime(90*1000, 150);
//                needCashAmount = amountBox.getText().toString().trim();
                if (walletAmount.length() == 0 || walletAmount.equals("0") || walletAmount.equals("0.00")) {
                    float currentAmount = Float.parseFloat(walletAmount);
                    //  float requestedAmount = Float.parseFloat(amountBox.getText().toString().trim());
                    float requestedAmount = Float.parseFloat(needCashAmount);
                    if (requestedAmount <= currentAmount) {
                        // Start Find pair request here
                        FunduNotificationManager.setOnPairResult(this);
                        startFindTransactionPairRequest(needCashAmount,"");
                    } else {
                        Toast.makeText(this, "Insufficient Amount. Please load your wallet first.", Toast.LENGTH_SHORT).show();
                    /*Intent intent1 = new Intent(HomeActivity.this, LoadMoneyActivity.class);
                    startActivity(intent1);*/
                        float neededAmount = requestedAmount - currentAmount;

                    }
                } else {
                    float currentAmount = Float.parseFloat(walletAmount);
                    float requestedAmount = Float.parseFloat(needCashAmount);
                    if (requestedAmount <= currentAmount) {
                        FunduNotificationManager.setOnPairResult(this);
                        startFindTransactionPairRequest(needCashAmount,"");
                    } else {
                        Toast.makeText(this, "Insufficient Amount. Please load your wallet first.", Toast.LENGTH_SHORT).show();
                        float neededAmount = requestedAmount - currentAmount;

                    }
                }
            } else {
                if (jsonObject.has("Error"))
                    Toast.makeText(this, jsonObject.optString("Error"), Toast.LENGTH_SHORT).show();
            }


        } catch (JSONException e) {
            e.printStackTrace();

        }
    }
    public void startFindTransactionPairRequest(String amount,String charges) {
        //requestLocation = new LatLng()
        amountP = Integer.parseInt(amount);
        UserTransactions.getInstance().findPair(this, getString(R.string.wallet), FunduUser.getContactId(),
                FunduUser.getContactIDType(), null, FunduUser.getContactIDType(), Integer.parseInt(amount),
                1000, true,charges,mRequestLocation);
        if (progressOverLay != null) {
            progressOverLay.showProgress();

        }
    }

    @Override
    public void onCheckBalanceError(VolleyError error) {
        if (dialog.isShowing()) {
            dialog.hide();
        }
        Toast.makeText(this, "Check balance Error", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoadWalletResponse(JSONObject response) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        if (response.has("data")) {
            if (response.optString("message").length() > 0) {
                Toast.makeText(this, response.optString("message"), Toast.LENGTH_SHORT).show();
                // finish();
                startFindTransactionPairRequest(needCashAmount,"");
            }
        } else {
            if (response.optString("message").length() > 0)
                Toast.makeText(this, response.optString("message"), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onLoadWalletError(VolleyError error) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        Fog.d(TAG,"onStop");
        dialog.dismiss();
    }





    public DrawerLayout getDrawer() {
        return drawer;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onAccepted(final ArrayList<String> alertArray,final JSONObject jData) {
        Fog.e("OnAccepted", alertArray.toString());
        Fog.e("OnAccepted", "pushData"+jData);
        hideProgressOverlay();
        if (FunduUser.getCountryShortName().equalsIgnoreCase("KEN")){
            Intent intent1 = new Intent(HomeActivity.this, PairContactFoundActivity.class);
            intent1.putStringArrayListExtra(Constants.ALERT, alertArray);
            intent1.putExtra(Constants.TRANSACTION_ID, "KENTID");
            intent1.putExtra("amount", amountP);
            intent1.putExtra("tt",Constants.NEED_CASH_TYPE);
            startActivity(intent1);
        }
        else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //insertData(alertArray,"");
                    initiateTransaction(alertArray, jData);
                }
            });
        }
    }

    private void initiateTransaction(final ArrayList<String> alertArray,final JSONObject jData) {
        if (jData.optString("contact_type").equalsIgnoreCase("ATM") /*|| alertArray.get(2).equalsIgnoreCase("AGENT")*/) {

            Intent intent1 = new Intent(HomeActivity.this, ShowPathActivity.class);
            intent1.putStringArrayListExtra(Constants.ALERT, alertArray);
            intent1.putExtra(Constants.PUSH_JSON_DATA, jData.toString());
            intent1.putExtra("tt",Constants.NEED_CASH_TYPE);
            startActivity(intent1);
            hideProgressOverlay();
        }
        else {
            hideProgressOverlay();
            dialog.setMessage("Initiating transaction...");
            dialog.show();
            pref.putInt(Constants.PrefKey.NEED_AMOUNT, amountP);
            final TransactionInitiateRequest initiateRequest = new TransactionInitiateRequest(HomeActivity.this);
            if(FunduUser.getCountryShortName().equalsIgnoreCase("KEN"))
                initiateRequest.setData(getString(R.string.wallet), FunduUser.getContactId(), FunduUser.getContactIDType(), alertArray.get(1), FunduUser.getContactIDType(), pref.getInt(Constants.PrefKey.NEED_AMOUNT, -1), 1000,alertArray.get(1),"0","0",jData.optString(Constants.PushNotificationKeys.PAIR_REQUEST_ID));
            else{
                //String recipientId = alertArray.get(11);//pushData.optString("recipient_id");
                String recipientId = "1234565";//pushData.optString("recipient_id");
                Fog.d("recipientId",""+recipientId);
                initiateRequest.setData(getString(R.string.upi), FunduUser.getContactId(), FunduUser.getContactIDType(), recipientId, FunduUser.getContactIDType(), (int) pref.getInt(Constants.PrefKey.NEED_AMOUNT, -1), 1000,alertArray.get(1),jData.optString(Constants.PushNotificationKeys.PROVIDER_CHARGE),
                        jData.optString(Constants.PushNotificationKeys.FEE),jData.optString(Constants.PushNotificationKeys.PAIR_REQUEST_ID));
            }
//            setData(String alias, String sender_id, String sender_id_type, String recipient_id, String recipient_id_type, int amount, int hold_timeout) {
            initiateRequest.setParserCallback(new TransactionInitiateRequest.OnTransactionInitiateResults() {
                @Override
                public void onTransactionInitiateResponse(JSONObject response) {
                    Fog.d("TransactionInitiateRequest","****"+response);
                    try {
                        jData.put("tid", response.getJSONObject("data").getJSONObject("data").getString("tid"));
                        jData.put(Constants.PushNotificationKeys.SEEKER,FunduUser.getContactId());
                    }
                    catch (JSONException e){
                        e.printStackTrace();
                    }
                    FunduTransaction transaction = GreenDaoHelper.getInstance(HomeActivity.this).addTransaction(alertArray,jData);
                    FunduAnalytics.getInstance(HomeActivity.this).sendAction("Transaction","Initiated",(int)Double.parseDouble(transaction.getAmount()));
                    GreenDaoHelper.getInstance(HomeActivity.this).updateTransactionState(transaction.getId(),
                            Constants.TRANSACTION_STATE.SEEKER_INITIATED.getCode());
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Intent intent1 = new Intent(HomeActivity.this, PairContactFoundActivity.class);
                    intent1.putExtra(Constants.FUNDU_TRANSACTION_ID,transaction.getId());
                    intent1.putStringArrayListExtra(Constants.ALERT, alertArray);
                    intent1.putExtra(Constants.PUSH_JSON_DATA,jData.toString());
                    intent1.putExtra("tt",Constants.NEED_CASH_TYPE);
                    startActivity(intent1);

                }

                @Override
                public void onTransactionInitiateError(VolleyError error) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(HomeActivity.this, "Initiate transaction error.", Toast.LENGTH_SHORT).show();
                }
            });
            initiateRequest.start();
        }
    }

    private void hideProgressOverlay() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressOverLay != null)
                    progressOverLay.hideProgress();
            }
        });
    }

    @Override
    public void onNoPairFound(ArrayList<String> alertArray) {
        hideProgressOverlay();
        if (FunduUser.getCountryShortName().equalsIgnoreCase("KEN")) {

            Toast.makeText(HomeActivity.this, "Sorry " + FunduUser.getFullName()
                    + ", we didn't get someone to assist you. Try Again Later", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(HomeActivity.this, "No Transaction Pair Found for this request", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {

        if (dialogsuccess)
            checkWallet(amountP);
    }

    public   boolean checkAndRequestPermissionsn()
    {
        int sms = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS);
        int loc = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int loc2 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int readcontact = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS);
        int readcalllog = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CALL_LOG);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (sms != PackageManager.PERMISSION_GRANTED)
        {
            listPermissionsNeeded.add(android.Manifest.permission.READ_SMS);
        }
        if (loc != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (loc2 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (readcontact != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_CONTACTS);
        }
        if (readcalllog != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_CALL_LOG);
        }
        if (!listPermissionsNeeded.isEmpty())
        {
            ActivityCompat.requestPermissions(this,listPermissionsNeeded.toArray(new
                    String[listPermissionsNeeded.size()]),2016);
            return false;
        }
        return true;
    }

    //
    public void enterAmountScreen(){
        if(checkForPendingTransaciton()){
            //There is a pending transaction, finish that first.
            return;
        }
        Fragment fragment = EnterAmount.newInstance();
        addFragment(fragment,true);
        /**
         *Testing code
         */
//         testQRCodeTransaction();
//         deeplinkPushnotificationTest();
    }


//    private void deeplinkPushnotificationTest(){
//        String url = "https://hn34u.app.goo.gl/transactions";
//
//        try {
//
//            JSONObject object = new JSONObject();
//            object.put("deeplink", url);
//            object.put("message","Testing non deeplink push notification");
//            FunduNotificationManager.messageToUser(this,object);
//        }
//        catch(Exception e){
//            e.printStackTrace();
//        }
//    }

    InviteFriends inviteFriends = new InviteFriends();
    ContactsFragment contactsFragment = new ContactsFragment();

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReplaceFragment(ReplaceFragment event) {
        Fog.i(TAG,"onReplaceFragment");
        if(event.getTo() == InviteFriends.class&&event.getFrom() == ContactsFragment.class)
            //showInviteFriendsList();
            addFragment(inviteFriends,true);

        else if(event.getTo() == ContactsFragment.class&&event.getFrom() == InviteFriends.class){
            addFragment(contactsFragment,true);
            //showContacts();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowFragment(ShowFragment event) {
        Fog.i(TAG,"onReplaceFragment");
        if(event.whichFragment == ContactsFragment.class){
            getWindow().getDecorView().getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    hideHamburgerIcon();
                    addFragment(ContactsFragment.newInstance("",""),true);
                }
            }, 200);
        }

    }


    public void showHamburgerIcon(){
        if(toolbar != null)
            toolbar.setVisibility(View.VISIBLE);
    }

    public void hideHamburgerIcon() {
        if(toolbar != null)
            toolbar.setVisibility(View.GONE);
    }

    public void setmLinkedAccountList(JSONArray mLinkedAccountList) {
        this.mLinkedAccountList = mLinkedAccountList;
    }

    public void setmRequestLocation(LatLng mRequestLocation) {
        this.mRequestLocation = mRequestLocation;
    }
    public JSONArray getmLinkedAccountList() {
        return mLinkedAccountList;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions, @NonNull int[] grantResults) {

        //take necessary actions
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (f instanceof ContactsFragment) {
            ContactsFragment cf = (ContactsFragment) f;
            cf.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }
    }
}