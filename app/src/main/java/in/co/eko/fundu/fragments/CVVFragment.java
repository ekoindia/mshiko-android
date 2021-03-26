package in.co.eko.fundu.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import in.co.eko.fundu.R;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.constants.V1API;
import in.co.eko.fundu.views.customviews.CustomButton;
import in.co.eko.fundu.models.Contact;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.requests.FinalRegistrationRequest;
import in.co.eko.fundu.requests.UpdateSettingRequest;
import in.co.eko.fundu.services.NearByContactsService;
import in.co.eko.fundu.utils.Fog;
import in.co.eko.fundu.utils.Utils;


public class CVVFragment extends BaseFragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String expirymonth = "", expiryyear = "";
    private String secQuesId, secAnswer, FunduPin,expiry,AccountNumber,cardnumber;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private Contact mParam1;
    private ProgressDialog dialog;
    private String mParam2;
    public CustomButton done;
    public ImageView back;
    public EditText cvv;


    public CVVFragment() {
        // Required empty public constructor
    }


    public static CVVFragment newInstance(Contact param1, String param2) {
        CVVFragment fragment = new CVVFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
            expiry = getArguments().getString("expiry");
            cardnumber = getArguments().getString("cardnumber");
            AccountNumber = getArguments().getString("AccountNumber");
            Fog.e("ques_id", secQuesId);
            Fog.e("answer", secAnswer);
            Fog.e("cardnumber", cardnumber);
            Fog.e("expiry", expiry);
        }
        dialog = new ProgressDialog(getActivity());
        dialog.setCancelable(false);
        dialog.setMessage("Please wait...");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cvv, container, false);

        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        back = (ImageView)view.findViewById(R.id.imageView_back);
        done = (CustomButton)view.findViewById(R.id.done);
        cvv  = (EditText)view.findViewById(R.id.cvv);
        cvv.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });

    }
    public void doneClick(View view){
        if(view.getId()==R.id.done)
        validate();
    }
    private void validate() {

        if(TextUtils.isEmpty(cvv.getText().toString())){
            Toast.makeText(getActivity(), "Please enter CVV number.", Toast.LENGTH_SHORT).show();
        }
        else {
            callFinalRegAPI();
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
        Fog.e("Mobile", Utils.appendCountryCodeToNumber(getContext(), mParam1.getContactId()));
        Fog.e("CountryShortName", mParam1.getCountry_shortname());
        Fog.e("QuestionID", pref.getString("QuestionId"));
        Fog.e("Answer", pref.getString("Answer"));
        Fog.e("CardNumber", cardnumber);
        Fog.e("CardNumber", String.valueOf(cardnumber));
        Fog.e("Expiry", expiry);
        // Fog.e("CVV", editCVV.getText().toString());
        Fog.e("Email", pref.getString(Constants.PrefKey.EMAIL));
        Fog.e("FunduPin", pref.getString("FunduPin"));
        Fog.e("CustId", pref.getString("custid"));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("custid", pref.getString("custid"));
            jsonObject.put("mobile", Utils.appendCountryCodeToNumber(getContext(), mParam1.getContactId()));
            jsonObject.put("country_shortname", mParam1.getCountry_shortname());
            jsonObject.put("question_id", pref.getString("QuestionId"));
            jsonObject.put("answer", pref.getString("Answer"));
           // jsonObject.put("accno", AccountNumber);
            jsonObject.put("accno", pref.getString("AccountNumber"));
            //jsonObject.put("card_number", cardnumber);
            jsonObject.put("card_number", pref.getString("cardnumber"));
           // jsonObject.put("card_expiry", expiry);
            jsonObject.put("card_expiry", pref.getString("expiry"));
            jsonObject.put("cvv", cvv.getText().toString());
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
                Utils.showShortToast(getActivity(), "Registration Completed Successfully!");
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
    public boolean onBackPressed() {
        return false;
    }

}
