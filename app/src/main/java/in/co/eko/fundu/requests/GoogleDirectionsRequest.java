package in.co.eko.fundu.requests;
/*
 * Created by Bhuvnesh
 */

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

public class GoogleDirectionsRequest extends StringRequest  {

    public GoogleDirectionsRequest(String googleDirectionAPI, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(googleDirectionAPI, listener, errorListener);

    }


}
