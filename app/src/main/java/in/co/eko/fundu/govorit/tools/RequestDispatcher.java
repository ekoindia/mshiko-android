package in.co.eko.fundu.govorit.tools;

import android.os.Process;
import android.os.SystemClock;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;


/**
 * Created by zartha on 3/15/18.
 */

public class RequestDispatcher extends Thread {

    //TODO Implement queue later
    Request request;

    @Override
    public void run() {

        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        while (true) {
            long startTimeMs = SystemClock.elapsedRealtime();
            try {
                if (request.isCanceled()) {
                    break;
                }
                // Perform the network request.
                NetworkResponse networkResponse = performRequest(request);
                if(!request.isWaitForResponse()){
                    return;
                }
                // Parse the response here on the worker thread.
                Response<?> response = request.parseNetworkResponse(networkResponse);
                request.deliverResponse(response);

            } catch (Error error) {
                error.setNetworkTimeMs(SystemClock.elapsedRealtime() - startTimeMs);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public NetworkResponse performRequest(Request request) throws Error{
        NetworkResponse networkResponse = new NetworkResponse();

        try{

            String url = request.getUrl();
            HashMap<String, String> map = new HashMap<String, String>();
            map.putAll(request.getHeaders());
            URL parsedUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection)parsedUrl.openConnection();
            for (String headerName : map.keySet()) {
                connection.addRequestProperty(headerName, map.get(headerName));
            }
            setRequestProperties(request,connection);
            int responseCode = connection.getResponseCode();
            networkResponse.setResponseCode(responseCode);
            if(request.isWaitForResponse()){
                if (responseCode == -1) {
                    // -1 is returned by getResponseCode() if the response code could not be retrieved.
                    // Signal to the caller that something was wrong with the connection.
                    throw new IOException("Could not retrieve response code from HttpUrlConnection.");
                }
                //TODO For other requests
                //Get the response and parse accordingly and deliver
            }

        }catch(MalformedURLException e){
            e.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return networkResponse;
    }
    private void setRequestProperties(Request request, HttpURLConnection connection) throws ProtocolException{
        switch(request.getMethod()){
            case Request.Method.POST:
                connection.setRequestMethod("POST");

                //TODO check for post data and modify the request accordingly
                break;
            case Request.Method.GET:
                connection.setRequestMethod("GET");
                break;
            case Request.Method.DELETE:
                connection.setRequestMethod("DELETE");
                break;
            case Request.Method.PUT:
                //TODO check for put data and modify the request accordingly
                connection.setRequestMethod("PUT");
                break;
            case Request.Method.PATCH:
                //TODO check for patch data and modify the request accordingly
                connection.setRequestMethod("PATCH");
                break;
            default:
                connection.setRequestMethod("GET");
        }

    }
}
