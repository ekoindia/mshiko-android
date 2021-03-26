package in.co.eko.fundu.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.co.eko.fundu.R;
import in.co.eko.fundu.adapters.QuestionSpinnerAdapter;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.fragments.BaseFragment;
import in.co.eko.fundu.fragments.CVVFragment;
import in.co.eko.fundu.fragments.CardDetailFragment;
import in.co.eko.fundu.fragments.ContactsFragment;
import in.co.eko.fundu.fragments.CreateFunduPinFragment;
import in.co.eko.fundu.fragments.EnterBankAccountFragment;
import in.co.eko.fundu.fragments.EnterPhoneNumberFragment;
import in.co.eko.fundu.fragments.IdentificationFragment;
import in.co.eko.fundu.fragments.IntroductionToUPI;
import in.co.eko.fundu.fragments.MerchantCardDetailFragment;
import in.co.eko.fundu.fragments.MerchantRegistrationFragment;
import in.co.eko.fundu.fragments.QuestionAnswerFragment;
import in.co.eko.fundu.fragments.SelectSimFragment;
import in.co.eko.fundu.fragments.SimNotSupportedFragment;
import in.co.eko.fundu.fragments.ThanksFragment;
import in.co.eko.fundu.fragments.VerifyCodeFragment;
import in.co.eko.fundu.interfaces.OnFragmentInteractionListener;
import in.co.eko.fundu.models.Contact;
import in.co.eko.fundu.models.CountryMobile;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.requests.GetCountriesRequest;
import in.co.eko.fundu.utils.Fog;
import in.co.eko.fundu.utils.FunduAnalytics;
import in.co.eko.fundu.utils.SimSuscriptionManager;
import in.co.eko.fundu.utils.Utils;

//import in.co.eko.fundu.database.tables.CountryMobileTables;


public class UserOnboardingActivity extends AppCompatActivity implements OnFragmentInteractionListener {

    private CountryMobile[] country_mobile_array;
    String fragmentName;
    public static QuestionSpinnerAdapter UserOnBoardQuestion_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();
        Fog.e("HOME BROADCAST", "UserOnboardingActivity");
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
         Contact contact1 = (Contact) getIntent().getSerializableExtra(Contact.class.getSimpleName());

         String fragmentName = getIntent().getStringExtra(BaseFragment.FRAGMENT_NAME);
      if(fragmentName != null && fragmentName.length() > 0){
            addFragment("IntroductionToUPI", IntroductionToUPI.newInstance(contact1, ""));
        }
        else
        //  startFragment(contact1);
         addFragment("EnterPhoneNumberFragment", EnterPhoneNumberFragment.newInstance());

