package in.co.eko.fundu.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.security.MessageDigest;

import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.R;
import in.co.eko.fundu.requests.UpdateAccountNumberRequest;
import in.co.eko.fundu.utils.Fog;
import in.co.eko.fundu.utils.Utils;

/**
 * Created by user on 4/27/17.
 */

public class UpadteAccountNoActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher, TextView.OnEditorActionListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam2;
    private ProgressDialog dialog;
    private Button submitaccno;
    private EditText editaccno, editreenteraccno;
    private EditText fundupin;
    UpdateAccountNumberRequest updateAccountNumberRequest;
    static String custiddd = null;

//    public static void start(Context context, String custid) {
//        Intent starter = new Intent(context, UpadteAccountNoActivity.class);
//        custiddd = custid;
//        context.startActivity(starter);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_accountno);
        dialog = new ProgressDialog(UpadteAccountNoActivity.this);
        dialog.setCancelable(false);
        dialog.setMessage("Please wait...");
        if (getIntent().getStringExtra("custid5") != null)
            custiddd = getIntent().getStringExtra("custid5");
        submitaccno = (Button) findViewById(R.id.submit_accno);
        editaccno = (EditText) findViewById(R.id.accno);
        editreenteraccno = (EditText) findViewById(R.id.reenteraccno);
        fundupin = (EditText) findViewById(R.id.funduPinaccno);
        fundupin.addTextChangedListener(this);
        submitaccno.setOnClickListener(this);
        Utils.hideSoftKeyboard(this);
//        editaccno.setFilters(new InputFilter[]{
//                new InputFilter() {
//                    @Override
//                    public CharSequence filter(CharSequence src, int start, int end, Spanned dest, int dstart, int dend) {
//                        if (dend>21){
////                            return "";
//                        }
//                        else if (src.equals("")) {
//                            return src;
//                        }
//                        else if (src.toString().matches("[a-zA-Z0-9 ]+")) {
//                            return src;
//                        }
//                        return "";
//                    }
//
//                }
//        });
//        editreenteraccno.setFilters(new InputFilter[]{
//                new InputFilter() {
//                    @Override
//                    public CharSequence filter(CharSequence src, int start, int end, Spanned dest, int dstart, int dend) {
//                        if (dend>21){
////                            return "";
//                        }
//                        else if (src.equals("")) {
//                            return src;
//                        }
//                        else if (src.toString().matches("[a-zA-Z0-9 ]+")) {
//                            return src;
//                        }
//
//                        return "";
//                    }
//
//                }
//        });
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.submit_accno) {
            if (editaccno.getText().length() < 10) {
                Toast.makeText(getApplicationContext(), "Please enter minimum 10 digits bank account number", Toast.LENGTH_SHORT).show();
            } else if (editreenteraccno.getText().length() < 10) {
                Toast.makeText(getApplicationContext(), "Please re-enter the bank account number", Toast.LENGTH_SHORT).show();
            } else if (!(editreenteraccno.getText().toString().toLowerCase().equalsIgnoreCase(editaccno.getText().toString()))) {
                Toast.makeText(getApplicationContext(), "Account number do not match.", Toast.LENGTH_SHORT).show();
            } else if (fundupin.length() < 4) {
                Toast.makeText(getApplicationContext(), "Enter 4 dgits Fundu PIN", Toast.LENGTH_SHORT).show();
            } else {
                String fundupinmd5 = md5(fundupin.getText().toString());
                if (Utils.isNetworkAvailable(getApplicationContext()))
                callUpdateAccountApi(fundupinmd5);
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

    void callUpdateAccountApi(String fundupinmd5) {
        dialog.show();

        updateAccountNumberRequest = new UpdateAccountNumberRequest(getApplicationContext());
        Fog.e("USER", FunduUser.getCustomerId());
        updateAccountNumberRequest.setData(FunduUser.getCustomerId(), FunduUser.getCountryShortName(), editaccno.getText().toString(), fundupinmd5, custiddd);
        updateAccountNumberRequest.setParserCallback(new UpdateAccountNumberRequest.OnUpdateAccountNumberResults() {
            @Override
            public void onUpdateAccountNumberResponse(JSONObject response) {
                dialog.dismiss();
                if (response != null) {
                    try {
                        String status = response.optString("status");
                        if (status != null) {
                            if (status.equalsIgnoreCase("Success")) {
                                Utils.hideSoftKeyboard(UpadteAccountNoActivity.this);
                                Toast.makeText(getApplicationContext(), "Account Number Changed Successfully!", Toast.LENGTH_LONG).show();
                                finish();
                            } else
                                Toast.makeText(getApplicationContext(), response.optString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onUpdateAccountNumberError(VolleyError error) {
                dialog.dismiss();
                Fog.d("ChangePin", "Error");
            }
        });
        updateAccountNumberRequest.start();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() < 4) {
            submitaccno.setEnabled(false);
            fundupin.setTextColor(getResources().getColor(android.R.color.black));
        } else {
            if (s.length() == 4) {
                submitaccno.setEnabled(true);
            }
        }

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        boolean handled = false;
        if (actionId == EditorInfo.IME_ACTION_DONE) {
//            if (reenterfpinEditText.getText().toString().equals(fpinEditText.getText().toString())) {
//                View view = getCurrentFocus();
//                if (view != null) {
//                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//                }
//                String fundupinmd5 = md5(fpinEditText.getText().toString());
//                callUpdateAccountApi(fundupinmd5);
//                handled = true;
//            }
            return handled;
        }

        return false;
    }
}
