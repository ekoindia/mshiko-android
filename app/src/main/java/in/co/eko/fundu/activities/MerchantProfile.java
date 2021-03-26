package in.co.eko.fundu.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import in.co.eko.fundu.R;
import in.co.eko.fundu.constants.API;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.dialogs.TotpDialog;
import in.co.eko.fundu.gcm.FunduNotificationManager;
import in.co.eko.fundu.models.Contact;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.models.LinkAccountItem;
import in.co.eko.fundu.models.UserProfileItem;
import in.co.eko.fundu.parser.UniversalParser;
import in.co.eko.fundu.requests.CallWebService;
import in.co.eko.fundu.requests.CheckBalanceRequest;
import in.co.eko.fundu.requests.FindTransactionPairRequest;
import in.co.eko.fundu.requests.HasFundRequest;
import in.co.eko.fundu.requests.KenConfirmationRequest;
import in.co.eko.fundu.requests.LoadWalletRequest;
import in.co.eko.fundu.requests.TransactionCommitRequest;
import in.co.eko.fundu.requests.TransactionInitiateRequest;
import in.co.eko.fundu.utils.AppPreferences;
import in.co.eko.fundu.utils.Fog;
import in.co.eko.fundu.utils.FunduAnalytics;
import in.co.eko.fundu.utils.UserTransactions;
import in.co.eko.fundu.utils.Utils;
import in.co.eko.fundu.views.AlertMessage;
import in.co.eko.fundu.views.ProgressOverlay;
import in.co.eko.fundu.views.slidinguppanel.SlidingUpPanelLayout;


public class MerchantProfile extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener, TextWatcher, CheckBalanceRequest.OnCheckBalanceResults, LoadWalletRequest.OnLoadWalletResults,
        SlidingUpPanelLayout.PanelSlideListener, TransactionInitiateRequest.OnTransactionInitiateResults, FunduNotificationManager.OnPairResult, FindTransactionPairRequest.OnFindTransactionPairResults, CallWebService.ObjectResponseCallBack, AppCompatDialog.OnDismissListener {
    private static final String TAG = MerchantProfile.class.getName();
    //    LinearLayout otherUserLayout;
    private Contact contactItem;
    private ProgressDialog dialog;
    private CheckBalanceRequest checkBalanceRequest;
    private String needCashAmount;
    private int amountP;
    public AppPreferences pref;
    ArrayList<String> list;
    float requestedAmount = 0;
    private static final int SEND_MONEY_TO_WALLET = 1;
    private static final int GET_CASH = 2;

    private static final int SEND_MONEY_TO_ACCOUNT = 3;
    private int transactionType = -1;
    private ArrayList<String> alertArray;
    private int recipientID;
    private String accountNumber = "";
    private ArrayList<LinkAccountItem> linkAccountItems;
    //    private TextView amountstdart, amountend;
    private String username, usermobile, userid;
    int minimum_amount = 1000, maximum_amount = 10000, minimum_twice = 100, max = 100, min = 0;
    private KenConfirmationRequest confirmrequest;
    private TotpDialog totpDialog;
    public static boolean isdialogsuccess = false;
    public static String ctid = "";
    private String provider_number;
    private TextView amountstart, amountend;
    private int amountInt;

    ImageView backdrop;
    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbar;
    AppBarLayout appbar;
    CoordinatorLayout mainContent;
    ImageButton subtractButton;
    EditText amountBox;
    ImageButton addButton;
    AppCompatSeekBar amountSelector;
    Button submit;
    LinearLayout needCash;
    SlidingUpPanelLayout slidePanel;
    TextView titleTV;
    ProgressOverlay progressOverlay;
    //    LinearLayout transactionsLayout;
    TextView merchant_name, merchant_address, merchant_mobile, opening_time, closing_time,
            sunday, monday, tuesday, wednesday, thursday, friday, saturday;
    RatingBar merchant_rating;
    Button call, getcash, map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.merchant_profile);
        FunduAnalytics.getInstance(this).sendScreenName("MerchantProfile");
        amountstart = (TextView) findViewById(R.id.amountstarts);
        amountend = (TextView) findViewById(R.id.amountend);
