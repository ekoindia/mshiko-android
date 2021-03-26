package in.co.eko.fundu.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import in.co.eko.fundu.FunduApplication;
import in.co.eko.fundu.R;
import in.co.eko.fundu.activities.HomeActivity;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.models.Contact;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.requests.UpdateCustomer;
import in.co.eko.fundu.utils.Fog;
import in.co.eko.fundu.utils.FunduAnalytics;

import static in.co.eko.fundu.constants.Constants.appName;
import static in.co.eko.fundu.constants.Constants.merchantKey;
import static in.co.eko.fundu.constants.Constants.mid;

/**
 * ICICI Bank UPI import
 * <p>
 * Yes bank UPI Import
 */
//import com.icicibank.isdk.ISDK;
//import com.icicibank.isdk.listner.ISDKCreateNewVPAListner;
//import com.icicibank.isdk.listner.ISDKManageVPAListner;
/**
 * Yes bank UPI Import
 */


public class IntroductionToUPI extends BaseFragment implements View.OnClickListener/*,ISDKCreateNewVPAListner, ISDKManageVPAListner*/ {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TextView createVPA,learnMore,later;
    private TextView warnings;
    private String TAG = IntroductionToUPI.class.getName();


    // TODO: Rename and change types of parameters
    private Contact mParam1;
    private String mParam2;

    public IntroductionToUPI() {
        // Required empty public constructor
    }


