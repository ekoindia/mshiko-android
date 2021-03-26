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
 * Created by user on 1/19/17.
 */

public class ChangeFunduPinRequest extends BaseRequest<ChangeFunduPinRequest.OnChangeFunduPinResults> implements Response.ErrorListener, Response.Listener<JSONObject> {

    private final String TAG = this.getClass().getSimpleName();
    private final Context context;
    private JsonObjectRequest request;
    private String custid, country_shortname, oldpin, newpin, role;
    private JSONObject object;

    public ChangeFunduPinRequest(Context context) {
        super(context);
        this.context = context;
    }
//    custid,countryShortname,old_pin,new_pin,role
    public void setData(String custid, String country_shortname, String oldpin, String newpin, String role ) {
        this.custid = custid;
        this.country_shortname = country_shortname;
        this.oldpin = oldpin;
        this.newpin = newpin;
        this.role = role;

        object = new JSONObject();
        try {
            object.put("custid", this.custid);
            object.put("country_shortname", this.country_shortname);
            object.put("old_fundu_pin", this.oldpin);
            object.put("new_fundu_pin", this.newpin);
            object.put("role", this.role);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void start() {
        if (object == null) {
            throw new NullPointerException("Set contactId to start request");
        }
        Fog.e("Change FPin API",API.CHANGE_FUNDU_PIN_API+" \nJson : "+object.toString());
        request = new JsonObjectRequest(Request.Method.POST, API.CHANGE_FUNDU_PIN_API, object, this, this) {
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
        if (callback != null) {
            callback.onChangeFunduPinError(error);
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        onVolleyResponse(response);
//        try {
//            JSONObject jsonObject = new JSONObject(response);
//            String walletAmount = jsonObject.optString("Balance Amount");
//            FunduUser.setWalletAmount(walletAmount);
//            Intent intent = new Intent(Constants.HOME_ACTIVITY_ACTION);
//            intent.putExtra(Constants.UPDATED_AMOUNT, walletAmount);
//            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Fog.d(TAG, "Exception - Due to unexpected key-value");
//        }
        if (callback != null) {
            callback.onChangeFunduPinResponse(response);
        }
    }

    public interface OnChangeFunduPinResults {
        void onChangeFunduPinResponse(JSONObject response);

        void onChangeFunduPinError(VolleyError error);
    }


}
