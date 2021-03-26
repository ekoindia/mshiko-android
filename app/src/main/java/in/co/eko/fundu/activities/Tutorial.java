package in.co.eko.fundu.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.co.eko.fundu.R;
import in.co.eko.fundu.adapters.TutorialPagerAdapter;
import in.co.eko.fundu.interfaces.OnFragmentInteractionListener;
import in.co.eko.fundu.interfaces.SocialListener;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.models.User;
import in.co.eko.fundu.utils.Fog;
import in.co.eko.fundu.utils.FunduAnalytics;
import in.co.eko.fundu.utils.Utils;

//import com.facebook.FacebookCallback;
//import com.facebook.FacebookException;
//import com.facebook.GraphRequest;
//import com.facebook.GraphResponse;
//import com.facebook.HttpMethod;
//import com.facebook.Profile;
//import com.facebook.ProfileTracker;
//import com.facebook.login.LoginResult;


public class Tutorial extends BaseActivity implements OnFragmentInteractionListener,
        View.OnClickListener, SocialListener, GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 9001;
    private static String TAG = "Tutorial";
//    private ProfileTracker mProfileTracker;
    private  GoogleApiClient mGoogleApiClient;
    /**
     * Tutorial Pager to show Fundu USPs
     */
    private ViewPager tutorialPager;
    private ImageView imgFrwrdArrow;

    private Button btnGetStarted;
    private TextView txtSkip;
    private TutorialPagerAdapter pagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_tutorial);
        init();
        setOnClickListener();
        // GPlus Auth here
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        FunduAnalytics.getInstance(this).sendScreenName("Tutorial");

    }
    private void setOnClickListener() {

        btnGetStarted.setOnClickListener(this);
        imgFrwrdArrow.setOnClickListener(this);
        txtSkip.setOnClickListener(this);
    }
    /**
     * To initialize the views
     */
    private void init() {
        tutorialPager = (ViewPager) findViewById(R.id.tut_pager);
        imgFrwrdArrow = (ImageView) findViewById(R.id.img_frwrdarrow);
        txtSkip       = (TextView) findViewById(R.id.txt_skip);
        btnGetStarted = (Button) findViewById(R.id.btn_getstarted);

        pagerAdapter  = new TutorialPagerAdapter(this,this);
        tutorialPager.setAdapter(pagerAdapter);
        tutorialPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                moveDots(position);

                if (position == 0) {
                    txtSkip.setVisibility(View.VISIBLE);
                    imgFrwrdArrow.setVisibility(View.VISIBLE);
                    btnGetStarted.setVisibility(View.GONE);

                } else if (position == 1) {
                    txtSkip.setVisibility(View.VISIBLE);
                    imgFrwrdArrow.setVisibility(View.VISIBLE);
                    btnGetStarted.setVisibility(View.GONE);

                } else if (position == 2) {
                    txtSkip.setVisibility(View.VISIBLE);
                    imgFrwrdArrow.setVisibility(View.VISIBLE);
                    btnGetStarted.setVisibility(View.GONE);

                }
                else if (position == 3) {
                    imgFrwrdArrow.setVisibility(View.GONE);
                    txtSkip.setVisibility(View.GONE);
                    btnGetStarted.setVisibility(View.VISIBLE);

                }
                else if (position == 4) {
                    txtSkip.setVisibility(View.GONE);
                    imgFrwrdArrow.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
//        mFacebookHandler = FacebookHandler.getInstance(Tutorial.this);

    }


    /**
     * Move dots showing below USPs
     *
     * @param position : Position of the solid dot.
     */
    private void moveDots(int position) {
        switch (position) {
            case 0:
                txtSkip.setVisibility(View.VISIBLE);
                findViewById(R.id.tut_dot_one).setBackgroundResource(R.drawable.tut_circle_solid);
                findViewById(R.id.tut_dot_two).setBackgroundResource(R.drawable.tut_circle_stroke);
                findViewById(R.id.tut_dot_three).setBackgroundResource(R.drawable.tut_circle_stroke);
                findViewById(R.id.tut_dot_four).setBackgroundResource(R.drawable.tut_circle_stroke);
                findViewById(R.id.tut_dot_five).setBackgroundResource(R.drawable.tut_circle_stroke);
                break;
            case 1:
                txtSkip.setVisibility(View.VISIBLE);
                findViewById(R.id.tut_dot_one).setBackgroundResource(R.drawable.tut_circle_stroke);
                findViewById(R.id.tut_dot_two).setBackgroundResource(R.drawable.tut_circle_solid);
                findViewById(R.id.tut_dot_three).setBackgroundResource(R.drawable.tut_circle_stroke);
                findViewById(R.id.tut_dot_four).setBackgroundResource(R.drawable.tut_circle_stroke);
                findViewById(R.id.tut_dot_five).setBackgroundResource(R.drawable.tut_circle_stroke);
                break;
            case 2:
                txtSkip.setVisibility(View.VISIBLE);
                findViewById(R.id.tut_dot_one).setBackgroundResource(R.drawable.tut_circle_stroke);
                findViewById(R.id.tut_dot_two).setBackgroundResource(R.drawable.tut_circle_stroke);
                findViewById(R.id.tut_dot_three).setBackgroundResource(R.drawable.tut_circle_solid);
                findViewById(R.id.tut_dot_four).setBackgroundResource(R.drawable.tut_circle_stroke);
                findViewById(R.id.tut_dot_five).setBackgroundResource(R.drawable.tut_circle_stroke);
                break;
            case 3:
                txtSkip.setVisibility(View.VISIBLE);
                findViewById(R.id.tut_dot_one).setBackgroundResource(R.drawable.tut_circle_stroke);
                findViewById(R.id.tut_dot_two).setBackgroundResource(R.drawable.tut_circle_stroke);
                findViewById(R.id.tut_dot_three).setBackgroundResource(R.drawable.tut_circle_stroke);
                findViewById(R.id.tut_dot_four).setBackgroundResource(R.drawable.tut_circle_solid);
                findViewById(R.id.tut_dot_five).setBackgroundResource(R.drawable.tut_circle_stroke);
                break;
            case 4:
                txtSkip.setVisibility(View.GONE);
                findViewById(R.id.tut_dot_one).setBackgroundResource(R.drawable.tut_circle_stroke);
                findViewById(R.id.tut_dot_two).setBackgroundResource(R.drawable.tut_circle_stroke);
                findViewById(R.id.tut_dot_three).setBackgroundResource(R.drawable.tut_circle_stroke);
                findViewById(R.id.tut_dot_four).setBackgroundResource(R.drawable.tut_circle_stroke);
                findViewById(R.id.tut_dot_five).setBackgroundResource(R.drawable.tut_circle_solid);
                break;
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        GoogleSignInAccount acct = result.getSignInAccount();
        User user = new User();

        if (acct != null) {
            String personName = acct.getDisplayName();
//            String temp = personName;
            if(personName==null || personName.length()==0) {
                personName = "Fundu User";
            }
            personName = personName.replaceAll("[^A-Za-z@ ]", "");
            if (personName.contains("@")) {
                personName = personName.substring(0, personName.indexOf("@"));
            }
            if (personName.length()==0) {
                personName = "Fundu User";
            }
            Fog.d("personName",""+personName);
            String socialId = acct.getId();
            Uri imageUrl = acct.getPhotoUrl();
            String personPhotoUrl = null;
            if (imageUrl != null) {
                personPhotoUrl = imageUrl.toString();
            }

            user.setName(personName);
            user.setEmail(acct.getEmail());
            user.setLoginType(User.GOOGLE_PLUS_TYPE);
            user.setProfileUrl(personPhotoUrl);
            //* hardcoded mobile type*//*
            user.setContactIdType("mobile_type");
            FunduUser.saveUser(user);
            FunduUser.setLoggedIn(true);
            startNewActivity();
            finish();

        }

    }

    private String validateName(String name) {
        String[] bits = name.split(" ");
        if (bits.length>4) {
            StringBuffer sbf = new StringBuffer();

            for (int i = 0; i < 4; i++) {
                sbf.append(bits[i]).append(" ");

            }

            String strn = sbf.toString();
            name = strn.substring(0, strn.length() - 1);
        }
        Pattern ptrn = Pattern.compile("[a-zA-Z]{0,}\\s{0,}[a-zA-Z]{0,}\\s{0,}[a-zA-Z]{0,}\\s{0,}[a-zA-Z]{2,}|^OM$|^om$");
        Matcher mtch = ptrn.matcher(name);
        if (!mtch.matches()) {
            name = name.substring(0, name.indexOf(" "));
        }

        Pattern ptr = Pattern.compile("^((?!(a+b+c|b+c+d|c+d+e|d+e+f|e+f+g|f+g+h|j+k+l|k+l+m|l+m+n|o+p+q|p+q+r|q+r+s|t+u+v|u+v+w|v+w+x|w+x+y|x+y+z)).)*$");
        Matcher mtch1 = ptr.matcher(name);
        if (!mtch1.matches()) {
            for(int i=0;i<name.length()-2;i++) {
                if((name.charAt(i)==name.charAt(i+1)-1) && name.charAt(i)==name.charAt(i+2)-2) {
                    name = name.substring(0,i)+name.substring(i+1, name.length());
                    i--;
                }
            }
            if(name.length()==2) {
                Pattern pattern = Pattern.compile("[oO]{1}[mM]{1}"); // [aAeEiIoOuU]{1}[mM]{1}
                Matcher mtc = pattern.matcher(name);

                if (!mtc.matches()) {
                    name = "Fundu User";
                    return name;
                }
            }
        }


        int size = name.length();
        switch (size) {
            case 1:
                name = "Fundu User";
                break;
            case 2:
                Pattern pattern = Pattern.compile("[oO]{1}[mM]{1}"); // [aAeEiIoOuU]{1}[mM]{1}
                Matcher mtc = pattern.matcher(name);

                if (!mtc.matches()) {
                    name = "Fundu User";
                }
                break;
            case 3:
                Pattern ptrn1 = Pattern.compile("[a-zA-Z&&[^aAeEiIoOuU]]{3}");

                Matcher mt = ptrn1.matcher(name);

                if (String.valueOf(name.charAt(0)).equals(String.valueOf(name.charAt(2))) || mt.matches()) {
                    name = "Fundu User";
                }
                break;
            case 4:
                Pattern ptrrn = Pattern.compile("[a-zA-Z&&[^aAeEiIoOuU]]{4}");
                Matcher mtchr = ptrrn.matcher(name);
                int val = 0;
                for (int k = 1; k <= 3; k++) {
                    if (String.valueOf(name.charAt(0)).equals(String.valueOf(name.charAt(k)))) {
                        val = val + 1;
                    }
                }
                if (val >= 2 || mtchr.matches()) {
                    name = "Fundu User";
                }
                break;
            default:
                int flag = 0;
                for (int i = 0; i < size - 2; i++) {
                    String st1 = String.valueOf(name.charAt(i));

                    if (name.contains(st1 + st1 + st1)) {
                        name = name.replaceAll(st1+st1+st1, st1+st1);
                    }

                }

                Pattern ptrrnn = Pattern.compile("[a-zA-Z&&[^aAeEiIoOuU]]{5,30}");
                Matcher mtchrr = ptrrnn.matcher(name);
                if (mtchrr.matches()) {
                    name = "Fundu User";
                }
                break;

        }
        return name;
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
//
//    @Override
//    public void onSuccess(LoginResult loginResult) {
//      /*  GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), this);
//        Bundle parameters = new Bundle();
//        parameters.putString("fields", "id, name, email, first_name, last_name");
//        request.setParameters(parameters);
//        request.executeAsync();
//        dialog.setMessage("Fetching information...");
//        dialog.show();*/
//    }

//    @Override
//    public void onCancel() {
//
//    }
//
//    @Override
//    public void onError(FacebookException error) {
//
//    }
//
//    @Override
//    public void onCompleted(final JSONObject json, GraphResponse response) {
//        if (Profile.getCurrentProfile() == null) {
//            mProfileTracker = new ProfileTracker() {
//                @Override
//                protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
//                    saveUserFbInfo(currentProfile, json);
//                    mProfileTracker.stopTracking();
//                }
//            };
//            mProfileTracker.startTracking();
//        } else {
//            saveUserFbInfo(Profile.getCurrentProfile(), json);
//        }
//    }
//
//    private void saveUserFbInfo(Profile currentProfile, JSONObject json) {
//        try {
//            AppPreferences appPreferences = FunduUser.getAppPreferences();
//            String profileUrl;
//            if (currentProfile.getProfilePictureUri(400, 400) != null)
//                profileUrl = currentProfile.getProfilePictureUri(400, 400).toString();
//            else {
//                profileUrl = "";
//            }
//            String email = json.optString("email");
//            String str_id = json.getString("id");
//            String str_firstname = json.getString("first_name");
//            String str_lastname = json.getString("last_name");
//            String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
//            User contact = new User();
//            contact.setName(appPreferences.getString(Constants.NAME));
//            contact.setDeviceId(androidId);
//            contact.setDeviceToken(appPreferences.getString(Constants.GCM_TOKEN));
//            contact.setEmail(email);
//            contact.setLoginType(User.FACEBOOK_TYPE);
//            contact.setContactIdType("mobile_type");
//            FunduUser.setLoggedIn(true);
//            FunduUser.saveUser(contact);
//           // contact1 = contact;
//            startNewActivity();
//
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public void onFragmentInteraction(Bundle bundle) {

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.txt_skip:
                setCurrentItem (5, true);

                break;

            case R.id.img_frwrdarrow:
                setCurrentItem (tutorialPager.getCurrentItem(), true);
                break;

            case R.id.btn_getstarted:
                showLoginOptions();
                break;

        }
    }


    private void showLoginOptions(){
        findViewById(R.id.tutorialinfo).setVisibility(View.GONE);
        findViewById(R.id.loginoptions).setVisibility(View.VISIBLE);
        ImageView googleLogin=(ImageView)findViewById(R.id.google_login);
       // ImageView facebookLogin=(ImageView)findViewById(R.id.facebook_login);

        googleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                socialType("google");

            }
        });
