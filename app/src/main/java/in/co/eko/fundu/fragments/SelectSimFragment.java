package in.co.eko.fundu.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import java.util.ArrayList;

import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.R;
import in.co.eko.fundu.activities.UserOnboardingActivity;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.views.customviews.CustomTextView;
import in.co.eko.fundu.models.Contact;
import in.co.eko.fundu.requests.ContactCreateUpdateRequest;
import in.co.eko.fundu.utils.Fog;
import in.co.eko.fundu.utils.FunduAnalytics;
import in.co.eko.fundu.utils.SimSuscriptionManager;
import in.co.eko.fundu.utils.Utils;
import in.co.eko.fundu.views.CustomProgressDialog;


public class SelectSimFragment extends BaseFragment implements
        ContactCreateUpdateRequest.OnContactResults,
         View.OnClickListener {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String simName,simNumber;
    public static CustomProgressDialog dialog;
    private ContactCreateUpdateRequest request;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    public static String countryShortname = "";

    SimSuscriptionManager simSuscriptionManager = new SimSuscriptionManager(getActivity());

    private String locationCountry = "India";
    private String phoneNumber;
    private Boolean isclickavailable = false;


    private OnFragmentInteractionListener mListener;

    private TextView textViewSim1, textViewSim2, textViewSim1Number, textViewSim2Number;
    private ImageView imageViewSim1, imageViewSim2;
    private LinearLayout linearLayoutPhoneNumber, linearLayoutNoSimCard, linearLayoutSim1, linearLayoutSim2;
    private View view1;

    private CustomTextView textViewTiltle, textViewMessage;


    public SelectSimFragment() {
        // Required empty public constructor
    }


    public static SelectSimFragment newInstance(Contact param1, String param2) {
        SelectSimFragment fragment = new SelectSimFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    private void getCountriesFromNetwork() {
        Activity activity = getActivity();

        if (activity != null && activity instanceof UserOnboardingActivity) {
            Fog.d("getCountries","SelectSimFragmenet");
            dialog.show();
            ((UserOnboardingActivity) activity).getCountries();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dialog = new CustomProgressDialog(getActivity());
        request = new ContactCreateUpdateRequest(getActivity(), pref);
        request.setParserCallback(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_select_sim, container, false);


        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);
        setOnClickListeners();


    }

    private void setOnClickListeners() {

        linearLayoutSim1.setOnClickListener(this);
        linearLayoutSim2.setOnClickListener(this);


    }

    private void init(View view) {

        linearLayoutSim1 = (LinearLayout) view.findViewById(R.id.linearLayout_sim1);
        linearLayoutSim2 = (LinearLayout) view.findViewById(R.id.linearLayout_sim2);
        linearLayoutNoSimCard = (LinearLayout) view.findViewById(R.id.linearLayout_noSimCard);
        linearLayoutPhoneNumber = (LinearLayout) view.findViewById(R.id.linearLayout_phone_number);


        textViewSim1 = (TextView) view.findViewById(R.id.textView_sim1);
        textViewSim2 = (TextView) view.findViewById(R.id.textView_sim2);
        textViewTiltle = (CustomTextView) view.findViewById(R.id.textView_title);
        textViewMessage = (CustomTextView) view.findViewById(R.id.textView_message);
        textViewSim1Number = (TextView) view.findViewById(R.id.textView_sim1_number);
        textViewSim2Number = (TextView) view.findViewById(R.id.textView_sim2_number);

        imageViewSim1 = (ImageView) view.findViewById(R.id.img_sim1);
        imageViewSim2 = (ImageView) view.findViewById(R.id.img_sim2);

        view1                 = view.findViewById(R.id.view1);
        Utils.getSimDetails(getActivity());


        FunduAnalytics.getInstance(getActivity()).sendScreenName("EnterPhoneNumber");

        if (isSimSupport(getActivity())) {

            linearLayoutPhoneNumber.setVisibility(View.VISIBLE);
            checkAndDisplaySimInfo();
        } else {
            linearLayoutNoSimCard.setVisibility(View.VISIBLE);
            linearLayoutPhoneNumber.setVisibility(View.GONE);
        }
        if (Utils.getCountryID().equalsIgnoreCase("IN")){
            countryShortname = "IND";
        }
        else if (Utils.getCountryID().equalsIgnoreCase("KE")) {
            countryShortname = "KEN";
        }
        FunduUser.setCountryShortName(countryShortname);

    }

    private void checkAndDisplaySimInfo() {

        if (!Utils.isDualSim(getActivity())) {
            view1.setVisibility(View.GONE);
            linearLayoutSim2.setVisibility(View.GONE);

        }

        getSimDetails();


    }

    private void getSimDetails() {

        displaySimName();
        displaySimNumber();

    }

    private void displaySimNumber() {

        TextView[] textViewNumbers = new TextView[]{textViewSim1Number, textViewSim2Number};
        String simTwoNumber, simOneNumber,simNumber;
        SimSuscriptionManager simSuscriptionManager = new SimSuscriptionManager(getActivity());
        ArrayList<String> simNumbers = simSuscriptionManager.getSuscriptionSimNumbers();
        if(simNumbers.size()==1)linearLayoutSim2.setVisibility(View.GONE);
        else linearLayoutSim2.setVisibility(View.VISIBLE);
        for(int i=0;i<simNumbers.size();i++){

           // simOneNumber = addCountryCode(simNumbers.get(i));
            textViewNumbers[i].setText(addCountryCode(simNumbers.get(i)));

        }
    }


    private String addCountryCode(String simNumber) {

        String localNumber ;
        String country_code = Utils.GetCountryZipCode(getActivity());
        Fog.d("COUNTRYID","country_code"+country_code);
        if (Utils.getCountryID().equalsIgnoreCase("254")) {

            localNumber = simNumber.substring(0, 3);
        } else {
            localNumber = simNumber.substring(0, 2);

        }

        Fog.d("simOneNumber", "simOneNumber" + localNumber);

        if (country_code.equalsIgnoreCase(localNumber)) {
            simNumber = simNumber.replace(localNumber, "+" + country_code + " ");

        } else {
            simNumber = "+" + country_code + " " + simNumber;
        }

        Fog.d("simOneNumber", "simOneNumber" + simNumber);
        return simNumber;

    }
    private void displaySimName() {

        TextView[] textViews = new TextView[]{textViewSim1, textViewSim2};
        ImageView[] imageViews = new ImageView[]{imageViewSim1, imageViewSim2};

        try {
            ArrayList<String> simNames = simSuscriptionManager.getSimNames();
            if(simNames.size()==1)linearLayoutSim2.setVisibility(View.GONE);
            else linearLayoutSim2.setVisibility(View.VISIBLE);
            for(int i=0;i<simNames.size();i++){
                int j = i + 1;
                textViews[i].setText(simNames.get(i) + " - Sim" + j);
                displaySimImage(simNames.get(i), imageViews[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();

        }


    }

    private void displaySimImage(String s,ImageView imageView) {
        if(s.trim().equalsIgnoreCase("Airtel"))imageView.setImageResource(R.drawable.ic_airtel);
        else if(s.trim().toUpperCase().contains("JIO"))imageView.setImageResource(R.drawable.ic_jio);
        else if(s.trim().contains("MTS"))imageView.setImageResource(R.drawable.ic_mts);
        else if(s.trim().contains("Idea"))imageView.setImageResource(R.drawable.ic_idea);
        else if(s.trim().contains("RELIANCE"))imageView.setImageResource(R.drawable.ic_reliance);
        else if(s.trim().contains("MTNL"))imageView.setImageResource(R.drawable.ic_mtnl);
        else if(s.trim().contains("VODAFONE"))imageView.setImageResource(R.drawable.ic_vodafone);
        else if(s.trim().contains("AIRCEL"))imageView.setImageResource(R.drawable.ic_aircel);
        else if(s.trim().contains("BSNL"))imageView.setImageResource(R.drawable.ic_bsnl);
        else if(s.trim().contains("DOCOMO"))imageView.setImageResource(R.drawable.ic_docomo);
        else if(s.trim().contains("SAFARICOM"))imageView.setImageResource(R.drawable.ic_safaricom);
        else if(s.trim().contains("YU"))imageView.setImageResource(R.drawable.ic_telkom);
        else if(s.trim().contains("EQUITEL"))imageView.setImageResource(R.drawable.ic_equitel);
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }



    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }




    @Override
    public void onContactResponse(Contact contact) {
       if (dialog.isShowing()) {
            dialog.dismiss();
        }
        Fog.d("getCountries","onContactResponse"+"Select");
       // String mobile = phoneNumber;
        String mobile = removeCountryCode(phoneNumber);
        Fog.d("mobile","mobile"+mobile);
        FunduUser.setUserMobileVerified(true);
        FunduUser.setCountryShortName(countryShortname);
        readContacts();
    }

    private String removeCountryCode(String phoneNumber) {

        if(phoneNumber.contains("+91")){
            phoneNumber = phoneNumber.replace("+91","");
        }
        else if(phoneNumber.contains("254")){
            phoneNumber = phoneNumber.replace("254","");
        }

        return phoneNumber;
    }


    private void readContacts() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            checkContactVerified();
        }
    }


    private void checkContactVerified() {

        gotoVerifyFragment();
    }

    private void gotoVerifyFragment() {

        pref.putBoolean(Constants.SENT_TOKEN_TO_SERVER, true);
        pref.putString(Constants.SimName,simName);
        pref.putString(Constants.SimNumber,simNumber);
        Bundle bundle = new Bundle();
        bundle.putString(FRAGMENT_NAME, "VerifyCodeFragment");
        onButtonPressed(bundle);
    }

    @Override
    public void onContactError(VolleyError error) {
        if(dialog.isShowing()){
            dialog.hide();
        }
        Toast.makeText(getActivity(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {


        switch (view.getId()){
            case R.id.linearLayout_sim1:
                if (Utils.isNetworkAvailable(getActivity())) {
                    phoneNumber = textViewSim1Number.getText().toString().replaceAll("\\s+", "");
                    if (phoneNumber.contains("xxxxxxxxxx") || phoneNumber.contains("xxxxxxxxx")) {
                        gotoEnterPhoneNumberFragemnt();
                    } else {
                        simName = textViewSim1.getText().toString();
                        simNumber = textViewSim1Number.getText().toString();
                        FunduUser.setCountryShortName(countryShortname);
                        Fog.d("phoneNumber", "phoneNumber" + phoneNumber);
                        if(Utils.getCountryID().equalsIgnoreCase("IN")){
                            phoneNumber = phoneNumber.replace("+91","");
                        }
                        else{
                            phoneNumber = phoneNumber.replace("+254","");
                        }
                        FunduUser.setContactId(phoneNumber);
                        callRegisterWebService();
                    }
                }
                break;
            case R.id.linearLayout_sim2:
                if (Utils.isNetworkAvailable(getActivity())) {
                  try{


                    String localnum = textViewSim2Number.getText().toString().replaceAll("\\s+","");
                    phoneNumber = textViewSim2Number.getText().toString().replaceAll("\\s+","");
                    if(phoneNumber.contains("xxxxxxxxxx")||phoneNumber.contains("xxxxxxxxx")){
                        gotoEnterPhoneNumberFragemnt();
                    }
                    else{

                        simName = textViewSim2.getText().toString();
                        simNumber = textViewSim2Number.getText().toString();
                        Fog.d("phoneNumber","phoneNumber"+phoneNumber);
                        if(Utils.getCountryID().equalsIgnoreCase("IN")){
                            phoneNumber = phoneNumber.replace("+91","");
                        }
                        else{
                            phoneNumber = phoneNumber.replace("+254","");
                        }
                        FunduUser.setCountryShortName(countryShortname);
                        FunduUser.setContactId(phoneNumber);
                        callRegisterWebService();
                    }

                  }

                  catch (Exception e){
                      e.printStackTrace();
                  }
                }
                    break;


        }


    }

    private void gotoEnterPhoneNumberFragemnt() {
        Bundle bundle = new Bundle();
        bundle.putString(FRAGMENT_NAME, "EnterPhoneNumberFragment");
        onButtonPressed(bundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        getCountriesFromNetwork();
    }

    private void  callRegisterWebService() {
        dialog.show();
        request.setContact();
        request.start();
    }


    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }


    public static boolean isSimSupport(Context context)
    {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);  //gets the current TelephonyManager
        return !(tm.getSimState() == TelephonyManager.SIM_STATE_ABSENT);

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (view1 != null) {
            ViewGroup parentViewGroup = (ViewGroup) view1.getParent();
            if (parentViewGroup != null) {
                parentViewGroup.removeAllViews();
            }
        }
    }
    @Override
    public boolean onBackPressed() {
        return false;
    }




}
