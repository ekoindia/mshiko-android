package in.co.eko.fundu.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

import in.co.eko.fundu.R;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.utils.Fog;
import in.co.eko.fundu.utils.FunduAnalytics;
import in.co.eko.fundu.utils.Utils;

/**
 * Created by zartha on 5/18/18.
 */

public class InviteDialogFragment extends DialogFragment {
    public static final String TAG = "FullScreenDialog";
    private String msg;
    private String title;
    private String video;
    private String shareMessage;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle state) {
        super.onCreateView(inflater, parent, state);
        View view = getActivity().getLayoutInflater().inflate(R.layout.invite_friends_link, parent, false);
        Button button = (Button) view.findViewById(R.id.share);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareLink();
            }
        });

        TextView textView = (TextView)view.findViewById(R.id.invitationLink);
        textView.setText(FunduUser.getInvitationLink());

        msg = getString(R.string.invite_msg);

        title = "Fundu - Get Cash Anywhere!";

        String js = FunduUser.getInvitationMessage();
        if(!TextUtils.isEmpty(js)){
            try{
                JSONObject jsonObject = new JSONObject(js);
                video = jsonObject.optString("video");
                String message = jsonObject.optString("message");
                String titlej = jsonObject.optString("title");
                if(!TextUtils.isEmpty(message)){
                    msg = message;
                }
                if(!TextUtils.isEmpty(titlej)){
                    title = titlej;
                }

            }catch(Exception e){
                Fog.logException(e);
            }
        }

        TextView invitationText = (TextView)view.findViewById(R.id.invitationText);
        invitationText.setText(msg);


        view.findViewById(R.id.imageview_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        view.findViewById(R.id.feedback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onFeedBackClick();
            }
        });
        FunduAnalytics.getInstance(getActivity()).sendScreenName("InviteFriends");

        shareMessage = msg;
        if(!TextUtils.isEmpty(FunduUser.getInvitationLink())){
            shareMessage = shareMessage+"\nInstall Now - "+FunduUser.getInvitationLink();
        }

        if(!TextUtils.isEmpty(video)){
            shareMessage = shareMessage+"\nCheck out Fundu - "+video;
        }
        return view;
    }
    public void onFeedBackClick(){
        View v = getView();
        v.setDrawingCacheEnabled(true);
        Bitmap bmp = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false);
        Utils.takeFeedback(bmp,getActivity());
    }
    public void shareLink(){

        FunduAnalytics.getInstance(getActivity()).sendAction("Share","AppInvitation");

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        //sendIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        startActivity(sendIntent);


//        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invite_friends))
//                .setMessage(getString(R.string.app_name))
//                .setDeepLink(dynamicLinkUri)
//                .setCustomImage(Uri.parse("https://commons.wikimedia.org/wiki/File:Logo_u_fund.jpg"))
//                .setCallToActionText("call to action text")
//                .build();
//        startActivityForResult(intent, REQUEST_INVITE);
    }
}
