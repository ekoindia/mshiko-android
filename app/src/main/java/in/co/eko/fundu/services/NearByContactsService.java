package in.co.eko.fundu.services;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import in.co.eko.fundu.models.Contact;
import in.co.eko.fundu.models.ContactsNearByModel;
import in.co.eko.fundu.parser.UniversalParser;
import in.co.eko.fundu.requests.NearByContactsRequest;
import in.co.eko.fundu.requests.UpdateLocationRequest;
import in.co.eko.fundu.utils.ContactsUtils;
import in.co.eko.fundu.utils.Fog;
import in.co.eko.fundu.utils.GPSTracker;
import in.co.eko.fundu.utils.Utils;

public class NearByContactsService extends Service implements NearByContactsRequest.OnNearByContactsResults {


    public static ArrayList<ContactsNearByModel> contactsNearByModels = new ArrayList<>();
    ScheduledExecutorService scheduler;
    public static NearByContactsService nearByContactsService = new NearByContactsService();
    private static Handler mHandler = null;
    private GPSTracker gpsTracker = null;

    public NearByContactsService() {
    }

    public static NearByContactsService getInstance() {
        return nearByContactsService;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ContactsUtils.getInstance(getBaseContext()).syncLocalDB(getBaseContext());
        gpsTracker = new GPSTracker(getBaseContext());
        CallNearByApi();
        try {
            updateCurrentLocation();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate
                (new Runnable() {
                    public void run() {
                        CallNearByApi();
                        try {
                            updateCurrentLocation();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, 1, 1, TimeUnit.MINUTES);

        return START_STICKY;
    }

    private void updateCurrentLocation() throws JSONException {
        Location location = gpsTracker.getLocation();
        if (location != null) {
            double coordinates[] = {location.getLongitude(), location.getLatitude()};
           // double coordinates[] = {location.getLatitude(), location.getLongitude()};
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            JSONObject locationObject = new JSONObject(gson.toJson(new Contact.Location(coordinates)));
            Fog.e("JSON Location", locationObject.toString());

            UpdateLocationRequest request = new UpdateLocationRequest(this);
            request.setLocation(locationObject);
            request.start();
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    private void CallNearByApi() {
        if (Utils.isNetworkAvailable(getBaseContext())) {
            NearByContactsRequest nearByContactsRequest = new NearByContactsRequest(getBaseContext());
            nearByContactsRequest.setParserCallback(this);
            nearByContactsRequest.start();
        }
    }

    @Override
    public void onNearByContactsResponse(JSONObject response) {
        try {
            contactsNearByModels = UniversalParser.getInstance().parseJsonArrayWithJsonObject(response.getJSONArray("customer_neighbor_response_list"), ContactsNearByModel.class);
            Fog.d("nearbyresponse","nearbyresponse"+response);
            if (mHandler != null)
                mHandler.sendEmptyMessage(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNearByContactsError(VolleyError error) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        scheduler.shutdownNow();
    }

    public void setHandler(Handler handler) {
        mHandler = handler;
        if (mHandler != null)
            mHandler.sendEmptyMessage(0);
    }

}
