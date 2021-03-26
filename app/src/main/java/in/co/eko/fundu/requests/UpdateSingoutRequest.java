package in.co.eko.fundu.requests;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.co.eko.fundu.FunduApplication;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.constants.API;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.utils.Fog;

/**
 * Created by pallavi on 7/12/17.
 */

public class UpdateSingoutRequest extends BaseRequest<UpdateSingoutRequest.OnUpdateSingoutResult>
        implements Response.ErrorListener, Response.Listener<JSONObject> {

    private final String TAG = this.getClass().getSimpleName();
    private JsonObjectRequest request;
    private JSONObject jsonObject;
    private final Context context;

    public UpdateSingoutRequest(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public void start() {
        if (jsonObject==null||FunduUser.getContactId()==null) {
            throw new NullPointerException("Set data before start");
        }
        Fog.e("UPDATE_SINGOUT",API.UPDATE_SINGOUT);
        Fog.e("UPDATE_SINGOUT",jsonObject.toString());
        if (request != null) {
            request.cancel();
        }
        request = new JsonObjectRequest(Request.Method.POST, String.format(API.UPDATE_SINGOUT, FunduUser.getContactId()), jsonObject, this, this) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization",
                        Constants.PrivateKey.SWAGGER_AUTHENTICATION_KEY);
                return headers;
            }


        };
        RetryPolicy policy = new DefaultRetryPolicy(Constants.REQUEST_TIMEOUT_TIME, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        FunduApplication.getInstance().addToRequestQueue(request);
    }

    @Override
    protected void stop() {
        if (request != null) {
            request.cancel();
        }
    }

    @Override
    protected String getTag() {
        return null;
    }

    @Override
    public void onResponse(JSONObject response) {
        Fog.d("response",""+response);
        if(callback != null)
            callback.onUpdateSingoutResponse(response);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        onVolleyErrorResponse(volleyError);

        if (callback != null) {
            callback.onUpdateSingoutError(volleyError);
        }
    }

    public void setData(String device_id) {
        jsonObject = new JSONObject();
        try {

            jsonObject.put("device_id", device_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    public interface OnUpdateSingoutResult {

        void onUpdateSingoutResponse(JSONObject object);

        void onUpdateSingoutError(VolleyError error);

    }
}
