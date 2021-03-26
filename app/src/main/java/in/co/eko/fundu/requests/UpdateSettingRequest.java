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
import in.co.eko.fundu.models.UpdateSettingItem;
import in.co.eko.fundu.parser.UniversalParser;
import in.co.eko.fundu.utils.AppPreferences;
import in.co.eko.fundu.utils.Fog;
import in.co.eko.fundu.utils.Utils;

/**
 * Created by divyanshu.jain on 7/20/2016.
 */
public class UpdateSettingRequest extends BaseRequest<UpdateSettingRequest.OnUpdateSettingResults> implements Response.ErrorListener, Response.Listener<JSONObject> {
    JSONObject jsonObject = null;
    private final String TAG = this.getClass().getSimpleName();
    private JsonObjectRequest request;
    private int requestType;

    public UpdateSettingRequest(Context context) {
        super(context);
    }

    public void setData(boolean autoCashout, boolean notification, boolean shareLocation, int requestType) {
        this.requestType = requestType;
        jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.SettingsPref.AUTOCASHOUT, autoCashout);
            jsonObject.put(Constants.SettingsPref.NOTIFICATION, notification);
            jsonObject.put(Constants.SettingsPref.SHARE_LOCATION, shareLocation);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        if (requestType != Request.Method.GET && jsonObject == null) {
            throw new NullPointerException("Set new settings");
        }
        String contactID = Utils.appendCountryCodeToNumber(getContext(), FunduUser.getContactId());
        request = new JsonObjectRequest(requestType, String.format(API.UPDATE_AND_POST_SETTING, FunduUser.getContactIDType(), contactID), jsonObject, this, this) {
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

    }

    @Override
    protected String getTag() {
        return TAG;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        saveSettingPreference(true, true, true);
        //Fog.e(TAG, error.getMessage());
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            Fog.i(TAG, response.getString("status"));
            UpdateSettingItem updateSettingItem = UniversalParser.getInstance().parseJsonObject(response.getJSONObject("data"), UpdateSettingItem.class);
            saveSettingPreference(updateSettingItem.isAuto_cash_out(), updateSettingItem.isNotification(), updateSettingItem.isShare_location());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public interface OnUpdateSettingResults {
        void onUpdateSettingResponse(JSONObject jsonObject);

        void onUpdateSettingError(VolleyError error);
    }

    private void saveSettingPreference(boolean autoCashout, boolean notification, boolean shareLocation) {
        AppPreferences pref = FunduUser.getAppPreferences();
        pref.putBoolean(Constants.SettingsPref.AUTOCASHOUT, autoCashout);
        pref.putBoolean(Constants.SettingsPref.NOTIFICATION, notification);
        pref.putBoolean(Constants.SettingsPref.SHARE_LOCATION, shareLocation);
    }
}