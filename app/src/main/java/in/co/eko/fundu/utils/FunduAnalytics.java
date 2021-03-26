package in.co.eko.fundu.utils;

import android.content.Context;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import in.co.eko.fundu.FunduApplication;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.constants.Constants;

/**
 * Created by zartha on 8/17/17.
 */

public class FunduAnalytics {
    private static FunduAnalytics instance;
    private Tracker mTracker;

    public static FunduAnalytics getInstance(Context context) {
        if(instance == null){
            instance = new FunduAnalytics((FunduApplication) context.getApplicationContext());
        }
        return instance;
    }
    FunduAnalytics(FunduApplication application){
        mTracker = application.getTracker();
       if(FunduUser.getUser() != null && FunduUser.getUser().getContactId() != null && FunduUser.getUser().getContactId().length() >0 ){
           mTracker.set("userId",FunduUser.getContactId());
       }
    }

    public void sendScreenName(String screenName){
        if(Constants.debug)
            return;
        mTracker.setScreenName(screenName);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
    public void sendAction(String category,String action,String label,int value){
        if(Constants.debug)
            return;
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .setValue(value)
                .build());
    }
    public void sendAction(String category,String action){
        if(Constants.debug)
            return;
        if(category == null)
            category = "Action";
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .build());
    }
    public void sendAction(String category,String action,String label){
        if(Constants.debug)
            return;
        if(category == null)
            category = "Action";
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .setValue(1)
                .build());
    }

    public void sendAction(String category,String action,int value){
        if(Constants.debug){
            Fog.wtf(FunduAnalytics.class.getName(),"Category:"+category+"\n"
            +"action:"+action+"\n"+"value:"+value);
            return;
        }
        if(category == null)
            category = "Action";
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setValue(value)
                .build());
    }
    public void sendAction(String action){
        if(Constants.debug)
            return;
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction(action)
                .build());


    }


}
