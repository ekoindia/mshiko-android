package in.co.eko.fundu.fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import in.co.eko.fundu.R;
import in.co.eko.fundu.activities.About;
import in.co.eko.fundu.activities.UpadteAccountNoActivity;
import in.co.eko.fundu.activities.UpdateDaysAndTime;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.event.DataUpdated;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.requests.IsMerchantRequest;
import in.co.eko.fundu.requests.UpdateSettingRequest;
import in.co.eko.fundu.utils.FunduAnalytics;
import in.co.eko.fundu.utils.SignoutHelper;
import in.co.eko.fundu.utils.Utils;

import static in.co.eko.fundu.constants.Constants.merchantKey;
import static in.co.eko.fundu.constants.Constants.mid;


//import com.icicibank.isdk.ISDK;
//import com.icicibank.isdk.listner.ISDKCreateNewVPAListner;


public class SettingsFragment extends BaseFragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, AdapterView.OnItemSelectedListener/*,ISDKCreateNewVPAListner*/{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ImageView back;
    private ProgressDialog dialog;
    private View viewupdatedays,changeaccno;
    private TextView signOut, updateDays,incentiveVal,about, rateUs;
    private UpdateSettingRequest updateSettingRequest;
    SwitchCompat notificationSwitch, autoCashOutSwitch, shareLocationSwitch;
    private LinearLayout incentiveRl;

    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public SettingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialog = new ProgressDialog(getActivity());
        dialog.setCancelable(false);
        dialog.setMessage(getString(R.string.sign_out));
        setHasOptionsMenu(true);
        updateSettingRequest = new UpdateSettingRequest(getContext());
        EventBus.getDefault().register(this);
        FunduAnalytics.getInstance(getActivity()).sendScreenName("Settings");
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        signOut        = (TextView) view.findViewById(R.id.signOut);
        changeaccno    = (View) view.findViewById(R.id.changeaccountnumber);
        updateDays     = (TextView) view.findViewById(R.id.updatedays);
        about          = (TextView) view.findViewById(R.id.about);
        rateUs         = (TextView)view.findViewById(R.id.rate_us); 
        incentiveVal   = (TextView) view.findViewById(R.id.incentiveVal);
        viewupdatedays   = (View) view.findViewById(R.id.viewupdatedays);
        back           = (ImageView) view.findViewById(R.id.imageview_back);
        incentiveRl    = (LinearLayout)view.findViewById(R.id.incentivesrl);


