package in.co.eko.fundu.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.R;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.requests.CheckBalanceRequest;
import in.co.eko.fundu.requests.TransactionCancelRequest;
import in.co.eko.fundu.requests.TransactionCommitRequest;


public class CommitTransactionActivity extends BaseActivity implements View.OnClickListener, TransactionCommitRequest.OnTransactionCommitResults, TransactionCancelRequest.OnTransactionCancelResults {

    private ProgressDialog dialog;
    private ArrayList<String> list;
    private String transactionId;
    private TransactionCommitRequest request;
    private String toolbarText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commit_transaction);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarText = getIntent().getStringExtra(Constants.NAME);
        setSupportActionBar(toolbar);
        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        dialog = new ProgressDialog(this);
        dialog.setMessage("Committing transaction...");
        dialog.setCancelable(false);
        Button button = (Button) findViewById(R.id.commitTransaction);
        Button cancelTransaction = (Button) findViewById(R.id.cancelTransaction);
        TextView name = (TextView) findViewById(R.id.name);
        TextView amount = (TextView) findViewById(R.id.amount);
        final TextView totalBalance = (TextView) findViewById(R.id.totalBalance);

        cancelTransaction.setOnClickListener(this);
        button.setOnClickListener(this);
        Intent intent = getIntent();
        list = intent.getStringArrayListExtra(Constants.ALERT);
        transactionId = intent.getStringExtra(Constants.TRANSACTION_ID);
        request = new TransactionCommitRequest(this);
        request.setData(transactionId,"");
        request.setParserCallback(this);
        amount.setText(getString(R.string.ruppee_symbol) + list.get(9));
        name.setText(list.get(8));

        if (toolbarText != null && toolbarText.length() > 0) {
            toolbar.setTitle(toolbarText);
            button.setText(R.string.confirm);
        }

        // list.add(7, list.get(8));
        CheckBalanceRequest balanceRequest = new CheckBalanceRequest(this);
        balanceRequest.setData(FunduUser.getContactId());
        balanceRequest.setParserCallback(new CheckBalanceRequest.OnCheckBalanceResults() {
            @Override
            public void onCheckBalanceResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    totalAmount = jsonObject.optString("Balance Amount");
                    totalBalance.setText(getString(R.string.ruppee_symbol) + totalAmount);
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

    String totalAmount;

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.commitTransaction) {
            request.start();
            dialog.show();
        } else if (v.getId() == R.id.cancelTransaction) {
            TransactionCancelRequest cancelRequest = new TransactionCancelRequest(this);
            cancelRequest.setData(transactionId,"","","","",FunduUser.getCountryShortName());
            cancelRequest.setParserCallback(this);
            cancelRequest.start();
            dialog.setMessage("Cancelling transaction...");
            dialog.show();
        }
    }

    @Override
    public void onTransactionCommitResponse(String s) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        //Toast.makeText(this, "Something wrong.", Toast.LENGTH_SHORT).show();
        if (toolbarText != null && toolbarText.length() > 0) {
            Intent sendMoneyIntent = new Intent(this, MoneyToAccountSuccess.class);
            sendMoneyIntent.putExtra(Constants.AMOUNT, Float.parseFloat(list.get(9)));
            sendMoneyIntent.putExtra(Constants.ACCOUNT_NUMBER, list.get(8));
            startActivity(sendMoneyIntent);
            finish();
        } else {
            Intent intent = new Intent(this, TransactionSuccessActivity.class);
            intent.putExtra(Constants.RATING_TYPE, 1);
            intent.putStringArrayListExtra(Constants.ALERT, list);
            intent.putExtra(Constants.TRANSACTION_ID, transactionId);
            if (totalAmount != null) {
                intent.putExtra(Constants.TOTAL_AMOUNT, totalAmount);
            }
            startActivity(intent);
            finish();
        }

    }

    @Override
    public void onTransactionCommitError(VolleyError error) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        Toast.makeText(this, "Something wrong.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onTransactionCancelResponse(JSONObject response) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        CheckBalanceRequest balanceRequest = new CheckBalanceRequest(this);
        balanceRequest.setData(FunduUser.getContactId());
        balanceRequest.start();
        Toast.makeText(this, "Transaction canceled successfully.", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onTransactionCancelError(VolleyError error) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        Toast.makeText(this, "Transaction cancellation error.", Toast.LENGTH_SHORT).show();
    }
}
