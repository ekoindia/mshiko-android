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

import java.util.Map;

import in.co.eko.fundu.FunduApplication;
import in.co.eko.fundu.constants.API;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.utils.Fog;

/**
 * Created by user on 12/22/16.
 */

public class HasFundRequest extends BaseRequest<HasFundRequest.OnHasFundResults> implements Response.ErrorListener, Response.Listener<JSONObject> {

    private final String TAG = this.getClass().getSimpleName();
    private final Context context;
    private JsonObjectRequest request;
    private String contactId;
    private JSONObject object;

    public HasFundRequest(Context context, JSONObject object) {
        super(context);
        this.context = context;
        this.object = object;
    }

//    public void setData(String contactId) {
//        this.contactId = contactId;
//    }

    @Override
    public void start() {
        if (object==null) {
            throw new NullPointerException("Set data before start");
        }
        Fog.e("HAS FUND API",API.HAS_FUND_API);
        Fog.e("HAS FUND JSON",object.toString());

        request = new JsonObjectRequest(Request.Method.POST, API.HAS_FUND_API, object, this, this) {
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
            callback.onHasFundError(error);
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        onVolleyResponse(response);
        if (callback != null) {
            callback.onHasFundResponse(response.toString());
        }
    }

    public interface OnHasFundResults {
        void onHasFundResponse(String response);

        void onHasFundError(VolleyError error);
    }


}
