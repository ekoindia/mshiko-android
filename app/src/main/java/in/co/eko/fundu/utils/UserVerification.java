package in.co.eko.fundu.utils;
/*
 * Created by Bhuvnesh
 */

import android.content.Context;

public class UserVerification {
    private Context context;

    public UserVerification(Context context) {
        this.context = context;


    }
    public boolean isMobileVerified(){
        return true;
    }
    public boolean isUserLogin() {
        return true;
    }
}
