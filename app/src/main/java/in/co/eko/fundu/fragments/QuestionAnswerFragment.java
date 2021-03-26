package in.co.eko.fundu.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

import in.co.eko.fundu.R;
import in.co.eko.fundu.activities.UserOnboardingActivity;
import in.co.eko.fundu.adapters.QuestionSpinnerAdapter;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.models.Contact;
import in.co.eko.fundu.models.QuestionModel;
import in.co.eko.fundu.requests.GetQuestionRequest;
import in.co.eko.fundu.utils.Fog;
import in.co.eko.fundu.utils.Utils;

/**
 * Created by user on 12/7/16.
 */

public class QuestionAnswerFragment extends BaseFragment implements TextWatcher,AdapterView.OnItemSelectedListener{

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Calendar c;
    String question_value = "";
    int Byear, Bmonth, Bday;
    int selectedYear = 0;
    private Contact mParam1;
    private String mParam2, question_id, FunduPin ="";
    private ProgressDialog dialog;
    private QuestionSpinnerAdapter question_adapter;
    private Spinner question_spinner;
    private EditText edtanswer;
    private Button submitanswerBtn;
    private int minimum_length = 0;
    private boolean openCalender = false, openSelectYear = false;
    private String expiryyear="";
    private Button createPinButton;
    private EditText fpinEditText;
    private EditText reenterfpinEditText;

    public QuestionAnswerFragment() {
        // Required empty public constructor
    }

    public static QuestionAnswerFragment newInstance(Contact param1, String param2) {
        QuestionAnswerFragment fragment = new QuestionAnswerFragment();
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
            FunduPin = getArguments().getString("Fundu_Pin");
        }
        dialog = new ProgressDialog(getActivity());
        dialog.setCancelable(false);
        dialog.setMessage("Please wait...");

        c = Calendar.getInstance();
        Bmonth = c.get(Calendar.MONTH);
        Byear = c.get(Calendar.YEAR);
        Bday = c.get(Calendar.DATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question_answer, container, false);
        question_spinner = (Spinner) view.findViewById(R.id.spinner_question);
        edtanswer = (EditText) view.findViewById(R.id.edtanswer);
        submitanswerBtn = (Button) view.findViewById(R.id.submitanswer);
        submitanswerBtn.setEnabled(true);
        submitanswerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoCardDetailScreen();
            }
        });
        edtanswer.setOnClickListener(new View.OnClickListener() {
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
                        .setSingleChoiceItems(years, selectedYear, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                int selectedPosition = which;
                                String item  = ((AlertDialog) dialog).getListView().getItemAtPosition(selectedPosition).toString();
                                Fog.e("Itme", item);
                                edtanswer.setText(item);
                                selectedYear = selectedPosition;
                                Fog.e("POSITION", String.valueOf(selectedPosition)+" which "+which);
                            }
                        })

//                        .setPositiveButton(R.string.string_ok, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int whichButton) {
//                                dialog.dismiss();
//                                int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
//                                String item  = ((AlertDialog) dialog).getListView().getItemAtPosition(selectedPosition).toString();
//                                Fog.e("Itme", item);
//                                edtanswer.setText(item);
//                                expiryyear = item.substring(2);
//                                selectedYear = selectedPosition;
//                                Fog.e("POSITION", String.valueOf(selectedPosition));
//                            }
//                        })
                        .show();
            }
            }
        });
        if (Utils.isNetworkAvailable(getActivity()))
            getQuestionsfromRequest();
