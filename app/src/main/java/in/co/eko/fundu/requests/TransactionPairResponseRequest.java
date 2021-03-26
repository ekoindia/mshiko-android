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

import java.util.Map;

import in.co.eko.fundu.FunduApplication;
import in.co.eko.fundu.constants.API;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.models.Contact;
import in.co.eko.fundu.utils.Fog;

/*
 *
 */

public class TransactionPairResponseRequest extends BaseRequest<TransactionPairResponseRequest.OnTransactionPairResults> implements Response.ErrorListener, Response.Listener<String> {

    private final String TAG = this.getClass().getSimpleName();
    private final Context context;
    private StringRequest request;
    private String requestId;
    private String contactid;
    private String decision;

    public TransactionPairResponseRequest(Context context) {
        super(context);
        this.context = context;
    }
    public void setData(String requestId, String contactid, String decision){
        this.requestId = requestId;
        this.contactid = contactid;
        this.decision = decision;
    }

    @Override
    public void start() {
        if (requestId==null || contactid ==null || decision == null) {
            throw new NullPointerException("Set requestId id, clientId and decision to start request");
        }
        Fog.e("Provider PAIR Accpt ",String.format(API.TRANSACTION_PAIR_RESPONSE_API, requestId, contactid,decision ));
        request = new StringRequest(Request.Method.GET, String.format(API.TRANSACTION_PAIR_RESPONSE_API, requestId, contactid,decision ), this, this){
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
            callback.onTransactionPairError(error);
        }
    }

    @Override
    public void onResponse(String response) {
        onVolleyResponse(response);
        Fog.e("TPRR",response);
        if (callback!=null){
            callback.onTransactionPairResponse(new Contact());
        }
    }

    public interface OnTransactionPairResults{
        void onTransactionPairResponse(Contact contact);
        void onTransactionPairError(VolleyError error);
    }
}
