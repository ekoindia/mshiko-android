package in.co.eko.fundu.fragments;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import in.co.eko.fundu.R;
import in.co.eko.fundu.activities.HomeActivity;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.constants.V1API;
import in.co.eko.fundu.event.CustomEditTextEvent;
import in.co.eko.fundu.event.OtpReceiverEvent;
import in.co.eko.fundu.models.Contact;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.requests.ContactCreateUpdateRequest;
import in.co.eko.fundu.requests.UpdateSettingRequest;
import in.co.eko.fundu.requests.VerifyOtpRequest;
import in.co.eko.fundu.services.NearByContactsService;
import in.co.eko.fundu.utils.AppPreferences;
import in.co.eko.fundu.utils.Fog;
import in.co.eko.fundu.utils.FunduAnalytics;
import in.co.eko.fundu.utils.Utils;
import in.co.eko.fundu.views.Pinview;

public class VerifyCodeFragment extends BaseFragment implements View.OnClickListener,
        VerifyOtpRequest.OnVerifyOtpResults, AppCompatDialog.OnDismissListener,  ContactCreateUpdateRequest.OnContactResults {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    public static int person_agent = 1; //person = 1, agent = 2;
    private Button submitCodeButton;
    private Pinview fPin;
    private String fPinNum;
    private static AppPreferences pref;
    static ProgressDialog dialog;
    static VerifyOtpRequest verifyOtpRequest;
    private ContactCreateUpdateRequest contactCreateUpdateRequest;
    LinearLayout rel;
    TextView text_incorrect_otp,textViewSim,textViewSimNumber,resend;
    EditText[] editTexts;
    String otp = "";
    LinearLayout llCode;
    private int otpLength = 4;
    CountDownTimer resendTimer,mOtpTimer;


    String contact_typepa = "PERSON";
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

        }
    };
    private ImageView verifyImageView,imageViewSim;
    private TextView tvtime, textTitleTextView;
    private TextView textMessageTextView;


    public VerifyCodeFragment() {
        // Required empty public constructor
    }

    public static VerifyCodeFragment newInstance(Contact param1, String param2) {
        VerifyCodeFragment fragment = new VerifyCodeFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref= FunduUser.getAppPreferences();
        EventBus.getDefault().register(this);
        verifyOtpRequest = new VerifyOtpRequest(getActivity());
        verifyOtpRequest.setParserCallback(this);
        contactCreateUpdateRequest = new ContactCreateUpdateRequest(getActivity(),pref);
        contactCreateUpdateRequest.setParserCallback(this);
        dialog = new ProgressDialog(getActivity());
        dialog.setCancelable(false);
        dialog.setMessage("Please wait...");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Fog.d("onCreateView",""+"onCreateView");
        View view = inflater.inflate(R.layout.fragment_verify_code, container, false);
        InputMethodManager inputMethodManager = (InputMethodManager)getActivity(). getSystemService(Activity.INPUT_METHOD_SERVICE);
        View cur_focus = getActivity().getCurrentFocus();
        if (cur_focus != null) {
            inputMethodManager.hideSoftInputFromWindow(cur_focus.getWindowToken(), 0);
        }
        verifyImageView = (ImageView) view.findViewById(R.id.verifyImageView);
        fPin = (Pinview)view.findViewById(R.id.fPin);
        tvtime = (TextView) view.findViewById(R.id.tvtime);
        rel = (LinearLayout) view.findViewById(R.id.rel);
        textMessageTextView = (TextView) view.findViewById(R.id.textMessageTextView);
        textViewSim = (TextView) view.findViewById(R.id.textView_sim);
        resend = (TextView) view.findViewById(R.id.resend);
        textViewSimNumber = (TextView) view.findViewById(R.id.textView_sim_number);
        imageViewSim = (ImageView) view.findViewById(R.id.img_sim);
        text_incorrect_otp = (TextView) view.findViewById(R.id.text_incorrect_otp);
        submitCodeButton = (Button) view.findViewById(R.id.submitCodeButton);
        String simName,simNumber;
        simName = pref.getString(Constants.SimName);
        simNumber = pref.getString(Constants.SimNumber);
        resend.setOnClickListener(this);
        resend.setEnabled(false);
        submitCodeButton.setOnClickListener(this);
        if(FunduUser.getCountryShortName().equalsIgnoreCase("KEN")){
            textViewSim.setVisibility(View.GONE);
            textViewSimNumber.setVisibility(View.GONE);
            imageViewSim.setVisibility(View.GONE);
            otpLength = 4;
        }
        else{
            otpLength = 3;
            if(!simName.isEmpty()&&!simNumber.isEmpty()){
                if(simName.contains("- Sim1")) textViewSim.setText(simName);
                else textViewSim.setText(simName+" - Sim" +1);
                textViewSimNumber.setText(simNumber);
                displaySimImage(simName,imageViewSim);

            }
            else {
                textViewSim.setVisibility(View.GONE);
                textViewSimNumber.setVisibility(View.GONE);
                imageViewSim.setVisibility(View.GONE);
            }
        }
        fPin.setPinLength(otpLength);
        fPin.requestFocus();
        Utils.toggleSoftKeyboard(getActivity());
        fPin.setPinViewEventListener(new Pinview.PinViewEventListener() {
            @Override
            public void onDataEntered(Pinview pinview, boolean fromUser) {
                fPinNum = pinview.getValue();
                Utils.hideSoftKeyboard(getActivity());
                if (validateOtp())
                    submitCodeButton.setEnabled(true);
                Fog.d("fPinNum","******"+fPinNum);
            }
        });

        Fog.e("merchant_img_old", pref.getString(Constants.PROFILE_PIC_URL));
        mOtpTimer = new CountDownTimer(60000, 1000) { // adjust the milli seconds here

            public void onTick(long millisUntilFinished) {
                tvtime.setText("00:" + String.format("%d sec",
//                        TimeUnit.MILLISECONDS.toMinutes( millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }

            public void onFinish() {
                tvtime.setText("Please enter the code manually");
                resend.setEnabled ( true );
                try {

                    if (getActivity() != null) {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }.start();
        FunduAnalytics.getInstance(getActivity()).sendScreenName("OTPVerification");
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        displayMessageToUser();
    }


    private void displayMessageToUser() {

        boolean hasPermission = checkReadSMSPermission();
        if (!hasPermission) {
            textMessageTextView.setText(String.format(getString(R.string.onetimepass),FunduUser.getContactId())
                    );

        } else {
            textMessageTextView.setText(String.format(getString(R.string.onetimepasspermission),FunduUser.getContactId()));
        }
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.submitCodeButton) {

            //Fog.d("" + "otp", "otp" + otp);
            if(validateOtp()){
              if (Utils.isNetworkAvailable(getActivity())) {
                    dialog.show();
                  sendVerifyOtpRequest();
                }
            }
        }
        else if(v.getId()==R.id.resend){

            if (Utils.isNetworkAvailable(getActivity()))
                if(resend.isEnabled ()) {
                    callRegisterWebService ();
                    updateRetryUi ();
                }

        }
    }
    private void sendVerifyOtpRequest(){
        verifyOtpRequest.setData(FunduUser.getContactIDType(), FunduUser.getContactId(), otp, FunduUser.getCountryShortName());
        verifyOtpRequest.start();
    }



   /* @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() == 0) {
            submitCodeButton.setEnabled(false);
        } else {
            submitCodeButton.setEnabled(true);
        }

        if (s.length() == 1) {
            //codeEditTextTwo.requestFocus();
        }


    }

    @Override
    public void afterTextChanged(Editable s) {

    }
*/
    public void recivedSms(String message) {
        Fog.e("recived", message);
        if (!(message == null || message.equals(""))) {
            try {
               // codeEditText.setText(message);
                fPin.setValue(message);
                fPinNum = message;
              /* for (int i = 0; i < editTexts.length; i++) {


                    //editTexts[i].setText(String.valueOf(message.charAt(i)));
                    fPin.setValue(message);

                }*/
                validateOtp();
                dialog.show();
                sendVerifyOtpRequest();
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }

    private void onCodeMismatch(String msg) {

        if(msg != null && msg.contains ( "limit exhausted" )){
            //Tell user too many incorrect attempts and exit the app
            getView ().findViewById ( R.id.toomanyattempts ).setVisibility ( View.VISIBLE );
            getView ().findViewById ( R.id.otplayout ).setVisibility ( View.GONE );
            getView ().findViewById ( R.id.okButton ).setOnClickListener ( new View.OnClickListener () {
                @Override
                public void onClick(View v) {
                    getActivity ().finish ();
                }
            } );
            return;
        }

        updateUI();
        submitCodeButton.setEnabled(false);
    }

    private void updateUI() {

        text_incorrect_otp.setVisibility(View.VISIBLE);
        submitCodeButton.setEnabled(false);
        new CountDownTimer(2000, 1000) { // adjust the milli seconds here

            public void onTick(long millisUntilFinished) {
                text_incorrect_otp.setVisibility(View.VISIBLE);

                fPin.resetValues();
                fPin.setPinBackgroundRes(R.drawable.code_mismatch);
            }

            public void onFinish() {

                text_incorrect_otp.setVisibility(View.INVISIBLE);
                fPin.requestFocus();
                fPin.resetValues();
                fPin.setPinBackgroundRes(R.drawable.code_letter_back);

            }
        }.start();
    }

    @Override
    public void onVerifyOtpResponse(JSONObject jsonObject) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        try {
            if (jsonObject.has("status")) {
                String success = jsonObject.optString("status");
                if (success.equalsIgnoreCase("SUCCESS")) {
                    if (jsonObject.optString("message").equalsIgnoreCase("Successfully updated")) {

                        JSONObject innerObject = jsonObject.getJSONObject("data");
                        if (innerObject.has("verified")) {
                            boolean isverified = innerObject.optBoolean("verified");
                            String custid = innerObject.optString("custid");
                            FunduUser.setCustomerId(custid);
                            FunduUser.setAuthToken ( jsonObject.optString ( "fundu_auth_token" ) );

//                            contact_type
                            if (innerObject.optString(Constants.COUNTRY_SHORTCODE).equals("KEN")) {
                                if (isverified) {
                                    pref.putString(Constants.CONTACT_TYPE_PA, innerObject.optString("contact_type"));
                                    if (innerObject.optString("contact_type").equalsIgnoreCase("AGENT")) {
                                        String businessname = innerObject.optString("business_name");
                                        if (!(businessname == null || businessname.equalsIgnoreCase("")))
                                            pref.putString(Constants.NAME, businessname);
                                        if (innerObject.optString(Constants.OPENING_TIME) != null) {
                                            pref.putString(Constants.DAYS, innerObject.optString(Constants.DAYS));
                                            pref.putString(Constants.OPENING_TIME, innerObject.optString(Constants.OPENING_TIME));
                                            pref.putString(Constants.CLOSING_TIME, innerObject.optString(Constants.CLOSING_TIME));
                                        }
                                    }
                                    Fog.e("merchant_img_url", "merchant_img_url" + innerObject.optString("merchant_img_url"));
                                    if (!(innerObject.optString("merchant_img_url") == null || innerObject.optString("merchant_img_url").equalsIgnoreCase(""))) {
                                        Fog.e("VERIFY IMGURL", innerObject.optString("merchant_img_url"));
                                        pref.putString(Constants.PROFILE_PIC_URL, V1API.BASE_URL + "/v2/customers/getMerchantImage/" + FunduUser.getContactId());
                                    }
                                    if (innerObject.optString(Constants.ALLOW_WITHDRAW).equalsIgnoreCase("No") && innerObject.optString("contact_type").equalsIgnoreCase("AGENT")) {
                                        pref.putString(Constants.ALLOW_WITHDRAW, innerObject.optString(Constants.ALLOW_WITHDRAW));
                                    } else
                                        pref.putString(Constants.ALLOW_WITHDRAW, "Yes");
                                    syncData();
                                    saveUserContact();
                                    showThanksFragment();
                                } else {
                                    String contact_type = innerObject.optString("contact_type");
                                    if (contact_type.equalsIgnoreCase("AGENT")) {
//                                        merchantIndividual = new MerchantIndividual(getActivity());
//                                        merchantIndividual.setOnDismissListener(this);
//                                        merchantIndividual.show();
                                        contact_typepa = "AGENT";

                                        Bundle bundle = new Bundle();
                                        bundle.putString(FRAGMENT_NAME, "MerchantRegistrationFragment");
                                        onButtonPressed(bundle);
                                        pref.putString(Constants.CONTACT_TYPE_PA, contact_typepa);
                                    } else {
                                        gotoCreateFunduPinFragment();
                                        pref.putString(Constants.CONTACT_TYPE_PA, contact_typepa);
                                    }
                                }
                            } else {
                                String vpa = innerObject.optString("vpa");
                                if(vpa != null && vpa.length() != 0){
                                    FunduUser.setVpa(vpa);
                                }
                                syncData();
                                saveUserContact();
                                FunduAnalytics.getInstance(getActivity()).sendAction("Registration","PhoneNumberVerified");
                                if(vpa == null || vpa.length() == 0){
                                    Bundle bundle = new Bundle();
                                    bundle.putString(FRAGMENT_NAME, "IntroductionToUPI");
                                    onButtonPressed(bundle);
                                }
                                else{
                                    showThanksFragment();
                                }
                            }
                        } else {
                            onCodeMismatch(jsonObject.optString("message"));
                        }
                    } else {
                        onCodeMismatch(jsonObject.optString("message"));
                    }
                } else {
                    onCodeMismatch(jsonObject.optString("message"));
                }
            }
//            String message_success = jsonObject.optString("message");
//
//            if (message_success.equals("Successfully updated")){
//
//            }
//            else {
////                Toast.makeText(getContext(),message_success,Toast.LENGTH_LONG).show();
//
//            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void syncData() {
        FunduUser.setUserLoginOrRegister(true);
        //readContacts();
    }

    @Override
    public void onVerifyOtpError(VolleyError error) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        Fog.e("OTP Error", error.toString());
//        Toast.makeText(getContext(),"OTP Error",Toast.LENGTH_LONG).show();
        onCodeMismatch("Verify Error!");
    }

    private void gotoCreateFunduPinFragment() {

        pref.putBoolean(Constants.SENT_TOKEN_TO_SERVER, true);
        Bundle bundle = new Bundle();
        bundle.putString(FRAGMENT_NAME, "CreateFunduPinFragment");
        onButtonPressed(bundle);
    }

    private void readContacts() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            triggerServices();
        }
    }

    private void triggerServices() {

        getActivity().startService(new Intent(getActivity(), NearByContactsService.class));
        //SyncContactsIntentService.startService(getActivity(), handler,Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID));
        UpdateSettingRequest updateSettingRequest = new UpdateSettingRequest(getContext());
        updateSettingRequest.setData(true, true, true, Request.Method.POST);
        updateSettingRequest.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        Utils.hideSoftKeyboard(getActivity());
    }

    private void saveUserContact() {
        pref.putBoolean(Constants.IS_USER_LOGGED_IN, true);
        pref.putBoolean(Constants.IS_MOBILE_VERIFIED, true);
        pref.putString(Constants.MOBILE, FunduUser.getContactId());
        FunduUser.setUserMobileVerified(true);

    }
    private void showThanksFragment(){
        /*Bundle bundle = new Bundle();
        bundle.putSerializable(Contact.class.getSimpleName(), mParam1);
        bundle.putString(FRAGMENT_NAME, "ThanksFragment");
        onButtonPressed(bundle);*/
        if(resendTimer != null){
            resendTimer.cancel();
        }
        if(mOtpTimer != null){
            mOtpTimer.cancel ();
            mOtpTimer = null;
        }
        Intent intent = new Intent();
        getActivity().setResult(-5, intent);
        getActivity().finish();
        startActivity(new Intent(getActivity(), HomeActivity.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                triggerServices();
            } else {
                Toast.makeText(getActivity(), "Until you grant the permission, we can not sync your contact", Toast.LENGTH_SHORT).show();
            }
        }
    }

   /* @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        boolean handled = false;
        Utils.hideSoftKeyboard(getActivity());

        if (actionId == EditorInfo.IME_ACTION_NEXT) {
            validateOtp();
            if (otp != null)
                submitCodeButton.setEnabled(true);
            if (!TextUtils.isEmpty(otp)) {

                if (Utils.isNetworkAvailable(getActivity())) {
                    dialog.show();
                    verifyOtpRequest.setData(mParam1.getContactIdType(), mParam1.getContactId(), otp, mParam1.getCountry_shortname());
                    verifyOtpRequest.start();
                }

            } else {
                Toast.makeText(getActivity(), "Please Enter OTP Code.", Toast.LENGTH_SHORT).show();
            }
            handled = true;
        }


        return handled;
    }
*/
    @Override
    public void onDismiss(DialogInterface dialog) {
        Fog.e("Dismss", person_agent + "");
        contact_typepa = "PERSON";
        if (person_agent == 2) {
            contact_typepa = "AGENT";
            Bundle bundle = new Bundle();
            bundle.putString(FRAGMENT_NAME, "MerchantRegistrationFragment");
            onButtonPressed(bundle);
            pref.putString(Constants.CONTACT_TYPE_PA, contact_typepa);
        } else {
            gotoCreateFunduPinFragment();
            pref.putString(Constants.CONTACT_TYPE_PA, contact_typepa);
        }
    }


    private boolean checkReadSMSPermission() {

        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && getActivity().checkSelfPermission(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED;
    }


    /**
     *
     * @return true if valid and false if not valid
     */
    public boolean validateOtp() {


       // otp = customDynamicEditText.getText();
        otp = fPinNum;
        if(otp.length() < otpLength){
            Toast.makeText(getActivity(), "Please Enter correct OTP Code.", Toast.LENGTH_SHORT).show();
            otp = "";
            return false;
        }
        if(otp.length() == otpLength){
            return true;
        }


        return false;

    }


    /*@Override
    public void onTextChange(int position) {
        if(position == otpLength - 1){
            submitCodeButton.setEnabled(true);
        }
        else{
            submitCodeButton.setEnabled(false);
        }
    }*/

    @Override
    public void onContactResponse(Contact contact) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }

    }

    @Override
    public void onContactError(VolleyError error) {
        dialog.hide();
    }

    public void setSelection(EditText editText){
        editText.setSelection(editText.getText().length());
    }
    /**
     * Event Bus events handlers
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOtpReceived(OtpReceiverEvent event) {
        if(!event.getOtp().isEmpty()){
            Fog.d("onOtpReceived","onOtpReceived"+event.getOtp());

            recivedSms(event.getOtp());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceivedCodeFromEditTetx(CustomEditTextEvent event) {
        if(!event.getCode().isEmpty()){
            Fog.d("onOtpReceived","onOtpReceived"+event.getCode());
            otp = event.getCode();
            submitCodeButton.setEnabled(true);

        }
    }




    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if(mOtpTimer != null)
            mOtpTimer.cancel ();
    }




    private void displaySimImage(String s,ImageView imageView) {
        if(s.trim().contains("Airtel"))imageView.setImageResource(R.drawable.ic_airtel);
        else if(s.trim().contains("Jio 4G"))imageView.setImageResource(R.drawable.ic_jio);
        else if(s.trim().contains("MTS"))imageView.setImageResource(R.drawable.ic_mts);
        else if(s.trim().contains("Reliance"))imageView.setImageResource(R.drawable.ic_reliance);
        else if(s.trim().contains("MTNL"))imageView.setImageResource(R.drawable.ic_mtnl);
        else if(s.trim().contains("Vodafone"))imageView.setImageResource(R.drawable.ic_vodafone);
        else if(s.trim().contains("Aircel"))imageView.setImageResource(R.drawable.ic_aircel);
        else if(s.trim().contains("BSNL"))imageView.setImageResource(R.drawable.ic_bsnl);
        else if(s.trim().contains("Docomo"))imageView.setImageResource(R.drawable.ic_docomo);
        else if(s.trim().contains("Safaricom"))imageView.setImageResource(R.drawable.ic_safaricom);
        else if(s.trim().contains("yu"))imageView.setImageResource(R.drawable.ic_telkom);
        else if(s.trim().contains("Equitel"))imageView.setImageResource(R.drawable.ic_equitel);
    }



    private void callRegisterWebService() {

        dialog.show();
        try{
            contactCreateUpdateRequest.setContact();
            contactCreateUpdateRequest.start();
            contactCreateUpdateRequest.setParserCallback(new ContactCreateUpdateRequest.OnContactResults() {
                @Override
                public void onContactResponse(Contact contact) {
                    dialog.hide();
                }

                @Override
                public void onContactError(VolleyError error) {
                    dialog.hide();
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public void updateRetryUi(){
        if(resendTimer != null){
            resendTimer.cancel();
            resendTimer = null;
        }
        resendTimer =  new CountDownTimer(60000, 1000) { // adjust the milli seconds here

            public void onTick(long millisUntilFinished) {
                if(getActivity() == null){
                    return;
                }
                resend.setEnabled(false);
                resend.setText("Retry in " + String.format("%d sec",
//                        TimeUnit.MILLISECONDS.toMinutes( millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }

            public void onFinish() {
                if(getActivity() == null){
                    return;
                }
                resend.setText("Resend");
                resend.setEnabled(true);

                try {

                    if (getActivity() != null) {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
