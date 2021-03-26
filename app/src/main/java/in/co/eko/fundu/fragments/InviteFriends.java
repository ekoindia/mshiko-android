package in.co.eko.fundu.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.co.eko.fundu.FunduApplication;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.R;
import in.co.eko.fundu.activities.HomeActivity;
import in.co.eko.fundu.adapters.InviteFriendsRecyclerViewAdapter;
import in.co.eko.fundu.database.DatabaseHelper;
import in.co.eko.fundu.database.tables.UserContactsTable;
import in.co.eko.fundu.event.DataUpdated;
import in.co.eko.fundu.models.ContactItem;
import in.co.eko.fundu.requests.InviteFriendsRequest;
import in.co.eko.fundu.utils.Fog;
import in.co.eko.fundu.utils.FunduAnalytics;
import in.co.eko.fundu.utils.Utils;
import in.co.eko.fundu.views.CustomProgressDialog;

//import butterknife.InjectView;


/**
 * A fragment representing a list of Items.
 * <p/>
 */
public class InviteFriends extends BaseFragment implements SearchView.OnQueryTextListener{
    private static final int SMS_REQUEST_CODE = 1;
    private String TAG = InviteFriends.class.getName();
    boolean is_send_button_clicked = false;
    private static final String ARG_COLUMN_COUNT = "1";
    private IntentFilter intentFilter;
    private static CustomProgressDialog progressDialog = null;
    private int mColumnCount = 1;
    private TextView search;
    private SearchView searchView;
    private InviteFriendsRecyclerViewAdapter.OnListFragmentInteractionListener mListener;
    private ArrayList<ContactItem> contactItems;
    private HomeActivity activity;
    private TextView textView_no,textViewSelectAll;
    private BroadcastReceiver sentReceiver;
    static boolean  isFirstClick;
    private AsyncTask mGetContactsTask;
//    @BindView(R.id.list)
    RecyclerView recyclerView;
    @BindView(R.id.sendButton)
    //ImageView sendButton;
     Button sendButton;
    private InviteFriendsRecyclerViewAdapter adapter;

