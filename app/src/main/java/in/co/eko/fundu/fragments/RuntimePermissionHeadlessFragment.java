package in.co.eko.fundu.fragments;


import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import in.co.eko.fundu.utils.Fog;
import in.co.eko.fundu.utils.PermissionUtil;


/**
 * Created by divyanshu.jain on 9/5/2016.
 */
public class RuntimePermissionHeadlessFragment extends Fragment {
    private static final int READ_CALL_LOG_PERMISSIONS = 10;
    public static final String TAG = "READ_CALL_LOG";

    private static CallLogPermissionCallback mCallback;
    private static boolean isCallLogPermissionDenied;

    public static RuntimePermissionHeadlessFragment newInstance(CallLogPermissionCallback callLogPermissionCallback) {
        mCallback = callLogPermissionCallback;
        return new RuntimePermissionHeadlessFragment();
    }

    public RuntimePermissionHeadlessFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setRetainInstance(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
            checkCallLogPermissions();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    public void checkCallLogPermissions() {
        if (PermissionUtil.hasSelfPermission(getActivity(), new String[]{Manifest.permission.READ_CALL_LOG})) {
            mCallback.onCallLogPermissionGranted();
        } else {
            // UNCOMMENT TO SUPPORT ANDROID M RUNTIME PERMISSIONS
            if (!isCallLogPermissionDenied) {
                requestPermissions(new String[]{Manifest.permission.READ_CALL_LOG}, READ_CALL_LOG_PERMISSIONS);
            }
        }
    }

    public void setCameraMicPermissionDenied(boolean cameraMicPermissionDenied) {
        isCallLogPermissionDenied = cameraMicPermissionDenied;
    }

    public static boolean isCameraMicPermissionDenied() {
        return isCallLogPermissionDenied;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {

        if (requestCode == READ_CALL_LOG_PERMISSIONS) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                mCallback.onCallLogPermissionGranted();
            } else {
                Fog.i("BaseActivity", "LOCATION permission was NOT granted.");
                mCallback.onCallLogPermissionDenied();
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    public interface CallLogPermissionCallback {
        void onCallLogPermissionGranted();

        void onCallLogPermissionDenied();
    }

}
