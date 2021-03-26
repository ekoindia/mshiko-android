package in.co.eko.fundu.adapters;/*
 * Created by Bhuvnesh
 */

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public abstract class MapAdapter<T> {
    private Context context;
    private GoogleMap googleMap;

    public MapAdapter(Context context, GoogleMap googleMap) {
        this.context = context;
        this.googleMap = googleMap;
    }

    public Context getContext() {
        return context;
    }

    public GoogleMap getGoogleMap() {
        return googleMap;
    }

    public abstract void onMyLocationUpdate(LatLng location);
    public abstract void onMarkersUpdate(T t, LatLng myLatLng);
    public abstract  void setMyLocation(LatLng latLng);




}
