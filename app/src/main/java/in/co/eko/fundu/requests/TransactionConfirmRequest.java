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
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.utils.Fog;

/**
 * Created by zartha on 7/27/17.
 */

public class TransactionConfirmRequest extends BaseRequest<TransactionConfirmRequest.onTransactionConfirmRequestResult>
        implements Response.ErrorListener, Response.Listener<JSONObject>{

    private String TAG = TransactionConfirmRequest.class.getName();
    private Context context;
    private onTransactionConfirmRequestResult listener;
    private JSONObject requestParams;
    private JsonObjectRequest request;
    public TransactionConfirmRequest(Context context, onTransactionConfirmRequestResult listener){
        super(context);
        this.context = context;
        this.listener = listener;
    }
    @Override
    public void start(){
        if (requestParams==null) {
            throw new NullPointerException("Set data before start");
        }
        Fog.i(TAG,requestParams.toString());

        String requestUrl = String.format(API.TRANSACTION_CONFIRM, FunduUser.getContactIDType(), FunduUser.getContactId());
        request = new JsonObjectRequest(Request.Method.POST, requestUrl, requestParams, this, this) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
               return getFunduHeaders();
            }
        };
        RetryPolicy policy = new DefaultRetryPolicy(Constants.REQUEST_TIMEOUT_TIME, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        FunduApplication.getInstance().addToRequestQueue(request);
    }
    @Override
    public void stop(){

    }
    @Override
    public void onResponse(JSONObject result){
        onVolleyResponse(result);
        if(listener != null)
            listener.onTransactionConfirmResponse(result);
    }
    @Override
    public void onErrorResponse(VolleyError error){
        onVolleyErrorResponse(error);
        if(listener != null)
            listener.onTransactionConfirmError(error);
    }
    public interface onTransactionConfirmRequestResult {
        void onTransactionConfirmResponse(JSONObject response);

        void onTransactionConfirmError(VolleyError error);
    }
    @Override
    public String getTag(){
        return TAG;
    }

    public void setData(String tid,String requestId,JSONObject transactionStatus){
        try{
            requestParams = new JSONObject();
            requestParams.put("transaction_id",tid);
            requestParams.put("request_id",requestId);
            requestParams.put("status",transactionStatus);

        }
        catch (JSONException e){
            e.printStackTrace();
        }

    }
}