    public static IntroductionToUPI newInstance(Contact param1, String param2) {
        IntroductionToUPI fragment = new IntroductionToUPI();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = (Contact) getArguments().getSerializable(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_introduction_to_upi, container, false);
        init(view);
        FunduAnalytics.getInstance(getActivity()).sendScreenName("IntroductionToUpi");
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Bundle bundle) {
        if (mListener != null) {
            mListener.onFragmentInteraction(bundle);
        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }
    Handler handler = new Handler();




    public void init(View view){
        createVPA = (TextView)view.findViewById(R.id.createVPA);
        learnMore = (TextView)view.findViewById(R.id.learnMore);
        later = (TextView)view.findViewById(R.id.later);
        later.setOnClickListener(this);
        learnMore.setOnClickListener(this);
        createVPA.setOnClickListener(this);
        later.setVisibility(View.INVISIBLE);
        learnMore.setVisibility(View.INVISIBLE);
//        createVPA.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ISDK.createNewVPA(getActivity(),IntroductionToUPI.this);
//            }
//        });
        warnings = (TextView)view.findViewById(R.id.warnings);
        warnings.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        Fog.d("vpaCreationCanceled","onResume: vpaCreationCanceled");
       /* FunduUser.setVpa("");
        FunduUser.setAccountNo("");*/
    }

    @Override
    public void onClick(View v) {
        /*if(v == createVPA){
            //ISDK.manageVPA(getContext(),IntroductionToUPI.this);
            ISDK.createNewVPA(getContext(),IntroductionToUPI.this);
        }*/

        switch (v.getId()){

            case R.id.createVPA:
                if(Constants.upiProvider == Constants.UPI_PROVIDER.ICICI){
                    //ISDK.createNewVPA(getContext(),IntroductionToUPI.this);
                }
                else if(Constants.upiProvider == Constants.UPI_PROVIDER.YESBANK)
                    if(Constants.dummyUPI){
                        sendDummyVPA();
                    }
                    else {
                        startRegistration ();
                    }

                break;

            case R.id.learnMore:
                //ISDK.createNewVPA(getContext(),IntroductionToUPI.this);
                break;

            case R.id.later:
                startActivity(new Intent(getActivity(), HomeActivity.class));
                getActivity().finish();
                break;

        }



    }


    /**
     * ICICI Bank UPI callbacks
     *
     */

//    @Override
//    public void vpaCreationFailed(int i) {
//
//        Fog.i(TAG,"vpaCreationFailed");
//        if(Constants.debug)
//            Toast.makeText(getContext(),getString(R.string.vpacreatetionfailed)+" errorcode "+i,Toast.LENGTH_SHORT).show();
//        /*FunduUser.setVpa("ankitsharma123456");
//        FunduUser.setIFSC("ICIC0001353");
//        FunduUser.setAccountNo("031401571487");*/
////        Intent intent = new Intent(getActivity(), LinkAccountActivity.class);
////        intent.putExtra("after_action","HomeAcitivity");
////        intent.putExtra("after_action","HomeAcitivity");
////
////        getActivity().startActivity(intent);
////        getActivity().finish();
//    }
//
//    @Override
//    public void vpaCreationSuccessful(String s, String s1, String s2) {
//        //VPA created ask for bank accounts
//        Fog.i(TAG,"vpaCreationSuccessful");
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
//        getActivity().startActivity(intent);
//        getActivity().finish();
//
//    }
//
//    @Override
//    public void vpaCreationCanceled() {
//        Fog.d("vpaCreationCanceled","vpaCreationCanceled");
//        FunduUser.setVpa("");
//        FunduUser.setAccountNo("");
//        if(Constants.debug)
//            Toast.makeText(getContext(),getString(R.string.vpacreatetionfailed),Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void manageVPAFailed(int i) {
//        Fog.i(TAG,"manageVPAFailed");
//    }
//
//    @Override
//    public void manageVPASuccessful() {
//        Fog.i(TAG,"manageVPAFailed");
//    }
//
//    @Override
//    public void manageVPACanceled() {
//        Fog.i(TAG,"manageVPAFailed");
//    }
//
//

    /**
     * Yes bank UPI Registation
     */

    public void startRegistration()
    {
        Date now = new Date();
        Bundle bundle = new Bundle();
        bundle.putString("mid", mid);
        bundle.putString("merchantKey",merchantKey );
        bundle.putString("merchantTxnID", "yblreg"+now.getTime());
        bundle.putString("appName", appName);
        bundle.putString("add1", "");
        bundle.putString("add2", "");
        bundle.putString("add3", "");
        bundle.putString("add4", "");
        bundle.putString("add5", "");
        bundle.putString("add6", "");
        bundle.putString("add7", "");
        bundle.putString("add8", "");
        bundle.putString("add9", "NA");
        bundle.putString("add10", "NA");
        //Yes Bank UPI Registation Activiy

        Toast.makeText(getContext(), R.string.user_registeration_add, Toast.LENGTH_SHORT).show();

//        Intent intent = new Intent(FunduApplication.getAppContext(), RegistrationActivity.class); intent.putExtras(bundle); startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            Bundle bundle = data.getExtras();
            String status = bundle.getString("status");
            String statusdesc = bundle.getString("statusDesc");
            if(TextUtils.isEmpty(statusdesc)){
                statusdesc = bundle.getString("statusdesc");
            }
            if(status != null && status.equalsIgnoreCase("S")){
                FunduAnalytics.getInstance(getActivity()).sendAction("Registration","UPIRegistrationCompeleted");
                String pgMeTrnRefNo = bundle.getString("pgMeTrnRefNo");
                String yblRefId = bundle.getString("yblRefId");
                String virtualAddress = bundle.getString("virtualAddress");
                String registrationDate = bundle.getString("");
                String add1 = bundle.getString("add1");
                String add2 = bundle.getString("add2");
                String add3 = bundle.getString("add3");
                String bankName = "",accno = "",recipientId = "";
                JSONObject jData = new JSONObject();
                try{
                    jData.put("vpa",virtualAddress);
                    jData.put("registrationDate",registrationDate);
                    jData.put("additional",add1+add2+add3);
                    jData.put("yblRefId",yblRefId);
                    String parts[] = add1.split ( "!" );
                    if(parts != null && parts.length > 3){
                        recipientId = parts[0];
                        accno = parts[1];
                        bankName = parts[2];
                        jData.put ( "recipient_id",  recipientId);
                        jData.put ( "accno",accno);
                        jData.put ( "bank_name",bankName );
                    }
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
                Toast.makeText(getActivity(),statusdesc,Toast.LENGTH_SHORT).show();
                FunduUser.setVpa(virtualAddress);
                FunduUser.setBankName ( bankName );
                FunduUser.setAccountNo ( accno );
                FunduUser.setRecipientId ( recipientId );

                UpdateCustomer request = new UpdateCustomer(getActivity());
                request.setData(jData);
                request.start();
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                getActivity().startActivity(intent);
                getActivity().finish();

            }
            else{

                Toast.makeText(getActivity(),statusdesc,Toast.LENGTH_SHORT).show();
                try {
                    String message = "";
                    for(String key : bundle.keySet()) {
                        message = "Param: "+key + "=" + bundle.get(key);
                        Crashlytics.log(message);
                    }
                    Crashlytics.log("contact_id:"+FunduUser.getContactId());
                    Date date = new Date();
                    Throwable upiRegistrationException = new Throwable("Yes Bank UPI Error"+date.toString());
                    Crashlytics.logException(upiRegistrationException);
                }catch(Exception e){
                    e.printStackTrace();
                }

            }


        }
    }
    public void sendDummyVPA(){

        String virtualAddress = FunduUser.getContactId()+"@yesbank";
        String accountNo = "YESDUMMY12345";
        String bankName  = "YESB Dummy";
        String recipientId = "1111111";
        String yblRefId = "Dummy1234";
        Date now = new Date();
        JSONObject jData = new JSONObject();
        try{
            jData.put("vpa",virtualAddress);
            jData.put("registrationDate",now.toString());
            jData.put("address","DummyAddress");
            jData.put("yblRefId",yblRefId);
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        Toast.makeText(getActivity(),"Registered Successfully",Toast.LENGTH_SHORT).show();
        FunduUser.setVpa(virtualAddress);
        FunduUser.setRecipientId(recipientId);
        FunduUser.setBankName(bankName);
        FunduUser.setAccountNo(accountNo);
        UpdateCustomer request = new UpdateCustomer(getActivity());
        request.setData(jData);
        request.start();
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        getActivity().startActivity(intent);
        getActivity().finish();
    }
    @Override
    public boolean onBackPressed() {
        return false;
    }
}
