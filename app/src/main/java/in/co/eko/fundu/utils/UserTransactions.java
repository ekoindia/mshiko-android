package in.co.eko.fundu.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.requests.FindTransactionPairRequest;
import in.co.eko.fundu.requests.LoadWalletRequest;
import in.co.eko.fundu.requests.TransactionInitiateRequest;

/**
 * Created by divyanshu.jain on 7/11/2016.
 */
public class UserTransactions {

    private UserTransactions() {

    }

    private static UserTransactions userTransactions = null;
    private float neededAmount = 0;
    private Context context;
    private LoadWalletRequest walletRequest;
    private AppPreferences pref;
    private ProgressDialog dialog;
    private Activity activity;


    public static UserTransactions getInstance() {
        if (userTransactions == null)
            userTransactions = new UserTransactions();
        return userTransactions;
    }

    public void initiateTransactions(Activity activity, String alias, String sender_id, String sender_id_type, String recipient_id, String recipient_id_type, int amount, int hold_timeout,String recipentId,String provider_charge, String fee,String pairRequestId) {
        this.activity = activity;
        TransactionInitiateRequest initiateRequest = new TransactionInitiateRequest(activity);
        initiateRequest.setData(alias, sender_id, sender_id_type, recipentId, recipient_id_type, amount, hold_timeout,recipient_id,provider_charge,fee,pairRequestId);
        initiateRequest.setParserCallback((TransactionInitiateRequest.OnTransactionInitiateResults) activity);
        initiateRequest.start();
    }

    public void initTransactionForSendMoneyToAccount(Activity activity, String sender_id, String sender_id_type, String recipient_id, String recipient_id_type, int amount, int hold_timeout, int recipient_number) {
        this.activity = activity;
        TransactionInitiateRequest initiateRequest = new TransactionInitiateRequest(activity);
        initiateRequest.setDataForSendMoneyToAccount(sender_id, sender_id_type, recipient_id, recipient_id_type, amount, hold_timeout, recipient_number);
        initiateRequest.setParserCallback((TransactionInitiateRequest.OnTransactionInitiateResults) activity);
        initiateRequest.start();
    }

    public void findPair(Activity activity, String alias, String sender_id, String sender_id_type,
                         String recipient_id, String recipient_id_type, int amount, int hold_timeout, boolean fromMap,
                         String fee, LatLng location) {
        this.activity = activity;
        FindTransactionPairRequest initiateRequest = new FindTransactionPairRequest(activity, FunduUser.getAppPreferences(), amount);
        initiateRequest.setData(alias, sender_id, sender_id_type, recipient_id, recipient_id_type, amount, hold_timeout, fromMap, fee,location);
        initiateRequest.setParserCallback((FindTransactionPairRequest.OnFindTransactionPairResults) activity);
        initiateRequest.start();
    }
}
