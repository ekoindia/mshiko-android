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
import in.co.eko.fundu.utils.Fog;

/**
 * Created by Rahul on 4/28/17.
 */

public class UpdateCustomer extends BaseRequest<UpdateCustomer.OnUpdateAccountNumberResults> implements Response.ErrorListener, Response.Listener<JSONObject> {

    private final String TAG = this.getClass().getSimpleName();
    private final Context context;
    private JsonObjectRequest request;
    private JSONObject object;

    public UpdateCustomer(Context context) {
        super(context);
        this.context = context;
    }

    public void setData(String[] keys,String[] value){
        JSONObject object = new JSONObject();
        for(int i=0;i<keys.length;i++){
            try {
                object.put(keys[i],value[i]);
            } catch(JSONException e) {
                e.printStackTrace();
            }
            catch(ArrayIndexOutOfBoundsException e){
                e.printStackTrace();
            }
        }
        setData(object);

    }

    public void setData(JSONObject info) {
        object = info;
        try{
            object.put("country_shortname", FunduUser.getAppPreferences().getString(Constants.COUNTRY_SHORTCODE));
            object.put("mobile", FunduUser.getAppPreferences().getString(Constants.PrefKey.CONTACT_NUMBER));
        }catch (JSONException e){
            e.printStackTrace();
        }


    }

    @Override
    public void start() {
        if (object == null) {
            throw new NullPointerException("Set contactId to start request");
        }
        Fog.e("Change FPin API", API.CHANGE_ACCOUNT_NO_API + " \nJson : " + object.toString());
        String url = String.format(API.CUSTOMER, FunduUser.getContactIDType(), FunduUser.getContactId());
        request = new JsonObjectRequest(Request.Method.PUT, url, object, this, this) {
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
            callback.onUpdateAccountNumberError(error);
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        onVolleyResponse(response);
        if (callback != null) {
            callback.onUpdateAccountNumberResponse(response);
        }
    }

    public interface OnUpdateAccountNumberResults {
        void onUpdateAccountNumberResponse(JSONObject response);

        void onUpdateAccountNumberError(VolleyError error);
    }


}
