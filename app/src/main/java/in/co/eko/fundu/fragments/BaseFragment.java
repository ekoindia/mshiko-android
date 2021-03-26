package in.co.eko.fundu.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import in.co.eko.fundu.R;
import in.co.eko.fundu.interfaces.NeedCash;
import in.co.eko.fundu.interfaces.OnFragmentInteractionListener;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.utils.AppPreferences;
import in.co.eko.fundu.utils.TypefaceManager;

/*
 * Created by Bhuvnesh
 */
public abstract class BaseFragment extends Fragment {
    protected OnFragmentInteractionListener mListener;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    protected NeedCash needCash;
    public final static String FRAGMENT_NAME = "fragment_name";
    protected TypefaceManager typeface;
    protected AppPreferences pref;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must implement OnFragmentInteractionListener");
        }
        try {
            needCash = (NeedCash) activity;
        } catch (ClassCastException e) {
           // throw new ClassCastException(activity.toString() + "must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref= FunduUser.getAppPreferences();
        typeface = TypefaceManager.getInstance(getActivity());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        needCash = null;
    }

    public void onButtonPressed(Bundle bundle) {
        if (mListener != null) {
            mListener.onFragmentInteraction(bundle);
        }
    }
    protected boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(getActivity());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(),
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getActivity().getApplicationContext(),
                        getString(R.string.device_not_supported), Toast.LENGTH_LONG)
                        .show();
                // finish();
            }
            return false;
        }
        return true;
    }


    public abstract boolean  onBackPressed();

}
