package in.co.eko.fundu.requests;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.co.eko.fundu.FunduApplication;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.utils.Fog;
import in.co.eko.fundu.views.CustomProgressDialog;

/**
 * Created by Divyanshu jain on 30-10-2015.
 */
public class CallWebService implements Response.ErrorListener, Response.Listener {

    private static Context context = null;

    private static CallWebService instance = null;

    private static CustomProgressDialog progressDialog = null;

    public static int GET = Request.Method.GET;
    public static int POST = Request.Method.POST;
    public static int PUT = Request.Method.PUT;
    public static int DELETE = Request.Method.DELETE;
    private ObjectResponseCallBack objectCallBackInterface;
    private ArrayResponseCallback arrayCallBackInterface;
    private static int apiCode = 0;
    private String url;

    public static CallWebService getInstance(Context context, boolean showProgressBar, int apiCode) {
        CallWebService.context = context;
        if (context != null && showProgressBar)
            progressDialog = new CustomProgressDialog(context);
        else
            progressDialog = null;

        if (instance == null) {
            instance = new CallWebService();
        }
        CallWebService.apiCode = apiCode;
        return instance;
    }

    public void hitJsonObjectRequestAPI(int requestType, final String url, JSONObject json, final ObjectResponseCallBack callBackinerface) {
        objectCallBackInterface = callBackinerface;
        //Fog.e("JSON INVITE", url+" - JSON - "+json.toString());
        Fog.e("LinkAccount", "hitJsonObjectRequestAPI");
        this.url = url;
        if (progressDialog != null)
            progressDialog.show();

        JsonObjectRequest request = new JsonObjectRequest(requestType, url, json == null ? null : (json), this, this) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
               return getFunduHeaders();
            }
        };
        Fog.d("CallWebService","CallWebService"+url+request);
        addRequestToVolleyQueue(url, request);
    }

    public void hitJsonArrayRequestAPI(int requestType, final String url, JSONArray json, final ArrayResponseCallback callBackinerface) {
        arrayCallBackInterface = callBackinerface;
        Fog.e("LinkAccount", "hitJsonArrayRequestAPI");
        this.url = url;
        if (progressDialog != null)
            progressDialog.show();

        JsonArrayRequest request = new JsonArrayRequest(requestType, url, json == null ? null : (json), this, this) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
               return getFunduHeaders();
            }
        };
        addRequestToVolleyQueue(url, request);
    }

    private void addRequestToVolleyQueue(String url, Request request) {
        RetryPolicy policy = new DefaultRetryPolicy(Constants.REQUEST_TIMEOUT_TIME, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        FunduApplication.getInstance().addToRequestQueue(request, url);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Fog.e("LinkAccount", "onErrorResponse");
        progressDialog.dismiss();
        FunduApplication.getInstance().getRequestQueue().getCache().invalidate(url, true);
        error = configureErrorMessage(error);
        Fog.d("CallWebService","error"+error.getMessage());
        onError(error.getMessage());
    }


    @Override
    public void onResponse(Object response) {
        Fog.d("LinkAccount","onResponse"+response.toString());
        FunduApplication.getInstance().getRequestQueue().getCache().invalidate(url, true);
        if (progressDialog != null)
            progressDialog.dismiss();

        if (response instanceof JSONObject) {
            onJsonObjectResponse((JSONObject) response);
        } else if (response instanceof JSONArray) {
            onJsonArrayResponse((JSONArray) response);
        }

    }


    private void onJsonObjectResponse(JSONObject response) {
        Fog.d("LinkAccount","onJsonObjectResponse"+response.toString());
        try {
            progressDialog.dismiss();
            Fog.d("CallWebService","onJsonObjectSuccess"+response.toString());
            objectCallBackInterface.onJsonObjectSuccess(response, apiCode);
        } catch (final JSONException e) {
            Fog.d("CallWebService","JSONException"+e);
            onError(e.getMessage());
            e.printStackTrace();
        }
    }

    private void onJsonArrayResponse(JSONArray response) {
        Fog.d("LinkAccount","onJsonArrayResponse"+response.toString());
        try {
            progressDialog.dismiss();
            Fog.d("CallWebService","JSONArray"+response.toString());
            arrayCallBackInterface.onJsonArraySuccess(response, apiCode);
        } catch (final JSONException e) {
            Fog.d("CallWebService","JSONException"+e);
            onError(e.getMessage());
            e.printStackTrace();
        }
    }

    public interface ObjectResponseCallBack {
        void onJsonObjectSuccess(JSONObject response, int apiType) throws JSONException;

        void onFailure(String str, int apiType);
    }

    public interface ArrayResponseCallback {
        void onJsonArraySuccess(JSONArray array, int apiType) throws JSONException;

        void onFailure(String str, int apiType);
    }

    private void onError(String error) {
        Fog.d("LinkAccount","onError"+error);
        objectCallBackInterface.onFailure(error, apiCode);
    }


    private VolleyError configureErrorMessage(VolleyError volleyError) {
        Fog.d("LinkAccount","volleyError"+volleyError);
       // progressDialog.dismiss();
        Toast.makeText(context, "There seems to be a problem processing your request. Please try in sometimes.", Toast.LENGTH_SHORT).show();
        if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
            VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
            Fog.d("CallWebService","volleyError"+error);
            volleyError = error;
        }
        return volleyError;
    }
    public Map<String, String> getFunduHeaders(){
        Map<String, String> headers = new HashMap<> ();
        headers.put("Authorization", Constants.PrivateKey.SWAGGER_AUTHENTICATION_KEY);
        headers.put("FunduAuthorization", FunduUser.getAuthToken ());
        return headers;
    }
}