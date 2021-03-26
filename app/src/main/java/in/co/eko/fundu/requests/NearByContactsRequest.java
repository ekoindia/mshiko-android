package in.co.eko.fundu.requests;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import in.co.eko.fundu.FunduApplication;
import in.co.eko.fundu.constants.API;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.utils.Fog;

/**
 * Created by divyanshu.jain on 7/6/2016.
 */
public class NearByContactsRequest extends BaseRequest<NearByContactsRequest.OnNearByContactsResults> implements Response.Listener<String>, Response.ErrorListener {
    private final String TAG = this.getClass().getSimpleName();
    private Context context;
    private StringRequest request;

    public NearByContactsRequest(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public void start() {
        String id = FunduUser.getContactId();
        String id_type = FunduUser.getContactIDType();
        Fog.e("Nearby Contacts", String.format(API.GET_NEAR_BY_CONTACTS, id_type, id));
        request = new StringRequest(Request.Method.GET, String.format(API.GET_NEAR_BY_CONTACTS, id_type, id), this, this) {
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
    protected void stop() {

    }

    @Override
    protected String getTag() {
        return TAG;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        callback.onNearByContactsError(error);
    }

    @Override
    public void onResponse(String response) {
        try {
            Fog.e("NEIGHBOUR CONT", response);
            callback.onNearByContactsResponse(new JSONObject(response));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public interface OnNearByContactsResults {
        void onNearByContactsResponse(JSONObject response);
        void onNearByContactsError(VolleyError error);
    }
}
