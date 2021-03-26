package in.co.eko.fundu.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.co.eko.fundu.R;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.views.customviews.CreditCardEditText;
import in.co.eko.fundu.constants.V1API;
import in.co.eko.fundu.models.Contact;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.requests.FinalRegistrationRequest;
import in.co.eko.fundu.requests.UpdateSettingRequest;
import in.co.eko.fundu.services.NearByContactsService;
import in.co.eko.fundu.utils.Fog;
import in.co.eko.fundu.utils.Utils;

/**
 * Created by Rahul on 12/13/16.
 */

public class CardDetailFragment extends BaseFragment implements TextWatcher, View.OnKeyListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private Contact mParam1;
    private String mParam2;
    private final int mDefaultDrawableResId = R.drawable.creditcard;
    private int mCurrentDrawableResId = 0;
    private Drawable mCurrentDrawable;
    private SparseArray<Pattern> mCCPatterns = null;
    private ProgressDialog dialog;
    private EditText edtExpiryMonth, edtExpiryYear, editCVV, editAccountNo, reenteraccount_number;
    private ImageView back;
    private Button submitbutton,next;
    private EditText[] editTexts ;
    private EditText editText1,editText2,editText3,editText4,editText5,editText6,editText7,editText8,editText9,editText10,
            editText11,editText12,editText13,editText14,editText15,editText16,editText_Month1,editText_Month2,
            editText_Year1,editText_Year2;
    private CreditCardEditText edittextCreditCard;
    private String expiry = "", expirymonth = "", expiryyear = "",confirmAccNumber;
    private String secQuesId, secAnswer, FunduPin;
    static int cvvint = 3;
    private ImageView imageViewCard;
    public static CardDetailFragment newInstance(Contact param1, String param2) {
        CardDetailFragment fragment = new CardDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public CardDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = (Contact) getArguments().getSerializable(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            secAnswer = getArguments().getString("Answer");
            secQuesId = getArguments().getString("QuestionKey");
            FunduPin = getArguments().getString("FunduPin");
            confirmAccNumber = getArguments().getString("AccountNumber");
//            Fog.e("ques_id", secQuesId);
//            Fog.e("answer", secAnswer);
        }
        dialog = new ProgressDialog(getActivity());
        dialog.setCancelable(false);
        dialog.setMessage(getString(R.string.please_wait)+"..");
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.card_fragment, container, false);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        edtExpiryMonth = (EditText) view.findViewById(R.id.editexpiryMonth);
        edtExpiryYear  = (EditText) view.findViewById(R.id.editexpiryYear);
        submitbutton   = (Button) view.findViewById(R.id.submitButton);
        next           = (Button) view.findViewById(R.id.next);
        //back           = (ImageView) view.findViewById(R.id.back);
        imageViewCard  = (ImageView) view.findViewById(R.id.imageViewCard);
        //editCVV = (EditText) view.findViewById(R.id.editCvv);

        editText1 = (EditText) view.findViewById(R.id.editText1);
        editText2 = (EditText) view.findViewById(R.id.editText2);
        editText3 = (EditText) view.findViewById(R.id.editText3);
        editText4 = (EditText) view.findViewById(R.id.editText4);

        editText5 = (EditText) view.findViewById(R.id.editText5);
        editText6 = (EditText) view.findViewById(R.id.editText6);
        editText7 = (EditText) view.findViewById(R.id.editText7);
        editText8 = (EditText) view.findViewById(R.id.editText8);

        editText9 = (EditText) view.findViewById(R.id.editText9);
        editText10 = (EditText) view.findViewById(R.id.editText10);
        editText11= (EditText) view.findViewById(R.id.editText11);
        editText12= (EditText) view.findViewById(R.id.editText12);

        editText13= (EditText) view.findViewById(R.id.editText13);
        editText14= (EditText) view.findViewById(R.id.editText14);
        editText15= (EditText) view.findViewById(R.id.editText15);
        editText16= (EditText) view.findViewById(R.id.editText16);

        editText_Month1= (EditText) view.findViewById(R.id.editText_Month1);
        editText_Month2= (EditText) view.findViewById(R.id.editText_Month2);

        editText_Year1= (EditText) view.findViewById(R.id.editText_Year1);
        editText_Year2= (EditText) view.findViewById(R.id.editText_Year2);

        editTexts = new EditText[]{editText1,editText2,editText3,editText4,
                                   editText5,editText6,editText7,editText8,
                                   editText9,editText10,editText11,editText12,
                                   editText13,editText14,editText15,editText16};

        for(int i=0;i<editTexts.length;i++){

            if(i<15){
                editTexts[i].setOnKeyListener(this);
                editTexts[i].addTextChangedListener(new FocusSwitchingTextWatcher( editTexts[i+1]));
            }

        }

        editText_Month1.addTextChangedListener(new FocusSwitchingTextWatcher(editText_Month2));
        editText_Month2.addTextChangedListener(new FocusSwitchingTextWatcher(editText_Year1));
        editText_Year1.addTextChangedListener(new FocusSwitchingTextWatcher(editText_Year2));

        editText_Month1.setOnKeyListener(this);
        editText_Month2.setOnKeyListener(this);
        editText_Year1.setOnKeyListener(this);
        editText_Year2.setOnKeyListener(this);

        editText1.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        // sedittextCreditCard = (CreditCardEditText) view.findViewById(R.id.card_number);
        editAccountNo = (EditText) view.findViewById(R.id.account_number);
        reenteraccount_number = (EditText) view.findViewById(R.id.reenteraccount_number);



        mCCPatterns = new SparseArray<>();

        mCCPatterns.put(R.drawable.ic_visa_inc_logo, Pattern.compile(
                "^4[0-9]{2,12}(?:[0-9]{3})?$"));
        mCCPatterns.put(R.drawable.mastercard, Pattern.compile(
                "^5[1-5][0-9]{1,14}$"));
        mCCPatterns.put(R.drawable.amex, Pattern.compile(
                "^3[47][0-9]{1,13}$"));
        mCCPatterns.put(R.drawable.rupay, Pattern.compile(
                "^6[0-9]{15}$"));
        mCCPatterns.put(R.drawable.ic_discover_card_logo, Pattern.compile(
                "^6(?:011|5[0-9]{2})[0-9]{3,}$"));
        mCCPatterns.put(R.drawable.dinersclublogo, Pattern.compile(
                "^3(?:0[0-5]|[68][0-9])[0-9]{4,}$"));



        editText_Year2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                validate();
                return false;
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //validate();
                gotoCVVDetailScreen();

            }
        });

       /* back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });*/

