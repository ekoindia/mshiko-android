package in.co.eko.fundu.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import in.co.eko.fundu.R;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.utils.FunduAnalytics;

public class PermissionsActivity extends AppCompatActivity
        implements View.OnClickListener {

    public static final int MULTIPLE_PERMISSIONS = 10;
    private static final int REQUEST_PERMISSION_SETTING = 11;
    public static String[] mPermissions = {
            "android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.RECEIVE_BOOT_COMPLETED",
            "android.permission.WRITE_EXTERNAL_STORAGE",
           // "android.permission.CAMERA",
            "android.permission.READ_PHONE_STATE",
            "android.permission.RECEIVE_SMS",
            "android.permission.READ_SMS",
            "android.permission.SEND_SMS",
            "android.permission.READ_CONTACTS",
            "android.permission.CALL_PHONE",
            "android.permission.GET_ACCOUNTS",
            "android.permission.USE_CREDENTIALS"
            };
    boolean allgranted;
    List<String> listPermissionsNeeded = new ArrayList<>();
    private TextView mPermissionText;
    private Button mAllowAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);
        Bundle extras = getIntent().getExtras();
//        FLog.d("contact1","*********"+ FunduUser.getUser().getName());
        initViews();
        FunduAnalytics.getInstance(this).sendScreenName("Permissions");
    }


    private void initViews() {
        findViewById(R.id.btn_allow_access).setOnClickListener(this);
        mPermissionText = (TextView)findViewById(R.id.txt_permission);
        mAllowAccess = (Button)findViewById(R.id.btn_allow_access);
        showPermissionsNotGranted();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_allow_access:
                mAllowAccess.setEnabled(false);
                checkPermissions();
                break;

        }
    }

    private void setPermissionVisibility(String p,int visibility){
        if(p.equalsIgnoreCase("android.permission.CALL_PHONE")){
            findViewById(R.id.call_permission).setVisibility(visibility);
        }
        else if(p.equalsIgnoreCase("android.permission.READ_CONTACTS")){
            findViewById(R.id.contacts_permission).setVisibility(visibility);
        }
        else if(p.contains("LOCATION")){
            findViewById(R.id.location_permission).setVisibility(visibility);
        }
        else if(p.contains("SMS")){
            findViewById(R.id.sms_permission).setVisibility(visibility);
        }
        else if(p.contains("WRITE_EXTERNAL_STORAGE")){
            findViewById(R.id.storage_permission).setVisibility(visibility);
        }
    }

    private void showPermissionsNotGranted(){
        mAllowAccess.setEnabled(true);
        listPermissionsNeeded.clear();
        for (String p : mPermissions) {
            int result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
                setPermissionVisibility(p,View.VISIBLE);
            }
            else{
                listPermissionsNeeded.remove(p);
                setPermissionVisibility(p,View.GONE);
            }

        }
        if(listPermissionsNeeded.size() > 1){
            if(listPermissionsNeeded.size() == 2 && listPermissionsNeeded.contains("android.permission.ACCESS_COARSE_LOCATION")){
                mPermissionText.setText(getString(R.string.permission_text));
            }
            else
                mPermissionText.setText(getString(R.string.permissions_text));
        }
        else{
            mPermissionText.setText(getString(R.string.permission_text));
        }
    }


    private boolean checkPermissions() {

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions, @NonNull int[] grantResults) {
        allgranted = true;
        for (int i = 0; i < grantResults.length; i++) {

            if(grantResults[i]==PackageManager.PERMISSION_DENIED)
                allgranted = false;
        }
        if(allgranted)
            proceedAfterPermission();
        else{
            Toast.makeText(this,getString(R.string.please_give_all_permissions),Toast.LENGTH_SHORT).show();
            showPermissionsNotGranted();
        }

    }
    private void proceedAfterPermission() {
        FunduAnalytics.getInstance(this).sendAction("Registration","PermissionsGranted");
        if(FunduUser.getUser() != null && FunduUser.isUserMobileVerified()){
            Intent intent = new Intent(this,HomeActivity.class);
            intent.setAction(Constants.DONT_CHECK_PERMISSION_ACTION);
            startActivity(intent);
            finish();
        }
        else{

            Intent intent = new Intent(this,UserOnboardingActivity.class);
            startActivity(intent);
            finish();
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
    }
}