//        ButterKnife.inject(this);
        Fog.e("KCB", "RESUMED");
        InitViews();
        confirmrequest = new KenConfirmationRequest(getApplicationContext());
        if (FunduUser.getCountryShortName().equalsIgnoreCase("KEN")) {
            totpDialog = new TotpDialog(MerchantProfile.this);
            totpDialog.setOnDismissListener(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        onIntent(getIntent());
        totpDialog = new TotpDialog(MerchantProfile.this);
    }

    private void InitViews() {
        pref = FunduUser.getAppPreferences();
        contactItem = (Contact) getIntent().getSerializableExtra("merchant_data");
//        Fog.e("CONTACT", contactItem.toString());
        backdrop = (ImageView) findViewById(R.id.backdrop);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("");
        appbar = (AppBarLayout) findViewById(R.id.appbar);
        mainContent = (CoordinatorLayout) findViewById(R.id.main_content);
        subtractButton = (ImageButton) findViewById(R.id.subtractButton);
        amountBox = (EditText) findViewById(R.id.amountBox);
        addButton = (ImageButton) findViewById(R.id.addButton);
        amountSelector = (AppCompatSeekBar) findViewById(R.id.amountSelector);
        submit = (Button) findViewById(R.id.submit);
        needCash = (LinearLayout) findViewById(R.id.needCash);
        slidePanel = (SlidingUpPanelLayout) findViewById(R.id.slidePanel);
        titleTV = (TextView) findViewById(R.id.titleTV);
        progressOverlay = (ProgressOverlay) findViewById(R.id.progressOverlay);
//        transactionsLayout = (LinearLayout) findViewById(R.id.transactionsLayout);
//        otherUserLayout = (LinearLayout) findViewById(R.id.otherUserLayout);
        merchant_rating = (RatingBar) findViewById(R.id.merchant_rating);
        String rating = contactItem.getRating();
        if (rating != null) {
            double rat = Double.valueOf(rating);
            merchant_rating.setRating((float) (rat));
        }
        merchant_name = (TextView) findViewById(R.id.merchant_name);
        merchant_address = (TextView) findViewById(R.id.merchant_address);
        merchant_mobile = (TextView) findViewById(R.id.merchant_mobile);
        opening_time = (TextView) findViewById(R.id.opening_time);
        closing_time = (TextView) findViewById(R.id.closing_time);
        sunday = (TextView) findViewById(R.id.sunday);
        monday = (TextView) findViewById(R.id.monday);
        tuesday = (TextView) findViewById(R.id.tuesday);
        wednesday = (TextView) findViewById(R.id.wednesday);
        thursday = (TextView) findViewById(R.id.thursday);
        friday = (TextView) findViewById(R.id.friday);
        saturday = (TextView) findViewById(R.id.saturday);
        call = (Button) findViewById(R.id.b1);
        getcash = (Button) findViewById(R.id.b2);
        map = (Button) findViewById(R.id.b3);
        call.setOnClickListener(this);
        map.setOnClickListener(this);
        if (pref.getString(Constants.ALLOW_WITHDRAW).equalsIgnoreCase("No")) {
            getcash.setVisibility(View.INVISIBLE);
        }
        getcash.setOnClickListener(this);
        String namefirst = "<b><font color='#000000'>Business Name: </font></b>";
        String namelast = contactItem.getBusiness_name();
        merchant_name.setText(Html.fromHtml(namefirst + namelast));
//        merchant_name.setText(contactItem.getBusiness_name());
        username = contactItem.getBusiness_name();
        if (!(contactItem.getPhysical_location() == null || contactItem.getPhysical_location().equalsIgnoreCase("null"))) {
            String addfirst = "<b><font color='#000000'>Address: </font></b>";
            String addlast = contactItem.getPhysical_location();
            merchant_address.setText(Html.fromHtml(addfirst + addlast)/*boldText("Address:", contactItem.getPhysical_location())*/);
        } else merchant_address.setVisibility(View.GONE);

        String mobfirst = "<b><font color='#000000'>Mobile No: </font></b>";
        String moblast = "+254" + contactItem.getMobile();
        merchant_mobile.setText(Html.fromHtml(mobfirst + moblast));
        if (contactItem.getOpening_time() != null) {
            String opentime, closetime;
            int selectedopeninghour = Integer.parseInt(contactItem.getOpening_time().split(":")[0]);
            int selectedopeningminute = Integer.parseInt(contactItem.getOpening_time().split(":")[1]);
            if (selectedopeninghour <= 12)
                opentime = new DecimalFormat("00").format(selectedopeninghour) + ":" + new DecimalFormat("00").format(selectedopeningminute) + " AM";
            else
                opentime = new DecimalFormat("00").format(selectedopeninghour - 12) + ":" + new DecimalFormat("00").format(selectedopeningminute) + " PM";
            int selectedclosinghour = Integer.parseInt(contactItem.getClosing_time().split(":")[0]);
            int selectedclosingminute = Integer.parseInt(contactItem.getClosing_time().split(":")[1]);
            if (selectedclosinghour <= 12)
                closetime = new DecimalFormat("00").format(selectedclosinghour) + ":" + new DecimalFormat("00").format(selectedclosingminute) + " AM";
            else
                closetime = new DecimalFormat("00").format(selectedclosinghour - 12) + ":" + new DecimalFormat("00").format(selectedclosingminute) + " PM";
            String openfirst = "<b><font color='#000000'>Opening Days & Hours: </font></b>";
            opening_time.setText(Html.fromHtml(openfirst));
            if (contactItem.getDays() != null) {
                String days = contactItem.getDays();

                if (days.contains("MONDAY"))
                    monday.setText(opentime + "-" + closetime);
                else
                    monday.setText(Html.fromHtml("<b><font color='#ff0000'>Closed</font></b>"));
                if (days.contains("TUESDAY"))
                    tuesday.setText(opentime + "-" + closetime);
                else
                    tuesday.setText(Html.fromHtml("<b><font color='#ff0000'>Closed</font></b>"));
                if (days.contains("WEDNESDAY"))
                    wednesday.setText(opentime + "-" + closetime);
                else
                    wednesday.setText(Html.fromHtml("<b><font color='#ff0000'>Closed</font></b>"));
                if (days.contains("THURSDAY"))
                    thursday.setText(opentime + "-" + closetime);
                else
                    thursday.setText(Html.fromHtml("<b><font color='#ff0000'>Closed</font></b>"));
                if (days.contains("FRIDAY"))
                    friday.setText(opentime + "-" + closetime);
                else
                    friday.setText(Html.fromHtml("<b><font color='#ff0000'>Closed</font></b>"));
                if (days.contains("SATURDAY"))
                    saturday.setText(opentime + "-" + closetime);
                else
                    saturday.setText(Html.fromHtml("<b><font color='#ff0000'>Closed</font></b>"));
                if (days.contains("SUNDAY"))
                    sunday.setText(opentime + "-" + closetime);
                else
                    sunday.setText(Html.fromHtml("<b><font color='#ff0000'>Closed</font></b>"));
            }
        } else {
            opening_time.setVisibility(View.GONE);
            closing_time.setVisibility(View.GONE);
        }

        Button submit = (Button) findViewById(R.id.submit);
        addButton.setOnClickListener(this);
        subtractButton.setOnClickListener(this);
        submit.setOnClickListener(this);
        Fog.i("PROFILE IMAGE", contactItem.getMerchant_img_url() + "");
        if (contactItem.getMerchant_img_url() != null) {
            try {
                Picasso.with(getApplicationContext()).load(contactItem.getMerchant_img_url()).placeholder(R.drawable.ic_user_image).into(backdrop);
            } catch (IllegalArgumentException e) {
                backdrop.setImageURI(Uri.parse(contactItem.getMerchant_img_url()));
            }
//            backdrop.setImageURI(Uri.parse(contactItem.getContactImage()));
        }
        setToolbar();
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        amountSelector.setOnSeekBarChangeListener(this);
        slidePanel.addPanelSlideListener(this);
        minimum_amount = FunduUser.getCountryShortName().equalsIgnoreCase("KEN") ? 200 : 100;
        maximum_amount = FunduUser.getCountryShortName().equalsIgnoreCase("KEN") ? 9999 : 10000;
        amountBox.setText(String.valueOf(minimum_amount));
        amountBox.addTextChangedListener(this);

        if (FunduUser.getCountryShortName().equalsIgnoreCase("KEN")) {
            amountstart.setText("Shs. 200");
            amountend.setText("Shs. 9,999");
            max = (10000 - 200) / 200;
            amountSelector.setMax(max);
            minimum_twice = minimum_amount;
        } else {
            amountstart.setText(Utils.getCurrency(getApplicationContext()) + " 100");
            amountend.setText(Utils.getCurrency(getApplicationContext()) + " 10,000");
            amountSelector.setMax(max);
            minimum_twice = minimum_amount;
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(ratingReceiver, new IntentFilter(Constants.USER_PROFILE_ACTIVITY_ACTION));

    }

    private void getUserInfoApiCall() {
        CallWebService.getInstance(this, true, Constants.ApiType.GET_CONTACT_INFORMATION).hitJsonObjectRequestAPI(CallWebService.GET, createUrl(), null, this);
    }

    private String boldText(String firstt, String lastt) {
        String finaltext = "";
        String fir = firstt;
        String first = "<b><font color='#000000'>" + fir + "</font></b>";
        String last = lastt;
        finaltext = "" + Html.fromHtml(first + last);
        return finaltext;

    }
    private String createUrl() {
        if (isMe())
            return String.format(API.GET_CONTACT_API, FunduUser.getContactIDType(), contactItem.getContactId());
        else
            return String.format(API.GET_CONTACT_INFORMATION, FunduUser.getContactIDType(), FunduUser.getContactId(), FunduUser.getContactIDType(), contactItem.getContactId());
    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapsingToolbar.setTitle("Profile");
        collapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.tranlusent));
        collapsingToolbar.setCollapsedTitleTextColor(getResources().getColor(R.color.White));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
//                onBackPressed();
            }
        });
    }


    /* @Override
     public boolean onCreateOptionsMenu(Menu menu) {
         getMenuInflater().inflate(R.menu.menu_user_profile, menu);
         return true;
     }

     @Override
     public boolean onOptionsItemSelected(MenuItem item) {
         int id = item.getItemId();
         if(id == R.id.action_report_block) {
           //  finish();
             return true;
         }
         return super.onOptionsItemSelected(item);
     }*/
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.b2:
                if (pref.getString(Constants.ALLOW_WITHDRAW).equalsIgnoreCase("Yes")) {

                    if (FunduUser.getCountryShortName().equalsIgnoreCase("KEN"))
                        titleTV.setText("Get Cash");
                    else
                        titleTV.setText(R.string.need_cash_header_text);
                    transactionType = GET_CASH;
                    TransactionCommitRequest.needCash = true;
                    slidePanel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                }
                break;
            case R.id.b1:
                Uri call = Uri.parse("tel:" + "+254" + contactItem.getContactId());
                Intent surf = new Intent(Intent.ACTION_DIAL, call);
                startActivity(surf);
                break;
            case R.id.b3:
                in.co.eko.fundu.fragments.MapFragment.focusCamera = true;
                finish();
                break;
            case R.id.addButton:
                increaseAmount();
                break;
            case R.id.subtractButton:
                decreaseAmount();
                break;
            case R.id.submit:
                onSubmitClick();
                break;
        }
    }

    private void onSubmitClick() {
        if (Utils.isNetworkAvailable(getApplicationContext())) {
            if (amountBox.length() > 0) {
                slidePanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                amountInt = Integer.parseInt(amountBox.getText().toString().trim());
                needCashAmount = amountBox.getText().toString().trim();
                amountBox.setText(String.valueOf(minimum_amount));
                goForTransaction();
            } else
                Utils.showShortToast(getApplicationContext(), "Amount should't be blank");
        /*switch (transactionType) {
            case SEND_MONEY_TO_ACCOUNT:
                AlertMessage.showAlertDialogWithCallback(this, getString(R.string.send_money_to_wallet_alert_msg), this);
                break;
            default:
                goForTransaction();
                break;
        }*/
        }
    }

    private void goForTransaction() {
        try {

            amountP = amountInt;
            if (amountInt >= minimum_amount && amountInt <= maximum_amount) {
                checkWallet(amountInt);
            } else {
                Toast.makeText(this, "Amount should be between " + minimum_amount + " to " + maximum_amount
                        /*R.string.amount_range_validation*/, Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.correct_amount_validation, Toast.LENGTH_SHORT).show();
        }
    }

    private void increaseAmount() {
//        String amount = amountBox.getText().toString();
//        try {
//            int amountInInt = Integer.parseInt(amount);
//            if (amountInInt <= 9900) {
//                amountInInt = amountInInt + 100;
//                amountBox.setText(String.valueOf(amountInInt));
//            }
//        } catch (NumberFormatException e) {
//            amountBox.setText(R.string.minimum_amount);
        String amount = amountBox.getText().toString();
        try {
            int amountInInt = Integer.parseInt(amount);
            if (amountInInt < maximum_amount - minimum_amount) {
                amountInInt = amountInInt + minimum_twice;
                amountBox.setText(String.valueOf(amountInInt));
            } else if (amountInInt >= maximum_amount - minimum_twice) {
                amountBox.setText(String.valueOf(maximum_amount));
            }
        } catch (NumberFormatException e) {
            amountBox.setText(String.valueOf(minimum_amount));
        }
//        }
    }

    private void decreaseAmount() {
//        String amount2 = amountBox.getText().toString();
//        try {
//            int amountInInt2 = Integer.parseInt(amount2);
//            if (amountInInt2 >= 200) {
//                amountInInt2 = amountInInt2 - 100;
//                amountBox.setText(String.valueOf(amountInInt2));
//            }
//        } catch (NumberFormatException e) {
//            amountBox.setText(R.string.minimum_amount);
//        }
        String amount2 = amountBox.getText().toString();
        try {
            int amountInInt2 = Integer.parseInt(amount2);
            if (amountInInt2 > minimum_twice) {
                amountInInt2 = amountInInt2 - minimum_twice;
                amountBox.setText(String.valueOf(amountInInt2));
            } else if (amountInInt2 <= minimum_twice) {
                amountBox.setText(String.valueOf(minimum_amount));
            }
        } catch (NumberFormatException e) {
            amountBox.setText(String.valueOf(minimum_amount));
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser)
//            amountBox.setText("" + ((progress + 1) * 100));
            amountBox.setText("" + ((progress + 1) * minimum_twice));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    private void checkWallet(int amount) {
        dialog.setMessage(getString(R.string.text_check_balance));
        dialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("custid", FunduUser.getCustomerId());
            object.put("mobile", pref.getString(Constants.PrefKey.CONTACT_NUMBER));
            object.put("countryShortname", pref.getString(Constants.COUNTRY_SHORTCODE));
            object.put("amount", String.valueOf(amount));
            if (transactionType == SEND_MONEY_TO_WALLET)
                object.put("type", Constants.SEND_MONEY_TYPE);
            else
                object.put("type", Constants.NEED_CASH_TYPE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        HasFundRequest request = new HasFundRequest(getApplicationContext(), object);
        request.setParserCallback(new HasFundRequest.OnHasFundResults() {
            @Override
            public void onHasFundResponse(String response) {
                dialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    String walletAmount = jsonObject.optString("Balance Amount");
                    if (jsonObject.has("Balance Amount")) {
                        if (walletAmount.length() == 0 || walletAmount.equals("0") || walletAmount.equals("0.00")) {
                            float currentAmount = Float.parseFloat(walletAmount);
                            float requestedAmount = Float.parseFloat(needCashAmount);
                            if (requestedAmount <= currentAmount) {
                                switch (transactionType) {
                                    case SEND_MONEY_TO_WALLET:
                                        initTransaction(contactItem.getContactId(), requestedAmount);
                                        break;
                                    case GET_CASH:
                                        FunduNotificationManager.setOnPairResult(MerchantProfile.this);
                                        progressOverlay.setTime(30000, 30);
                                        startFindTransactionPairRequest();
                                        break;
                                    case SEND_MONEY_TO_ACCOUNT:
                                        initTransaction(contactItem.getContactId(), requestedAmount);
                                        break;
                                }
                            } else {
                                Toast.makeText(MerchantProfile.this, "Insufficient Amount. Please load your wallet first.", Toast.LENGTH_SHORT).show();
                                float neededAmount = requestedAmount - currentAmount;
                                refillWallet(neededAmount);

                            }
                        } else {
                            float currentAmount = Float.parseFloat(walletAmount);
                            float requestedAmount = Float.parseFloat(needCashAmount);
                            if (requestedAmount <= currentAmount) {
                                switch (transactionType) {
                                    case SEND_MONEY_TO_WALLET:
                                        initTransaction(contactItem.getContactId(), requestedAmount);
                                        break;
                                    case GET_CASH:
                                        FunduNotificationManager.setOnPairResult(MerchantProfile.this);
                                        progressOverlay.setTime(30000, 30);
                                        startFindTransactionPairRequest();
                                        break;
                                    case SEND_MONEY_TO_ACCOUNT:
                                        initTransaction(contactItem.getContactId(), requestedAmount);
                                        break;
                                }
                            } else {
                                Toast.makeText(MerchantProfile.this, "Insufficient Amount. Please load your wallet first.", Toast.LENGTH_SHORT).show();
                                float neededAmount = requestedAmount - currentAmount;
                                refillWallet(neededAmount);
                            }
                        }
                    } else {
                        if (jsonObject.has("Error"))
                            Toast.makeText(MerchantProfile.this, jsonObject.optString("Error"), Toast.LENGTH_SHORT).show();
                    }
                    if (jsonObject.optString("status").equalsIgnoreCase("Success")) {

                        int charges = jsonObject.optInt("charges");
                        FunduUser.setChargesKen(String.valueOf(charges));
//                        FunduNotificationManager.setOnPairResult(MerchantProfile.this);
//                        startFindTransactionPairRequest();
                        switch (transactionType) {
                            case SEND_MONEY_TO_WALLET:
                                initTransaction(contactItem.getContactId(), requestedAmount);
                                break;
                            case GET_CASH:
                                FunduNotificationManager.setOnPairResult(MerchantProfile.this);
                                progressOverlay.setTime(30000, 30);
                                startFindTransactionPairRequest();
                                break;
                            case SEND_MONEY_TO_ACCOUNT:
                                initTransaction(contactItem.getContactId(), requestedAmount);
                                break;
                        }
                    } else if (jsonObject.optString("status").equalsIgnoreCase("ERROR")) {
                        if (jsonObject.optString("message").equalsIgnoreCase("Customer doesn't exist")
                                || jsonObject.optString("message").equalsIgnoreCase("Customer not registered with us")) {
                            HomeActivity.Signout(getApplicationContext());
                            finish();
                        }
                        Toast.makeText(getApplicationContext(), jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Fog.d(TAG, "Exception - Due to unexpected key-value");
                }

            }

            @Override
            public void onHasFundError(VolleyError error) {
                dialog.dismiss();
            }
        });
        request.start();
    }


//    private void checkWallet() {
//        dialog.setMessage(getString(R.string.text_check_balance));
//        dialog.show();
//        checkBalanceRequest = new CheckBalanceRequest(this);
//        checkBalanceRequest.setData(FunduUser.getContactId());
//        checkBalanceRequest.setParserCallback(this);
//        checkBalanceRequest.start();
//    }

    @Override
    public void onCheckBalanceResponse(String response) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        parseResponse(response);
    }

    private void parseResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String walletAmount = jsonObject.optString(getString(R.string.balance_amount));

            if (jsonObject.has(getString(R.string.balance_amount))) {
//                needCashAmount = amountBox.getText().toString().trim();
                //  if (walletAmount.length() == 0 || walletAmount.equals("0") || walletAmount.equals("0.00")) {
                checkWalletAndProceed(walletAmount);
                //   }
            } else {
                if (jsonObject.has(getString(R.string.error)))
                    Toast.makeText(this, jsonObject.optString(getString(R.string.error)), Toast.LENGTH_SHORT).show();
            }


        } catch (JSONException e) {
            e.printStackTrace();
            Fog.d(TAG, "Exception - Due to unexpected key-value");
        }
    }


    private void checkWalletAndProceed(String walletAmount) {
        float currentAmount = Float.parseFloat(walletAmount);
        requestedAmount = Float.parseFloat(needCashAmount);
        if (requestedAmount <= currentAmount) {
            switch (transactionType) {
                case SEND_MONEY_TO_WALLET:
                    initTransaction(contactItem.getContactId(), requestedAmount);
                    break;
                case GET_CASH:
                    FunduNotificationManager.setOnPairResult(this);
                    progressOverlay.setTime(30000, 30);
                    startFindTransactionPairRequest();
                    break;
                case SEND_MONEY_TO_ACCOUNT:
                    initTransaction(contactItem.getContactId(), requestedAmount);
                    break;
            }
        } else {
            Toast.makeText(this, R.string.insufficient_amount, Toast.LENGTH_SHORT).show();
            float neededAmount = requestedAmount - currentAmount;
            refillWallet(neededAmount);
        }
    }

    private void startFindTransactionPairRequest() {
        UserTransactions.getInstance().findPair(this, getString(R.string.wallet), FunduUser.getContactId(), FunduUser.getContactIDType(), contactItem.getContactId(), FunduUser.getContactIDType(), (int) (Double.parseDouble(needCashAmount)), 1000, false,"0",null);
        if (progressOverlay != null) {
            progressOverlay.showProgress();
        }
    }

    private void doPayment(String sendMoneyAmount, String transactionID) {
        createListForCommitTransaction(sendMoneyAmount);
        Intent intent = new Intent(this, CommitTransactionActivity.class);
        intent.putExtra(Constants.ALERT, list);
        intent.putExtra(Constants.NAME, getString(R.string.send_money_header_text));
        intent.putExtra(Constants.TRANSACTION_ID, transactionID);
        startActivity(intent);

    }

    private void createListForCommitTransaction(String needCashAmount) {
        list = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            switch (i) {
                case 1:
                    list.add(1, contactItem.getContactId());
                    break;
                case 7:
                    list.add(7, contactItem.getContactId());
                    break;
                case 8:
                    list.add(8, contactItem.getBusiness_name());
                    break;
                case 9:
                    list.add(9, needCashAmount);
                    break;
                default:
                    list.add("");
                    break;
            }
        }
    }

    @Override
    public void onCheckBalanceError(VolleyError error) {

    }

    public void refillWallet(final float neededAmount) {
        // UserTransactions.getInstance().rechargeWallet(this, neededAmount, dialog);
    }

    @Override
    public void onLoadWalletResponse(JSONObject response) {
        Fog.d(TAG, response.toString());
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        if (response.has("data")) {
            try {
                JSONObject object = response.getJSONObject("data");
                if (object.has("amount") && object.has("customer_balance")) {
                    goForTransaction();
                    Toast.makeText(this, response.optString("message"), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, response.optString("message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onLoadWalletError(VolleyError error) {

    }


    private void initTransaction(String recipient_mobile, float amount) {
        if (!(username == null || username.equalsIgnoreCase(""))) {

            provider_number = recipient_mobile;
            if (transactionType == SEND_MONEY_TO_ACCOUNT) {//UserTransactions.getInstance().initTransactionForSendMoneyToAccount(this, FunduUser.getContactId(), FunduUser.getContactIDType(), recipient_mobile, FunduUser.getContactIDType(), (int) amount, 1000, recipientID);
                goToMoneyToAccountConfirmActivity(recipient_mobile, amount);
            } else {
                if (FunduUser.getCountryShortName().equalsIgnoreCase("KEN")) {
                    if (transactionType == SEND_MONEY_TO_WALLET) {
                        dialog.show();
                        confirmrequest.setData(FunduUser.getCustomerId(), amountP, FunduUser.getCountryShortName(), Constants.SEND_MONEY_TYPE);
                        confirmrequest.setParserCallback(new KenConfirmationRequest.KenConfirmationResults() {
                            @Override
                            public void onKenConfirmationResponse(String object) {
                                dialog.dismiss();
                                String message = "", status = "";
                                try {
                                    JSONObject job = new JSONObject(object);
                                    status = job.optString("status");
                                    message = job.optString("message");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                if (status.equalsIgnoreCase("ERROR")) {
                                    Utils.showLongToast(getApplicationContext(), message);
                                } else {
                                    totpDialog.setData(username, provider_number, "tid", amountP, Integer.parseInt(FunduUser.getChargesKen()), userid, Constants.SEND_MONEY_TYPE);
                                    totpDialog.show(); // Testing
                                }
                            }

                            @Override
                            public void onKenConfirmationError(VolleyError error) {
                                dialog.dismiss();
                                Utils.showShortToast(getApplicationContext(), "Error!");
                            }
                        });
                        confirmrequest.start();
                    } else {
                        Intent intent1 = new Intent(MerchantProfile.this, PairContactFoundActivity.class);
                        intent1.putStringArrayListExtra(Constants.ALERT, alertArray);
                        intent1.putExtra(Constants.TRANSACTION_ID, "KENTID");
                        intent1.putExtra("amount", amountP);
                        intent1.putExtra("tt", Constants.GET_CASH_TYPE);
                        startActivity(intent1);
                    }
                } else {
                    dialog.show();
                    pref.putInt(Constants.PrefKey.NEED_AMOUNT, amountP);
                    UserTransactions.getInstance().initiateTransactions(this, getString(R.string.wallet), FunduUser.getContactId(), FunduUser.getContactIDType(), recipient_mobile, FunduUser.getContactIDType(), pref.getInt(Constants.PrefKey.NEED_AMOUNT, -1)/*(int) amount*/, 1000,"","","0","");
                }
            }
        } else {
            Utils.showShortToast(getApplicationContext(), "Customer doesn't exist!");
            finish();
        }
    }

    private void goToMoneyToAccountConfirmActivity(String recipient_mobile, float amount) {
        Intent intent = new Intent(this, SendMoneyToAccountConfirmActivity.class);
        intent.putExtra(Constants.RECIPIENT_NUMBER, recipient_mobile);
        intent.putExtra(Constants.RECIPIENT_ID, recipientID);
        intent.putExtra(Constants.AMOUNT, amount);
        intent.putExtra(Constants.NAME, getString(R.string.send_money_header_text));
        intent.putExtra(Constants.ACCOUNT_NUMBER, accountNumber);
        intent.putExtra("user_name", username);
        intent.putExtra("user_id", userid);
        startActivity(intent);
    }


    @Override
    public void onPanelSlide(View panel, float slideOffset) {

    }

    @Override
    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

    }



    @Override
    public void onTransactionInitiateResponse(JSONObject response) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        try {
            if (response.getString("status").equals("SUCCESS")) {
                String s = response.getJSONObject("data").getJSONObject("data").getString("tid");
                Fog.d("TransactionID", s);
                switch (transactionType) {
                    case SEND_MONEY_TO_WALLET:
                        doPayment(needCashAmount, s);
                        break;
                    case GET_CASH:
                        Intent intent1 = new Intent(MerchantProfile.this, PairContactFoundActivity.class);
                        intent1.putStringArrayListExtra(Constants.ALERT, alertArray);
                        intent1.putExtra(Constants.TRANSACTION_ID, s);
                        intent1.putExtra("tt", Constants.GET_CASH_TYPE);
                        startActivity(intent1);
                        break;
                    case SEND_MONEY_TO_ACCOUNT:
                        AlertMessage.showAlertDialog(this, getString(R.string.transaction_successful));
                        break;
                    default:

                        break;
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
            try {
                Toast.makeText(this, response.getString("message"), Toast.LENGTH_SHORT).show();
                if (response.getString("message").equalsIgnoreCase("Insufficient Balance.")) {
                }
            } catch (JSONException e1) {
                e1.printStackTrace();
                Toast.makeText(this, "message key not found", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onTransactionInitiateError(VolleyError error) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        //Toast.makeText(this, "Initiate transaction error.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAccepted(ArrayList<String> alertArray,JSONObject jData) {
        hideProgressOverlay();
        if (FunduUser.getCountryShortName().equalsIgnoreCase("KEN")) {
            Intent intent1 = new Intent(MerchantProfile.this, PairContactFoundActivity.class);
            intent1.putStringArrayListExtra(Constants.ALERT, alertArray);
            intent1.putExtra(Constants.TRANSACTION_ID, "KENTID");
            intent1.putExtra("amount", requestedAmount);
            intent1.putExtra("tt", Constants.GET_CASH_TYPE);
            startActivity(intent1);
        }
        this.alertArray = alertArray;
        initTransaction(contactItem.getContactId(), requestedAmount);
    }

    private void hideProgressOverlay() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressOverlay != null)
                    progressOverlay.hideProgress();
            }
        });

    }

    @Override
    public void onNoPairFound(ArrayList<String> alertArray) {
        hideProgressOverlay();
        Toast.makeText(MerchantProfile.this, "Transaction Not Accepted!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFindTransactionPairResponse(JSONObject contact) {

    }

    @Override
    public void onFindTransactionPairError(VolleyError error) {

    }

    @Override
    public void onJsonObjectSuccess(JSONObject response, int apiType) throws JSONException {
        Fog.e("UserProfile", response.toString());
        if (response.has("name")) {
            UserProfileItem userProfileItem = UniversalParser.getInstance().parseJsonObject(response, UserProfileItem.class);
//            ratingBar.setRating((float) (userProfileItem.getRating()));
//            addNewAccountNumberLayout(userProfileItem);
            username = userProfileItem.getName();
            usermobile = userProfileItem.getContact_id();
            userid = userProfileItem.getCustid();
        } else {
            onBackPressed();
        }

    }

    private void addNewAccountNumberLayout(UserProfileItem userProfileItem) {
        linkAccountItems = userProfileItem.getIdentities();
        if (linkAccountItems != null)
            for (int i = 0; i < linkAccountItems.size(); i++) {
                LinkAccountItem linkAccountItem = linkAccountItems.get(i);
                if (linkAccountItem.getAdditional_id_type().equals(getString(R.string.account_type)))
                    addViewToLayout(linkAccountItem, i);
            }
    }

    private void addViewToLayout(LinkAccountItem linkAccountItem, int pos) {
        View accountNumberView = getLayoutInflater().inflate(R.layout.user_profile_transaction_item, null);
        TextView sendMoneyToBankTV = (TextView) accountNumberView.findViewById(R.id.sendMoneyToBankTV);
        if (pos == 0 && isMe())
            (accountNumberView.findViewById(R.id.verticalLine)).setVisibility(View.GONE);
        sendMoneyToBankTV.setTag(pos);
        sendMoneyToBankTV.setText(getString(R.string.send_money_to) + " " + linkAccountItem.getName() + "_" + linkAccountItem.getAdditional_id_value());
        sendMoneyToBankTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMoneyToAccount(v);
            }
        });

//        transactionsLayout.addView(accountNumberView);
    }

    private boolean isMe() {
        return contactItem.getContactId().equals(FunduUser.getContactId());
    }

    private void sendMoneyToAccount(View v) {
        titleTV.setText(R.string.send_money_header_text);
        int pos = (Integer) v.getTag();
        recipientID = linkAccountItems.get(pos).getRecipient_id();
        accountNumber = linkAccountItems.get(pos).getAdditional_id_value();
        transactionType = SEND_MONEY_TO_ACCOUNT;
        slidePanel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
    }

    @Override
    public void onFailure(String str, int apiType) {
        try {
            JSONObject job = new JSONObject(str);
            String error = job.optString("ERROR");
            Utils.showShortToast(this, error);
        } catch (Exception e) {
            Utils.showShortToast(this, str);
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        if (!(slidePanel.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED || slidePanel.getPanelState() == SlidingUpPanelLayout.PanelState.HIDDEN)) {
            slidePanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            amountBox.setText(String.valueOf(minimum_amount));
        } else if (progressOverlay.getVisibility() == View.VISIBLE) {
        } else
            super.onBackPressed();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        onIntent(intent);
    }

    private void onIntent(final Intent intent) {
        if (intent.hasExtra(Constants.ALERT) && intent.hasExtra(Constants.PUSH_TYPE)) {
            hideProgressOverlay();
            this.alertArray = intent.getStringArrayListExtra(Constants.ALERT);
            if (intent.getIntExtra(Constants.PUSH_TYPE, -1) == 2 && FunduUser.getUser() != null) {

                initTransaction(contactItem.getContactId(), requestedAmount);
            } else if (intent.getIntExtra(Constants.PUSH_TYPE, -1) == 4) {
                Toast.makeText(MerchantProfile.this, username + " is not available at this moment. Please try again later.", Toast.LENGTH_SHORT).show();
            } else if (intent.getIntExtra(Constants.PUSH_TYPE, -1) == 7) {
                Toast.makeText(MerchantProfile.this, username + " did not accept your request. Please try again later.", Toast.LENGTH_SHORT).show();
            }
        }
        if (intent.hasExtra(Constants.PUSH_TYPE)) {
            hideProgressOverlay();
            if (intent.getIntExtra(Constants.PUSH_TYPE, -1) == 6)
                Utils.showShortToast(getApplicationContext(), Constants.SHOP_CLOSED);
        }
    }

    /*@Override
    public void clickOK() {
        goForTransaction();
    }

    @Override
    public void clickCancel() {

    }*/

    public void onPaymentSuccess(String razorpayPaymentID) {
        try {
            Fog.e("Payment ID", razorpayPaymentID);
        } catch (Exception e) {
            Fog.e("com.merchant", e.getMessage(), e);
        }
    }

    public void onPaymentError(int code, String response) {
        try {
            Toast.makeText(this, "Payment failed: " + Integer.toString(code) + " " + response, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Fog.e(TAG, e.getMessage(), e);
        }
    }


    private BroadcastReceiver ratingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String rating = intent.getStringExtra(Constants.AVERAGE_RATING);
//            ratingBar.setRating(Float.parseFloat(rating));
        }
    };

    @Override
    public void onDismiss(DialogInterface dialog) {

        Fog.e("DISMISS", "" + isdialogsuccess);
        if (isdialogsuccess) {
            isdialogsuccess = false;
            Intent intent = new Intent(this, TransactionSuccessActivity.class);
            intent.putExtra(Constants.RATING_TYPE, 1);
//            intent.putStringArrayListExtra(Constants.ALERT, alerts);
            intent.putExtra(Constants.TRANSACTION_ID, ctid);
            intent.putExtra(Constants.TOTAL_AMOUNT, String.valueOf(amountP));
            intent.putExtra("pname", username);
            intent.putExtra("pmobile", provider_number);
            startActivity(intent);
//            finish();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        try {
            int amount = Integer.parseInt(s.toString().trim());
            amountSelector.setProgress(amount / minimum_twice);
        } catch (NumberFormatException e) {
            // amountBox.setText("100");
            // amountSelectorSeekBar.setProgress(1);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        amountBox.setSelection(s.length());
        try {
            int amount = Integer.parseInt(s.toString().trim());
            if (amount > maximum_amount) {
                amountBox.setText(String.valueOf(maximum_amount));
                amountSelector.setProgress(max);
            }
            if (amount == minimum_amount - 1) {
                amountBox.setText(String.valueOf(minimum_amount));
                amountSelector.setProgress(min);
            }
            /*if (amount < 100) {
                amountBox.setText("100");
                amountSelectorSeekBar.setProgress(1);
                // seekBar.setSelection(0);
            }*/
        } catch (NumberFormatException e) {
            //seekBar.setSelection(0);
            // amountBox.setText("100");
            //  amountSelectorSeekBar.setProgress(1);

        }
    }
}
