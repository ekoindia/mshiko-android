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

import java.util.Map;

import in.co.eko.fundu.FunduApplication;
import in.co.eko.fundu.constants.API;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.utils.Fog;

/*
 *
 */

public class TransactionCancelRequest extends BaseRequest<TransactionCancelRequest.OnTransactionCancelResults> implements Response.ErrorListener, Response.Listener<JSONObject> {

    private final String TAG = this.getClass().getSimpleName();
    private final Context context;
    private JsonObjectRequest request;
    private String requestId;
    private String reason;
    private JSONObject jsonObject;

    public TransactionCancelRequest(Context context) {
        super(context);
        this.context = context;
    }
    public void setData(String transactionId,String requestId,String reason,String contactId, String recipientId,String countryShortName){
        jsonObject = new JSONObject();
        try {
            jsonObject.put("transaction_id", transactionId);
            jsonObject.put("contact_id",contactId);
            jsonObject.put("recipient_id",recipientId);
            jsonObject.put("country_shortname",countryShortName);
            jsonObject.put("request_id",requestId);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.requestId = requestId;
        this.reason = reason;
    }

    @Override
    public void start() {
        if (requestId==null && jsonObject == null) {
            throw new NullPointerException("Set transactionId to start request");
        }
        /*request = new StringRequest(Request.Method.POST, String.format(API.TRANSACTION_REVERT, transactionId), this, this){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
               return getFunduHeaders();
            }
        };*/

        request = new JsonObjectRequest(Request.Method.DELETE, String.format(API.TRANSACTION_REVERT, FunduUser.getContactIDType(), FunduUser.getContactId(), requestId,reason),jsonObject, this, this){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
               return getFunduHeaders();
            }
        };
        RetryPolicy policy = new DefaultRetryPolicy(Constants.REQUEST_TIMEOUT_TIME, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        Fog.d("request",""+request);
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
            callback.onTransactionCancelError(error);
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        onVolleyResponse(response);
        if (callback!=null){
            callback.onTransactionCancelResponse(response);
        }
    }
    public interface OnTransactionCancelResults{
        void onTransactionCancelResponse(JSONObject response);
        void onTransactionCancelError(VolleyError error);
    }


}
