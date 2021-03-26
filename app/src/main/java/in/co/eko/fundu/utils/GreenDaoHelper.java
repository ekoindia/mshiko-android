package in.co.eko.fundu.utils;

import android.content.Context;

import org.greenrobot.greendao.database.Database;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.co.eko.fundu.R;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.database.greendao.DaoMaster;
import in.co.eko.fundu.database.greendao.DaoSession;
import in.co.eko.fundu.database.greendao.FunduTransaction;
import in.co.eko.fundu.database.greendao.FunduTransactionDao;

import static in.co.eko.fundu.constants.Constants.dbEncrypted;


/**
 * Created by pallavi on 28/12/17.
 */

public class GreenDaoHelper {

    private static GreenDaoHelper greenDaoHelper;
    private FunduTransactionDao transactionDoa;
    private DaoSession daoSession;
    private String TAG = GreenDaoHelper.class.getName();

    private GreenDaoHelper(Context context){
        initDao(context);
    }

    public static GreenDaoHelper getInstance(Context context){
        if(greenDaoHelper == null){
            greenDaoHelper = new GreenDaoHelper(context);
        }
        return greenDaoHelper;
    }

    private void initDao(Context context) {
        // get the note DAO
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context,"fundu.db.greendao");
        String key = context.getString(R.string.title_transaction_type);
        Database db = dbEncrypted ? helper.getEncryptedWritableDb(key) : helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
        transactionDoa = daoSession.getFunduTransactionDao();
    }

    public FunduTransaction getPendingTransaction(){
        List<FunduTransaction> funduTransactions = transactionDoa.queryBuilder().build().list();
        if(funduTransactions != null && funduTransactions.size() > 0){
            return funduTransactions.get(0);
        }
        return null;
    }

    public FunduTransaction getTransaction(long key){
        return transactionDoa.load(key);
    }

    public  FunduTransaction addTransaction(FunduTransaction transaction){
        long id = transactionDoa.insertOrReplace(transaction);
        transaction.setId(id);
        return transaction;

    }

    public void addTransaction(String tid,String amount,String fee, String providerCharge){

    }

    public FunduTransaction populateTransaction(ArrayList<String> alertArray, JSONObject jData){
        FunduTransaction transaction = new FunduTransaction();
        try{

            transaction.setStatus(jData.optString(Constants.PushNotificationKeys.ACTION));
            transaction.setAmount(jData.optString(Constants.PushNotificationKeys.AMOUNT));
            transaction.setPhoneNumber(jData.getString(Constants.PushNotificationKeys.PHONENUMBER));
            transaction.setName(jData.getString(Constants.PushNotificationKeys.NAME));
            transaction.setImage(jData.optString(Constants.PushNotificationKeys.IMAGEURL));
            transaction.setRating(jData.optString(Constants.PushNotificationKeys.RATING));
            transaction.setPairRequestId(jData.optString(Constants.PushNotificationKeys.PAIR_REQUEST_ID));
            transaction.setFee(jData.getString(Constants.PushNotificationKeys.FEE));
            transaction.setProviderCharge(jData.optString(Constants.PushNotificationKeys.PROVIDER_CHARGE));
            transaction.setProvider(jData.optString(Constants.PushNotificationKeys.PROVIDER));
            transaction.setSeeker(jData.optString(Constants.PushNotificationKeys.SEEKER));
            transaction.setCustid(jData.optString(Constants.PushNotificationKeys.CUST_ID));

            if (FunduUser.getCountryShortName().equalsIgnoreCase("IND")) {
                transaction.setTid(jData.optString(Constants.PushNotificationKeys.TID));

            } else {
                transaction.setTid("0");
            }
            JSONArray location = jData.optJSONArray(Constants.PushNotificationKeys.LOCATION);
            if(location != null && location.length() == 2){
                transaction.setLatitude(location.getDouble(1));
                transaction.setLongitude(location.getDouble(0));
            }
            JSONArray requestLocation = jData.optJSONArray(Constants.PushNotificationKeys.REQUEST_LOCATION);
            if(requestLocation != null && requestLocation.length() == 2){
                transaction.setRequestLatitude(requestLocation.getDouble(1));
                transaction.setRequestLongitude(requestLocation.getDouble(0));
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return transaction;
    }

    public FunduTransaction addTransaction(ArrayList<String> alertArray, JSONObject jData) {

        FunduTransaction transaction = populateTransaction(alertArray,jData);
       return addTransaction(transaction);

    }

    public void updateTransactionState(long id,int state){
        FunduTransaction transaction = transactionDoa.load(id);
        transaction.setState(state);
        transactionDoa.update(transaction);
    }
    public FunduTransaction updateTransactionState(String tid,String requestId,int state){
        List<FunduTransaction> list = transactionDoa.queryBuilder()
                .whereOr(FunduTransactionDao.Properties.Tid.eq(tid),FunduTransactionDao.Properties.PairRequestId.eq(requestId)).build().list();
        if(list != null && list.size() >0){
            FunduTransaction funduTransaction = list.get(0);
            funduTransaction.setState(state);
            transactionDoa.update(funduTransaction);
            return funduTransaction;
        }
        return null;
    }

    public void updateTransaction(FunduTransaction transaction){
        transactionDoa.update(transaction);
    }

    public void updateTransactionStatus(long id, String status) {
        FunduTransaction transaction = transactionDoa.load(id);
        transaction.setStatus(status);
        transactionDoa.update(transaction);
    }

    /**
     *
     * @param pairRequestId
     * @param tid
     */

    public  void updateTransactionTid(String pairRequestId,String tid){
        Fog.i(TAG,"updateTransactionTid pairRequestId:"+pairRequestId+" tid"+tid);
       List<FunduTransaction> list = transactionDoa.queryBuilder().where(FunduTransactionDao.Properties.PairRequestId.eq(pairRequestId)).build().list();
        if(list != null && list.size() >0){
            FunduTransaction funduTransaction = list.get(0);
            funduTransaction.setTid(tid);
            transactionDoa.update(funduTransaction);
        }

    }
    public  void updateCodeGreenDao(long id,String code){

        FunduTransaction transaction = transactionDoa.load(id);
        transaction.setCode(code);
        transactionDoa.update(transaction);

    }
    public  void deleteFunduTransaction(long id){
        transactionDoa.deleteByKey(id);
    }

    public  void deleteFunduTransaction(String prid) {

        List<FunduTransaction> list = transactionDoa.queryBuilder()
                .whereOr(FunduTransactionDao.Properties.PairRequestId.eq(prid),FunduTransactionDao.Properties.PairRequestId.eq(prid))
                .build().list();
        if (list != null && list.size() > 0) {
            transactionDoa.delete(list.get(0));
        }
    }
    public void clearDb(){
        transactionDoa.deleteAll();
    }

}