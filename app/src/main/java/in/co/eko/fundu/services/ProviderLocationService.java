package in.co.eko.fundu.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.android.volley.VolleyError;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.event.LocationEvent;
import in.co.eko.fundu.requests.GetReceiverRequest;
import in.co.eko.fundu.utils.Fog;

/**
 * Created by pallavi on 14/12/17.
 */

public class ProviderLocationService extends Service implements GetReceiverRequest.OnUserProfileRequestResult{
    private Handler handler;
    String mobileNum="";
    GetReceiverRequest getReceiverRequest;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent == null || intent.getStringExtra(Constants.PROVIDER_NUMBER) == null){
            stopSelf ();
        }
        else{
            mobileNum = intent.getStringExtra(Constants.PROVIDER_NUMBER);
            Fog.d("mobileNum",""+mobileNum);
            handler = new Handler();
            handler.postDelayed(runnable,100);
        }

        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(handler != null)
            handler.removeCallbacks(runnable);
        stopSelf();
    }


    final Runnable runnable = new Runnable() {
        public void run() {
            //Toast.makeText(ProviderLocationService.this, "receiver service start in runnable", Toast.LENGTH_SHORT).show();

            getReceiverRequest = new GetReceiverRequest(ProviderLocationService.this);
            getReceiverRequest.setData(mobileNum);
            getReceiverRequest.setParserCallback(ProviderLocationService.this);
            getReceiverRequest.start();
            handler.postDelayed(this, 7*1000);
        }
    };

    @Override
    public void onUserProfileResponse(JSONObject response) {

        double lat=0.0,lng=0.0;
        try{
            JSONObject jsonObject=new JSONObject(response.toString());
            JSONArray jsonArray=jsonObject.optJSONArray("coordinates");
            lat = jsonArray.optDouble(1);
            lng = jsonArray.optDouble(0);
            LocationEvent locationEvent = new LocationEvent(mobileNum,lat,lng);
            EventBus.getDefault().post(locationEvent);
        }catch (Exception e){

        }

    }

    @Override
    public void onUserProfileError(VolleyError error) {

    }
}