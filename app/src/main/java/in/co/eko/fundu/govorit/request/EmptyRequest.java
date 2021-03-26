package in.co.eko.fundu.govorit.request;

import in.co.eko.fundu.govorit.tools.NetworkResponse;
import in.co.eko.fundu.govorit.tools.Request;
import in.co.eko.fundu.govorit.tools.Response;

/**
 * Created by zartha on 3/15/18.
 */

public class EmptyRequest extends Request {


    public EmptyRequest(int method, String url){
        super(method,url);
        setWaitForResponse(false);

    }

    @Override
    protected Response parseNetworkResponse(NetworkResponse response) {
        return null;
    }

    @Override
    protected void deliverResponse(Response response) {

    }

}