    @BindView(R.id.textView_skip)
    public TextView skip;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public InviteFriends() {
    }
    public static InviteFriends newInstance(int columnCount) {
        InviteFriends fragment = new InviteFriends();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new CustomProgressDialog(getActivity());
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
        EventBus.getDefault().register(this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.contact_item_list, container, false);
        textView_no = (TextView)view.findViewById(R.id.textView_no);
        search = (TextView)view.findViewById(R.id.search);
        searchView = (SearchView)view.findViewById(R.id.searchview);
        recyclerView=(RecyclerView)view.findViewById(R.id.list);
        textViewSelectAll = (TextView)view.findViewById(R.id.textView_selectAll);

        ButterKnife.bind(this, view);
        // Set the adapter
       if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
        }
        initViews();
        FunduAnalytics.getInstance(getActivity()).sendScreenName("InviteContacts");
        return view;
    }
    private void initViews(){

        //Find contacts and populate the list in background
        if(mGetContactsTask != null){
            mGetContactsTask.cancel ( true );
        }
        mGetContactsTask = new GetContactsTask();
        mGetContactsTask.execute();
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get selected user and send invitation
                sendInvitation();
            }
        });
        mListener = new InviteFriendsRecyclerViewAdapter.OnListFragmentInteractionListener() {
            @Override
            public void onListFragmentInteraction(ContactItem item) {

            }
            @Override
            public void changeSendVisibility(int visibility) {
               /* if(visibility == View.VISIBLE)
                    Animate.bubble(sendButton);
                else
                    Animate.reverseBubble(sendButton,0);*/
            }
        };
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                //inviteFriendListener.onSkip();

            }
        });
        customizeSearchView(searchView);
        searchView.setOnQueryTextListener(this);
        textViewSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSelectAllButtonClick();
            }
        });

    }

    @Override
    public void onStop() {
        super.onStop();
       /* if(mUpdateExistingUserDBReceiver!=null){
            getActivity().unregisterReceiver(mUpdateExistingUserDBReceiver);
        }*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
       /* if(mUpdateExistingUserDBReceiver!=null){
            getActivity().unregisterReceiver(mUpdateExistingUserDBReceiver);
        }*/
    }


   /* @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        inviteFriendListener = (InviteFriendListener)activity;
    }*/

    /**
     * Gets the selected items and sends request on server for the invitation
     */
    private void sendInvitation(){

        ArrayList<ContactItem> selectedItems =  adapter.getSelectedItems();

        //Send invitation
        Fog.i(TAG,"Sending invites to "+selectedItems.size()+" contacts");
         if(selectedItems.size()==0){
             Toast.makeText(activity, "Please Select Friends.", Toast.LENGTH_SHORT).show();
         }
         else{
             sendSmsForInvitation(selectedItems);
         }
        //TODO: show message app with already populated contact numbers and message to send.

//         StringBuilder uri = new StringBuilder("sms:");
//        for (int i = 0; i < selectedItems.size(); i++) {
//            uri.append(selectedItems.get(i).getContactNumber());
//            if(i!=selectedItems.size()-1){
//                uri.append(", ");
//            }
//
//            Fog.d("uri","uri"+uri);
//        }
//        try {
//            Intent smsIntent = new Intent(Intent.ACTION_VIEW);
//            smsIntent.setType("vnd.android-dir/mms-sms");
//            smsIntent.setData(Uri.parse(uri.toString()));
//            smsIntent.putExtra("exit_on_sent", true);
//            smsIntent.putExtra("sms_body", "Have you tried FundU yet? FundU is the best way to access CASH Anywhere Anytime!! Get it now https://play.google.com/store/apps/details?id=in.co.eko.fundu&hl=en");
//            startActivity(smsIntent);
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }


    }

    private void sendSmsForInvitation(final ArrayList<ContactItem> selectedItems){

        sendRequestForAppInvitation(selectedItems);
        //show confirmation screen for
       /* AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle("Sending SMS");
        dialog.setMessage("You will be charged standard sms charges for sending invites.");
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Constants
                String SENT_ACTION = "SMS_SENT_ACTION";
                String DELIVERED_ACTION = "SMS_DELIVERED_ACTION";
                String MESSAGE = "Have you tried FundU yet? FundU is the best way to access CASH Anywhere Anytime!! Get it now https://play.google.com/store/apps/details?id=in.co.eko.fundu&hl=en";
                String numbers = "";
                for(ContactItem contactItem : selectedItems){
                    numbers = numbers+";0"+contactItem.getContactNumber();
                }
                if(numbers.length() > 0){
                    numbers = numbers.substring(1);
                    // SMS sent pending intent
                    PendingIntent sentIntent = PendingIntent.getBroadcast(getActivity(), 0,
                            new Intent(SENT_ACTION), 0);

                    // SMS delivered pending intent
                    PendingIntent deliveredIntent = PendingIntent.getBroadcast(getActivity(), 0,
                            new Intent(DELIVERED_ACTION), 0);
                    sentReceiver = new SentReceiver(selectedItems);
                    // SMS sent receiver
                    activity.registerReceiver(sentReceiver,new IntentFilter(SENT_ACTION));

                    // Send the SMS message
                    SmsManager sms = SmsManager.getDefault();
                    sms.sendTextMessage(numbers,null,MESSAGE,sentIntent,deliveredIntent);

                }
            }
        });
        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();*/

    }
    private void sendRequestForAppInvitation(ArrayList<ContactItem> selectedItems){


        FunduAnalytics.getInstance(getActivity()).sendAction("Contacts","InvitationSent",selectedItems.size());

        if(Utils.isNetworkAvailable(getActivity())){
            progressDialog.show();
            progressDialog.setMessage("Sending invite...");
            InviteFriendsRequest request = new InviteFriendsRequest(FunduApplication.getAppContext(), new InviteFriendsRequest.OnInviteFriendsResult() {
                @Override
                public void onInviteFriendsResponse(JSONObject response) {
                    try{

                        progressDialog.dismiss();
                        JSONArray alreadyFunduUsers = response.getJSONArray("alreadyAdded");
                        JSONArray alreadyInvited = response.getJSONArray("alreadyInvited");
                        JSONArray successfullyInvited = response.getJSONArray("successfullyInvited");
                        String message = response.optString("message");
                        for(int i = 0;i<alreadyInvited.length();i++){
                            successfullyInvited.put(alreadyInvited.get(i));
                        }
                        UserContactsTable.updateForInvited(getActivity(),successfullyInvited);
                        DatabaseHelper.getInstance(getActivity()).getUctInstance().updateForFunduUser(alreadyFunduUsers);
                        if(message.equalsIgnoreCase("Succussfully invited")){
                            Toast.makeText(activity, "Succussfully invited", Toast.LENGTH_SHORT).show();
                            if(activity != null){
                                activity.onBackPressed();
                                activity.onBackPressed();
                            }

                        }

                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
                @Override
                public void onInviteFriendsError(VolleyError error) {

                }
            });
            request.setData(selectedItems);
            request.start();
        }
        else{
            Toast.makeText(activity, "Please check Internet connection and try again.", Toast.LENGTH_SHORT).show();
        }

    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);

        if(resultCode== Activity.RESULT_OK && requestCode==1&& data!=null){
            Fog.d("   requestCode","requestCode"+data.getData());
            //getActivity().finish();
            if(activity != null)
                activity.onBackPressed();
            //getActivity().onBackPressed();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof HomeActivity){
            activity = (HomeActivity)context;
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();

        if(sentReceiver != null){
            activity.unregisterReceiver(sentReceiver);
            sentReceiver = null;
        }
        if(adapter != null){
            adapter.clearSelectedItems();
            adapter = null;
        }
        mListener = null;
        activity = null;
        if(mGetContactsTask != null && (mGetContactsTask.getStatus () ==  AsyncTask.Status.RUNNING ||
                mGetContactsTask.getStatus() == AsyncTask.Status.PENDING)){
            mGetContactsTask.cancel(true);
        }
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //ButterKnife.reset(this);
    }

    /**
     * AsyncTask to get unregistered and uninvited contacts for user to invite
     *
     */
    public class GetContactsTask extends AsyncTask<Object, Object, Object> {
        @Override
        protected Object doInBackground(Object... params) {

            contactItems = UserContactsTable.getContacts(getActivity(),1);/*getUnRegisteredContacts*/
            Collections.sort(contactItems, new Comparator<ContactItem>() {
                @Override
                public int compare(ContactItem lhs, ContactItem rhs) {
                    return lhs.getContactName().compareTo(rhs.getContactName());
                }
            });
            return null;
        }
        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            //This is to fix if the person hit back before the contact list is loaded
            if(getActivity() == null)
                return;
            if(contactItems != null && contactItems.size () > 0){
                if(adapter == null){
                    adapter = new InviteFriendsRecyclerViewAdapter(contactItems, mListener ,getActivity(), "Invite");
                    recyclerView.setAdapter(adapter);
                }
                else{
                    adapter.setContacts(contactItems);
                    recyclerView.setAdapter(adapter);
                }
            }

            else{
                recyclerView.setVisibility(View.GONE);
                textView_no.setVisibility(View.VISIBLE);
                sendButton.setVisibility(View.GONE);
            }
        }
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Fog.i("FragCreateList", "onCreateOptionsMenu called");
        super.onCreateOptionsMenu(menu, inflater);
        if (FunduUser.getContactId()!=null) {
            inflater.inflate(R.menu.menu_contacts_fragment, menu);
            final MenuItem searchItem = menu.findItem(R.id.action_search);
            final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            customizeSearchView(searchView);
            searchView.setOnQueryTextListener(this);
        } else {
        }
    }

    private void customizeSearchView(SearchView searchView) {
        searchView.setQueryHint(getString(R.string.contact_search_hint_text));
        View searchplate = searchView.findViewById(android.support.v7.appcompat.R.id.search_plate);
        //searchplate.setBackgroundResource(R.drawable.green_button_disable_background);
        EditText searchEditText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setHintTextColor(getActivity().getResources().getColor(R.color.warm_grey));
        searchEditText.setHint("Search Friend");
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/Muli-Regular.ttf");
        searchEditText.setTypeface(face);

        if(searchEditText != null){
            searchEditText.setTextColor(getResources().getColor(R.color.brownish_grey));
            searchEditText.setHintTextColor(getResources().getColor(R.color.warm_grey));
            searchEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
        }

    }


    @Override
    public void onResume() {
        super.onResume();

       /* intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.EXISTINGUSERDB);
        getActivity().registerReceiver(mUpdateExistingUserDBReceiver,intentFilter);
*/
    }

    private void filter(String query) {

        if(adapter == null){
            return;
        }

        final String lowerCaseQuery = query.toLowerCase();

        if(lowerCaseQuery == null){
            return;
        }
        else if( lowerCaseQuery.length() == 0){
            adapter.setContacts(contactItems);
            adapter.notifyDataSetChanged();
            return;
        }

        final List<ContactItem> filteredContactlList = new ArrayList<>();
        if (contactItems != null && contactItems.size() > 0) {
            for (ContactItem contactItem : contactItems) {
                final String name = contactItem.getContactName().toLowerCase();
                final String number = contactItem.getContactNumber().toLowerCase();

                if (name.startsWith(lowerCaseQuery) || number.startsWith(lowerCaseQuery)) {
                    filteredContactlList.add(contactItem);
                }
            }
            if(contactItems.size() > filteredContactlList.size()){
                //there are filtered contact items, so change the list
                adapter.setContacts(filteredContactlList);
            }
            else{
                //No change, so no need to do anything
                adapter.setContacts(contactItems);
            }
            adapter.notifyDataSetChanged();
        }

    }
    /**
     * OnQueryTextListener
     */

    @Override
    public boolean onQueryTextChange(String query) {
        // Here is where we are going to implement the filter logic
        if(activity == null){
            //Fragment is not attached
            return false;
        }
        filter(query);
        return false;
    }
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }


   /* BroadcastReceiver  mUpdateExistingUserDBReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Fog.d("onReceive","onReceive");

        }
    };

*/

    /**
     * Event Bus events handlers
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataUpdated(DataUpdated event) {
        DataUpdated.DataUpdatedType type =  event.type;
        if(type != null && type == DataUpdated.DataUpdatedType.UserContacts){
            if(mGetContactsTask != null && (mGetContactsTask.getStatus () ==  AsyncTask.Status.RUNNING ||
                    mGetContactsTask.getStatus() == AsyncTask.Status.PENDING)){
                mGetContactsTask.cancel ( true );
            }
            mGetContactsTask = new GetContactsTask();
            mGetContactsTask.execute();
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }



    private class SentReceiver extends BroadcastReceiver {
        ArrayList<ContactItem> selectedItems;
        SentReceiver(ArrayList<ContactItem> selectedItems){
            this.selectedItems = selectedItems;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Fog.d(TAG, "SMS sent intent received.");
            Toast toast = Toast.makeText(context,"Invitation Sent",Toast.LENGTH_SHORT);
            if(toast != null)
            {
                toast.show();
            }
            //Send request to update backend
            sendRequestForAppInvitation(selectedItems);
            if(activity != null)
                activity.onBackPressed();
        }
    }

    public void onSelectAllButtonClick(){

        if(!isFirstClick){
            isFirstClick=true;
//            textViewSelectAll.setText("Select All");
            adapter.selectAllInviteFriends(isFirstClick);
        }
        else{
            isFirstClick=false;
//            textViewSelectAll.setText("UnSelect All");
            adapter.selectAllInviteFriends(isFirstClick);
        }


    }






}
