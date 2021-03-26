package in.co.eko.fundu.database.tables;
/*
 * Created by Bhuvnesh
 */

import android.database.sqlite.SQLiteDatabase;


import in.co.eko.fundu.utils.Fog;


public class UserTable {

    // Database table
    public static final String TABLE_USER = "user";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_USERNAME = "category";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_OTHER_EMAIL = "otherEmail";
    public static final String COLUMN_MOBILE = "mobile";
    public static final String COLUMN_OTHER_MOBILE = "otherMobile";
    public static final String COLUMN_ACCOUNTS_NUMBER= "accountsNumber";
    public static final String COLUMN_BANK_NAME= "bankName";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_WALLET = "wallet";
    public static final String COLUMN_IS_LOGIN = "isLogin";
    public static final String COLUMN_SOCIAL_ID = "social_id";
    public static final String COLUMN_PROFILE_PIC_URL = "pic_url";
    public static final String COLUMN_DEVICE_ID = "deviceId";
    public static final String IS_MOBILE_VERIFIED = "isMobileVerified";


    // Database creation SQL statement
    private static final String CREATE_USER_TABLE = "create table "
            + TABLE_USER + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_USERNAME + " text , "
            + COLUMN_DEVICE_ID + " text , "
            + COLUMN_NAME + " text , "
            + COLUMN_EMAIL + " text ,"
            + COLUMN_OTHER_EMAIL + " text, "
            + COLUMN_MOBILE + " text, "
            + COLUMN_OTHER_MOBILE + " text, "
            + COLUMN_ACCOUNTS_NUMBER + " text, "
            + COLUMN_BANK_NAME + " text, "
            + COLUMN_ADDRESS + " text, "
            + COLUMN_WALLET + " text, "
            + COLUMN_IS_LOGIN + " text, "
            + COLUMN_SOCIAL_ID + " text, "
            + IS_MOBILE_VERIFIED + " text, "
            + COLUMN_PROFILE_PIC_URL + " text "
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_USER_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Fog.e(UserTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(database);
    }
}
