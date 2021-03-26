package in.co.eko.fundu.requests;


import java.util.HashMap;
import java.util.Map;

import in.co.eko.fundu.constants.API;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.govorit.request.EmptyRequest;
import in.co.eko.fundu.govorit.tools.Request;
import in.co.eko.fundu.models.FunduUser;

/**
 * Created by zartha on 3/15/18.
 */

public class LocationPulse {
    private EmptyRequest request;

    public void start(){
        String url = String.format(API.UPDATE_LOCATION, FunduUser.getContactIDType(),
                FunduUser.getContactId());

        request = new EmptyRequest(Request.Method.PATCH,url){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", Constants.PrivateKey.SWAGGER_AUTHENTICATION_KEY);
                return headers;
            }
        }
        ;
        request.execute();
    }

    public void stop(){
        if(request != null)
            request.cancel();
    }
}
