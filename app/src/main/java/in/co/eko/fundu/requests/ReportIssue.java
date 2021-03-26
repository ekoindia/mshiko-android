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

/**
 * Created by Rahul on 4/28/17.
 */

public class ReportIssue extends BaseRequest<ReportIssue.OnReportIssueResult> implements Response.ErrorListener, Response.Listener<JSONObject> {

    private final String TAG = this.getClass().getSimpleName();
    private JsonObjectRequest request;
    private JSONObject object;

    public ReportIssue(Context context) {
        super(context);
    }


    public void setData(String summary, String description) {
        object = new JSONObject();
        try{
            object.put("summary", summary);
            object.put("description", description);
            object.put("country_shortname", FunduUser.getCountryShortName());

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        if (object == null) {
            throw new NullPointerException("Set contactId to start request");
        }
        String url = String.format(API.REPORT_ISSUE, FunduUser.getContactIDType(), FunduUser.getContactId());
        request = new JsonObjectRequest(Request.Method.POST, url, object, this, this) {
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
            callback.onReportIssueError(error);
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        onVolleyResponse(response);
        if (callback != null) {
            callback.onReportIssueResponse(response);
        }
    }
    public interface OnReportIssueResult {
        void onReportIssueResponse(JSONObject response);
        void onReportIssueError(VolleyError error);
    }



}
