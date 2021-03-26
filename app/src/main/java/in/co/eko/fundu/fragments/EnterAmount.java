package in.co.eko.fundu.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import in.co.eko.fundu.R;
import in.co.eko.fundu.activities.GetCashFromContact;
import in.co.eko.fundu.activities.HomeActivity;
import in.co.eko.fundu.activities.upi.YesBankUPIClient;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.gcm.FunduNotificationManager;
import in.co.eko.fundu.interfaces.OnFragmentInteractionListener;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.requests.HasFundRequest;
import in.co.eko.fundu.utils.AppPreferences;
import in.co.eko.fundu.utils.Fog;
import in.co.eko.fundu.utils.FunduAnalytics;
import in.co.eko.fundu.utils.Utils;
import in.co.eko.fundu.views.ProgressOverlay;

import static android.view.KeyEvent.KEYCODE_DEL;
import static in.co.eko.fundu.R.id.amtFee;
import static in.co.eko.fundu.R.id.amtPayable;
import static in.co.eko.fundu.activities.upi.YesBankUPIClient.YES_BANK_CLIENT_ACTION.CHECK_BALANCE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EnterAmount#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EnterAmount extends Fragment implements View.OnClickListener, TextView.OnEditorActionListener {

    protected AppPreferences pref;
    private TextView fa1,fa2,fa3,fa4,fa5;
    private EditText amountNeeded;
    private TextView requestCash;
    private ProgressBar mFeeloader;
    private int charges;
    private String amtPaayble;
    private TextView currency_icon,mAmtPayable,mAmtFee,mError;
    private TextView textviewInfo;
    private ProgressDialog dialog;
    private View mCheckBalance;
    public EnterAmount() {
        // Required empty public constructor
    }


    public static EnterAmount newInstance() {
        EnterAmount fragment = new EnterAmount();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        Activity parentActivity = getActivity ();
        if(parentActivity != null && parentActivity instanceof HomeActivity){
            ((HomeActivity)parentActivity).hideHamburgerIcon ();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_enter_amount, container, false);
        init(view);
        FunduAnalytics.getInstance(getActivity()).sendScreenName("EnterAmount");
        return view;
    }
    private void init(View view){
        dialog = new ProgressDialog(getActivity());
        dialog.setCancelable(false);
        dialog.setMessage("Please wait...");
        pref = new AppPreferences(getActivity());
        fa1 = (TextView)view.findViewById(R.id.fixedamount1);
        fa2 = (TextView)view.findViewById(R.id.fixedamount2);
        fa3 = (TextView)view.findViewById(R.id.fixedamount3);
        fa4 = (TextView)view.findViewById(R.id.fixedamount4);
        fa5 = (TextView)view.findViewById(R.id.fixedamount5);

        mCheckBalance = view.findViewById ( R.id.checkbalance );
        mFeeloader = (ProgressBar) view.findViewById(R.id.feeloader);
        mAmtPayable = (TextView)view.findViewById(amtPayable);
        mAmtFee = (TextView)view.findViewById(amtFee);
        amountNeeded = (EditText)view.findViewById(R.id.amountNeeded);
        requestCash = (TextView)view.findViewById(R.id.requestCash);
        currency_icon = (TextView)view.findViewById(R.id.currency_icon);
        textviewInfo = (TextView)view.findViewById(R.id.textviewInfo);
        mError = (TextView)view.findViewById(R.id.error);
        amountNeeded.requestFocus();
        amountNeeded.setSelection(amountNeeded.length());
        Utils.toggleSoftKeyboard(getActivity());
        amountNeeded.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_UP){
                    if(keyCode == KEYCODE_DEL){
                        mError.setVisibility(View.GONE);
                    }
                }
                return false;
            }
        });
        amountNeeded.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int start,
                                      int before, int count) {

                mError.setVisibility(View.GONE);
               if(count==0){
                   mAmtFee.setVisibility(View.GONE);
                   mAmtPayable.setVisibility(View.GONE);
                   disableRequestCash();

               }
               disableRequestCash();
               checkIfValidAmount(false);


            }
            @Override
            public void afterTextChanged(final Editable editable) {
//                if(!amountNeeded.getText().toString().equalsIgnoreCase("")){
//                    int amt = Integer.parseInt(amountNeeded.getText().toString());
//                    if(FunduUser.getCountryShortName().equalsIgnoreCase("KEN")){
//                        if(editable.length()==4&&amt>9000){
//                            //amountNeeded.setText("");
//                            disableRequestCash();
//                        }
//                    }
//                    else{
//                        if(editable.length()==4&&amt>2000){
//                            //samountNeeded.setText("");
//                            disableRequestCash();
//                        }
//                    }
//
//                }
            }
        });


        if(Utils.getCurrency(getContext()).equalsIgnoreCase("Shs.")){
            currency_icon.setText("Ksh");
            textviewInfo.setText(R.string.enter_amount_desc_ken);
        }
        else {
            currency_icon.setText(Utils.getCurrency(getContext()));
            textviewInfo.setText(R.string.enter_amount_desc_india);
        }
        fa1.setOnClickListener(this);
        fa2.setOnClickListener(this);
        fa3.setOnClickListener(this);
        fa4.setOnClickListener(this);
        fa5.setOnClickListener(this);
        mCheckBalance.setOnClickListener ( this );
        amountNeeded.setOnEditorActionListener(this);
        requestCash.setOnClickListener(this);
        if(amountNeeded.getText().toString()!=null){

            checkWallet(Integer.parseInt(amountNeeded.getText().toString()));   
        }
        if(getActivity() instanceof HomeActivity){
            View progressOverlay = view.findViewById(R.id.progressOverlay);
            ((HomeActivity)getActivity()).setProgressOverLay((ProgressOverlay) progressOverlay);
        }

        if(TextUtils.isEmpty(FunduUser.getRecipientId()) || FunduUser.getCountryShortName().equalsIgnoreCase("KEN")){
            mCheckBalance.setVisibility(View.GONE);
        }
        disableRequestCash();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {

        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {

        if(v == mCheckBalance){
            Utils.hideSoftKeyboard(getActivity());
            checkUserBalance();
        }
        else if(v == requestCash){
            //Request Cash request
            String cashNeededString = amountNeeded.getText().toString();
            if(cashNeededString == null || cashNeededString.length() == 0){
                Toast.makeText(getActivity(),"Select a valid amount",Toast.LENGTH_SHORT).show();
                return;
            }
            Utils.hideSoftKeyboard(getActivity());
            Activity activity = getActivity();
            FunduAnalytics.getInstance(activity).sendAction(null,"AmountRequested",amountNeeded.getText().toString());
            FunduAnalytics.getInstance(activity).sendAction("Transaction","Requested",(int)Double.parseDouble(amountNeeded.getText().toString()));
            if(activity != null && activity instanceof HomeActivity){
              HomeActivity hActivity = (HomeActivity)getActivity();
               // hActivity.submitNeedCashRequest(cashNeededString);
                if(charges!=0.0||charges!=0){
                    FunduNotificationManager.setOnPairResult(hActivity);
                    hActivity.startFindTransactionPairRequest(amountNeeded.getText().toString(),String.valueOf(charges));
                }
            }
            else if(activity != null && activity instanceof GetCashFromContact){
                GetCashFromContact gActivity = (GetCashFromContact)activity;
                if(charges!=0.0||charges!=0){
                    gActivity.submitNeedCashRequest(cashNeededString,String.valueOf(charges));
                }
            }

        }
        else if(v == fa1){
            amountNeeded.setText(fa1.getText().toString());
            amountNeeded.setSelection(fa1.getText().length());
            int amount = Integer.parseInt(amountNeeded.getText().toString());
            checkWallet(amount);
        }
        else if(v == fa2){
            amountNeeded.setText(fa2.getText().toString());
            amountNeeded.setSelection(fa2.getText().length());
            int amount = Integer.parseInt(amountNeeded.getText().toString());
            checkWallet(amount);
        }
        else if(v == fa3){
            amountNeeded.setText(fa3.getText().toString());
            amountNeeded.setSelection(fa3.getText().length());
            int amount = Integer.parseInt(amountNeeded.getText().toString());
            checkWallet(amount);
           // checkWallet(Integer.parseInt(amountNeeded.getText().toString()));
        }
        else if(v == fa4){
            amountNeeded.setText(fa4.getText().toString());
            amountNeeded.setSelection(fa4.getText().length());
            int amount = Integer.parseInt(amountNeeded.getText().toString());
            checkWallet(amount);
            //checkWallet(amount);

        }
        else if(v == fa5){
            amountNeeded.setText(fa5.getText().toString());
            amountNeeded.setSelection(fa5.getText().length());
            int amount = Integer.parseInt(amountNeeded.getText().toString());
            checkWallet(amount);
            //checkWallet(amount);

        }
    }
    private void checkUserBalance(){

        if(Constants.dummyUPI){
            BottomSheetDialog dialog = new BottomSheetDialog(getActivity());
            dialog.setContentView(R.layout.yes_bank_dummy_popup);
            TextView tv = (TextView) dialog.findViewById(R.id.info);
            tv.setText("Dummy Balance - 123456");
            dialog.show();
            return;
        }
        FunduAnalytics.getInstance(getActivity()).sendAction("EnterAmount","CheckBalance",1);
        Intent intent = new Intent(getActivity (), YesBankUPIClient.class);
        intent.putExtra ( "action",CHECK_BALANCE);
        intent.putExtra("recipient_id",FunduUser.getRecipientId());
        startActivityForResult (intent,1);
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode, Intent data)
    {

        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            Bundle bundle = data.getExtras();
            if(bundle == null)
                return;
            for (String key : bundle.keySet())
            {
                Log.e("Bundle Debug", key + " = \"" + bundle.get(key) + "\"");
            }
        }

    }

    private void checkWallet(int pAmount) {

        mAmtFee.setVisibility(View.GONE);
        mAmtPayable.setVisibility(View.GONE);
        mFeeloader.setVisibility(View.VISIBLE);
        JSONObject object = new JSONObject();

        try {

            object.put("custid", FunduUser.getCustomerId());
            object.put("mobile", pref.getString(Constants.PrefKey.CONTACT_NUMBER));
            object.put("country_shortname", pref.getString(Constants.COUNTRY_SHORTCODE));
            object.put("amount", String.valueOf(pAmount));
            object.put("type", Constants.NEED_CASH_TYPE);
            Fog.d("object",""+object);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        HasFundRequest request = new HasFundRequest(getActivity(), object);
        request.setParserCallback(new HasFundRequest.OnHasFundResults() {
            @Override
            public void onHasFundResponse(String response) {
                if(getActivity() == null){
                    return;
                }
                mFeeloader.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonObject1 = jsonObject.getJSONObject("data");

                    Fog.d("onHasFundResponse",""+response);
                    if (jsonObject.has("Balance Amount")) {

                    } else {
                        if (jsonObject.has("Error"))
                            Toast.makeText(getActivity(), jsonObject.optString("Error"), Toast.LENGTH_SHORT).show();
                    }
                    if(jsonObject.optString("status").equalsIgnoreCase("Success")){

                        charges = Integer.parseInt(jsonObject.optString("charges"));
                        amtPaayble = jsonObject1.optString("amtWithTxCharges");
                        int jAmount = jsonObject1.optInt ( "amount" );
                        int tvAmount = 0;
                        if(amountNeeded.getText ().length () != 0)
                            tvAmount = Integer.parseInt ( amountNeeded.getText ().toString () );
                        if(tvAmount == jAmount){
                            mAmtFee.setVisibility(View.VISIBLE);
                            mAmtPayable.setVisibility(View.VISIBLE);
                            mAmtFee.setText(Utils.getCurrency(getActivity())+" "+charges);
                            mAmtPayable.setText(Utils.getCurrency(getActivity())+" "+amtPaayble);
                            enableRequestCash();
                        }
                        Fog.d("charges",""+charges+amtPaayble);
                    }
                    else if (jsonObject.optString("status").equalsIgnoreCase("ERROR")){

                        if (jsonObject.optString("message").equalsIgnoreCase("Customer doesn't exist")
                                || jsonObject.optString("message").equalsIgnoreCase("Customer not registered with us")){
                            HomeActivity.Signout(getActivity());
                        }
                        Toast.makeText(getActivity(), jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Fog.d("EnetrAmount", "Exception - Due to unexpected key-value");
                }
            }
            @Override
            public void onHasFundError(VolleyError error) {
                if(getActivity() == null){
                    return;
                }
                if(dialog!=null&&dialog.isShowing()){
                    dialog.dismiss();
                }
                mFeeloader.setVisibility(View.GONE);

            }
        });
        request.start();
    }
    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        boolean handled = false;
        Utils.hideSoftKeyboard(getActivity());
        Fog.i(EnterAmount.class.getName(),"editor action "+actionId);

        if (actionId == EditorInfo.IME_ACTION_NEXT) {

            if(checkIfValidAmount(true))
                Utils.hideSoftKeyboard(getActivity());
            else
                disableRequestCash();
        }
        return handled;

    }
    /**
     *
     * @return Error if any
     */
    private boolean checkIfValidAmount(boolean next){
        int MIN_AMOUNT = 200;
        int MAX_AMOUNT = 2000;
        if(!TextUtils.isEmpty(amountNeeded.getText().toString())){
            int amount = Integer.parseInt(amountNeeded.getText().toString());
            String error = "";
            String currency = getString ( R.string.rs_symbol );
            if(FunduUser.getCountryShortName().equalsIgnoreCase("IND")){

                MIN_AMOUNT = 1;
                MAX_AMOUNT = 5000;
            }
            else{

                MIN_AMOUNT = 200;
                MAX_AMOUNT = 9000;
                currency = getString ( R.string.ksh_symbol );
            }
            error = String.format(getString(R.string.please_enter_amount_between)," "+currency+MIN_AMOUNT
                    +" and "+currency+
                    MAX_AMOUNT);

            if(amount>MAX_AMOUNT || amount < MIN_AMOUNT){
                mAmtPayable.setVisibility(View.GONE);
                mAmtFee.setVisibility(View.GONE);
                if(!next && amount < MIN_AMOUNT){
                    return false;
                }
                mError.setText(error);
                mError.setVisibility(View.VISIBLE);
                Utils.vibratePhone(getActivity());
                return false;
            }
            else{
                checkWallet(amount);
                return true;
            }
        }
        return false;

    }


    public void disableRequestCash(){
        requestCash.setEnabled(false);

    }

    public void enableRequestCash(){
        requestCash.setEnabled(true);
       // Utils.hideSoftKeyboard(getActivity());

    }






}
