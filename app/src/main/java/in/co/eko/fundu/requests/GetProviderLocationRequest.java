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
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.utils.Fog;

/**
 * Created by pallavi on 14/12/17.
 */

public class GetProviderLocationRequest extends BaseRequest<GetUserProfileRequest.OnUserProfileRequestResult>
        implements Response.ErrorListener, Response.Listener<JSONObject>  {

    private final String TAG = GetUserProfileRequest.class.getName();
    private JsonObjectRequest request;
    private Context context;
    String providerNumber = "";
    public GetProviderLocationRequest(Context context) {
        super(context);
        this.context = context;
    }
    @Override
    public void start() {
        // if (contactId == null) {
        //   throw new NullPointerException("Set contactId to start request");
        //}
        // change the api later
        String id = FunduUser.getContactId();
        String id_type = FunduUser.getContactIDType();
        String url = String.format(API.GET_CONTACT_API,id_type,id);
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


    public void setData(String providerNumber) {
        this.providerNumber = providerNumber;
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
        Fog.e("Profile",response.toString());

        if (callback != null) {
            callback.onUserProfileResponse(response);
        }
    }

    public interface OnUserProfileRequestResult {
        void onUserProfileResponse(JSONObject response);
        void onUserProfileError(VolleyError error);
    }
}
