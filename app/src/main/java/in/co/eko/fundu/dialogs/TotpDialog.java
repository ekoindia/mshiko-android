
package in.co.eko.fundu.dialogs;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.R;
import in.co.eko.fundu.activities.TransactionSuccessActivity;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.requests.KenTransferRequest;
import in.co.eko.fundu.utils.AppPreferences;
import in.co.eko.fundu.utils.Fog;
import in.co.eko.fundu.utils.Utils;
import in.co.eko.fundu.views.CustomProgressDialog;

/**
 * Created by user on 12/28/16.
 */

public class TotpDialog extends AppCompatDialog implements View.OnClickListener, TextWatcher,  TextView.OnEditorActionListener  {
    private CustomProgressDialog dialog;
    public static EditText entertotpedit, enterFunduPin;
    private Button submitotp, cancaltotp;
    private TextView pName, pMobile, pTid, pAmount, pFees, pTotalAmount, oneminute;
    private String name, mobile, tid, pCustid, type;
    private int fees;
    private double amount;
    private KenTransferRequest kenTransferRequest;
    protected AppPreferences pref;
    private CountDownTimer cdt;


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    public void setData(String name, String mobile, String tid, double amount, int fees, String provider_custid, String type){
        this.name = name;
        this.mobile = mobile;
        this.tid = tid;
        this.amount = amount;
        this.fees = fees;
        this.pCustid = provider_custid;
        this.type = type;
        pName.setText(name);
        pMobile.setText(mobile);
        pTid.setText(tid);
        pAmount.setText("Shs. "+String.valueOf(amount));
        pFees.setText("Shs. "+String.valueOf(fees));
        pTotalAmount.setText("Shs. "+String.valueOf(amount+fees));
        entertotpedit.setText("");
        enterFunduPin.setText("");
        Fog.d("TOTP", enterFunduPin.getText().toString() + " EDIT " + entertotpedit.getText().toString());
        cdt = new CountDownTimer(60000, 1000) { // adjust the milli seconds here

            public void onTick(long millisUntilFinished) {
                oneminute.setText("You will get Verification Code SMS in 00:"+String.format("%d sec",
//                        TimeUnit.MILLISECONDS.toMinutes( millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }

            public void onFinish() {
                oneminute.setText(getContext().getString(R.string.enter_code_manually));

            }
        }.start();
    }

    public TotpDialog(AppCompatActivity baseActivity) {
        super(baseActivity, R.style.AppTheme);
        setContentView(R.layout.dialog_totp);
        pref= FunduUser.getAppPreferences();
        dialog = new CustomProgressDialog(baseActivity);
        dialog.setCancelable(false);

        entertotpedit = (EditText)findViewById(R.id.edittextotp);
        enterFunduPin = (EditText)findViewById(R.id.editfundupin);
        submitotp = (Button) findViewById(R.id.submitTotp);
        submitotp.setEnabled(true);
        cancaltotp = (Button) findViewById(R.id.canceltotp);
        pName = (TextView) findViewById(R.id.name);
        pMobile = (TextView) findViewById(R.id.mobile);
        pTid = (TextView) findViewById(R.id.pTid);
        pAmount = (TextView) findViewById(R.id.amount);
        pFees = (TextView) findViewById(R.id.fees);
        pTotalAmount = (TextView) findViewById(R.id.pTotalAmount);
        oneminute = (TextView) findViewById(R.id.oneMinute);
        pName.setText(name);
        pMobile.setText(mobile);
        pTid.setText(tid);
        pAmount.setText("Shs. "+String.valueOf(amount));
        pFees.setText("Shs. "+String.valueOf(fees));
        pTotalAmount.setText("Shs. "+String.valueOf(amount+fees));


        entertotpedit.setOnEditorActionListener(this);
        entertotpedit.addTextChangedListener(this);
        submitotp.setOnClickListener(this);
        cancaltotp.setOnClickListener(this);
        kenTransferRequest = new KenTransferRequest(getContext());
        kenTransferRequest.setParserCallback(new KenTransferRequest.OnKenTransferResults() {
            @Override
            public void onKenTransferResponse(String object) {
                dialog.dismiss();
                try {
                    Fog.d("Fundu Resp", object);
                    JSONObject response = new JSONObject(object);
//                    {"message":"SUCCESS","status":"SUCCESS"}
//                    {"message":"Incorrect one time password","status":"ERROR"}
//                    {"tid":"FJAN7000000009","message":"Funds transfered successfully!","status":"SUCCESS"}
                    if (response.has("status")){
                        if (response.getString("status").equalsIgnoreCase("ERROR")){
                            /*if (response.getString("message").equalsIgnoreCase("Incorrect FundU Pin")){
                                Utils.showLongToast(getContext(), response.getString("message")*//*"Wrong verification Code!"*//*);
                            }
                            else */
                            if (response.getString("message").contains("Incorrect one time password")){
                                Utils.showLongToast(getContext(), "Wrong verification Code");
                            } else if (response.optString("message").equalsIgnoreCase("No universal account")) {
                                new AlertDialog.Builder(getContext())
                                        .setTitle("Alert")
                                        .setMessage(name + " has entered wrong Account Details. Wait for him correct his " +
                                                "account details and then you can initiate transaction again.")
                                        .setCancelable(false)
                                        .setPositiveButton("ok", new OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // Whatever...
                                                cdt.cancel();
                                                enterFunduPin.setText("");
                                                entertotpedit.setText("");

                                                dismiss();
                                            }
                                        }).show();
                            }
                            else
                            Utils.showLongToast(getContext(), response.getString("message"));
                        }
                        else if (response.getString("status").equalsIgnoreCase("SUCCESS")){
                            if (response.has("message")){
                                if (response.has("tid")){


                                }
                                if (response.optString("message").equalsIgnoreCase("Funds transfered successfully!")
                                        || response.optString("message").equalsIgnoreCase("SUCCESS")) {

                                    enterFunduPin.setText("");
                                    entertotpedit.setText("");

                                    if (type.equalsIgnoreCase(Constants.SEND_MONEY_TYPE)) {
                                        Intent intent = new Intent(getContext(), TransactionSuccessActivity.class);
                                        intent.putExtra(Constants.RATING_TYPE, 1);
                                        intent.putExtra(Constants.TRANSACTION_ID, response.getString("tid"));
                                        intent.putExtra(Constants.TOTAL_AMOUNT, String.valueOf(amount));
                                        intent.putExtra("pname", name);
                                        intent.putExtra("pmobile", mobile);
                                        getContext().startActivity(intent);
                                        dismiss();
                                    } else
                                        dismiss();
                                }
                                else if (response.optString("message").contains("Money send successfully!")
                                        || response.optString("message").contains("transaction was successfull")){

                                    enterFunduPin.setText("");
                                    entertotpedit.setText("");
                                    Toast.makeText(getContext(), "Money sent successfully!", Toast.LENGTH_SHORT).show();
                                    if (type.equalsIgnoreCase(Constants.SEND_MONEY_TYPE)) {
                                        Intent intent = new Intent(getContext(), TransactionSuccessActivity.class);
                                        intent.putExtra(Constants.RATING_TYPE, 1);
                                        intent.putExtra(Constants.TRANSACTION_ID, response.getString("tid"));
                                        intent.putExtra(Constants.TOTAL_AMOUNT, String.valueOf(amount));
                                        intent.putExtra("pname", name);
                                        intent.putExtra("pmobile", mobile);
                                        getContext().startActivity(intent);
                                        dismiss();
                                    } else
                                        dismiss();
                                }
                                else if (response.optString("message").equalsIgnoreCase("Incorrect FundU Pin")){
                                    Toast.makeText(getContext(), "Incorrect Fundu PIN entered!", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Utils.showLongToast(getContext(), response.getString("message"));
                                }
                        }
                    }
                    }
                    else{
                        Utils.showLongToast(getContext(), "Error!");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onKenTransferError(VolleyError error) {
                Fog.d("Fundu Resp E", "Error");

                dialog.dismiss();
                Toast.makeText(getContext(), "Transaction Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void setOnDismissListener(OnDismissListener listener) {
        super.setOnDismissListener(listener);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.canceltotp:
                dsms();
                break;
            case R.id.submitTotp: {
                Fog.d("TOTP SUB", enterFunduPin.getText().toString() + " EDIT " + entertotpedit.getText().toString());
                if (entertotpedit.getText().toString().trim().equalsIgnoreCase("") && enterFunduPin.getText().toString().trim().equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), "Please Enter Verification Code and Fundu PIN", Toast.LENGTH_SHORT).show();
                }
//                else if (enterFunduPin.length()<4 && entertotpedit.length()<4){
//                    Toast.makeText(getContext(), "Please Enter OTP and Fundu Pin", Toast.LENGTH_SHORT).show();
//                }
                else if (entertotpedit.length() < 4) {
                    Toast.makeText(getContext(), "Please Enter Verification Code", Toast.LENGTH_SHORT).show();
                } else if (enterFunduPin.length() < 4) {
                    Toast.makeText(getContext(), "Please Enter 4 digit Fundu PIN", Toast.LENGTH_SHORT).show();
                } else {
                    if (Utils.isNetworkAvailable(getContext())) {
                        kenTransferRequest.setData(pref.getString(Constants.CUSTOMERID), pCustid, 200, FunduUser.getCountryShortName(), entertotpedit.getText().toString(), Utils.md5(enterFunduPin.getText().toString()), type);
                        kenTransferRequest.start();
                        dialog.show();
                    }
                }
            }
                break;
        }
    }

    void dsms(){
        new AlertDialog.Builder(getContext())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Cancel Transaction?")
                .setMessage("Are you sure you want to cancel the transaction?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cdt.cancel();
                        enterFunduPin.setText("");
                        entertotpedit.setText("");
                        dismiss();
                    }

                })
                .setNegativeButton("No", null)
                .show();

    }



    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() <4) {
//            submitotp.setEnabled(true);
        } else {
            if (s.length()==4) {

//                submitotp.setEnabled(true);
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        boolean handled = false;
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            if (entertotpedit.getText().length()>3){
//                kenTransferRequest.setData(pref.getString(Constants.CUSTOMERID),PairContactFoundActivity.pCustid, PairContactFoundActivity.pAmount, FunduUser.getCountryShortName(),entertotpedit.getText().toString(), md5(enterFunduPin.getText().toString()));
//                kenTransferRequest.start();
            }

            handled = true;
        }
        return handled;
    }

}
