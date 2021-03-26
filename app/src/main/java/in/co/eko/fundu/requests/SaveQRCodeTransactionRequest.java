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
 * Created by zartha on 4/12/18.
 */

public class SaveQRCodeTransactionRequest extends BaseRequest implements Response.ErrorListener, Response.Listener<JSONObject>{
    private String TAG = this.getClass().getName();
    private JSONObject mParams;
    private JsonObjectRequest request;

    public SaveQRCodeTransactionRequest(Context context){
        super(context);

    }

    public void setData(String orderId, String amount,JSONObject status){
        mParams = new JSONObject();
        try {
            mParams.put("transaction_id",orderId);
            mParams.put("amount",amount);
            mParams.put("contact_id",FunduUser.getContactId());
            mParams.put("recipient_id","merchant");
            mParams.put("country_shortname", FunduUser.getCountryShortName());
            mParams.put("status",status);
        }catch(JSONException e){
            e.printStackTrace();
        }
    }


    @Override
    public void start() {
        if (mParams==null) {
            throw new NullPointerException("Set data before start");
        }
        Fog.i(TAG,mParams.toString());

        String requestUrl = String.format(API.TRANSACTION, FunduUser.getContactIDType(), FunduUser.getContactId(),"qrcode");
        request = new JsonObjectRequest(Request.Method.POST, requestUrl, mParams, this, this) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getFunduHeaders();
            }
        };
        RetryPolicy policy = new DefaultRetryPolicy(Constants.REQUEST_TIMEOUT_TIME, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        FunduApplication.getInstance().addToRequestQueue(request);
    }

    @Override
    protected void stop() {

    }

    @Override
    protected String getTag() {
        return null;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        onVolleyErrorResponse(error);

    }

    @Override
    public void onResponse(JSONObject response) {
        onVolleyResponse(response);

    }
}
