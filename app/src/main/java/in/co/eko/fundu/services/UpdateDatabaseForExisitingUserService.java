package in.co.eko.fundu.services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;

import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.database.tables.UserAllContactsTable;
import in.co.eko.fundu.database.tables.UserContactsTable;
import in.co.eko.fundu.utils.Fog;

/**
 * Created by ankit on 16-08-2017.
 */

public class UpdateDatabaseForExisitingUserService extends Service {

    Intent intent;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent();
        intent.setAction(Constants.EXISTINGUSERDB);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


//        UpdateExistingUserDatabase updateExistingUserDatabase = new UpdateExistingUserDatabase();
//        updateExistingUserDatabase.execute(name);
        return super.onStartCommand(intent, flags, startId);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public class UpdateExistingUserDatabase extends AsyncTask<String,Void,Void>{


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            String name = params[0];
            if(name.equalsIgnoreCase(Constants.USERCONTACTTABLE)){
                UserContactsTable.addCountryCodeData();
            }
            else {
                UserAllContactsTable.addCountryCodeData();
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Fog.d("onPostExecute","onPostExecute");
            sendBroadcast(intent);
        }

    }






}
