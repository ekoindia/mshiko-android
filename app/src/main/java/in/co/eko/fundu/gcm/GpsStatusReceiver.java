package in.co.eko.fundu.gcm;/*
 * Created by Bhuvnesh
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import in.co.eko.fundu.utils.Fog;

// For Future implementation
public class GpsStatusReceiver extends BroadcastReceiver {
    private static final String TAG = GpsStatusReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
            /*Toast.makeText(context, "in android.location.PROVIDERS_CHANGED",
                    Toast.LENGTH_SHORT).show();
            Intent pushIntent = new Intent(context, LocalService.class);
            context.startService(pushIntent);*/
            Fog.d(TAG, "android.location.PROVIDERS_CHANGED");
        }
    }
}
