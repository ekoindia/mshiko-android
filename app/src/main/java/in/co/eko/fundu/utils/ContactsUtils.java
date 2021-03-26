package in.co.eko.fundu.utils;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.PhoneNumberUtils;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.database.tables.UserAllContactsTable;
import in.co.eko.fundu.database.tables.UserContactsTable;
import in.co.eko.fundu.models.ContactItem;
import in.co.eko.fundu.models.ContactResponseItem;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.parser.UniversalParser;
import in.co.eko.fundu.requests.SaveContactsToServerRequest;

/**
 * Created by divyanshu.jain on 8/1/2016.
 */


public class ContactsUtils {
    private static ContactsUtils contactsUtils;
    private String TAG = "ContactsUtils";
    private HashMap<String, Integer> isUnregisterdMap = new HashMap<>();
    private ArrayList<ContactItem> contactitems = new ArrayList<>();
    ArrayList<ContactItem> finalContactList = new ArrayList<>();
    private Context context;
    private boolean haveAccess;
    private ContactsUtils(Context context) {
        this.context = context;
        if(Utils.checkPermission(context, Manifest.permission.READ_CONTACTS)){
            haveAccess = true;
        }
    }

    public static ContactsUtils getInstance(Context context) {
        if (contactsUtils == null){
            contactsUtils = new ContactsUtils(context);
        }
        return contactsUtils;
    }

    private ArrayList<ContactItem> contactItemsFromContacts = new ArrayList<>();
    private ArrayList<ContactItem> contactItemsFromLocalDB = new ArrayList<>();
    private ArrayList<String> contactNumbersFromLocalDB = new ArrayList<>();
    private HashMap<String, ContactItem> contactNumbersFromContactsMap = new HashMap<>();

    public ArrayList<ContactItem> getContactsFromPhoneBook() {
        contactItemsFromContacts.clear();
        contactNumbersFromContactsMap.clear();
        if(haveAccess){
            Fog.e(TAG, "getContactsFromPB");
            Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            String[] projection = new String[]{
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER,
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Phone.PHOTO_URI
            };
            getContactsFromUri(context, uri, projection, true);
        }
        return contactItemsFromContacts;
    }

