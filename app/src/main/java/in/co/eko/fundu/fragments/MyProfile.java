package in.co.eko.fundu.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.co.eko.fundu.R;
import in.co.eko.fundu.activities.HomeActivity;
import in.co.eko.fundu.activities.UpadteAccountNoActivity;
import in.co.eko.fundu.activities.upi.YesBankUPIClient;
import in.co.eko.fundu.adapters.BankAccountsAdapter;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.interfaces.BankAccountOptions;
import in.co.eko.fundu.models.BankAccountItem;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.utils.FunduAnalytics;
import in.co.eko.fundu.utils.Utils;

import static in.co.eko.fundu.activities.upi.YesBankUPIClient.YES_BANK_CLIENT_ACTION.CHECK_BALANCE;
import static in.co.eko.fundu.activities.upi.YesBankUPIClient.YES_BANK_CLIENT_ACTION.FETCH_PROFILE;
import static in.co.eko.fundu.activities.upi.YesBankUPIClient.YES_BANK_CLIENT_ACTION.MANAGE_ACCOUNT;
import static in.co.eko.fundu.activities.upi.YesBankUPIClient.YES_BANK_CLIENT_ACTION.SET_UPI_PIN;

public class MyProfile extends BaseFragment implements View.OnClickListener{

    private TextView mName;
    private RecyclerView mBankInfo;
    private BankAccountsAdapter adapter;
    private ArrayList<BankAccountItem> accountItems = new ArrayList<BankAccountItem>();
    private ImageView mUserImage;
    private WebView mPpTos;
    private LinearLayout mPpTosll;



    private final int FETCH_YESBANK_PROFILE = 1;
    private final int ADD_YESBANK_ACCOUNT = 2;
    private final int ADD_YESBANK_CHECKBALANCE = 3;
    private final int SET_UPI_YESBANK_PIN = 4;


    public static MyProfile newInstance() {
        MyProfile fragment = new MyProfile();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FunduAnalytics.getInstance(getActivity()).sendScreenName("Your Profile");
        View view = inflater.inflate(R.layout.fragment_my_profile, container, false);
        init(view);
        mName.setText(FunduUser.getFullName());
        String imagepath = FunduUser.getProfilePic();
        if(!TextUtils.isEmpty(imagepath)){
            Picasso.with(getActivity()).load(imagepath).into(mUserImage);
        }
        return view;
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        HomeActivity homeActivity = (HomeActivity)activity;
        homeActivity.hideHamburgerIcon();

    }


    @Override
    public boolean onBackPressed() {
        if(mPpTosll.getVisibility() == View.VISIBLE){
            mPpTosll.setVisibility(View.GONE);
            return true;
        }
        return false;
    }

