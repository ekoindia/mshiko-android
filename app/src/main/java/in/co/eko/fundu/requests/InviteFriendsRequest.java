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
 * Created by zartha on 7/27/17.
 */

public class InviteFriendsRequest extends BaseRequest<InviteFriendsRequest.OnInviteFriendsResult>
        implements Response.ErrorListener, Response.Listener<JSONObject>{

    private String TAG = InviteFriendsRequest.class.getName();
    private Context context;
    private OnInviteFriendsResult listener;
    private JSONObject requestParams;
    private JsonObjectRequest request;
    public InviteFriendsRequest(Context context,OnInviteFriendsResult listener){
        super(context);
        this.context = context;
        this.listener = listener;
    }
    @Override
    public void start(){
        if (requestParams==null) {
            throw new NullPointerException("Set data before start");
        }
        Fog.i(TAG,requestParams.toString());
        String contactID = FunduUser.getContactId();
        String requestUrl = String.format(API.USER_CONTACTS, FunduUser.getContactIDType(), contactID) + "?invite=true&"
                + Constants.COUNTRY_CODE + "=" + FunduUser.getCountryShortName();
        request = new JsonObjectRequest(Request.Method.PUT, requestUrl, requestParams, this, this) {
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
    public void stop(){

    }
    @Override
    public void onResponse(JSONObject result){
        onVolleyResponse(result);
        if(listener != null)
            listener.onInviteFriendsResponse(result);
    }
    @Override
    public void onErrorResponse(VolleyError error){
        onVolleyErrorResponse(error);
        if(listener != null)
            listener.onInviteFriendsError(error);
    }
    public interface OnInviteFriendsResult {
        void onInviteFriendsResponse(JSONObject response);

        void onInviteFriendsError(VolleyError error);
    }
    @Override
    public String getTag(){
        return TAG;
    }

    public void setData(ArrayList<ContactItem> invitedContacts){
        try{
            requestParams = new JSONObject();
            requestParams.put(Constants.COUNTRY_SHORTCODE,FunduUser.getCountryShortName());
            JSONArray jsonArray = new JSONArray();
            for(ContactItem contactItem : invitedContacts){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(Constants.CONTACT_ID_TYPE, Constants.MOBILE_TYPE);
                if (contactItem.getContactNumber().contains("+")){
                    jsonObject.put(Constants.CONTACT_ID, contactItem.getContactNumber());
                }
                else{
                    jsonObject.put(Constants.CONTACT_ID, Utils.appendCountryCodeToNumber(getContext(), contactItem.getContactNumber()));
                }
                jsonArray.put(jsonObject);
            }
            requestParams.put(Constants.CONTACTS,jsonArray);
        }
        catch (JSONException e){
            e.printStackTrace();
        }

    }
}
