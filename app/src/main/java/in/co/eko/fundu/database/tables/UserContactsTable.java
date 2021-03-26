package in.co.eko.fundu.database.tables;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import in.co.eko.fundu.FunduApplication;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.database.DatabaseHelper;
import in.co.eko.fundu.event.DataUpdated;
import in.co.eko.fundu.models.ContactItem;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.utils.AppPreferences;
import in.co.eko.fundu.utils.ContactsUtils;
import in.co.eko.fundu.utils.Fog;
import in.co.eko.fundu.utils.Utils;

/**
 * Created by divyanshu.jain on 7/5/2016.
 */
public final class UserContactsTable {

    static String TAG = "UserContactsTable";
    //private Context context;


    public static final String TABLE_USER_CONTACTS = "user_contacts";
    public static final String TABLE_USER_ALL_CONTACTS = "user_all_contacts";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_NUMBER = "number";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_COUNTRY_CODE = "country_code";
    public static final String COLUMN_IS_ADDED_IN_NETWORK = "isAddedInNetwork";
    public static final String COLUMN_SUB_TITLE = "subTitle";
    public static final String COLUMN_VERSION = "version";
    public static final String COLUMN_IS_UNREGISTERD = "is_unregistered";
    private static final String POST_ARGS = " =?";
    /*primary key autoincrement*/
    private static final String CREATE_USER_CONTACTS_TABLE = "create table "
            + TABLE_USER_CONTACTS + "("
            + COLUMN_ID + " integer , "
            + COLUMN_NAME + " text , "
            + COLUMN_NUMBER + " text primary key, "
            + COLUMN_COUNTRY_CODE + " text , "
            + COLUMN_IMAGE + " text ,"
            + COLUMN_IS_ADDED_IN_NETWORK + " boolean, "
            + COLUMN_SUB_TITLE + " text, "
            + COLUMN_VERSION + " text, "
            + COLUMN_IS_UNREGISTERD + " integer "// 0 for registered and 1 for unregistered
            + ");";

    public UserContactsTable(Context context){

        //this.context = context;
    }

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_USER_CONTACTS_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