//        facebookLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                socialType("facebook");
//
//            }
//        });
    }

    public void doGoogleLogin(){

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    private void startNewActivity() {
        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean hasPermission = false;
            for (int i = 0; i < PermissionsActivity.mPermissions.length; i++) {

                hasPermission = Utils.checkPermission(this, PermissionsActivity.mPermissions[i]);

                if (!hasPermission) {
                    break;
                }
            }
            if (hasPermission) {
                if(FunduUser.getUser()!=null&&FunduUser.getUser().isVerified())
                    intent = new Intent(this, HomeActivity.class);
                else
                    intent = new Intent(this,UserOnboardingActivity.class);
            }
            else{
                intent = new Intent(this,PermissionsActivity.class);
            }

        }
        else {

            if(FunduUser.getUser() != null && FunduUser.getUser().isVerified())
                intent = new Intent(this, HomeActivity.class);
            else
                intent = new Intent(this,UserOnboardingActivity.class);

        }
        startActivity(intent);
        finish();

    }


    public void setCurrentItem (int item, boolean smoothScroll) {
        Fog.d("item","item"+item);
        tutorialPager.setCurrentItem(item+1, smoothScroll);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        mFacebookHandler.onActivityResult(requestCode, resultCode, data);
        Fog.e("Tutorial", requestCode +" == "+RC_SIGN_IN+" ,<--- RESCODE ---> "+resultCode+" == "+ Activity.RESULT_OK);
        if (requestCode == RC_SIGN_IN && resultCode == Activity.RESULT_OK) {
            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            Fog.e("AuthDl","OnActivityResult");
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()){
                Fog.e("AuthDl","Success");
                handleSignInResult(result);
            }
        }
