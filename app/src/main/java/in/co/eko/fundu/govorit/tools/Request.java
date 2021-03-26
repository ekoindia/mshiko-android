package in.co.eko.fundu.govorit.tools;


import java.util.Collections;
import java.util.Map;

/**
 * Created by zartha on 3/15/18.
 */

public abstract class Request<T>  {
    /**
     * Supported request methods.
     */
    public interface Method {
        int GET = 0;
        int POST = 1;
        int PUT = 2;
        int DELETE = 3;
        int HEAD = 4;
        int OPTIONS = 5;
        int TRACE = 6;
        int PATCH = 7;
    }

    /**
     * Request method of this request.  Currently supports GET, POST, PUT, DELETE, HEAD, OPTIONS,
     * TRACE, and PATCH.
     */
    private final int mMethod;

    /** URL of this request. */
    private final String mUrl;

    private boolean mCanceled;

    private boolean mWaitForResponse;

    private Response.ErrorListener mErrorListner;

    private RequestDispatcher dispatcher;


    public Map<String, String> getHeaders() {
        return Collections.emptyMap();
    }


    public Request(int method, String url) {
        this(method,url,null);

    }

    public int getMethod() {
        return mMethod;
    }

    public Request(int method, String url, Response.ErrorListener errorListener){
        this.mMethod = method;
        this.mUrl = url;
        this.mErrorListner = errorListener;
        this.mWaitForResponse = true;
        this.dispatcher = new RequestDispatcher();
        this.dispatcher.setRequest(this);
    }

    public String getUrl() {
        return mUrl;
    }

    abstract protected Response<T> parseNetworkResponse(NetworkResponse response);

    abstract protected void deliverResponse(Response response);
    public void deliverError(Error error){
        if(mErrorListner != null)
            mErrorListner.onErrorResponse(error);
    }

    public void cancel() {
        this.mCanceled = true;
    }

    public boolean isCanceled(){
        return this.mCanceled;
    }

    protected void setWaitForResponse(boolean mWaitForResponse) {
        this.mWaitForResponse = mWaitForResponse;
    }

    protected boolean isWaitForResponse() {
        return mWaitForResponse;
    }

    //TODO: Add each request in a queue and then execute from the queue
    public void execute(){
        dispatcher.start();
    }

}
