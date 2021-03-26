package in.co.eko.fundu.utils;

import android.os.Bundle;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import in.co.eko.fundu.constants.Constants;

/**
 * Created by zartha on 7/11/17.
 */

public class Fog {

    public static void logException(Throwable throwable){

        if(Constants.debug){
            throwable.printStackTrace();
        }
        else
            Crashlytics.logException(throwable);

    }
    public static void e(String tag,String msg){
        if(Constants.debug){
            Log.e(tag,msg+"");
        }

    }
    public static void e(String tag,String msg, Exception e){
        if(Constants.debug){

            Log.e(tag,msg+"",e);
        }
    }
    public static void d(String tag,String msg){
        if(Constants.debug){
            Log.d(tag,msg+"");
        }

    }
    public static void d(String tag,String msg, Exception e){
        if(Constants.debug){

            Log.d(tag,msg+"",e);
        }
    }
    public static void i(String tag,String msg){
        if(Constants.debug){
            Log.i(tag,msg+"");
        }

    }
    public static void wtf(String tag,String msg){
        if(Constants.debug){
            Log.wtf(tag,msg+"");
        }

    }
    public static void logBundle(String tag, Bundle bundle){
        if(!Constants.debug){
            return;
        }
        for (String key : bundle.keySet())
        {
            Log.e(tag, key + " = \"" + bundle.get(key) + "\"");
        }
    }

    public static void logEvent(boolean isTid, String tid, String activity, String method, String reason, String timeStamp, String state) {
        if(isTid) {
            Crashlytics.log("Param: transaction_id="+ tid);
        } else {
            Crashlytics.log("Param: pair_request_id="+ tid);
        }
        Crashlytics.log("Param: activity="+ activity);
        Crashlytics.log("Param: method="+ method);
        Crashlytics.log("Param: reason="+ reason);
        Crashlytics.log("Param: timeStamp="+ timeStamp);
        Crashlytics.log("Param: state="+ state);
        Throwable deleteTxn = new Throwable("Delete Transaction");
        Crashlytics.logException(deleteTxn);
    }
}
