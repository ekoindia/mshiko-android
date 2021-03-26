package in.co.eko.fundu.views;/*
 * Created by Bhuvnesh
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import in.co.eko.fundu.R;
import in.co.eko.fundu.activities.HomeActivity;


public class ProgressOverlay extends FrameLayout implements View.OnClickListener {
    private TextView timer;
    private TextView mTextDesc,mTextInfo;
    private CountDownTimer downTimer;
    private HomeActivity activity;
    private long time = 120*1000;
    float pStatus = 0;
    int max = 100;
    private float progressPer;
    ProgressBar mProgress,mProgressInner;
    Drawable drawable;
    public ProgressOverlay(Context context) {
        super(context);
    }

    public ProgressOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ProgressOverlay(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ProgressOverlay(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        if (getContext() instanceof HomeActivity) {
            activity = (HomeActivity) getContext();

        }
        setBackgroundResource(R.color.whiteOverlay);
        setClickable(true);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.request_active_overlay, this, true);
//        text = (TextView) view.findViewById(R.id.text);
//        timer = (TextView) view.findViewById(R.id.timer);
//        timer.setTypeface(Typeface.DEFAULT_BOLD);
//        Resources res = getResources();
//        drawable = res.getDrawable(R.drawable.circular);
         mProgress = (ProgressBar) findViewById(R.id.progress_outer);
        mProgressInner = (ProgressBar) findViewById(R.id.progress_inner);

        TextView cancel = (TextView) view.findViewById(R.id.cancel_active_request);
        cancel.setOnClickListener(this);
        setOnClickListener(this);

        mTextDesc = (TextView) view.findViewById(R.id.request_cash_desc);
        mTextInfo = (TextView)view.findViewById(R.id.request_cash_info);
        mTextInfo.setVisibility(View.GONE);



    }

    public void showProgress() {
        setVisibility(VISIBLE);
        if(downTimer != null)
            downTimer.cancel();
        downTimer = new CountDownTimer(time, 600) {
            public void onTick(long millisUntilFinished) {
                pStatus+=0.5;
                // mProgress.setProgress(pStatus);
                mProgress.setProgress((int)pStatus);
                mProgressInner.setProgress((int)pStatus);
                //String songsFound = getResources().getQuantityString(R.plurals.numberOfSeconds, (int) millisUntilFinished / 1000, (int) millisUntilFinished / 1000);

            }
            public void onFinish() {
                hideProgress();
                //Utils.showShortToast(CashRequestAction.this,getString(R.string.user_not_accept_request));
            }
        }.start();

    }



    public void hideProgress() {
        pStatus = 0;
        if(downTimer != null)
            downTimer.cancel();
        setVisibility ( View.GONE );

    }


    private void updateDrawerState(boolean state) {
        if (activity != null) {
            activity.getActionBarDrawerToggle().setDrawerIndicatorEnabled(state);
            if (!state)
                activity.getDrawer().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            else
                activity.getDrawer().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }

    @Override
    public void onClick(View v) {
        setVisibility(View.GONE);
//        if (downTimer != null) {
//            downTimer.cancel();
//        }
    }

    public void setName(String name){
        String desc = String.format(getContext().getString(R.string.request_active_desc_contact),name);
        String info = String.format(getContext().getString(R.string.request_active_info_contact),name);
        mTextDesc.setText(desc);
        mTextInfo.setText(info);
    }

    public void setTime(long millisecond, int maxprogress) {

        mProgress.setProgress(1);   // Main Progress
        mProgressInner.setProgress(1);
        mProgress.setSecondaryProgress(this.max); // Secondary Progress
        mProgress.setMax(max); // Maximum Progress
        mProgressInner.setMax(max);
        progressPer = (300*max)/(time);

    }
}
