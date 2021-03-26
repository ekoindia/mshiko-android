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

import java.util.Map;

import in.co.eko.fundu.FunduApplication;
import in.co.eko.fundu.constants.API;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.utils.Fog;

/**
 * Created by user on 12/30/16.
 */

public class KenConfirmationRequest extends BaseRequest<KenConfirmationRequest.KenConfirmationResults> implements Response.ErrorListener, Response.Listener<JSONObject> {

    private final String TAG = this.getClass().getSimpleName();
    private JsonObjectRequest request;
    private String  country_shortcode, custid, type;
    private JSONObject jsonObject;
    private double amount = 0;
    public KenConfirmationRequest(Context context) {
        super(context);
    }

    @Override
    public void start() {
        if (request != null) {
            request.cancel();
        }
        if (amount == 0 ) {
            Fog.d(getTag(), "DATA NULL");
            throw new NullPointerException("Set contact id and contact type to start request");
        }
        Fog.e("OTP URL ", API.KEN_CONFIRMATION_API+" JSON "+createJsonForFunduRequest());
        request = new JsonObjectRequest(Request.Method.POST, API.KEN_CONFIRMATION_API, createJsonForFunduRequest(), this, this) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getFunduHeaders();
            }
        };
        RetryPolicy policy = new DefaultRetryPolicy(Constants.REQUEST_TIMEOUT_TIME, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        FunduApplication.getInstance().addToRequestQueue(request);
    }
    private JSONObject createJsonForFunduRequest() {
        jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.CUSTOMERID, custid);
            jsonObject.put("amount", amount);
            jsonObject.put(Constants.COUNTRY_SHORTCODE, country_shortcode);
            jsonObject.put("type", type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
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
        callback.onKenConfirmationError(error);
    }

    public void setData(String custid, double amount, String country_shortcode, String type) {
        this.custid = custid;
        this.amount = amount;
        this.country_shortcode = country_shortcode;
        this.type = type;
    }

    @Override
    public void onResponse(JSONObject response) {
        onVolleyResponse(response);
        callback.onKenConfirmationResponse(response.toString());
    }

    public interface KenConfirmationResults {
        void onKenConfirmationResponse(String object);

        void onKenConfirmationError(VolleyError error);
    }

}


