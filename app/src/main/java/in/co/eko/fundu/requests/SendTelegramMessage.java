package in.co.eko.fundu.requests;

/*
 * Created by Bhuvnesh
 */

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
import in.co.eko.fundu.constants.Constants;

/*
 *
 */

public class SendTelegramMessage extends
        BaseRequest implements Response.ErrorListener, Response.Listener<String> {

    private final String TAG = this.getClass().getSimpleName();
    private StringRequest request;
    private String text;

    public SendTelegramMessage(Context context) {
        super(context);

    }

    public void setText(String text) {
        this.text = text;

    }

    @Override
    public void start() {
        if(text == null){
            return;
        }
        // change the api later
        String url = "https://api.telegram.org/bot534778280:AAH66zhDZf7LFmJ6h5Q6IAt5IkCh7ZBYv98/sendMessage?chat_id="+Constants.telegramChatId+"&text="+text;
        request = new StringRequest(Request.Method.GET,url, this, this) {
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

    }

    @Override
    public void onResponse(String response) {
        onVolleyResponse(response);


    }



}
