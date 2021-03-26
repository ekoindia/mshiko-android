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

public class GetCampaignDataRequest extends
        BaseRequest<GetCampaignDataRequest.OnGetCampaignDataRequestResult>
        implements Response.ErrorListener, Response.Listener<String> {

    private final String TAG = this.getClass().getSimpleName();
    private final Context context;
    private StringRequest request;

    public GetCampaignDataRequest(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public void start() {
        request = new StringRequest(Request.Method.GET, API.CAMPAIGN_API, this, this) {
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
    public void onResponse(String response) {
        onVolleyResponse(response);
        Fog.e("Campaign",response);

        if (callback != null) {
            callback.onCampaignDataResponse(response);
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        onVolleyErrorResponse(error);
        if (callback != null) {
            callback.onCampaignDataError(error);
        }
    }

    public interface OnGetCampaignDataRequestResult {
        void onCampaignDataResponse(String response);
        void onCampaignDataError(VolleyError error);
    }
}
