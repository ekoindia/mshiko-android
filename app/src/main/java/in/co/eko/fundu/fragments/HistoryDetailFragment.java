package in.co.eko.fundu.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import in.co.eko.fundu.BuildConfig;

import in.co.eko.fundu.R;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.interfaces.OnFragmentInteractionListener;
import in.co.eko.fundu.models.HistoryUser;
import in.co.eko.fundu.models.TransactionHistoryModel;
import in.co.eko.fundu.utils.Utils;
import in.co.eko.fundu.views.ColorCircleDrawable;
import in.co.eko.fundu.views.customviews.CustomButton;
import in.co.eko.fundu.views.customviews.CustomTextView;

public class HistoryDetailFragment  extends BaseFragment implements View.OnClickListener{

    private ImageView back, userImage, txnTag, mapScreenshot;
    private CustomTextView username, date, amount, fullName, txnId, cashReceivedAmt,
            txnFeeAmt, youPaidAmt, reportIssue, tvCashReceived, tvTxnFee, tvYouPaid, txnStatus,
            reason, rewardAmt, referId;
    private CustomButton btnShare;
    private TransactionHistoryModel historyModel;
    private int color;
    private LinearLayout llTxnSuccess, llTxnFailed, llRating;
    private OnFragmentInteractionListener onFragmentInteractionListener;
    private TextView userInitials;
    private double latitude, longitude;
    private RatingBar ratingBar;
    private RelativeLayout mapLayout, rewardLayout;
    private boolean isReward = false;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        onFragmentInteractionListener = (OnFragmentInteractionListener)activity;
        historyModel = (TransactionHistoryModel) getArguments().get("history");
        color = (int) getArguments().get("color");
        if (historyModel.getTx_type().equalsIgnoreCase("reward")) {
            isReward = true;
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        if (isReward) {
            view = inflater.inflate(R.layout.reward_layout, container, false);
        } else {
            view = inflater.inflate(R.layout.fragment_history_detail, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (isReward) {
            initRewardLayout(view);
            loadRewardData();
        } else {
            initViews(view);

            loadData();
        }

    }

    private void loadRewardData() {
        String currency = Utils.getCurrency(getActivity());
        reason.setText(historyModel.getCustomer_name());
        amount.setText(currency+""+(int)historyModel.getTx_amount());
        referId.setText("Reference id: "+historyModel.getTx_id());
        btnShare.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    private void initRewardLayout(View view) {
        reason = (CustomTextView)view.findViewById(R.id.tv_reason);
        amount = (CustomTextView)view.findViewById(R.id.tv_amount);
        btnShare = (CustomButton)view.findViewById(R.id.btn_share);
        referId = (CustomTextView)view.findViewById(R.id.reference_id);
        back = (ImageView) view.findViewById(R.id.iv_back);
    }

    private void loadData() {
        String currency = Utils.getCurrency(getActivity());
        txnId.setText("Txn ID: "+historyModel.getTx_id());
        fullName.setText(historyModel.getCustomer_name());
        date.setText(formatdate(historyModel.getCreated_at()));
        amount.setText(currency+" "+historyModel.getTx_amount());
        String status = historyModel.getTx_status();
        if (status.equalsIgnoreCase("CANCEl")) {
            llRating.setVisibility(View.GONE);
            setTxnFailedLayout();
            txnTag.setImageLevel(1);
            txnTag.setImageResource(R.drawable.ic_history_cancel);
            username.setText(getString(R.string.transactionCancelled));
            txnStatus.setText(status);
        } else if(status.equalsIgnoreCase("CREDIT_FAILED")) {
            if(historyModel.getRole().equalsIgnoreCase("provider")) {
                setTxnSuccessLayout();
                setTextForSeeker();
                txnFeeAmt.setText(currency+" "+historyModel.getSeeker_charge());
                cashReceivedAmt.setText(currency+" "+historyModel.getTx_amount());
                double totalAmount = historyModel.getTx_amount()+historyModel.getSeeker_charge();
                youPaidAmt.setText(currency+" "+totalAmount);
                txnTag.setImageLevel(1);
                txnTag.setImageResource(R.drawable.ic_history_arrow_credit);
                username.setText(getString(R.string.received_cash_from)+" "+historyModel.getCustomer_name());
                if(historyModel.getProvider()!=null && historyModel.getProvider().getRating()!=0) {
                    llRating.setVisibility(View.VISIBLE);
                    ratingBar.setRating((float)historyModel.getProvider().getRating());
                } else {
                    llRating.setVisibility(View.GONE);
                }
            } else if(historyModel.getRole().equalsIgnoreCase("seeker")) {
                llRating.setVisibility(View.GONE);
                setTxnFailedLayout();
                txnTag.setImageLevel(1);
                txnTag.setImageResource(R.drawable.ic_processing);
                username.setText(getString(R.string.transaction_failed));
                txnStatus.setText(getString(R.string.credit_processing));
                username.setText(getString(R.string.gave_cash_to)+" "+historyModel.getCustomer_name());
            }
        } else if(status.equalsIgnoreCase("DEBIT_FAILED")) {
            llRating.setVisibility(View.GONE);
            setTxnFailedLayout();
            txnTag.setImageLevel(1);
            txnTag.setImageResource(R.drawable.ic_history_cancel);
            username.setText(getString(R.string.transaction_failed));
            txnStatus.setText(getString(R.string.no_refund_required));
        } else if (status.contains("FAILED")) {
            llRating.setVisibility(View.GONE);
            setTxnFailedLayout();
            txnTag.setImageLevel(1);
            txnTag.setImageResource(R.drawable.ic_history_cancel);
            username.setText(getString(R.string.transaction_failed));
            txnStatus.setText(status);
        } else if (status.contains("SUCCESS")) {
            double totalAmount;
            setTxnSuccessLayout();
            llRating.setVisibility(View.VISIBLE);
            cashReceivedAmt.setText(currency+" "+historyModel.getTx_amount());
            if(historyModel.getRole().equalsIgnoreCase  ("provider")) {
                setTextForSeeker(); // provider => seeker and seeker => provider
                txnFeeAmt.setText(currency+" "+historyModel.getSeeker_charge());
                totalAmount = historyModel.getTx_amount()+historyModel.getSeeker_charge();
                youPaidAmt.setText(currency+" "+totalAmount);
                txnTag.setImageLevel(1);
                txnTag.setImageResource(R.drawable.ic_history_arrow_credit);
                username.setText(getString(R.string.received_cash_from)+" "+historyModel.getCustomer_name());
                if(historyModel.getProvider()!=null && historyModel.getProvider().getRating()!=0) {
                    llRating.setVisibility(View.VISIBLE);
                    ratingBar.setRating((float)historyModel.getProvider().getRating());
                } else {
                    llRating.setVisibility(View.GONE);
                }

            } else if (historyModel.getRole().equalsIgnoreCase("seeker")){
                setTextForProvider();
                txnFeeAmt.setText(currency+" "+historyModel.getProvider_charge());
                totalAmount = historyModel.getTx_amount()+historyModel.getProvider_charge();
                youPaidAmt.setText(currency+" "+totalAmount);
                txnTag.setImageLevel(0);
                txnTag.setImageResource(R.drawable.ic_history_arrow_debit);
                username.setText(getString(R.string.gave_cash_to)+" "+historyModel.getCustomer_name());
                if(historyModel.getSeeker()!=null && historyModel.getSeeker().getRating()!=0) {
                    llRating.setVisibility(View.VISIBLE);
                    ratingBar.setRating((float)historyModel.getSeeker().getRating());
                } else {
                    llRating.setVisibility(View.GONE);
                }
            }
        } else if (status.contains("REVERSE")) {
            llRating.setVisibility(View.GONE);
            setTxnFailedLayout();
            txnTag.setImageLevel(1);
            if (historyModel.getRole().equalsIgnoreCase("provider")) {
                txnTag.setImageResource(R.drawable.ic_history_arrow_credit);
                username.setText(getString(R.string.transactionCancelled));
                txnStatus.setText(getString(R.string.refunded));
            } else if(historyModel.getRole().equalsIgnoreCase("seeker")) {
                txnTag.setImageResource(R.drawable.ic_history_cancel);
                username.setText(getString(R.string.transaction_failed));
                txnStatus.setText(getString(R.string.failed));
            }
        }

        setUserImage();
        setMapImage();
    }

    private void setCampaignRewardLayout() {
        mapLayout.setVisibility(View.GONE);
        llTxnSuccess.setVisibility(View.GONE);
        llTxnFailed.setVisibility(View.GONE);
        rewardLayout.setVisibility(View.VISIBLE);
    }

    private void setMapImage() {
        HistoryUser user = null;
        if(historyModel.getRole().equalsIgnoreCase("SEEKER")) {
            user = historyModel.getProvider();
        } else if(historyModel.getRole().equalsIgnoreCase("PROVIDER")) {
            user = historyModel.getSeeker();
        }
        if(user!=null) {
            double[] location = user.getLocation();
            if (location!=null && location.length!=0) {
                longitude = location[0];
                latitude = location[1];
                String url ="https://maps.googleapis.com/maps/api/staticmap?center="+latitude+","+longitude;
                url+="&zoom=16&size=300x150&key=" + BuildConfig.CONSUME_MAP_API_KEY;

                Picasso.Builder picassoBuilder = new Picasso.Builder(getContext());
                Picasso picasso = picassoBuilder.listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                        Toast.makeText(getContext(), exception.toString(), Toast.LENGTH_SHORT).show();
                    }
                }).build();
                picasso.load(url).into(mapScreenshot);
//                Picasso.with(getContext()).load(url).into(mapScreenshot);
//                    @Override
//                    public void onSuccess() {
//                        mapLayout.setVisibility(View.VISIBLE);
//                    }
//
//                    @Override
//                    public void onError() {
//                        Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
//                        mapLayout.setVisibility(View.GONE);
//                    }
//                });
//                Picasso picasso = picassoBuilder.listener(new Picasso.Listener() {
//                    @Override
//                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
//                        Toast.makeText(getContext(), exception.toString(), Toast.LENGTH_SHORT).show();
//                    }
//                }).build();
//                Picasso.with(getContext()).load(url).into(mapScreenshot, picasso);
            } else {
                mapLayout.setVisibility(View.GONE);
            }
        } else {
            mapLayout.setVisibility(View.GONE);
        }
    }

