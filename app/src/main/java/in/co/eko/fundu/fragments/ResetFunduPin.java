package in.co.eko.fundu.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

import in.co.eko.fundu.R;
import in.co.eko.fundu.activities.HomeActivity;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.requests.CheckAnswersResetPinRequest;
import in.co.eko.fundu.requests.SecurityQuestionCardResquest;
import in.co.eko.fundu.utils.Fog;
import in.co.eko.fundu.utils.Utils;

/**
 * Created by Rahul on 1/28/17.
 */

    public class ResetFunduPin extends BaseFragment implements TextWatcher, TextView.OnEditorActionListener, SecurityQuestionCardResquest.OnSecurityQuestionCardResults
    , CheckAnswersResetPinRequest.OnCheckAnswersResetPinResults{
        private static final String ARG_PARAM1 = "param1";
        private static final String ARG_PARAM2 = "param2";

        private ProgressDialog dialog;
        private Button submit;
        private TextView question, quesCard;
        private EditText cardno;
        private EditText answer, expmonth, expyear;
        private SecurityQuestionCardResquest securityQuestionCardResquest;
        private CheckAnswersResetPinRequest checkAnswersResetPinRequest;
    private int minimum_length = 0, maximum_length = 20;
    private boolean openCalender = false, openSelectYear = false;
    Calendar c;
    int Byear,Bmonth, Bday;
    int selectedMonth = 0, selectedYear = 0, selectedYearAnswer = 0;
    private String expiry="", expirymonth="", expiryyear="";
    String question_id, questionValue, answer_mode, answer_check, place_holder;


public static ResetFunduPin newInstance(String param1, String param2) {
            ResetFunduPin fragment = new ResetFunduPin();
            Bundle args = new Bundle();
            args.putString(ARG_PARAM1, param1);
            args.putString(ARG_PARAM2, param2);
            fragment.setArguments(args);
            return fragment;
        }

        public ResetFunduPin() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (Utils.isNetworkAvailable(getContext())) {
                dialog = new ProgressDialog(getActivity());
                dialog.setCancelable(false);
                dialog.setMessage("Please Wait...");
                securityQuestionCardResquest = new SecurityQuestionCardResquest(getContext());
                securityQuestionCardResquest.setParserCallback(this);
                checkAnswersResetPinRequest = new CheckAnswersResetPinRequest(getContext());
                checkAnswersResetPinRequest.setParserCallback(this);
                Fog.d("CustomerId",""+FunduUser.getCustomerId());
                securityQuestionCardResquest.setData(FunduUser.getCustomerId());
                securityQuestionCardResquest.start();
                dialog.show();
                c = Calendar.getInstance();
                Bmonth = c.get(Calendar.MONTH);
                Byear = c.get(Calendar.YEAR);
                Bday = c.get(Calendar.DATE);
                setHasOptionsMenu(true);
            } else {
                getActivity().onBackPressed();
               /* FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainer, TabFragment.newInstance());
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                HomeActivity.toolbar.setTitle(getString(R.string.title_home));
                fragmentTransaction.commit();*/
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.reset_fpin_question, container, false);
            answer = (EditText) view.findViewById(R.id.answer);
            cardno = (EditText) view.findViewById(R.id.cardNumber);

            expmonth = (EditText) view.findViewById(R.id.editexpiryMonth);
            expyear = (EditText) view.findViewById(R.id.editexpiryYear);
            submit = (Button) view.findViewById(R.id.submitButton);
            question = (TextView) view.findViewById(R.id.question);
            quesCard = (TextView) view.findViewById(R.id.card4digit);
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (answer.getText().toString().trim().equalsIgnoreCase("")
                            || cardno.length()<16 || expyear.getText().toString().trim().equalsIgnoreCase("")
                            || expyear.getText().toString().trim().equalsIgnoreCase("")){
                        Utils.showShortToast(getActivity(),"Please fill all the boxes");
                    }
                    else{
                        if (Utils.isNetworkAvailable(getActivity())) {
                            dialog.show();
                            checkAnswersResetPinRequest.setData(question_id, answer.getText().toString().trim(),
                                    cardno.getText().toString().trim(),  expirymonth+ expiryyear, FunduUser.getCustomerId());
                            checkAnswersResetPinRequest.start();
                        }
                    }
                }
            });
            answer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (openCalender){
                        opencal();
                    }
                    else if(openSelectYear){
                        String[] years = new String[55];
                        int yr = 1980;
                        for (int i = 0; i < years.length; i++){
                            years[i] = String.valueOf(yr);
                            yr++;
                        }
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Select Year")
                                .setSingleChoiceItems(years, selectedYearAnswer, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        int selectedPosition = which;
                                        String item  = ((AlertDialog) dialog).getListView().getItemAtPosition(selectedPosition).toString();
                                        Fog.e("Itme", item);
                                        answer.setText(item);
                                        selectedYearAnswer = selectedPosition;
                                        Fog.e("POSITION", String.valueOf(selectedPosition)+" which "+which);
                                    }
                                })

                                .show();
                    }
                }
            });
            expmonth.setInputType(InputType.TYPE_NULL);
            expyear.setInputType(InputType.TYPE_NULL);
            expyear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View view = getActivity().getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    String[] years = new String[100];
                    int yr = 2017;
                    for (int i = 0; i < years.length; i++){
                        years[i] = String.valueOf(yr);
                        yr++;
                    }
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Select Year")
                            .setSingleChoiceItems(years, selectedYear, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
//
//                            }
//                        })
//                        .setPositiveButton(R.string.string_ok, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                    int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                                    String item  = ((AlertDialog) dialog).getListView().getItemAtPosition(selectedPosition).toString();
                                    Fog.e("Itme", item);
                                    expyear.setText(item);
                                    expiryyear = item.substring(2);
                                    selectedYear = selectedPosition;
                                    Fog.e("POSITION", String.valueOf(selectedPosition));
                                }
                            })
                            .show();
                }
            });

            expmonth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                createDialogWithoutDateField().show();
                    View view = getActivity().getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Select Month")
                            .setSingleChoiceItems(months, selectedMonth, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
//
                                    dialog.dismiss();

                                    int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                                    selectedMonth = selectedPosition;
                                    Fog.e("POSITION", String.valueOf(selectedPosition));

                                    switch (selectedPosition) {
                                        case 0:
                                            expmonth.setText("01");
                                            expirymonth = "01";
                                            break;
                                        case 1:
                                            expmonth.setText("02");
                                            expirymonth = "02";
                                            break;
                                        case 2:
                                            expmonth.setText("03");
                                            expirymonth = "03";
                                            break;
                                        case 3:
                                            expmonth.setText("04");
                                            expirymonth = "04";
                                            break;
                                        case 4:
                                            expmonth.setText("05");
                                            expirymonth = "05";
                                            break;
                                        case 5:
                                            expmonth.setText("06");
                                            expirymonth = "06";
                                            break;
                                        case 6:
                                            expmonth.setText("07");
                                            expirymonth = "07";
                                            break;
                                        case 7:
                                            expmonth.setText("08");
                                            expirymonth = "08";
                                            break;
                                        case 8:
                                            expmonth.setText("09");
                                            expirymonth = "09";
                                            break;
                                        case 9:
                                            expmonth.setText("10");
                                            expirymonth = "10";
                                            break;
                                        case 10:
                                            expmonth.setText("11");
                                            expirymonth = "11";
                                            break;
                                        case 11:
                                            expmonth.setText("12");
                                            expirymonth = "12";
                                            break;
                                        default:
                                            expmonth.setText("01");
                                            expirymonth = "01";
                                            break;
                                    }
                                }
                            })
                            .show();
                }
            });
            return view;
        }

    void opencal(){
        DatePickerDialog dpd = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Byear = year;
                Bmonth = monthOfYear;
                Bday = dayOfMonth;
                if (monthOfYear <9 && dayOfMonth <9 ){
                    answer.setText(year + "-0" + (monthOfYear + 1) + "-0" + dayOfMonth);
                }
                else if (dayOfMonth < 9) {
                    answer.setText(year + "-" + (monthOfYear + 1) + "-0" + dayOfMonth);
                }
                else if (monthOfYear < 9) {
                    answer.setText(year + "-0" + (monthOfYear + 1) + "-" + dayOfMonth);
                }
                else {
                    answer.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                }
            }
        }, Byear, Bmonth, Bday);
        dpd.getDatePicker().setMaxDate(new Date().getTime());
        dpd.show();
    }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            return false;
        }

    @Override
    public void onSecurityQuestionCardResponse(String object) {
    dialog.dismiss();
        try {
            JSONObject object1 = new JSONObject(object);
            Fog.e("SecQues", object);
            if (object1.has("status")) {
                if (object1.getString("status").equalsIgnoreCase("SUCCESS")) {
                    JSONObject data = new JSONObject(object1.getString("data"));
                    openSelectYear = false;
                    openCalender = false;
                    question_id = data.optString("question_id");
                    questionValue = data.optString("question_value");
                    answer_mode = data.optString("answer_mode");
                    answer_check = data.optString("answer_check");
                    minimum_length = Integer.parseInt(data.optString("answer_min_length"));
                    maximum_length = Integer.parseInt(data.optString("answer_max_length"));
                    place_holder = data.optString("place_holder");


                    question.setText("Q. " + questionValue);
                    answer.setText("");
                    answer.setHint(place_holder);
                    answer.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maximum_length)});
                    if (answer_mode.equals("text") && answer_check.equals("Numeric")) {
                        answer.setInputType(InputType.TYPE_CLASS_NUMBER);
                    } else if (answer_mode.equals("select") && answer_check.equals("Numeric")) {
                        answer.setInputType(InputType.TYPE_NULL);
                        openSelectYear = true;
                    } else if (answer_mode.equals("Calender") && answer_check.equals("Numeric")) {
                        answer.setInputType(InputType.TYPE_NULL);
                        openCalender = true;
                    } else {
                        answer.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                        answer.setFilters(new InputFilter[]{
                                new InputFilter() {
                                    @Override
                                    public CharSequence filter(CharSequence src, int start, int end, Spanned dest, int dstart, int dend) {
                                        if (src.equals("")) {
                                            return src;
                                        }
                                        if (src.toString().matches("[a-zA-Z ]+")) {
                                            return src;
                                        }
                                        return "";
                                    }
                                }
                        });
                    }

                    quesCard.setText(" Enter Card Number ending with " + data.optString("card_number"));
                } else {
                    Utils.showShortToast(getActivity(), object1.optString("message"));
                    getActivity().onBackPressed();
                    /*FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentContainer, TabFragment.newInstance());
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    HomeActivity.toolbar.setTitle(getString(R.string.title_home));
                    fragmentTransaction.commit();*/
                    HomeActivity.Signout(getContext());
                }
            }
        }
        catch (Exception e) {
        }
    }

    @Override
    public void onSecurityQuestionCardError(VolleyError error) {
        dialog.dismiss();
        getActivity().onBackPressed();
        /*FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, TabFragment.newInstance());
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        HomeActivity.toolbar.setTitle(getString(R.string.title_home));
        fragmentTransaction.commit();*/
        Utils.showShortToast(getActivity(),"Error found!\nPlease try again later.");
    }


    @Override
    public void onCheckAnswersResetPinResponse(JSONObject response) {
            dialog.dismiss();
        if (response.optString("status").equalsIgnoreCase("SUCCESS")){
            Utils.hideSoftKeyboard(getActivity());
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, ResetConfirmFunduPin.newInstance());
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.commit();
        }
        else{
        Utils.showShortToast(getContext() , response.optString("message"));
        }
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof HomeActivity)
            ((HomeActivity)activity).hideHamburgerIcon();
    }

    @Override
    public boolean onBackPressed() {

        return false;
    }

    @Override
    public void onCheckAnswersResetPinError(VolleyError error) {
    dialog.dismiss();
    }
}
