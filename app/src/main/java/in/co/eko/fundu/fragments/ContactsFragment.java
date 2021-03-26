package in.co.eko.fundu.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import in.co.eko.fundu.R;
import in.co.eko.fundu.activities.HomeActivity;
import in.co.eko.fundu.adapters.EarnRffralAdapter;
import in.co.eko.fundu.adapters.SimpleAdapter;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.database.tables.UserContactsTable;
import in.co.eko.fundu.event.ReplaceFragment;
import in.co.eko.fundu.interfaces.InviteFragmentListener;
import in.co.eko.fundu.interfaces.NeedCash;
import in.co.eko.fundu.interfaces.OnFragmentInteractionListener;
import in.co.eko.fundu.models.ContactItem;
import in.co.eko.fundu.models.ContactsNearByModel;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.services.NearByContactsService;
import in.co.eko.fundu.utils.AppPreferences;
import in.co.eko.fundu.utils.Fog;
import in.co.eko.fundu.utils.Utils;



public class ContactsFragment extends BaseFragment implements /*NearByContactsRequest.OnNearByContactsResults,*/
        View.OnClickListener, SearchView.OnQueryTextListener, RuntimePermissionHeadlessFragment.CallLogPermissionCallback {
    private String TAG = ContactsFragment.class.getName();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    //ArrayList<CallLogsModel> callLogs;
    private Button btn_invite_freinds;
    private String mParam1;
    private String mParam2;
    int sizeOfFinalList=0;
    private OnFragmentInteractionListener onFragmentInteractionListener;
    private InviteFragmentListener inviteFragmentListener;
    //private ContactsRecyclerAdapter mAdapter;
    private TextView textView_referral_title,info,noData,textviewAlreadyExixtinPeople,textView_skip,
            textViewReffralPoint1,textViewReffralPoint2,textViewReffralPoint3,textViewReffralPoint4;
    EarnRffralAdapter earnRffralAdapter;
    private SimpleAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayout linearLayout_reffral,linearLayout_button;
    private List<ContactItem> contactItems = new ArrayList<>();
    private AppPreferences preferences;
    private ArrayList<ContactsNearByModel> contactsNearByModels = new ArrayList<>();
    private int nearByContactsCount = 0;
    protected NeedCash needCash;
    private View view;
    private boolean callLogPermissionGranted = false;
    private ArrayList<ContactItem> contacts = new ArrayList<>();
    boolean loaded = true;
    private ArrayList<ContactItem> registeredContactItems = new ArrayList<>();

    public static ContactsFragment newInstance(String param1, String param2) {
        ContactsFragment fragment = new ContactsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        // view = inflater.inflate(R.layout.fragment_contacts, container, false);
        view = inflater.inflate(R.layout.fragment_invite_referral, container, false);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        needCash = (NeedCash) activity;
//        inviteFragmentListener = (InviteFragmentListener)getActivity();
//        RuntimePermissionHeadlessFragment runtimePermissionHeadlessFragment = RuntimePermissionHeadlessFragment.newInstance(this);
//        getChildFragmentManager().beginTransaction().add(runtimePermissionHeadlessFragment, runtimePermissionHeadlessFragment.getClass().getName()).commit();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(updateContactReceiver, new IntentFilter(Constants.UPDATE_CONTACT_ACTION));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        InitViews();
    }

    private void InitViews() {
        recyclerView = (RecyclerView) getView().findViewById(R.id.RecyclerView);
        // Layout Managers:
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL, false));
        textviewAlreadyExixtinPeople = (TextView) getView().findViewById(R.id.textview_already_exixting_people);
        noData = (TextView) getView().findViewById(R.id.noData);
        textView_referral_title = (TextView) getView().findViewById(R.id.textView_referral_title);
        textViewReffralPoint1 = (TextView) getView().findViewById(R.id.textView_reffral_point1);
        textViewReffralPoint2 = (TextView) getView().findViewById(R.id.textView_reffral_point2);
        textViewReffralPoint3 = (TextView) getView().findViewById(R.id.textView_reffral_point3);
        textViewReffralPoint4 = (TextView) getView().findViewById(R.id.textView_reffral_point4);
        textView_skip = (TextView) getView().findViewById(R.id.textView_skip);
        info = (TextView) getView().findViewById(R.id.info);
        btn_invite_freinds = (Button) getView().findViewById(R.id.btn_invite_freinds);
        linearLayout_reffral = (LinearLayout)getView().findViewById(R.id.linearLayout_reffral);
        linearLayout_button = (LinearLayout)getView().findViewById(R.id.linearLayout_button);

        InitNearByContact();
        updateEmptyView();
        updateReffralPoints();
        btn_invite_freinds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new ReplaceFragment(InviteFriends.class, ContactsFragment.class));
            }
        });

        textView_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fog.d("add","skip");
                HomeActivity homeActivity=(HomeActivity) getActivity();
                if(homeActivity != null){
                    homeActivity.onBackPressed();
                }
