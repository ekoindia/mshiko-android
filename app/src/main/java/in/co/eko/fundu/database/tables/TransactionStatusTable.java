package in.co.eko.fundu.database.tables;/*
 * Created by Bhuvnesh
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import in.co.eko.fundu.database.DatabaseHelper;
import in.co.eko.fundu.models.TransactionPair;

public final class TransactionStatusTable {
    // Database table
    public static final String TABLE_TRANSACTION_STATUS = "transaction_status";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_REQUEST_ID = "requestId";
    public static final String COLUMN_PAIR_CONTACT_ID = "pairContactId";
    public static final String COLUMN_REQUESTER_ID = "requestorId";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_FEE = "fee";
    public static final String COLUMN_TRANSACTION_TYPE = "transactionType";
    public static final String COLUMN_RATING = "rating";

    // Database creation SQL statement
    private static final String CREATE_TRANSACTION_STATUS_TABLE = "create table "
            + TABLE_TRANSACTION_STATUS + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_STATUS + " text , "
            + COLUMN_REQUEST_ID + " text , "
            + COLUMN_PAIR_CONTACT_ID + " text ,"
            + COLUMN_REQUESTER_ID + " text, "
            + COLUMN_AMOUNT + " text, "
            + COLUMN_FEE + " text, "
            + COLUMN_TRANSACTION_TYPE + " text, "
            + COLUMN_RATING + " text "
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TRANSACTION_STATUS_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTION_STATUS);
         onCreate(database);
    }


    public static void deleteTransactionPairByRequesterId(Context context, String requesterId) {
        DatabaseHelper helper = DatabaseHelper.getInstance(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        db.delete(TABLE_TRANSACTION_STATUS, COLUMN_REQUESTER_ID + " = ?", new String[]{requesterId});
        //db.close();
    }

    public static List<TransactionPair> getTransactionsByStatus(Context context, String status) {
        DatabaseHelper helper = DatabaseHelper.getInstance(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        List<TransactionPair> transactionPairs = new ArrayList<TransactionPair>();
        Cursor cursor = db.query(
                TABLE_TRANSACTION_STATUS,  // The table to query
                null,                               // The columns to return
                COLUMN_STATUS + " =?",                                // The columns for the WHERE clause
                new String[]{status},                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        if (cursor.moveToFirst()) {
            do {
                TransactionPair contact = new TransactionPair();
                contact.setPairContactId(cursor.getString(cursor.getColumnIndex(COLUMN_PAIR_CONTACT_ID)));
                contact.setRequesterId(cursor.getString(cursor.getColumnIndex(COLUMN_REQUESTER_ID)));
                contact.setRequestId(cursor.getString(cursor.getColumnIndex(COLUMN_REQUEST_ID)));
                contact.setRequest_type(cursor.getString(cursor.getColumnIndex(COLUMN_TRANSACTION_TYPE)));
                // Adding contact to list
                transactionPairs.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();
        //db.close();
        return transactionPairs;
    }

    public static int getTransactionsCountByStatus(Context context, String status) {
        DatabaseHelper helper = DatabaseHelper.getInstance(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        List<TransactionPair> transactionPairs = new ArrayList<TransactionPair>();
        Cursor cursor = db.query(
                TABLE_TRANSACTION_STATUS,  // The table to query
                null,                               // The columns to return
                COLUMN_STATUS + " =?",                                // The columns for the WHERE clause
                new String[]{status},                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        int i = cursor.getCount();
//        cursor.close();
     //   db.close();
        return i;
    }

    public static boolean addTransaction(Context context, List<String> alerts) {
        DatabaseHelper helper = DatabaseHelper.getInstance(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        //Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, "false");
        values.put(COLUMN_TRANSACTION_TYPE, alerts.get(0));
        values.put(COLUMN_REQUEST_ID, alerts.get(1));
        values.put(COLUMN_PAIR_CONTACT_ID, alerts.get(2));
        values.put(COLUMN_REQUESTER_ID, alerts.get(3));
        //Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(
                TABLE_TRANSACTION_STATUS,
                null,
                values);
       // db.close();
        return true;
    }

    public static void deleteTransactionPairs(Context context) {
        DatabaseHelper helper = DatabaseHelper.getInstance(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        db.delete(TABLE_TRANSACTION_STATUS, null, null);

    }
}
