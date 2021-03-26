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
import in.co.eko.fundu.constants.API;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.utils.Fog;
import in.co.eko.fundu.utils.Utils;

/**
 * Created by user on 3/28/17.
 */

public class VerifyMerchantAgent extends BaseRequest<VerifyMerchantAgent.OnVerifyMerchantAgentResults> implements Response.ErrorListener, Response.Listener<JSONObject> {

    private final String TAG = this.getClass().getSimpleName();
    private JsonObjectRequest request;
    private String country_shortcode, mobile, business_name, incorp_businessNo, business_type, vertical_market, contact_person_name, contact_person_phone,
            allow_withdraw, opening_time, closing_time, days, physical_location, image_url;
    private JSONObject jsonObject;
    private boolean becomemerchant = false;
    private Context context;

    public VerifyMerchantAgent(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public void start() {
        if (request != null) {
            request.cancel();
        }
        if (incorp_businessNo == null) {
            Fog.d(getTag(), "DATA NULL");
            throw new NullPointerException("Set contact id and contact type to start request");
        }
        Fog.e("OTP URL ", String.format(API.VERIFY_AGENT_API, Constants.MOBILE_TYPE, Utils.appendCountryCodeToNumber(context, mobile), becomemerchant) + " JSON " + createJsonForAgentRequest());
        request = new JsonObjectRequest(Request.Method.PUT, String.format(API.VERIFY_AGENT_API, Constants.MOBILE_TYPE, Utils.appendCountryCodeToNumber(context, mobile), becomemerchant), createJsonForAgentRequest(), this, this) {

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

    private JSONObject createJsonForAgentRequest() {
        jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.COUNTRY_SHORTCODE, country_shortcode);
            jsonObject.put("mobile", mobile);
            jsonObject.put("business_name", business_name);
            jsonObject.put("incorp_businessNo", incorp_businessNo);
            jsonObject.put("business_type", business_type);
            jsonObject.put("vertical_market", vertical_market);
            jsonObject.put("contact_person_name", contact_person_name);
            jsonObject.put("contact_person_phone", contact_person_phone);
            jsonObject.put(Constants.ALLOW_WITHDRAW, allow_withdraw);
            jsonObject.put(Constants.OPENING_TIME, opening_time);
            jsonObject.put(Constants.CLOSING_TIME, closing_time);
            jsonObject.put(Constants.DAYS, days);
            jsonObject.put("physical_location", physical_location);
            jsonObject.put("merchant_img_url", image_url);
//            jsonObject.put("become_merchant", becomemerchant);
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
        callback.onVerifyMerchantAgentError(error);
    }

    public void setData(String country_shortcode, String mobile, String business_name, String incorp_businessNo,
                        String business_type, String vertical_market, String contact_person_name, String contact_person_phone,
                        String allow_withdraw, String opening_time, String closing_time, String days, String physical_location, String image_url,
                        boolean becomeMerchant) {

        this.country_shortcode = country_shortcode;
        this.mobile = mobile;
        this.business_name = business_name;
        this.incorp_businessNo = incorp_businessNo;
        this.business_type = business_type;
        this.vertical_market = vertical_market;
        this.contact_person_name = contact_person_name;
        this.contact_person_phone = contact_person_phone;
        this.allow_withdraw = allow_withdraw;
        this.opening_time = opening_time;
        this.closing_time = closing_time;
        this.days = days;
        this.physical_location = physical_location;
        this.image_url = image_url;
        this.becomemerchant = becomeMerchant;
    }

    @Override
    public void onResponse(JSONObject response) {
        onVolleyResponse(response);
        callback.onVerifyMerchantAgentResponse(response.toString());
    }

    public interface OnVerifyMerchantAgentResults {
        void onVerifyMerchantAgentResponse(String object);

        void onVerifyMerchantAgentError(VolleyError error);
    }

}
