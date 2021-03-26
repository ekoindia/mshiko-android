package in.co.eko.fundu.govorit.tools;

/**
 * Created by zartha on 3/15/18.
 */

public class Error extends Exception{
    private long networkTimeMs;

    public void setNetworkTimeMs(long networkTimeMs) {
        this.networkTimeMs = networkTimeMs;
    }
}
