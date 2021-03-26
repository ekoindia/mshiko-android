package in.co.eko.fundu.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import in.co.eko.fundu.R;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.utils.FunduAnalytics;
import in.co.eko.fundu.utils.Utils;

public class TransactionStatusActivity extends AppCompatActivity {

    Button button;
    TextView dismiss, title, cancelDesc;
    private ImageView icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_cancelled);
        button = (Button)findViewById(R.id.button);
        dismiss = (TextView)findViewById(R.id.dismiss);
        title = (TextView)findViewById(R.id.title_tv);
        cancelDesc = (TextView)findViewById(R.id.cancel_desc);
        icon = (ImageView)findViewById(R.id.icon);

        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(TransactionStatusActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        FunduAnalytics.getInstance(TransactionStatusActivity.this).sendScreenName("TransactionStatusScreen");
        try{

            if(getIntent().getAction() != null && getIntent().getAction().contains("cancel")){

            }
            else if(getIntent().getAction() != null && getIntent().getAction().contains("qrcode_status")){
                Bundle bundle = getIntent().getExtras();
                String statusCode = bundle.getString("status");
                if(statusCode.equalsIgnoreCase("S")){
                    title.setText(getString(R.string.transaction_successful));
                    icon.setImageResource(R.drawable.ic_done);
                    String orderNo = bundle.getString("orderNo");
                    String txnAmount = bundle.getString("txnAmount");
                    String tranAuthdate = bundle.getString("tranAuthdate");
                    DateFormat format = new SimpleDateFormat("yyyy:MM:dd hh:mm:ss");

                    Date date = format.parse(tranAuthdate);
                    format = new SimpleDateFormat("EEE, dd MMMM yyyy | h:mm a");
                    tranAuthdate = format.format(date);
                    View successDesc = findViewById(R.id.success_desc);
                    successDesc.setVisibility(View.VISIBLE);
                    TextView txnId = (TextView) successDesc.findViewById(R.id.txid);
                    TextView amount = (TextView) successDesc.findViewById(R.id.amount);
                    TextView dateTv = (TextView) successDesc.findViewById(R.id.txdate);
                    txnId.setText("Txn ID: "+orderNo);
                    amount.setText(txnAmount);
                    dateTv.setText(tranAuthdate);
                }
                else if(statusCode.equalsIgnoreCase("T")){
                    icon.setImageResource(R.drawable.ic_processing);
                    title.setText(R.string.timedout);
                    cancelDesc.setText(R.string.qrcode_t_o);
                    cancelDesc.setVisibility(View.VISIBLE);
                }
                else if(statusCode.equalsIgnoreCase("MT08")){
                    icon.setImageResource(R.drawable.ic_red_cross);
                    title.setText(getString(R.string.payment_unsuccessful));
                    cancelDesc.setText(getString(R.string.i_pin));
                    cancelDesc.setVisibility(View.VISIBLE);
                    button.setVisibility(View.VISIBLE);
                    button.setText(R.string.retry);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FunduAnalytics.getInstance(TransactionStatusActivity.this).sendAction("TransactionStatus","RetryQRCode");
                            Intent intent = new Intent();
                            intent.setAction("retry");
                            setResult(Activity.RESULT_OK,intent);
                            finish();
                        }
                    });

                }
                else {
                    icon.setImageResource(R.drawable.ic_red_cross);
                    title.setText(getString(R.string.payment_unsuccessful));
                }

            }

            else if(getIntent().getAction() != null && getIntent().getAction().contains("credit_failed")){
                icon.setImageResource(R.drawable.ic_red_cross);
                title.setText(getString(R.string.payment_unsuccessful));
                cancelDesc.setText(getString(R.string.c_u));
                cancelDesc.setVisibility(View.VISIBLE);
                button.setVisibility(View.VISIBLE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FunduAnalytics.getInstance(TransactionStatusActivity.this).sendAction("TransactionStatus","ContactUs");
                        Intent intent = new Intent(TransactionStatusActivity.this, Feedback.class);
                        intent.setAction("help");
                        intent.putExtra(Constants.PushNotificationKeys.TID,getIntent().getStringExtra("orderNo"));
                        startActivity(intent);
                        finish();
                    }
                });
            }
            else if(getIntent().getAction() != null && getIntent().getAction().contains("credit_timeout")){
                icon.setImageResource(R.drawable.ic_processing);
                title.setText(getString(R.string.payment_pending));
                cancelDesc.setText(getString(R.string.c_u));
                cancelDesc.setVisibility(View.VISIBLE);
                button.setVisibility(View.VISIBLE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FunduAnalytics.getInstance(TransactionStatusActivity.this).sendAction("TransactionStatus","ContactUs");
                        Intent intent = new Intent(TransactionStatusActivity.this, Feedback.class);
                        intent.setAction("help");
                        intent.putExtra(Constants.PushNotificationKeys.TID,getIntent().getStringExtra("orderNo"));
                        startActivity(intent);
                    }
                });
            }
            else if(getIntent().getAction() != null && getIntent().getAction().contains("failed")){
                icon.setImageResource(R.drawable.ic_red_cross);
                title.setText(getString(R.string.payment_unsuccessful));
                cancelDesc.setText(getString(R.string.transaction_failed_desc));
                cancelDesc.setVisibility(View.VISIBLE);
            }

            else if(getIntent ().getAction () != null && getIntent ().getAction ().contains ( "code_limit_exhausted" )){
                title.setText ( R.string. transactionCancelled);
                cancelDesc.setText ( R.string.transaction_code_limit_exhausted_desc );
                cancelDesc.setVisibility(View.VISIBLE);
            }


        }catch (Exception e){
            e.printStackTrace();
        }
        findViewById(R.id.feedback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = getWindow().getDecorView().getRootView();
                v.setDrawingCacheEnabled(true);
                Bitmap bmp = Bitmap.createBitmap(v.getDrawingCache());
                v.setDrawingCacheEnabled(false);
                Utils.takeFeedback(bmp,TransactionStatusActivity.this);
            }
        });

    }


}