//        reenteraccount_number.setFilters(new InputFilter[]{
//                new InputFilter() {
//                    @Override
//                    public CharSequence filter(CharSequence src, int start, int end, Spanned dest, int dstart, int dend) {
//                        if (dend>21){
//                            return "";
//                        }
//                        else if (src.equals("")) {
//                            return src;
//                        }
//                        else if (src.toString().matches("[a-zA-Z0-9 ]+")) {
//                            return src;
//                        }
//                        return "";
//                    }
//
//                }
//        });
//        editAccountNo.setFilters(new InputFilter[]{
//                new InputFilter() {
//                    @Override
//                    public CharSequence filter(CharSequence src, int start, int end, Spanned dest, int dstart, int dend) {
//                        if (dend>21){
//                            return "";
//                        }
//                        else if (src.equals("")) {
//                            return src;
//                        }
//                        else if (src.toString().matches("[a-zA-Z0-9 ]+")) {
//                            return src;
//                        }
//
//                        return "";
//                    }
//
//                }
//        });
     /*   submitbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (editAccountNo.getText().length() < 10) {
                    Toast.makeText(getActivity(), "Please enter minimum 10 digits bank account number", Toast.LENGTH_SHORT).show();
                } else if (reenteraccount_number.getText().length() < 10) {
                    Toast.makeText(getActivity(), "Please re-enter the bank account number", Toast.LENGTH_SHORT).show();
                } else if (!(reenteraccount_number.getText().toString().toLowerCase().equalsIgnoreCase(editAccountNo.getText().toString()))) {
                    Toast.makeText(getActivity(), "Account number do not match.", Toast.LENGTH_SHORT).show();
                } else if (edittextCreditCard.getText().length() < 16) {
                    Toast.makeText(getActivity(), "Please enter 16 digit card number", Toast.LENGTH_SHORT).show();
                } else if (edtExpiryMonth.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Please select expiry month", Toast.LENGTH_SHORT).show();
                } else if (edtExpiryYear.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Please select expiry year", Toast.LENGTH_SHORT).show();
                } else if (editCVV.getText().length() < 3) {
                    Toast.makeText(getActivity(), "Please enter 3 digit CVV number", Toast.LENGTH_SHORT).show();
                }
//                else if((!isCardValid(edittextCreditCard.getText().toString())
//                        || edittextCreditCard.getText().toString().equalsIgnoreCase("0000000000000000"))){
//                    Toast.makeText(getActivity(), "Entered card number is wrong!", Toast.LENGTH_SHORT).show();
//                }
                else {
                    if (Utils.isNetworkAvailable(getActivity()))
                        callFinalRegAPI();
                }
            }
        });*/
      /*  edtExpiryMonth.setInputType(InputType.TYPE_NULL);
        edtExpiryYear.setInputType(InputType.TYPE_NULL);
        edtExpiryYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getActivity().getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                String[] years = new String[100];
                int yr = 2017;
                for (int i = 0; i < years.length; i++) {
                    years[i] = String.valueOf(yr);
                    yr++;
                }
//                String[] years = {"2016", "2017", "2018", "2019"
//                        , "2020", "2021", "2022", "2023", "2024", "2025", "2026", "2027", "2028", "2029"
//                        , "2030", "2031", "2032", "2033", "2034", "2035", "2036", "2037", "2038", "2039"
//                        , "2040", "2041", "2042", "2043", "2044", "2045", "2046", "2047", "2048", "2049", "2050"};
                new AlertDialog.Builder(getActivity())
                        .setTitle("Select Year")
                        .setSingleChoiceItems(years, selectedYear, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//
//                            }
//                        })
//                        .setPositiveButton(R.string.string_ok, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                                int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                                String item = ((AlertDialog) dialog).getListView().getItemAtPosition(selectedPosition).toString();
                                Fog.e("Itme", item);
                                edtExpiryYear.setText(item);
                                expiryyear = item.substring(2);
                                selectedYear = selectedPosition;
                                Fog.e("POSITION", String.valueOf(selectedPosition));
                            }
                        })
                        .show();
            }
        });
        edtExpiryMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                createDialogWithoutDateField().show();
                View view = getActivity().getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
                new AlertDialog.Builder(getActivity())
                        .setTitle("Select Month")
                        .setSingleChoiceItems(months, selectedMonth, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//
//                            }
//                        })
//                        .setPositiveButton(R.string.string_ok, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();

                                int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                                selectedMonth = selectedPosition;
                                Fog.e("POSITION", String.valueOf(selectedPosition));

                                switch (selectedPosition) {
                                    case 0:
                                        edtExpiryMonth.setText("01");
                                        expirymonth = "01";
                                        break;
                                    case 1:
                                        edtExpiryMonth.setText("02");
                                        expirymonth = "02";
                                        break;
                                    case 2:
                                        edtExpiryMonth.setText("03");
                                        expirymonth = "03";
                                        break;
                                    case 3:
                                        edtExpiryMonth.setText("04");
                                        expirymonth = "04";
                                        break;
                                    case 4:
                                        edtExpiryMonth.setText("05");
                                        expirymonth = "05";
                                        break;
                                    case 5:
                                        edtExpiryMonth.setText("06");
                                        expirymonth = "06";
                                        break;
                                    case 6:
                                        edtExpiryMonth.setText("07");
                                        expirymonth = "07";
                                        break;
                                    case 7:
                                        edtExpiryMonth.setText("08");
                                        expirymonth = "08";
                                        break;
                                    case 8:
                                        edtExpiryMonth.setText("09");
                                        expirymonth = "09";
                                        break;
                                    case 9:
                                        edtExpiryMonth.setText("10");
                                        expirymonth = "10";
                                        break;
                                    case 10:
                                        edtExpiryMonth.setText("11");
                                        expirymonth = "11";
                                        break;
                                    case 11:
                                        edtExpiryMonth.setText("12");
                                        expirymonth = "12";
                                        break;
                                    default:
                                        edtExpiryMonth.setText("01");
                                        expirymonth = "01";
                                        break;
                                }
                            }
                        })
                        .show();


            }
        });
*/
        return view;
    }
    StringBuilder  cardnumber=new StringBuilder();
    private void validate() {

        for(int i=0;i<editTexts.length;i++){

            if(TextUtils.isEmpty(editTexts[i].getText())){
                //editTexts[i].setBackground(getResources().getDrawable(R.drawable.code_mismatch));
                Toast.makeText(getActivity(), getString(R.string.please_enter_all_fields), Toast.LENGTH_SHORT).show();
                return;
            }

            else {
                cardnumber.append(editTexts[i].getText().toString());
            }
        }
         if(TextUtils.isEmpty(editText_Month1.getText())){
            editText_Month1.setBackground(getResources().getDrawable(R.drawable.code_mismatch));
            Toast.makeText(getActivity(), getString(R.string.please_enter_all_fields), Toast.LENGTH_SHORT).show();
            return;
        }
        else{
             expirymonth = editText_Month1.getText().toString();
         }
        if(TextUtils.isEmpty(editText_Month2.getText())){
            editText_Month2.setBackground(getResources().getDrawable(R.drawable.code_mismatch));
            Toast.makeText(getActivity(), getString(R.string.please_enter_all_fields), Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            expirymonth = expirymonth+editText_Month2.getText().toString();
        }
        if(TextUtils.isEmpty(editText_Year1.getText())){
            editText_Year1.setBackground(getResources().getDrawable(R.drawable.code_mismatch));
            Toast.makeText(getActivity(), getString(R.string.please_enter_all_fields), Toast.LENGTH_SHORT).show();
            return;
        }

        else{
            expiryyear = editText_Year1.getText().toString();
        }
        if(TextUtils.isEmpty(editText_Year2.getText())){
            editText_Year2.setBackground(getResources().getDrawable(R.drawable.code_mismatch));
            Toast.makeText(getActivity(), getString(R.string.please_enter_all_fields), Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            expiryyear = expiryyear+editText_Year2.getText().toString();

            displayCreditCardImage(cardnumber.subSequence(0,4));
        }




    }

    private void displayCreditCardImage(CharSequence charSequence) {

        int mDrawableResId = 0;
        String startNummber = charSequence.toString();
        for (int i = 0; i < mCCPatterns.size(); i++) {
            int key = mCCPatterns.keyAt(i);
            // get the object by the key.
            Pattern p = mCCPatterns.get(key);

            Matcher m = p.matcher(charSequence);
            if (m.find()) {
                mDrawableResId = key;
                //Fog.e("OnTEXT CVV", String.valueOf(i));
                break;
            }

            if (mDrawableResId > 0 && mDrawableResId !=
                    mCurrentDrawableResId) {
                mCurrentDrawableResId = mDrawableResId;
            } else if (mDrawableResId == 0) {
                mCurrentDrawableResId = mDefaultDrawableResId;
            }
            mCurrentDrawable = getResources()
                    .getDrawable(mCurrentDrawableResId);
            imageViewCard.setVisibility(View.VISIBLE);

            if(startNummber.startsWith("4"))imageViewCard.setImageResource(R.drawable.ic_visa_inc_logo);

            else if(startNummber.startsWith("51")||startNummber.startsWith("52")
                    ||startNummber.startsWith("53")||startNummber.startsWith("54")||startNummber.startsWith("55")){
                imageViewCard.setImageResource(R.drawable.mastercard);
            }
            else if(startNummber.startsWith("6011")||startNummber.startsWith("644")||
                    startNummber.startsWith("655"))imageViewCard.setImageResource(R.drawable.ic_discover_card_logo);

            else if(startNummber.startsWith("34")||startNummber.startsWith("37")||
                    startNummber.startsWith("655")){
                imageViewCard.setImageResource(R.drawable.american_express_logo);
            }
            else{
                imageViewCard.setImageDrawable(mCurrentDrawable);
            }





        }

    }


    void callFinalRegAPI() {

//        {"custid": "50F6-7F92-2832-F934","mobile": "918199119911",
        // "countryShortname":"KEN",
        // "question_id": "Fundu_001",
        // "answer": "15-09-1992",
        // "card_number":"4893010010000025",
        // "card_expiry": "1701",
        // "cvv": "871",
        // "email":"preet@gmail.com"}
        expiry = expiryyear + expirymonth;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("custid", pref.getString("custid"));
            jsonObject.put("mobile", Utils.appendCountryCodeToNumber(getContext(), mParam1.getContactId()));
            jsonObject.put("country_shortname", mParam1.getCountry_shortname());
            jsonObject.put("question_id", pref.getString("QuestionId"));
            jsonObject.put("answer", pref.getString("Answer"));
            jsonObject.put("accno", editAccountNo.getText().toString());
            jsonObject.put("card_number", edittextCreditCard.getText().toString());
            jsonObject.put("card_expiry", expiry);
            jsonObject.put("cvv", editCVV.getText().toString());
            jsonObject.put("email", pref.getString(Constants.PrefKey.EMAIL));
            jsonObject.put("fundu_pin", pref.getString("FunduPin"));
            jsonObject.put("contact_type", pref.getString(Constants.CONTACT_TYPE_PA));
//            jsonObject.put("type", "0");
            if (pref.getString(Constants.CONTACT_TYPE_PA).equalsIgnoreCase("AGENT")) {
//                jsonObject.put(Constants.DAYS, "SUNDAY,MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY");
//                jsonObject.put(Constants.OPENING_TIME, "07:00");
//                jsonObject.put(Constants.CLOSING_TIME, "20:00");
            }
//            "days": "Sunday,monday,tuesday",  "opening_time": "07:00",  "closing_time": "20:00",
        } catch (Exception e) {
            e.printStackTrace();
        }

        dialog.show();
        FinalRegistrationRequest request = new FinalRegistrationRequest(getContext(), jsonObject);
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
                            pref.putString("custid", custid);
                            FunduUser.setCustomerId(custid);
                            if (innerObject.optString(Constants.ALLOW_WITHDRAW).equalsIgnoreCase("No") && innerObject.optString("contact_type")
                                    .equalsIgnoreCase("AGENT")) {
                                pref.putString(Constants.ALLOW_WITHDRAW, innerObject.optString(Constants.ALLOW_WITHDRAW));
                            } else
                                pref.putString(Constants.ALLOW_WITHDRAW, "Yes");
                            Fog.e("merchant_img_url", "merchant_img_url" + innerObject.optString("merchant_img_url"));
                            if (!(innerObject.optString("merchant_img_url") == null || innerObject.optString("merchant_img_url").equalsIgnoreCase(""))) {
                                Fog.e("ImageUrl", innerObject.optString("merchant_img_url"));
                                pref.putString(Constants.PROFILE_PIC_URL, V1API.BASE_URL + "/v2/customers/getMerchantImage/" + mParam1.getContactId());
                            }
                            if (innerObject.optString(Constants.OPENING_TIME) != null) {

                            }
//                            localhost:9999/v2/customers/getMerchantImage/700000015
                            String businessname = innerObject.optString("business_name");
                            if (!(businessname == null || businessname.equalsIgnoreCase("") || businessname.isEmpty()) && pref.getString(Constants.CONTACT_TYPE_PA)
                                    .equalsIgnoreCase("AGENT"))
                                pref.putString(Constants.NAME, businessname);
                            syncData();

                        } else {
                            Toast.makeText(getContext(), message_success, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getContext(), message_success, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            private void syncData() {
                FunduUser.setUserLoginOrRegister(true);
                FunduUser.setContactIDType(mParam1.getContactIdType());
                FunduUser.setUserMobile(mParam1.getContactId());
                FunduUser.setCountryShortName(mParam1.getCountry_shortname());
                saveUserContact();
                readContacts();
            }

            private void readContacts() {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    getActivity().requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
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
                pref.putBoolean(Constants.IS_USER_LOGGED_IN, true);
                pref.putBoolean(Constants.IS_MOBILE_VERIFIED, true);
                pref.putString(Constants.MOBILE, mParam1.getContactId());
//                Bundle bundle = new Bundle();
                FunduUser.setUserMobile(mParam1.getContactId());
                FunduUser.setUserMobileVerified(true);
//                bundle.putSerializable(Contact.class.getSimpleName(), mParam1);
//                bundle.putString(FRAGMENT_NAME, "ThanksFragment");
//                onButtonPressed(bundle);
                Intent intent = new Intent();
                intent.putExtra(Contact.class.getSimpleName(), mParam1);
                getActivity().setResult(-5, intent);
                getActivity().finish();
                Utils.showShortToast(getActivity(), getString(R.string.registration_success));
            }

            private void triggerServices() {
//        dialog.show();
                getActivity().startService(new Intent(getActivity(), NearByContactsService.class));
                //SyncContactsIntentService.startService(getActivity(), handler, mParam1.getDeviceId());

                UpdateSettingRequest updateSettingRequest = new UpdateSettingRequest(getContext());
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

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
//        if (s.length() <4) {
//            reenterfpinEditText.setTextColor(getResources().getColor(android.R.color.black));
//        } else {
//            if (s.length()==4) {
//
//                if (reenterfpinEditText.length() == 4 && fpinEditText.length() == 4) {
//                    if (reenterfpinEditText.getText().toString().equals(fpinEditText.getText().toString())) {
//                        reenterfpinEditText.setTextColor(getResources().getColor(android.R.color.black));
//                        createPinButton.setEnabled(true);
//                    } else {
//                        reenterfpinEditText.setTextColor(getResources().getColor(android.R.color.holo_red_light));
//                    }
//                }
//            }
//
//        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public boolean isCardValid(String ccNumber) {
        int sum = 0;
        boolean alternate = false;
        for (int i = ccNumber.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(ccNumber.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        return (sum % 10 == 0);
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event) {
        if (event.getAction() != KeyEvent.ACTION_DOWN) {

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

                   /* for(int i=0;i<editTexts.length;i++){
                        if(editTexts[i].hasFocus()){
                            if (editTexts[i].getText().length() != 0) {
                                editTexts[i].setText("" + (keyCode - 7));
                            }
                            return true;
                        }
                    }*/
                    if (editText1.hasFocus()) {
                        if (editText1.length() != 0) {
                            editText2.setText("" + (keyCode - 7));
                        }
                        return true;
                    }
                    if (editText2.hasFocus()) {
                        if (editText2.length() != 0) {
                            editText3.setText("" + (keyCode - 7));
                        }
                        return true;
                    }
                    if (editText3.hasFocus()) {
                        if (editText3.length() != 0) {
                            editText4.setText("" + (keyCode - 7));
                        }
                        return true;
                    }
                    if (editText4.hasFocus()) {
                        if (editText4.length() != 0) {
                            editText5.setText("" + (keyCode - 7));
                        }
                        return true;
                    }
                    if (editText5.hasFocus()) {
                        if (editText5.length() != 0) {
                            editText6.setText("" + (keyCode - 7));
                        }
                        return true;
                    }
                    if (editText6.hasFocus()) {
                        if (editText6.length() != 0) {
                            editText7.setText("" + (keyCode - 7));
                        }
                        return true;
                    }
                    if (editText7.hasFocus()) {
                        if (editText7.length() != 0) {
                            editText8.setText("" + (keyCode - 7));
                        }
                        return true;
                    }
                    if (editText9.hasFocus()) {
                        if (editText9.length() != 0) {
                            editText10.setText("" + (keyCode - 7));
                        }
                        return true;
                    }
                    if (editText10.hasFocus()) {
                        if (editText11.length() != 0) {
                            editText12.setText("" + (keyCode - 7));
                        }
                        return true;
                    }
                    if (editText12.hasFocus()) {
                        if (editText12.length() != 0) {
                            editText13.setText("" + (keyCode - 7));
                        }
                        return true;
                    }
                    if (editText13.hasFocus()) {
                        if (editText13.length() != 0) {
                            editText14.setText("" + (keyCode - 7));
                        }
                        return true;
                    }
                    if (editText14.hasFocus()) {
                        if (editText14.length() != 0) {
                            editText15.setText("" + (keyCode - 7));
                        }
                        return true;
                    }
                    if (editText15.hasFocus()) {
                        if (editText15.length() != 0) {
                            editText16.setText("" + (keyCode - 7));
                        }
                        return true;
                    }
                    if (editText8.hasFocus()) {
                        if (editText8.length() != 0) {
                            editText9.setText("" + (keyCode - 7));
                        }
                        return true;
                    }
                    if (editText11.hasFocus()) {
                        if (editText11.length() != 0) {
                            editText12.setText("" + (keyCode - 7));
                        }
                        return true;
                    }

                    if (editText_Month1.hasFocus()) {
                        if (editText_Month1.length() != 0) {
                            editText_Month2.setText("" + (keyCode - 7));
                        }
                        return true;
                    }
                    if (editText_Month2.hasFocus()) {
                        if (editText_Month2.length() != 0) {
                            editText_Year1.setText("" + (keyCode - 7));
                        }
                        return true;
                    }
                    if (editText_Year1.hasFocus()) {
                        if (editText_Year1.length() != 0) {
                            editText_Year2.setText("" + (keyCode - 7));
                        }
                        return true;
                    }
                    if (editText_Year2.hasFocus()) {
                        if (editText_Year2.length() != 0) {
                            editText_Year2.setText("" + (keyCode - 7));
                            editText_Year2.setSelection(editText_Year2.length());
                        }
                        return true;
                    }

            }
            if (keyCode == KeyEvent.KEYCODE_BACK) {

            }
            else if (keyCode == KeyEvent.KEYCODE_DEL) {
               // Toast.makeText(getActivity(), "Clicked", Toast.LENGTH_SHORT).show();

                imageViewCard.setImageResource(R.drawable.creditcard);
                if (editText1.hasFocus()) {
                    //if (editText1.length() != 0) {
                        editText1.setText("");
                    //}
                    return true;
                }
                if (editText2.hasFocus()) {
                    //if (editText2.length() != 0) {
                        editText2.setText("");
                        editText1.requestFocus();
                    //}
                    return true;
                }
                if (editText3.hasFocus()) {
                    //if (editText3.length() != 0) {
                        editText3.setText("");
                        editText2.requestFocus();
                    //}
                    return true;
                }
                if (editText4.hasFocus()) {
                    //if (editText4.length() != 0) {
                        editText4.setText("");
                        editText3.requestFocus();
                   // }
                    return true;
                }
                if (editText5.hasFocus()) {
                   // if (editText5.length() != 0) {
                        editText5.setText("");
                        editText4.requestFocus();
                   // }
                    return true;
                }
                if (editText6.hasFocus()) {
                    //if (editText6.length() != 0) {
                        editText6.setText("");
                        editText5.requestFocus();
                   // }
                    return true;
                }
                if (editText7.hasFocus()) {
                   // if (editText7.length() != 0) {
                        editText7.setText("");
                        editText6.requestFocus();
                   // }
                    return true;
                }
                if (editText8.hasFocus()) {
                    // if (editText9.length() != 0) {
                    editText8.setText("");
                    editText7.requestFocus();
                    // }
                    return true;
                }
                if (editText9.hasFocus()) {
                   // if (editText9.length() != 0) {
                        editText9.setText("");
                        editText8.requestFocus();
                   // }
                    return true;
                }
                if (editText10.hasFocus()) {
                   // if (editText10.length() != 0) {
                        editText10.setText("");
                        editText9.requestFocus();
                   // }
                    return true;
                }
                if (editText11.hasFocus()) {
                    // if (editText10.length() != 0) {
                    editText11.setText("");
                    editText10.requestFocus();
                    // }
                    return true;
                }
                if (editText12.hasFocus()) {
                   // if (editText12.length() != 0) {
                        editText12.setText("");
                        editText11.requestFocus();
                   // }
                    return true;
                }
                if (editText13.hasFocus()) {
                   // if (editText13.length() != 0) {
                        editText13.setText("");
                        editText12.requestFocus();
                   // }
                    return true;
                }
                if (editText14.hasFocus()) {
                   // if (editText14.length() != 0) {
                        editText14.setText("");
                        editText13.requestFocus();
                   // }
                    return true;
                }
                if (editText15.hasFocus()) {
                    //if (editText15.length() != 0) {
                        editText15.setText("");
                        editText14.requestFocus();
                    //}
                    return true;
                }

                if (editText16.hasFocus()) {
                    //if (editText15.length() != 0) {
                    editText16.setText("");
                    editText15.requestFocus();
                    editText15.setSelection(editText15.length());
                    //}
                    return true;
                }

                if (editText_Month1.hasFocus()) {
                    editText_Month1.setText("");

                    return true;
                } else if (editText_Month2.hasFocus()) {
                    editText_Month2.setText("");
                    editText_Month1.requestFocus(editText_Month2.length());

                    return true;
                } else if (editText_Year1.hasFocus()) {
                    editText_Year1.setText("");
                    editText_Month2.requestFocus(editText_Month2.length());
                    return true;
                } else if (editText_Year2.hasFocus()) {
                    editText_Year2.setText("");
                    editText_Year1.requestFocus(editText_Year1.length());
                    return true;
                }
            }

        }
        return false;
    }


    private static class FocusSwitchingTextWatcher implements TextWatcher {

        private final View nextViewToFocus;


        FocusSwitchingTextWatcher(View nextViewToFocus) {
            this.nextViewToFocus = nextViewToFocus;

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() == 1) {

                if (nextViewToFocus instanceof Button) {
                    Button submitCodeButton = (Button)nextViewToFocus;
                    submitCodeButton.setEnabled(true);
                }
                else{
                    EditText view = (EditText)nextViewToFocus;
                    view.requestFocus(view.getText().length());
                }

            }

           /* if (s.length() == 0) {

                previousViewToFocus.requestFocus();
                submitCodeButton.setEnabled(false);
            }*/


        }

        @Override
        public void afterTextChanged(Editable s) {


        }


    }

    void gotoCVVDetailScreen(){

        Bundle bundle = new Bundle();
        bundle.putString(FRAGMENT_NAME, "CVVFragment");
        bundle.putSerializable(Contact.class.getSimpleName(), mParam1);

        bundle.putString("QuestionKey", secQuesId);
        bundle.putString("Answer", secAnswer);
        bundle.putString("FunduPin", FunduPin);
        bundle.putString("AccountNumber", confirmAccNumber);
        bundle.putString("expiry", expirymonth.concat(expiryyear));
        bundle.putString("cardnumber", String.valueOf(cardnumber));
        pref.putString("cardnumber", String.valueOf(cardnumber));
        pref.putString("expiry", expirymonth.concat(expiryyear));
        /*UserOnboardingActivity userOnboardingActivity = new UserOnboardingActivity();
        userOnboardingActivity.addFragment(FRAGMENT_NAME, CVVFragment.newInstance((Contact) bundle.getSerializable(Contact.class.getSimpleName()), ""));*/
        onButtonPressed(bundle);
    }
    @Override
    public boolean onBackPressed() {
        return false;
    }

}
