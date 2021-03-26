package in.co.eko.fundu.requests;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import in.co.eko.fundu.FunduApplication;
import in.co.eko.fundu.constants.API;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.utils.Fog;

/**
 * Created by Rahul on 1/22/17.
 */

public class TransactionHistoryRequest extends BaseRequest<TransactionHistoryRequest.OnTransferHistoryResult> implements Response.ErrorListener, Response.Listener<String>{

    private final String TAG = this.getClass().getSimpleName();
    private StringRequest request;
    private Context context;
    private String country_shortname, custid, start,  end;

    public TransactionHistoryRequest(Context context){
        super(context);
        this.context = context;
    }

    public void setData(String country_shortname, String custid, String start, String end){
        this.country_shortname = country_shortname;
        this.custid = custid;
        this.start = start;
        this.end = end;
    }
    @Override
    public void start() {
        if (custid==null) {
            throw new NullPointerException("Set transactionId to start request");
        }
        Fog.e("THR",String.format(API.TRANSACTION_HISTORY, country_shortname,custid,start,end));
        request = new StringRequest(Request.Method.GET, String.format(API.TRANSACTION_HISTORY, country_shortname,custid,start,end), this, this){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
               return getFunduHeaders();
            }
        };
//        RetryPolicy policy = new DefaultRetryPolicy(Constants.REQUEST_TIMEOUT_TIME, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        RetryPolicy policy = new DefaultRetryPolicy(Constants.REQUEST_TIMEOUT_TIME, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
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
    public String getTag() {
        return TAG;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        onVolleyErrorResponse(error);
    if (callback != null){
        callback.onTransferHistoryError(error);
    }
    }

    @Override
    public void onResponse(String response) {
        onVolleyResponse(response);
        if (callback != null){
            callback.onTransferHistroryResults(response);
        }
    }

    public interface OnTransferHistoryResult{
        void onTransferHistroryResults(String object);
        void onTransferHistoryError(VolleyError error);

    }
}
