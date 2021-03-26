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
 * Created by user on 5/31/17.
 */

public class UpdateMerchantTimingRequest extends BaseRequest<UpdateMerchantTimingRequest.UpdateMerchantTimingRequestResults> implements Response.ErrorListener, Response.Listener<JSONObject> {

    private final String TAG = this.getClass().getSimpleName();
    private JsonObjectRequest request;
    private String country_shortcode, openingtime, closingtime, days, contact_id_type, contact_id;
    private JSONObject jsonObject;
    private int amount = 0;

    public UpdateMerchantTimingRequest(Context context) {
        super(context);
    }

    @Override
    public void start() {
        if (request != null) {
            request.cancel();
        }
        if (contact_id == null) {
            Fog.d(getTag(), "DATA NULL");
            throw new NullPointerException("Set contact id and contact type to start request");
        }
        Fog.e("OTP URL ", API.UPDATEMERCHANTTIMING + " JSON " + createJsonForFunduRequest());
        request = new JsonObjectRequest(Request.Method.PUT, API.UPDATEMERCHANTTIMING, createJsonForFunduRequest(), this, this) {
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
            jsonObject.put(Constants.DAYS, days);
            jsonObject.put(Constants.OPENING_TIME, openingtime);
            jsonObject.put(Constants.CLOSING_TIME, closingtime);
            jsonObject.put(Constants.COUNTRY_SHORTCODE, country_shortcode);
            jsonObject.put(Constants.CONTACT_ID_TYPE, contact_id_type);
            jsonObject.put(Constants.CONTACT_ID, contact_id);
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
        callback.onUpdateMerchantTimingRequestError(error);
    }

    public void setData(String days, String openingTime, String closingTime, String country_shortcode, String contact_id_type, String contact_id) {

        this.days = days;
        this.openingtime = openingTime;
        this.closingtime = closingTime;
        this.country_shortcode = country_shortcode;
        this.contact_id_type = contact_id_type;
        this.contact_id = contact_id;
    }

    @Override
    public void onResponse(JSONObject response) {
        onVolleyResponse(response);
        callback.onUpdateMerchantTimingRequestResponse(response.toString());
    }

    public interface UpdateMerchantTimingRequestResults {
        void onUpdateMerchantTimingRequestResponse(String object);

        void onUpdateMerchantTimingRequestError(VolleyError error);
    }

}


