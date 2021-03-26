package in.co.eko.fundu.requests;

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
import in.co.eko.fundu.utils.Fog;

/**
 * Created by Rahul on 12/13/16.
 */

public class GetQuestionRequest extends BaseRequest<GetQuestionRequest.OnQuestionRequestResult> implements Response.ErrorListener, Response.Listener<String> {

    private final String TAG = this.getClass().getSimpleName();
    private final Context context;
    private StringRequest request;

    public GetQuestionRequest(Context context) {
        super(context);
        this.context = context;
    }


    @Override
    public void start() {
        // if (contactId == null) {
        //   throw new NullPointerException("Set contactId to start request");
        //}
        // change the api later
        Fog.e("Question API", API.QUESTION_API);
        request = new StringRequest(Request.Method.GET, API.QUESTION_API, this, this) {
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
            callback.onQuestionError(error);
        }
    }

    @Override
    public void onResponse(String response) {
        onVolleyResponse(response);

        /***possible response

         {countries:[{"name":"India",
         "country_code":"+91"
         "starts_with":""
         "number_length":"10"
         "symbol":"\u20B9"*/
        Fog.e("Countries",response);

        if (callback != null) {
            callback.onQuestionResponse(response);
        }
    }

    public interface OnQuestionRequestResult {
        void onQuestionResponse(String response);
        void onQuestionError(VolleyError error);
    }


}
