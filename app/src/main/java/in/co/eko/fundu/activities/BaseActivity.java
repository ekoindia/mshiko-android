package in.co.eko.fundu.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import in.co.eko.fundu.R;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.utils.AppPreferences;
import in.co.eko.fundu.utils.Fog;
import in.co.eko.fundu.utils.TypefaceManager;

public class BaseActivity extends AppCompatActivity {


    private static final String TAG = "BaseActivity";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public AppPreferences pref;
    protected TypefaceManager typeface;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FunduUser.initialize ( this );
        pref       = FunduUser.getAppPreferences();
        typeface   = TypefaceManager.getInstance(this);

    }

    public AppPreferences getPref() {
        return pref;
    }

    protected boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Fog.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }


    /**
     * @param fragment
     */
    protected void addFragment(Fragment fragment,boolean add) {
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.fragment_enter, R.anim.fragment_exit, R.anim.fragment_pop_enter, R.anim.fragment_pop_exit);

            fragmentTransaction.replace(R.id.fragmentContainer, fragment);

            if(add){
                fragmentTransaction.addToBackStack(null);
                Fog.d("add","add");
            }
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.commit();
        }
    }

    protected void addFragment(Fragment fragment, String tag) {
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, fragment, tag);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.commit();
        }
    }
}