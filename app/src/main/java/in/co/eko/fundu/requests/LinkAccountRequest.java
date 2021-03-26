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
import in.co.eko.fundu.utils.Utils;

/**
 * Created by divyanshu.jain on 7/21/2016.
 */
public class LinkAccountRequest extends BaseRequest<LinkAccountRequest.OnLinkAccountResults> implements Response.Listener<JSONObject>, Response.ErrorListener {
    private final String TAG = this.getClass().getSimpleName();
    JSONObject jsonObject = null;
    JsonObjectRequest request;

    public LinkAccountRequest(Context context) {
        super(context);
    }

    public void setData(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    @Override
    public void start() {
        String id = Utils.appendCountryCodeToNumber(getContext(), FunduUser.getContactId());
        String id_type = FunduUser.getContactIDType();
        request = new JsonObjectRequest(Request.Method.PUT, String.format(API.CUSTOMER, id_type, id), jsonObject, this, this) {
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
    protected void stop() {

    }

    @Override
    protected String getTag() {
        return TAG;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Fog.i(TAG, error.toString());
        callback.onLinkAccountError(error);
    }

    @Override
    public void onResponse(JSONObject response) {
        Fog.i(TAG, jsonObject.toString());

        callback.onLinkAccountResponse(response);
    }


    public interface OnLinkAccountResults {
        void onLinkAccountResponse(JSONObject response);

        void onLinkAccountError(VolleyError error);
    }
}
