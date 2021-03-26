package in.co.eko.fundu.interfaces;
/*
 * Created by Bhuvnesh
 */

import com.android.volley.VolleyError;

public interface OnApiResults <T>{
    void onApiResponse(T t);
    void onApiError(VolleyError error);
}