//        else
//            Utils.showShortToast(getContext(), "Facebook Login failed!");
    }


    @Override
    public void socialType(String type) {
        if(type.equalsIgnoreCase("google")){
            doGoogleLogin();
            //Toast.makeText(Tutorial.this, "google click", Toast.LENGTH_SHORT).show();
        }
//        else if(type.equalsIgnoreCase("facebook")){
//            fbLogin();
//           // Toast.makeText(Tutorial.this, "facebook click", Toast.LENGTH_SHORT).show();
//        }
    }

//    public void fbLogin(){
//        if(Utils.isNetworkAvailable(this)){
//            if (mFacebookHandler.isLoggedIn()) {
//                mFacebookHandler.logOut();
//            }
//            mFacebookHandler.login(Tutorial.this, new FacebookCallback<LoginResult>() {
//                @Override
//                public void onSuccess(LoginResult loginResult) {
//
//                    Bundle params = new Bundle();
//                    params.putString("fields", "id,email,gender,name,cover,picture.type(large)");
//
//                    new GraphRequest(mFacebookHandler.getAccessToken(), "me", params, HttpMethod.GET,
//                            new GraphRequest.Callback() {
//                                @Override
//                                public void onCompleted(GraphResponse response) {
//                                    if (response != null) {
//                                        try {
//                                            JSONObject data = response.getJSONObject();
//                                            String email = data.optString("email");
//                                            String str_id = data.getString("id");
//                                            String name = data.getString("name");
//                                            if (data.has("picture")) {
//                                                String profilePicUrl = data.getJSONObject("picture").getJSONObject("data").getString("url");
//                                                Fog.d(TAG,"profile************** "+profilePicUrl);
//                                                pref.putString(Constants.PROFILE_PIC_URL, profilePicUrl);
//                                                //Toast.makeText(Tutorial.this, ""+profilePicUrl, Toast.LENGTH_SHORT).show();
//                                            }
//
//                                            User contact = new User();
//                                            contact.setName(name);
//                                            contact.setDeviceToken(pref.getString(Constants.GCM_TOKEN));
//                                            contact.setEmail(email);
//                                            contact.setLoginType(User.FACEBOOK_TYPE);
//                                            contact.setContactIdType("mobile_type");
//                                            FunduUser.saveUser(contact);
//                                            FunduUser.setLoggedIn(true);
//                                            startNewActivity();
//
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//                                }
//                            }).executeAsync();
//
//                    Fog.d(TAG, loginResult.getAccessToken().getToken());
//                }
//
//                @Override
//                public void onCancel() {
//                }
//
//                @Override
//                public void onError(FacebookException exception) {
//                }
//            });
//        }
//        else{
//
//        }
//    }






}
