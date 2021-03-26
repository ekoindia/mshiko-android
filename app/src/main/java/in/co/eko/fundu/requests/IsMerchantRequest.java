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
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.utils.Fog;

/**
 * Created by Rahul on 5/23/17.
 */

public class IsMerchantRequest extends BaseRequest<IsMerchantRequest.OnIsMerchantRequestResults> implements Response.ErrorListener, Response.Listener<String> {

    private final String TAG = this.getClass().getSimpleName();
    private final Context context;
    private StringRequest request;

    public IsMerchantRequest(Context context) {
        super(context);
        this.context = context;
    }


    @Override
    public void start() {
        if (FunduUser.getContactId() == null) {
            throw new NullPointerException("Set contactId to start request");
        }
        Fog.e("IsMerchant API", String.format(API.ISMERCHANT, FunduUser.getContactIDType(), FunduUser.getContactId()));
        request = new StringRequest(Request.Method.GET, String.format(API.ISMERCHANT, FunduUser.getContactIDType(), FunduUser.getContactId()), this, this) {
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
            callback.onIsMerchantRequestError(error);
        }
    }

    @Override
    public void onResponse(String response) {
        onVolleyResponse(response);
//        try {
//            JSONObject jsonObject = new JSONObject(response);
//            String walletAmount = jsonObject.optString("Balance Amount");
//            FunduUser.setWalletAmount(walletAmount);
//            Intent intent = new Intent(Constants.HOME_ACTIVITY_ACTION);
//            intent.putExtra(Constants.UPDATED_AMOUNT, walletAmount);
//            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Fog.d(TAG, "Exception - Due to unexpected key-value");
//        }
        if (callback != null) {
            callback.onIsMerchantRequestResponse(response);
        }
    }

    public interface OnIsMerchantRequestResults {
        void onIsMerchantRequestResponse(String response);

        void onIsMerchantRequestError(VolleyError error);
    }


}
