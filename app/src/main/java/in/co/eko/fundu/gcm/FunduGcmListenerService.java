package in.co.eko.fundu.gcm;
/*
 * Created by Bhuvnesh
 */

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.StringTokenizer;

import in.co.eko.fundu.R;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.database.tables.UserContactsTable;
import in.co.eko.fundu.event.DataUpdated;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.utils.AppPreferences;
import in.co.eko.fundu.utils.Fog;
import in.co.eko.fundu.utils.FunduAnalytics;

public class FunduGcmListenerService extends FirebaseMessagingService {

    private static final String TAG = "MyGcmListenerService";
    private AppPreferences prefs;


    @Override
    public void onMessageReceived(RemoteMessage message) {

        String from = message.getFrom();
        Map data = message.getData();
        Fog.d("Notification Data-->", "dataAll" + data);
        JSONObject jdata;
        prefs = FunduUser.getAppPreferences();

        try {
            String json = ((String) data.get("data")).replaceAll("\\\\", "");
            JSONObject dataObject = new JSONObject(json);
            String alert = dataObject.getString("alert");
            Fog.d("Notification Data-->", json);
            if(json.contains("InvitationAccepted")) {
                JSONObject jParams = new JSONObject(alert);
                int incentive = jParams.optInt("totalIncentive");
                FunduUser.setTotalIncentiveFromInvitaion(incentive);
                FunduNotificationManager.invitationConverted(this, jParams);
                DataUpdated event = new DataUpdated();
                event.type = DataUpdated.DataUpdatedType.InvitationIncentive;
                EventBus.getDefault().post(event);
            } else if(json.contains("Change your account number")) {
                StringTokenizer tokenizer = new StringTokenizer(alert, ":,");
                ArrayList<String> alertArray = new ArrayList<>();
                while(tokenizer.hasMoreTokens()) {
                    alertArray.add(tokenizer.nextToken());
                }
                FunduNotificationManager.correctAccountNumber(this, alertArray.get(1), alertArray.get(2));
            } else if(json.contains("Shop is closed.")) {
                Fog.d("SHOP", "CLOSED");
                FunduNotificationManager.shopIsClosed(this);
            } else if(json.contains("Transaction Initiated")) {
                String json1 = (String) data.get("jData");
                jdata = new JSONObject(json1);
                FunduNotificationManager.transactionInitiated(this, jdata);
            } else if(json.contains("has updated his account number")) {
                StringTokenizer tokenizer = new StringTokenizer(alert, ":,");
                ArrayList<String> alertArray = new ArrayList<>();
                while(tokenizer.hasMoreTokens()) {
                    alertArray.add(tokenizer.nextToken());
                }
                FunduNotificationManager.updatedAccountNo(this, alertArray.get(0), alertArray.get(1), alertArray.get(2));
            } else {

                String json1 = (String) data.get("jData");
                jdata = new JSONObject(json1);
                if(jdata.optInt(Constants.PushNotificationKeys.PUSH_TYPE) == Constants.PUSH_TYPE_ENUM.TRANSACTION_FAILED.getCode()) {
                    //Transaction failed
                    FunduNotificationManager.onTransactionFailed(this, jdata);
                    return;

                }

                StringTokenizer tokenizer = new StringTokenizer(alert, ":,");
                ArrayList<String> alertArray = new ArrayList<>();

                while(tokenizer.hasMoreTokens()) {
                    alertArray.add(tokenizer.nextToken());
                }
                FunduAnalytics.getInstance(this).sendAction("Notification","Received",alertArray.get(0));
                if(alertArray.get(0).equalsIgnoreCase("Pair Contact found")) {
                    // onPairContactFound(alertArray);
                    onPairContactFound(alertArray, jdata);
//                    {"alert":"Accept or Reject request for Need Cash:e777617a-81ce-4485-8478-bae72b9f87a4,789789789,712345678,100.0,0.0,cash out,0.0,62,10,Ppsingh Singh,Wed Jan 04 17:37:21 IST 2017"}

                } else if(alertArray.get(0).equalsIgnoreCase("Accept or Reject request for Need Cash")) {

                    FunduNotificationManager.openAcceptOrRejectNotification(this, alertArray, jdata);
                } else if(alertArray.get(0).equalsIgnoreCase("Accept or Reject request for Get Cash")) {
                    FunduNotificationManager.openAcceptOrRejectNotification(this, alertArray, jdata);
                } else if(alertArray.get(0).equalsIgnoreCase("Verify Transaction Code")) {
                    FunduNotificationManager.verifyTransaction(this, alertArray, jdata);
                } else if(alertArray.get(0).equalsIgnoreCase("Need Cash Transaction Completed")) {
                    FunduNotificationManager.notifyForRating(this, alertArray, jdata);
                } else if(alertArray.get(0).equalsIgnoreCase("W2W Transaction Completed")) {
                    FunduNotificationManager.notifyForSubmitScreen(this, alertArray);
                } else if(alertArray.get(0).equalsIgnoreCase("No Transaction Pair Found for request Id")) {
                    FunduNotificationManager.noPairFound(this, alertArray, jdata);

                } else if(alertArray.get(0).equalsIgnoreCase("Recipient is not registered")) {
                    FunduNotificationManager.notifyRecipientIsnotRegistered(this, alertArray);
                } else if(alertArray.get(0).equalsIgnoreCase(getString(R.string.user_not_accept_request))) {
                    FunduNotificationManager.notifyRecipientDidNotAccepted(this, alertArray, jdata);
                } else if(alertArray.get(0).trim().equalsIgnoreCase("Transaction Cancelled")) {
                    Fog.e("Transaction Cancelled", alertArray.toString());
                    FunduNotificationManager.onTransactionCancelled(this, alertArray, jdata);
                } else if(jdata.optInt("push_type") == Constants.PUSH_TYPE_ENUM.MERCHANT_ATM_FOUND.getCode()) {
                    FunduNotificationManager.onMerchantAtmsFound(this, jdata);
                } else if(jdata.optInt("push_type") == Constants.PUSH_TYPE_ENUM.MESSAGE_TO_USER.getCode()) {
                    FunduNotificationManager.messageToUser(this, jdata);
                } else if(alertArray.get(1).trim().equalsIgnoreCase("contact_update")) {
                    Fog.e("CONTACT UPDATE ", alertArray.toString());
                    UserContactsTable.updateContactRegisterStatus(this, alertArray.get(5).trim(), 0);
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    private void onPairContactFound(ArrayList<String> alertArray, JSONObject jdata) {

        FunduNotificationManager.openPairFoundNotification(this, alertArray, jdata);

    }


}
