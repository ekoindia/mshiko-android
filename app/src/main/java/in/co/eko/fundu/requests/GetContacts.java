package in.co.eko.fundu.requests;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

import java.util.Map;

import in.co.eko.fundu.FunduApplication;
import in.co.eko.fundu.constants.API;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.utils.Fog;

/**
 * Created by zartha on 8/16/17.
 */

public class GetContacts extends
        BaseRequest<GetContacts.OnContactRequestResult> implements Response.ErrorListener, Response.Listener<JSONArray> {

    private final String TAG = this.getClass().getSimpleName();
    private final Context context;
    private JsonArrayRequest request;
    private String type;


    public GetContacts(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public void start() {
        // if (contactId == null) {
        //   throw new NullPointerException("Set contactId to start request");
        //}
        // change the api later

        String id = FunduUser.getContactId();
        String id_type = FunduUser.getContactIDType();
        String url = String.format(API.CONTACTS_STATUS,id_type,id,type);
        request = new JsonArrayRequest(Request.Method.GET, url, null, this,this) {
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
            callback.onContactsError(error);
        }
    }

    @Override
    public void onResponse(JSONArray response) {
        onVolleyResponse(response);
        Fog.e("Contacts onResponse for "+type,response.toString());

        if (callback != null) {
            callback.onContactsResponse(response);
        }
    }

    public interface OnContactRequestResult {
        void onContactsResponse(JSONArray response);
        void onContactsError(VolleyError error);
    }

    public void setData(String type){
        this.type = type;
    }
}
