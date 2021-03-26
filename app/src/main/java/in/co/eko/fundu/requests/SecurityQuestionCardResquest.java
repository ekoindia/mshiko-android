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
 * Created by Rahul on 1/28/17.
 */

public class SecurityQuestionCardResquest extends BaseRequest<SecurityQuestionCardResquest.OnSecurityQuestionCardResults> implements Response.ErrorListener, Response.Listener<String> {

    private final String TAG = this.getClass().getSimpleName();
    private StringRequest request;
    private String cust_id = "";

    public SecurityQuestionCardResquest(Context context) {
        super(context);
    }

    @Override
    public void start() {
        if (request != null) {
            request.cancel();
        }
        if (cust_id.equalsIgnoreCase("") ) {
            Fog.d(getTag(), "DATA NULL");
            throw new NullPointerException("Set contact id and contact type to start request");
        }
        String _API = API.SECURITY_QUESTION_CARD+cust_id;
        request = new StringRequest(Request.Method.GET, _API, this, this) {

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
        callback.onSecurityQuestionCardError(error);
    }

    public void setData(String custid){
        this.cust_id = custid;
    }

    @Override
    public void onResponse(String response) {
        onVolleyResponse(response);
        callback.onSecurityQuestionCardResponse(response);
    }

    public interface OnSecurityQuestionCardResults {
        void onSecurityQuestionCardResponse(String object);

        void onSecurityQuestionCardError(VolleyError error);
    }

}


