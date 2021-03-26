package in.co.eko.fundu.requests;

/**
 * Created by zartha on 10/30/17.
 */

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.Map;

import in.co.eko.fundu.FunduApplication;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.constants.V1API;
import in.co.eko.fundu.models.FunduUser;


public class GetTransactionStatus extends BaseRequest<GetTransactionStatus.GetTransactionStatusResult> implements Response.ErrorListener, Response.Listener<JSONObject> {

    private final String TAG = this.getClass().getSimpleName();
    private final Context context;
    private JsonObjectRequest request;
    private String id;

    public GetTransactionStatus(Context context, String  id) {
        super(context);
        this.context = context;
        this.id = id;
    }

    @Override
    public void start() {
        if (id==null) {
            throw new NullPointerException("Set tid before start");
        }
        request = new JsonObjectRequest(Request.Method.GET, String.format(V1API.TRANSACTION_STATUS, FunduUser.getContactIDType(), FunduUser.getContactId(),id), null, this, this) {
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
            callback.OnTransactionStatusError(error);
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        onVolleyResponse(response);
        if (callback != null) {
            callback.OnTransactionStatusResponse(response);
        }
    }

    public interface GetTransactionStatusResult {
        void OnTransactionStatusResponse(JSONObject response);
        void OnTransactionStatusError(VolleyError error);
    }


}

