package in.co.eko.fundu.requests;
/*
 * Created by Bhuvnesh
 */

import android.content.Context;
import android.os.AsyncTask;
import android.provider.Settings;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import in.co.eko.fundu.FunduApplication;
import in.co.eko.fundu.R;
import in.co.eko.fundu.constants.API;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.models.Contact;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.utils.AppPreferences;
import in.co.eko.fundu.utils.Fog;
import in.co.eko.fundu.utils.Utils;

/*
 *
 */

public class ContactCreateUpdateRequest extends BaseRequest<ContactCreateUpdateRequest.OnContactResults> implements Response.ErrorListener, Response.Listener<JSONObject> {

    private final String TAG = this.getClass().getSimpleName();
    private final Context context;
    private final AppPreferences preferences;
    private JsonObjectRequest request;
    private Contact contact;

    public ContactCreateUpdateRequest(Context context, AppPreferences preferences) {
        super(context);
        this.context = context;
        this.preferences = preferences;
    }

    @Override
    public void start() {

        new AsyncTask<Void, Boolean, String>() {
            @Override
            protected String doInBackground(Void... params) {
                FirebaseInstanceId instanceID = FirebaseInstanceId.getInstance();
                try {
                    String token = instanceID.getToken(context.getString(R.string.gcm_defaultSenderId),
                            FirebaseMessaging.INSTANCE_ID_SCOPE);
                    preferences.putString(Constants.GCM_TOKEN, token);
                    return token;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (s != null) {
                    JSONObject contactObject = null;
                    if (contact == null) {
                        throw new NullPointerException();
                    } else {
                        contact.setDeviceToken(s);
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        Gson gson = gsonBuilder.create();
                        String mContact = gson.toJson(contact);
                        try {
                            contactObject = new JSONObject(mContact);
                            //*** Remove location array form json object ***//
                            contactObject.remove("location");
                            contactObject.remove("autocashout");
                            contactObject.remove("verified");
                            contactObject.remove("deleted");
                            contactObject.remove("countryShortname");
                            if(!preferences.getString(Constants.PROFILE_PIC_URL).isEmpty())
                            contactObject.put("person_img_url",preferences.getString(Constants.PROFILE_PIC_URL));
                            contactObject.put("country_shortname", FunduUser.getCountryShortName());
                            contactObject.put("sim_number", Utils.Sim_number(context));
                            contactObject.put("imei_number", Utils.IMEI(context)); //+"1"
                            Fog.d("ContactData-->", String.valueOf(contactObject));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    request = new JsonObjectRequest(Request.Method.POST, API.CONTACT_API, contactObject, ContactCreateUpdateRequest.this, ContactCreateUpdateRequest.this) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {

                            return getFunduHeaders();
                        }
                    };
                    RetryPolicy policy = new DefaultRetryPolicy(Constants.REQUEST_TIMEOUT_TIME, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    request.setRetryPolicy(policy);
                    FunduApplication.getInstance().addToRequestQueue(request);
                } else {
                    if (callback != null) {
                        callback.onContactError(new NetworkError());
                    }
                }
            }
        }.execute();

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
            
            callback.onContactError(error);
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        onVolleyResponse(response);
        if (callback != null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            Contact contact = null;
            if (response.toString().contains("data")) {
                try {
                    contact = gson.fromJson(response.getJSONObject("data").toString(), Contact.class);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                callback.onContactResponse(contact);
                CheckBalanceRequest balanceRequest = new CheckBalanceRequest(context);
                balanceRequest.setData(contact.getContactId());
                balanceRequest.start();
            } else {
//                {"message":"sim number is mismatch","status":"ERROR"}
                VolleyError error = new VolleyError(response.optString("message"));
                callback.onContactError(error);
            }
        }
    }

    public void setContact() {
        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        //contact = mParam1;
        contact = FunduUser.getUser();
        contact.setGcmSenderId(context.getString(R.string.gcm_defaultSenderId));
        contact.setContactIdType("mobile_type");
        contact.setContactType("PERSON");
        contact.setDeviceType("android");
        contact.setDeviceId(androidId);
        // contact.setDeviceToken(preferences.getString(Constants.GCM_TOKEN));
        contact.setLocation(new Contact.Location(new double[]{preferences.getDouble(Constants.PrefKey.LATITUDE, 0), preferences.getDouble(Constants.PrefKey.LONGITUDE, 0)}));
    }

    public interface OnContactResults {
        void onContactResponse(Contact contact);

        void onContactError(VolleyError error);
    }
}