    private void getContactsFromUri(Context context, Uri uri, String[] projection, boolean isNumberUri) {

        String selection = ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER + " = '1'";
        selection = selection+" AND "+ContactsContract.CommonDataKinds.Phone.NUMBER +" NOT LIKE ?";
        String[] filter = {"%"+FunduUser.getContactId()
                +"%"};
        String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
        Cursor phones = context.getContentResolver().query(uri, projection,
                selection, filter, sortOrder);
        String number = "";
        HashMap<String,String> formattedMap = new HashMap<>(); //created by pallavi
        int contactCount = phones.getCount();
        if(contactCount > 0 && phones.moveToFirst()){
            do {
                String Name = phones
                        .getString(phones
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                if (isNumberUri)
                    number = phones
                            .getString(phones
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String image_uri = phones.getString(phones
                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                formattedMap = Utils.phoneNumberParser(context,number);
                if(!formattedMap.get(Constants.PHONENUMBER).contains("****")){

                    ContactItem contactItem = new ContactItem(Name, formattedMap.get(Constants.PHONENUMBER), image_uri,formattedMap.get(Constants.COUNTRYCODE));
                    if (!contactItemsFromContacts.contains(contactItem)) {
                        contactItemsFromContacts.add(contactItem);
                        contactNumbersFromContactsMap.put(formattedMap.get(Constants.PHONENUMBER), contactItem);
                    }
                }
            } while(phones.moveToNext());
        }
        phones.close();
    }

    public ArrayList<ContactItem> getAllContactsFromLocalDB(Context context) {
        Fog.e(TAG, "getAllContactsFromLocalDB");
        contactItemsFromLocalDB = UserContactsTable.getContacts(context);
        return contactItemsFromLocalDB;
    }

    public int getAllContactsDBCount(Context context) {
        return UserAllContactsTable.getContactsCount(context);
    }

    private void insertAllContactsToLocalDB(Context context, Collection<ContactItem> contactItems) {
        Fog.d("UserAllConTable","insertAllContactsToLocalDB");
        UserAllContactsTable.insertContactItemsIntoDB(context, new ArrayList<>(contactItems));
    }

    private void insertContactsToLocalDB(Context context, Collection<ContactItem> contactItems) {
        Fog.d("UserAllConTable","insertContactsToLocalDB");
        for (ContactItem contactitem : contactItems) {
        contactitem.setIsUnregisterd(1);
        }
        UserContactsTable.insertArrayListIntoDB(context, new ArrayList<>(contactItems), 0);
    }

    private void updateContactsDeleteStatusIntoLocalDB(Context context, ArrayList<String> contactNumbers, int status) {
        Fog.d("UserAllConTable","updateContactsDeleteStatusIntoLocalDB");
        for (String contactNumber : contactNumbers) {
            UserAllContactsTable.updateContact(context, status, contactNumber);
        }
    }

    public void syncLocalDB(Context context) {
        new SyncLocalDbWithContact(context).execute();
    }

    private class SyncLocalDbWithContact extends AsyncTask<Void, Void, Void> {
        private Context context;
//        private ProgressDialog dialog;

        SyncLocalDbWithContact(Context context) {
            this.context = context;
//            dialog = new ProgressDialog(context);
//            dialog.setMessage("Loading...");
        }

        @Override
        protected Void doInBackground(Void... params) {

            contactNumbersFromLocalDB = UserAllContactsTable.getContactNumbers(context);
            getContactsFromPhoneBook();
            Fog.e("Size of Phone", ""+contactNumbersFromContactsMap.size()+" From ALl contact: "+contactNumbersFromLocalDB.size());
            if (contactNumbersFromLocalDB.size()==0){

            }
            else {
                for (Iterator<String> iterator = contactNumbersFromLocalDB.iterator();
                       iterator.hasNext(); ) {
                    String number = iterator.next();
                    if (contactNumbersFromContactsMap.containsKey(number)) {
                        contactNumbersFromContactsMap.remove(number);
                        iterator.remove();
                    }
                }
            }
            Fog.e("Size after match", ""+contactNumbersFromContactsMap.size()+" From ALl contact: "+contactNumbersFromLocalDB.size());
            contactitems = new ArrayList<>(contactNumbersFromContactsMap.values());
            updateContactsDeleteStatusIntoLocalDB(context, contactNumbersFromLocalDB, 1);
            insertAllContactsToLocalDB(context, contactNumbersFromContactsMap.values());
//            insertContactsToLocalDB(context, contactNumbersFromContactsMap.values());
            if (FunduUser.getFullName()!=null) {
                if (contactitems.size() > 0) {
                    String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
//                    dialog.show();
                    Fog.d("contactItem","contactItem"+contactitems.size());
                    SaveContactsToServerRequest saveContactsToServerRequest = new SaveContactsToServerRequest(context);
                    saveContactsToServerRequest.setData(contactitems, androidId, FunduUser.getCountryShortName());
                    saveContactsToServerRequest.setParserCallback(new SaveContactsToServerRequest.OnSaveContactsResults() {
                        @Override
                        public void onSaveContactsResponse(JSONObject response) {
//                        dialog.dismiss();
                            try {

                                ArrayList<ContactResponseItem> contactResponseItems = UniversalParser.getInstance().parseJsonArrayWithJsonObject(response.getJSONArray(Constants.CONTACT_RESPONSE_LIST), ContactResponseItem.class);
                                for (ContactResponseItem contactResponseItem : contactResponseItems) {
                                    int unregisterStatus;
                                    if (contactResponseItem.isDummyCustomer())
                                        unregisterStatus = 1;
                                    else
                                        unregisterStatus = 0;
                                    isUnregisterdMap.put(contactResponseItem.getId(), unregisterStatus);

//                                    Fog.e("IND SYC", contactResponseItem.getId()+"...."+unregisterStatus+"...."+contactResponseItem.isDummyCustomer());
                                }
                                saveToDB(context);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onSaveContactsError(VolleyError error) {
//                            dialog.dismiss();
                        }
                    });
                    saveContactsToServerRequest.start();
                }
            }
//            02-02 15:45:45.431 805-868/in.co.eko.fundu D/UserAllConTable: updateContactsDeleteStatusIntoLocalDB
//            02-02 15:45:45.431 805-868/in.co.eko.fundu D/UserAllConTable: insertAllContactsToLocalDB
//            02-02 15:45:45.432 805-868/in.co.eko.fundu E/UserAlConTableIN: 733333333 , reg======>-1
            System.gc();
            return null;
        }
    }

    private void saveToDB(Context context) {
        for (ContactItem contactItem : contactitems) {

            Integer isUnregisterd = isUnregisterdMap.get(contactItem.getContactNumber());
            if (isUnregisterd != null) {
                contactItem.setIsUnregisterd(isUnregisterd);
                finalContactList.add(contactItem);
            }
            else{
                contactItem.setIsUnregisterd(1);
                finalContactList.add(contactItem);
            }
        }
        UserContactsTable.insertArrayListIntoDB(context, finalContactList, 0);
        contactitems.clear();
        System.gc();

    }

//    public ArrayList<CallLogsModel> getCallLogs(Context context) {
//        Fog.d("UserConTable","getCallLogs");
//        ArrayList<String> registeredNumbers = UserContactsTable.getRegisteredNumbers(context);
//        ArrayList<CallLogsModel> callLogs = new ArrayList<>();
//
//       /* ArrayList<String> registeredNumbersNew = UserContactsTable.getRegisteredNumbersNew(context);
//        Fog.d("final_string","final_string"+registeredNumbersNew.size());*/
//
//        String mSelectionClause = android.provider.CallLog.Calls.DATE + " >= ?";
//        String[] mSelectionArgs = {createDate().toString()};
//
//        String[] projection = new String[]{
//                CallLog.Calls.CACHED_NAME,
//                CallLog.Calls.NUMBER,
//                CallLog.Calls.TYPE
//        };
//        ContentResolver cr = context.getContentResolver();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (context.checkSelfPermission(Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                return callLogs;
//            }
//        }
//
//        Cursor recentCallsCursor = cr.query(CallLog.Calls.CONTENT_URI,
//                projection, mSelectionClause, mSelectionArgs, null);
//
//        recentCallsCursor.moveToFirst();
//        while (recentCallsCursor.moveToNext()) {
//            int type = Integer.parseInt(recentCallsCursor.getString(recentCallsCursor.getColumnIndex(CallLog.Calls.TYPE)));
//            if ((type == CallLog.Calls.OUTGOING_TYPE)) {
//                String num = recentCallsCursor.getString(recentCallsCursor.getColumnIndex(CallLog.Calls.NUMBER));// for  number
//                String name = recentCallsCursor.getString(recentCallsCursor.getColumnIndex(CallLog.Calls.CACHED_NAME));// for name
//                num = Utils.checkAndRemoveCountryCode(context, num);
//                if (num != null && TextUtils.isDigitsOnly(num) && num.length() >= 8 /*10*/ /*&& num.substring(0, 1).matches("[7-9]")*/ /*&& !addedNumbers.contains(num)*/) {
//                    CallLogsModel callLogsModel = new CallLogsModel();
//                    callLogsModel.setContactNumber(num);
//                    if (callLogs.contains(callLogsModel)) {
//                        CallLogsModel callLogsModel1 = callLogs.get(callLogs.indexOf(callLogsModel));
//                        callLogs.get(callLogs.indexOf(callLogsModel)).setCount(callLogsModel1.getCount() + 1);
//                    } else {
//                        callLogsModel.setContactName(name == null ? num : name);
//                        callLogsModel.setCount(1);
//                        callLogsModel.setIsUnregisterd(registeredNumbers.contains(num) ? 0 : 1);
//                        callLogsModel.setContactImage("");
//                        callLogs.add(callLogsModel);
//                    }
//                }
//            }
//        }
//        Collections.sort(callLogs, new Comparator<CallLogsModel>() {
//            @Override
//            public int compare(CallLogsModel lhs, CallLogsModel rhs) {
//                return (rhs.getCount() - lhs.getCount());
//            }
//        });
//        if (callLogs.size() > 5)
//            callLogs = new ArrayList<>(callLogs.subList(0, 5));
//
//        registeredNumbers.clear();
//        System.gc();
//        return callLogs;
//    }

    private Long createDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -7);
        return calendar.getTimeInMillis();
    }

    public boolean contactExists(String number) {
        ContentResolver contentResolver = this.context.getContentResolver();
        Cursor phones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.
                CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        while (phones.moveToNext()){
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            if(PhoneNumberUtils.compare(number, phoneNumber)){
                return true;
            }
        }
        return false;
    }


}
