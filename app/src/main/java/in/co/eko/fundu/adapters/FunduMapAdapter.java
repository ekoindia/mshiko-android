package in.co.eko.fundu.adapters;
/*
 * Created by Bhuvnesh
 */

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import in.co.eko.fundu.R;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.constants.V1API;
import in.co.eko.fundu.models.Contact;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.models.Neighbour;
import in.co.eko.fundu.utils.AppPreferences;
import in.co.eko.fundu.utils.Fog;

public class FunduMapAdapter extends MapAdapter<ArrayList<Neighbour>> implements GoogleMap.OnMarkerClickListener, GoogleMap.InfoWindowAdapter {
    private String TAG = FunduMapAdapter.class.getName();

    private MarkerOptions myLocationMarkerOption = new MarkerOptions();
    private Marker myMarker;
    private AppPreferences pref;
    private Context mContext;


    public FunduMapAdapter(Context context, GoogleMap googleMap) {
        super(context, googleMap);
        init(context,googleMap,null);
    }

    public FunduMapAdapter(Context context, GoogleMap googleMap, Location mLastLocation){
        super(context,googleMap);
        init(context,googleMap,mLastLocation);
    }
    private void init(Context context,GoogleMap googleMap,Location mLastLocation){
        this.mContext = context;
        pref = FunduUser.getAppPreferences();
        googleMap.setOnMarkerClickListener(this);
        googleMap.setInfoWindowAdapter(this);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        myLocationMarkerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_current_location));
        myLocationMarkerOption.title("You");
        myLocationMarkerOption.flat(true);
        myLocationMarkerOption.draggable(true);
        if(mLastLocation != null){
            myLocationMarkerOption.position(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()));
        }
    }

    @Override
    public void onMyLocationUpdate(LatLng myLatLng) {
        Fog.i(TAG,"onMyLocationUpdate "+myLatLng.latitude+"-"+myLatLng.longitude);
        if(myLocationMarkerOption != null){
            myLocationMarkerOption.position(myLatLng);
        }
        if (myMarker == null) {
            myMarker = getGoogleMap().addMarker(myLocationMarkerOption);
        } else {
            myMarker.setPosition(myLatLng);
        }

    }
    public String makeURL(String destlat, String destlog) {
        Fog.i(TAG,"makeURL "+myMarker.getPosition().latitude+"-"+myMarker.getPosition().longitude);
        return "https://maps.googleapis.com/maps/api/directions/json" + "?origin=" + String.valueOf(myMarker.getPosition().latitude) + "," + String.valueOf(myMarker.getPosition().longitude) + "&destination=" + destlat + "," + destlog + "&sensor=false&mode=walking&alternatives=true&key=" +mContext.getResources().getString(R.string.google_map_key_for_server);
    }

    @Override
    public void onMarkersUpdate(ArrayList<Neighbour> contacts, LatLng myLatLng) {
        getGoogleMap().clear();
        myMarker = getGoogleMap().addMarker(myLocationMarkerOption);
        if (contacts != null && contacts.size() > 0 && myMarker != null) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (int i = 0; i < contacts.size(); i++) {
                MarkerOptions markerOptions = new MarkerOptions();
                Marker marker = null;
                String name = contacts.get(i).getName();
                String profile_pic = contacts.get(i).getMerchant_img_url();
                if (contacts.get(i).getContactType().equalsIgnoreCase("PERSON")) {
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.fundu_user_pin));
                }
                else if (contacts.get(i).getContactType().equalsIgnoreCase("MERCHANT")){
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.cash_point_pin));
                }
                else if (contacts.get(i).getContactType().equalsIgnoreCase("AGENT")) {
                    if (pref.getString(Constants.COUNTRY_SHORTCODE) == null) {
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_merchant));
                    } else if (pref.getString(Constants.COUNTRY_SHORTCODE).equalsIgnoreCase("KEN")) {
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_merchant_ken));
                    } else
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.fundu_user_pin));
                    if (contacts.get(i).getBusiness_name() != null)
                        name = contacts.get(i).getBusiness_name();
                    if (profile_pic != null) {
                        profile_pic = V1API.BASE_URL + "/v2/customers/getMerchantImage/" + contacts.get(i).getContactId();
                    } else
                        profile_pic = "No Image";
                    if (contacts.get(i).getBusiness_name() != null)
                        markerOptions.snippet(contacts.get(i).getContactId() + ";" + contacts.get(i).getPhysical_location() + ";" + profile_pic);
                    else
                        markerOptions.snippet("");
                } else {
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.atm_pin));
                }
                double lat = contacts.get(i).getLocation().coordinates[1];
                double lng = contacts.get(i).getLocation().coordinates[0];

                LatLng latLng = new LatLng(lat, lng);
                builder.include(latLng);
                markerOptions.position(latLng);
                if (FunduUser.isUserLogin()) {
                    if (contacts.get(i).getContactType().equalsIgnoreCase("PERSON")
                            || contacts.get(i).getContactType().equalsIgnoreCase("AGENT")) {
                        if (contacts.get(i).getContactId() != null) {
                            if (contacts.get(i).getContactId().equals(FunduUser.getContactId())) {
                            /*myLocationMarkerOption.position(myLatLng);
                            myMarker = getGoogleMap().addMarker(myLocationMarkerOption);*/
                                Fog.d("FunduMapAdapter", contacts.get(i).getRating() == null ? "You" : ("You;" + contacts.get(i).getRating()));
                                myMarker.setTitle(contacts.get(i).getRating() == null ? "You" : ("You;" + contacts.get(i).getRating()));

                                if (myLatLng != null)
                                    myMarker.setPosition(myLatLng);

                            } else {
                                markerOptions.title(contacts.get(i).getRating() == null ? name : (name + ";" + contacts.get(i).getRating()));
                                marker = getGoogleMap().addMarker(markerOptions);
                            }
                        } else {
                            markerOptions.title(contacts.get(i).getRating() == null ? name : (name + ";" + contacts.get(i).getRating()));
                            marker = getGoogleMap().addMarker(markerOptions);
                        }

                    } else {
                        markerOptions.title(contacts.get(i).getRating() == null ? name : (name + ";" + contacts.get(i).getRating()));
                        marker =  getGoogleMap().addMarker(markerOptions);
                    }
                } else {
                    // markerOptions.title(name+";"+contacts.get(i));
                    markerOptions.title(contacts.get(i).getRating() == null ? name : (name + ";" + contacts.get(i).getRating()));
                    marker = getGoogleMap().addMarker(markerOptions);
                }
                if(marker != null)
                    marker.setTag(contacts.get(i));
            }
        } else if (myLocationMarkerOption != null && myLocationMarkerOption.getPosition() != null) {
            getGoogleMap().clear();
            myLocationMarkerOption.position(myLatLng);
            myMarker = getGoogleMap().addMarker(myLocationMarkerOption);
        }
    }

    @Override
    public void setMyLocation(LatLng latLng) {
        /*CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        getGoogleMap().moveCamera(cameraUpdate);*/
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        return false;
    }

    Contact setcontact() {
        Contact contact = new Contact();

        return contact;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.marker_layout, null);
        TextView textView = (TextView) view.findViewById(R.id.title);
        textView.setText(marker.getTitle().split(";")[0]);
        return view;
    }
}
