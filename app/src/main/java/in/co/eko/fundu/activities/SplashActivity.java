package in.co.eko.fundu.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import in.co.eko.fundu.R;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.utils.AppPreferences;
import in.co.eko.fundu.utils.Utils;

import static in.co.eko.fundu.constants.Constants.SPLASH_TIME;

public class SplashActivity extends BaseActivity implements Runnable {
    private Handler handler;
    private AppPreferences appPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //splashText.setTypeface(TypefaceManager.getInstance(this).getOpenSansRegular());
        String cId = Utils.getCountryID();
        if(!cId.equalsIgnoreCase("IN")){
            //findViewById(R.id.kenlogo).setVisibility(View.VISIBLE);
        }
        handler = new Handler();
        handler.postDelayed(this, SPLASH_TIME);

        //Utils.printKeyHash(this);
        appPreferences = new AppPreferences(this);




    }




    @Override
    public void run() {
        if (appPreferences.getBoolean( Constants.PrefKey.IS_LOGIN_REG, false)) {

            startActivity(new Intent (SplashActivity.this, HomeActivity.class));
        }
        else{
            startActivity(new Intent(SplashActivity.this, Tutorial.class));
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(handler != null)
             handler.removeCallbacks(this);
    }

}
