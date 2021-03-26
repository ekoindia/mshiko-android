package in.co.eko.fundu.gcm;
/*
 * Created by Bhuvnesh
 */

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import in.co.eko.fundu.FunduApplication;
import in.co.eko.fundu.R;
import in.co.eko.fundu.activities.CashRequestAction;
import in.co.eko.fundu.activities.GetCashFromContact;
import in.co.eko.fundu.activities.HomeActivity;
import in.co.eko.fundu.activities.MerchantProfile;
import in.co.eko.fundu.activities.SplashActivity;
import in.co.eko.fundu.activities.TransactionStatusActivity;
import in.co.eko.fundu.activities.UpadteAccountNoActivity;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.database.greendao.FunduTransaction;
import in.co.eko.fundu.database.tables.TransactionStatusTable;
import in.co.eko.fundu.event.ProviderTransactionEvent;
import in.co.eko.fundu.event.SeekerTransactionEvent;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.models.User;
import in.co.eko.fundu.utils.AppPreferences;
import in.co.eko.fundu.utils.DateUtils;
import in.co.eko.fundu.utils.Fog;
import in.co.eko.fundu.utils.GreenDaoHelper;
import in.co.eko.fundu.utils.Utils;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


public class FunduNotificationManager {
    private static String TAG = FunduNotificationManager.class.getName();

    public static final String NOTIFICATION_CHANNEL_ID = "10001";

    public static OnPairResult getOnPairResult() {
        return onPairResult;
    }

    public static void setOnPairResult(OnPairResult onPairResult) {
        FunduNotificationManager.onPairResult = onPairResult;
    }

    public static OnPairResult onPairResult;

