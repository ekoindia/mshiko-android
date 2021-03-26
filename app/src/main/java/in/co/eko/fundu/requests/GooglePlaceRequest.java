package in.co.eko.fundu.requests;/*
 * Created by Bhuvnesh
 */

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

public class GooglePlaceRequest extends StringRequest {

    public GooglePlaceRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(url, listener, errorListener);

    }
}
