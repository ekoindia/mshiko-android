package in.co.eko.fundu.activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import in.co.eko.fundu.R;
import in.co.eko.fundu.utils.Utils;

public class DescribeIssue extends AppCompatActivity implements TextWatcher {

    ImageView back;
    Button done;
    String strCount;
    TextView length;
    EditText issue;

    int i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_describe_issue);
        back = (ImageView) findViewById(R.id.imageview_back);
        done = (Button) findViewById(R.id.done);
        length = (TextView) findViewById(R.id.textLength);
        issue = (EditText) findViewById(R.id.edit_issue);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.SHOW_IMPLICIT);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.hideSoftKeyboard(DescribeIssue.this);
                finish();
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(issue.getText().toString())){
                    Toast.makeText(DescribeIssue.this,
                            "Your issue has been sucessfully submitted with us.", Toast.LENGTH_SHORT).show();
                    Utils.hideSoftKeyboard(DescribeIssue.this);
                    finish();
                }


            }
        });

        issue.addTextChangedListener(this);


    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            strCount=""+charSequence.toString();
            if(strCount.length()<=160){
            length.setText(String.valueOf(strCount.length())+ "/160");
            }
            else{
            }

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
