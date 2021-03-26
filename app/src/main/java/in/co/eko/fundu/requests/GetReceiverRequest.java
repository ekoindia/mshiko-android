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

import java.util.Map;

import in.co.eko.fundu.FunduApplication;
import in.co.eko.fundu.constants.API;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.utils.Fog;

/**
 * Created by pallavi on 15/12/17.
 */

public class GetReceiverRequest extends BaseRequest<GetReceiverRequest.OnUserProfileRequestResult> implements Response.ErrorListener, Response.Listener<JSONObject>  {

    private final String TAG = GetReceiverRequest.class.getName();
    private JsonObjectRequest request;
    private Context context;
    private String mMobileNumber="";

    public GetReceiverRequest(Context context) {
        super(context);
        this.context = context;
    }
    @Override
    public void start() {
        String url = String.format(API.LOCATIONS,mMobileNumber);
        Fog.d(TAG,"providerurl****"+url);
        request = new JsonObjectRequest(Request.Method.GET,url
                ,null, this, this) {
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
            callback.onUserProfileError(error);
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        onVolleyResponse(response);
        Fog.e("ProviderProfile",response.toString());

        if (callback != null) {
            callback.onUserProfileResponse(response);
        }
    }

    public void  setData(String pMobileNumber){
        this.mMobileNumber = pMobileNumber;
    }

    public interface OnUserProfileRequestResult {
        void onUserProfileResponse(JSONObject response);
        void onUserProfileError(VolleyError error);
    }
}