        switch(oldVersion){
            case 9:
            case 10:
                database.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_CONTACTS);
                onCreate(database);
                break;
        }

       /* if (oldVersion < 10) {

            try{
                database.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_CONTACTS);
                onCreate(database);
                *//*Fog.d("execSQL","execSQL");
                database.execSQL("ALTER TABLE " + TABLE_USER_CONTACTS + " ADD COLUMN " +COLUMN_COUNTRY_CODE+ " TEXT ");
                Fog.d("execSQL","execSQL");

                Constants.CLASSNAME = Constants.USERCONTACTTABLE;
                FunduApplication.context.startService(new Intent(FunduApplication.context,
                        UpdateDatabaseForExisitingUserService.class));
*//*

            }
            catch (Exception e){
                Fog.d("Exception","Exception"+e);
            }


        }*/



    }

    public static void addCountryCodeData() {

        ArrayList<ContactItem> contactItems = ContactsUtils.getInstance(FunduApplication.getAppContext()).getContactsFromPhoneBook();
        DatabaseHelper helper = DatabaseHelper.getInstance(FunduApplication.getAppContext());


        for (ContactItem contactitem : contactItems) {
            String number = contactitem.getContactNumber();
            if(number == null)
                continue;
            String formatted_ph_num = Utils.formatNumber(FunduApplication.getAppContext(), number);
            HashMap<String, String> formatted_ph_num_map = Utils.phoneNumberParser(FunduApplication.getAppContext(), number);
           /* Fog.d("formatted_ph_num", "formatted_ph_num" + formatted_ph_num);
            Fog.d("formatted_ph_num", "formatted_ph_num_map" + formatted_ph_num_map.get(Constants.PHONENUMBER));
            Fog.d("formatted_ph_num", "formatted_ph_num_map" + formatted_ph_num_map.get(Constants.COUNTRYCODE));*/
            getEntryFromDb(formatted_ph_num,formatted_ph_num_map);
           // updateContactNumber(context,formatted_ph_num,formatted_ph_num_map);


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

    public static void insertContactToDB(Context context, ContactItem contactItem) {
        Fog.d("UserConTable", "insertContactToDB");
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
        Fog.d("UserConTable", "getContacts");
        ArrayList<ContactItem> contactItems = new ArrayList<>();
        DatabaseHelper helper = DatabaseHelper.getInstance(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER_CONTACTS, null, null, null, null, null, null);

        // Cursor cursor = db.query(TABLE_USER_ALL_CONTACTS, null, null, null, null, null, null);

        if (cursor != null && cursor.getColumnCount() != 0) {
            cursor.move(0);
            while (cursor.moveToNext()) {

                // int _id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                String number = cursor.getString(cursor.getColumnIndex(COLUMN_NUMBER));
                String image = cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE));
                String subTitle = cursor.getString(cursor.getColumnIndex(COLUMN_SUB_TITLE));
                String version = cursor.getString(cursor.getColumnIndex(COLUMN_VERSION));
                String country_code = cursor.getString(cursor.getColumnIndex(COLUMN_COUNTRY_CODE));
                int isUnregistered = cursor.getInt(cursor.getColumnIndex(COLUMN_IS_UNREGISTERD));

                ContactItem contactItem = new ContactItem(name, number, image, country_code);
                //  contactItem.set_id(_id);
                contactItem.setSubTitle(subTitle);
                contactItem.setVersion(version);
                contactItem.setIsUnregisterd(isUnregistered);
                contactItems.add(contactItem);
            }
        }
        //   db.close();
        return contactItems;
    }

    public static int getContactsCount(Context context,int isUnRegistered){
        Fog.d("UserConTable", "getRegisteredContacts");
        ArrayList<ContactItem> contactItems = new ArrayList<>();
        DatabaseHelper helper = DatabaseHelper.getInstance(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        AppPreferences appPreferences = FunduUser.getAppPreferences();
        String countryCode = FunduUser.getCountryMobileCode();
        //  Cursor cursor = db.query(TABLE_USER_CONTACTS, null, COLUMN_IS_UNREGISTERD + POST_ARGS,
        // new String[]{String.valueOf(isUnRegistered)}, null, null, null);

        /*created by pallavi. This cursor query is wriiten to see if User All Contact table have same data as User contact*/

        Cursor cursor = db.query(TABLE_USER_CONTACTS,
                null,
                COLUMN_IS_UNREGISTERD + POST_ARGS + " AND " + COLUMN_COUNTRY_CODE + POST_ARGS,
                new String[]{String.valueOf(isUnRegistered), countryCode}, null, null, null);

        if (cursor != null && cursor.getColumnCount() != 0) {
            return cursor.getCount();
        }
        // db.close();
        return 0;
    }

    /**
     * @param context
     * @param isUnRegistered 0 for registered, 1 for unregistered
     * @return List of contacts depending on isUnRegistered value
     */

    public static ArrayList<ContactItem> getContacts(Context context, int isUnRegistered) {
        Fog.d("UserConTable", "getRegisteredContacts");
        ArrayList<ContactItem> contactItems = new ArrayList<>();
        DatabaseHelper helper = DatabaseHelper.getInstance(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        AppPreferences appPreferences = FunduUser.getAppPreferences();
        String country_code_app = appPreferences.getString("country_code_r");

        //  Cursor cursor = db.query(TABLE_USER_CONTACTS, null, COLUMN_IS_UNREGISTERD + POST_ARGS,
        // new String[]{String.valueOf(isUnRegistered)}, null, null, null);

        /*created by pallavi. This cursor query is wriiten to see if User All Contact table have same data as User contact*/

        Cursor cursor = db.query(TABLE_USER_CONTACTS,
                null,
                COLUMN_IS_UNREGISTERD + POST_ARGS + " AND " + COLUMN_COUNTRY_CODE + POST_ARGS,
                new String[]{String.valueOf(isUnRegistered), country_code_app}, null, null, null);

        //  Cursor cursor = db.query(TABLE_USER_ALL_CONTACTS, null, COLUMN_IS_UNREGISTERD + POST_ARGS, new String[]{String.valueOf(isUnRegistered)}, null, null, null);

        if (cursor != null && cursor.getColumnCount() != 0) {
            cursor.move(0);
            while (cursor.moveToNext()) {

                // int _id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                String number = cursor.getString(cursor.getColumnIndex(COLUMN_NUMBER));
                String image = cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE));
                String subTitle = cursor.getString(cursor.getColumnIndex(COLUMN_SUB_TITLE));
                String version = cursor.getString(cursor.getColumnIndex(COLUMN_VERSION));
                String country_code = cursor.getString(cursor.getColumnIndex(COLUMN_COUNTRY_CODE));
                int isUnregistered = cursor.getInt(cursor.getColumnIndex(COLUMN_IS_UNREGISTERD));
                ContactItem contactItem = new ContactItem(name, number, image, country_code);
                //  contactItem.set_id(_id);
                contactItem.setSubTitle(subTitle);
                contactItem.setVersion(version);
                contactItem.setIsUnregisterd(isUnregistered);
                contactItem.setIsAddedInNetwork(cursor.getInt(cursor.getColumnIndex(COLUMN_IS_ADDED_IN_NETWORK)) == 1);
                contactItems.add(contactItem);

            }
        }
        // db.close();
        return contactItems;
    }

    public static ArrayList<String> getRegisteredNumbers(Context context) {

        Fog.d("UserConTable", "getRegisteredNumbers");
        ArrayList<String> numbers = new ArrayList<>();
        DatabaseHelper helper = DatabaseHelper.getInstance(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER_CONTACTS, null, COLUMN_IS_UNREGISTERD + POST_ARGS, new String[]{"0"}, null, null, null);

        /*created by pallavi. This cursor query is wriiten to see if User All Contact table have same data as User contact*/
        // Cursor cursor = db.query(TABLE_USER_ALL_CONTACTS, null,
        // COLUMN_IS_UNREGISTERD + POST_ARGS, new String[]{"0"}, null, null, null);

        if (cursor != null && cursor.getColumnCount() != 0) {
            cursor.move(0);
            while (cursor.moveToNext()) {
                String number = cursor.getString(cursor.getColumnIndex(COLUMN_NUMBER));
                number = Utils.checkAndRemoveCountryCode(context, number);
                numbers.add(number);
            }
        }
        // db.close();
        return numbers;
    }


    /*crested by pallavi to see if it gets separe codes and phone number for each entry in database*/
    public static ArrayList<String> getRegisteredNumbersNew(Context context)

    {
        Fog.d("UserConTable", "getRegisteredNumbers");
        ArrayList<String> numbers = new ArrayList<>();
        DatabaseHelper helper = DatabaseHelper.getInstance(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USER_CONTACTS, null, COLUMN_IS_UNREGISTERD + POST_ARGS, new String[]{"0"}, null, null, null);

        /*created by pallavi. This cursor query is wriiten to see if User All Contact table have same data as User contact*/

        // Cursor cursor = db.query(TABLE_USER_ALL_CONTACTS, null, COLUMN_IS_UNREGISTERD + POST_ARGS, new String[]{"0"}, null, null, null);

        if (cursor != null && cursor.getColumnCount() != 0) {
            cursor.move(0);
            while (cursor.moveToNext()) {
                String number = cursor.getString(cursor.getColumnIndex(COLUMN_NUMBER));

                //   Fog.d("final_string","contactTablenumber"+number);

               /*created by palavi*/
                HashMap<String, String> seprated_codes_numbers = Utils.phoneNumberParser(context, number);

             /*   Fog.d("final_string","contactTable"+ seprated_codes_numbers.get(Constants.COUNTRYCODE));
                Fog.d("final_string","contactTable"+ seprated_codes_numbers.get(Constants.PHONENUMBER));
*/

                number = seprated_codes_numbers.get(Constants.PHONENUMBER);
                numbers.add(number);


            }
        }
        // db.close();
        return numbers;
    }

    public static void deleteContact(Context context, ContactItem contactItem) {
        DatabaseHelper helper = DatabaseHelper.getInstance(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        db.delete(TABLE_USER_CONTACTS, COLUMN_NUMBER + POST_ARGS, new String[]{contactItem.getContactNumber()});
        // db.close();
    }

    public static void deleteAllContact(Context context) {
        Fog.d("UserConTable", "deleteAllContact");
        DatabaseHelper helper = DatabaseHelper.getInstance(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(TABLE_USER_CONTACTS, null, null);

    }

    public static void updateContact(Context context, ContactItem contactItem) {
        DatabaseHelper helper = DatabaseHelper.getInstance(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        db.update(TABLE_USER_CONTACTS, getContentValues(contactItem), COLUMN_ID + POST_ARGS + contactItem.get_id(), null);
        // db.close();
    }

    @NonNull
    private static ContentValues getContentValues(ContactItem contactItem) {
        Fog.d("UserConTable", "getContentValues");
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, contactItem.getContactName());
        contentValues.put(COLUMN_NUMBER, contactItem.getContactNumber());
        contentValues.put(COLUMN_IMAGE, contactItem.getContactImage());
        contentValues.put(COLUMN_SUB_TITLE, contactItem.getSubTitle());
        contentValues.put(COLUMN_IS_ADDED_IN_NETWORK, contactItem.isAddedInNetwork());
        contentValues.put(COLUMN_VERSION, contactItem.getVersion());
        contentValues.put(COLUMN_IS_UNREGISTERD, contactItem.isUnregisterd());
        return contentValues;
    }

    public static void updateContactRegisterStatus(Context context, String contactNumber, int isUnregistered) {
        Fog.d("UserConTable", "updateContactRegisterStatus");
        DatabaseHelper helper = DatabaseHelper.getInstance(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        db.update(TABLE_USER_CONTACTS, getUpdateRegisterStatusCV(isUnregistered), "number ='" + contactNumber + "'", null);

        /*created by pallavi. This cursor query is wriiten to see if User All Contact table have same data as User contact*/

        // db.update(TABLE_USER_ALL_CONTACTS, getUpdateRegisterStatusCV(isUnregistered), "number ='" + contactNumber + "'", null);

        Intent intent = new Intent(Constants.UPDATE_CONTACT_ACTION);
        intent.putExtra(Constants.RECIPIENT_NUMBER, contactNumber);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        // db.close();
    }

    private static ContentValues getUpdateRegisterStatusCV(int isUnregistered) {
        Fog.d("UserConTable", "getUpdateRegisterStatusCV");
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_IS_UNREGISTERD, isUnregistered);
        return contentValues;
    }


    public static void insertArrayListIntoDB(Context context, ArrayList<ContactItem> contactItems, int i) {
        ArrayList<ContactItem> contacts = new ArrayList<>();
        if (i == 1) {
            deleteAllContact(context);
        }
        for (ContactItem ct : contactItems) {

            if (!(ct.getContactNumber().equalsIgnoreCase(FunduUser.getContactId()))) {
                contacts.add(ct);
            }
        }

        String sql = "INSERT OR REPLACE INTO " + TABLE_USER_CONTACTS + " (" + COLUMN_NAME + "," + COLUMN_NUMBER + "," + COLUMN_COUNTRY_CODE + "," + COLUMN_IMAGE + "," + COLUMN_SUB_TITLE + "," + COLUMN_IS_ADDED_IN_NETWORK + "," + COLUMN_VERSION + "," + COLUMN_IS_UNREGISTERD + "  ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ? )";

         /*created by pallavi. This cursor query is wriiten to see if User All Contact table have same data as User contact*/
        //  String sql = "INSERT OR REPLACE INTO " + TABLE_USER_ALL_CONTACTS + " (" + COLUMN_NAME + "," + COLUMN_NUMBER + "," + COLUMN_IMAGE + "," + COLUMN_SUB_TITLE + "," + COLUMN_IS_ADDED_IN_NETWORK + "," + COLUMN_VERSION + "," + COLUMN_IS_UNREGISTERD + "  ) VALUES ( ?, ?, ?, ?, ?, ?, ? )";

        DatabaseHelper helper = DatabaseHelper.getInstance(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransactionNonExclusive();
        SQLiteStatement stmt = db.compileStatement(sql);
        for (ContactItem contactItem : contacts) {

            getStatement(stmt, contactItem);
            long id = stmt.executeInsert();
            Fog.d("insertContact", "insertContact" + contactItem.getContactNumber()+":"+contactItem.getContactName()
            +":"+id);
            //stmt.execute();
            stmt.clearBindings();
//            Fog.e("UserConTableIN",contactItem.getContactNumber()+" , insertArrayListIntoDB reg======>"+contactItem.getIsUnregisterd());
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        DataUpdated updated = new DataUpdated();
        updated.type = DataUpdated.DataUpdatedType.UserContacts;
        EventBus.getDefault().post(updated);
    }

    private static void getStatement(SQLiteStatement stmt, ContactItem contactItem) {


        stmt.bindString(1, (contactItem.getContactName() == null ? "" : contactItem.getContactName()));
        stmt.bindString(2, (contactItem.getContactNumber() == null ? "" : contactItem.getContactNumber()));
        stmt.bindString(3, (contactItem.getContactCountryCode() == null ? "" : contactItem.getContactCountryCode()));
        stmt.bindString(4, (contactItem.getContactImage() == null ? "" : contactItem.getContactImage()));
        stmt.bindString(5, (contactItem.getSubTitle() == null ? "" : contactItem.getSubTitle()));
        stmt.bindString(6, "");
        stmt.bindString(7, (contactItem.getVersion() == null ? "" : contactItem.getVersion()));
        stmt.bindDouble(8, (contactItem.isUnregisterd() == -1 ? 1 : contactItem.isUnregisterd()));


    }

    public static ArrayList<String> getContactNumbers(Context context) {
        ArrayList<String> contactNumber = new ArrayList<>();
        DatabaseHelper helper = DatabaseHelper.getInstance(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USER_CONTACTS, new String[]{COLUMN_NUMBER}, null, null, null, null, null);
        if (cursor != null && cursor.getColumnCount() != 0) {
            cursor.move(0);
            while (cursor.moveToNext()) {
                String number = cursor.getString(0);
                contactNumber.add(number);
            }
        }
        //   db.close();
        return contactNumber;
    }
    public  void updateForFunduUser(JSONArray funduUsers){
        if(funduUsers == null || funduUsers.length() == 0)
            return;
        AsyncTask<JSONArray,Void, String> task = new AsyncTask<JSONArray, Void, String>() {
            @Override
            protected String doInBackground(JSONArray... params) {
                JSONArray list = params[0];
                ArrayList<String> funduUsers = new ArrayList<>();
                String funduUsersClause = "";

                for(int i = 0;i<list.length();i++){
                    try {
                        JSONObject funduUser = list.getJSONObject(i);
                        String contactId = funduUser.getString("contact_id");
                        funduUsersClause = funduUsersClause+",?";
                        funduUsers.add(contactId);
                    }
                    catch (JSONException e){
                        e.printStackTrace();
                    }


                }
                DatabaseHelper helper = DatabaseHelper.getInstance(FunduApplication.getAppContext());
                SQLiteDatabase db = helper.getReadableDatabase();

                if(funduUsers.size()>0){
                    funduUsersClause = funduUsersClause.substring(1);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(COLUMN_IS_UNREGISTERD, false);
                    String[] args = funduUsers.toArray(new String[funduUsers.size()]);
                    int rowsUpdated =  db.update(TABLE_USER_CONTACTS, contentValues, COLUMN_NUMBER+" IN ("+funduUsersClause+")",args);
                    Fog.i(TAG,"rows updated:"+rowsUpdated);

                }


                return null;
            }
            @Override
            protected void onPostExecute(String params){


            }
        };
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,funduUsers
        );

    }
    public static void updateForInvited(final Context context,JSONArray invited){
        if(invited == null || invited.length() ==0)
            return;
        AsyncTask<JSONArray,Void, String> task = new AsyncTask<JSONArray, Void, String>() {
            @Override
            protected String doInBackground(JSONArray... params) {
                JSONArray list = params[0];
                ArrayList<String> invited = new ArrayList<>();
                String invitedClause = "";
                ArrayList<String>  notInvited = new ArrayList<>();
                String notInvitedClause = "";
                for(int i = 0;i<list.length();i++){
                    try {
                        JSONObject invitedContact = list.getJSONObject(i);
                        String contactId = invitedContact.getString("contact_id");
                        String ii = invitedContact.optString("ii");

                        if(ii.length() > 1){
                            invitedClause = invitedClause+",?";
                            invited.add(contactId);
                        }
                        else{
                            notInvited.add(contactId);
                            notInvitedClause = notInvitedClause+",?";
                        }

                    }
                    catch (JSONException e){
                        e.printStackTrace();
                    }


                }
                DatabaseHelper helper = DatabaseHelper.getInstance(context);
                SQLiteDatabase db = helper.getReadableDatabase();

                if(invited.size()>0){
                    invitedClause = invitedClause.substring(1);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(COLUMN_IS_ADDED_IN_NETWORK, true);
                    String[] args = invited.toArray(new String[invited.size()]);
                    int rowsUpdated =  db.update(TABLE_USER_CONTACTS, contentValues, COLUMN_NUMBER+" IN ("+invitedClause+")",args);
                    Fog.i(TAG,"rows updated:"+rowsUpdated);

                }
                if(notInvited.size()>0){
                    notInvitedClause = notInvitedClause.substring(1);
                    String[] args = notInvited.toArray(new String[notInvited.size()]);
                    ContentValues contentValues1 = new ContentValues();
                    contentValues1.put(COLUMN_IS_ADDED_IN_NETWORK, false);
                    int rowsUpdated = db.update(TABLE_USER_CONTACTS, contentValues1, "number IN(" + notInvitedClause + ")", args);
                    Fog.i(TAG,"rows updated:"+rowsUpdated);

                }
                return null;
            }
            @Override
            protected void onPostExecute(String params){
                DataUpdated event = new DataUpdated();
                event.type = DataUpdated.DataUpdatedType.UserContacts;
                EventBus.getDefault().post(event);

            }
        };
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,invited
        );
    }
    public static void updateForRegistered(final Context context,JSONArray invited){
        if(invited == null || invited.length() ==0)
            return;
        AsyncTask<JSONArray,Void, String> task = new AsyncTask<JSONArray, Void, String>() {
            @Override
            protected String doInBackground(JSONArray... params) {
                JSONArray list = params[0];
                ArrayList<String> registered = new ArrayList<>();
                String registeredClause = "";
                ArrayList<String>  unregistred = new ArrayList<>();
                String unregistredClause = "";
                for(int i = 0;i<list.length();i++){
                    try {
                        JSONObject contact = list.getJSONObject(i);
                        String contactId = contact.getString("contact_id");
                        boolean active = contact.getBoolean("active");
                        if(active){
                            registeredClause = registeredClause+",?";
                            registered.add(contactId);
                        }
//                        else{
//                            unregistred.add(contactId);
//                            unregistredClause = notInvitedClause+",?";
//                        }
                    }
                    catch (JSONException e){
                        e.printStackTrace();
                    }
                }
                DatabaseHelper helper = DatabaseHelper.getInstance(context);
                SQLiteDatabase db = helper.getReadableDatabase();

                if (registered.size() > 0) {
                    registeredClause = registeredClause.substring(1);
                    String[] args = registered.toArray(new String[registered.size()]);
                    ContentValues contentValues1 = new ContentValues();
                    contentValues1.put(COLUMN_IS_UNREGISTERD, 0);
                    int rowsUpdated = db.update(TABLE_USER_CONTACTS, contentValues1, "number IN(" + registeredClause + ")", args);
                    Fog.i(TAG, "rows updated registered number: " + rowsUpdated);

                }
                if (unregistred.size() > 0) {
                    unregistredClause = unregistredClause.substring(1);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(COLUMN_IS_UNREGISTERD, 1);
                    String[] args = unregistred.toArray(new String[unregistred.size()]);
                    int rowsUpdated = db.update(TABLE_USER_CONTACTS, contentValues, COLUMN_NUMBER + " IN (" + unregistredClause + ")", args);
                    Fog.i(TAG, "rows updated unregistered numbers:" + rowsUpdated);

                }
                return null;
            }
            @Override
            protected void onPostExecute(String params){
                DataUpdated event = new DataUpdated();
                event.type = DataUpdated.DataUpdatedType.UserContacts;
                EventBus.getDefault().post(event);

            }
        };
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,invited
        );
    }

    public static void updateForRegistered(final Context context, ArrayList<ContactItem> contactItems) {
        if (contactItems == null || contactItems.size() == 0)
            return;
        AsyncTask<ArrayList<ContactItem>, Void, String> task = new AsyncTask<ArrayList<ContactItem>, Void, String>() {
            @Override
            protected String doInBackground(ArrayList<ContactItem>... params) {
                try {
                    ArrayList<ContactItem> list = params[0];
                    ArrayList<String> unregistred = new ArrayList<>();
                    String unregisteredClause = "";
                    ArrayList<String> registered = new ArrayList<>();
                    String registeredClause = "";
                    for (int i = 0; i < list.size(); i++) {

                        ContactItem contactItem = list.get(i);
                        String contactId = contactItem.getContactNumber();
                        int unregisterd = contactItem.getIsUnregisterd();

                        if (unregisterd == 1) {
                            unregisteredClause = unregisteredClause + ",?";
                            unregistred.add(contactId);
                        } else {
                            registered.add(contactId);
                            registeredClause = registeredClause + ",?";
                        }


                    }
                    DatabaseHelper helper = DatabaseHelper.getInstance(context);
                    SQLiteDatabase db = helper.getReadableDatabase();

                    if (registered.size() > 0) {
                        registeredClause = registeredClause.substring(1);
                        String[] args = registered.toArray(new String[registered.size()]);
                        ContentValues contentValues1 = new ContentValues();
                        contentValues1.put(COLUMN_IS_UNREGISTERD, 0);
                        int rowsUpdated = db.update(TABLE_USER_CONTACTS, contentValues1, "number IN(" + registeredClause + ")", args);
                        Fog.i(TAG, "rows updated registered number: " + rowsUpdated);

                    }
                    if (unregistred.size() > 0) {
                        unregisteredClause = unregisteredClause.substring(1);
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(COLUMN_IS_UNREGISTERD, 1);
                        String[] args = unregistred.toArray(new String[unregistred.size()]);
                        int rowsUpdated = db.update(TABLE_USER_CONTACTS, contentValues, COLUMN_NUMBER + " IN (" + unregisteredClause + ")", args);
                        Fog.i(TAG, "rows updated unregistered numbers:" + rowsUpdated);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String params) {
                DataUpdated event = new DataUpdated();
                event.type = DataUpdated.DataUpdatedType.UserContacts;
                EventBus.getDefault().post(event);

            }
        };
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, contactItems
        );
    }

    public static void deleteContact(final Context context,ArrayList<ContactItem> contactItems){
        if(contactItems == null || contactItems.size() ==0)
            return;
        AsyncTask<ArrayList<ContactItem>,Void, String> task = new AsyncTask<ArrayList<ContactItem>, Void, String>() {
            @Override
            protected String doInBackground(ArrayList<ContactItem>... params) {
                ArrayList<ContactItem> list = params[0];
                ArrayList<String> deleted = new ArrayList<>();
                String deletedClause = "";

                for(int i = 0;i<list.size();i++){
                    try {
                        ContactItem contactItem = list.get(i);
                        String contactId = contactItem.getContactNumber();
                        deletedClause = deletedClause+",?";
                        deleted.add(contactId);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }


                }
                DatabaseHelper helper = DatabaseHelper.getInstance(context);
                SQLiteDatabase db = helper.getReadableDatabase();

                if(deleted.size()>0){
                    deletedClause = deletedClause.substring(1);
                    String[] args = deleted.toArray(new String[deleted.size()]);
                    int rowsdeleted =  db.delete(TABLE_USER_CONTACTS, COLUMN_NUMBER+" IN ("+deletedClause+")",args);
                    Fog.i(TAG,"rows deleted:"+rowsdeleted);

                }
                return null;
            }
            @Override
            protected void onPostExecute(String params){
                DataUpdated event = new DataUpdated();
                event.type = DataUpdated.DataUpdatedType.UserContacts;
                EventBus.getDefault().post(event);

            }
        };
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,contactItems
        );
    }

    public static void  updateContactNumber(Context context, String number, HashMap<String, String> formattedMap) {


        DatabaseHelper helper = DatabaseHelper.getInstance(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
       /* Fog.d("formatted_ph_num", "formatted_ph_num" + number);
        Fog.d("formatted_ph_num", "PHONENUMBER" + formattedMap.get(Constants.PHONENUMBER));
        Fog.d("formatted_ph_num", "COUNTRYCODE" + formattedMap.get(Constants.COUNTRYCODE));*/

        values.put(COLUMN_COUNTRY_CODE, formattedMap.get(Constants.COUNTRYCODE));
        values.put(COLUMN_NUMBER, formattedMap.get(Constants.PHONENUMBER));
        int rowsModified = db.update(TABLE_USER_CONTACTS, values, COLUMN_NUMBER + POST_ARGS , new String[]{number});
        Fog.i("","");

    }

    public void updateContacts(ArrayList<ContactItem> contactItems){

    }




}
