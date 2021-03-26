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
 * Created by user on 12/29/16.
 */

public class VerifyFunduPinRequest extends BaseRequest<VerifyFunduPinRequest.OnVerifyFunduPinResults> implements Response.ErrorListener, Response.Listener<JSONObject> {

    private final String TAG = this.getClass().getSimpleName();
    private JsonObjectRequest request;
    private String contactType;
    private String contactId;
    private String fundupin, country_shortcode, custid;
    private JSONObject jsonObject;

    public VerifyFunduPinRequest(Context context) {
        super(context);
    }

    @Override
    public void start() {
        if (request != null) {
            request.cancel();
        }
        if (fundupin == null) {
            Fog.d(getTag(), "DATA NULL");
            throw new NullPointerException("Set contact id and contact type to start request");
        }
        Fog.e("OTP URL ",API.VERIFY_FUNDUPIN_API+" JSON "+createJsonForFunduRequest());
        request = new JsonObjectRequest(Request.Method.POST, API.VERIFY_FUNDUPIN_API, createJsonForFunduRequest(), this, this) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
               return getFunduHeaders ();
            }


        };
        RetryPolicy policy = new DefaultRetryPolicy(Constants.REQUEST_TIMEOUT_TIME, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        FunduApplication.getInstance().addToRequestQueue(request);
    }
//    {"custid":"50F6-7F92-2832-F9322","fundu_pin":"1111","country_shortname":"KEN"}
    private JSONObject createJsonForFunduRequest() {
        jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.CUSTOMERID, custid);
            jsonObject.put("fundu_pin", fundupin);
            jsonObject.put(Constants.COUNTRY_SHORTCODE, country_shortcode);
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
        callback.onVerifyFunduPinError(error);
    }

    public void setData(String custid, String fundupin, String country_shortcode) {
        this.custid = custid;
        this.fundupin = fundupin;
        this.country_shortcode = country_shortcode;
    }

    @Override
    public void onResponse(JSONObject response) {
        onVolleyResponse(response);
        callback.onVerifyFunduPinResponse(response.toString());
    }

    public interface OnVerifyFunduPinResults {
        void onVerifyFunduPinResponse(String object);

        void onVerifyFunduPinError(VolleyError error);
    }

}

