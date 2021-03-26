package in.co.eko.fundu.gcm;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import in.co.eko.fundu.models.Contact;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.requests.LocationPulse;
import in.co.eko.fundu.requests.UpdateLocationRequest;
import in.co.eko.fundu.utils.Fog;

/**
 * Created by zartha on 3/13/18.
 */

public class FunduJobService extends JobService {

    private String TAG = this.getClass().getName();

    public static final String SEND_LOCATION = "send_location";


    @Override
    public boolean onStartJob(JobParameters job) {
        Fog.wtf(TAG,"onStartJob");
        String action = job.getTag();
        switch(action){
            case SEND_LOCATION:
                updateLocation();
                break;
        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return true;
    }

    private void updateLocation() {
        int TWO_MINUTES = 1000 * 60 * 2;
        int MINIMUM_DISTANCE = 50;//in meters

        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location finalLocation=null,gpsLocation=null,networkLocation=null;
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            if(gpsLocation == null && networkLocation == null){
                return;
            }
            else if (gpsLocation != null && networkLocation != null) {


                long timeDelta = networkLocation.getTime() - gpsLocation.getTime();
                boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
                boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
                boolean isNewer = timeDelta > 0;

                // If it's been more than two minutes since the current location, use the new location
                // because the user has likely moved
                if (isSignificantlyNewer) {
                    finalLocation = networkLocation;
                    // If the new location is more than two minutes older, it must be worse
                } else if (isSignificantlyOlder) {
                    finalLocation = gpsLocation;
                }
                else{
                    // Check whether the new location fix is more or less accurate
                    int accuracyDelta = (int) (networkLocation.getAccuracy() - gpsLocation.getAccuracy());
                    boolean isLessAccurate = accuracyDelta > 0;
                    boolean isMoreAccurate = accuracyDelta < 0;
                    boolean isSignificantlyLessAccurate = accuracyDelta > 200;


                    // Determine location quality using a combination of timeliness and accuracy
                    if (isMoreAccurate) {
                        finalLocation = networkLocation;
                    } else if (isNewer && !isLessAccurate && !isSignificantlyLessAccurate) {
                        finalLocation = networkLocation;
                    }
                    else
                        finalLocation = gpsLocation;

                }
            } else {

                if (gpsLocation != null) {
                    finalLocation = gpsLocation;
                } else if (networkLocation != null) {
                    finalLocation = networkLocation;
                }
            }
            /**
             *  Compare user's last location with this location(finallocation).
             *  No significant change - Send pulse
             *  Significant change - Update location
             */
            try{

                FunduUser.initialize(getApplicationContext());
                float distance = finalLocation.distanceTo(FunduUser.getLocation());
                //if distance is greater than minimum
                if(distance > MINIMUM_DISTANCE)
                {
                    FunduUser.setLocation(finalLocation);
                    double coordinates[] = {finalLocation.getLongitude(), finalLocation.getLatitude()};
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();
                    JSONObject locationObject = new JSONObject(gson.toJson(new Contact.Location(coordinates)));
                    UpdateLocationRequest request = new UpdateLocationRequest(getApplicationContext());
                    request.setLocation(locationObject);
                    request.start();
                }
                else
                {
                    LocationPulse request = new LocationPulse();
                    request.start();
                }
            }catch(Exception e) {
                e.printStackTrace();
                Fog.logException(e);
            }
        }

    }
}
