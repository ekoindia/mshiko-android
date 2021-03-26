package in.co.eko.fundu.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.co.eko.fundu.R;
import in.co.eko.fundu.activities.GetCashFromContact;
import in.co.eko.fundu.adapters.InviteFriendsRecyclerViewAdapter;
import in.co.eko.fundu.database.tables.UserContactsTable;
import in.co.eko.fundu.event.ShowFragment;
import in.co.eko.fundu.models.ContactItem;
import in.co.eko.fundu.utils.Fog;
import in.co.eko.fundu.utils.FunduAnalytics;

import static in.co.eko.fundu.R.id.sendButton;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link ContactsNearBy#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactsNearBy extends Fragment {

    @BindView(R.id.list)
    RecyclerView recyclerView;
    @BindView(sendButton)
    Button proceedButton;
    @BindView(R.id.textView_message)
    TextView desc;
    @BindView(R.id.no_friends_on_fundu)
    View mNoFunduFriends;
    @BindView(R.id.invite_friends)
    View mInviteFriends;

    private AsyncTask mGetContactsTask;

    private ArrayList<ContactItem> contactItems;


    private GetCashFromContact activity;
    private InviteFriendsRecyclerViewAdapter adapter;
    private InviteFriendsRecyclerViewAdapter.OnListFragmentInteractionListener mListener;

    public ContactsNearBy() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ContactsNearBy.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactsNearBy newInstance() {
        ContactsNearBy fragment = new ContactsNearBy();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FunduAnalytics.getInstance(getActivity()).sendScreenName("ContactsNearBy");
        View view = inflater.inflate(R.layout.fragment_contacts_near_by, container, false);
        ButterKnife.bind(this, view);
        initViews();
        return view;
    }

    private void initViews(){
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //Find contacts and populate the list in background
        if(mGetContactsTask != null && (mGetContactsTask.getStatus () ==  AsyncTask.Status.RUNNING ||
                mGetContactsTask.getStatus() == AsyncTask.Status.PENDING)){
            mGetContactsTask.cancel(true);
        }
        mGetContactsTask = new ContactsNearBy.GetNearByContactsTask();
        mGetContactsTask.execute();
        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //request cash from selected contacts
                ArrayList<ContactItem> selectedItems = adapter.getSelectedItems();
                //Check and then requestCash
                if(selectedItems.size() > 0){
                    requestCashFromSelected(selectedItems.get(0));
                }


            }
        });
        proceedButton.setVisibility(View.GONE);
        mListener = new InviteFriendsRecyclerViewAdapter.OnListFragmentInteractionListener() {
            @Override
            public void onListFragmentInteraction(ContactItem item) {
                //show enter amount screen
                requestCashFromSelected(item);

            }
            @Override
            public void changeSendVisibility(int visibility) {
               /* if(visibility == View.VISIBLE)
                    Animate.bubble(sendButton);
                else
                    Animate.reverseBubble(sendButton,0);*/
            }
        };
        desc.setText(getString(R.string.select_friends_desc));
        activity = (GetCashFromContact) getActivity();
        mInviteFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = getActivity();
                activity.finish();
                EventBus.getDefault().post(new ShowFragment(ContactsFragment.class));
            }
        });

    }

    private void requestCashFromSelected(ContactItem contactItem){
        activity.setSelectedContact(contactItem);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(mGetContactsTask != null && (mGetContactsTask.getStatus () ==  AsyncTask.Status.RUNNING ||
                mGetContactsTask.getStatus() == AsyncTask.Status.PENDING)){
            mGetContactsTask.cancel(true);
        }
        mListener = null;
    }

    /**
     * AsyncTask to get unregistered and uninvited contacts for user to invite
     *
     */
    public class GetNearByContactsTask extends AsyncTask<Object, Object, Object> {
        @Override
        protected Object doInBackground(Object... params) {

            contactItems = UserContactsTable.getContacts(getActivity(),0);/*getRegisteredContacts*/
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
            if(getActivity () == null)
                return;
            Fog.d("contactItems",""+contactItems.size());
            if(contactItems.size() != 0){
                for(int i=0;i<contactItems.size();i++){
                    if(contactItems.get(i).isAddedInNetwork()){
                        contactItems.remove(i);
                    }
                }
                if(adapter == null){
                    adapter = new InviteFriendsRecyclerViewAdapter(contactItems, mListener ,getActivity(),"ContactsNearby");
                    adapter.setmTapSelection(true);
                    recyclerView.setAdapter(adapter);
                    adapter.setAllowedSelection(1);
                }
                else{
                    adapter.setContacts(contactItems);
                    adapter.notifyDataSetChanged();
                    adapter.setAllowedSelection(1);
                }
            }

            else{
                recyclerView.setVisibility(View.GONE);
                mNoFunduFriends.setVisibility(View.VISIBLE);
                proceedButton.setVisibility(View.GONE);
                desc.setVisibility(View.GONE);
            }
        }
    }
}
