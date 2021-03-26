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
import in.co.eko.fundu.utils.Fog;

/*
 *
 */

public class GetCountriesRequest extends
        BaseRequest<GetCountriesRequest.OnCountriesRequestResult> implements Response.ErrorListener, Response.Listener<String> {

    private final String TAG = this.getClass().getSimpleName();
    private final Context context;
    private StringRequest request;

    public GetCountriesRequest(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public void start() {

        // change the api later
        request = new StringRequest(Request.Method.GET, API.COUNTRY_API, this, this) {
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
            callback.onCountriesError(error);
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
            callback.onCountriesResponse(response);
        }
    }

    public interface OnCountriesRequestResult {
        void onCountriesResponse(String response);
        void onCountriesError(VolleyError error);
    }


}
