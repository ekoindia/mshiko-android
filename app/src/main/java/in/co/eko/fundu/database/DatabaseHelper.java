package in.co.eko.fundu.database;
/*
 * Created by Bhuvnesh
 */

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import in.co.eko.fundu.database.tables.TransactionStatusTable;
import in.co.eko.fundu.database.tables.UserAllContactsTable;
import in.co.eko.fundu.database.tables.UserContactsTable;
import in.co.eko.fundu.utils.Fog;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "fundu.db";
    private static final int DATABASE_VERSION = 11;
    private static DatabaseHelper sInstance;
    private UserContactsTable uctInstance;

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        uctInstance = new UserContactsTable(context);
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());

        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        TransactionStatusTable.onCreate(db);
        UserContactsTable.onCreate(db);
        UserAllContactsTable.onCreate(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Fog.d("database version","database version"+oldVersion+newVersion);
        TransactionStatusTable.onUpgrade(db, oldVersion, newVersion);
        UserContactsTable.onUpgrade(db, oldVersion, newVersion);
        UserAllContactsTable.onUpgrade(db, oldVersion, newVersion);


    }

    public ArrayList<Cursor> getData(String Query) {
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[]{"mesage"};
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2  = new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);


        try {
            String maxQuery = Query;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);


            //add value to cursor2
            Cursor2.addRow(new Object[]{"Success"});

            alc.set(1, Cursor2);
            if (null != c && c.getCount() > 0) {


                alc.set(0, c);
                c.moveToFirst();

                return alc;
            }
            return alc;
        } catch (SQLException sqlEx) {
            Fog.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[]{"" + sqlEx.getMessage()});
            alc.set(1, Cursor2);
            return alc;
        } catch (Exception ex) {

            Fog.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[]{"" + ex.getMessage()});
            alc.set(1, Cursor2);
            return alc;
        }
    }

    public UserContactsTable getUctInstance() {
        return uctInstance;
    }
}
