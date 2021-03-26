package in.co.eko.fundu.fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import in.co.eko.fundu.R;
import in.co.eko.fundu.activities.HomeActivity;
import in.co.eko.fundu.adapters.CalendarAdapter;
import in.co.eko.fundu.database.tables.UserContactsTable;
import in.co.eko.fundu.interfaces.OnClickNeedHelp;
import in.co.eko.fundu.interfaces.OnFragmentInteractionListener;
import in.co.eko.fundu.models.ContactItem;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.models.TransactionHistoryModel;
import in.co.eko.fundu.requests.TransactionHistoryRequest;
import in.co.eko.fundu.utils.Fog;
import in.co.eko.fundu.utils.Utils;


public class CalendarFragment extends BaseFragment implements TransactionHistoryRequest.OnTransferHistoryResult,OnClickNeedHelp {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TransactionHistoryRequest request;
    private String mParam1;
    private String mParam2;
    public static int start = 0, end = 30;
    String[] adapterData;
    private OnFragmentInteractionListener onFragmentInteractionListener;
    private RelativeLayout relativeLayout;
    CalendarAdapter mAdapter;
    ArrayList<TransactionHistoryModel> listHistory = new ArrayList<>();
    ArrayList<TransactionHistoryModel> listHistorysecondary = new ArrayList<>();
    private static LinearLayoutManager mLayoutManager;
    private TextView errorMsg;
    private ImageView imageviewBack;
    public boolean loaded = false;
    private boolean userScrolled = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    ArrayList<ContactItem> registeredContacts;
    static RecyclerView recyclerView;

    private ProgressDialog progressDialog;

