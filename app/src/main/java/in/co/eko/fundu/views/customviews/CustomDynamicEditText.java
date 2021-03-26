package in.co.eko.fundu.views.customviews;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;

import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import in.co.eko.fundu.R;
import in.co.eko.fundu.interfaces.CustomDynamicEditTextInterface;
import in.co.eko.fundu.utils.Fog;
import in.co.eko.fundu.utils.Utils;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;



public class CustomDynamicEditText  {

    Context context;
    private int count;
    private  List<EditText> editTextList = new ArrayList<EditText>();
    public static ArrayList alistEdit=new ArrayList<>();
    public static StringBuilder stringBuilderNew = new StringBuilder();
    public boolean flag;
    private boolean isCheck;
    private int check=0;
    CustomDynamicEditTextInterface listener;
    private int inputType;

    public CustomDynamicEditText(Context context, int count, int inputType, boolean isCheck, CustomDynamicEditTextInterface listener) {
        this.context=context;
        this.count=count;
        this.inputType = inputType;
        this.isCheck = isCheck;
        this.listener = listener;
    }

    public CustomDynamicEditText(Context context, int count) {
        this(context,count,InputType.TYPE_CLASS_TEXT,false,null);

    }
    public CustomDynamicEditText(Context context, int count,int inputType,CustomDynamicEditTextInterface listener) {
       this(context,count,inputType,false,listener);
    }
    public CustomDynamicEditText(Context context, int count,int inputType,boolean isCheck) {
       this(context,count,inputType,isCheck,null);
    }

    public LinearLayout addEditView(){

        LinearLayout linearLayout = new LinearLayout(context);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        linearLayout.setLayoutParams(params);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(tableLayout(count));
        return linearLayout;
    }



    // Using a TableLayout as it provides you with a neat ordering structure

    private TableLayout tableLayout(int count) {
        TableLayout tableLayout = new TableLayout(context);
        // tableLayout.setStretchAllColumns(true);
        int noOfRows = count / 5;
        for (int i = 0; i < noOfRows; i++) {
            int rowId = 5 * i;
            tableLayout.addView(createOneFullRow(rowId));
        }
        int individualCells = count % 5;
        tableLayout.addView(createLeftOverCells(individualCells, count));
        return tableLayout;
    }
    android.widget.TableRow.LayoutParams p = new android.widget.TableRow.LayoutParams();
    private TableRow createLeftOverCells(int individualCells, int count) {
        TableRow tableRow = new TableRow(context);
        p.rightMargin = dpToPixel(5, context);
        p.leftMargin = dpToPixel(5, context); // right-margi
        p.topMargin =  dpToPixel(5, context);
        p.bottomMargin = dpToPixel(5, context);
        tableRow.setGravity(Gravity.CENTER);
        // tableRow.setPadding(0, 10, 0, 0);
        int rowId = count - individualCells;
        for (int i = 1; i <= individualCells; i++) {
            tableRow.addView(editText(String.valueOf(rowId + i)));
        }
        return tableRow;
    }

    private TableRow createOneFullRow(int rowId) {

        TableRow tableRow = new TableRow(context);
        p.rightMargin = dpToPixel(5, context);
        p.leftMargin = dpToPixel(5, context); // right-margi
        p.topMargin =  dpToPixel(5, context);
        p.bottomMargin = dpToPixel(5, context);

        //  tableRow.setPadding(0, 10, 0, 0);
        for (int i = 1; i <= 5; i++) {
            tableRow.addView(editText(String.valueOf(rowId + i)));

        }
        return tableRow;
    }

    int pos =0;

    public EditText editText(String hint) {
        final EditText editText = new EditText(context);
        editText.setId(Integer.valueOf(hint));
        editText.setHint(hint);
        editText.setSingleLine(true);
        editText.setLayoutParams(p);
        editText.setHeight(30);
        editText.setWidth(30);
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editText.setBackground(context.getResources().getDrawable(R.drawable.code_letter_back));
        editText.setInputType(inputType);
        editText.setGravity(Gravity.CENTER);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Utils.hideSoftKeyboard((Activity) context);
                    handled = true;
                }
                return handled;
            }
        });
        int maxLength = 1;
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxLength);
        editText.setFilters(fArray);

        if(editTextList.size()>1){
            editTextList.get(0).requestFocus();
        }
        editTextList.add(editText);


        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                pos = editText.getId() -1 ;
                if (editText != null && editText.length() > 0) {
                    editText.setNextFocusForwardId(editText.getId());
                    View next = editText.focusSearch(View.FOCUS_RIGHT); // or FOCUS_FORWARD
                    if (next != null) {
                        next.requestFocus();
                    }
                    if(listener != null){
                        listener.onTextChange(pos);
                    }
                    if (pos < editTextList.size() - 1) {
                        editTextList.get(pos+1).requestFocus();
                        pos++;
                    }
                }

            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return false;
            }
        });