    private void init(View view) {
        TextView mobile = (TextView) view.findViewById(R.id.mobile);
        TextView email = (TextView) view.findViewById(R.id.email);
        mobile.setText(FunduUser.getContactId());
        email.setText(FunduUser.getEmail());
        mName = (TextView)view.findViewById(R.id.name);
        mUserImage = (ImageView)view.findViewById(R.id.user_image);
        mBankInfo = (RecyclerView) view.findViewById ( R.id.bank_info );
        TextView versionnumber = (TextView)view.findViewById(R.id.versionnumber);
        versionnumber.setText(getString(R.string.version)+" "+Utils.getAppVersion());
        mPpTos = (WebView)view.findViewById(R.id.pptos);
        mPpTosll = (LinearLayout)view.findViewById(R.id.pptosll);
        TextView vpa = (TextView)view.findViewById(R.id.vpa);


        view.findViewById(R.id.privacypolicy).setOnClickListener(this);
        view.findViewById(R.id.changeaccount).setOnClickListener(this);
        view.findViewById(R.id.tos).setOnClickListener(this);
        view.findViewById(R.id.imageview_back).setOnClickListener(this);
        view.findViewById(R.id.changeaccountnumberll).setOnClickListener(this);
        view.findViewById(R.id.forgotpinll).setOnClickListener(this);
        view.findViewById(R.id.changepinll).setOnClickListener(this);

        if(FunduUser.getCountryShortName().equalsIgnoreCase("KEN")){
            View bankInfoRl = view.findViewById(R.id.bank_info_rl);
            bankInfoRl.setVisibility(View.GONE);
            vpa.setVisibility(View.GONE);
            view.findViewById(R.id.accout_actions_ken).setVisibility(View.VISIBLE);
        }
        else{
            View bankInfoRl = view.findViewById(R.id.bank_info_rl);
            bankInfoRl.setVisibility(View.VISIBLE);
            vpa.setVisibility(View.VISIBLE);
            vpa.setText(FunduUser.getVpa());
            HomeActivity homeActivity = (HomeActivity) getActivity();
            if(homeActivity.getmLinkedAccountList() == null && !Constants.dummyUPI)
                fetchProfile();
            setAccountList(homeActivity.getmLinkedAccountList());
            adapter = new BankAccountsAdapter(accountItems, new BankAccountOptions() {
                @Override
                public void onCheckBalance(BankAccountItem item) {
                    checkBalance(item);
                }

                @Override
                public void onSetPin(BankAccountItem item) {
                    setPin(item);
                }
            });
            mBankInfo.setLayoutManager(new LinearLayoutManager(getActivity()));
            mBankInfo.setAdapter(adapter);
            view.findViewById(R.id.accout_actions_ken).setVisibility(View.GONE);
        }




    }
    private void setAccountList(JSONArray jAccList){
        if(jAccList != null){
            HomeActivity activity = (HomeActivity)getActivity();
            activity.setmLinkedAccountList(jAccList);
            try{
                for(int i = 0;i<jAccList.length();i++){
                    JSONObject bankInfo = jAccList.getJSONObject(i);
                    BankAccountItem item = new BankAccountItem();
                    item.setRecipientId(bankInfo.getString("accountId"));
                    item.setAccountNumber(bankInfo.getString("accountNumber"));
                    item.setName(bankInfo.getString("bankName"));
                    item.setIfsc(bankInfo.getString("ifscCode"));
                    item.setPinAvailable(bankInfo.getString("mpinStatus").equalsIgnoreCase("Y"));
                    if(accountItems.contains(item)){
                        accountItems.remove(item);
                    }
                    accountItems.add(item);
                }
            }catch(JSONException e){
                e.printStackTrace();
            }
        }
        else{
            BankAccountItem item = new BankAccountItem();
            item.setAccountNumber(FunduUser.getAccountNo());
            item.setIfsc(FunduUser.getIFSC());
            item.setName(FunduUser.getBankName());
            item.setRecipientId(FunduUser.getRecipientId());
            accountItems.add(item);
        }





    }

    private void fetchProfile(){
        Intent intent = new Intent(getActivity(), YesBankUPIClient.class);
        intent.putExtra ( "action",FETCH_PROFILE);
        startActivityForResult(intent,FETCH_YESBANK_PROFILE);
    }

    private void setPin(BankAccountItem item){
        FunduAnalytics.getInstance(getActivity()).sendAction("Profile","SetUpiPin");
        Intent intent = new Intent(getActivity(), YesBankUPIClient.class);
        intent.putExtra ( "action",SET_UPI_PIN);
        intent.putExtra("recipient_id",item.getRecipientId());
        intent.putExtra("accno",item.getAccountNumber());
        intent.putExtra("bank_name",item.getName());
        startActivityForResult(intent,SET_UPI_YESBANK_PIN);
    }
    private void checkBalance(BankAccountItem item){
        if(Constants.dummyUPI){
            BottomSheetDialog dialog = new BottomSheetDialog(getActivity());
            dialog.setTitle("Dummy Balance");
            dialog.show();
            return;
        }
        FunduAnalytics.getInstance(getActivity()).sendAction("Profile","CheckBalance",1);
        Intent intent = new Intent(getActivity(), YesBankUPIClient.class);
        intent.putExtra ( "action",CHECK_BALANCE);
        intent.putExtra("recipient_id",item.getRecipientId());
        startActivityForResult(intent,ADD_YESBANK_CHECKBALANCE);
    }

