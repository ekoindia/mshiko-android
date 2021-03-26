package in.co.eko.fundu.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import in.co.eko.fundu.R;
import in.co.eko.fundu.activities.CheckOutActivity;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.views.customviews.CustomDynamicEditText;
import in.co.eko.fundu.models.Contact;
import in.co.eko.fundu.utils.AppPreferences;
import in.co.eko.fundu.utils.Fog;
import in.co.eko.fundu.utils.Utils;


public class EnterBankAccountFragment extends BaseFragment implements View.OnClickListener, TextView.OnEditorActionListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Button next;
    private LinearLayout linearLayoutAccountNumber;
    private EditText editextAccountNumber,editextConfirmAccountNumber;
    private ImageView back;
    private String expiry = "", expirymonth = "", expiryyear = "";
    private String secQuesId, secAnswer, FunduPin;
    static int cvvint = 3;
    private String mParam2;
    CustomDynamicEditText customDynamicEditText1,customDynamicEditText2,customDynamicEditText3,customDynamicEditText4;
    LinearLayout linearLayout1,linearLayout2,linearLayout3,linearLayout4;
    private String accNumber, confirmAccNumber;
    private Contact mParam1;
    private ProgressDialog dialog;

    public EnterBankAccountFragment() {

    }


    public static EnterBankAccountFragment newInstance(Contact param1, String param2) {
        EnterBankAccountFragment fragment = new EnterBankAccountFragment();
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
            secAnswer = getArguments().getString("Answer");
            secQuesId = getArguments().getString("QuestionKey");
            FunduPin = getArguments().getString("FunduPin");
            Fog.e("ques_id", AppPreferences.getInstance(getActivity()).getString("Answer"));
            Fog.e("answer", AppPreferences.getInstance(getActivity()).getString("QuestionId"));
            Fog.e("FunduPin", AppPreferences.getInstance(getActivity()).getString("FunduPin"));
        }
        dialog = new ProgressDialog(getActivity());
        dialog.setCancelable(false);
        dialog.setMessage("Please wait...");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_enter_bank_account, container, false);

        next = (Button)view.findViewById(R.id.next);


      back = (ImageView) view.findViewById(R.id.imageView_back);
        editextAccountNumber = (EditText)view.findViewById(R.id.editextAccountNumber);
        editextConfirmAccountNumber = (EditText)view.findViewById(R.id.editextConfirmAccountNumber);
        editextAccountNumber.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        setOnClickListener();
        editextConfirmAccountNumber.setOnEditorActionListener(this);

        return view;
    }

    private void setOnClickListener() {

        next.setOnClickListener(this);
        back.setOnClickListener(this);

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

            case R.id.next:
                validateAccountNumber();
                break;

            case R.id.imageView_back:
                getFragmentManager().popBackStack();
                break;


        }


    }

    private void validateAccountNumber() {


        if(TextUtils.isEmpty(editextAccountNumber.getText())||
                TextUtils.isEmpty(editextConfirmAccountNumber.getText())){

            Toast.makeText(getActivity(), "Please enter Account Number", Toast.LENGTH_SHORT).show();
            return;

        }

        else {

            accNumber        = editextAccountNumber.getText().toString();
            confirmAccNumber = editextConfirmAccountNumber.getText().toString();

            if(accNumber.equalsIgnoreCase(confirmAccNumber)){

                gotoCardDetailScreen();


            }
            else{

                editextConfirmAccountNumber.setText("");
                Toast.makeText(getActivity(), "Please enter correct Account Number", Toast.LENGTH_SHORT).show();

            }




        }





    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        boolean handled = false;

        Utils.hideSoftKeyboard(getActivity());

        if (i == EditorInfo.IME_ACTION_NEXT) {
            validateAccountNumber();
            if (confirmAccNumber != null) next.setEnabled(true);

            handled = true;
        }


        return handled;
    }

    public void gotoCardDetailScreen(){

            if (confirmAccNumber != null) {
                //Utils.hideSoftKeyboard(getActivity());
                Bundle bundle = new Bundle();
                Intent intent = new Intent(getActivity(), CheckOutActivity.class);
                if (pref.getString(Constants.CONTACT_TYPE_PA).equalsIgnoreCase("AGENT")){
                    bundle.putString(FRAGMENT_NAME, "MerchantCardDetailFragment");
                    onButtonPressed(bundle);
                }

                else
               /* bundle.putString(FRAGMENT_NAME, "CardDetailFragment");
                bundle.putSerializable(Contact.class.getSimpleName(), mParam1);
                bundle.putString("AccountNumber", confirmAccNumber);
                bundle.putString("QuestionKey", secQuesId);
                bundle.putString("Answer", secAnswer);
                bundle.putString("FunduPin", FunduPin);*/
                pref.putString("Answer", secAnswer);
                pref.putString("AccountNumber", confirmAccNumber);
                intent.putExtra(Contact.class.getSimpleName(), mParam1);
                intent.putExtra("AccountNumber", confirmAccNumber);
                intent.putExtra("QuestionKey", secQuesId);
                intent.putExtra("Answer", secAnswer);
                intent.putExtra("FunduPin", FunduPin);
                getActivity().startActivity(intent);






                //onButtonPressed(bundle);
            } else {

            }
        }


    @Override
    public boolean onBackPressed() {
        return false;
    }

    }
