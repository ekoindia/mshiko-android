package in.co.eko.fundu.gcm;
/*
 * Created by Bhuvnesh
 */

import android.content.Intent;


import com.google.firebase.iid.FirebaseInstanceIdService;

import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.utils.Fog;

public class MyInstanceIDListenerService extends FirebaseInstanceIdService {
    private static final String TAG = "MyInstanceIDLS";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceID provider.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
        Fog.d("MyInstanceIDLS","onTokenRefresh called");
        Intent intent = new Intent(this, RegistrationIntentService.class);
        intent.putExtra(Constants.TOKEN_REFRESH, true);
        startService(intent);
    }
    // [END refresh_token]
}
