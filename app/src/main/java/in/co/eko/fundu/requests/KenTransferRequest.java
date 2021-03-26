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
 * Created by user on 1/3/17.
 */

public class KenTransferRequest extends BaseRequest<KenTransferRequest.OnKenTransferResults> implements Response.ErrorListener, Response.Listener<JSONObject> {

    private final String TAG = this.getClass().getSimpleName();
    private JsonObjectRequest request;
    private String  country_shortcode, seekercustid, providercustid, totp, fpin, type;
    private JSONObject jsonObject;
    private int amount = 0;

    public KenTransferRequest(Context context) {
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
        Fog.e("OTP URL ", API.KEN_TRANSFER_API+" JSON "+createJsonForFunduRequest());
        request = new JsonObjectRequest(Request.Method.POST, API.KEN_TRANSFER_API, createJsonForFunduRequest(), this, this) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getFunduHeaders();
            }

        };
        RetryPolicy policy = new DefaultRetryPolicy(180000/*Constants.REQUEST_TIMEOUT_TIME*/, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        FunduApplication.getInstance().addToRequestQueue(request);
    }
    //    {"provider_custid":"FF45-EF59-A0F5-14A0","amount":"10000","seeker_custid":"50F6-5D06-3932-F935","country_shortname":"KEN","totp":"1234"}
    private JSONObject createJsonForFunduRequest() {
        jsonObject = new JSONObject();
        try {
            jsonObject.put("provider_custid", providercustid);
            jsonObject.put("amount", String.valueOf(amount));
            jsonObject.put("seeker_custid", seekercustid);
            jsonObject.put(Constants.COUNTRY_SHORTCODE, country_shortcode);
            jsonObject.put("totp", totp);
            jsonObject.put("fundu_pin", fpin);
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
        callback.onKenTransferError(error);
    }

    public void setData(String seekercustid, String providercustid, int amount, String country_shortcode, String totp, String fpin, String type) {
        this.seekercustid = seekercustid;
        this.providercustid = providercustid;
        this.amount = amount;
        this.country_shortcode = country_shortcode;
        this.totp = totp;
        this.fpin = fpin;
        this.type = type;
    }

    @Override
    public void onResponse(JSONObject response) {
        onVolleyResponse(response);
        callback.onKenTransferResponse(response.toString());
    }

    public interface OnKenTransferResults {
        void onKenTransferResponse(String object);

        void onKenTransferError(VolleyError error);
    }

}


