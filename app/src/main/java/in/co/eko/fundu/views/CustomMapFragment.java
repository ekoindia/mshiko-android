package in.co.eko.fundu.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

/**
 * Created by divyanshu.jain on 8/8/2016.
 */
public class CustomMapFragment extends SupportMapFragment {
    public View mOriginalContentView;
    public CustomGestureDetector mTouchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        mOriginalContentView = super.onCreateView(inflater, parent, savedInstanceState);
        mTouchView = new CustomGestureDetector(getActivity());
        mTouchView.addView(mOriginalContentView);
        return mTouchView;
    }

    @Override
    public View getView() {
        return mOriginalContentView;
    }

    public void InitializeMap(GoogleMap googleMap) {
        mTouchView.init(googleMap);
    }
}