//        editText.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//
//
//                if (event.getAction() != KeyEvent.ACTION_DOWN) {
//
//                    switch (keyCode) {
//                        case KeyEvent.KEYCODE_0:
//                        case KeyEvent.KEYCODE_1:
//                        case KeyEvent.KEYCODE_2:
//                        case KeyEvent.KEYCODE_3:
//                        case KeyEvent.KEYCODE_4:
//                        case KeyEvent.KEYCODE_5:
//                        case KeyEvent.KEYCODE_6:
//                        case KeyEvent.KEYCODE_7:
//                        case KeyEvent.KEYCODE_8:
//                        case KeyEvent.KEYCODE_9:
//                            return false;
//                           /* if (editText.hasFocus()) {
//                                if (editText.length() != 0) {
//                                    editText.setText("" + (keyCode - 7));
//                                    editText.setSelection(editText.length());
//                                    View next = editText.focusSearch(View.FOCUS_FORWARD); // or FOCUS_BACKWARD
//                                    if (next != null){
//                                        next.requestFocus();
//                                    }
//                                }
//                                return true;
//                            }*/
//
//                    }
//                }
//                if(keyCode == KeyEvent.KEYCODE_DEL) {
//                    if(editText != null){
//                        if(editText.length() == 0){
//                            EditText next = (EditText) editText.focusSearch(View.FOCUS_LEFT); // or FOCUS_BACKWARD
//                            if (next != null){
//                                next.setText("");
//                                next.requestFocus();
//                           }
//                        }
//                        else{
//                            editText.setText("");
//                            EditText next = (EditText) editText.focusSearch(View.FOCUS_LEFT); // or FOCUS_BACKWARD
//                            if (next != null){
//                                next.requestFocus();
//                            }
//                        }
//                        return true;
//                    }
//
////                    flag=true;
////                    if(editText.length()==0){
////                        flag=false;
////                        View next = editText.focusSearch(View.FOCUS_LEFT); // or FOCUS_BACKWARD
////                        if (next != null){
////                            next.requestFocus();
////                        }
////                        new Thread(new Runnable() {
////                            @Override
////                            public void run() {
////                                try{
////                                    Thread.sleep(100);
////                                    flag=true;
////                                }catch (Exception e){
////                                }
////                            }                        }).start();
////                    }
////                    if (editText != null && flag)
////                    {
////                        editText.setText("");
////                        flag=false;
////                        View next = editText.focusSearch(View.FOCUS_LEFT); // or FOCUS_BACKWARD
////                        if (next != null){
////                            next.requestFocus();
////                        }
////                        new Thread(new Runnable() {
////                            @Override
////                            public void run() {
////                                try{
////                                    Thread.sleep(100);
////                                    flag=true;
////                                }catch (Exception e){
////                                }
////                            }}).start();
////                    }
//                    Fog.d("onkeyevekld", "onKey length************** "+editText.length());
//                }
//                return false;
//            }        });

        return editText;
    }

    private void setBackground() {

        for(int i=0;i<editTextList.size();i++){

            editTextList.get(i).setBackground(context.getResources().getDrawable(R.drawable.code_mismatch));
            editTextList.get(i).setText("");
        }

    }

    private static Float scale;
    public static int dpToPixel(int dp, Context context) {
        if (scale == null)
            scale = context.getResources().getDisplayMetrics().density;
        return (int) ((float) dp * scale);
    }



    public String getText(){

        for(int i=0;i<alistEdit.size();i++){
            Fog.d("text","customDynamicEditTextCoirmnfPin"+alistEdit.get(i));
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (EditText editText : editTextList) {

            if(!validate())
            {
                Toast.makeText(context,"Please enter all the fields",Toast.LENGTH_SHORT).show();
                //Fog.d("pos","Please enter");
                break;
            }

            else {
                stringBuilder.append(editText.getText().toString());
            }
        }

        Fog.d("chhhhhsdfsfs","******"+stringBuilder.toString());
        return stringBuilder.toString();
    }

    private boolean validate(){
        for(int i=0; i<editTextList.size(); i++){
            EditText currentField=editTextList.get(i);
            if(currentField.getText().toString().length()<=0){
                return false;
            }
        }
        return true;
    }
    public void onIncorrectInput(){
        setEdittextBack(R.drawable.code_mismatch);
        editTextList.get(0).requestFocus();
    }
    public void resetInput(){
        setEdittextBack(R.drawable.code_letter_back);
    }
    private void setEdittextBack(int resId){
        for(int i =0;i<editTextList.size();i++){
            editTextList.get(i).setBackground(context.getResources().getDrawable(resId));
            editTextList.get(i).setText("");
        }
    }

}
