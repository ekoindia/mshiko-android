package in.co.eko.fundu.requests;
/*
 * Created by Bhuvnesh
 */

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
import in.co.eko.fundu.R;
import in.co.eko.fundu.constants.API;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.utils.Fog;

/*
 *
 */

public class TransactionInitiateRequest extends BaseRequest<TransactionInitiateRequest.OnTransactionInitiateResults> implements Response.ErrorListener, Response.Listener<JSONObject> {

    private final String TAG = this.getClass().getSimpleName();
    private final Context context;
    private JsonObjectRequest request;
    private JSONObject transactionObject;

    public TransactionInitiateRequest(Context context) {
        super(context);
        this.context = context;
    }

    /**
     *
     * @param alias
     * @param sender_id
     * @param sender_id_type
     * @param recipient_id
     * @param recipient_id_type
     * @param amount
     * @param hold_timeout
     */

    public void setData(String alias, String sender_id, String sender_id_type, String recipient_id, String recipient_id_type, int amount, int hold_timeout, String recipientMobile, String provider_charge,String fee,String pairRequestId) {
        transactionObject = new JSONObject();
        try {
            if (FunduUser.getCountryShortName().equals("IND"))
            transactionObject.put("currency", "INR");
            else
            transactionObject.put("currency", "KSH");
            transactionObject.put("fee", fee);
            transactionObject.put("provider_charge", provider_charge);
            transactionObject.put("amount", amount);
            transactionObject.put("hold_timeout", hold_timeout);
            transactionObject.put("request_id",pairRequestId);

            JSONObject senderObject = new JSONObject();
            senderObject.put("alias", alias);
            senderObject.put("id_type", sender_id_type);
            senderObject.put("id", sender_id);

            JSONObject recipientObject = new JSONObject();
            recipientObject.put("alias", alias);
            recipientObject.put("id_type", recipient_id_type);
            recipientObject.put("id", recipientMobile);
            if(recipient_id == null)
                recipient_id = "10301";
            recipientObject.put("recipient_id",recipient_id);
            transactionObject.put("sender", senderObject);
            transactionObject.put("recipient", recipientObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setDataForSendMoneyToAccount(String sender_id, String sender_id_type, String recipient_id, String recipient_id_type, int amount, int hold_timeout, int recipient_number) {
        transactionObject = new JSONObject();
        try {
            if (FunduUser.getCountryShortName().equals("IND"))
                transactionObject.put("currency", "INR");
            else
            transactionObject.put("currency", "KSH");
            transactionObject.put("fee", 0);
            transactionObject.put("amount", amount);
            transactionObject.put("hold_timeout", hold_timeout);

            JSONObject senderObject = new JSONObject();
            senderObject.put("alias", context.getString(R.string.wallet));
            senderObject.put("id_type", sender_id_type);
            senderObject.put("id", sender_id);

            JSONObject recipientObject = new JSONObject();
            recipientObject.put("alias", context.getString(R.string.account));
            recipientObject.put("id_type", recipient_id_type);
            recipientObject.put("id", recipient_id);

            transactionObject.put("recipient_id", recipient_number);
            transactionObject.put("sender", senderObject);
            transactionObject.put("recipient", recipientObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        if (transactionObject == null) {
            throw new NullPointerException("Set JSON object to request");
        }
        Fog.d(TAG, transactionObject.toString());
        request = new JsonObjectRequest(Request.Method.POST, String.format(API.TRANSACTION_INITIATE, FunduUser.getContactIDType(), FunduUser.getContactId()), transactionObject, this, this) {
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
    public void onErrorResponse(VolleyError volleyError) {
        if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
            VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
            volleyError = error;
        }
        onVolleyErrorResponse(volleyError);
        if (callback != null) {
            try {
                JSONObject jsonObject = new JSONObject(volleyError.getMessage());
                String message = jsonObject.getJSONArray("errors").getString(0);
                Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            callback.onTransactionInitiateError(volleyError);
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        onVolleyResponse(response);
        if (callback != null) {
            callback.onTransactionInitiateResponse(response);
        }
    }

    public interface OnTransactionInitiateResults {
        void onTransactionInitiateResponse(JSONObject response);

        void onTransactionInitiateError(VolleyError error);
    }


}
