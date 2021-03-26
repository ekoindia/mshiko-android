package in.co.eko.fundu.requests;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import in.co.eko.fundu.FunduApplication;
import in.co.eko.fundu.constants.API;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.utils.Fog;

/**
 * Created by pallavi on 4/12/17.
 */

public class VerifyTransactionCodeRequest extends BaseRequest<VerifyTransactionCodeRequest.OnVerifyTransactionCodeResults>
        implements Response.ErrorListener, Response.Listener<JSONObject> {



    private final String TAG = this.getClass().getSimpleName();
    private JsonObjectRequest request;
    private JSONObject jsonObject;
    private final Context context;
    private LatLng location;

    public VerifyTransactionCodeRequest(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public void start() {

        if (jsonObject==null) {
            throw new NullPointerException("Set data before start");
        }
        Fog.e(TAG,API.VERIFY_TRANX_CODE);
        Fog.e(TAG,jsonObject.toString());
        if (request != null) {
            request.cancel();
        }

        request = new JsonObjectRequest(Request.Method.POST, String.format(API.VERIFY_TRANX_CODE, FunduUser.getContactId()), jsonObject, this, this) {

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
        callback.onVerifyTransactionCodeResponse(response.toString());

    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {

        onVolleyErrorResponse(volleyError);

        if (callback != null) {
            callback.onVerifyTransactionCodeError(volleyError);
        }
    }

    public void setData(String transactionId, String requestId, String code, LatLng location) {
        jsonObject = new JSONObject();
        try {

            jsonObject.put("transaction_id", transactionId);
            jsonObject.put("request_id",requestId);
            jsonObject.put("code", code);

            JSONObject location1 = new JSONObject();
            JSONArray locationArray = new JSONArray();
            locationArray.put(location.longitude);
            locationArray.put(location.latitude);
            location1.put("coordinates",locationArray);

            jsonObject.put("otp_exchange_location", location1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public interface OnVerifyTransactionCodeResults {
        void onVerifyTransactionCodeResponse(String object);

        void onVerifyTransactionCodeError(VolleyError error);
    }

}