    public static void correctAccountNumber(Context context, String name, String custid) {

        final int notificationId = new SecureRandom().nextInt();
        AppPreferences pref = FunduUser.getAppPreferences();
        ArrayList<Integer> list = pref.getListInt(Constants.PushNotificationKeys.EXTRA_NOTIFI);
        if(list==null) {
            list = new ArrayList<>();
        }
        list.add(notificationId);
        pref.putListInt(Constants.PushNotificationKeys.EXTRA_NOTIFI, list);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        User user = FunduUser.getUser();
        Utils.playRingtone(context);
        Intent notificationIntent = new Intent(context, UpadteAccountNoActivity.class);
        notificationIntent.putExtra("noti_type", 1);
        notificationIntent.putExtra("custid5", custid);
        notificationIntent.putExtra(Constants.NOTIFICATION_ID, notificationId);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1,notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, iCancel,
//                PendingIntent.FLAG_CANCEL_CURRENT);

        if (user != null) {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle("Hi, " + user.getName() + ", ")
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setStyle(new NotificationCompat.BigTextStyle().bigText("Please correct your bank account number which is entered wrong by you."))
                    .setContentText("Please correct your bank account number which is entered wrong by you.")
                    .setAutoCancel(true)
                    .setTicker("Account Number Update")
                    .setContentIntent(pendingIntent);
//                    .setSound(defaultSoundUri);
            final NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Fundu", importance);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
//                    notificationChannel.setLightColor(Color.parseColor("#"));
                notificationChannel.enableVibration(true);
                assert notificationManager != null;
                notificationBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
                notificationManager.createNotificationChannel(notificationChannel);
            }

            notificationManager.notify(notificationId /* ID of notification */, notificationBuilder.build());
        }
    }

    public static void updatedAccountNo(Context context, String text, String phnumber, String custid) {

//        String name1 = text;
//        String phno = phnumber;
//        String customerId = custid;
//        String name = name1.replace(" has updated his account number", "");
//        final int notificationId = new SecureRandom().nextInt(); // just use a counter in some util class...
//        Intent iCancel = new Intent(context, FunduRequestReceiver.class);
//        iCancel.putExtra(Constants.NOTIFICATION_ID, notificationId);
//        iCancel.putExtra(Constants.DECISION, false);
//        iCancel.putExtra("noti_type", 2);
//        iCancel.setFlags(0);
//        Intent iAccept = new Intent(context, UserProfileActivity.class);
//        iAccept.putExtra(Constants.NOTIFICATION_ID, notificationId);
//        iAccept.putExtra(Constants.DECISION, true);
//        iAccept.putExtra("phno", phno);
//        iAccept.putExtra("name", name);
//        iAccept.putExtra("seekercustid", customerId);
//        iAccept.putExtra("noti_type", 2);
//        Fog.e(" NNNNNNNNNNNNNN ", name + " phno " + phno + " custid " + customerId);
////        iAccept.setFlags(0);
////        PendingIntent cancelIntent = PendingIntent.getActivity(context,0,iCancel,0);
//        PendingIntent acceptIntent = PendingIntent.getActivity(context, 0, iAccept, PendingIntent.FLAG_UPDATE_CURRENT);
//        PendingIntent cancelIntent = PendingIntent.getBroadcast(context, 1, iCancel,
//                PendingIntent.FLAG_CANCEL_CURRENT);
////        PendingIntent acceptIntent = PendingIntent.getBroadcast(context, 2, iAccept,
////                PendingIntent.FLAG_CANCEL_CURRENT);
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        User user = FunduUser.getUser();
//        String currency = context.getString(R.string.ruppee_symbol);
//        Utils.playRingtone(context);
//        if (user != null) {
//
//            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
//                    .setSmallIcon(R.drawable.ic_notification)
//                    .setContentTitle("Hi, " + user.getName() + ", ")
//                    .setDefaults(Notification.DEFAULT_ALL)
//                    .setWhen(System.currentTimeMillis())
//                    .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
//                    .setContentText(text)
//                    .setAutoCancel(true)
//                    .setTicker("Account Number Update")
////                    .setSound(defaultSoundUri)
//                    .addAction(R.drawable.ic_action_close, "Cancel", cancelIntent)
//                    .addAction(R.drawable.ic_action_done, "Retry", acceptIntent);
//                /*.setContentIntent(pendingIntent);*/
//            final NotificationManager notificationManager =
//                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//            notificationManager.notify(notificationId /* ID of notification */, notificationBuilder.build());
//        }
    }


    public static void openAcceptOrRejectNotification(Context context, ArrayList<String> alertArray, JSONObject jData) {

        final int notificationId = new SecureRandom().nextInt(); // just use a counter in some util class...
        Fog.d("openAcceptOrRejectNotification","data"+jData);
        Intent iCancel = new Intent(context, FunduRequestReceiver.class);
        iCancel.putExtra(Constants.PUSH_TYPE, Constants.PUSH_TYPE_ENUM.ACCEPT_REJECT.getCode());
        iCancel.putExtra(Constants.NOTIFICATION_ID, notificationId);
        // DECISION true for accept and false for cancel
        iCancel.putExtra(Constants.DECISION, false);
        iCancel.putStringArrayListExtra(Constants.ALERT, alertArray);
        iCancel.putExtra(Constants.PUSH_JSON_DATA, jData.toString());
//        iCancel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        iCancel.setFlags(0);
        Intent iAccept = new Intent(context, FunduRequestReceiver.class);
        iAccept.putExtra(Constants.NOTIFICATION_ID, notificationId);
        iAccept.putExtra(Constants.DECISION, true);
        iAccept.putStringArrayListExtra(Constants.ALERT, alertArray);
        iAccept.putExtra(Constants.PUSH_JSON_DATA, jData.toString());
//        iAccept.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        iAccept.setFlags(0);
        iAccept.putExtra(Constants.PUSH_TYPE, Constants.PUSH_TYPE_ENUM.ACCEPT_REJECT.getCode());

        int uniqueInt1 = new SecureRandom().nextInt();
        int uniqueInt2 = new SecureRandom().nextInt();
        int uniqueInt3 = new SecureRandom().nextInt();

        PendingIntent cancelIntent = PendingIntent.getBroadcast(context, uniqueInt1, iCancel,
                PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent acceptIntent = PendingIntent.getBroadcast(context, uniqueInt2, iAccept,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Intent tapIntent = new Intent(context, CashRequestAction.class);
        tapIntent.putExtra(Constants.NOTIFICATION_ID, notificationId);
        tapIntent.putExtra(Constants.PUSH_TYPE, Constants.PUSH_TYPE_ENUM.ACCEPT_REJECT.getCode());
        tapIntent.putStringArrayListExtra(Constants.ALERT, alertArray);
        tapIntent.putExtra(Constants.PUSH_JSON_DATA, jData.toString());

        PendingIntent tapIntentPending = PendingIntent.getActivity(context,uniqueInt3,tapIntent,PendingIntent.FLAG_CANCEL_CURRENT);
        User user = FunduUser.getUser();
        String currency = "";
        if(FunduUser.getCountryShortName().equalsIgnoreCase("KEN")){
            currency = context.getString(R.string.ksh_symbol);
        }
        else{
             currency = context.getString(R.string.ruppee_symbol);
        }
        String amt[] = jData.optString(Constants.PushNotificationKeys.AMOUNT).split("\\.");

        Utils.playRingtone(context);
        if (user != null) {

            double rating = jData.optDouble(Constants.PushNotificationKeys.RATING);
            String reward = jData.optString(Constants.PushNotificationKeys.PROVIDER_CHARGE);

            String message = jData.optString(Constants.PushNotificationKeys.NAME) + " needs some cash.";
            if(reward != null && reward.length() > 0 && !reward.equalsIgnoreCase("0")){
                message = message +" Earn " + currency + reward + " by helping with " + currency + amt[0];
            }
            else{
                message = message+" Please help with " + currency + amt[0];
            }

            if(rating > 0){
                message = message+"\nRating: " + String.format("%.1f", rating);
            }

            message = message + "\nAbout " +
                    Double.valueOf(alertArray.get(8)) + " m near you";

                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_notification)
                    /*.setContentTitle("Hi, " + user.getName() + ", ")*/
                        .setContentTitle("Be Fundu!")
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setWhen(System.currentTimeMillis())
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message)).setContentText(message)
                        .setAutoCancel(true)
                        .setTicker(alertArray.get(0).replace("Accept or Reject request for ", ""))
                        .setContentIntent(tapIntentPending)
                        .addAction(R.drawable.ic_action_close, "Cancel", cancelIntent)
                        .addAction(R.drawable.ic_action_done, "Accept", acceptIntent);

                /*.setContentIntent(pendingIntent);*/
                final NotificationManager notificationManager =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
                    int importance = NotificationManager.IMPORTANCE_HIGH;
                    NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Fundu", importance);
                    notificationChannel.enableLights(true);
                    notificationChannel.setLightColor(Color.RED);
//                    notificationChannel.setLightColor(Color.parseColor("#"));
                    notificationChannel.enableVibration(true);
                    assert notificationManager != null;
                    notificationBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
                    notificationManager.createNotificationChannel(notificationChannel);
                }
                notificationManager.notify(notificationId /* ID of notification */, notificationBuilder.build());
                long delayInMilliseconds = Integer.parseInt(alertArray.get(9)) * 1000;
                new AsyncTask<Long, Void, Void>() {
                    @Override
                    protected Void doInBackground(Long[] params) {
                        try {
                            Thread.sleep(params[0]);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        notificationManager.cancel(notificationId);
                    }
                }.execute(delayInMilliseconds);

        }
    }
    public static void invitationConverted(Context context, JSONObject object){
        User user = FunduUser.getUser();
        String convertedContact = object.optString("name");
        final int notificationId = new SecureRandom().nextInt();
        AppPreferences pref = FunduUser.getAppPreferences();
        ArrayList<Integer> list = pref.getListInt(Constants.PushNotificationKeys.EXTRA_NOTIFI);
        if(list==null) {
            list = new ArrayList<>();
        }
        list.add(notificationId);
        pref.putListInt(Constants.PushNotificationKeys.EXTRA_NOTIFI, list);
        Utils.playRingtone(context);
        if (user != null) {
            int incentive = object.optInt("totalIncentive");
            String title = "Invitation Accepted";
            String body = "Your friend "+convertedContact+" is on Fundu Now.";
            if(incentive != 0)
                body = body+"\nTotal Earnings - "+incentive;
            Intent notificationIntent = new Intent(FunduApplication.getAppContext(), SplashActivity.class);

            notificationIntent.setAction("notification_files");
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0,notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle(title)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                    .setContentText(body)
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true);


//                    .setSound(defaultSoundUri)

                /*.setContentIntent(pendingIntent);*/
            final NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Fundu", importance);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
//                    notificationChannel.setLightColor(Color.parseColor("#"));
                notificationChannel.enableVibration(true);
                assert notificationManager != null;
                notificationBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
                notificationManager.createNotificationChannel(notificationChannel);
            }
            notificationManager.notify(notificationId /* ID of notification */, notificationBuilder.build());
        }
    }

    public static void shopIsClosed(Context context) {

        Intent intent = null;
        if (onPairResult instanceof HomeActivity)
            intent = new Intent(context, HomeActivity.class);
        else if (onPairResult instanceof MerchantProfile)
            intent = new Intent(context, MerchantProfile.class);
        intent.putExtra(Constants.PUSH_TYPE, Constants.PUSH_TYPE_ENUM.SHOP_CLOSED.getCode());
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

    }

    public static void openPairFoundNotification(Context context, ArrayList<String> alertArray,JSONObject jsonObject) {
        Intent intent = null;
        if (onPairResult instanceof HomeActivity)
            intent = new Intent(context, HomeActivity.class);
        else if (onPairResult instanceof MerchantProfile)
            intent = new Intent(context, MerchantProfile.class);
        else if(onPairResult instanceof GetCashFromContact)
            intent = new Intent(context,GetCashFromContact.class);
        intent.putStringArrayListExtra(Constants.ALERT, alertArray);
        intent.putExtra(Constants.PUSH_JSON_DATA, jsonObject.toString());
        intent.putExtra(Constants.PUSH_TYPE, Constants.PUSH_TYPE_ENUM.PAIR_FOUND.getCode());
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    public static void notifyForRating(Context context, ArrayList<String> alertArray, JSONObject jData) {
       /* LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(Constants.DISMISS_PATH_SCREEN_ACTION));
        TransactionStatusTable.deleteTransactionPairByRequesterId(context, alertArray.get(alertArray.size() - 3));
        Intent intent = new Intent(context, HomeActivity.class);
        intent.putStringArrayListExtra(Constants.ALERT, alertArray);
        intent.putExtra(Constants.PUSH_TYPE, 3);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);*/
        GreenDaoHelper.getInstance(context).updateTransactionState(jData.optString(Constants.PushNotificationKeys.TID), jData.optString(Constants.PushNotificationKeys.PAIR_REQUEST_ID) ,Constants.TRANSACTION_STATE.RATING_PENDING.getCode());
        SeekerTransactionEvent event = new SeekerTransactionEvent(jData, Constants.PUSH_TYPE_ENUM.NEEDCASH_TRANSACTION_COMPLETED.getCode());
        EventBus.getDefault().post(event);

    }

    public static void notifyForSubmitScreen(Context context, ArrayList<String> alertArray) {
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(Constants.DISMISS_PATH_SCREEN_ACTION));
        TransactionStatusTable.deleteTransactionPairByRequesterId(context, alertArray.get(alertArray.size() - 3));
        Intent intent = new Intent(context, HomeActivity.class);
        intent.putStringArrayListExtra(Constants.ALERT, alertArray);
        intent.putExtra(Constants.PUSH_TYPE, Constants.PUSH_TYPE_ENUM.W2W_TRANSACTION_COMPLETED.getCode());
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void notifyRecipientIsnotRegistered(Context context, ArrayList<String> alertArray) {
//        Intent intent = new Intent(context, UserProfileActivity.class);
        Intent intent = null;
        if (onPairResult instanceof MerchantProfile)
            intent = new Intent(context, MerchantProfile.class);
        intent.putStringArrayListExtra(Constants.ALERT, alertArray);
        intent.putExtra(Constants.PUSH_TYPE, Constants.PUSH_TYPE_ENUM.NO_PAIR_FOUND.getCode());
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
//        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(Constants.DISMISS_PATH_SCREEN_ACTION));
//        TransactionStatusTable.deleteTransactionPairByRequesterId(context, alertArray.get(alertArray.size() - 3));
//        Intent intent = new Intent(context, HomeActivity.class);
//        intent.putStringArrayListExtra(Constants.ALERT, alertArray);
//        intent.putExtra(Constants.PUSH_TYPE, 5);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(intent);
    }

    public static void notifyRecipientDidNotAccepted(Context context, ArrayList<String> alertArray,JSONObject jData) {
//        Intent intent = new Intent(context, UserProfileActivity.class);
        Intent intent = null;
        if (onPairResult instanceof MerchantProfile)
            intent = new Intent(context, MerchantProfile.class);
        else if(onPairResult instanceof GetCashFromContact)
            intent = new Intent(context,GetCashFromContact.class);
        intent.putStringArrayListExtra(Constants.ALERT, alertArray);
        intent.putExtra(Constants.PUSH_TYPE, Constants.PUSH_TYPE_ENUM.USER_DIDNOT_ACCEPT.getCode());
        intent.putExtra(Constants.PUSH_JSON_DATA,jData.toString());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void noPairFound(Context context, ArrayList<String> alertArray, JSONObject jData) {
        Intent intent = null;
        if (onPairResult instanceof HomeActivity)
            intent = new Intent(context, HomeActivity.class);
        else if (onPairResult instanceof MerchantProfile)
            intent = new Intent(context, MerchantProfile.class);
        else if(onPairResult instanceof GetCashFromContact)
            intent = new Intent(context,GetCashFromContact.class);
        intent.putStringArrayListExtra(Constants.ALERT, alertArray);
        intent.putExtra(Constants.PUSH_JSON_DATA,jData.toString());
        intent.putExtra(Constants.PUSH_TYPE, Constants.PUSH_TYPE_ENUM.NO_PAIR_FOUND.getCode());
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        onPairResult.onNoPairFound(alertArray);
    }

    public static void onTransactionFailed(Context context,JSONObject jdata){
        String prid = jdata.optString(Constants.PushNotificationKeys.PAIR_REQUEST_ID);
        Fog.logEvent(false, prid, "FunduNotificationManager","onTransactionFailed", "onTransactionFailed", DateUtils.getCurrentUTCtime(), Constants.getState(Constants.PUSH_TYPE_ENUM.TRANSACTION_FAILED.getCode()));

        GreenDaoHelper.getInstance(context).deleteFunduTransaction(prid);
        Intent intent = new Intent(context, TransactionStatusActivity.class);
        intent.setAction("transaction_failed");
        intent.putExtra(Constants.PUSH_TYPE, Constants.PUSH_TYPE_ENUM.TRANSACTION_FAILED.getCode());
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void onTransactionCancelled(Context context, ArrayList<String> alertArray,
                                              JSONObject jdata) {
        String pairRequestId = jdata.optString(Constants.PushNotificationKeys.PAIR_REQUEST_ID);
        Fog.logEvent(false, pairRequestId, "FunduNotificationManager","onTransactionCancelled", "onTransactionCancelled", DateUtils.getCurrentUTCtime(), Constants.getState(Constants.PUSH_TYPE_ENUM.TRANSACTION_CANCELLED.getCode()));

        GreenDaoHelper.getInstance(context).deleteFunduTransaction(pairRequestId);
        Intent intent = new Intent(context, TransactionStatusActivity.class);
        intent.setAction("cancel");
        intent.putStringArrayListExtra(Constants.ALERT, alertArray);
        intent.putExtra(Constants.PUSH_TYPE, Constants.PUSH_TYPE_ENUM.TRANSACTION_CANCELLED.getCode());
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void verifyTransaction(Context context, ArrayList<String> alertArray, JSONObject jData) {


        Fog.i(TAG, "verifyTransaction");


        FunduTransaction transaction = GreenDaoHelper.getInstance(context).updateTransactionState(jData.optString(Constants.PushNotificationKeys.TID), jData.optString(Constants.PushNotificationKeys.PAIR_REQUEST_ID), Constants.TRANSACTION_STATE.PROVIDER_VERIFY_CODE.getCode());
        Intent intent = new Intent(context, HomeActivity.class);
        intent.putExtra(Constants.ALERT, alertArray);
        intent.putExtra(Constants.PUSH_JSON_DATA, jData.toString());
        intent.putExtra(Constants.PUSH_TYPE, Constants.PUSH_TYPE_ENUM.VERIFY_TRANSACTION_CODE.getCode());
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.FUNDU_TRANSACTION_ID, transaction.getId());
        context.startActivity(intent);


        //EventBus.getDefault().post(new TransactionInitiatedEvent(jData,"9",alertArray));
    }

    public static void transactionInitiated(Context context, JSONObject jdata) {
        GreenDaoHelper.getInstance(context).updateTransactionTid(jdata.optString(Constants.PushNotificationKeys.PAIR_REQUEST_ID),
                jdata.optString(Constants.PushNotificationKeys.TID));
        EventBus.getDefault().post(new ProviderTransactionEvent(jdata, Constants.PUSH_TYPE_ENUM.TRANSACTION_INITIATED.getCode()));
    }

    public static void messageToUser(Context context,JSONObject jdata){
        String message = jdata.optString("message");
        String deeplink = jdata.optString("deeplink");
        final int notificationId = new SecureRandom().nextInt();
        AppPreferences pref = FunduUser.getAppPreferences();
        ArrayList<Integer> list = pref.getListInt(Constants.PushNotificationKeys.EXTRA_NOTIFI);
        if(list==null) {
            list = new ArrayList<>();
        }
        list.add(notificationId);
        pref.putListInt(Constants.PushNotificationKeys.EXTRA_NOTIFI, list);
        PendingIntent tapIntentPending = null;
        if(!TextUtils.isEmpty(deeplink)){
            Intent tapIntent = new Intent(Intent.ACTION_VIEW);
            tapIntent.setData(Uri.parse(deeplink));
            tapIntentPending = PendingIntent.getActivity(FunduApplication.getAppContext(),(int)new Date().getTime(),
                    tapIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        }
        else{
            Intent tapIntent = new Intent(context,HomeActivity.class);
            tapIntentPending = PendingIntent.getActivity(FunduApplication.getAppContext(),(int)new Date().getTime(),
                    tapIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(jdata.optString("title"))
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentText(message)
                .setSound(Uri.parse("android.resource://in.co.eko.fundu/" + R.raw.notification_tone))
                .setAutoCancel(true);

        if(tapIntentPending != null)
            notificationBuilder.setContentIntent(tapIntentPending);

        final NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Fundu", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
//                    notificationChannel.setLightColor(Color.parseColor("#"));
            notificationChannel.enableVibration(true);
            assert notificationManager != null;
            notificationBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    public static void createElectronicCreditNotification(String amount) {
        final int notificationId = new SecureRandom().nextInt();
        AppPreferences pref = FunduUser.getAppPreferences();
        ArrayList<Integer> list = pref.getListInt(Constants.PushNotificationKeys.EXTRA_NOTIFI);
        if(list==null) {
            list = new ArrayList<>();
        }
        list.add(notificationId);
        pref.putListInt(Constants.PushNotificationKeys.EXTRA_NOTIFI, list);
        Intent tapIntent = new Intent(FunduApplication.getAppContext(), HomeActivity.class);
        tapIntent.putExtra(Constants.PUSH_TYPE, Constants.PUSH_TYPE_ENUM.CREDIT_SUCCESS.getCode());
        PendingIntent tapIntentPending = PendingIntent.getActivity(FunduApplication.getAppContext(),0,
                tapIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        // Uri alert = Uri.parse("android.resource://in.co.eko.fundu/" + R.raw.notification_tone);
        String body = Utils.getCurrency(FunduApplication.getAppContext())+amount+" "+FunduApplication.getAppContext().getString(R.string.electronic_credit);


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(FunduApplication.getAppContext())
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(FunduApplication.getAppContext().getString(R.string.funduNotification))
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setContentText(body)
                .setContentIntent(tapIntentPending)
                .setSound(Uri.parse("android.resource://in.co.eko.fundu/" + R.raw.notification_tone))
                .setAutoCancel(true);

        final NotificationManager notificationManager =
                (NotificationManager) FunduApplication.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Fundu", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
//                    notificationChannel.setLightColor(Color.parseColor("#"));
            notificationChannel.enableVibration(true);
            assert notificationManager != null;
            notificationBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        notificationManager.notify(notificationId /* ID of notification */, notificationBuilder.build());



    }
    public static void onMerchantAtmsFound(Context context,JSONObject jData){
        Intent intent = new Intent(context,HomeActivity.class);
        intent.putExtra(Constants.PUSH_JSON_DATA,jData.toString());
        intent.putExtra(Constants.PUSH_TYPE,Constants.PUSH_TYPE_ENUM.MERCHANT_ATM_FOUND.getCode());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

    }

    public interface OnPairResult {
        void onAccepted(ArrayList<String> alertArray,JSONObject jData);

        void onNoPairFound(ArrayList<String> alertArray);
    }





}

