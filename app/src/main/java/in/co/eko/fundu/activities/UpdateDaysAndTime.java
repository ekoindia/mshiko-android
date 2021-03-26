package in.co.eko.fundu.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Calendar;

import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.R;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.requests.UpdateMerchantTimingRequest;
import in.co.eko.fundu.utils.AppPreferences;
import in.co.eko.fundu.utils.Fog;
import in.co.eko.fundu.utils.Utils;

/**
 * Created by Rahul on 5/26/17.
 */

public class UpdateDaysAndTime extends AppCompatActivity implements View.OnClickListener {

    private ProgressDialog dialog;
    private Button updateButton;
    private EditText editopentime, editclosingtime, editdays;
    String msg = "";
    AppPreferences pref;
    final CharSequence myList[] = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    boolean bl[] = {true, true, true, true, true, true, true};
    boolean boolsunday = false, boolmonday = false, booltuesday = false, boolwednesday = false, boolthursday = false, boolfriday = false, boolsaturday = false;
    int selectedopeninghour, selectedopeningminute, selectedclosinghour, selectedclosingminute;
    private UpdateMerchantTimingRequest updateMerchantTimingRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatedaysandtime);
        dialog = new ProgressDialog(UpdateDaysAndTime.this);
        dialog.setCancelable(false);
        dialog.setMessage("Please wait...");
        pref = FunduUser.getAppPreferences();

        updateButton = (Button) findViewById(R.id.updateBtn);
        editopentime = (EditText) findViewById(R.id.editopeningtime);
        editclosingtime = (EditText) findViewById(R.id.editclosingtime);
        editdays = (EditText) findViewById(R.id.editdays);
        updateMerchantTimingRequest = new UpdateMerchantTimingRequest(getApplicationContext());
        String days = pref.getString(Constants.DAYS);
        String openingtime = pref.getString(Constants.OPENING_TIME);
        String closingtime = pref.getString(Constants.CLOSING_TIME);
        Fog.d("PREF VALUE", openingtime + ">>>" + closingtime + ">>>>>" + days);
        if (!(openingtime == null || openingtime.equalsIgnoreCase(""))) {
            selectedopeninghour = Integer.parseInt(openingtime.split(":")[0]);
            selectedopeningminute = Integer.parseInt(openingtime.split(":")[1]);
            if (selectedopeninghour <= 12)
                editopentime.setText(new DecimalFormat("00").format(selectedopeninghour) + ":" + new DecimalFormat("00").format(selectedopeningminute) + " AM");
            else
                editopentime.setText(new DecimalFormat("00").format(selectedopeninghour - 12) + ":" + new DecimalFormat("00").format(selectedopeningminute) + " PM");
        } else {
            selectedopeninghour = 10;
            selectedopeningminute = 0;
        }
        if (!(closingtime == null || closingtime.equalsIgnoreCase(""))) {
            selectedclosinghour = Integer.parseInt(closingtime.split(":")[0]);
            selectedclosingminute = Integer.parseInt(closingtime.split(":")[1]);
            if (selectedclosinghour <= 12)
                editclosingtime.setText(new DecimalFormat("00").format(selectedclosinghour) + ":" + new DecimalFormat("00").format(selectedclosingminute) + " AM");
            else
                editclosingtime.setText(new DecimalFormat("00").format(selectedclosinghour - 12) + ":" + new DecimalFormat("00").format(selectedclosingminute) + " PM");
        } else {
            selectedclosinghour = 10;
            selectedclosingminute = 0;
        }
        if (!(days == null || days.equalsIgnoreCase(""))) {
            if (days.contains("SUNDAY")) {
                days = days.replace("SUNDAY", "Sunday");
                boolsunday = true;
            }
            if (days.contains("MONDAY")) {
                days = days.replace("MONDAY", "Monday");
                boolmonday = true;
            }
            if (days.contains("TUESDAY")) {
                days = days.replace("TUESDAY", "Tuesday");
                booltuesday = true;
            }
            if (days.contains("WEDNESDAY")) {
                days = days.replace("WEDNESDAY", "Wednesday");
                boolwednesday = true;
            }
            if (days.contains("THURSDAY")) {
                days = days.replace("THURSDAY", "Thursday");
                boolthursday = true;
            }
            if (days.contains("FRIDAY")) {
                days = days.replace("FRIDAY", "Friday");
                boolfriday = true;
            }
            if (days.contains("SATURDAY")) {
                days = days.replace("SATURDAY", "Saturday");
                boolsaturday = true;
            }
            editdays.setText(days);
        }
        bl[0] = boolsunday;
        bl[1] = boolmonday;
        bl[2] = booltuesday;
        bl[3] = boolwednesday;
        bl[4] = boolthursday;
        bl[5] = boolfriday;
        bl[6] = boolsaturday;


        updateButton.setOnClickListener(this);
        editdays.setOnClickListener(this);
        editclosingtime.setOnClickListener(this);
        editopentime.setInputType(InputType.TYPE_NULL);
        editopentime.setOnClickListener(this);
        editdays.setInputType(InputType.TYPE_NULL);
        editclosingtime.setInputType(InputType.TYPE_NULL);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.updateBtn) {
            if (editclosingtime.getText().toString().equalsIgnoreCase("")) {
                Utils.showShortToast(getApplicationContext(), "Select shop opening time");
            } else if (editopentime.getText().toString().equalsIgnoreCase("")) {
                Utils.showShortToast(getApplicationContext(), "Select shop closing time");
            } else if (editdays.getText().toString().equalsIgnoreCase("")) {
                Utils.showShortToast(getApplicationContext(), "Select shop opening days");
            } else {
                if (Utils.isNetworkAvailable(getApplicationContext()))
                    gotoUpdate();
                else {

                }
            }
        }
        if (v.getId() == R.id.editopeningtime) {
            Calendar mcurrentTime = Calendar.getInstance();
            TimePickerDialog mTimePicker;

            mTimePicker = new TimePickerDialog(UpdateDaysAndTime.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    selectedopeninghour = selectedHour;
                    selectedopeningminute = selectedMinute;
                    if (selectedHour < 12)
                        editopentime.setText(new DecimalFormat("00").format(selectedHour) + ":" + new DecimalFormat("00").format(selectedMinute) + " AM");
                    else
                        editopentime.setText(new DecimalFormat("00").format(selectedHour - 12) + ":" + new DecimalFormat("00").format(selectedMinute) + " PM");
                }
            }, selectedopeninghour, selectedopeningminute, false);//Yes 24 hour time
            mTimePicker.setTitle("Select Shop Opening Time");
            mTimePicker.show();
        }
        if (v.getId() == R.id.editclosingtime) {
            Calendar mcurrentTime = Calendar.getInstance();
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(UpdateDaysAndTime.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    selectedclosinghour = selectedHour;
                    selectedclosingminute = selectedMinute;
                    if (selectedHour < 12)
                        editclosingtime.setText(new DecimalFormat("00").format(selectedHour) + ":" + new DecimalFormat("00").format(selectedMinute) + " AM");
                    else
                        editclosingtime.setText(new DecimalFormat("00").format(selectedHour - 12) + ":" + new DecimalFormat("00").format(selectedMinute) + " PM");
                }
            }, selectedclosinghour, selectedclosingminute, false);//Yes 24 hour time
            mTimePicker.setTitle("Select Shop Closing Time");
            mTimePicker.show();
        }
        if (v.getId() == R.id.editdays) {

            final AlertDialog.Builder ad = new AlertDialog.Builder(this);
            ad.setTitle("Select Shop Opening Days");
            ad.setMultiChoiceItems(myList, bl, new DialogInterface.OnMultiChoiceClickListener() {

                @Override
                public void onClick(DialogInterface arg0, int arg1, boolean arg2) {
                    // TODO Auto-generated method stub

                    bl[arg1] = arg2;
                }
            });
            ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    msg = "";
                    for (int i = 0; i < bl.length; i++) {

                        if (bl[i]) {
                            if (msg.equalsIgnoreCase("") || msg == null)
                                msg = "" + myList[i];
                            else
                                msg = msg + "," + myList[i];
                        }
                    }
//                    pref.putString(Constants.DAYS, msg.toUpperCase());
                    Fog.e("DAYSS", msg.toUpperCase());
                    editdays.setText(msg);
//                    Utils.showLongToast(getApplicationContext(), msg.toUpperCase());
                }
            });
            ad.show();
        }
    }

    void gotoUpdate() {
        dialog.show();
        updateMerchantTimingRequest.setData(editdays.getText().toString().toUpperCase()/*msg.toUpperCase()*/, new DecimalFormat("00").format(selectedopeninghour) + ":" + new DecimalFormat("00").format(selectedopeningminute),
                new DecimalFormat("00").format(selectedclosinghour) + ":" + new DecimalFormat("00").format(selectedclosingminute),
                FunduUser.getCountryShortName(), FunduUser.getContactIDType(), FunduUser.getContactId());
        updateMerchantTimingRequest.setParserCallback(new UpdateMerchantTimingRequest.UpdateMerchantTimingRequestResults() {
            @Override
            public void onUpdateMerchantTimingRequestResponse(String object) {
                Fog.e("UPDATE RESULT", object);
                dialog.dismiss();
//                {  "message": "merchant timing updated",  "status": "SUCCESS"}
                try {
                    JSONObject job = new JSONObject(object);

                    if (job.getString("status").equalsIgnoreCase("SUCCESS")) {
                        if (!msg.equalsIgnoreCase(""))
                            pref.putString(Constants.DAYS, editdays.getText().toString().toUpperCase()/*msg.toUpperCase()*/);
                        pref.putString(Constants.OPENING_TIME, new DecimalFormat("00").format(selectedopeninghour) + ":" + new DecimalFormat("00").format(selectedopeningminute));
                        pref.putString(Constants.CLOSING_TIME, new DecimalFormat("00").format(selectedclosinghour) + ":" + new DecimalFormat("00").format(selectedclosingminute));
                        Utils.showShortToast(getApplicationContext(), "Days and timings updated successfully!");
                        finish();
                    } else
                        Utils.showShortToast(getApplicationContext(), job.optString("message"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onUpdateMerchantTimingRequestError(VolleyError error) {
                dialog.dismiss();
            }
        });
        updateMerchantTimingRequest.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