//        else
//        {
//            String stringdata = " [{\"question_id\":\"Q101\",\"question_value\":\"What is your Identity Card Number?\",\"answer_mode\":\"text\",\"answer_check\":\"Numeric\",\"answer_min_length\":\"7\",\"answer_max_length\":\"15\",\"place_holder\":\"ID Number\"},\n" +
//                    " {\"question_id\":\"Q102\",\"question_value\":\"What is your Home County?\",         \"answer_mode\":\"text\",\"answer_check\":\"Alphabet\",\"answer_min_length\":\"3\",\"answer_max_length\":\"15\",\"place_holder\":\"County name\"},\n" +
//                    " {\"question_id\":\"Q103\",\"question_value\":\"What is your Date of Birth?\",        \"answer_mode\":\"Calender\",\"answer_check\":\"Numeric\",\"answer_min_length\":\"7\",\"answer_max_length\":\"15\",\"place_holder\":\"Select DOB\"},\n" +
//                    " {\"question_id\":\"Q104\",\"question_value\":\"Which year did you Graduate?\",       \"answer_mode\":\"text\",\"answer_check\":\"Numeric\",\"answer_min_length\":\"4\",\"answer_max_length\":\"15\",\"place_holder\":\"Graduate Year\"},\n" +
//                    " {\"question_id\":\"Q105\",\"question_value\":\"What is your Mother's First Name?\",  \"answer_mode\":\"text\",\"answer_check\":\"Alphabet\",\"answer_min_length\":\"3\",\"answer_max_length\":\"15\",\"place_holder\":\"Mother's First Name\"}]\n";
//            try {
//                JSONArray array = new JSONArray(stringdata);
//
//                QuestionModel[] arraylist = new QuestionModel[array.length()];
//
//                for (int i = 0; i < array.length(); i++) {
//                    JSONObject obj = array.getJSONObject(i);
//                    QuestionModel bean = new QuestionModel();
//                    bean.setQuestion_id(obj.getString("question_id"));
//                    bean.setQuestion_value(obj.getString("question_value"));
//                    bean.setAnswer_mode(obj.getString("answer_mode"));
//                    bean.setAnswer_check(obj.getString("answer_check"));
//                    bean.setAnswer_min_length(obj.getString("answer_min_length"));
//                    bean.setAnswer_max_length(obj.getString("answer_max_length"));
//                    bean.setPlace_holder(obj.getString("place_holder"));
//                    arraylist[i] = bean;
//                }
//
//
//                question_adapter = new QuestionSpinnerAdapter(getActivity(),android.R.layout.simple_list_item_1,arraylist);
//                question_spinner.setAdapter(question_adapter);
//                question_spinner.setOnItemSelectedListener(QuestionAnswerFragment.this);
//            }
//            catch (Exception e){
//                e.printStackTrace();
//            }
//        }
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
                    edtanswer.setText(year + "-0" + (monthOfYear + 1) + "-0" + dayOfMonth);
                }
                else if (dayOfMonth < 9) {
                    edtanswer.setText(year + "-" + (monthOfYear + 1) + "-0" + dayOfMonth);
                }
                else if (monthOfYear < 9) {
                    edtanswer.setText(year + "-0" + (monthOfYear + 1) + "-" + dayOfMonth);
                }
                else {
                    edtanswer.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                }
            }
        }, Byear, Bmonth, Bday);
        dpd.getDatePicker().setMaxDate(new Date().getTime());
        dpd.show();
    }

    void gotoCardDetailScreen(){
        if (edtanswer.getText().length() > minimum_length - 1) {
            if (question_spinner.getItemAtPosition(0) != null) {
                Utils.hideSoftKeyboard(getActivity());
                Bundle bundle = new Bundle();
                if (pref.getString(Constants.CONTACT_TYPE_PA).equalsIgnoreCase("AGENT"))
                    bundle.putString(FRAGMENT_NAME, "MerchantCardDetailFragment");
                else
                   // bundle.putString(FRAGMENT_NAME, "CardDetailFragment");
                    bundle.putString(FRAGMENT_NAME, "EnterBankAccountFragment");
                bundle.putSerializable(Contact.class.getSimpleName(), mParam1);
                bundle.putString("QuestionKey", question_id);
                bundle.putString("Answer", edtanswer.getText().toString());
                bundle.putString("FunduPin", FunduPin);
                pref.putString(Constants.SEC_ANSWER, edtanswer.getText().toString());
                pref.putString(Constants.SEC_QUESTION, question_id);
                edtanswer.setText("");
                onButtonPressed(bundle);
            } else {
                if (Utils.isNetworkAvailable(getActivity())) {
                    getQuestionsfromRequest();
                    Utils.showLongToast(getActivity(), "Page refreshed! Select the question and answer it again");
                }
            }
        }
            else {
            if (minimum_length == 4) {
                Toast.makeText(getContext(), "Please select the graduate year", Toast.LENGTH_SHORT).show();
            } else if (minimum_length == 7 && question_value.equalsIgnoreCase("What is your Date of Birth?")) {
                Toast.makeText(getContext(), "Please select the date of birth", Toast.LENGTH_SHORT).show();
            } else
            Toast.makeText(getContext(), "Please enter minimum " + minimum_length + " length.", Toast.LENGTH_SHORT).show();
        }

    }

    private void getQuestionsfromRequest(){

        dialog.show();
        GetQuestionRequest request = new GetQuestionRequest(getActivity());
        request.setParserCallback(new GetQuestionRequest.OnQuestionRequestResult() {
            @Override
            public void onQuestionResponse(String response) {
                Fog.e("QUES RESP", response);
                dialog.dismiss();
                try {
                    JSONArray array = new JSONArray(response);
                    QuestionModel[] arraylist=new QuestionModel[array.length()];

                    for(int i=0;i<array.length();i++){
                        JSONObject obj=array.getJSONObject(i);
                        QuestionModel bean=new QuestionModel();
                        bean.setQuestion_id(obj.getString("question_id"));
                        bean.setQuestion_value(obj.getString("question_value"));
                        bean.setAnswer_mode(obj.getString("answer_mode"));
                        bean.setAnswer_check(obj.getString("answer_check"));
                        bean.setAnswer_min_length(obj.getString("answer_min_length"));
                        bean.setAnswer_max_length(obj.getString("answer_max_length"));
                        bean.setPlace_holder(obj.getString("place_holder"));
                        arraylist[i]=bean;
                    }

                    question_adapter = new QuestionSpinnerAdapter(getActivity(),android.R.layout.simple_list_item_1,arraylist);
                    UserOnboardingActivity.UserOnBoardQuestion_adapter = question_adapter;
                    question_spinner.setAdapter(question_adapter);
                    question_spinner.setOnItemSelectedListener(QuestionAnswerFragment.this);
                }catch (JSONException ex){

                }
            }

            @Override
            public void onQuestionError(VolleyError error) {
                dialog.dismiss();
            }

        });
        request.start();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (question_adapter != null) {
            int select = question_spinner.getSelectedItemPosition();
            if (select != -1) {

                if (s.length() == 0) {
                    submitanswerBtn.setEnabled(false);
                } else {
                    submitanswerBtn.setEnabled(true);
                }
//                if (count == countries_adapter.getCountryItem(select).getLength()) {
//                    InputMethodManager imm = (InputMethodManager)
//                            getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//                    if (imm != null) {
//                        imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);
//                    }
//                }
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void alterNumberValidation(int i) {

        openSelectYear = false;
        openCalender = false;
        QuestionModel selectedQuestion= question_adapter.getQuestionItem(i);
        question_value = selectedQuestion.getQuestion_value();
        minimum_length = Integer.parseInt(selectedQuestion.getAnswer_min_length());
        question_id = selectedQuestion.getQuestion_id();
        edtanswer.setText("");
        edtanswer.setHint(selectedQuestion.getPlace_holder());
        edtanswer.setFilters(new InputFilter[] {new InputFilter.LengthFilter(Integer.parseInt(selectedQuestion.getAnswer_max_length()))});

        if (selectedQuestion.getAnswer_mode().equals("text") && selectedQuestion.getAnswer_check().equals("Numeric")){
        edtanswer.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
        else if (selectedQuestion.getAnswer_mode().equals("select") && selectedQuestion.getAnswer_check().equals("Numeric")){
            edtanswer.setInputType(InputType.TYPE_NULL);
            openSelectYear = true;
        }
        else if (selectedQuestion.getAnswer_mode().equals("Calender") && selectedQuestion.getAnswer_check().equals("Numeric") ){
            edtanswer.setInputType(InputType.TYPE_NULL);
            openCalender = true;
        }
        else{
            edtanswer.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            edtanswer.setFilters(new InputFilter[]{
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
        stopkeyboard();
    }

    private void stopkeyboard(){
        Thread splashTread = new Thread() {
            @Override
            public void run() {
                try {

                    sleep(1);
                } catch (InterruptedException e) {
                    // do nothing
                } finally {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(edtanswer.getWindowToken(), 0);

                        }
                    });

                }
            }
        };
        splashTread.start();
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        alterNumberValidation(position);
        ((TextView) parent.getChildAt(0)).setGravity(Gravity.CENTER_VERTICAL);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    @Override
    public boolean onBackPressed() {
        return false;
    }


}