        if (FunduUser.getUser() != null) {
            signOut.setVisibility(View.VISIBLE);
            signOut.setOnClickListener(this);
            incentiveRl.setVisibility(View.VISIBLE);
            incentiveRl.setOnClickListener(this);
            incentiveVal.setText(""+FunduUser.getTotalIncentivesFromInvitations());
            if (pref.getString(Constants.CONTACT_TYPE_PA).equalsIgnoreCase("AGENT")) {
                updateDays.setVisibility(View.VISIBLE);
                updateDays.setOnClickListener(this);
                viewupdatedays.setVisibility(View.VISIBLE);
            } else {
                changeaccno.setVisibility(View.VISIBLE);
                changeaccno.setOnClickListener(this);
            }
            about.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  startActivity(new Intent(getActivity(), About.class));
                }
            });
            rateUs.setOnClickListener(this);
            incentiveRl.setVisibility(View.GONE);
            changeaccno.setVisibility(View.GONE);


        }
        notificationSwitch = (SwitchCompat) view.findViewById(R.id.notificationSwitch);
        autoCashOutSwitch = (SwitchCompat) view.findViewById(R.id.autoCashOutSwitch);
        shareLocationSwitch = (SwitchCompat) view.findViewById(R.id.shareLocationSwitch);
        SwitchCompat userRequestSwitch = (SwitchCompat) view.findViewById(R.id.userRequestSwitch);
        SwitchCompat blockedUserSwitch = (SwitchCompat) view.findViewById(R.id.blockedUserSwitch);
        notificationSwitch.setOnCheckedChangeListener(this);
        autoCashOutSwitch.setOnCheckedChangeListener(this);
        shareLocationSwitch.setOnCheckedChangeListener(this);
        userRequestSwitch.setOnCheckedChangeListener(this);
        blockedUserSwitch.setOnCheckedChangeListener(this);

        notificationSwitch.setChecked(pref.getBoolean(Constants.SettingsPref.NOTIFICATION, true));
        autoCashOutSwitch.setChecked(pref.getBoolean(Constants.SettingsPref.AUTOCASHOUT, false));
        shareLocationSwitch.setChecked(pref.getBoolean(Constants.SettingsPref.SHARE_LOCATION, true));
        userRequestSwitch.setChecked(pref.getBoolean(Constants.SettingsPref.USER_REQUEST, false));
        blockedUserSwitch.setChecked(pref.getBoolean(Constants.SettingsPref.BLOCKED_USER, false));
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });


        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(pref.getInt(Constants.SettingsPref.DISABLE_UNTIL, 0));
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.findItem(R.id.action_search).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.updatedays) {
            startActivity(new Intent(getContext(), UpdateDaysAndTime.class));
        }
        else if (v.getId() == R.id.changeaccountnumber) {
            FunduAnalytics.getInstance(getActivity()).sendAction("Settings","ChangeAccountNo");
            if(FunduUser.getCountryShortName().equalsIgnoreCase("IND")){
                showChangeAccountDialog();
            }
            else{
                startActivity(new Intent(getContext(), UpadteAccountNoActivity.class));
            }

        }
        else if (v.getId() == R.id.becomeamerchant) {
            IsMerchantRequest isMerchantRequest = new IsMerchantRequest(getContext());
            isMerchantRequest.setParserCallback(new IsMerchantRequest.OnIsMerchantRequestResults() {
                @Override
                public void onIsMerchantRequestResponse(String response) {
                    try {
                        JSONObject job = new JSONObject(response);
                        if (job.optString("status").equalsIgnoreCase("SUCCESS")) {
//                          HomeActivity.toolbar.setVisibility(View.GONE);
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("becomemerchant", true);
                            Fragment fragment = new MerchantRegistrationFragment();
                            fragment.setArguments(bundle);
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.fragmentContainer, fragment);
                            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                            fragmentTransaction.commit();
                        } else {
                            if (job.optString("message").equalsIgnoreCase("agent data not found"))
                                Utils.showShortToast(getContext(), "You cannot become a Merchant since our database doesn't hold any record of you as a Merchant");
                            else
                                Utils.showShortToast(getContext(), job.optString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onIsMerchantRequestError(VolleyError error) {

                }

            });
            if (Utils.isNetworkAvailable(getActivity()))
                isMerchantRequest.start();
        }
        if (v.getId() == R.id.signOut) {
            FunduAnalytics.getInstance(getActivity()).sendAction("Settings","SignOut");
            SignoutHelper.getInstance().callSingoutService(getActivity());

        }
        else if(v.getId() == R.id.incentivesrl){
            //TODO: Show all the incentives he got
        } else if (v.getId() == R.id.rate_us) {
            rateAppOnPlayStore();
        }
    }

    private void rateAppOnPlayStore() {

        FunduAnalytics.getInstance(getActivity()).sendAction("Settings","Rate us");
        Uri uri = Uri.parse("market://details?id=" + getContext().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getContext().getPackageName())));
        }

    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (buttonView.getId() == R.id.autoCashOutSwitch)
        {
            hitUpdateSettingAPI();

        }
        else if(buttonView.getId() == R.id.shareLocationSwitch){
            hitUpdateSettingAPI();
        }
        else if(buttonView.getId() == R.id.notificationSwitch){
            hitUpdateSettingAPI();
            if(isChecked){
                FunduAnalytics.getInstance(getActivity()).sendAction("Settings","PushNotificationOn");
            }
            else
                FunduAnalytics.getInstance(getActivity()).sendAction("Settings","PushNotificationOff");
        } else if (buttonView.getId() == R.id.userRequestSwitch) {
            pref.putBoolean(Constants.SettingsPref.USER_REQUEST, isChecked);
        } else if (buttonView.getId() == R.id.blockedUserSwitch) {
            pref.putBoolean(Constants.SettingsPref.BLOCKED_USER, isChecked);

        }
    }

    private void hitUpdateSettingAPI() {
        updateSettingRequest.setData(autoCashOutSwitch.isChecked(), notificationSwitch.isChecked(), shareLocationSwitch.isChecked(), Request.Method.PUT);
        updateSettingRequest.start();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        pref.putInt(Constants.SettingsPref.DISABLE_UNTIL, position);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * Event Bus events handlers
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataUpdated(DataUpdated event) {
        DataUpdated.DataUpdatedType type =  event.type;
        if(type != null && type == DataUpdated.DataUpdatedType.InvitationIncentive){
            incentiveVal.setText(""+FunduUser.getTotalIncentivesFromInvitations());
        }
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    public void doAccountManagement(){
        if(Constants.upiProvider == Constants.UPI_PROVIDER.ICICI){
            //ISDK.createNewVPA(getContext(),SettingsFragment.this);
        }
        else{
            yesbankAccountManagement();
        }
    }

    Handler handler = new Handler();

    /**
     * ICICI Bank UPI
     */
//    @Override
//    public void vpaCreationFailed(int i) {
//        Toast.makeText(getActivity(), ""+getActivity().getResources().getString(R.string.vpacreatetionfailed), Toast.LENGTH_SHORT).show();
//
//    }
//
//    @Override
//    public void vpaCreationSuccessful(String s, String s1, String s2) {
//        Fog.i("","vpaCreationSuccessful");
//        AppPreferences appPreferences = new AppPreferences(getActivity());
//        appPreferences.putString(Constants.FromContact,"settings");
//        Constants.FromSettings = true;
//        Toast.makeText(getContext(),getString(R.string.vpacreatetionsuccess),Toast.LENGTH_SHORT).show();
//        if(s2 == null || s2.length() == 0)
//            s2 = "Not Available";
//        FunduUser.setVpa(s2);
//        if(s != null && s.length() != 0)
//            FunduUser.setIFSC(s);
//        if(s1 != null && s1.length() != 0)
//            FunduUser.setAccountNo(s1);
//        Intent intent = new Intent(getActivity(), LinkAccountActivity.class);
//        intent.putExtra("after_action","HomeAcitivity");
//
//        getActivity().startActivity(intent);
//        getActivity().finish();
//    }
//
//    @Override
//    public void vpaCreationCanceled() {
//
//    }

    /**
     * Yes bank UPI
     */
    private final int ACCOUNT_MANAGEMENT_CODE = 3;
    private final int ADD_ACCOUNT_CODE = 4;
    public void yesbankAccountManagement(){
        {
            Date now = new Date();

            Bundle bundle = new Bundle();
            bundle.putString("mid", mid);
            bundle.putString("merchantKey", merchantKey);
            bundle.putString("merchantTxnID", ""+now.getTime());
            bundle.putString("virtualAddress", FunduUser.getVpa());
            //bundle.putString("theme_color","AppTheme");
            bundle.putString("add1", "");
            bundle.putString("add2", "");
            bundle.putString("add3", "");
            bundle.putString("add4", "");
            bundle.putString("add5", "");
            bundle.putString("add6", "");
            bundle.putString("add7", "");
            bundle.putString("add8", "");
            bundle.putString("add9", "NA" );
            bundle.putString("add10", "NA" );

            Toast.makeText(getContext(), R.string.user_account_add, Toast.LENGTH_SHORT).show();

            /*Yes bank UPI Account Management  add your code*/
//            Intent intent = new Intent(getActivity(), AddAccountActivity.class);
//            intent.putExtras(bundle); startActivityForResult(intent, ADD_ACCOUNT_CODE);
//            Intent intent = new Intent(getActivity(), AccountManagementActivity.class);
//            intent.putExtras(bundle); startActivityForResult(intent, ACCOUNT_MANAGEMENT_CODE);
        }
    }
    @Override
    public void onActivityResult(int requestCode,int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_ACCOUNT_CODE && resultCode == Activity.RESULT_OK && data != null) {
            Bundle bundle = data.getExtras();
            String status = bundle.getString("status");
            String statusdesc = bundle.getString("statusDesc");
            if(status.equalsIgnoreCase("S")){
                String yblRefNo = bundle.getString("yblRefNo");
                String virtualAddress = bundle.getString("virtualAddr");
                String merchantTxnId = bundle.getString("merchantTxnId");
                String add1 = bundle.getString("add1");
                String add2 = bundle.getString("add2");
                String add3 = bundle.getString("add3");
                String add4 = bundle.getString("add4");
                String add5 = bundle.getString("add5");
                String add6 = bundle.getString("add6");
                String add7 = bundle.getString("add7");
                String add8 = bundle.getString("add8");
                String add9 = bundle.getString("add9");
                String add10 = bundle.getString("add10");
                Toast.makeText(getActivity(),statusdesc,Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getActivity(),statusdesc,Toast.LENGTH_SHORT).show();

            }

        }
        else if (requestCode == ACCOUNT_MANAGEMENT_CODE && resultCode == Activity.RESULT_OK && data != null) {
            Bundle bundle = data.getExtras();
            String status = bundle.getString("status");
            String statusdesc = bundle.getString("statusDesc");
            if(status.equalsIgnoreCase("S")){
                String yblRefNo = bundle.getString("yblRefNo");
                String virtualAddress = bundle.getString("virtualAddr");
                String merchantTxnId = bundle.getString("merchantTxnId");
                String add1 = bundle.getString("add1");
                String add2 = bundle.getString("add2");
                String add3 = bundle.getString("add3");
                String add4 = bundle.getString("add4");
                String add5 = bundle.getString("add5");
                String add6 = bundle.getString("add6");
                String add7 = bundle.getString("add7");
                String add8 = bundle.getString("add8");
                String add9 = bundle.getString("add9");
                String add10 = bundle.getString("add10");
                Toast.makeText(getActivity(),statusdesc,Toast.LENGTH_SHORT).show();
            }
            else{
                //Toast.makeText(getActivity(),statusdesc,Toast.LENGTH_SHORT).show();

            }

        }
    }

    private void showChangeAccountDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.dialog_title));
        builder.setMessage(getString(R.string.changeAccInfo));

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doAccountManagement();
                        dialog.dismiss();
                    }
                });

        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }
    @Override
    public boolean onBackPressed() {
        return false;
    }




}
