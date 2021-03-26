package in.co.eko.fundu.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.security.MessageDigest;

import in.co.eko.fundu.R;
import in.co.eko.fundu.activities.HomeActivity;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.requests.ChangeFunduPinRequest;
import in.co.eko.fundu.utils.Fog;
import in.co.eko.fundu.utils.Utils;
import in.co.eko.fundu.views.Pinview;

/**
 * Created by Rahul on 1/18/17.
 */

public class ChangeFunduPinFragment extends BaseFragment implements View.OnClickListener, TextWatcher, TextView.OnEditorActionListener, View.OnKeyListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ProgressDialog dialog;
   // private EditText oldPin, newPin, reEnterPin;
    private Button changePin;
    private Pinview oldPin,newPin,reEnterPin;
    private String oldPinNum,newPinNum,reEnterPinNum;
    ChangeFunduPinRequest changefpinrequest;

    public static ChangeFunduPinFragment newInstance(){
        return new ChangeFunduPinFragment();
    }
    public static ChangeFunduPinFragment newInstance(String param1, String param2) {
        ChangeFunduPinFragment fragment = new ChangeFunduPinFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ChangeFunduPinFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        dialog = new ProgressDialog(getActivity());
        dialog.setCancelable(false);
        dialog.setMessage("Please Wait...");
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_fpin, container, false);


        oldPin = (Pinview) view.findViewById(R.id.oldPin);
        newPin = (Pinview) view.findViewById(R.id.newPin);
        reEnterPin = (Pinview) view.findViewById(R.id.reEnterPin);

        reEnterPin.setOnKeyListener(this);

        oldPin.setPinViewEventListener(new Pinview.PinViewEventListener() {
            @Override
            public void onDataEntered(Pinview pinview, boolean fromUser) {
               // Toast.makeText(getActivity(), pinview.getValue(), Toast.LENGTH_SHORT).show();
                oldPinNum = pinview.getValue();
                newPin.requestFocus();
            }
        });

        newPin.setPinViewEventListener(new Pinview.PinViewEventListener() {
            @Override
            public void onDataEntered(Pinview pinview, boolean fromUser) {
               // Toast.makeText(getActivity(), pinview.getValue(), Toast.LENGTH_SHORT).show();
                newPinNum = pinview.getValue();
                reEnterPin.requestFocus();
            }
        });


        reEnterPin.setPinViewEventListener(new Pinview.PinViewEventListener() {
            @Override
            public void onDataEntered(Pinview pinview, boolean fromUser) {
                reEnterPinNum = pinview.getValue();
                //Toast.makeText(getActivity(), pinview.getValue(), Toast.LENGTH_SHORT).show();
                validatePin();
            }
        });



        changePin = (Button) view.findViewById(R.id.changePin);
        changePin.setEnabled(false);

        changePin.setOnClickListener(this);
        return view;
    }

    private void validatePin() {

        if(newPinNum.equalsIgnoreCase(reEnterPinNum)){
            if(TextUtils.isEmpty(oldPinNum)){
                Toast.makeText(getActivity(), "Please Enter all the feilds.", Toast.LENGTH_SHORT).show();
                return;
             }
             else {
                changePin.setEnabled(true);
            }
        }
        else {

            Toast.makeText(getActivity(), "Pin does not matched.", Toast.LENGTH_SHORT).show();
            new CountDownTimer(10000, 1000) {

                public void onTick(long millisUntilFinished) {
                    newPin.resetValues();
                    reEnterPin.resetValues();
                    newPinNum = "";
                    reEnterPinNum = "";
                    newPin.setPinBackgroundRes(R.drawable.code_mismatch);
                    reEnterPin.setPinBackgroundRes(R.drawable.code_mismatch);
                    newPin.requestFocus();
                    //here you can have your logic to set text to edittext
                }

                public void onFinish() {
                    newPin.requestFocus();
                    newPin.setPinBackgroundRes(R.drawable.code_letter_back);
                    reEnterPin.setPinBackgroundRes(R.drawable.code_letter_back);
                }

            }.start();

        }




    }

    @Override
    public void onClick(View v) {

       /* if(v.getId()== R.id.changePin){

            if (Utils.isNetworkAvailable(getContext())) {
                if (oldPinNum.equalsIgnoreCase("") && newPinNum.equalsIgnoreCase("")
                        && reEnterPinNum.equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), "Please Enter Current and New Fundu PIN", Toast.LENGTH_SHORT).show();
                } else if (oldPinNum.length() < 4) {
                    Toast.makeText(getContext(), "Please Enter Your 4 digit Current Fundu PIN", Toast.LENGTH_SHORT).show();
                } else if (newPinNum.length() < 4) {
                    Toast.makeText(getContext(), "Please Enter New 4 digit Fundu PIN", Toast.LENGTH_SHORT).show();
                } else if (reEnterPinNum.length() < 4) {
                    Toast.makeText(getContext(), "Please Re-Enter 4 digit Fundu PIN", Toast.LENGTH_SHORT).show();
                } else if (oldPinNum.equalsIgnoreCase(newPinNum.toString())) {
                    Toast.makeText(getContext(), "Current and New Fundu PIN should not be the same", Toast.LENGTH_SHORT).show();
                } else {
                    dialog.show();

                    changefpinrequest = new ChangeFunduPinRequest(getActivity());
                    Fog.d("oldpin",""+oldPinNum+""+newPinNum+" "+reEnterPinNum);
                    changefpinrequest.setData(FunduUser.getCustomerId(), FunduUser.getCountryShortName(), md5(oldPinNum.toString()),
                            md5(newPinNum.toString()), "System");
                    changefpinrequest.setParserCallback(new ChangeFunduPinRequest.OnChangeFunduPinResults() {
                        @Override
                        public void onChangeFunduPinResponse(JSONObject response) {
                            dialog.dismiss();
                            if (response != null) {
                                try {
                                    String status = response.optString("status");
                                    if (status != null) {
                                        if (status.equalsIgnoreCase("Success")) {
                                            Toast.makeText(getActivity(), "PIN Changed Successfully!", Toast.LENGTH_LONG).show();
                                            oldPin.resetValues();
                                            newPin.resetValues();
                                            reEnterPin.resetValues();
                                            Utils.hideSoftKeyboard(getActivity());
                                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                            fragmentTransaction.replace(R.id.fragmentContainer, TabFragment.newInstance());
                                            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                                            HomeActivity.toolbar.setTitle(getString(R.string.title_home));
                                            fragmentTransaction.commit();
                                        } else if (status.equalsIgnoreCase("ERROR")) {
                                            if (response.optString("message").equalsIgnoreCase("Incorrect Fundu Pin")) {
                                                Toast.makeText(getActivity(), "Please enter the correct Fundu PIN", Toast.LENGTH_LONG).show();
                                            } else if (response.optString("message").equalsIgnoreCase("Customer doesn't exist")) {
                                                Utils.showShortToast(getActivity(), response.optString("message"));
                                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                                fragmentTransaction.replace(R.id.fragmentContainer, TabFragment.newInstance());
                                                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                                                HomeActivity.toolbar.setTitle(getString(R.string.title_home));
                                                fragmentTransaction.commit();
                                                HomeActivity.Signout(getContext());
                                            } else
                                                Toast.makeText(getActivity(), response.optString("message"), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                } catch (Exception e) {

                                }
                            }
                        }

                        @Override
                        public void onChangeFunduPinError(VolleyError error) {
                            dialog.dismiss();
                            Fog.d("ChangePin", "Error");
                        }
                    });
                    changefpinrequest.start();
                }
            }


        }*/




        if (v.getId() == R.id.changePin) {
            if (Utils.isNetworkAvailable(getContext())) {
                if (newPin.getValue().equalsIgnoreCase("") && reEnterPin.getValue().toString().equalsIgnoreCase("")
                        && oldPin.getValue().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), "Please Enter Current and New Fundu PIN", Toast.LENGTH_SHORT).show();
                } else if (oldPin.getValue().length() < 4) {
                    Toast.makeText(getContext(), "Please Enter Your 4 digit Current Fundu PIN", Toast.LENGTH_SHORT).show();
                } else if (newPin.getValue().length() < 4) {
                    Toast.makeText(getContext(), "Please Enter New 4 digit Fundu PIN", Toast.LENGTH_SHORT).show();
                } else if (reEnterPin.getValue().length() < 4) {
                    Toast.makeText(getContext(), "Please Re-Enter 4 digit Fundu PIN", Toast.LENGTH_SHORT).show();
                } else if (oldPin.getValue().toString().equalsIgnoreCase(newPin.getValue().toString())) {
                    Toast.makeText(getContext(), "Current and New Fundu PIN should not be the same", Toast.LENGTH_SHORT).show();
                } else {
                    dialog.show();

                    changefpinrequest = new ChangeFunduPinRequest(getActivity());
                    changefpinrequest.setData(FunduUser.getCustomerId(), FunduUser.getCountryShortName(), md5(oldPin.getValue().toString()),
                            md5(newPin.getValue().toString()), "System");
                    changefpinrequest.setParserCallback(new ChangeFunduPinRequest.OnChangeFunduPinResults() {
                        @Override
                        public void onChangeFunduPinResponse(JSONObject response) {
                            dialog.dismiss();
                            if (response != null) {
                                try {
                                    String status = response.optString("status");
                                    if (status != null) {
                                        if (status.equalsIgnoreCase("Success")) {
                                            Toast.makeText(getActivity(), "PIN Changed Successfully!", Toast.LENGTH_LONG).show();
                                            /*oldPin.setText("");
                                            newPin.setText("");
                                            reEnterPin.setText("");*/
                                            oldPin.resetValues();
                                            newPin.resetValues();
                                            reEnterPin.resetValues();
                                            Utils.hideSoftKeyboard(getActivity());
                                            getActivity().onBackPressed();

                                        } else if (status.equalsIgnoreCase("ERROR")) {
                                            if (response.optString("message").equalsIgnoreCase("Incorrect Fundu Pin")) {
                                                Toast.makeText(getActivity(), "Please enter the correct Fundu PIN", Toast.LENGTH_LONG).show();
                                            } else if (response.optString("message").equalsIgnoreCase("Customer doesn't exist")) {
                                                Utils.showShortToast(getActivity(), response.optString("message"));
                                                HomeActivity.Signout(getContext());
                                            } else
                                                Toast.makeText(getActivity(), response.optString("message"), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                } catch (Exception e) {

                                }
                            }
                        }

                        @Override
                        public void onChangeFunduPinError(VolleyError error) {
                            dialog.dismiss();
                            Fog.d("ChangePin", "Error");
                        }
                    });
                    changefpinrequest.start();
                }
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
     /*   if (s.length() <4) {
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

        }*/
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
    public boolean onKey(View view, int i, KeyEvent keyEvent) {

        //if(keyEvent = KeyEvent.IME_MASK_ACTION)
        return false;
    }
    @Override
    public boolean onBackPressed() {

        return false;
    }
}