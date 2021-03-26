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

import java.util.HashMap;
import java.util.Map;

import in.co.eko.fundu.FunduApplication;
import in.co.eko.fundu.constants.API;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.utils.Fog;

/**
 * Created by Rahul on 12/15/16.
 */

public class FinalRegistrationRequest extends BaseRequest<FinalRegistrationRequest.OnFinalRegistationResults> implements Response.ErrorListener, Response.Listener<JSONObject> {

    private final String TAG = this.getClass().getSimpleName();
    private final Context context;
    private JsonObjectRequest request;
    private JSONObject object;

    public FinalRegistrationRequest(Context context, JSONObject object) {
        super(context);
        this.context = context;
        this.object = object;
    }
//    public void setData(String recipient_id, String amount, String auth, String client_ref_id){
//        object = new JSONObject();
//        try {
//            object.put("recipient_id", recipient_id);
//            object.put("amount", amount);
//            object.put("auth", auth);
//            object.put("client_ref_id", client_ref_id);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        Fog.d(TAG, object.toString());
//    }

    @Override
    public void start() {
        if (object==null) {
            throw new NullPointerException("Set data before start");
        }
        Fog.e("FINAL REG", object.toString());
        request = new JsonObjectRequest(Request.Method.POST, API.FINALREGISTRATION_API,object, this, this){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
               return getFunduHeaders();
            }
        };

        RetryPolicy policy = new DefaultRetryPolicy(180000/*Constants.REQUEST_TIMEOUT_TIME*/, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        FunduApplication.getInstance().addToRequestQueue(request);
    }

    @Override
    public void stop() {
        if (request!=null){
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
        if (callback!=null){
            callback.onFinalRegistationError(error);
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        onVolleyResponse(response);
        if (callback!=null){
            callback.onFinalRegistationResponse(response);
        Fog.e("resppppp", response.toString());
        }
//        if (response.has("data")) {
//            try {
//                JSONObject object = response.getJSONObject("data");
//                if (object.has("amount") && object.has("customer_balance")) {
//                    String walletAmount = object.getString("customer_balance");
//                    FunduUser.setWalletAmount(walletAmount);
//                    Intent intent = new Intent(Constants.HOME_ACTIVITY_ACTION);
//                    intent.putExtra(Constants.UPDATED_AMOUNT, walletAmount);
//                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
        /*if (FunduUser.isUserLogin()) {
            CheckBalanceRequest balanceRequest = new CheckBalanceRequest(context);
            balanceRequest.setData(FunduUser.getContactId());
            balanceRequest.start();
        }*/
    }
    public interface OnFinalRegistationResults{
        void onFinalRegistationResponse(JSONObject response);
        void onFinalRegistationError(VolleyError error);
    }


}
