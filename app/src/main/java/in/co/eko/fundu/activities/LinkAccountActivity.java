package in.co.eko.fundu.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.R;
import in.co.eko.fundu.adapters.ExistingAccountAdapter;
import in.co.eko.fundu.constants.API;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.interfaces.OnFragmentInteractionListener;
import in.co.eko.fundu.models.BanksNameItem;
import in.co.eko.fundu.models.LinkAccountItem;
import in.co.eko.fundu.parser.UniversalParser;
import in.co.eko.fundu.utils.AppPreferences;
import in.co.eko.fundu.requests.CallWebService;
import in.co.eko.fundu.utils.Fog;
import in.co.eko.fundu.utils.Utils;



public class LinkAccountActivity extends BaseActivity implements View.OnClickListener, TextWatcher, CallWebService.ObjectResponseCallBack, CallWebService.ArrayResponseCallback,OnFragmentInteractionListener {

    private LinearLayout containerAccounts;
    private LinearLayout containerEmails;
    private LinearLayout containerContacts;
    private LinearLayout linearLayoutAccountNumber;
    private RelativeLayout linearLayoutExistingAcc;
    private RelativeLayout relativeLayout;
    private LinearLayout linearLayout_enter_acc_details;
    private EditText confirmAccNum;
    private TextView ifscETFixed,btn_add_acc,textViewMessageAccount;
    ArrayList<BanksNameItem> banksNameItems = new ArrayList<>();
    private  View enterAccount;
    private String afterAction;
    private ImageView imageview_back,close;
    private Button btn_confirm;
    private ArrayAdapter<String> spinnerAdapter = null;
    private JSONArray identitiesArray = null;
    private JSONObject mainJsonObject = null, additionalIdentitiesObject = null;
    private boolean allFieldsAreOK = true;
    private ExistingAccountAdapter existingAccountAdapter;
    RecyclerView recyclerView;
    //contact layout fields
    EditText contactNumberET;

    @Nullable   @BindView
            (R.id.contactNumberSP)
    Spinner contactNumberSP;
    //email layout fields
    EditText emailET;

    @Nullable @BindView(R.id.emailSP)
    Spinner emailSP;
    //account layout fields
    private EditText accountNumberET, ifscET;

    @Nullable
    @BindView(R.id.bankNameSP)
    Spinner bankNameSP;
    static final int CLICKED_ACCOUNT_NUMBER = 101;
    static final int CLICKED_EMAIL = 102;
    static final int CLICKED_CONTACT_NUMBER = 103;
    List<String> contactTypes;
    List<String> emailTypes;
    List<String> accountTypes;
    int clickedAddMore = 0;