        checkAndRequestPermissionsn();
        onIntent(getIntent());
    }
    public void onIntent(Intent intent){
        processDynamicLink();
    }
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
                            switch(type){
                                case "invite":
                                    String ii = deepLink.getQueryParameter("source");
                                    FunduUser.setInvitationId(ii);
                                    FunduAnalytics.getInstance(UserOnboardingActivity.this).sendAction("UserAcquisition","invite",1);
                                    break;
                                case "fpn":
                                    String a = deepLink.getQueryParameter("a");
                                    FunduUser.setInvitationId(a);
                                    FunduAnalytics.getInstance(UserOnboardingActivity.this).sendAction("UserRetention","fpn",1);
                                    break;
                                case "facebook" :
                                    String iif = deepLink.getQueryParameter("source");
                                    FunduUser.setInvitationId(iif);
                                    FunduAnalytics.getInstance(UserOnboardingActivity.this).sendAction("UserAcquisition", "facebook", 1);
                                    break;
                                case "instagram" :
                                    String iii = deepLink.getQueryParameter("source");
                                    FunduUser.setInvitationId(iii);
                                    FunduAnalytics.getInstance(UserOnboardingActivity.this).sendAction("UserAcquisition", "instagram", 1);
                                    break;
                            }
                            Fog.wtf("","deepLink "+deepLink);
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

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        onIntent(intent);
    }


    private void init(){
        FunduUser.initialize(this);
        findViewById(R.id.feedback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = getWindow().getDecorView().getRootView();
                v.setDrawingCacheEnabled(true);
                Bitmap bmp = Bitmap.createBitmap(v.getDrawingCache());
                v.setDrawingCacheEnabled(false);
                Utils.takeFeedback(bmp,UserOnboardingActivity.this);
            }
        });

    }

    private void startFragment(Contact contact1) {
        if(!Utils.isSimSupport(this)) {
            addFragment("SimNotSupportedFragment", SimNotSupportedFragment.newInstance("", ""));
            return;
        }
        else{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {

                int phoneState =   checkPermissionToReadPhoneState();
                if(phoneState!=PackageManager.PERMISSION_GRANTED){
                    fragmentName = "EnterPhoneNumberFragment";
                    addFragment("EnterPhoneNumberFragment", EnterPhoneNumberFragment.newInstance());
                }

                else{
                    if(Utils.isDualSim(this)){
                        SimSuscriptionManager simSuscriptionManager = new SimSuscriptionManager(this);
                        ArrayList<String> simNumbers = simSuscriptionManager.getSuscriptionSimNumbers();

                       if(simNumbers.size()==0){
                           fragmentName = "EnterPhoneNumberFragment";
                            addFragment("EnterPhoneNumberFragment", EnterPhoneNumberFragment.newInstance());
                            return;
                       }
                        if(simNumbers.size()>=1){

                            fragmentName = "SelectSimFragment";
                            addFragment("SelectSimFragment", SelectSimFragment.newInstance(contact1, ""));
                            return;
                        }
                        else{

                            /*fragmentName = "EnterPhoneNumberFragment";
                            addFragment("EnterPhoneNumberFragment", EnterPhoneNumberFragment.newInstance(contact, ""));*/
                            fragmentName = "SelectSimFragment";
                            addFragment("SelectSimFragment", SelectSimFragment.newInstance(contact1, ""));
                            return;


                        }

                    }
                    else{
                        fragmentName = "SelectSimFragment";
                        addFragment("SelectSimFragment", SelectSimFragment.newInstance(contact1, ""));
                        return;
                    }
                }


            }
            else {
               /* if(!Utils.isDualSim(this)){
                    fragmentName = "SelectSimFragment";
                    addFragment("SelectSimFragment", SelectSimFragment.newInstance(contact, ""));
                }
                else{*/
                    fragmentName = "EnterPhoneNumberFragment";
                    addFragment("EnterPhoneNumberFragment", EnterPhoneNumberFragment.newInstance());
                return;
               // }


            }
        }
    }

    private int checkPermissionToReadPhoneState() {
        int phoneState = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

        return phoneState;
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
    }
    public  void addFragment(String fragName, Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.fragmentContainer, fragment, fragName);
        if (fragment.getClass().toString().equalsIgnoreCase("class in.co.eko.fundu.fragments.QuestionAnswerFragment")
                || fragment.getClass().toString().equalsIgnoreCase("class in.co.eko.fundu.fragments.CardDetailFragment")
                || fragment.getClass().toString().equalsIgnoreCase("class in.co.eko.fundu.fragments.CVVFragment")
                || fragment.getClass().toString().equalsIgnoreCase("class in.co.eko.fundu.fragments.EnterBankAccountFragment")
                || fragment.getClass().toString().equalsIgnoreCase("class in.co.eko.fundu.fragments.MerchantCardDetailFragment")) {
            ft.addToBackStack(null);
        }
        ft.commitAllowingStateLoss();
    }

    @Override
    public void onFragmentInteraction(Bundle bundle) {
        final String FRAGMENT_NAME = bundle.getString(BaseFragment.FRAGMENT_NAME);
        assert FRAGMENT_NAME != null;

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        switch (FRAGMENT_NAME) {
            case "IdentificationFragment" :
                addFragment(FRAGMENT_NAME, IdentificationFragment.newInstance((Contact) bundle.getSerializable(Contact.class.getSimpleName()), ""));
                break;
            case "SelectSimFragment" :
                addFragment(FRAGMENT_NAME, IdentificationFragment.newInstance((Contact) bundle.getSerializable(Contact.class.getSimpleName()), ""));
                break;
            case "EnterPhoneNumberFragment" :
                addFragment(FRAGMENT_NAME, EnterPhoneNumberFragment.newInstance());
                break;
            case "VerifyCodeFragment" :
                addFragment(FRAGMENT_NAME, VerifyCodeFragment.newInstance((Contact) bundle.getSerializable(Contact.class.getSimpleName()), ""));
                break;
            case "ThanksFragment" :
                addFragment(FRAGMENT_NAME, ThanksFragment.newInstance((Contact) bundle.getSerializable(Contact.class.getSimpleName()), ""));
                break;
            case "CreateFunduPinFragment" :
                addFragment(FRAGMENT_NAME, CreateFunduPinFragment.newInstance((Contact) bundle.getSerializable(Contact.class.getSimpleName()), ""));
                break;
            case "QuestionAnswerFragment" :
                addFragment(FRAGMENT_NAME, QuestionAnswerFragment.newInstance((Contact) bundle.getSerializable(Contact.class.getSimpleName()), ""));
                break;
            case "CardDetailFragment" :
                addFragment(FRAGMENT_NAME, CardDetailFragment.newInstance((Contact) bundle.getSerializable(Contact.class.getSimpleName()), ""));
                break;
            case "MerchantCardDetailFragment":
                addFragment(FRAGMENT_NAME, MerchantCardDetailFragment.newInstance((Contact) bundle.getSerializable(Contact.class.getSimpleName()), ""));
                break;
            case "MerchantRegistrationFragment":
                addFragment(FRAGMENT_NAME, MerchantRegistrationFragment.newInstance((Contact) bundle.getSerializable(Contact.class.getSimpleName()), ""));
                break;
            case "EnterBankAccountFragment":
                addFragment(FRAGMENT_NAME, EnterBankAccountFragment.newInstance((Contact) bundle.getSerializable(Contact.class.getSimpleName()), ""));
                break;
            case "CVVFragment":
                addFragment(FRAGMENT_NAME, CVVFragment.newInstance((Contact) bundle.getSerializable(Contact.class.getSimpleName()), ""));
                break;
            case "IntroductionToUPI":
                addFragment(FRAGMENT_NAME, IntroductionToUPI.newInstance((Contact) bundle.getSerializable(Contact.class.getSimpleName()), ""));
                break;
            case "ContactsFragment":
                addFragment(FRAGMENT_NAME, ContactsFragment.newInstance("", ""));
                break;
            default:
                break;
        }
    }

    public void onClickGetANewCode(View view) {
        //addFragment("VerifyCodeFragment", VerifyCodeFragment.newInstance(con,""));
    }

    public void onClickEditPhoneNumber(View view) {
       // addFragment("EnterPhoneNumberFragment", new EnterPhoneNumberFragment());
    }

    public void onClickTour(View view) {
    }

    public void onClickHelp(View view) {

       // Snackbar.make(view, "Test", Snackbar.LENGTH_LONG).show();

    }

    boolean state_active=false;
    @Override
    protected void onResume() {
        super.onResume();
        state_active=true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        state_active=false;
    }
    private  boolean checkAndRequestPermissionsn()
    {
        int sms   = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS);
        int loc   = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int loc2  = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int rdcnt = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS);
        int phoneState = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
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
        if (rdcnt != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_CONTACTS);
        }
        if (phoneState != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_PHONE_STATE);
        }
        if (!listPermissionsNeeded.isEmpty())
        {
            ActivityCompat.requestPermissions(this,listPermissionsNeeded.toArray(new
                    String[listPermissionsNeeded.size()]),2016);
            return false;
        }
        return true;
    }


    private void updateCountryList(){
//        new insertIntoDb().execute(country_mobile_array);
    }

    public void   getCountries(){
        // list.add(7, list.get(8));
        Fog.d("getCountries","getCountries"+"UserOnBoarding");
        GetCountriesRequest request = new GetCountriesRequest(this);
        request.setParserCallback(new GetCountriesRequest.OnCountriesRequestResult() {

            @Override
            public void onCountriesResponse(String response) {
              EnterPhoneNumberFragment.dialog.dismiss();
            /* if(fragmentName.equalsIgnoreCase("EnterPhoneNumberFragment"))EnterPhoneNumberFragment.dialog.dismiss();
                else  SelectSimFragment.dialog.dismiss();*/
                try {
                    JSONArray array = new JSONArray(response);
                    CountryMobile[] arraylist=new CountryMobile[array.length()];

                    for(int i=0;i<array.length();i++){
                        JSONObject obj=array.getJSONObject(i);
                        CountryMobile bean=new CountryMobile();
                        bean.setCountryName(obj.getString("country_name"));
                        bean.setStartsWith(obj.getString("start_with"));
                        bean.setCountryCode(obj.getString("country_code"));
                        bean.setLength(obj.getInt("number_length"));
                        bean.setSymbol(obj.getString("symbol"));
                        bean.setEnable(obj.optBoolean("enable", true));
                        bean.setcountry_shortname(obj.getString(Constants.COUNTRY_SHORTCODE));
//                        EnterPhoneNumberFragment.countryShortname = obj.getString("countryShortname");
                        arraylist[i]=bean;
                    }
                    country_mobile_array=arraylist;        //get countries hit

                    updateCountryList();
                    Fragment fragment=getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
                    if(fragment!=null && fragment instanceof EnterPhoneNumberFragment)
                        ((EnterPhoneNumberFragment)fragment).onCountriesResponse(country_mobile_array);



                }catch (JSONException ex){

                    Toast.makeText(getApplicationContext(),"No Countries found", Toast.LENGTH_LONG).show();
                    updateCountryList();
                }
            }

            @Override
            public void onCountriesError(VolleyError error) {
                EnterPhoneNumberFragment.dialog.dismiss();
                error.printStackTrace();

            }
        });
        request.start();



    }
    public  void finishActivity(){
        finish();
    }

   /* @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }*/
}
