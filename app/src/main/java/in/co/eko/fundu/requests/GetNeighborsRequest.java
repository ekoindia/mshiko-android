package in.co.eko.fundu.requests;
/*
 * Created by Bhuvnesh
 */

import android.content.Context;
import android.location.Location;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

import java.util.Map;

import in.co.eko.fundu.FunduApplication;
import in.co.eko.fundu.constants.API;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.utils.Fog;

/*
 *
 */

public class GetNeighborsRequest extends BaseRequest<GetNeighborsRequest.OnGetNeighborsResults> implements Response.ErrorListener, Response.Listener<JSONArray> {

    private final String TAG = this.getClass().getSimpleName();
    private JsonArrayRequest request;
    private String latitude;
    private String longitude;
    private String type;
    private String distance;

    public GetNeighborsRequest(Context context) {
        super(context);
    }

    @Override
    public void start() {
        if (request != null) {
            request.cancel();
        }
        if (latitude == null || longitude == null) {
            Fog.d(getTag(), "LATLONG NULL");
            return;
        }
        if (request != null)
            FunduApplication.getInstance().cancelPendingRequests(TAG);
        String url =  String.format(API.GET_NEIGHBORS_API, latitude, longitude);
        if(type != null)
            url = url+"&type="+type;
        if(distance != null)
            url=url+"&"+distance;
        Fog.i(TAG,url);
        request = new JsonArrayRequest(url, this, this) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getFunduHeaders();
            }
        };
        RetryPolicy policy = new DefaultRetryPolicy(Constants.REQUEST_TIMEOUT_TIME, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        FunduApplication.getInstance().addToRequestQueue(request, TAG);
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
        callback.onGetNeighborsError(error);
    }

    @Override
    public void onResponse(JSONArray response) {
        onVolleyResponse(response);
        if(callback != null)
            callback.onGetNeighborsResponse(response);


    }

    public void setData(Location location, String type,boolean addDistance){
        setLocation(location);
        setType(type);
        if(addDistance)
            setDistance();
    }

    public void setDistance(){
        this.distance = "distance";
    }
    public void setLocation(Location location) {
        latitude = String.valueOf(location.getLatitude());
        longitude = String.valueOf(location.getLongitude());
    }

    public void setType(String type) {
        this.type = type;
    }

    public interface OnGetNeighborsResults {
        void onGetNeighborsResponse(JSONArray contacts);

        void onGetNeighborsError(VolleyError error);
    }

}
