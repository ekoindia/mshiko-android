package in.co.eko.fundu.utils;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.Settings;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;

import in.co.eko.fundu.R;
import in.co.eko.fundu.activities.Tutorial;
import in.co.eko.fundu.database.tables.UserAllContactsTable;
import in.co.eko.fundu.database.tables.UserContactsTable;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.requests.UpdateSingoutRequest;
import in.co.eko.fundu.services.NearByContactsService;

/**
 * Created by pallavi on 8/12/17.
 */

public class SignoutHelper {

     ProgressDialog dialog;


    private static final SignoutHelper ourInstance = new SignoutHelper();

    public static SignoutHelper getInstance() {
        return ourInstance;
    }

    private SignoutHelper() {
    }

    
    public void callSingoutService(final Context context){


        if(Utils.isNetworkAvailable(context)){
            dialog = new ProgressDialog(context);
            dialog.setCancelable(false);
            dialog.setMessage(context.getString(R.string.sign_out));
            dialog.show();
            UpdateSingoutRequest updateSingoutRequest = new UpdateSingoutRequest(context);
            updateSingoutRequest.setData(Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
            Fog.d("DeviceId","DeviceId"+ Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
            signout(context);

            updateSingoutRequest.start();
        }
        else{
            Toast.makeText(context, "Internet not Connected ,please try in sometime.", Toast.LENGTH_SHORT).show();
        }


    }
    
    public void signout(final Context context) {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        dispatcher.cancelAll();
        try{
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancelAll();
        }catch(Exception e){
            e.printStackTrace();
            Crashlytics.logException(e);
        }
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }
            @Override
            protected Boolean doInBackground(Void... params) {
                FirebaseInstanceId instanceID = FirebaseInstanceId.getInstance();
                try {
                    instanceID.deleteToken(context.getString(R.string.gcm_defaultSenderId), FirebaseMessaging.INSTANCE_ID_SCOPE);
                    FunduUser.signOut();
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (dialog!=null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (aBoolean) {
                    Toast.makeText(context, "Sign out successfully.", Toast.LENGTH_SHORT).show();
                    context.stopService(new Intent(context, NearByContactsService.class));
                    UserContactsTable.deleteAllContact(context);
                    UserAllContactsTable.deleteAllContact(context);
                    GreenDaoHelper.getInstance(context).clearDb();
                    context.startActivity(new Intent(context, Tutorial.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    Activity activity = (Activity)context;
                    if (activity!=null) {
                        activity.finish();
                    }

                } else {
                    Toast.makeText(context, "Sign out unsuccessful. Please check your internet connection and try again.", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();

    }







}
