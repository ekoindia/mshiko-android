package in.co.eko.fundu.views;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by divyanshu.jain on 7/6/2016.
 */
public class CustomProgressDialog extends ProgressDialog {
    Context context;

    public CustomProgressDialog(Context context) {
        super(context);
        this.context = context;
        InitDialog(context);
    }

    private void InitDialog(Context context) {
        setMessage("Please Wait...");
        setCancelable(false);
    }

    @Override
    public void setMessage(CharSequence title) {
        super.setMessage(title);
    }

    @Override
    public void show() {
        if (!((Activity) context).isFinishing())
            super.show();
    }
}
