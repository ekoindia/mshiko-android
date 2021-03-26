package in.co.eko.fundu.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.security.MessageDigest;

import in.co.eko.fundu.R;
import in.co.eko.fundu.models.Contact;
import in.co.eko.fundu.utils.Utils;
import in.co.eko.fundu.views.Pinview;

/**
 * Created by Rahul on 12/6/16.
 */

public class CreateFunduPinFragment extends BaseFragment implements  View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Contact mParam1;
    private String mParam2;
    private ProgressDialog dialog;
    View view1;
    private String fpin;
    private Button createPinButton;
    private EditText[] editTexts ;
    private Pinview mNewPin,mConfimPin;
    static TextView texterror;

    public static CreateFunduPinFragment newInstance(Contact param1, String param2) {
        CreateFunduPinFragment fragment = new CreateFunduPinFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public CreateFunduPinFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = (Contact) getArguments().getSerializable(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
//        verifyOtpRequest = new VerifyOtpRequest(getActivity());
//        verifyOtpRequest.setParserCallback(this);
        dialog = new ProgressDialog(getActivity());
        dialog.setCancelable(false);
        dialog.setMessage("Please wait...");

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_fundu_pin, container, false);
        createPinButton = (Button) view.findViewById(R.id.createPinButton);
        mNewPin = (Pinview)view.findViewById(R.id.newPin);
        mConfimPin = (Pinview)view.findViewById(R.id.confirmPin);

        view1 = view.findViewById(R.id.view1);
        texterror = (TextView) view.findViewById(R.id.texterror);

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        createPinButton.setOnClickListener(this);


        /*linearLayoutConfirmPin.addView(customDynamicEditTextCoirmnfPin.addEditView());
        linearLayoutPin.addView(customDynamicEditTextPin.addEditView());*/

        return view;
    }
    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.createPinButton) {
            getPin();
            //Fog.d("customDynamicEdit",""+customDynamicEditTextPin.getText());
            //Fog.d("customDynamicEdit",""+customDynamicEditTextCoirmnfPin.getText());
            //String fundupinmd5 = md5(fpinEditText.getText().toString());
            if(!TextUtils.isEmpty(fpin)){
                String fundupinmd5 = md5(fpin);
                gototoQuestionAnswerScreeen(fundupinmd5);
            }
            else{
                Toast.makeText(getActivity(), "Please enter Pin to continue.", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public static final String md5(final String toEncrypt) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("md5");
            digest.update(toEncrypt.getBytes());
            final byte[] bytes = digest.digest();
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(String.format("%02X", bytes[i]));
            }
            return sb.toString().toLowerCase();
        } catch (Exception exc) {
            return ""; // Impossibru!
        }
    }

    void gototoQuestionAnswerScreeen(String fpin){
        if (Utils.isNetworkAvailable(getActivity())) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            //reenterFPinEditText.setText("");
            //fpinEditText.setText("");
            //String funduPin = fpin;
           // String funduPin = confirmFunduPin;
            String funduPin = fpin;
            Bundle bundle = new Bundle();
            bundle.putString(FRAGMENT_NAME, "QuestionAnswerFragment");
            bundle.putSerializable(Contact.class.getSimpleName(), mParam1);
            bundle.putString("Fundu_Pin", fpin);
            pref.putString("FunduPin", fpin);
            onButtonPressed(bundle);
        }
    }




    public void getPin() {

            String newPin     =   mNewPin.getValue();
            String confirmPin =   mConfimPin.getValue();
            validatePin(newPin,confirmPin);

        //Toast.makeText(getActivity(), "CreateFundu", Toast.LENGTH_SHORT).show();

    }

    private void validatePin(String newPin,String confirmPin) {

        if(newPin.equalsIgnoreCase(confirmPin)){
            fpin = confirmPin;
            createPinButton.setEnabled(true);
            texterror.setVisibility(View.INVISIBLE);

        }
        else {
               //CustomDynamicEditText.onCodeMismatch(getActivity());

               texterror.setVisibility(View.VISIBLE);

        }
    }
    @Override
    public boolean onBackPressed() {
        return false;
    }




}
