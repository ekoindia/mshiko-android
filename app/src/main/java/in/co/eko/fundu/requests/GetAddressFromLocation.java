package in.co.eko.fundu.requests;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.Locale;
import java.util.Map;

import in.co.eko.fundu.FunduApplication;
import in.co.eko.fundu.constants.Constants;

/**
 * Created by zartha on 8/28/17.
 */

public class GetAddressFromLocation extends BaseRequest<GetAddressFromLocation.OnAddressResult> implements Response.ErrorListener, Response.Listener<JSONObject>{
    private String TAG = GetAddressFromLocation.class.getName();
    private JsonObjectRequest request;
    private double lat,lng;

    public void setData(double lat, double lng, int maxResult){
        this.lat = lat;
        this.lng = lng;
    }
    public GetAddressFromLocation(Context context){
        super(context);
    }


    @Override
    public void start() {

        // change the api later
        String url = String.format(Locale.ENGLISH, "https://maps.googleapis.com/maps/api/geocode/json?latlng=%1$f,%2$f&sensor=false&language=" + Locale.getDefault().getCountry(), lat, lng);
        request = new JsonObjectRequest(Request.Method.GET, url, null,this, this) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getFunduHeaders();
            }
        };

        RetryPolicy policy = new DefaultRetryPolicy(Constants.REQUEST_TIMEOUT_TIME, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        FunduApplication.getInstance().addToRequestQueue(request);
    }

    @Override
    public void stop() {
        if (request != null) {
            request.cancel();
        }
    }

    @Override
    protected String getTag() {
        return TAG;
    }

    @Override
    public void onErrorResponse(VolleyError error) {

        onVolleyErrorResponse(error);
        if (callback != null) {
            callback.onAddressError(error);
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        onVolleyResponse(response);


        if (callback != null) {
            callback.onAddressResponse(response);
        }
    }

    public interface OnAddressResult {
        void onAddressResponse(JSONObject response);
        void onAddressError(VolleyError error);
    }

}