//                MapFragment mapFragment = new MapFragment();
//                homeActivity.pushFragment(mapFragment);

                //getActivity().finish();
                // getActivity().onBackPressed();
            }
        });

        // ContactsUtils.getInstance().getCallLogs(getActivity());
    }

    private void updateReffralPoints() {

        textViewReffralPoint1.setText(R.string.reffral_point1);
        String a = "Get ";
        String b = "";
        String c = " for every friend who signs up. Even if they don’t use your sign up link!";
        if(FunduUser.getCountryShortName().equalsIgnoreCase("KEN")){
            b = "Shs.10" ;
        }
        else b = "Rs.10 ";

        textViewReffralPoint2.setVisibility(View.GONE);
        textViewReffralPoint3.setText(R.string.reffral_point2);
        textViewReffralPoint4.setText(R.string.reffral_point3);

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions, @NonNull int[] grantResults) {
        boolean requiredPermissionsGiven = true;
        for (int i = 0; i < grantResults.length; i++) {

            if(grantResults[i]== PackageManager.PERMISSION_DENIED){
                requiredPermissionsGiven = false;
                break;
            }
        }
        if(requiredPermissionsGiven) {
            Fog.i(TAG, "onRequestPermissionsResult all required Permissions given");
            view.findViewById(R.id.nopermissionll).setVisibility(View.GONE);
            view.findViewById(R.id.invite_referral_sv).setVisibility(View.VISIBLE);
        }
    }

    private void checkIfUserHasGivenPermission(){
        if(Utils.checkPermission(getActivity(), Manifest.permission.READ_CONTACTS)){
            view.findViewById(R.id.nopermissionll).setVisibility(View.GONE);
            view.findViewById(R.id.invite_referral_sv).setVisibility(View.VISIBLE);

        }
        else{
            view.findViewById(R.id.nopermissionll).setVisibility(View.VISIBLE);
            view.findViewById(R.id.invite_referral_sv).setVisibility(View.GONE);
            Button givenow = (Button)view.findViewById(R.id.givenow);
            givenow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HomeActivity homeActivity = (HomeActivity)getActivity();
                    homeActivity.checkAndRequestPermissionsn();
                }
            });
        }
    }

    private void checkUserLoggedIn() {

        preferences = new AppPreferences(getActivity());
        if (FunduUser.getContactId()!=null) {
            linearLayout_reffral.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);

            addMeInArrayList();
            checkIfUserHasGivenPermission();
        } else {
            linearLayout_reffral.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);

            linearLayout_button.setVisibility(View.GONE);
            textView_referral_title.setVisibility(View.GONE);

        }
    }

    private void InitNearByContact() {
        //This is the code to provide a sectioned grid

        adapter = new SimpleAdapter(getActivity(), contactItems);
        //  earnRffralAdapter = new EarnRffralAdapter(getActivity(),contactItems);

        ArrayList<ContactItem> finalList = new ArrayList<>();
        for(int i=0;i<registeredContactItems.size();i++){
            if(!finalList.contains(registeredContactItems.get(i))){
                finalList.add(registeredContactItems.get(i));
            }
        }
         sizeOfFinalList = finalList.size();
        earnRffralAdapter = new EarnRffralAdapter(getActivity(),finalList);
        recyclerView.setAdapter(earnRffralAdapter);
        textviewAlreadyExixtinPeople.setText(finalList.size()+" "+getActivity().getResources().getString(R.string.peopleAlreadyExists));
       /* mSectionedAdapter = new
                SectionedGridRecyclerViewAdapter(getActivity(), R.layout.sticky_header, R.id.title, recyclerView, adapter);
        recyclerView.setAdapter(mSectionedAdapter);
  */      // sections.add(new SectionedGridRecyclerViewAdapter.Section(0, getString(R.string.me)));

    }

    private void setNearByData() {
//        if (Utils.isNetworkAvailable(getActivity()))
        new GetContactsTask().execute();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    private void addMeInArrayList() {

        ContactItem contactItem = new ContactItem(FunduUser.getContactId(), FunduUser.getContactId(),
                FunduUser.getAppPreferences().getString(Constants.PROFILE_PIC_URL),"");
        contactItem.setIsUnregisterd(0);
        Fog.e("AddMe", contactItems.size()+"");
        if (contactItems.size()>0){
            if (contactItems.get(0).getContactNumber().equalsIgnoreCase(contactItem.getContactNumber())){

            }
            else{
                contactItems.add(0, contactItem);
            }
        }
        else
            contactItems.add(0, contactItem);
    }

    private void getRegisteredNearbyContacts(ArrayList<ContactsNearByModel> contactsNearByModels,
                                             ArrayList<ContactItem> registeredContacts) {

        boolean foundInRegisteredContacts;
        for (Iterator<ContactsNearByModel> iterator = contactsNearByModels.iterator(); iterator.hasNext(); ) {
            foundInRegisteredContacts = false;
            ContactsNearByModel contactsNearByModel = iterator.next();
            for (ContactItem contactItem : registeredContacts) {
//                Fog.e("REG NO ", contactItem.getContactNumber());
                String number = contactsNearByModel.getCustomer_id().split(":")[1];
                if (contactItem.getContactNumber().contains(number)) {
                    contactItem.setIsUnregisterd(0);
                    UserContactsTable.updateContactRegisterStatus(getActivity(), contactItem.getContactNumber(), 0);
                    contactItems.add(1, contactItem);
                    registeredContacts.add(1,contactItem);
                    contactItem.setDistance(contactsNearByModel.getDistance());
                    contactItem.setDistanceInTime(contactsNearByModel.getDistanceInTime());
                    contactItem.setCustomer_id(contactsNearByModel.getCustomer_id());
                    foundInRegisteredContacts = true;
                    break;
                }
            }
            if (!foundInRegisteredContacts)
                iterator.remove();
        }
    }

    private void updateEmptyView() {
        /*if (contactItems.size() == 0) {
            tv_empty_view.setVisibility(View.VISIBLE);
            textView_referral_title.setVisibility(View.GONE);
            linearLayout_button.setVisibility(View.GONE);

        }*/

        Fog.d("registeredContactItems","size"+registeredContactItems.size());
        if (registeredContactItems.size() == 0) {
            // tv_empty_view.setVisibility(View.VISIBLE);
            noData.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            info.setVisibility(View.GONE);
            textviewAlreadyExixtinPeople.setVisibility(View.GONE);
           /* textView_referral_title.setVisibility(View.GONE);
            linearLayout_button.setVisibility(View.GONE);
            linearLayout_reffral.setVisibility(View.GONE);*/

        }



        else {
            textviewAlreadyExixtinPeople.setVisibility(View.VISIBLE);
            noData.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            info.setVisibility(View.VISIBLE);

            /*textView_referral_title.setVisibility(View.VISIBLE);
            linearLayout_button.setVisibility(View.VISIBLE);
            linearLayout_reffral.setVisibility(View.VISIBLE);
            textView_referral_title.setVisibility(View.VISIBLE);*/
        }
    }

    private void updateNearByListData() {
        nearByContactsCount = contactsNearByModels.size();
        contactsNearByModels = NearByContactsService.contactsNearByModels;

        if (contactItems.size() > 1){
            for (int i = 0; i < nearByContactsCount; i++)
                contactItems.remove(1);
        }

        if (registeredContactItems.size() > 1){
            for (int i = 0; i < nearByContactsCount; i++)
                registeredContactItems.remove(1);
        }






    }

    @Override
    public void onStop() {
        super.onStop();
        Fog.i("Stop Contact","STOPPED");
        shutDownScheduler();
    }

    public void scheduleUpdateNearByContacts() {
        if (FunduUser.getAppPreferences().getBoolean(Constants.IS_USER_LOGGED_IN, false) && isAdded())
            NearByContactsService.getInstance().setHandler(handler);
    }

    public void shutDownScheduler() {
        NearByContactsService.getInstance().setHandler(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        shutDownScheduler();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            callsetNearByData();
        }
    };
    private void callsetNearByData(){
        Thread splashTread = new Thread() {
            @Override
            public void run() {
                try {

                    sleep(1);
                } catch (InterruptedException e) {
                    // do no  thing
                } finally {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            setNearByData();

                        }
                    });

                }
            }
        };
        splashTread.start();
    }
    @Override
    public void onResume() {
        super.onResume();
        checkUserLoggedIn();
        Utils.hideSoftKeyboard(getActivity());
        if (FunduUser.getContactId()!=null)
        {
            //callLogs = ContactsUtils.getInstance(getActivity()).getCallLogs(getActivity());
            scheduleUpdateNearByContacts();
        }
        preferences = new AppPreferences(getActivity());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Fog.i("FragCreateList", "onCreateOptionsMenu called");
        super.onCreateOptionsMenu(menu, inflater);
        checkUserLoggedIn();

        if (FunduUser.getContactId()!=null) {
            inflater.inflate(R.menu.menu_contacts_fragment, menu);
            final MenuItem searchItem = menu.findItem(R.id.action_search);
            final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            customizeSearchView(searchView);
            searchView.setOnQueryTextListener(this);
        } else {
        }
    }
    public class GetContactsTask extends AsyncTask<Object, Object, Object> {
        @Override
        protected Object doInBackground(Object... params) {

            updateNearByListData();
            ArrayList<ContactItem> registeredContacts = UserContactsTable.getContacts(getActivity(),0);/*getFunduRegisteredContacts*/
            registeredContactItems = registeredContacts;
            Fog.d("fundu","fundu registered contacts"+registeredContacts.size());
            Collections.sort(registeredContacts, new Comparator<ContactItem>() {
                @Override
                public int compare(ContactItem lhs, ContactItem rhs) {
                    return lhs.getContactName().compareTo  (rhs.getContactName());
                }
            });
            Collections.sort(registeredContactItems, new Comparator<ContactItem>() {
                @Override
                public int compare(ContactItem lhs, ContactItem rhs) {
                    return lhs.getContactName().compareTo  (rhs.getContactName());
                }
            });

            Fog.e("Get Con Count", ""+registeredContacts.size());
            getRegisteredNearbyContacts(contactsNearByModels, registeredContacts);
            for(int i=0;i<registeredContacts.size();i++){
                Fog.d("registeredContacts",""+registeredContacts.get(i));
            }

            if (loaded && registeredContacts.size() > 0) {
                contacts = registeredContacts;/*UserContactsTable.getContacts(getActivity())*/
                contactItems.addAll(registeredContacts);
                loaded = false;
            }
            return null;

        }



        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            //This is to fix if the person hit back before the contact list is loaded
            if(getActivity() == null)
                return;
            updateEmptyView();
            info.setText(UserContactsTable.getContactsCount(getActivity(),1)+" people from your contacts can be invited to Fundu");

            // SectionedGridRecyclerViewAdapter.Section[] dummy = new SectionedGridRecyclerViewAdapter.Section[sections.size()];
//            mSectionedAdapter.setSections(sections.toArray(dummy));
            //adapter.setContactList(contactItems, false,inviteFragmentListener);
            ArrayList<ContactItem> finalList = new ArrayList<>();

            for(int i=0;i<registeredContactItems.size();i++){
                if(!finalList.contains(registeredContactItems.get(i))){
                    finalList.add(registeredContactItems.get(i));
                }
            }
            textviewAlreadyExixtinPeople.setText(finalList.size()+" "+getActivity().getResources().getString(R.string.peopleAlreadyExists));
            earnRffralAdapter.setContactList(finalList, false,inviteFragmentListener);
            earnRffralAdapter.notifyDataSetChanged();
            //adapter.notifyDataSetChanged();

        }
    }
    @Override
    public void onPause() {
        super.onPause();
        Fog.i("Stop Contact","Paused");
    }


    private BroadcastReceiver updateContactReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String number = intent.getStringExtra(Constants.RECIPIENT_NUMBER);
            for (int i = 0; i < contactItems.size(); i++) {
                ContactItem contactItem = contactItems.get(i);
                if (contactItem.getContactNumber().equalsIgnoreCase(number)) {
                    contactItems.get(i).setIsUnregisterd(0);
                    break;
                }
            }
            adapter.setContactList(contactItems, false,inviteFragmentListener);
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onDetach() {
        super.onDetach();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(updateContactReceiver);
    }

    @Override
    public void onCallLogPermissionGranted() {
        callLogPermissionGranted = true;
    }

    @Override
    public void onCallLogPermissionDenied() {

    }
    private void customizeSearchView(SearchView searchView) {
        searchView.setQueryHint(getString(R.string.contact_search_hint_text));
        View searchplate = searchView.findViewById(android.support.v7.appcompat.R.id.search_plate);
        searchplate.setBackgroundResource(R.drawable.green_button_disable_background);
        EditText searchEditText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        if(searchEditText != null){
            searchEditText.setTextColor(getResources().getColor(R.color.White));
            searchEditText.setHintTextColor(getResources().getColor(R.color.White));
            searchEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
        }

    }

    @Override
    public boolean onQueryTextChange(String query) {
        // Here is where we are going to implement the filter logic
        filterList(query);
        return false;
    }

    private void filterList(String query) {
        final List<ContactItem> contactItemList = filter(query.length() > 0 ? contacts : contactItems, query);
        if (contactItemList.size() == contactItems.size())
            adapter.setContactList(contactItemList, false,inviteFragmentListener);
        else
            adapter.setContactList(contactItemList, true,inviteFragmentListener);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private static List<ContactItem> filter(List<ContactItem> models, String query) {
        final String lowerCaseQuery = query.toLowerCase();

        final List<ContactItem> filteredModelList = new ArrayList<>();
        if (models.size() > 0) {
            for (ContactItem model : models) {
                final String name = model.getContactName().toLowerCase();
                final String number = model.getContactNumber().toLowerCase();

                if (name.startsWith(lowerCaseQuery) || number.startsWith(lowerCaseQuery)) {
                    filteredModelList.add(model);
                }
            }
        }
        return filteredModelList;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fog.d("contact fragment","tab fragment");
        Fog.d("requestCode", "Contact onActivityResult:"+data.getData());

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onButtonPressed(Bundle bundle) {
        super.onButtonPressed(bundle);
        getFragmentManager().popBackStackImmediate();
    }
    @Override
    public boolean onBackPressed() {
        return false;
    }
}