    private void setTextForProvider() {
        tvCashReceived.setText(getString(R.string.cash_provided));
        tvTxnFee.setText(getString(R.string.you_earned));
        tvYouPaid.setText(getString(R.string.you_got));
    }

    private void setTextForSeeker() {
        tvCashReceived.setText(getString(R.string.cash_received));
        tvTxnFee.setText(getString(R.string.txn_fee));
        tvYouPaid.setText(getString(R.string.you_paid));
    }

    private void setTxnFailedLayout() {
        llTxnSuccess.setVisibility(View.GONE);
        llTxnFailed.setVisibility(View.VISIBLE);
    }

    private void setTxnSuccessLayout() {
        llTxnFailed.setVisibility(View.GONE);
        llTxnSuccess.setVisibility(View.VISIBLE);
    }

    private void setUserImage() {
        String name = historyModel.getCustomer_name();
        userImage.setVisibility(View.GONE);
        userInitials.setVisibility(View.VISIBLE);
        if(name != null && name.length()>=1){
            name = name.substring(0,1);
            userInitials.setText(name);
            userInitials.setBackground(new ColorCircleDrawable(color));
        }
    }

//    private void setBackground() {
//        int sdk = android.os.Build.VERSION.SDK_INT;
//        if(sdk <= android.os.Build.VERSION_CODES.JELLY_BEAN) {
//
//            userInitials.setBackgroundDrawable(new ColorCircleDrawable(color));
//        } else {
//            userInitials.setBackground(new ColorCircleDrawable(color));
//        }
//    }