    private void showPrivacyPolicy(){
        FunduAnalytics.getInstance(getActivity()).sendAction("Profile","PrivacyPolicy");
        ((TextView)mPpTosll.findViewById(R.id.title)).setText(getString(R.string.privacypolicy));
        mPpTos.loadUrl("file:///android_asset/pp.html");
        mPpTosll.setVisibility(View.VISIBLE);
        mPpTosll.findViewById(R.id.backarrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPpTosll.setVisibility(View.GONE);
            }
        });
    }

    private void showTos(){
        FunduAnalytics.getInstance(getActivity()).sendAction("Profile","Terms of Service");
        ((TextView)mPpTosll.findViewById(R.id.title)).setText(getString(R.string.tos));
        mPpTos.loadUrl("file:///android_asset/tos.html");
        mPpTosll.setVisibility(View.VISIBLE);
        mPpTosll.findViewById(R.id.backarrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPpTosll.setVisibility(View.GONE);
            }
        });
    }

    private void yesbankAccountManagement(){
        if(Constants.dummyUPI){
            BottomSheetDialog dialog = new BottomSheetDialog(getActivity());
            dialog.setContentView(R.layout.yes_bank_dummy_popup);
            TextView tv = (TextView) dialog.findViewById(R.id.info);
            tv.setText("Not allowed for Dummy");
            dialog.show();
            return;
        }
        Intent intent = new Intent(getActivity(), YesBankUPIClient.class);
        intent.putExtra ( "action",MANAGE_ACCOUNT);
        startActivityForResult(intent,ADD_YESBANK_ACCOUNT);
    }

    public void doAccountManagement(){
        if(Constants.upiProvider == Constants.UPI_PROVIDER.ICICI){

            //ISDK.createNewVPA(getContext(),SettingsFragment.this);
        }
        else{
            FunduAnalytics.getInstance(getActivity()).sendAction("Profile","AddAccount");
            yesbankAccountManagement();
        }
    }

    @Override
    public void onClick(View view){
         switch(view.getId()){

             case R.id.changeaccount:{

                 doAccountManagement();
             }
             break;
             case R.id.privacypolicy:{
                 showPrivacyPolicy();
             }
             break;
             case R.id.tos:{
                 showTos();
             }
             break;
             case R.id.imageview_back:
             {
                 getActivity().onBackPressed();
             }
             break;
             case R.id.changepinll:
             {
                 HomeActivity homeActivity = (HomeActivity)getActivity();
                 homeActivity.onChangeFunduPin();
             }

                 break;
             case R.id.forgotpinll:{
                 HomeActivity homeActivity = (HomeActivity)getActivity();
                 homeActivity.onForgotFunduPin();
             }
                 break;
             case R.id.changeaccountnumberll:
                  startActivity(new Intent(getContext(), UpadteAccountNoActivity.class));
                 break;
         }
    }


    @Override
    public void onActivityResult(int requestCode,int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK){
            switch(requestCode){
                case FETCH_YESBANK_PROFILE:
                {
                    Bundle bundle = data.getExtras();
                    String statusCode = bundle.getString("status");
                    String statusDesc = bundle.getString("statusDesc");
                    if(statusCode.equalsIgnoreCase("S")) {

                        String accList = bundle.getString("accList");
                        try {
                            JSONArray jAccList = new JSONArray(accList);
                            setAccountList(jAccList);

                        } catch(JSONException e) {
                            e.printStackTrace();
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
                    break;
                case ADD_YESBANK_ACCOUNT:{

                        Bundle bundle = data.getExtras();
                        String status = bundle.getString("status");
                        String statusdesc = bundle.getString("statusDesc");
                        if(status.equalsIgnoreCase("S")){
                            fetchProfile();
                        }
                        else{
                            Toast.makeText(getActivity(),statusdesc,Toast.LENGTH_SHORT).show();

                        }
                }
                break;
                case SET_UPI_YESBANK_PIN:{
                    Bundle bundle = data.getExtras();
                    String status = bundle.getString("status");
                    String statusdesc = bundle.getString("statusDesc");
                    if(status.equalsIgnoreCase("S")){
                        fetchProfile();
                    }
                    else{
                        Toast.makeText(getActivity(),statusdesc,Toast.LENGTH_SHORT).show();

                    }
                }
                    break;
            }
        }

    }


}
