package in.co.eko.fundu.requests;/*
 * Created by Bhuvnesh
 */

import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.ClientError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.utils.Fog;

public abstract class BaseRequest <T>{

    private Context context;
    protected T callback;

    public BaseRequest(Context context) {
        this.context = context;
    }

    protected abstract void start();
    protected abstract void stop();
    protected abstract String getTag();
    public Context getContext(){
        return context;
    }
    public Context getApplicationContext(){
        return context.getApplicationContext();
    }
    public void onVolleyErrorResponse(VolleyError error) {
        // Handle your error types accordingly.For Timeout & No connection error, you can show 'retry' button.
        // For AuthFailure, you can re login with user credentials.
        // For ClientError, 400 & 401, Errors happening on client side when sending api request.
        // In this case you can check how client is forming the api and debug accordingly.
        // For ServerError 5xx, you can do retry or handle accordingly.
        Fog.e("Error", error.toString());
        if( error instanceof NoConnectionError) {

            //Toast.makeText(getContext(), "Not connected to Fundu", Toast.LENGTH_SHORT).show();
            Fog.d("VolleyError "+getTag(), "NoConnectionError");
        } else if( error instanceof NetworkError) {
            //Toast.makeText(getContext(), "Network Error", Toast.LENGTH_SHORT).show();
            Fog.d("VolleyError "+getTag(), "NetworkError");
        } else if (error instanceof ClientError) {
           // Toast.makeText(getContext(), "ClientError", Toast.LENGTH_SHORT).show();
            Fog.d("VolleyError "+getTag(), "ClientError");

        } else if (error instanceof ServerError) {
            //Toast.makeText(getContext(), "Something went Wrong ", Toast.LENGTH_SHORT).show();
            Fog.d("VolleyError "+getTag(), "ServerError");
        } else if( error instanceof AuthFailureError) {
            //Toast.makeText(getContext(), "Auth Failure Error", Toast.LENGTH_SHORT).show();
            Fog.d("VolleyError "+getTag(), "AuthFailureError");
        } else if( error instanceof ParseError)  {
          //  Toast.makeText(getContext(), "ParseError", Toast.LENGTH_SHORT).show();
            Fog.d("VolleyError "+getTag(), "Parse Error");
        } else if( error instanceof TimeoutError) {
            Toast.makeText(getContext(), "Internet is too slow", Toast.LENGTH_SHORT).show();
            Fog.d("VolleyError "+getTag(), "TimeoutError");
        }
    }

    public void onVolleyResponse(String response) {
        Fog.d("VolleyResponse "+getTag(), response);
    }
    public void onVolleyResponse(JSONObject response) {
        Fog.d("VolleyResponse "+getTag(), response.toString());
    }
    public void onVolleyResponse(JSONArray response) {
        Fog.d("VolleyResponse "+getTag(), response.toString());
    }
    public Map<String, String> getFunduHeaders(){
        Map<String, String> headers = new HashMap<> ();
        headers.put("Authorization", Constants.PrivateKey.SWAGGER_AUTHENTICATION_KEY);
        headers.put("FunduAuthorization", FunduUser.getAuthToken());
        return headers;
    }
    public void setParserCallback(T t) {
        this.callback= t;
    }
}
