package in.co.eko.fundu.activities;

import android.Manifest;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import in.co.eko.fundu.R;
import in.co.eko.fundu.adapters.ViewPagerAdapter;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.constants.V1API;
import in.co.eko.fundu.fragments.CCNameFragment;
import in.co.eko.fundu.fragments.CCNumberFragment;
import in.co.eko.fundu.fragments.CCSecureCodeFragment;
import in.co.eko.fundu.fragments.CCValidityFragment;
import in.co.eko.fundu.fragments.CardBackFragment;
import in.co.eko.fundu.fragments.CardFrontFragment;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.requests.FinalRegistrationRequest;
import in.co.eko.fundu.requests.UpdateSettingRequest;
import in.co.eko.fundu.services.NearByContactsService;
import in.co.eko.fundu.utils.AppPreferences;
import in.co.eko.fundu.utils.CreditCardUtils;
import in.co.eko.fundu.utils.Fog;
import in.co.eko.fundu.utils.Utils;

public class CheckOutActivity extends FragmentActivity implements FragmentManager.OnBackStackChangedListener {


    Button btnNext;

    public CardFrontFragment cardFrontFragment;
    public CardBackFragment cardBackFragment;
    private ProgressDialog dialog;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private ViewPager viewPager;

    AppPreferences pref;
    CCNumberFragment numberFragment;
    CCNameFragment nameFragment;
    CCValidityFragment validityFragment;
    public CCSecureCodeFragment secureCodeFragment;

    int total_item;
    boolean backTrack = false;

    private boolean mShowingBack = false;

    String cardNumber, cardCVV, cardValidity, cardName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("Please wait...");
        btnNext = (Button)findViewById(R.id.btnNext) ;


        cardFrontFragment = new CardFrontFragment();
        cardBackFragment = new CardBackFragment();

