package in.co.eko.fundu.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.R;
import in.co.eko.fundu.adapters.BankAdapter;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.constants.V1API;
import in.co.eko.fundu.models.BanksNameItem;
import in.co.eko.fundu.models.Contact;
import in.co.eko.fundu.requests.FinalRegistrationRequest;
import in.co.eko.fundu.requests.UpdateSettingRequest;
import in.co.eko.fundu.services.NearByContactsService;
import in.co.eko.fundu.services.SyncContactsIntentService;
import in.co.eko.fundu.utils.Fog;
import in.co.eko.fundu.utils.Utils;

/**
 * Created by Rahul on 15/06/16.
 */

public class MerchantCardDetailFragment extends BaseFragment implements AdapterView.OnItemSelectedListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private Contact mParam1;
    private String mParam2;
    private ProgressDialog dialog;
    private EditText editAccountNo, reenteraccount_number;
    private Button submitbutton;
    ArrayList<BanksNameItem> bankitems;
    private Spinner spinner_bank;
    private BankAdapter bank_adapter;
    BanksNameItem[] arraylist;
    private int pos = 0;
    String bin_number = "", bank_name = null;


    public static MerchantCardDetailFragment newInstance(Contact param1, String param2) {
        MerchantCardDetailFragment fragment = new MerchantCardDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public MerchantCardDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = (Contact) getArguments().getSerializable(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        dialog = new ProgressDialog(getActivity());
        dialog.setCancelable(false);
        dialog.setMessage("Please wait...");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.merchant_card_detail_screen, container, false);
        submitbutton = (Button) view.findViewById(R.id.submitButton);
        editAccountNo = (EditText) view.findViewById(R.id.account_number);
        reenteraccount_number = (EditText) view.findViewById(R.id.reenteraccount_number);
        spinner_bank = (Spinner) view.findViewById(R.id.spinner_bank);
        submitbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (pos == 0) {
                    Toast.makeText(getActivity(), "Please select your Bank first", Toast.LENGTH_SHORT).show();
                } else if (editAccountNo.getText().length() < 10) {
                    Toast.makeText(getActivity(), "Please enter minimum 10 digits bank account number", Toast.LENGTH_SHORT).show();
                } else if (reenteraccount_number.getText().length() < 10) {
                    Toast.makeText(getActivity(), "Please re-enter the bank account number", Toast.LENGTH_SHORT).show();
                } else if (!(reenteraccount_number.getText().toString().toLowerCase().equalsIgnoreCase(editAccountNo.getText().toString()))) {
                    Toast.makeText(getActivity(), "Account number do not match.", Toast.LENGTH_SHORT).show();
                } else {
                    if (Utils.isNetworkAvailable(getActivity()))
                        callFinalRegAPI();
                }
            }
        });
        setupSpinner();
        return view;
    }

    void setupSpinner() {
        String stringdata = "[\n" +
                "  {\"bankname\": \"Select your bank\", \"bankcode\": \"000000\"},\n" +
                "  {\"bankname\": \"KCB\", \"bankcode\": \"418087\"},\n" +
                "  {\"bankname\": \"UBA\", \"bankcode\": \"457277\"},\n" +
                "  {\"bankname\": \"Sidian Bank\", \"bankcode\": \"400955\"},\n" +
                "  {\"bankname\": \"Sumac DTM\", \"bankcode\": \"637058\"},\n" +
                "  {\"bankname\": \"Family Bank\", \"bankcode\": \"423934\"},\n" +
                "  {\"bankname\": \"Middle East Bank\", \"bankcode\": \"504936\"},\n" +
                "  {\"bankname\": \"Chase Bank\", \"bankcode\": \"424108\"},\n" +
                "  {\"bankname\": \"Housing Finance\", \"bankcode\": \"639237\"},\n" +
                "  {\"bankname\": \"NIC\", \"bankcode\": \"627557\"},\n" +/*413271*/
                "  {\"bankname\": \"Postbank\", \"bankcode\": \"442461\"},\n" +
                "  {\"bankname\": \"KWFT\", \"bankcode\": \"504158\"}\n" +
                "]";
        try {
            JSONArray array = new JSONArray(stringdata);

            arraylist = new BanksNameItem[array.length()];

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                BanksNameItem bean = new BanksNameItem();
                bean.setBankname(obj.getString("bankname"));
                bean.setBankcode(obj.getString("bankcode"));
                arraylist[i] = bean;
            }

            bank_adapter = new BankAdapter(getActivity(), android.R.layout.simple_list_item_1, arraylist);
            spinner_bank.setAdapter(bank_adapter);
            spinner_bank.setOnItemSelectedListener(MerchantCardDetailFragment.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void callFinalRegAPI() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("custid", pref.getString("custid"));
            jsonObject.put("mobile", Utils.appendCountryCodeToNumber(getContext(), mParam1.getContactId()));
            jsonObject.put("countryShortname", mParam1.getCountry_shortname());
            jsonObject.put("question_id", pref.getString("QuestionId"));
            jsonObject.put("answer", pref.getString("Answer"));
            jsonObject.put("accno", editAccountNo.getText().toString());
            jsonObject.put("card_number", bin_number + "9999999999");
            jsonObject.put("card_expiry", "2006");
            jsonObject.put("cvv", "123");
            jsonObject.put("email", pref.getString(Constants.PrefKey.EMAIL));
            jsonObject.put("fundu_pin", pref.getString("FunduPin"));
            jsonObject.put("contact_type", pref.getString(Constants.CONTACT_TYPE_PA));
            jsonObject.put("bank_name", bank_name);
//            jsonObject.put("type", "1");
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
                FunduUser.setUserMobile(mParam1.getContactId());
                FunduUser.setUserMobileVerified(true);
                Intent intent = new Intent();
                intent.putExtra(Contact.class.getSimpleName(), mParam1);
                getActivity().setResult(-5, intent);
                getActivity().finish();
                Utils.showShortToast(getActivity(), "Registration Completed Successfully!");
            }

            private void triggerServices() {
                getActivity().startService(new Intent(getActivity(), NearByContactsService.class));
                SyncContactsIntentService.startService(getActivity(), mParam1.getDeviceId());

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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        pos = position;
        if (position > 0) {
            Fog.e("Selected Bank", "" + arraylist[position].getBankname() + " ++ " + arraylist[position].getBankcode());
            bin_number = arraylist[position].getBankcode();
            bank_name = arraylist[position].getBankname();
            editAccountNo.setText("");
            reenteraccount_number.setText("");
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    @Override
    public boolean onBackPressed() {
        return false;
    }
}
