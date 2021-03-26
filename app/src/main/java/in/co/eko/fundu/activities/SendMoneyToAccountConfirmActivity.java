package in.co.eko.fundu.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.R;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.requests.CheckBalanceRequest;
import in.co.eko.fundu.requests.KenConfirmationRequest;
import in.co.eko.fundu.requests.TransactionInitiateRequest;
import in.co.eko.fundu.utils.UserTransactions;
import in.co.eko.fundu.utils.Utils;
import in.co.eko.fundu.views.CustomProgressDialog;

/**
 * Created by divyanshu.jain on 8/5/2016.
 */
public class SendMoneyToAccountConfirmActivity extends BaseActivity implements AppCompatDialog.OnDismissListener, View.OnClickListener, TransactionInitiateRequest.OnTransactionInitiateResults {
    private String recipient_mobile, accountNumber = "", custid, custname;
    private int recipientID;
    private float amountText;
    private CustomProgressDialog customProgressDialog;
    private KenConfirmationRequest confirmrequest;
//    private TotpDialog totpDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commit_transaction);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        String toolbarText = getIntent().getStringExtra(Constants.NAME);
        if (toolbarText != null && toolbarText.length() > 0)
            toolbar.setTitle(toolbarText);
        customProgressDialog = new CustomProgressDialog(this);
        customProgressDialog.setMessage("Please Wait...");
        confirmrequest = new KenConfirmationRequest(getApplicationContext());
        setSupportActionBar(toolbar);
        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
//        if (FunduUser.getCountryShortName().equalsIgnoreCase("KEN")) {
//            totpDialog = new TotpDialog(this);
//            totpDialog.setOnDismissListener(this);
//        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Button button = (Button) findViewById(R.id.commitTransaction);
        button.setText(R.string.confirm);
        Button cancelTransaction = (Button) findViewById(R.id.cancelTransaction);
        TextView name = (TextView) findViewById(R.id.name);
        TextView amount = (TextView) findViewById(R.id.amount);
        final TextView totalBalance = (TextView) findViewById(R.id.totalBalance);

        cancelTransaction.setOnClickListener(this);
        button.setOnClickListener(this);

        Intent intent = getIntent();
        recipientID = intent.getIntExtra(Constants.RECIPIENT_ID, 0);
        recipient_mobile = intent.getStringExtra(Constants.RECIPIENT_NUMBER);
        accountNumber = intent.getStringExtra(Constants.ACCOUNT_NUMBER);
        amountText = intent.getFloatExtra(Constants.AMOUNT, 0);
        custid = intent.getStringExtra("user_id");
        custname = intent.getStringExtra("user_name");

        amount.setText(Utils.getCurrency(getApplicationContext())/*getString(R.string.ruppee_symbol)*/ + String.valueOf(amountText));
        name.setText(accountNumber);
        // list.add(7, list.get(8));
        if (FunduUser.getCountryShortName().equalsIgnoreCase("IND")) {
            CheckBalanceRequest balanceRequest = new CheckBalanceRequest(this);
            balanceRequest.setData(FunduUser.getContactId());
            balanceRequest.setParserCallback(new CheckBalanceRequest.OnCheckBalanceResults() {
                @Override
                public void onCheckBalanceResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        totalAmount = jsonObject.optString("Balance Amount");
                        totalBalance.setText(Utils.getCurrency(getApplicationContext())/*getString(R.string.ruppee_symbol)*/ + totalAmount);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCheckBalanceError(VolleyError error) {
                    totalBalance.setText("Request Failed");
                }
            });
            balanceRequest.start();
        }
    }

    String totalAmount;

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.commitTransaction) {
            if (FunduUser.getCountryShortName().equals("KEN")) {
//                customProgressDialog.show();
//                        totpDialog.setData(custid,recipient_mobile, "tid", (int)Math.round(amountText), Integer.parseInt(FunduUser.getChargesKen()));
//                        totpDialog.show(); // Testing
                confirmrequest.setData(FunduUser.getCustomerId(), Math.round(amountText), FunduUser.getCountryShortName(), Constants.SEND_MONEY_TYPE);
                confirmrequest.setParserCallback(new KenConfirmationRequest.KenConfirmationResults() {
                    @Override
                    public void onKenConfirmationResponse(String object) {
                        customProgressDialog.dismiss();
//                        totpDialog.show();
                    }

                    @Override
                    public void onKenConfirmationError(VolleyError error) {
                        customProgressDialog.dismiss();
                        Utils.showShortToast(getApplicationContext(), "Error!");
                    }
                });
//                confirmrequest.start();
            } else {
                customProgressDialog.show();
                UserTransactions.getInstance().initTransactionForSendMoneyToAccount(this, FunduUser.getContactId(), FunduUser.getContactIDType(), recipient_mobile, FunduUser.getContactIDType(), (int) amountText, 30000, recipientID);
            }
        }else if (v.getId() == R.id.cancelTransaction) {
            finish();
        }
    }

    @Override
    public void onTransactionInitiateResponse(JSONObject response) {
        if (customProgressDialog != null)
            customProgressDialog.dismiss();
        Intent intent = new Intent(this, MoneyToAccountSuccess.class);
        intent.putExtra(Constants.AMOUNT, amountText);
        intent.putExtra(Constants.ACCOUNT_NUMBER, accountNumber);
        startActivity(intent);
        finish();
    }

    @Override
    public void onTransactionInitiateError(VolleyError error) {
        if (customProgressDialog != null)
            customProgressDialog.dismiss();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {

    }
}

