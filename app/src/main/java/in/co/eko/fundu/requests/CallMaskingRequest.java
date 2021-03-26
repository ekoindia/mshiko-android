package in.co.eko.fundu.requests;

import android.content.Context;
import android.widget.Toast;

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
import in.co.eko.fundu.utils.Fog;

/**
 * Created by pallavi on 4/12/17.
 */

public class CallMaskingRequest extends BaseRequest<CallMaskingRequest.OnCallMaskingRequestResult>
        implements Response.ErrorListener, Response.Listener<JSONObject> {



    private final String TAG = this.getClass().getSimpleName();
    private JsonObjectRequest request;
    private JSONObject jsonObject;
    private final Context context;

    public CallMaskingRequest(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public void start() {

        if (jsonObject==null) {
            throw new NullPointerException("Set data before start");
        }

        Fog.e(TAG,"Call masking data:"+ jsonObject.toString());
        if (request != null) {
            request.cancel();
        }

        request = new JsonObjectRequest(Request.Method.POST, String.format(API.MASK_CALL, FunduUser.getContactId()), jsonObject, this, this) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
              return getFunduHeaders ();

            }


        };
        RetryPolicy policy = new DefaultRetryPolicy(Constants.REQUEST_TIMEOUT_TIME, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        FunduApplication.getInstance().addToRequestQueue(request);
    }

    @Override
    protected void stop() {
        if (request != null) {
            request.cancel();
        }
    }

    @Override
    protected String getTag() {
        return null;
    }

    @Override
    public void onResponse(JSONObject response) {
        Fog.d("response",""+response);
        callback.onCallMaskingResponse(response);

    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {

        onVolleyErrorResponse(volleyError);
        if (callback != null) {
            try {
                JSONObject jsonObject = new JSONObject(volleyError.getMessage());
                String message = jsonObject.getJSONArray("errors").getString(0);
                Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            callback.onCallMaskingError(volleyError);
        }
    }

    public void setData(String transaction_id) {
        jsonObject = new JSONObject();
        try {

            jsonObject.put("transaction_id", transaction_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public interface OnCallMaskingRequestResult {
        void onCallMaskingResponse(JSONObject object);

        void onCallMaskingError(VolleyError error);
    }

}
