package in.co.eko.fundu.requests;
/*
 * Created by Bhuvnesh
 */

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

public class VerifyOtpRequest extends BaseRequest<VerifyOtpRequest.OnVerifyOtpResults> implements Response.ErrorListener, Response.Listener<JSONObject> {

    private final String TAG = this.getClass().getSimpleName();
    private JsonObjectRequest request;
    private String contactType;
    private String contactId;
    private String otp, country_shortcode;
    private JSONObject jsonObject;

    public VerifyOtpRequest(Context context) {
        super(context);
    }

    @Override
    public void start() {
        if (request != null) {
            request.cancel();
        }
        if (contactType == null || contactId == null || otp == null) {
            Fog.d(getTag(), "DATA NULL");
            throw new NullPointerException("Set contact id and contact type to start request");
        }
        Fog.e("OTP URL ",String.format(API.VERIFY_OTP_API, contactType, contactId)+" JSON "+createJsonForOtpRequest());
        request = new JsonObjectRequest(Request.Method.PUT, String.format(API.VERIFY_OTP_API, contactType, contactId), createJsonForOtpRequest(), this, this) {

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

    private JSONObject createJsonForOtpRequest() {
        jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.OTP, otp);
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
        callback.onVerifyOtpError(error);
    }

    public void setData(String contactType, String contactId, String otp, String country_shortcode) {
        this.contactType = contactType;
        //TODO Check if country code is actually required
        this.contactId = Utils.appendCountryCodeToNumber(getContext(), contactId);
        this.otp = otp;
        this.country_shortcode = country_shortcode;
    }

    @Override
    public void onResponse(JSONObject response) {
        onVolleyResponse(response);
        if(callback != null)
            callback.onVerifyOtpResponse(response);
    }

    public interface OnVerifyOtpResults {
        void onVerifyOtpResponse(JSONObject object);

        void onVerifyOtpError(VolleyError error);
    }

}
