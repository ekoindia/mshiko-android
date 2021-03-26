package in.co.eko.fundu.govorit.tools;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by zartha on 3/15/18.
 */

public class FunduUrlConnection extends HttpURLConnection {

    public FunduUrlConnection(URL url){
        this(url,null);
    }

    public FunduUrlConnection(URL url, Map<String,String> headers){
        super(url);
        if(headers != null){
            Iterator it = headers.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                setRequestProperty((String)pair.getKey(),(String)pair.getValue());
                it.remove();
            }
        }
    }

    @Override
    public void disconnect() {

    }

    @Override
    public boolean usingProxy() {
        return false;
    }

    @Override
    public void connect() throws IOException {

    }

}
