package in.co.eko.fundu.fragments;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.R;
import in.co.eko.fundu.activities.HomeActivity;
import in.co.eko.fundu.activities.UserOnboardingActivity;
import in.co.eko.fundu.adapters.CountryMobileSpinnerAdapter;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.models.Contact;
import in.co.eko.fundu.models.CountryMobile;
import in.co.eko.fundu.requests.ContactCreateUpdateRequest;
import in.co.eko.fundu.utils.Fog;
import in.co.eko.fundu.utils.FunduAnalytics;
import in.co.eko.fundu.utils.Utils;
import in.co.eko.fundu.views.CustomProgressDialog;

public class EnterPhoneNumberFragment extends BaseFragment implements View.OnClickListener, TextWatcher,
        ContactCreateUpdateRequest.OnContactResults, TextView.OnEditorActionListener, AdapterView.OnItemSelectedListener {

    public static CustomProgressDialog dialog;
    private ContactCreateUpdateRequest request;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    TextView validation;
    private String locationCountry = "India";
    private String phoneNumber;
    private Boolean isclickavailable = false;
    private CountryMobileSpinnerAdapter countries_adapter;
    private Spinner country_spinner;
    private String countryShortname;



    public static EnterPhoneNumberFragment newInstance() {
        EnterPhoneNumberFragment fragment = new EnterPhoneNumberFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private void getCountriesFromNetwork() {

        Activity activity= getActivity();
        if(activity!=null && activity instanceof UserOnboardingActivity){
            dialog.show();
            ((UserOnboardingActivity)activity).getCountries();
        }
    }


    public EnterPhoneNumberFragment() {
        // Required empty public constructor
    }



    // called in both cases error or response
    public void onCountriesResponse(CountryMobile[] arrayList) {

        if (dialog.isShowing())
            dialog.dismiss();

        List<CountryMobile> list = Arrays.asList(arrayList);
        List<CountryMobile> nl = new ArrayList<>();
        List<CountryMobile> pl = new ArrayList<>();
        List<CountryMobile> finalList = new ArrayList<>();

        if(arrayList!=null && arrayList.length>0){

            for (int i = 0; i< list.size(); i++){
                if (list.get(i).getCountryName().equalsIgnoreCase(locationCountry)){
                    nl.add(list.get(i));
                }
                else{
                    pl.add(list.get(i));
                }
            }
            if (nl.size()>0){
                finalList.addAll(nl);
                finalList.addAll(pl);
            }else{
                CountryMobile bean=new CountryMobile();
                bean.setCountryName(locationCountry);
                bean.setStartsWith("9");
                bean.setCountryCode("--");
                bean.setLength(10);
                bean.setSymbol("IP");
                bean.setEnable(false);
                bean.setcountry_shortname("UN");
                finalList.add(bean);
//              Fog.e("Country DNM ", bean.getCountryName());
                finalList.addAll(pl);
                Fog.d("FundCountry",""+FunduUser.getCountryShortName());
            }

            CountryMobile[] array = finalList.toArray(new CountryMobile[finalList.size()]);

            countries_adapter = new CountryMobileSpinnerAdapter(getActivity(),android.R.layout.simple_list_item_1,array);
            country_spinner.setAdapter(countries_adapter);
            int j =0;
            for(int i=0;i<finalList.size();i++){
                if(finalList.get(i).getCountryName().equalsIgnoreCase(locationCountry)){
                    j=i;
                }
            }
            country_spinner.setSelection(j);
            country_spinner.setOnItemSelectedListener(this);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fog.d("CountryID()",""+Utils.getCountryID());
        dialog = new CustomProgressDialog(getActivity());
        request = new ContactCreateUpdateRequest(getActivity(), pref);
        request.setParserCallback(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enter_phone_number_change, container, false);
        // View view = inflater.inflate(R.layout.layout_common_enter_phone, container, false);
        Fog.d("FundCountry",""+Utils.getCountryID());
        sendCodeButton = (Button) view.findViewById(R.id.sendCodeButton);
        phoneEditText = (EditText) view.findViewById(R.id.phoneEditText);
        country_spinner = (Spinner) view.findViewById(R.id.spinner_country);
        countryCode = (EditText)view.findViewById(R.id.country_code);
        validation = (TextView)view.findViewById(R.id.validation);
        phoneEditText.requestFocus();
        Utils.toggleSoftKeyboard(getActivity());
        phoneEditText.addTextChangedListener(this);
        sendCodeButton.setOnClickListener(this);
        phoneEditText.setOnClickListener(this);
        phoneEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE ) {
                    CountryMobile bean=countries_adapter.getCountryItem(country_spinner.getSelectedItemPosition());
                    checkvalidations(bean);
                    return true;
                }
                return false;
            }
        });
        if(Utils.getCountryID().equalsIgnoreCase("IN")){
            locationCountry = "India";
        }
        else{
            locationCountry = "Kenya";
        }
        getCountriesFromNetwork();
        FunduAnalytics.getInstance(getActivity()).sendScreenName("EnterPhoneNumber");
        return view;
    }

    private Button sendCodeButton;


    private EditText phoneEditText, countryCode;

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sendCodeButton) {
            CountryMobile bean=countries_adapter.getCountryItem(country_spinner.getSelectedItemPosition());
            checkvalidations(bean);

        }
        if (v.getId() == R.id.phoneEditText){
            if (isclickavailable){

                Fog.e("POPUP", "YES");
            }
            else{
                Fog.e("POPUP", "NO");
                showPopUp();
            }
        }
    }

    void showPopUp(){
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        new AlertDialog.Builder(getActivity())
                .setTitle("Fundu Alert")
                .setMessage("Server error occurred. Please try again.")/*+". Please write us to fundu@gmail.com to enjoy our service or to become our partner.*/
                .setCancelable(true)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Whatever...
                        dialog.dismiss();
                    }
                }).show();
    }

    private void callRegisterWebService() {

        dialog.show();
       // Fog.d("mParam1",""+mParam1.getName());
        //Fog.d("mParam1",""+mParam1.get);
      //  if(mParam1==null)mParam1 = new Contact();

       /* mParam1 = FunduUser.getUser();*/

        try{
            phoneNumber = phoneEditText.getText().toString().trim();/*Utils.appendCountryCodeToNumber(getActivity(), phoneEditText.getText().toString())*/
            FunduUser.setContactId(phoneNumber);
            FunduUser.setUserMobile(phoneNumber);
            request.setContact();
            request.start();
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (country_spinner != null) {
            int select = country_spinner.getSelectedItemPosition();
            if (select != -1) {

                if (s.length() == 0) {
                    sendCodeButton.setEnabled(false);
                } else {
                    sendCodeButton.setEnabled(true);
                }
                if (count == countries_adapter.getCountryItem(select).getLength()) {
                    InputMethodManager imm = (InputMethodManager)
                            getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    }
                }
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onContactResponse(Contact contact) {

        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        String mobile = phoneEditText.getText().toString();
        Fog.d("mobile","mobile"+contact.toString());
        FunduUser.setUserMobileVerified(contact.isVerified());
        pref.putString(Constants.MOBILE, mobile);
        pref.putString(Constants.ID_TYPE,contact.getContactType());
        readContacts();
    }

    private void readContacts() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity()!=null && getActivity().checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            checkContactVerified();
        }
    }

    private void checkContactVerified() {

        gotoVerifyFragment();
    }



    @Override
    public void onContactError(VolleyError volleyError) {
        try{
            String error = Utils.getErrorMessage(volleyError);
            Toast.makeText(getActivity(), error,
                    Toast.LENGTH_SHORT).show();
        }catch(Exception e){
            e.printStackTrace();
        }

        if (dialog.isShowing())
            dialog.dismiss();
    }

    void checkvalidations(CountryMobile beans){
        String regexFormat = "^[%s]\\d{%d}$";
        String regexNumber = String.format(regexFormat, beans.getStartsWith().replace(",",""),beans.getLength() -1);
        CountryMobile bean = beans;
        if (bean==null) {
            Toast.makeText(getContext(), "Please select the country first.", Toast.LENGTH_SHORT).show();
        }
        else if (phoneEditText.getText().toString().startsWith("0")){
            Toast.makeText(getContext(), "Your number should not start with 0", Toast.LENGTH_SHORT).show();
        }
        else if (phoneEditText.length() != bean.getLength()) {
            Toast.makeText(getContext(), "Please enter " + bean.getLength() + " digit mobile number.", Toast.LENGTH_SHORT).show();
        }

        else if (bean.getcountry_shortname().equalsIgnoreCase("KEN")){
            if(!bean.getStartsWith().equals("") && !phoneEditText.getText().toString().trim().startsWith(bean.getStartsWith())) {
                Toast.makeText(getContext(), "Please make sure the number starts with " + bean.getStartsWith(), Toast.LENGTH_SHORT).show();
            }
            else{
                if (Utils.isNetworkAvailable(getActivity()))
                    callRegisterWebService();
            }
        }
        else if (bean.getcountry_shortname().equalsIgnoreCase("IND")){

            if (!(phoneEditText.getText().toString().trim().matches(regexNumber))){
                Fog.e("INDIA", phoneEditText.getText().toString().trim());
                Toast.makeText(getContext(), "Please enter a valid number that starts with " + bean.getStartsWith(), Toast.LENGTH_SHORT).show();
            }
            else if (Utils.isNetworkAvailable(getActivity()))
                callRegisterWebService();
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        boolean handled = false;
        if (actionId == EditorInfo.IME_ACTION_DONE) {

            if(countries_adapter!=null){
                CountryMobile bean=countries_adapter.getCountryItem(country_spinner.getSelectedItemPosition());
                checkvalidations(bean);

            }
            handled = true;
        }
        return handled;
    }

    @Override
    public void onPause() {
        super.onPause();

        Utils.hideSoftKeyboard(getActivity());
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (dialog.isShowing())
                dialog.dismiss();

            Intent intent = new Intent(getActivity(), HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(Constants.IS_MOBILE_VERIFIED, true);
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            getActivity().startActivity(intent);
            getActivity().finish();
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                checkContactVerified();
                // triggerServices();
            } else {
                Toast.makeText(getActivity(), "Until you grant the permission, we can not sync your contact", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void gotoVerifyFragment() {

        pref.putBoolean(Constants.SENT_TOKEN_TO_SERVER, true);
        Bundle bundle = new Bundle();
        bundle.putString(FRAGMENT_NAME, "VerifyCodeFragment");
        onButtonPressed(bundle);
    }


    private void alterNumberValidation(int i) {

        CountryMobile selectedCountryMobile= countries_adapter.getCountryItem(i);
        locationCountry = selectedCountryMobile.getCountryName();
        if (selectedCountryMobile.isEnable()) {
//            phoneEditText.setFocusable(true);
            isclickavailable = true;
            phoneEditText.setText("");
            countryCode.setText(selectedCountryMobile.getCountryCode());
//            phoneEditText.setEms(selectedCountryMobile.getLength());
            validation.setText("Hint: Your number should start with " + selectedCountryMobile.getStartsWith() + " and not with 0.");
//        phoneEditText.setMaxEms(selectedCountryMobile.getLength());
        }
        else{
            isclickavailable = false;
            phoneEditText.setText("");
//            phoneEditText.setFocusable(false);
            countryCode.setText(selectedCountryMobile.getCountryCode());
            validation.setText("This app is not available in "+ locationCountry);
        }

    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        // change the validation according to selected country
        ((TextView) adapterView.getChildAt(0)).setGravity(Gravity.CENTER_VERTICAL);
        alterNumberValidation(i);
        countryShortname = countries_adapter.getCountryItem(i).getcountry_shortname();
        phoneEditText.setMaxEms(countries_adapter.getCountryItem(i).getLength());
        phoneEditText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(countries_adapter.getCountryItem(i).getLength())});
        FunduUser.setCountryShortName(countryShortname);
        FunduUser.setCountryMobileCode(countries_adapter.getCountryItem(i).getCountryCode());
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    @Override
    public boolean onBackPressed() {
        return false;
    }
}