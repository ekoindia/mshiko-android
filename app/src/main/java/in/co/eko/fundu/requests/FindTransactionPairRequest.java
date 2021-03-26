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
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import in.co.eko.fundu.FunduApplication;
import in.co.eko.fundu.constants.API;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.utils.AppPreferences;
import in.co.eko.fundu.utils.Fog;

/*
 *
 */

public class FindTransactionPairRequest extends BaseRequest<FindTransactionPairRequest.OnFindTransactionPairResults> implements Response.ErrorListener, Response.Listener<JSONObject> {

    private final String TAG = this.getClass().getSimpleName();
    private final Context context;
    private final AppPreferences preferences;
    private int amount;

    private JsonObjectRequest request;
    private JSONObject mRequestObject;

    public FindTransactionPairRequest(Context context, AppPreferences preferences, int amount) {
        super(context);
        this.context = context;
        this.preferences = preferences;
        this.amount = amount;
        Fog.e("Amount", String.valueOf(amount));
    }

    public void setData(String alias, String sender_id, String sender_id_type,
                        String recipient_id, String recipient_id_type, double amount,
                        int hold_timeout, boolean fromMap, String fee, LatLng requestLocation) {
        mRequestObject = new JSONObject();
        try {
            //            if (FunduUser.getCountryShortName().equals("IND"))

            if(requestLocation != null){
                JSONObject location = new JSONObject();
                JSONArray locationArray = new JSONArray();
                locationArray.put(requestLocation.longitude);
                locationArray.put(requestLocation.latitude);
                location.put("coordinates",locationArray);
                mRequestObject.put("location",location);
            }

            mRequestObject.put("currency", "INR");


            mRequestObject.put("fee", fee);
            mRequestObject.put("amount", amount);
            mRequestObject.put("hold_timeout", hold_timeout);

            JSONObject senderObject = new JSONObject();
            senderObject.put("alias", alias);
            senderObject.put("id_type", sender_id_type);
            senderObject.put("id", sender_id);

            if (!fromMap) {
                JSONObject recipientObject = new JSONObject();
                recipientObject.put("alias", alias);
                recipientObject.put("id_type", recipient_id_type);
                recipientObject.put("id", recipient_id);
                mRequestObject.put("recipient", recipientObject);
            }
            mRequestObject.put("sender", senderObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
      /*  JSONObject contactObject = new JSONObject();
        Fog.d(TAG, "latitude:" + latitude + ", longitude:" + longitude);
        try {
            contactObject.put("contact_id", FunduUser.getContactId());
            contactObject.put("latitude", latitude);
            contactObject.put("longitude", longitude);
            contactObject.put("amount", Integer.parseInt(amount));
            contactObject.put("fee", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        Fog.d(TAG, mRequestObject.toString());
        Fog.e(TAG,String.format(API.FIND_TRANSACTION_PAIR_API, FunduUser.getContactIDType(),FunduUser.getContactId()));
        request = new JsonObjectRequest(Request.Method.POST, String.format(API.FIND_TRANSACTION_PAIR_API, FunduUser.getContactIDType(),FunduUser.getContactId()), mRequestObject, this, this) {
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
            callback.onFindTransactionPairError(error);
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        onVolleyResponse(response);
        preferences.putDouble(Constants.PrefKey.NEED_AMOUNT, amount);
        if (callback != null) {
            callback.onFindTransactionPairResponse(response);
        }
    }


    public interface OnFindTransactionPairResults {
        void onFindTransactionPairResponse(JSONObject contact);
        void onFindTransactionPairError(VolleyError error);
    }
}
