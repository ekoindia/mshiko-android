package in.co.eko.fundu.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.security.MessageDigest;

import in.co.eko.fundu.R;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.requests.ResetFunduPinRequest;
import in.co.eko.fundu.utils.Fog;
import in.co.eko.fundu.utils.Utils;


/**
 * Created by user on 1/29/17.
 */

public class ResetConfirmFunduPin extends BaseFragment implements View.OnClickListener, TextWatcher, TextView.OnEditorActionListener{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ProgressDialog dialog;
    private EditText  newPin, reEnterPin;
    private Button changePin;
    ResetFunduPinRequest resetFunduPinRequest;

    public static ResetConfirmFunduPin newInstance(){
        return new ResetConfirmFunduPin();
    }
    public static ResetConfirmFunduPin newInstance(String param1, String param2) {
        ResetConfirmFunduPin fragment = new ResetConfirmFunduPin();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ResetConfirmFunduPin() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialog = new ProgressDialog(getActivity());
        dialog.setCancelable(false);
        dialog.setMessage("Please Wait...");
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reset_confirm_fpin, container, false);
        newPin = (EditText) view.findViewById(R.id.newPin);
        reEnterPin = (EditText) view.findViewById(R.id.reEnterPin);
        changePin = (Button) view.findViewById(R.id.changePin);
        changePin.setEnabled(false);
        reEnterPin.addTextChangedListener(this);
        newPin.addTextChangedListener(this);
        reEnterPin.setOnEditorActionListener(this);
        changePin.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.changePin){
            if (newPin.getText().toString().equalsIgnoreCase("")
                    && reEnterPin.getText().toString().equalsIgnoreCase("")){
                Toast.makeText(getContext(), "Please Enter and Re-Enter Fundu PIN", Toast.LENGTH_SHORT).show();
            }
            else if (newPin.length()<4){
                Toast.makeText(getContext(), "Please Enter New 4 digit Fundu PIN", Toast.LENGTH_SHORT).show();
            }

            else if (reEnterPin.length()<4){
                Toast.makeText(getContext(), "Please Re-Enter 4 digit Fundu PIN", Toast.LENGTH_SHORT).show();
            }

            else {


                resetFunduPinRequest = new ResetFunduPinRequest(getActivity());
                resetFunduPinRequest.setData(FunduUser.getCustomerId(), FunduUser.getCountryShortName(),
                        md5(newPin.getText().toString()), "System");
                resetFunduPinRequest.setParserCallback(new ResetFunduPinRequest.OnResetFunduPinResults() {
                    @Override
                    public void onResetFunduPinResponse(JSONObject response) {
                        dialog.dismiss();
                        if (response!=null){
                            try{
                                String status = response.optString("status");
                                if (status!=null){
                                    if (status.equalsIgnoreCase("Success")){
                                        Toast.makeText(getActivity(), "PIN Changed Successfully!", Toast.LENGTH_LONG).show();
                                        newPin.setText("");
                                        reEnterPin.setText("");
                                        Utils.hideSoftKeyboard(getActivity());
                                        Activity activity = getActivity();
                                        activity.onBackPressed();
//                                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                                        fragmentTransaction.replace(R.id.fragmentContainer, TabFragment.newInstance());
//                                        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                                        HomeActivity.toolbar.setTitle(getString(R.string.title_home));
//                                        fragmentTransaction.commit();
                                    }
                                    else if (status.equalsIgnoreCase("ERROR")){
                                        Toast.makeText(getActivity(), response.optString("message"),Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                            catch (Exception e){

                            }
                        }
                    }

                    @Override
                    public void onResetFunduPinError(VolleyError error) {
                        dialog.dismiss();
                        Fog.d("ChangePin", "Error");
                    }


                });
                if (Utils.isNetworkAvailable(getActivity())) {
                    dialog.show();

                resetFunduPinRequest.start();
                }
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() <4) {
            changePin.setEnabled(false);
//            view1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
//            texterror.setText("");
            reEnterPin.setTextColor(getResources().getColor(android.R.color.black));
        } else {
            if (s.length()==4) {
                if (reEnterPin.length() == 4 && newPin.length() == 4) {
                    if (reEnterPin.getText().toString().equals(newPin.getText().toString())) {
                        reEnterPin.setTextColor(getResources().getColor(android.R.color.black));
//                    view1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        changePin.setEnabled(true);
//                    texterror.setText("");
                    } else {
                        reEnterPin.setTextColor(getResources().getColor(android.R.color.holo_red_light));
//                    view1.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                        Toast.makeText(getContext(), "Your re-enter Fundu PIN doesn't match.\nPlease enter the correct PIN", Toast.LENGTH_LONG).show();
//                    texterror.setText("Your re-enter Fundu PIN doesn't match.\nPlease enter the correct PIN");
                    }
                }
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        return false;
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
    @Override
    public boolean onBackPressed() {
        return false;
    }
}
