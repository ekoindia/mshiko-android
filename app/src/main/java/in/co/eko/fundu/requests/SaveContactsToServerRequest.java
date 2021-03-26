package in.co.eko.fundu.requests;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import in.co.eko.fundu.FunduApplication;
import in.co.eko.fundu.constants.API;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.models.ContactItem;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.utils.Fog;
import in.co.eko.fundu.utils.Utils;

/**
 * Created by divyanshu.jain on 7/5/2016.
 */
public class SaveContactsToServerRequest extends BaseRequest<SaveContactsToServerRequest.OnSaveContactsResults> implements Response.Listener<JSONObject>, Response.ErrorListener {


    private JSONObject contactsObject;
    private JsonObjectRequest request;

    private final String TAG = this.getClass().getSimpleName();

    public SaveContactsToServerRequest(Context context) {
        super(context);
    }

    public void setData(ArrayList<ContactItem> contactItems1, String device_id, String countryCode) {

        try {

            ArrayList<ContactItem> contactItems = new ArrayList<>(contactItems1);
            contactsObject = new JSONObject();
            JSONArray contactsArray = new JSONArray();
            Fog.d("contactItem","contactItem"+contactItems.size());
            for (ContactItem contactItem : contactItems) {

                JSONObject jsonObject = new JSONObject();
                jsonObject.put(Constants.CONTACT_ID_TYPE, Constants.MOBILE_TYPE);
                    if (/*contactItem.getContactNumber().startsWith(country_code) ||*/ contactItem.getContactNumber().contains("+")){
                        jsonObject.put(Constants.CONTACT_ID, contactItem.getContactNumber());
                }
                else{
                    jsonObject.put(Constants.CONTACT_ID, Utils.appendCountryCodeToNumber(getContext(), contactItem.getContactNumber()));
                }
                boolean deleted = false;
                if (contactItem.isDeleted() == 1)
                    deleted = true;
                jsonObject.put(Constants.DELETED, deleted);
                contactsArray.put(jsonObject);
            }
            contactsObject.put(Constants.CONTACTS, contactsArray);
            contactsObject.put(Constants.DEVICE_ID, device_id);
            contactsObject.put(Constants.VERSION_CODE, Utils.getVersionCode());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        if (contactsObject == null) {
            throw new NullPointerException("Set JSON object to request");
        }
        String id = FunduUser.getContactId();
        String id_type = FunduUser.getContactIDType();
        request = new JsonObjectRequest(Request.Method.POST,
                String.format(API.USER_CONTACTS+"/"+FunduUser.getCountryShortName()+API.LOCALE, id_type, id),
                contactsObject, this, this) {
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
    protected void stop() {

    }

    @Override
    protected String getTag() {
        return TAG;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        if(error.networkResponse != null) {
            String message = new String ( error.networkResponse.data );
        }
        callback.onSaveContactsError(error);
    }

    @Override
    public void onResponse(JSONObject response) {
        Fog.i("Contact onResponse*********",response.toString());
        callback.onSaveContactsResponse(response);
    }

    public interface OnSaveContactsResults {
        void onSaveContactsResponse(JSONObject response);

        void onSaveContactsError(VolleyError error);
    }
}
