package in.co.eko.fundu.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.database.tables.UserContactsTable;
import in.co.eko.fundu.models.ContactItem;
import in.co.eko.fundu.models.ContactResponseItem;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.parser.UniversalParser;
import in.co.eko.fundu.requests.GetContacts;
import in.co.eko.fundu.requests.SaveContactsToServerRequest;
import in.co.eko.fundu.utils.AppPreferences;
import in.co.eko.fundu.utils.ContactsUtils;
import in.co.eko.fundu.utils.Fog;

public class SyncContactsIntentService extends IntentService implements SaveContactsToServerRequest.OnSaveContactsResults {


    private String TAG = SyncContactsIntentService.class.getName();

    ArrayList<ContactItem> contactItems = new ArrayList<>();
    ArrayList<ContactItem> finalContactList = new ArrayList<>();
    private HashMap<String, Integer> isUnregisterdMap = new HashMap<>();
    private HashMap<String, Boolean> isInvitedMap = new HashMap<>();
    private static String device_id;
    private static AppPreferences pref;

    public SyncContactsIntentService() {
        super("SyncContactsIntentService");
    }

    public static void startService(Context context, String device_id) {

        SyncContactsIntentService.device_id = device_id;
        Intent intent = new Intent(context, SyncContactsIntentService.class);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
           // getContactItems();
            getNewContactsFromPhoneBook();
            if (contactItems.size() > 0){
                saveContactsToServer();
            }
            getInvitedContacts();
            getFunduContacts();

        }
    }
    private void getFunduContacts(){
        GetContacts getContacts = new GetContacts(this);
        getContacts.setData("fundu");
        getContacts.setParserCallback(new GetContacts.OnContactRequestResult() {
            @Override
            public void onContactsResponse(JSONArray response) {
                //Update contact table for invited
                UserContactsTable.updateForRegistered(SyncContactsIntentService.this,response);
            }
            @Override
            public void onContactsError(VolleyError error) {

            }
        });
        getContacts.start();
    }
    private void getInvitedContacts(){
        GetContacts getContacts = new GetContacts(this);
        getContacts.setData("invited");
        getContacts.setParserCallback(new GetContacts.OnContactRequestResult() {
            @Override
            public void onContactsResponse(JSONArray response) {
                //Update contact table for invited
                UserContactsTable.updateForInvited(SyncContactsIntentService.this,response);
            }
            @Override
            public void onContactsError(VolleyError error) {

            }
        });
        getContacts.start();
    }
    private void getNewContactsFromPhoneBook(){
        ArrayList<ContactItem> contactsFromPhoneBook,contactsFromDb;
        contactsFromPhoneBook = ContactsUtils.getInstance(this).getContactsFromPhoneBook();
        contactsFromDb = ContactsUtils.getInstance(this).getAllContactsFromLocalDB(this);
        Set<ContactItem> set1 = new HashSet<ContactItem>();
        set1.addAll(contactsFromDb);

        Set<ContactItem> set2 = new HashSet<ContactItem>();
        set2.addAll(contactsFromPhoneBook);
        set2.removeAll(set1);
        Set<ContactItem> set3 = new HashSet<>();
        set3.addAll(contactsFromPhoneBook);
        set1.removeAll(set3);
        ArrayList<ContactItem> newContacts =  new ArrayList<ContactItem>();
        newContacts.addAll(set2);
        ArrayList<ContactItem> removedContacts = new ArrayList<>();
        removedContacts.addAll(set1);
        UserContactsTable.deleteContact(this,removedContacts);
        contactItems = newContacts;

    }

    private void getContactItems() {
        if (ContactsUtils.getInstance(this).getAllContactsDBCount(getBaseContext()) > 0) {
            contactItems = ContactsUtils.getInstance(this).getAllContactsFromLocalDB(getBaseContext());
            Fog.e("CFD", "Contact from db");
        } else {
            contactItems = ContactsUtils.getInstance(this).getContactsFromPhoneBook();

            Fog.e("CFD", "Contact from Phonebook");
        }
    }

    private void saveContactsToServer() {
        pref= FunduUser.getAppPreferences();
        String countryCode = pref.getString("country_code_r");
        SaveContactsToServerRequest saveContactsToServerRequest = new SaveContactsToServerRequest(getBaseContext());
        saveContactsToServerRequest.setData(contactItems, device_id, countryCode);
        saveContactsToServerRequest.setParserCallback(this);
        saveContactsToServerRequest.start();
    }

    private void saveToDB() {
        Fog.i(TAG, "saveToDB");

        UserContactsTable.insertArrayListIntoDB(getBaseContext(), contactItems, 0);
        for (ContactItem contactItem : contactItems) {

            Integer isUnregisterd = isUnregisterdMap.get(contactItem.getContactNumber());

            if (isUnregisterd != null) {
                contactItem.setIsUnregisterd(isUnregisterd);
            }
            else{
                contactItem.setIsUnregisterd(1);
            }
            Boolean isInvited = isInvitedMap.get(contactItem.getContactNumber());
            if(isInvited != null && isInvited.booleanValue()){
                contactItem.setIsAddedInNetwork(true);
            }
            else{
                contactItem.setIsAddedInNetwork(false);
            }
            finalContactList.add(contactItem);
        }
        UserContactsTable.updateForRegistered(getBaseContext(), finalContactList);
        contactItems.clear();
        System.gc();

    }

    @Override
    public void onSaveContactsResponse(JSONObject response) {
        try {

            ArrayList<ContactResponseItem> contactResponseItems = UniversalParser.getInstance().parseJsonArrayWithJsonObject(response.getJSONArray(Constants.CONTACT_RESPONSE_LIST), ContactResponseItem.class);
            for (ContactResponseItem contactResponseItem : contactResponseItems) {
                int unregisterStatus;
                if (contactResponseItem.isDummyCustomer())
                    unregisterStatus = 1;
                else
                    unregisterStatus = 0;
                isUnregisterdMap.put(contactResponseItem.getId(), unregisterStatus);
                if(contactResponseItem.getIi()!=null&&contactResponseItem.getIi().length() >0){
                    isInvitedMap.put(contactResponseItem.getId(),true);
                }
                else{
                    isInvitedMap.put(contactResponseItem.getId(),false);
                }
            }
            saveToDB();
        } catch (JSONException e) {
            e.printStackTrace();

        }
    }

    @Override
    public void onSaveContactsError(VolleyError error) {
        //Toast.makeText(getBaseContext(), error.toString(), Toast.LENGTH_SHORT).show();

    }

    boolean contains(ArrayList<ContactItem> list, String number) {
        for (ContactItem item : list) {
            if (item.getContactNumber().equals(number)) {
                return true;
            }
        }
        return false;
    }
}