        if (savedInstanceState == null) {
            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, cardFrontFragment).commit();

        } else {
            mShowingBack = (getFragmentManager().getBackStackEntryCount() > 0);
        }

        getFragmentManager().addOnBackStackChangedListener(this);

        //Initializing viewPager
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(4);
        setupViewPager(viewPager);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == total_item)
                    btnNext.setText("SUBMIT");
                else
                    btnNext.setText("NEXT");

                Fog.d("track", "onPageSelected: " + position);

                if (position == total_item) {
                    flipCard();
                    backTrack = true;
                } else if (position == total_item - 1 && backTrack) {
                    flipCard();
                    backTrack = false;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = viewPager.getCurrentItem();
                if (pos < total_item) {
                    viewPager.setCurrentItem(pos + 1);
                } else {
                    checkEntries();
                }

            }
        });


    }

    public void checkEntries() {
//        cardName = nameFragment.getName();
        cardNumber = numberFragment.getCardNumber();
        cardNumber = cardNumber.replace(" ","");
        cardValidity = validityFragment.getValidity();
        cardCVV = secureCodeFragment.getValue();

       /* if (TextUtils.isEmpty(cardName)) {
            Toast.makeText(CheckOutActivity.this, "Enter Valid Name", Toast.LENGTH_SHORT).show();}*/
          if (TextUtils.isEmpty(cardNumber) || !CreditCardUtils.isValid(cardNumber.replace(" ",""))) {
            Toast.makeText(CheckOutActivity.this, "Enter Valid card number", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(cardValidity)||!CreditCardUtils.isValidDate(cardValidity)) {
            Toast.makeText(CheckOutActivity.this, "Enter correct validity", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(cardCVV)||cardCVV.length()<3) {
            Toast.makeText(CheckOutActivity.this, "Enter valid security number", Toast.LENGTH_SHORT).show();
        } else {
              AppPreferences.getInstance(this).putString("cardnumber", cardNumber);
              AppPreferences.getInstance(this).putString("expiry", cardValidity.replace("/",""));
              validate();
              //Toast.makeText(CheckOutActivity.this, "Your card is added", Toast.LENGTH_SHORT).show();
          }
    }

    @Override
    public void onBackStackChanged() {
        mShowingBack = (getFragmentManager().getBackStackEntryCount() > 0);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        numberFragment = new CCNumberFragment();
       // nameFragment = new CCNameFragment();
        validityFragment = new CCValidityFragment();
        secureCodeFragment = new CCSecureCodeFragment();
        adapter.addFragment(numberFragment);
       // adapter.addFragment(nameFragment);
        adapter.addFragment(validityFragment);
        adapter.addFragment(secureCodeFragment);

        total_item = adapter.getCount() - 1;
        viewPager.setAdapter(adapter);

    }

    private void flipCard() {
        if (mShowingBack) {
            getFragmentManager().popBackStack();
            return;
        }
        // Flip to the back.
        //setCustomAnimations(int enter, int exit, int popEnter, int popExit)

        mShowingBack = true;

        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.animator.card_flip_right_in,
                        R.animator.card_flip_right_out,
                        R.animator.card_flip_left_in,
                        R.animator.card_flip_left_out)
                .replace(R.id.fragment_container, cardBackFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        Utils.hideSoftKeyboard(this);
        int pos = viewPager.getCurrentItem();
        if (pos > 0) {
            viewPager.setCurrentItem(pos - 1);
        }
        else
            Utils.hideSoftKeyboard(this);
            //super.onBackPressed();
    }
    private void validate() {


            callFinalRegAPI();

    }
    public void nextClick() {
        btnNext.performClick();
    }


    void callFinalRegAPI() {

//
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("custid", AppPreferences.getInstance(this).getString("custid"));
            jsonObject.put("mobile", Utils.appendCountryCodeToNumber(CheckOutActivity.this, FunduUser.getContactId()));
            jsonObject.put("country_shortname", FunduUser.getCountryShortName());
            jsonObject.put("question_id", AppPreferences.getInstance(this).getString(Constants.SEC_QUESTION));
            jsonObject.put("answer", AppPreferences.getInstance(this).getString(Constants.SEC_ANSWER));
            // jsonObject.put("accno", AccountNumber);
            jsonObject.put("accno", AppPreferences.getInstance(this).getString("AccountNumber"));
            //jsonObject.put("card_number", cardnumber);
            jsonObject.put("card_number", AppPreferences.getInstance(this).getString("cardnumber"));
            // jsonObject.put("card_expiry", expiry);
            jsonObject.put("card_expiry", AppPreferences.getInstance(this).getString("expiry"));
            jsonObject.put("cvv", cardCVV);
            jsonObject.put("email", AppPreferences.getInstance(this).getString(Constants.PrefKey.EMAIL));
            jsonObject.put("fundu_pin", AppPreferences.getInstance(this).getString("FunduPin"));
            jsonObject.put("contact_type", AppPreferences.getInstance(this).getString(Constants.CONTACT_TYPE_PA));
//            jsonObject.put("type", "0");
            if (AppPreferences.getInstance(this).getString(Constants.CONTACT_TYPE_PA).equalsIgnoreCase("AGENT")) {
//                jsonObject.put(Constants.DAYS, "SUNDAY,MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY");
//                jsonObject.put(Constants.OPENING_TIME, "07:00");
//                jsonObject.put(Constants.CLOSING_TIME, "20:00");
            }
//            "days": "Sunday,monday,tuesday",  "opening_time": "07:00",  "closing_time": "20:00",
        } catch (Exception e) {
            e.printStackTrace();
        }

        dialog.show();
        FinalRegistrationRequest request = new FinalRegistrationRequest(CheckOutActivity.this, jsonObject);
        request.setParserCallback(new FinalRegistrationRequest.OnFinalRegistationResults() {
            @Override
            public void onFinalRegistationResponse(JSONObject response) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                try {

                    JSONObject jsonObject = new JSONObject(response.toString());
                    String message_success = jsonObject.optString("message");

                    if (message_success.equals("Successfully updated") || message_success.equals("Customer Successfully Verified")) {
                        JSONObject innerObject = jsonObject.getJSONObject("data");
                        if (innerObject.has("verified")) {

                            String custid = innerObject.optString("custid");
                            AppPreferences.getInstance(CheckOutActivity.this).putString("custid", custid);
                            FunduUser.setCustomerId(custid);
                            if (innerObject.optString(Constants.ALLOW_WITHDRAW).equalsIgnoreCase("No") && innerObject.optString("contact_type")
                                    .equalsIgnoreCase("AGENT")) {
                                AppPreferences.getInstance(CheckOutActivity.this).putString(Constants.ALLOW_WITHDRAW, innerObject.optString(Constants.ALLOW_WITHDRAW));
                            } else
                                AppPreferences.getInstance(CheckOutActivity.this).putString(Constants.ALLOW_WITHDRAW, "Yes");
                            Fog.e("merchant_img_url", "merchant_img_url" + innerObject.optString("merchant_img_url"));
                            if (!(innerObject.optString("merchant_img_url") == null || innerObject.optString("merchant_img_url").equalsIgnoreCase(""))) {
                                Fog.e("ImageUrl", innerObject.optString("merchant_img_url"));
                                AppPreferences.getInstance(CheckOutActivity.this).putString(Constants.PROFILE_PIC_URL, V1API.BASE_URL + "/v2/customers/getMerchantImage/" + FunduUser.getContactId());
                            }
                            if (innerObject.optString(Constants.OPENING_TIME) != null) {

                            }
//                            localhost:9999/v2/customers/getMerchantImage/700000015
                            String businessname = innerObject.optString("business_name");
                            if (!(businessname == null || businessname.equalsIgnoreCase("") || businessname.isEmpty()) && pref.getString(Constants.CONTACT_TYPE_PA)
                                    .equalsIgnoreCase("AGENT"))
                                AppPreferences.getInstance(CheckOutActivity.this).putString(Constants.NAME, businessname);
                            syncData();

                        } else {
                            Toast.makeText(CheckOutActivity.this, message_success, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(CheckOutActivity.this, message_success, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            private void syncData() {
                FunduUser.setUserLoginOrRegister(true);
                FunduUser.setUserMobile(FunduUser.getContactId());
                saveUserContact();
                readContacts();
            }

            private void readContacts() {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
                } else {
                    triggerServices();
                }
            }

            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }

                }
            };


            private void saveUserContact() {
                AppPreferences.getInstance(CheckOutActivity.this).putBoolean(Constants.IS_USER_LOGGED_IN, true);
                AppPreferences.getInstance(CheckOutActivity.this).putBoolean(Constants.IS_MOBILE_VERIFIED, true);
                FunduUser.setUserMobileVerified(true);
                Intent intent = new Intent(CheckOutActivity.this,HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                Utils.showShortToast(CheckOutActivity.this, "Registration Completed Successfully!");
            }

            private void triggerServices() {
//        dialog.show();
                startService(new Intent(CheckOutActivity.this, NearByContactsService.class));
               // SyncContactsIntentService.startService(CheckOutActivity.this, handler, mParam1.getDeviceId());

                UpdateSettingRequest updateSettingRequest = new UpdateSettingRequest(CheckOutActivity.this);
                updateSettingRequest.setData(true, true, true, Request.Method.POST);
                updateSettingRequest.start();
            }

            @Override
            public void onFinalRegistationError(VolleyError error) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
        request.start();
    }



}
