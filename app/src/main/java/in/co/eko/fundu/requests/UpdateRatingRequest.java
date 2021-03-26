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
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.Map;

import in.co.eko.fundu.FunduApplication;
import in.co.eko.fundu.constants.API;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.utils.Fog;

/*
 *
 */

public class UpdateRatingRequest extends BaseRequest<UpdateRatingRequest.OnUpdateRatingResults> implements Response.ErrorListener, Response.Listener<JSONObject> {

    private final String TAG = this.getClass().getSimpleName();
    private final Context context;
    private JsonObjectRequest request;
    private JSONObject object;
    private String ratedToID = "";

    public UpdateRatingRequest(Context context) {
        super(context);
        this.context = context;
    }

    public void setData(JSONObject object, String ratedToID) {
        this.object = object;
        this.ratedToID = ratedToID;
    }

    @Override
    public void start() {
        if (object == null) {
            throw new NullPointerException("Set contactId  and rating to start request");
        }

        Fog.e("Rating",String.format(API.UPDATE_RATING, FunduUser.getContactIDType(), ratedToID)+" \nObject = "+object.toString());
        request = new JsonObjectRequest(Request.Method.POST, String.format(API.UPDATE_RATING, FunduUser.getContactIDType(), ratedToID), object, this, this) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
               return getFunduHeaders();
            }
        };
        RetryPolicy policy = new DefaultRetryPolicy(Constants.REQUEST_TIMEOUT_TIME, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        Fog.d("request",""+request);
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
            callback.onUpdateRatingError(error);
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        onVolleyResponse(response);
        if (callback != null) {
            callback.onUpdateRatingResponse(response);
        }
    }
    public interface OnUpdateRatingResults {
        void onUpdateRatingResponse(JSONObject response);
        void onUpdateRatingError(VolleyError error);
    }
}
