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
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import in.co.eko.fundu.FunduApplication;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.constants.API;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.utils.Utils;

/*
 *
 */

public class TransactionCommitRequest extends BaseRequest<TransactionCommitRequest.OnTransactionCommitResults> implements Response.ErrorListener, Response.Listener<String> {

    private final String TAG = this.getClass().getSimpleName();
    public final Context context;
    private StringRequest request;
    private String transactionId;
    private String bankTransactionId;
    public static boolean needCash = true;
    public TransactionCommitRequest(Context context) {
        super(context);
        this.context = context;
    }

    public void setData(String transactionId,String bankTransactionId) {
        this.transactionId = transactionId;
        this.bankTransactionId = bankTransactionId;
    }

    @Override
    public void start() {
        if (transactionId == null) {
            throw new NullPointerException("Set transactionId to start request");
        }
        /*request = new StringRequest(Request.Method.POST, String.format(API.TRANSACTION_COMMIT, transactionId), this, this){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
               return getFunduHeaders();
            }
        };*/
        if(bankTransactionId == null){
            bankTransactionId = "";
        }

        request = new StringRequest(Request.Method.PUT, String.format(API.TRANSACTION_COMMIT, FunduUser.getContactIDType(), FunduUser.getContactId(), transactionId, needCash,bankTransactionId), this, this) {
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
            callback.onTransactionCommitError(Utils.configureErrorMessage(error));
        }
    }

    @Override
    public void onResponse(String response) {
        onVolleyResponse(response);
        if (callback != null) {
            callback.onTransactionCommitResponse(response);
        }
    }

    public interface OnTransactionCommitResults {
        void onTransactionCommitResponse(String response);

        void onTransactionCommitError(VolleyError error);
    }


}
