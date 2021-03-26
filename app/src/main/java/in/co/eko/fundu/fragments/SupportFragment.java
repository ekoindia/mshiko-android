package in.co.eko.fundu.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import in.co.eko.fundu.R;
import in.co.eko.fundu.activities.Feedback;
import in.co.eko.fundu.activities.HomeActivity;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.utils.Fog;


public class SupportFragment extends BaseFragment implements View.OnClickListener {

    private RelativeLayout call,email;
    private ImageView back;


    public SupportFragment() {
        // Required empty public constructor
    }


    public static SupportFragment newInstance(Bundle bundle) {
        SupportFragment fragment = new SupportFragment();
        if(bundle != null)
            fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_support, container, false);
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);
        setOnClickListeners();


    }

    private void setOnClickListeners() {

        call.setOnClickListener(this);
        email.setOnClickListener(this);
        back.setOnClickListener ( this );

    }

    private void init(View view) {

        call          = (RelativeLayout) view.findViewById(R.id.imageview_call);
        email         = (RelativeLayout) view.findViewById(R.id.imageview_email);
        back          = (ImageView)view.findViewById ( R.id.imageview_back );
        call.setVisibility(View.INVISIBLE);


    }








    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.imageview_call:
                displayPhoneIntent();
                break;

            case R.id.imageview_email:
                //displayEmailIntent("Send feedback");
                openHelp();
                break;
            case R.id.imageview_back:
                closefragment();
                break;
        }
    }

    private void openHelp(){
        Intent intent = new Intent(getActivity(), Feedback.class);
        intent.setAction("help");
        if(getArguments() != null) {
            String tid = getArguments ().getString ( Constants.PushNotificationKeys.TID );
            if(tid != null){
                intent.putExtra(Constants.PushNotificationKeys.TID,tid);
            }
        }
        startActivity(intent);
    }

//    private void showEmailPopup(){
//        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
//        LayoutInflater inflater = getActivity().getLayoutInflater();
//        View dialogView = inflater.inflate(R.layout.content_feedback, null);
//        dialogView.findViewById(R.id.screenshotcaptured).setVisibility(View.GONE);
//        dialogView.findViewById(R.id.checkboxll).setVisibility(View.GONE);
//
//        TextView tv = (TextView) dialogView.findViewById(R.id.title);
//        tv.setText(R.string.string_help);
//
//        final TextView issue = (TextView) dialogView.findViewById(R.id.issuewith);
//        issue.setText(R.string.issue);
//        issue.setVisibility(View.VISIBLE);
//
//
//        if(getArguments() != null) {
//            String tid = getArguments ().getString ( Constants.PushNotificationKeys.TID );
//            if(tid != null){
//                issue.setText(getString(R.string.issue_with)+" "+tid);
//            }
//        }
//
//        TextView fromEmail = (TextView) dialogView.findViewById(R.id.fromemail);
//        fromEmail.setText(String.format(getString(R.string.email_support), FunduUser.getEmail()));
//        fromEmail.setVisibility(View.VISIBLE);
//
//        final EditText problem = (EditText)dialogView.findViewById(R.id.feedback);
//
//        Button sendButton = (Button)dialogView.findViewById(R.id.sendButton);
//        dialogBuilder.setView(dialogView);
//
//        final Dialog dialog = dialogBuilder.show();
//
//        sendButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String text = problem.getText().toString();
//                if(text.length()==0){
//                    Utils.vibratePhone(getActivity());
//                }
//                else{
//                    reportIssue(issue.getText().toString(),text);
//                    dialog.dismiss();
//                }
//
//            }
//        });
//
//        dialogView.findViewById(R.id.imageview_back).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//
//    }
//    private void reportIssue(String summary,String description){
//        ReportIssue reportIssue = new ReportIssue(getActivity());
//        reportIssue.setData(summary,description);
//        reportIssue.start();
//        String message = String.format(getString(R.string.email_support),"your email");
//        Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT).show();
//        closefragment();
//    }
    private void displayEmailIntent(String subject) {

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        // your goes email goes here
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@your_company.com"});
        if(getArguments() != null) {
            String tid = getArguments ().getString ( Constants.PushNotificationKeys.TID );
            if(tid != null){
                intent.putExtra(Intent.EXTRA_SUBJECT, subject+" "+tid);
            }
            else{
                intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            }
        }
        else{
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        }
        //intent.putExtra ( Intent.EXTRA_ASSIST_INPUT_HINT_KEYBOARD,"Describe an issue or share your idea" );

        try {
            startActivity(Intent.createChooser(intent, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Fog.d("onResume","onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Fog.d("onResume","onPause");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof HomeActivity)
        ((HomeActivity)activity).hideHamburgerIcon();
    }

    private void displayPhoneIntent() {

       String posted_by = "+91111-333-222-4";

        String uri = "tel:" + posted_by.trim() ;
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse(Uri.decode(uri)));
        startActivity(intent);
    }
    private void closefragment() {
        getActivity().onBackPressed();
    }
    @Override
    public boolean onBackPressed() {
        return false;
    }
}
