package in.co.eko.fundu.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.R;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.models.User;
import in.co.eko.fundu.requests.CheckBalanceRequest;
import in.co.eko.fundu.requests.UpdateRatingRequest;
import in.co.eko.fundu.utils.Utils;


public class TransactionSuccessActivity extends BaseActivity implements View.OnClickListener, RatingBar.OnRatingBarChangeListener, UpdateRatingRequest.OnUpdateRatingResults {

    private ArrayList<String> list;
    private String totalAmount, usertype;
    private TextView remainingAmount;
    TextView yourname, provider;
    private RatingBar ratingBar;
    private ProgressDialog dialog;
//    M6NH-R4VL-BHPF-RDKQ
//    {"message":"Successfully updated","status":"SUCCESS","data":{"autocashout":false,"id":"mobile_type:755558888",
//            "name":"Rahul Srivastav","mobile":"755558888","location":{"coordinates":[77.0716913,28.4550255],"type":"Point"},
//        "email":"rahul.srivastava54@gmail.com","contact_id_type":"mobile_type","contact_id":"755558888",
//                "country_shortname":"KEN","sim_number":"89910430021308497684","imei_number":"352356070635370",
//                "contact_type":"PERSON","device_id":"f8f19d7a4197f76b","device_type":"android",
//                "device_token":"eUtqR-W7te4:APA91bGVUw1hYp25P988_l9VYskrJnNo_Aeu8bROGbCe2m5HiY_oTJQkKxi1El9Q8CcVCKQ" +
//                "1NtJGVprI2ZuvgT5xRUYPdHXQd3chnHds_vl8z9qDrgmfeWZWxRPop_FPEkptDfzBnf7-",
//                "created_at":1485253111703,"updated_at":1485254185923,"verified":true,"deleted":false,
//                "active":true,"gsm_sender_id":"937157477368","dummy":false,"rating":5,"ratingcount":1,"custid":"EY3P-BYGJ-GUTK-HFKC"}}EY3P-BYGJ-GUTK-HFKC
    private UpdateRatingRequest ratingRequest;
    private String transactionId;
    private EditText message;
    private Spinner spinner;
    private int ratingType;
    private LinearLayout ratingLayout, fragmentInd, fragmentKen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ratingRequest = new UpdateRatingRequest(this);
        setContentView(R.layout.activity_transaction_success);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        list = intent.getStringArrayListExtra(Constants.ALERT);
        totalAmount = intent.getStringExtra(Constants.TOTAL_AMOUNT);
        transactionId = intent.getStringExtra(Constants.TRANSACTION_ID);
        ratingType = intent.getIntExtra(Constants.RATING_TYPE, -1);
        String pname = intent.getStringExtra("pname");
        String pmobile = intent.getStringExtra("pmobile");
        usertype = pmobile;
        dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);


        ratingLayout = (LinearLayout) findViewById(R.id.ratingLayout);
        remainingAmount = (TextView) findViewById(R.id.remainingAmount);
        TextView transactionMessage = (TextView) findViewById(R.id.transactionMessage);
        TextView amount = (TextView) findViewById(R.id.amount);
        TextView name = (TextView) findViewById(R.id.name);
        TextView psname = (TextView) findViewById(R.id.psname);
        TextView psnmbr = (TextView) findViewById(R.id.psnmbr);
        TextView psamount = (TextView) findViewById(R.id.psamount);
        TextView psTransactionMessage = (TextView) findViewById(R.id.pstransactionMessage);
        TextView psTid = (TextView) findViewById(R.id.psTid);
        provider = (TextView) findViewById(R.id.provider);
        yourname = (TextView) findViewById(R.id.yourname);
        Button submit = (Button) findViewById(R.id.done);
        spinner = (Spinner) findViewById(R.id.spinner);
        message = (EditText) findViewById(R.id.et_your_message);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        fragmentInd = (LinearLayout) findViewById(R.id.fragmentContainer);
        fragmentKen = (LinearLayout) findViewById(R.id.fragmentContainerken);
        if (FunduUser.getCountryShortName().equalsIgnoreCase("KEN")){
            fragmentInd.setVisibility(View.GONE);
            yourname.setText(FunduUser.getFullName());


            if (ratingType == 1) {
                provider.setText("Sent To");
                psTransactionMessage.setText("Have been deducted from your Account");
                psTid.setText(transactionId);
                psname.setText(pname);
                psnmbr.setText(pmobile);
                psamount.setText("Shs. "+String.valueOf(Double.parseDouble(totalAmount)+Double.parseDouble(FunduUser.getChargesKen())));
            }
            else if (ratingType == 2){
                provider.setText("Sent By");
                psTransactionMessage.setText("Has been credited to your Account");
                psTid.setText(list.get(2));
                psname.setText(list.get(9));
                psnmbr.setText(list.get(7));
                psamount.setText("Shs. "+String.valueOf(Double.parseDouble(list.get(1))+Double.parseDouble(list.get(10))));
            }
        }
        else{
            fragmentKen.setVisibility(View.GONE);
            if (ratingType == 1) {

                if (totalAmount != null) {
                    float totalAmountInFloat = Float.parseFloat(totalAmount);
                    remainingAmount.setText(getString(R.string.ruppee_symbol) + totalAmountInFloat);
                }

            amount.setText(getString(R.string.ruppee_symbol) + list.get(9));
                name.setText(list.get(8));
            } else if (ratingType == 2) {
                //
            transactionMessage.setText(getString(R.string.have_been_sent_from_your_wallet_2));
                if (totalAmount != null) {
                    float totalAmountInFloat = Float.parseFloat(totalAmount);
                remainingAmount.setText(getString(R.string.ruppee_symbol) + totalAmountInFloat);
                }

                amount.setText(getString(R.string.ruppee_symbol) + list.get(1));
                name.setText(list.get(list.size() - 1));
            }
        }
        ratingBar.setOnRatingBarChangeListener(this);
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
        if (totalAmount == null && FunduUser.getCountryShortName().equalsIgnoreCase("IND"))
            balanceRequest.start();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.done) {
            User user = FunduUser.getUser();
            if (user != null) {
                if (ratingBar.getRating() < 1) {
                    Utils.showShortToast(this, "Kindly fill atleast 1 rating");
                    return;
                }
                JSONObject object = null;
                try {
                    object = new JSONObject();
                    //object.put("contact_id", ratingType==2?list.get(7):list.get(1));
                    object.put("rated_by_id", FunduUser.getContactIDType() + ":" + FunduUser.getContactId());
                    object.put("transaction_id", ratingType == 2 ? list.get(2) : transactionId);
                    object.put("rating", ratingBar.getRating());
                    object.put("comments", spinner.getSelectedItem() + ":" + message.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (FunduUser.getCountryShortName().equalsIgnoreCase("KEN")){
                    ratingRequest.setData(object, ratingType == 2 ? list.get(7) : usertype/*list.get(1)*/);
                }
                else {
                    ratingRequest.setData(object, ratingType == 2 ? list.get(7) : list.get(1));
                }
                ratingRequest.setParserCallback(this);
                dialog.show();
                ratingRequest.start();
            }

        }
    }

    @Override
    public void onBackPressed() {

    }
//    {"rated_by_id":"mobile_type:777777777","transaction_id":"FJAN7000000136","rating":4.5,"comments":"Other Reason:"}
//    {"comments":"Other Reason:","transaction_id":"FJAN7000000136","rating":4.5,"rated_by_id":"mobile_type:753753753"}
    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        if (rating <= 3) {
            findViewById(R.id.ratingView).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.ratingView).setVisibility(View.GONE);

        }
    }

    @Override
    public void onUpdateRatingResponse(JSONObject response) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        Toast.makeText(this, "Rating Successfully added to this user.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Constants.USER_PROFILE_ACTIVITY_ACTION);
        try {
            intent.putExtra(Constants.AVERAGE_RATING, response.getString(Constants.AVERAGE_RATING));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        finish();
    }


    @Override
    public void onUpdateRatingError(VolleyError error) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        Toast.makeText(this, "Rating error.", Toast.LENGTH_SHORT).show();
    }
}
