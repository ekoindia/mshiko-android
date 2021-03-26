package in.co.eko.fundu.gcm;/*
 * Created by Bhuvnesh
 */

import android.app.IntentService;
import android.content.Intent;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;

import in.co.eko.fundu.FunduApplication;
import in.co.eko.fundu.R;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.requests.UpdateCustomer;
import in.co.eko.fundu.utils.AppPreferences;
import in.co.eko.fundu.utils.Fog;

//import static in.co.eko.fundu.FunduApplication.context;

public class RegistrationIntentService extends IntentService {
    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};
    private AppPreferences preferences;
    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences = new AppPreferences(this);
        try {

            FirebaseInstanceId instanceID = FirebaseInstanceId.getInstance();
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    FirebaseMessaging.INSTANCE_ID_SCOPE);

            String oldToken = preferences.getString(Constants.GCM_TOKEN);
            preferences.putString(Constants.GCM_TOKEN, token);
            sendRegistrationToServer(token, oldToken);
            // Subscribe to topic channels
            //subscribeTopics(token);


        } catch (Exception e) {
            Fog.d(TAG, "Failed to complete token refresh", e);
            preferences.putBoolean(Constants.SENT_TOKEN_TO_SERVER, false);
        }

    }

    /**
     * Persist registration to third-party servers.
     *
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token, String oldToken) {

            if (preferences.getString(Constants.PrefKey.CONTACT_NUMBER).isEmpty()) {
                return;
            }
        if(oldToken != null && !oldToken.equalsIgnoreCase(token)){

            UpdateCustomer request = new UpdateCustomer(FunduApplication.getAppContext());
            request.setData(new String[]{"device_token","gsm_sender_id"},new String[]{token,FunduApplication.getAppContext().getString(R.string.gcm_defaultSenderId)});
            request.start();
        }

    }

    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    private void subscribeTopics(String token) throws IOException {
//        GcmPubSub pubSub = GcmPubSub.getInstance(this);
//        for (String topic : TOPICS) {
//            pubSub.subscribe(token, "/topics/" + topic, null);
//        }
    }
    // [END subscribe_topics]

}