    String formatdate(String pDate){
//                                 2017-02-17 11:49:17
        String oldFormat = "yyyy-MM-dd HH:mm:ss";
        //final String NEW_FORMAT = "hh:mm a\n dd/MMM/yy";
        String newFormat = "dd MMMM yyyy | h:mm a";
        String newDateString = "";

        SimpleDateFormat sdf = new SimpleDateFormat(oldFormat);
        Date d = null;
        try {
            d = sdf.parse(pDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sdf.applyPattern(newFormat);
        newDateString = sdf.format(d);
        return newDateString;
    }

    private void initViews(View view) {
        back = (ImageView) view.findViewById(R.id.iv_back);
        userImage = (ImageView) view.findViewById(R.id.user_image);
        txnTag = (ImageView) view.findViewById(R.id.txn_tag);
        mapScreenshot = (ImageView) view.findViewById(R.id.map_screenshot);
        btnShare = (CustomButton) view.findViewById(R.id.btn_share);
        userInitials = (TextView) view.findViewById(R.id.user_initial);
        username = (CustomTextView) view.findViewById(R.id.user_name);
        rewardLayout = (RelativeLayout) view.findViewById(R.id.layout_reward);
        rewardAmt = (CustomTextView) view.findViewById(R.id.reward_amt);
        date = (CustomTextView) view.findViewById(R.id.tv_date);
        amount = (CustomTextView) view.findViewById(R.id.tv_amt);
        fullName = (CustomTextView) view.findViewById(R.id.fullname);
        txnId = (CustomTextView) view.findViewById(R.id.txn_id);
        cashReceivedAmt = (CustomTextView) view.findViewById(R.id.cash_received_amt);
        txnFeeAmt = (CustomTextView) view.findViewById(R.id.txn_fee_amt);
        youPaidAmt = (CustomTextView) view.findViewById(R.id.you_paid_amt);
        reportIssue = (CustomTextView) view.findViewById(R.id.report_issue);
        llTxnSuccess = (LinearLayout) view.findViewById(R.  id.layout_txn_success);
        llTxnFailed = (LinearLayout) view.findViewById(R.id.layout_txn_failed);
        llRating = (LinearLayout) view.findViewById(R.id.rating_layout);
        ratingBar = (RatingBar) view.findViewById(R.id.rating);
        tvCashReceived = (CustomTextView) view.findViewById(R.id.tv_cash);
        tvTxnFee = (CustomTextView) view.findViewById(R.id.tv_txn_fee);
        tvYouPaid = (CustomTextView) view.findViewById(R.id.tv_you_paid);
        txnStatus = (CustomTextView) view.findViewById(R.id.txn_status);
        mapLayout = (RelativeLayout) view.findViewById(R.id.map_layout);
        back.setOnClickListener(this);
        reportIssue.setOnClickListener(this);
        btnShare.setOnClickListener(this);
        mapLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back :
                getActivity().onBackPressed();
                break;
            case R.id.report_issue :
                Bundle bundle = new Bundle();
                bundle.putString("needHelp", "needHelp");
                bundle.putString(Constants.PushNotificationKeys.TID, "Txn ID: "+historyModel.getTx_id());
                onFragmentInteractionListener.onFragmentInteraction(bundle);
                break;
            case R.id.btn_share :
//                btnShare.setEnabled(false);
                shareScreen();
                break;
            case R.id.map_screenshot :
                String uri = "https://www.google.com/maps/search/?api=1&query=" + latitude + "," + longitude;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                getContext().startActivity(intent);
                break;
            default:
                break;

        }
    }

    private void shareScreen() {
        Bitmap bitmap = takeScreenShot();
        File file = storeBitmap(bitmap, historyModel.getTx_id());
        int sdk = Build.VERSION.SDK_INT;
        Uri uri;
        if(sdk<=Build.VERSION_CODES.M) {
            uri = Uri.fromFile(file);
        } else {
            uri = FileProvider.getUriForFile(getContext(), getString(R.string.file_provider_authority),file);
        }
        shareIt(uri);
    }

    private void shareIt(Uri uri) {
//        Uri uri = Uri.fromFile(file);
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("image/*");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Fundu Transaction");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);

        startActivity(Intent.createChooser(sharingIntent, "Share via"));
//        btnShare.setEnabled(true);
    }


    private File storeBitmap(Bitmap bm, String filename){
        final String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Screenshots";
        File dir = new File(dirPath);
        if(!dir.exists())
            dir.mkdirs();
        File file = new File(dirPath, filename);
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    private Bitmap takeScreenShot() {
        View screenView = getView().findViewById(R.id.ll_screenshot);

        screenView.setDrawingCacheEnabled(true);

        screenView.buildDrawingCache(true);
        Bitmap b = screenView.getDrawingCache();

        Bitmap bitmap1 = Bitmap.createBitmap(b);
        screenView.setDrawingCacheEnabled(false);
        return bitmap1;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
