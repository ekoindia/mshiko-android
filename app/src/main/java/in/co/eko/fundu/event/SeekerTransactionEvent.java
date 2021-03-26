package in.co.eko.fundu.event;

import org.json.JSONObject;

/**
 * Created by pallavi on 8/12/17.
 */

public class SeekerTransactionEvent {

    private JSONObject jData;
    private int pushType;

    public SeekerTransactionEvent(JSONObject jData, int pushType) {
        this.jData = jData;
        this.pushType = pushType;
    }

    public int getPushType() {
        return pushType;
    }

    public JSONObject getTranxInitatedData() {
        return jData;
    }

}
