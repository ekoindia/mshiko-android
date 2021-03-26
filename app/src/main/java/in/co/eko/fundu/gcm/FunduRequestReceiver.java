package in.co.eko.fundu.gcm;
/*
 * Created by Bhuvnesh
 */

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.android.volley.ClientError;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.co.eko.fundu.R;
import in.co.eko.fundu.activities.CashRequestAction;
import in.co.eko.fundu.activities.GiveCash;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.database.greendao.FunduTransaction;
import in.co.eko.fundu.database.tables.TransactionStatusTable;
import in.co.eko.fundu.models.Contact;
import in.co.eko.fundu.requests.TransactionPairResponseRequest;
import in.co.eko.fundu.utils.Fog;
import in.co.eko.fundu.utils.FunduAnalytics;
import in.co.eko.fundu.utils.GreenDaoHelper;
import in.co.eko.fundu.utils.Utils;

public class FunduRequestReceiver extends BroadcastReceiver {

    int noti_type = 0;
    String custid = null;
    Context context;

    @Override
    public void onReceive(final Context context, final Intent intent) {

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(intent.getIntExtra(Constants.NOTIFICATION_ID, -1));
        this.context = context;
        noti_type = intent.getIntExtra("noti_type", 0);
        custid = intent.getStringExtra("custid");
        if (noti_type == 1) {
            Intent pairIntent = new Intent(Constants.HOME_ACTIVITY_ACTION);
            pairIntent.putExtra(Constants.UPDATE_ACCOUNT_NO_INTENT, true);
            pairIntent.putExtra("custid3", custid);
            LocalBroadcastManager.getInstance(context).sendBroadcast(pairIntent);
        } else if (noti_type == 2) {
            Utils.showShortToast(context, "Cancelled");
//            Intent pairIntent = new Intent(Constants.HOME_ACTIVITY_ACTION);
//            LocalBroadcastManager.getInstance(context).sendBroadcast(pairIntent);
        } else {
            final ArrayList<String> alerts = intent.getStringArrayListExtra(Constants.ALERT);
            JSONObject json = new JSONObject();
            try {
                json = new JSONObject(intent.getStringExtra(Constants.PUSH_JSON_DATA));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            final JSONObject jData = json;
            if (intent.getIntExtra(Constants.PUSH_TYPE, -1) == Constants.PUSH_TYPE_ENUM.ACCEPT_REJECT.getCode()) {
                Fog.e("NOTI FRR", intent.getStringArrayListExtra(Constants.ALERT).toString());
                TransactionPairResponseRequest transactionPairRequest = new TransactionPairResponseRequest(context);
                transactionPairRequest.setData(alerts.get(1), alerts.get(2), intent.getBooleanExtra(Constants.DECISION, false) ? "1" : "2");
                transactionPairRequest.setParserCallback(new TransactionPairResponseRequest.OnTransactionPairResults() {

                    @Override
                    public void onTransactionPairResponse(Contact contact) {
                        if (intent.getBooleanExtra(Constants.DECISION, false)) {
                            FunduTransaction transaction = GreenDaoHelper.getInstance(context).populateTransaction(alerts,jData);
                            FunduAnalytics.getInstance(context) .sendAction("Transaction","Accepted",(int)Double.parseDouble(transaction.getAmount()));
                            transaction.setState(Constants.TRANSACTION_STATE.PROVIDER_ACCEPTED.getCode());
                            GreenDaoHelper.getInstance(context).addTransaction(transaction);
                            TransactionStatusTable.addTransaction(context, alerts);
                            Intent notificationIntent = new Intent(context, GiveCash.class);
                            notificationIntent.putExtra(Constants.ALERT,alerts);
                            notificationIntent.putExtra(Constants.PUSH_JSON_DATA,jData.toString());
                            notificationIntent.putExtra(Constants.FUNDU_TRANSACTION_ID,transaction.getId());
                            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(notificationIntent);

                        }
                    }

                    @Override
                    public void onTransactionPairError(VolleyError error) {
                        if(error instanceof ClientError) {
                            if(intent.getBooleanExtra(Constants.DECISION, false)) {
                                Toast.makeText(context,  R.string.cra, Toast.LENGTH_SHORT).show();
                            }
                        } else if (error.networkResponse != null) {
                            if (error.networkResponse.statusCode == 408)
                                Toast.makeText(context,  R.string.cra, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context,  R.string.cra, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                if (Utils.isNetworkAvailable(context))
                transactionPairRequest.start();
            }
        }
    }
}
