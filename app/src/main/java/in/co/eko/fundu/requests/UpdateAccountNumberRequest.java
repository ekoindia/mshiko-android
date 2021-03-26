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
 * Created by Rahul on 4/28/17.
 */

public class UpdateAccountNumberRequest extends BaseRequest<UpdateAccountNumberRequest.OnUpdateAccountNumberResults> implements Response.ErrorListener, Response.Listener<JSONObject> {

    private final String TAG = this.getClass().getSimpleName();
    private final Context context;
    private JsonObjectRequest request;
    private String custid, country_shortname, accno, fundu_pin, seekercustid = null;
    private JSONObject object;

    public UpdateAccountNumberRequest(Context context) {
        super(context);
        this.context = context;
    }

    public void setData(String custid, String country_shortname, String accno, String fundu_pin, String seekercustid) {
        this.custid = custid;
        this.country_shortname = country_shortname;
        this.accno = accno;
        this.fundu_pin = fundu_pin;
        if (seekercustid != null)
            this.seekercustid = seekercustid;

        object = new JSONObject();
        try {
            object.put("custid", this.custid);
            object.put("country_shortname", this.country_shortname);
            object.put("accno", this.accno);
            object.put("fundu_pin", this.fundu_pin);
            if (this.seekercustid != null)
                object.put("seeker_custid", this.seekercustid.trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        if (object == null) {
            throw new NullPointerException("Set contactId to start request");
        }
        Fog.e("Change FPin API", API.CHANGE_ACCOUNT_NO_API + " \nJson : " + object.toString());
        request = new JsonObjectRequest(Request.Method.POST, API.CHANGE_ACCOUNT_NO_API, object, this, this) {
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
            callback.onUpdateAccountNumberError(error);
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
            callback.onUpdateAccountNumberResponse(response);
        }
    }

    public interface OnUpdateAccountNumberResults {
        void onUpdateAccountNumberResponse(JSONObject response);

        void onUpdateAccountNumberError(VolleyError error);
    }


}