    private ArrayList<LinkAccountItem> linkAccountItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_account);
        Fog.e("LinkAccountActivity", "LinkAccountActivity");
        ButterKnife.bind(this);
        InitViews();
        afterAction = getIntent().getStringExtra("after_action");
        Fog.d("LinkAccountActivity","after_action"+afterAction);

    }

    private void InitViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getTypes();

        containerContacts = (LinearLayout) findViewById(R.id.containerContacts);
        containerEmails = (LinearLayout) findViewById(R.id.containerEmails);
        containerAccounts = (LinearLayout) findViewById(R.id.containerAccounts);
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        accountNumberET = (EditText)findViewById(R.id.accountNumberET);
        ifscET = (EditText)findViewById(R.id.ifscET);
        ifscETFixed = (TextView)findViewById(R.id.ifscETFixed);
        textViewMessageAccount = (TextView)findViewById(R.id.textViewMessageAccount);
        linearLayoutExistingAcc = (RelativeLayout) findViewById(R.id.linearLayout_existing_list);
        close = (ImageView)findViewById(R.id.imageview_close);
        linearLayoutAccountNumber = (LinearLayout)findViewById(R.id.linearLayout_enter_acc_details);
        recyclerView = (RecyclerView)findViewById(R.id.RecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        confirmAccNum = (EditText) findViewById(R.id.confirmAccNum);
        imageview_back = (ImageView) findViewById(R.id.imageview_back);
        btn_confirm = (Button) findViewById(R.id.btn_confirm);
        btn_add_acc = (TextView) findViewById(R.id.btn_add_acc);
        accountNumberET.addTextChangedListener(this);
       /* setAdapter(contactNumberSP, contactTypes);
        setAdapter(emailSP, emailTypes);*/
        btn_confirm.setOnClickListener(this);
        btn_add_acc.setOnClickListener(this);
        close.setOnClickListener(this);
        imageview_back.setOnClickListener(this);



    }

    String splitedIdValue[];
    boolean isLinked;
    private void displayExistingAccount(ArrayList<LinkAccountItem> linkAccountItems) {

        // String action =  getIntent().getExtras().getString("after_action");

        /*if(action!=null){
            if(linkAccountItems.size()>0&&(action.equalsIgnoreCase("HomeAcitivity")||Constants.FromSettings)){
                textViewMessageAccount.setText("We have found "+linkAccountItems.size()+"account linked with your phone number. Select to proceed.");
            }
            else{
                textViewMessageAccount.setText("This account will be used to receive money if you provide cash to someone. Please fill the details carefully.");
            }
        }
        textViewMessageAccount.setText("This account will be used to receive money if you provide cash to someone. Please fill the details carefully.");*/

        if (this.linkAccountItems.size() > 0) {


            for (int i = 0; i < linkAccountItems.size(); i++) {
                String str1 = "", str2 = "";
                splitedIdValue = linkAccountItems.get(i).getAdditional_id_value().split("_");
                if (currentLinkAccountNumber.length() != 0 && ifsc.length() != 0) {
                    str1 = currentLinkAccountNumber.substring(currentLinkAccountNumber.length() - 4);
                    str2 = splitedIdValue[0].substring(splitedIdValue[0].length() - 4);
                }

                Fog.d("str1", "" + str1);
                Fog.d("str2", "" + str2);
                AppPreferences appPreferences2 = new AppPreferences(this);
                Fog.d("Constants.FromSettings", "" + Constants.FromSettings);
                if (Constants.FromSettings) {
                    isLinked = false;
                    Constants.FromSettings = false;
                }

                else if (ifsc.equalsIgnoreCase(splitedIdValue[1]) && str1.equalsIgnoreCase(str2)) {
                    isLinked = true;

                    break;
                }
            }

            linearLayoutExistingAcc.setVisibility(View.VISIBLE);
            linearLayoutAccountNumber.setVisibility(View.GONE);
            existingAccountAdapter = new ExistingAccountAdapter(this, linkAccountItems, currentLinkAccountNumber, ifsc, isLinked);


            recyclerView.setAdapter(existingAccountAdapter);
        } else {
            Utils.hideSoftKeyboard(this);
            Fog.d("banksNameItems", "" + banksNameItems.size());
            if (banksNameItems.size() != 0) {
                // Toast.makeText(this, ""+banksNameItems.get(bankNameSP.getSelectedItemPosition()).getBankcode(), Toast.LENGTH_SHORT).show();
            }

            linearLayoutAccountNumber.setVisibility(View.VISIBLE);
            linearLayoutExistingAcc.setVisibility(View.GONE);
            bankNameSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    ifscET.setVisibility(View.VISIBLE);
                    ifscETFixed.setVisibility(View.VISIBLE);
                    ifscETFixed.setText(banksNameItems.get(i).getBankcode());
                    ifscET.setSelection(ifscET.getText().length());

                }


                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }
    }



    private String createUrl() {
        String ContactId = FunduUser.getContactId();
        if(ContactId.contains("+91")||ContactId.contains("254")){
            ContactId = ContactId.replace("+91","");
        }
        if(ContactId.contains("254")){
            ContactId = ContactId.replace("254","");
        }
        Fog.d("CallWebService","getContactIDType"+FunduUser.getContactIDType());
        Fog.d("CallWebService","getContactId"+FunduUser.getContactId());
        Fog.d("CallWebService","getContactId"+String.format(API.GET_CONTACT_API, FunduUser.getContactIDType(), FunduUser.getContactId()));
        //return String.format(API.GET_CONTACT_API, FunduUser.getContactIDType(), FunduUser.getContactId());
        return String.format(API.GET_CONTACT_API, FunduUser.getContactIDType(), ContactId);

    }

    private void sendDataToServer() {
        identitiesArray = new JSONArray();
        allFieldsAreOK  = true;

        createAccountNumberJsonObjects();

        callUpdateLinkAccount(false);
    }

    private void callUpdateLinkAccount(boolean addMoreClicked) {
        if (allFieldsAreOK)
            try {
                if (identitiesArray.length() > 0) {
                    createFinalJson();
                    Fog.d("mainJsonObject",""+mainJsonObject);
                    hitApiForUpdatingLinkAccount(mainJsonObject, addMoreClicked);
                } else
                    finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }

    private void hitApiForUpdatingLinkAccount(JSONObject mainJsonObject, boolean addMoreClicked) {
        String id = Utils.appendCountryCodeToNumber(this, FunduUser.getContactId());
        String id_type = FunduUser.getContactIDType();
        if (!addMoreClicked) {
            Fog.d("LinkAccount", "addMoreClicked");
            CallWebService.getInstance(this, true, Constants.ApiType.SAVE_FINAL_ADDITIONAL_IDENTITIES).hitJsonObjectRequestAPI(Request.Method.PUT, String.format(API.CUSTOMER, id_type, id), mainJsonObject, this);
        } else {
            Fog.d("LinkAccount", "else");
            //CallWebService.getInstance(this, true, Constants.ApiType.SAVE_ADD_MORE_CLICKED_IDENTITES).hitJsonObjectRequestAPI(Request.Method.PUT, String.format(API.UPDATE_CUSTOMER, id_type, id), mainJsonObject, this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_your_atmnetwork, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        identitiesArray = new JSONArray();
        allFieldsAreOK = true;
        switch (v.getId()) {
            case R.id.addMoreContactNumber:
                clickedAddMore = CLICKED_CONTACT_NUMBER;
                // createContactNumberJsonObjects();
                break;
            case R.id.addMoreEmail:
                clickedAddMore = CLICKED_EMAIL;
                // createEmailJsonObjects();
                break;
            case R.id.addMoreAccountNumber:
                clickedAddMore = CLICKED_ACCOUNT_NUMBER;
                // createAccountNumberJsonObjects();
                break;

            case R.id.btn_add_acc:
                updateUi();
                break;

            case R.id.btn_confirm:
                sendDataToServer();
                break;
            case R.id.imageview_back:
                Fog.d("LinkAccountActivity","after_action"+"imageview_back");
                /*if(afterAction!=null&&afterAction.equalsIgnoreCase("HomeAcitivity")){
                    startActivity(new Intent(this,HomeActivity.class)
                            .putExtra("after_action","HomeAcitivity"));
                    finish();
                }
                else {
                    startActivity(new Intent(this,HomeActivity.class).putExtra("after_action",""));
                    finish();
                }*/
                AppPreferences appPreferences = new AppPreferences(this);
                if(appPreferences.getString(Constants.FromContact).equalsIgnoreCase("settings")){
                    startActivity(new Intent(this,HomeActivity.class)
                            .putExtra("after_action",""));
                    finish();
                }
                else {
                    startActivity(new Intent(this,HomeActivity.class)
                            .putExtra("after_action","HomeAcitivity"));
                    finish();
                }

                break;
            case R.id.imageview_close:
                Fog.d("LinkAccountActivity","after_action"+"imageview_close");
                AppPreferences appPreferences1 = new AppPreferences(this);
                if(appPreferences1.getString(Constants.FromContact).equalsIgnoreCase("settings")){
                    startActivity(new Intent(this,HomeActivity.class)
                            .putExtra("after_action",""));
                    finish();
                }
                else {
                    startActivity(new Intent(this,HomeActivity.class)
                            .putExtra("after_action","HomeAcitivity"));
                    finish();
                }

                break;
        }
        if (identitiesArray.length() > 0)
            callUpdateLinkAccount(true);

    }

    private void updateUi() {

      /* enterAccount.setVisibility(View.VISIBLE);
       containerAccounts.setVisibility(View.VISIBLE);
       linearLayoutExistingAcc.setVisibility(View.GONE);
       linearLayoutAccountNumber.setVisibility(View.VISIBLE);
       linearLayout_enter_acc_details.setVisibility(View.VISIBLE);*/

        linearLayoutAccountNumber.setVisibility(View.VISIBLE);
        linearLayoutExistingAcc.setVisibility(View.GONE);
        bankNameSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                ifscET.setVisibility(View.VISIBLE);
                ifscETFixed.setVisibility(View.VISIBLE);
                ifscETFixed.setText(banksNameItems.get(i).getBankcode());
                ifscET.setSelection(ifscET.getText().length());

               /* if(banksNameItems.get(i).getIfsc_req_ipms()==1){
                    ifscET.setVisibility(View.VISIBLE);
                    ifscET.setText(banksNameItems.get(i).getBankcode());
                    ifscET.setSelection(ifscET.getText().length());
                }
                else {
                    ifscET.setVisibility(View.GONE);
                }*/

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });





    }

    /*
    add new Layouts
     */
    private void addNewAccountLayout(int index) {
        View viewAccount = getLayoutInflater().inflate(R.layout.view_accounts, null);
        viewAccount.findViewById(R.id.bank).setVisibility(View.INVISIBLE);
        accountNumberET = (EditText) viewAccount.findViewById(R.id.accountNumberET);
        ifscET = (EditText) viewAccount.findViewById(R.id.ifscET);
        bankNameSP = (Spinner) viewAccount.findViewById(R.id.bankNameSP);
        accountNumberET.addTextChangedListener(this);
        setAdapter(bankNameSP, accountTypes);
        if (index >= 0)
            containerAccounts.addView(viewAccount, index);
        else
            containerAccounts.addView(viewAccount);


        viewAccount.setId(index);

    }

    private void addNewEmailLayout(int index) {
        View viewEmail = getLayoutInflater().inflate(R.layout.view_email, null);
        viewEmail.findViewById(R.id.email).setVisibility(View.INVISIBLE);
        emailET = (EditText) viewEmail.findViewById(R.id.emailET);
        emailSP = (Spinner) viewEmail.findViewById(R.id.emailSP);
        // setAdapter(emailSP, emailTypes);
        if (index >= 0)
            containerEmails.addView(viewEmail, index);
        else
            containerEmails.addView(viewEmail);
        viewEmail.setId(index);

    }

    private void addNewContactLayout(int index) {
        View viewContacts = getLayoutInflater().inflate(R.layout.view_contacts, null);
        viewContacts.findViewById(R.id.contact).setVisibility(View.INVISIBLE);
        contactNumberET = (EditText) viewContacts.findViewById(R.id.contactNumberET);
        contactNumberSP = (Spinner) viewContacts.findViewById(R.id.contactNumberSP);
        // setAdapter(contactNumberSP, contactTypes);
        if (index >= 0)
            containerContacts.addView(viewContacts, index);
        else
            containerContacts.addView(viewContacts);
        viewContacts.setId(index);

    }

    private static final char space = '-';

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        // Remove spacing char
        if (s.length() > 0 && (s.length() % 5) == 0) {
            final char c = s.charAt(s.length() - 1);
            if (space == c) {
                s.delete(s.length() - 1, s.length());
            }
        }
        // Insert char where needed.
        if (s.length() > 0 && (s.length() % 5) == 0) {
            char c = s.charAt(s.length() - 1);
            // Only if its a digit where there should be a space we insert a space
            if (Character.isDigit(c) && TextUtils.split(s.toString(), String.valueOf(space)).length <= 3) {
                s.insert(s.length() - 1, String.valueOf(space));
            }
        }
    }

    private void setAdapter(Spinner spinner, List<String> resourceArray) {
        spinnerAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, resourceArray);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_inner_item);
        spinner.setAdapter(spinnerAdapter);
    }


    private void addLayout() {
        switch (clickedAddMore) {
            case CLICKED_ACCOUNT_NUMBER:
                clearAccountFields();
                createAndSetValueInAccountLayout(linkAccountItems.get(0), accountTypes);
                break;
            case CLICKED_EMAIL:
                clearEmailFields();
                createAndSetValueInEmailLayout(linkAccountItems.get(0), emailTypes);
                break;
            case CLICKED_CONTACT_NUMBER:
                clearContactNumberFields();
                createAndSetValueInContactLayout(linkAccountItems.get(0), contactTypes);
                break;

        }
    }
    String currentLinkAccountNumber,ifsc;
    private boolean parseResponse(JSONObject response) {
        try {
            JSONObject data = response.getJSONObject("data");
            currentLinkAccountNumber = data.optString("accno");
            ifsc = data.optString("ifsc");
            Fog.d("currentLinkAccountNumber",""+currentLinkAccountNumber+" "+ifsc);
            getResponseInArrayList(response.getJSONObject(getString(R.string.additional_identities)).getJSONArray("identitiesResponse"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String message;
        StringBuilder stringBuilder = getStringBuilder();
        if (stringBuilder.length() > 0) {
            message = stringBuilder.toString();
            if(message.contains("Additional identity detail already exist in fundudb")){

                message = getString(R.string.saved_succssfully);
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                return true;
            }
            else{
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                return false;
            }
        } else {
            message = getString(R.string.saved_succssfully);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            return true;
        }
        //  AlertMessage.showLinkAccountResponseDialog(this, linkAccountItems);
    }

    private void getResponseInArrayList(JSONArray identitiesResponse) {

        if(identitiesResponse!=null){
            linkAccountItems = UniversalParser.getInstance().parseJsonArrayWithJsonObject(identitiesResponse, LinkAccountItem.class);
            for(int i=0;i<linkAccountItems.size();i++){
                Fog.i("LinkAccountActivity:", linkAccountItems.get(i).getAdditional_id_type());
                Fog.i("LinkAccountActivity:", linkAccountItems.get(i).getAdditional_id_value());
                Fog.i("LinkAccountActivity:", linkAccountItems.get(i).getAdditionalIdentity());
                Fog.i("LinkAccountActivity:", linkAccountItems.get(i).getFundu_db_message());
                Fog.i("LinkAccountActivity:", linkAccountItems.get(i).getName());
                Fog.i("LinkAccountActivity:", linkAccountItems.get(i).getSb_message());
                Fog.i("LinkAccountActivity:", linkAccountItems.get(i).getSb_status());
                Fog.i("LinkAccountActivity:", linkAccountItems.get(i).getUpdated_by());
            }
            displayExistingAccount(linkAccountItems);

        }
        else {
            linearLayout_enter_acc_details.setVisibility(View.VISIBLE);
            linearLayoutExistingAcc.setVisibility(View.GONE);
        }

    }

    @NonNull
    private StringBuilder getStringBuilder() {
        StringBuilder stringBuilder = new StringBuilder();
        for (LinkAccountItem linkAccountItem : linkAccountItems) {
            if (linkAccountItem.getSb_status() != null && !linkAccountItem.getSb_status().equals(getString(R.string.success)))
                stringBuilder.append(linkAccountItem.getSb_message() + "\n");
            if (linkAccountItem.getFundu_db_status() != null && !linkAccountItem.getFundu_db_status().equals(getString(R.string.success)))
                stringBuilder.append(linkAccountItem.getFundu_db_message() + "\n");
        }
        return stringBuilder;
    }

    private void addViewsToLayout() {

        for (LinkAccountItem linkAccountItem : linkAccountItems) {
            switch (linkAccountItem.getAdditional_id_type()) {
                case "mobile_type":
                    createAndSetValueInContactLayout(linkAccountItem, contactTypes);
                    break;
                case "email_id":
                    createAndSetValueInEmailLayout(linkAccountItem, emailTypes);
                    break;
                case "acc_ifsc":
                    // displayExistingAccount(linkAccountItems);
                   /* if(linkAccountItems.size()>0){
                        li.setVisibility(View.VISIBLE);
                    }
                    else{
                        linearLayoutAccountNumber.setVisibility(View.GONE);
                    }*/
                    // createAndSetValueInAccountLayout(linkAccountItem, accountTypes);
                    break;
            }
        }
    }

    /*
       setting values to dynamically added views
     */
    private void createAndSetValueInContactLayout(LinkAccountItem linkAccountItem, List<String> contactType) {
        addNewContactLayout(-1);
        contactNumberET.setText(linkAccountItem.getAdditional_id_value());
        contactNumberSP.setSelection(contactType.indexOf(linkAccountItem.getName()));
        setClickableFields(contactNumberET, contactNumberSP);
    }

    private void createAndSetValueInEmailLayout(LinkAccountItem linkAccountItem, List<String> type) {
        addNewEmailLayout(-1);
        emailET.setText(linkAccountItem.getAdditional_id_value());
        emailSP.setSelection(type.indexOf(linkAccountItem.getName()));
        setClickableFields(emailET, emailSP);

    }

    private void createAndSetValueInAccountLayout(LinkAccountItem linkAccountItem, List<String> type) {
        addNewAccountLayout(-1);
        ifscET.setVisibility(View.GONE);
        String[] splitedIdValue = linkAccountItem.getAdditional_id_value().split("_");
        Fog.d("splitedIdValue",""+splitedIdValue[0]+splitedIdValue[1]);
        accountNumberET.setText(splitedIdValue[0]);
        bankNameSP.setSelection(type.indexOf(linkAccountItem.getName()));
        setClickableFields(accountNumberET, bankNameSP);

    }

    private void setClickableFields(EditText editText, Spinner spinner) {
        editText.setClickable(false);
        editText.setEnabled(false);
        spinner.setClickable(false);
        spinner.setEnabled(false);
    }

    /*
       Json Responses
     */
    @Override
    public void onJsonObjectSuccess(JSONObject response, int apiType) throws JSONException {
        switch (apiType) {
            case Constants.ApiType.GET_CUSTOMER_INFO:

                currentLinkAccountNumber = response.optString("accno");
                ifsc = response.optString("ifsc");
                /*FunduUser.setAccountNo(currentLinkAccountNumber);
                FunduUser.setVpa();*/
                getResponseInArrayList(response.getJSONObject(getString(R.string.additional_identities)).getJSONArray("identities"));
                addViewsToLayout();
                break;
            case Constants.ApiType.SAVE_FINAL_ADDITIONAL_IDENTITIES:
                if (parseResponse(response)){
                    finish();
                    String afterAction = getIntent().getStringExtra("after_action");
                    if(afterAction != null && afterAction.equalsIgnoreCase("HomeAcitivity")){
                        Intent intent = new Intent(this,HomeActivity.class).putExtra("after_action","HomeAcitivity");
                        startActivity(intent);
                    }
                    else {
                        Intent intent = new Intent(this,HomeActivity.class).putExtra("after_action","");
                        startActivity(intent);
                    }

                }
                break;
            case Constants.ApiType.SAVE_ADD_MORE_CLICKED_IDENTITES:
                if (parseResponse(response) && linkAccountItems.size() > 0)
                    addLayout();

                clickedAddMore = 0;
                break;
        }
    }





    @Override
    public void onJsonArraySuccess(JSONArray array, int apiType) throws JSONException {
        switch (apiType) {
            case Constants.ApiType.GET_BANKS_NAME:
                banksNameItems = UniversalParser.getInstance().parseJsonArrayWithJsonObject(array, BanksNameItem.class);

                for(int i=0;i<banksNameItems.size();i++){
                    Fog.d("banksNameItems","banksNameItems"+banksNameItems.get(i));
                }

                for (BanksNameItem banksNameItem : banksNameItems)
                    accountTypes.add(banksNameItem.getBankname());
                setAdapter(bankNameSP, accountTypes);

                Fog.d("CallWebService",""+createUrl());
                Fog.d("LinkAccount", "onJsonArraySuccess");
                CallWebService.getInstance(this, true, Constants.ApiType.GET_CUSTOMER_INFO).hitJsonObjectRequestAPI(CallWebService.GET, createUrl(), null, this);
                displayExistingAccount(linkAccountItems);
                break;
        }
    }
    @Override
    public void onFailure(String str, int apiType) {

        clickedAddMore = 0;
    }
    private void getTypes() {
        contactTypes = Arrays.asList(getResources().getStringArray(R.array.contact_number_type));
        emailTypes = Arrays.asList(getResources().getStringArray(R.array.email_type));
        accountTypes = new ArrayList<>();
        CallWebService.getInstance(this, true, Constants.ApiType.GET_BANKS_NAME).hitJsonArrayRequestAPI(CallWebService.GET, API.GET_BANKS_NAME, null, this);
    }

    /*
      clear fields
     */
    private void clearAccountFields() {
        accountNumberET.setText("");
        ifscET.setText("");
        setAdapter(bankNameSP, accountTypes);
    }

    private void clearEmailFields() {
        emailET.setText("");
        setAdapter(emailSP, emailTypes);
    }

    private void clearContactNumberFields() {
        contactNumberET.setText("");
        setAdapter(contactNumberSP, contactTypes);
    }

    /*
        CREATING JSONS
     */
    private void createContactNumberJsonObjects() {
        int numberLLChildCount = containerContacts.getChildCount();
        for (int i = 0; i < numberLLChildCount; i++) {
            View view = containerContacts.getChildAt(i);
            if (view.getId() != -1) {
                contactNumberET = (EditText) view.findViewById(R.id.contactNumberET);
                contactNumberSP = (Spinner) view.findViewById(R.id.contactNumberSP);
                String contactNumber = contactNumberET.getText().toString().trim();
                String spinnerText = contactNumberSP.getSelectedItem().toString().trim();

                if (clickedAddMore > 0) {
                    if (contactNumberET.getText().toString().length() == 0) {
                        contactNumberET.setError(getString(R.string.empty_phone_number_error));
                        contactNumberET.requestFocus();
                        clickedAddMore = 0;
                        break;
                    }
                }
                if (contactNumber.length() > 0 && contactNumber.length() < 7) {
                    contactNumberET.setError(getString(R.string.invalid_contact_number));
                    contactNumberET.requestFocus();
                    clickedAddMore = 0;
                    allFieldsAreOK = false;
                    break;
                } else if (contactNumber.length() > 0) {
                    contactNumber = Utils.formatNumber(this, contactNumber);
                    contactNumber = Utils.appendCountryCodeToNumber(this, contactNumber);
                    createAndSetJsonObjectToArray(contactNumber, spinnerText, getString(R.string.mobile_type));
                }
            }
        }
    }

    private void createEmailJsonObjects() {
        int numberLLChildCount = containerEmails.getChildCount();
        for (int i = 0; i < numberLLChildCount; i++) {
            View view = containerEmails.getChildAt(i);
            if (view.getId() != -1) {
                emailET = (EditText) view.findViewById(R.id.emailET);
                emailSP = (Spinner) view.findViewById(R.id.emailSP);
                String emailAddress = emailET.getText().toString().trim();
                String spinnerText = emailSP.getSelectedItem().toString().trim();
                if (clickedAddMore > 0) {
                    if (emailET.getText().toString().length() == 0) {
                        emailET.setError(getString(R.string.empty_email_error));
                        emailET.requestFocus();
                        clickedAddMore = 0;
                        return;
                    }
                }
                if (emailAddress.length() > 0 && !android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
                    emailET.setError(getString(R.string.invalid_email_address));
                    emailET.requestFocus();
                    allFieldsAreOK = false;
                    break;
                } else
                    createAndSetJsonObjectToArray(emailAddress, spinnerText, getString(R.string.email_type));
            }
        }
    }

    private void createAccountNumberJsonObjects() {
//        int numberLLChildCount = containerAccounts.getChildCount();
        String ifsc="";
      /*  for (int i = 0; i < numberLLChildCount; i++) {*/
        //View view = containerAccounts.getChildAt(i);
        View view = containerAccounts;
        //if (view.getId() != -1) {
        accountNumberET = (EditText)findViewById(R.id.accountNumberET);
        ifscET = (EditText) findViewById(R.id.ifscET);
        bankNameSP = (Spinner)findViewById(R.id.bankNameSP);
        String accountNumber = accountNumberET.getText().toString().trim();
        String confirmaccountNumber = confirmAccNum.getText().toString().trim();
        ifsc = ifscETFixed.getText().toString()+ifscET.getText().toString().trim();

        String spinnerText = bankNameSP.getSelectedItem().toString().trim();
        if(!accountNumber.equalsIgnoreCase(confirmaccountNumber)){
            Toast.makeText(this, "Account Number doesn't matched. Please try again.",
                    Toast.LENGTH_SHORT).show();
            // accountNumberET.setText("");
            confirmAccNum.setText("");
            //ifscET.setText("");
            allFieldsAreOK = false;

        }
        else if(accountNumber.isEmpty()||confirmaccountNumber.isEmpty()||ifsc.isEmpty()||spinnerText.isEmpty())
        {
            Toast.makeText(this, "Please enter all details.",
                    Toast.LENGTH_SHORT).show();
            allFieldsAreOK = false;
        }
        else{
            if (clickedAddMore > 0) {
                if (accountNumberET.getText().toString().length() == 0) {
                    accountNumberET.setError(getString(R.string.account_number_error));
                    accountNumberET.requestFocus();
                    clickedAddMore = 0;
                    return;
                } else if (ifscET.getText().toString().length() == 0) {
                    ifscET.setError(getString(R.string.ifsc_error));
                    ifscET.requestFocus();
                    clickedAddMore = 0;
                    return;
                }
            }
            if (accountNumber.length() > 0)
                accountNumber = accountNumber.replace("-", "");
            createAndSetJsonObjectToArray(accountNumber + "_" + ifsc, spinnerText, getString(R.string.account_type));
        }


    }

    private void createAndSetJsonObjectToArray(String id_value, String spinnerText, String id_type) {
        if (id_value.length() > 0 && !id_value.equals("_")) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(getString(R.string.name), spinnerText);
                jsonObject.put(getString(R.string.visible), true);
                jsonObject.put(getString(R.string.additional_id_type), id_type);
                jsonObject.put(getString(R.string.additional_id_value), id_value);
                identitiesArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void createFinalJson() throws JSONException {

        mainJsonObject = new JSONObject();
        additionalIdentitiesObject = new JSONObject();
        additionalIdentitiesObject.put(getString(R.string.identites), identitiesArray);
        String accountNo = accountNumberET.getText().toString();
        accountNo = accountNo.replace("-","");
        mainJsonObject.put(getString(R.string.additional_identities), additionalIdentitiesObject);
        mainJsonObject.put("country_shortname", FunduUser.getAppPreferences().getString(Constants.COUNTRY_SHORTCODE));
        mainJsonObject.put("mobile", FunduUser.getAppPreferences().getString(Constants.PrefKey.CONTACT_NUMBER));
        mainJsonObject.put("accno",accountNo);
        mainJsonObject.put("ifsc",ifscETFixed.getText().toString()+ifscET.getText().toString());
        String vpa = FunduUser.getVpa();
        if(vpa != null && vpa.length() != 0)
            mainJsonObject.put("vpa",vpa);
        //mainJsonObject.put("vpa",vpa);
    }

    @Override
    public void onFragmentInteraction(Bundle bundle) {

    }






}
