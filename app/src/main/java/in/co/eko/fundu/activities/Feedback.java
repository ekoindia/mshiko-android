package in.co.eko.fundu.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;

import in.co.eko.fundu.R;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.govorit.request.MultipartRequest;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.requests.ReportIssue;
import in.co.eko.fundu.requests.SendTelegramMessage;
import in.co.eko.fundu.utils.FunduAnalytics;
import in.co.eko.fundu.utils.Utils;

public class Feedback extends BaseActivity {
    private ImageView mScreenShot;
    private CheckBox mIncludeScreeshot;
    private EditText mFeedBackText;
    private View mIncludeScreenshotPl;
    private TextView limit;
    private int totalAllowed = 160;
    private  String text;
    private String action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        action = getIntent().getAction();
        init();
        FunduAnalytics.getInstance(this).sendScreenName("Feedback");

    }
    private void init(){
        mFeedBackText = (EditText) findViewById(R.id.feedback);
        if(action != null && action.contains("feedback")){
            String screenShotPath = getIntent().getStringExtra("screen_shot_feedback");
            mScreenShot = (ImageView)findViewById(R.id.screenshotcaptured);

            mIncludeScreeshot = (CheckBox)findViewById(R.id.includescreenshot);
            if(TextUtils.isEmpty(screenShotPath)){
                mScreenShot.setVisibility(View.GONE);
            }
            else{
                File file = new File(screenShotPath);
                Picasso.with(this).load(file).into(mScreenShot);
            }

            mIncludeScreeshot.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        mScreenShot.setAlpha(1f);
                    }
                    else {
                        mScreenShot.setAlpha(0.2f);
                    }
                }
            });
            findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendFeedBack();
                }
            });
        }
        else if(action != null && action.contains("help")){
            findViewById(R.id.screenshotcaptured).setVisibility(View.GONE);
            findViewById(R.id.checkboxll).setVisibility(View.GONE);

            TextView tv = (TextView) findViewById(R.id.title);
            tv.setText(R.string.string_help);

            final TextView issue = (TextView) findViewById(R.id.issuewith);
            issue.setText(R.string.issue);
            issue.setVisibility(View.VISIBLE);


            if(getIntent() != null) {
                String tid = getIntent ().getStringExtra ( Constants.PushNotificationKeys.TID );
                if(tid != null){
                    issue.setText(getString(R.string.issue_with)+" "+tid);
                } else {
                    issue.setText("Issue");
                }
            }

            TextView fromEmail = (TextView) findViewById(R.id.fromemail);
            fromEmail.setText(String.format(getString(R.string.email_support), FunduUser.getEmail()));
            fromEmail.setVisibility(View.VISIBLE);



            Button sendButton = (Button)findViewById(R.id.sendButton);

            sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = mFeedBackText.getText().toString();
                    if(text.length()==0){
                        Utils.vibratePhone(Feedback.this);
                    }
                    else{
                         reportIssue(issue.getText().toString(),text);

                    }

                }
            });
        }

        limit = (TextView) findViewById(R.id.limit);
        limit.setText("0/"+totalAllowed);
        mFeedBackText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                limit.setText(s.toString().length()+"/"+totalAllowed);
            }
        });

        findViewById(R.id.imageview_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }
    private void reportIssue(String summary,String description){
        ReportIssue reportIssue = new ReportIssue(this);
        reportIssue.setData(summary,description);
        reportIssue.start();
        String message = String.format(getString(R.string.email_support),"your email");
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
        finish();
    }
    private void sendFeedBack() {
        if(TextUtils.isEmpty(mFeedBackText.getText())) {
            Toast.makeText(this, getString(R.string.pwyf), Toast.LENGTH_SHORT).show();
            return;
        }
        text = "\n"+mFeedBackText.getText().toString();
        String name = FunduUser.getFullName();
        if(!TextUtils.isEmpty(name)){
            text =  text+"\nfrom: "+name;
        }
        if(mIncludeScreeshot.isChecked()) {
            FunduAnalytics.getInstance(this).sendAction("Feedback","Sent","WithScreen");
            final String screenShotPath  = getIntent().getStringExtra("screen_shot_feedback");
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    File file = new File(screenShotPath);
                    try {

                        String requestURL = "https://api.telegram.org/yoururl:AAH66zhyouurl/sendPhoto?chat_id="+ Constants.telegramChatId;
                        MultipartRequest multipart = new MultipartRequest(requestURL, "UTF-8");
                        multipart.addFormField("chat_id", Constants.telegramChatId);
                        multipart.addFormField("caption", text);
                        multipart.addFilePart("photo",file );
                        String response = multipart.finish();
                        JSONObject responseJ = new JSONObject(response);
                        file.delete();

                    }
                    catch(Exception e){
                        Crashlytics.logException(e);
                        e.printStackTrace();
                        file.delete();
                    }
                }
            });
            thread.start();
        }
        else{
            //send message
            FunduAnalytics.getInstance(this).sendAction("Feedback","Sent","WithOutScreen");
            SendTelegramMessage message = new SendTelegramMessage(this);
            message.setText(text);
            message.start();
        }
        Toast.makeText(this,getString(R.string.tffb),Toast.LENGTH_SHORT).show();
        finish();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(action != null && action.contains("feedback")){
            FunduAnalytics.getInstance(this).sendAction("Feedback","Cancelled");
            String screenShotPath = getIntent().getStringExtra("screen_shot_feedback");
            File file = new File(screenShotPath);
            file.delete();
        }
        else{
            FunduAnalytics.getInstance(this).sendAction("HelpEmail","Cancelled");
        }

    }

}
