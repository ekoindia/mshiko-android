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
import in.co.eko.fundu.utils.Fog;

/**
 * Created by user on 1/29/17.
 */

public class CheckAnswersResetPinRequest extends BaseRequest<CheckAnswersResetPinRequest.OnCheckAnswersResetPinResults> implements Response.ErrorListener, Response.Listener<JSONObject> {

    private final String TAG = this.getClass().getSimpleName();
    private final Context context;
    private JsonObjectRequest request;
    private String custid, question_id, answer, cardNumber, expiry;
    private JSONObject object;

    public CheckAnswersResetPinRequest(Context context) {
        super(context);
        this.context = context;
    }

    public void setData(String question_id, String answer, String cardNumber, String expiry, String userId) {

        this.custid = userId;
        this.question_id = question_id;
        this.answer = answer;
        this.cardNumber = cardNumber;
        this.expiry = expiry;

        object = new JSONObject();
        try {
            object.put("custid", this.custid);
            object.put("question_id", this.question_id);
            object.put("card_number", this.cardNumber);
            object.put("card_expiry", this.expiry);
            object.put("answer", this.answer);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void start() {
        if (object == null) {
            throw new NullPointerException("Set contactId to start request");
        }
        Fog.e("Change FPin API", API.CHECK_ANSWER_RESET_PIN_API+" \nJson : "+object.toString());
        request = new JsonObjectRequest(Request.Method.POST, API.CHECK_ANSWER_RESET_PIN_API, object, this, this) {
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
            callback.onCheckAnswersResetPinError(error);
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        onVolleyResponse(response);
        if (callback != null) {
            callback.onCheckAnswersResetPinResponse(response);
        }
    }

    public interface OnCheckAnswersResetPinResults {
        void onCheckAnswersResetPinResponse(JSONObject response);

        void onCheckAnswersResetPinError(VolleyError error);
    }


}
