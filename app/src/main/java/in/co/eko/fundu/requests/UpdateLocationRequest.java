package in.co.eko.fundu.requests;/*
 * Created by Bhuvnesh
 */

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.co.eko.fundu.FunduApplication;
import in.co.eko.fundu.constants.API;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.utils.Fog;

// Extending Volley Request
public class UpdateLocationRequest extends BaseRequest implements Response.Listener<JSONObject>{

    private static final String TAG = "UpdateLocationRequest";

    JsonObjectRequest request;
    JSONObject location;

    public UpdateLocationRequest(Context context){
        super(context);
    }

    public void setLocation(JSONObject location) {
       this.location = location;
    }
    @Override
    public void start() {
        if(this.location == null){
            return;
        }
        String contactId = FunduUser.getContactId();
        request = new JsonObjectRequest(Request.Method.PUT,String.format(API.UPDATE_LOCATION, "mobile_type", contactId),location,this,null){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", Constants.PrivateKey.SWAGGER_AUTHENTICATION_KEY);
                //TODO: add headers correctly
                return headers;
            }
        };
        RetryPolicy policy = new DefaultRetryPolicy(Constants.REQUEST_TIMEOUT_TIME, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        FunduApplication.getInstance().addToRequestQueue(request,TAG);
    }

    @Override
    protected void stop() {
        if (request != null) {
            request.cancel();
        }
    }

    @Override
    protected String getTag() {
        return TAG;
    }

    @Override
    public void onResponse(JSONObject response) {
        Fog.i(TAG,"onResponse: "+response);
    }
}