    public static CalendarFragment newInstance(String param1, String param2) {
        CalendarFragment fragment = new CalendarFragment();
        Bundle args = new Bundle();

        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public CalendarFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        dialog = new CustomProgressDialog(getActivity());
        registeredContacts = UserContactsTable.getContacts(getActivity());/*getRegisteredContacts*/

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            request = new TransactionHistoryRequest(getActivity());
            request.setParserCallback(this);

        }
        callHistoryApi(0,getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!(FunduUser.isUserLoginorRegister())) {
//            if (!(FunduUser.getCountryShortName().equalsIgnoreCase("KEN"))) {
            errorMsg.setText(getString(R.string.no_record_found));
            errorMsg.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
//            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent, container, false);
        errorMsg = (TextView) view.findViewById(R.id.error_msg);
        imageviewBack = (ImageView) view.findViewById(R.id.imageview_back);
        relativeLayout = (RelativeLayout) view.findViewById(R.id.relativeLayout);
        return view;

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Please wait");
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        if(activity instanceof HomeActivity)
            ((HomeActivity)activity).hideHamburgerIcon();
    }
    public void callHistoryApi(int i, Context context){

        if (Utils.isNetworkAvailable(context)) {
                progressDialog.show();
                Fog.d("values",""+start+end+loaded);
                request = new TransactionHistoryRequest(getActivity());
                request.setParserCallback(this);
                if(FunduUser.getCountryShortName().equalsIgnoreCase("KEN"))
                    request.setData(FunduUser.getCountryShortName(), FunduUser.getCustomerId(), String.valueOf(i), String.valueOf(start+end));
                else
                    request.setData(FunduUser.getCountryShortName(), FunduUser.getContactId(), String.valueOf(i), String.valueOf(start+end));
                // request.setData(FunduUser.getCountryShortName(), FunduUser.getCustomerId(), "0", "30");
                request.start();
            }

    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.RecyclerView);
//        callHistoryApi(0,getActivity());
       mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setRefreshing(false);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
               if (FunduUser.getCountryShortName().equalsIgnoreCase("KEN")) {
                   if (Utils.isNetworkAvailable(getActivity())) {
                       start = 0;
                       listHistorysecondary = listHistory;
                       Fog.e("HistSec swipe", "" + listHistorysecondary.size());
                       callHistoryApi(0,getActivity());
                   } else
                       mSwipeRefreshLayout.setRefreshing(false);
               }
               else{
                   mSwipeRefreshLayout.setRefreshing(false);
                   //TODO: refresh for first
                   if(listHistory.size() == 0){
                       callHistoryApi(0,getActivity());
                   }

               }
            }
        });
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new CalendarAdapter(getActivity(), listHistory,onFragmentInteractionListener);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Fog.e("New State", ""+newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    userScrolled = true;
                }
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy > 0) //check for scroll down
                {
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = listHistory.size();
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();
                    start = totalItemCount+1;
                    Fog.e("RECYCLE ", "VISIBLE " + visibleItemCount + " TOTAL " + totalItemCount + " PASTVIS " + pastVisiblesItems + " TF " + userScrolled);
                    // Now check if userScrolled is true and also check if
                    // the item is end then update recycler view and set
                    // userScrolled to false
                    if (userScrolled && (visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        userScrolled = false;
                        listHistorysecondary = listHistory;
                        Fog.e("HistSec scroll", ""+listHistorysecondary.size());

                        Fog.e("CALL API", "TRUE");
                        //callHistoryApi(0,getActivity());
                        callHistoryApi(start,getActivity());
                    }
                }
            }
        });

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
//                headersDecor.invalidateHeaders();
            }
        });


        imageviewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                 getActivity().onBackPressed();
                 //inviteFriendListener.onSkip();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        Fog.i("Stop Calendar","Paused");
    }

    @Override
    public void onStop() {
        super.onStop();
        Fog.i("Stop Calender","STOPPED");
        loaded = false;
    }

    @Override
    public void onTransferHistroryResults(String response) {

        Fog.e("THR Response", response);
        mSwipeRefreshLayout.setRefreshing(false);
        if (response!=null){
            try {
                JSONObject object = new JSONObject(response);
                if (object.has("status")){
                    if (object.getString("status").equalsIgnoreCase("SUCCESS")){
                        JSONArray jary = object.getJSONArray("historyData");
                        Gson gson = new Gson();
//                        ArrayList<TransactionHistoryModel> contactResponseItems = UniversalParser.getInstance().parseJsonArrayWithJsonObject(jary, TransactionHistoryModel.class);
                        TransactionHistoryModel[] arr = gson.fromJson(jary.toString(), TransactionHistoryModel[].class);
                        List<TransactionHistoryModel> contactResponseItems = Arrays.asList(arr);
                        if(listHistory.size() > 0){
                            for(TransactionHistoryModel item : contactResponseItems){
                                if(!listHistory.contains(item))
                                    listHistory.add(item);
                            }
                        }
                        else{
                            listHistory.addAll(contactResponseItems);
                        }
                        if (listHistory.size()>0 || listHistorysecondary.size()>0) {
                            recyclerView.setVisibility(View.VISIBLE);
                            errorMsg.setVisibility(View.GONE);
                        }
                        else{
                            recyclerView.setVisibility(View.GONE);
                            errorMsg.setText(getString(R.string.no_record_found));
                            errorMsg.setVisibility(View.VISIBLE);
                        }
                    mAdapter.notifyDataSetChanged();
                    }

                    else{
                        if (listHistory.size()>0 || listHistorysecondary.size()>0) {
                        }
                        else{
                            recyclerView.setVisibility(View.GONE);
                            errorMsg.setText(getString(R.string.no_record_found));
                            errorMsg.setVisibility(View.VISIBLE);
                        }
                        Fog.e("THR Cust", object.getString("message"));
//                        if (object.getString("message").equalsIgnoreCase("Customer doesn't exist")) {
//                            Utils.showShortToast(getActivity(), object.getString("message"));
//                            HomeActivity.Signout(getContext());
//                        }
                    }

                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        progressDialog.dismiss();
    }

    @Override
    public void onTransferHistoryError(VolleyError error)
    {
        progressDialog.dismiss();
        mSwipeRefreshLayout.setRefreshing(false);
        Toast.makeText(getContext(), "Server error occurred, Please try later", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onFragmentInteractionListener = (OnFragmentInteractionListener) context;
    }

    @Override
    public void OnClickNeedHelp() {
        getChildFragmentManager().beginTransaction().add(R.id.relativeLayout, new SupportFragment()).addToBackStack(null).commit();

    }
    @Override
    public boolean onBackPressed() {
        return false;
    }
}
