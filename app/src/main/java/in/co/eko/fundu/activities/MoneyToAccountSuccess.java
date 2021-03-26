package in.co.eko.fundu.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.R;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.requests.CheckBalanceRequest;
import in.co.eko.fundu.utils.Utils;

/**
 * Created by divyanshu.jain on 8/5/2016.
 */
public class MoneyToAccountSuccess extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.transactionMessage)
    TextView transactionMessage;
    private String totalAmount;
    private TextView remainingAmount;
    private String accountNumber = "";
    private float amountText;
    private String caller = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.money_to_account_success);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        caller = intent.getStringExtra("caller");
        if (caller != null && caller.equalsIgnoreCase("HomeActivity")) {
            transactionMessage.setText(getString(R.string.have_been_sent_to_your_wallet));
        }

        totalAmount = intent.getStringExtra(Constants.TOTAL_AMOUNT);
        remainingAmount = (TextView) findViewById(R.id.remainingAmount);
        TextView amount = (TextView) findViewById(R.id.amount);
        TextView name = (TextView) findViewById(R.id.name);
        Button submit = (Button) findViewById(R.id.done);

        accountNumber = intent.getStringExtra(Constants.ACCOUNT_NUMBER);
        amountText = intent.getFloatExtra(Constants.AMOUNT, 0);

        amount.setText(getString(R.string.ruppee_symbol) + String.valueOf(amountText));
        name.setText(accountNumber);
        submit.setOnClickListener(this);
        CheckBalanceRequest balanceRequest = new CheckBalanceRequest(this);
        balanceRequest.setData(FunduUser.getContactId());
        balanceRequest.setParserCallback(new CheckBalanceRequest.OnCheckBalanceResults() {
            @Override
            public void onCheckBalanceResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    totalAmount = jsonObject.optString(getString(R.string.balance_amount));
                    float totalAmountInFloat = Float.parseFloat(totalAmount);
                    remainingAmount.setText(getString(R.string.ruppee_symbol) + totalAmountInFloat);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCheckBalanceError(VolleyError error) {
                remainingAmount.setText("Request Failed");
            }
        });
        if (totalAmount == null)
            balanceRequest.start();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.done) {
            Utils.showShortToast(this, getString(R.string.transaction_successful));
            finish();
        }
    }
}
