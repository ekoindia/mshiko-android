package in.co.eko.fundu.database.tables;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;

import in.co.eko.fundu.FunduApplication;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.database.DatabaseHelper;
import in.co.eko.fundu.models.ContactItem;
import in.co.eko.fundu.utils.ContactsUtils;
import in.co.eko.fundu.utils.Fog;
import in.co.eko.fundu.utils.Utils;

//import static in.co.eko.fundu.FunduApplication.context;


/**
 * Created by divyanshu.jain on 8/1/2016.
 */
public class UserAllContactsTable {

    public static final String TABLE_USER_CONTACTS = "user_all_contacts";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_NUMBER = "number";
    public static final String COLUMN_COUNTRY_CODE = "country_code";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_IS_ADDED_IN_NETWORK = "isAddedInNetwork";
    public static final String COLUMN_SUB_TITLE = "subTitle";
    public static final String COLUMN_VERSION = "version";
    public static final String COLUMN_IS_UNREGISTERD = "is_unregistered";
    public static final String COLUMN_DELETED = "deleted";
    private static final String POST_ARGS = " = ?";

    private static final String CREATE_USER_CONTACTS_TABLE = "create table "
            + TABLE_USER_CONTACTS + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_NAME + " text , "
            + COLUMN_NUMBER + " text , "
            + COLUMN_COUNTRY_CODE + " text , "
            + COLUMN_IMAGE + " text ,"
            + COLUMN_IS_ADDED_IN_NETWORK + " boolean, "
            + COLUMN_SUB_TITLE + " text, "
            + COLUMN_VERSION + " text, "
            + COLUMN_IS_UNREGISTERD + " integer, "// 0 for registered and 1 for unregistered
            + COLUMN_DELETED + " integer "// 0 for un deleted and 1 for deleted
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_USER_CONTACTS_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {

        switch(oldVersion){
            case 9:
                database.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_CONTACTS);
                onCreate(database);
                break;
        }
        /*FFLog.d("versions","versions"+oldVersion+newVersion);
        if (oldVersion < 10) {
           try{
               Fog.d("execSQL","execSQL");
               database.execSQL("ALTER TABLE " + TABLE_USER_CONTACTS + " ADD COLUMN "+ COLUMN_COUNTRY_CODE +" TEXT ");

              *//* FunduApplication.context.startService(new Intent(FunduApplication.context,UpdateDatabaseForExisitingUserService.class)
                       .putExtra(Constants.CLASSNAME,Constants.USERALLCONTACTTABLE));*//*
                Constants.CLASSNAME = Constants.USERALLCONTACTTABLE;
               FunduApplication.context.startService(new Intent(FunduApplication.context,UpdateDatabaseForExisitingUserService.class)
                      );

           }
           catch (Exception e){
             Fog.d("Exception","Exception"+e);
           }

        }
*/
    }



    public static void insertContactToDB(Context context, ContactItem contactItem) {
        DatabaseHelper helper = DatabaseHelper.getInstance(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = getContentValues(contactItem);
        db.insert(TABLE_USER_CONTACTS, null, contentValues);
    }


    public static int getContactsCount(Context context) {
        int count = 0;
        DatabaseHelper helper = DatabaseHelper.getInstance(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USER_CONTACTS, null, null, null, null, null, null);
        count = cursor.getCount();
        return count;
    }

    public static ArrayList<ContactItem> getContacts(Context context) {
        ArrayList<ContactItem> contactItems = new ArrayList<>();
        DatabaseHelper helper = DatabaseHelper.getInstance(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USER_CONTACTS, null, null, null, null, null, COLUMN_NAME + " ASC");
        if (cursor != null && cursor.getColumnCount() != 0) {
            cursor.move(0);
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                String number = cursor.getString(cursor.getColumnIndex(COLUMN_NUMBER));
                String image = cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE));
                String subTitle = cursor.getString(cursor.getColumnIndex(COLUMN_SUB_TITLE));
                String version = cursor.getString(cursor.getColumnIndex(COLUMN_VERSION));
                String country_code = cursor.getString(cursor.getColumnIndex(COLUMN_COUNTRY_CODE));
                int isUnregistered = cursor.getInt(cursor.getColumnIndex(COLUMN_IS_UNREGISTERD));
                int isDeleted = cursor.getInt(cursor.getColumnIndex(COLUMN_DELETED));

                ContactItem contactItem = new ContactItem(name, number, image,country_code);
                contactItem.setSubTitle(subTitle);
                contactItem.setVersion(version);
                contactItem.setIsUnregisterd(isUnregistered);
                contactItem.setDeleted(isDeleted);

                contactItems.add(contactItem);
            }
        }
        //  db.close();
        return contactItems;
    }


    public static void deleteContact(Context context, ContactItem contactItem) {
        DatabaseHelper helper = DatabaseHelper.getInstance(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        db.delete(TABLE_USER_CONTACTS, COLUMN_NUMBER + POST_ARGS, new String[]{contactItem.getContactNumber()});
        db.close();
    }

    public static void deleteAllContact(Context context) {
        DatabaseHelper helper = DatabaseHelper.getInstance(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(TABLE_USER_CONTACTS, null, null);
//        db.close();
    }

    public static void updateContact(Context context, int deleted, String number) {
        DatabaseHelper helper = DatabaseHelper.getInstance(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        String[] whereArgs = new String[]{number};
        db.update(TABLE_USER_CONTACTS, getContentValues(deleted), COLUMN_NUMBER + POST_ARGS, whereArgs);
        //db.close();
    }

    private static ContentValues getContentValues(int deleted) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_DELETED, deleted);
        return contentValues;
    }

    @NonNull
    private static ContentValues getContentValues(ContactItem contactItem) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, contactItem.getContactName());
        contentValues.put(COLUMN_NUMBER, contactItem.getContactNumber());
        contentValues.put(COLUMN_IMAGE, contactItem.getContactImage());
        contentValues.put(COLUMN_SUB_TITLE, contactItem.getSubTitle());
        contentValues.put(COLUMN_IS_ADDED_IN_NETWORK, contactItem.isAddedInNetwork());
        contentValues.put(COLUMN_VERSION, contactItem.getVersion());
        contentValues.put(COLUMN_IS_UNREGISTERD, contactItem.isUnregisterd());
        contentValues.put(COLUMN_DELETED, contactItem.isDeleted());
        return contentValues;
    }

    public static void insertContactItemsIntoDB(Context context, ArrayList<ContactItem> contactItems) {


        String sql = "INSERT OR REPLACE INTO " + TABLE_USER_CONTACTS + " (" + COLUMN_NAME + "," + COLUMN_NUMBER +  "," + COLUMN_COUNTRY_CODE +"," +COLUMN_IMAGE + "," + COLUMN_SUB_TITLE + "," + COLUMN_IS_ADDED_IN_NETWORK + "," + COLUMN_VERSION + "," + COLUMN_IS_UNREGISTERD + "," + COLUMN_DELETED  + "  ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ? )";
        DatabaseHelper helper = DatabaseHelper.getInstance(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransactionNonExclusive();
        SQLiteStatement stmt = db.compileStatement(sql);
        for (ContactItem contactItem : contactItems) {

            getStatement(stmt, contactItem);
            stmt.execute();
            stmt.clearBindings();
//            Fog.e("UserAlConTableIN",contactItem.getContactNumber()+" , reg======>"+contactItem.getIsUnregisterd());
        }
        db.setTransactionSuccessful();
        db.endTransaction();
//        db.close();
    }

    private static void getStatement(SQLiteStatement stmt, ContactItem contactItem) {
        /*stmt.bindString(1, (contactItem.getContactName() == null ? "" : contactItem.getContactName()));
        stmt.bindString(2, (contactItem.getContactNumber() == null ? "" : contactItem.getContactNumber()));
        stmt.bindString(3, (contactItem.getContactImage() == null ? "" : contactItem.getContactImage()));
        stmt.bindString(4, (contactItem.getSubTitle() == null ? "" : contactItem.getSubTitle()));
        stmt.bindString(5, "");
        stmt.bindString(6, (contactItem.getVersion() == null ? "" : contactItem.getVersion()));
        stmt.bindDouble(7, (contactItem.isUnregisterd() == -1 ? 1 : contactItem.isUnregisterd()));
        stmt.bindDouble(8, (contactItem.isDeleted() == -1 ? 1 : contactItem.isDeleted()));*/





        stmt.bindString(1, (contactItem.getContactName() == null ? "" : contactItem.getContactName()));
        stmt.bindString(2, (contactItem.getContactNumber() == null ? "" : contactItem.getContactNumber()));
        stmt.bindString(3, (contactItem.getContactCountryCode() == null ? "" : contactItem.getContactCountryCode()));
        stmt.bindString(4, (contactItem.getContactImage() == null ? "" : contactItem.getContactImage()));
        stmt.bindString(5, (contactItem.getSubTitle() == null ? "" : contactItem.getSubTitle()));
        stmt.bindString(6, "");
        stmt.bindString(7, (contactItem.getVersion() == null ? "" : contactItem.getVersion()));
        stmt.bindDouble(8, (contactItem.isUnregisterd() == -1 ? 1 : contactItem.isUnregisterd()));
        stmt.bindDouble(9, (contactItem.isDeleted() == -1 ? 1 : contactItem.isDeleted()));

    }

    public static ArrayList<String> getContactNumbers(Context context) {
        ArrayList<String> contactNumber = new ArrayList<>();
        DatabaseHelper helper = DatabaseHelper.getInstance(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        String where = COLUMN_DELETED + POST_ARGS;
        String[] whereArgs = new String[]{String.valueOf(0)};
        Cursor cursor = db.query(TABLE_USER_CONTACTS, new String[]{COLUMN_NUMBER}, where, whereArgs, null, null, null);
        if (cursor != null && cursor.getColumnCount() != 0) {
            cursor.move(0);
            while (cursor.moveToNext()) {
                String number = cursor.getString(0);
                contactNumber.add(number);
            }
        }
        //  db.close();
        return contactNumber;
    }



    public static void addCountryCodeData() {

        ArrayList<ContactItem> contactItems = ContactsUtils.getInstance(FunduApplication.getAppContext()).getContactsFromPhoneBook();
        DatabaseHelper helper = DatabaseHelper.getInstance(FunduApplication.getAppContext());


        for (ContactItem contactitem : contactItems) {
            String number = contactitem.getContactNumber();

            String pFormattedNumber = Utils.formatNumber(FunduApplication.getAppContext(), number);
            HashMap<String, String> formatted_ph_num_map = Utils.phoneNumberParser(FunduApplication.getAppContext(), number);
            /*Fog.d("formatted_ph_num", "formatted_ph_num" + pFormattedNumber);
            Fog.d("formatted_ph_num", "formatted_ph_num_map" + formatted_ph_num_map.get(Constants.PHONENUMBER));
            Fog.d("formatted_ph_num", "formatted_ph_num_map" + formatted_ph_num_map.get(Constants.COUNTRYCODE));*/
            //getEntryFromDb(formatted_ph_num,formatted_ph_num_map);
            updateContactNumber(FunduApplication.getAppContext(),pFormattedNumber,formatted_ph_num_map);


        }


    }

    private static void getEntryFromDb(String formatted_ph_num, HashMap<String, String> formatted_map) {

        DatabaseHelper helper = DatabaseHelper.getInstance(FunduApplication.getAppContext());
        SQLiteDatabase db = helper.getReadableDatabase();
        String where = COLUMN_NUMBER + POST_ARGS;
        String[] whereArgs = new String[]{String.valueOf(formatted_ph_num)};
        Cursor cursor = db.query(TABLE_USER_CONTACTS, new String[]{COLUMN_ID}, where, whereArgs, null, null, null);
        if (cursor != null && cursor.getColumnCount() != 0) {
            cursor.move(0);
            while (cursor.moveToNext()) {
                String id = cursor.getString(0);
                Fog.d("id","id"+id);
                updateContactNumber(FunduApplication.getAppContext(),id,formatted_map);
            }
        }
        //  db.close();

    }

    public static void  updateContactNumber(Context context, String number, HashMap<String, String> formatted_map) {

        ArrayList<String> contactNumber = new ArrayList<>();
        DatabaseHelper helper = DatabaseHelper.getInstance(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_COUNTRY_CODE, formatted_map.get(Constants.COUNTRYCODE));
        values.put(COLUMN_NUMBER, formatted_map.get(Constants.PHONENUMBER));
       // int rowsUpdated  = db.update(TABLE_USER_CONTACTS, values, COLUMN_NUMBER + POST_ARGS , new String[]{number});
        if(number.equalsIgnoreCase("11-257-58393")){
            Fog.i("","This number is not getting updated");
        }
        int rowsUpdated  = db.update(TABLE_USER_CONTACTS, values, COLUMN_NUMBER + POST_ARGS , new String[]{number});
        if(rowsUpdated == 0){
            Fog.i("","This number is not getting updated");
        }
        Fog.i("","");


    }



}
