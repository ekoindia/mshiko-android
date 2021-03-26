package in.co.eko.fundu.adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.R;
import in.co.eko.fundu.constants.API;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.event.ReplaceFragment;
import in.co.eko.fundu.fragments.ContactsFragment;
import in.co.eko.fundu.fragments.InviteFriends;
import in.co.eko.fundu.interfaces.InviteFragmentListener;
import in.co.eko.fundu.models.ContactItem;
import in.co.eko.fundu.requests.CallWebService;
import in.co.eko.fundu.utils.Fog;
import in.co.eko.fundu.utils.Utils;
import in.co.eko.fundu.views.ColorCircleDrawable;

public class SimpleAdapter extends RecyclerSwipeAdapter<SimpleAdapter.ViewHolder>
        implements View.OnClickListener, CallWebService.ObjectResponseCallBack {
    private static final int COUNT = 100;

    private final Context mContext;
    protected List<ContactItem> mDataset;
    private int inviteFriendPos;
    boolean search = false;
    InviteFragmentListener inviteFragmentListener;
    private int colorArray[] = null;

    public void setContactList(List<ContactItem> contactItems, boolean search,InviteFragmentListener inviteFragmentListener) {
        mDataset = contactItems;
        this.search = search;
        this.inviteFragmentListener = inviteFragmentListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        SwipeLayout swipeLayout;
        ImageView user_image;
        TextView textViewData;
        ImageView addFriendIV;
        TextView subtitle;
        TextView invite;
        public final TextView userInitial;
        LinearLayout rowContainer;

        public ViewHolder(View itemView) {
            super(itemView);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            user_image = (ImageView) itemView.findViewById(R.id.user_image);
            textViewData = (TextView) itemView.findViewById(R.id.user_name);
            addFriendIV = (ImageView) itemView.findViewById(R.id.addFriendIV);
            rowContainer = (LinearLayout) itemView.findViewById(R.id.rowContainer);
            subtitle = (TextView) itemView.findViewById(R.id.subtitle);
            invite = (TextView) itemView.findViewById(R.id.invite_friends);
            userInitial = (TextView)itemView.findViewById(R.id.user_initial);
            if(invite != null)
                 invite.setOnClickListener(SimpleAdapter.this);
            addFriendIV.setOnClickListener(SimpleAdapter.this);
            itemView.setOnClickListener(SimpleAdapter.this);
        }
    }

    public SimpleAdapter(Context context, List<ContactItem> objects) {
        mContext = context;
        this.mDataset = objects;
        TypedArray ta = context.getResources().obtainTypedArray(R.array.contacts_color);
        colorArray = new int[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            colorArray[i] = ta.getColor(i, 0);
        }
        ta.recycle();
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType) {
            case 0:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_contact, parent, false);
                break;
            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_contact_non_swipe, parent, false);
                break;
        }

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        ContactItem contactItem = mDataset.get(position);
        Fog.d("Fundu","position"+position);
        Fog.d("Fundu","Fundu registered user"+contactItem.getIsUnregisterd());
        Fog.d("Fundu","Fundu registered user"+contactItem.isUnregisterd());
        String item = contactItem.getContactName();
        hideOrShowAddFriendIcon(holder, contactItem);

        if (position == 0 && !search){
            contactItem.setContactName(FunduUser.getFullName());
            holder.textViewData.setText(FunduUser.getFullName());
            if(holder.invite != null)
                holder.invite.setVisibility(View.VISIBLE);
        }
        else{
            holder.textViewData.setText(item);
            if(holder.invite != null)
                holder.invite.setVisibility(View.GONE);

        }
        setUserImage(holder, contactItem,position);
        setDistanceInTime(holder, contactItem);
        holder.addFriendIV.setTag(position);
        holder.itemView.setTag(position);
        if (holder.swipeLayout != null) {
            mItemManger.bindView(holder.itemView, position);
            holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        }



    }

    private void hideOrShowAddFriendIcon(ViewHolder holder, ContactItem contactItem) {
        if (contactItem.isUnregisterd() == 1)
        {
            //Not registered, so make it visible
            //Edit: We dont need this anymore
            holder.addFriendIV.setVisibility(View.GONE);
        }
        else
            holder.addFriendIV.setVisibility(View.GONE);
    }

    private void setDistanceInTime(ViewHolder holder, ContactItem contactItem) {
        if (contactItem.getCustomer_id() != null) {
            String time = "";
            double minutes = ((contactItem.getDistanceInTime() % 3600) / 60);
            if (minutes >= 1)
                time = String.format("%.2f", minutes) + " " + mContext.getString(R.string.from_you);
            else
                time = mContext.getString(R.string.less_than_minute) + " " + mContext.getString(R.string.from_you);
            holder.subtitle.setText(time);
        } else
            holder.subtitle.setText(contactItem.getContactNumber());

//            holder.subtitle.setText("");
    }

    private void setUserImage(ViewHolder holder, ContactItem contactItem,int position) {
        if (!contactItem.getContactImage().isEmpty()) {
            holder.userInitial.setVisibility(View.GONE);
            holder.user_image.setVisibility(View.VISIBLE);
            try {
                Picasso.with(mContext).load(contactItem.getContactImage()).placeholder(R.drawable.ic_user_image).into(holder.user_image);
            } catch (IllegalArgumentException e){
                holder.user_image.setImageURI(Uri.parse(contactItem.getContactImage()));
//                Picasso.with(mContext).load(R.drawable.user_image).placeholder(R.drawable.user_image).into(holder.profile);
            }
        }else
        {
            //if name is available then show the initial
            String name  = contactItem.getContactName();
            if(name != null && name.length()>=1 && name.matches("^[^\\d].*")){
                name = name.substring(0,1);
                holder.userInitial.setText(name);
                holder.userInitial.setVisibility(View.VISIBLE);
                holder.user_image.setVisibility(View.GONE);
                setBackground(holder.userInitial,position);

            }
            else{
                holder.user_image.setImageResource(R.drawable.user_image_white);
                holder.user_image.setBackgroundResource(R.drawable.circular_backgrond);
                holder.userInitial.setVisibility(View.GONE);
                holder.user_image.setVisibility(View.VISIBLE);
                setBackground(holder.user_image,position);

            }
        }
    }
    private void setBackground(View view,int position){
        int color = ContextCompat.getColor(mContext,R.color.colorPrimary);
        try{
            color = colorArray[position%colorArray.length];

        }catch (Exception e){
            e.printStackTrace();
        }

        int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk <= android.os.Build.VERSION_CODES.JELLY_BEAN) {

            view.setBackgroundDrawable(new ColorCircleDrawable(color));
        } else {
            view.setBackground(new ColorCircleDrawable(color));
        }
    }


    public void removeItem(int position) {
        mDataset.remove(position);
        notifyItemRemoved(position);
    }
    public void notifydata(){
        notifyDataSetChanged();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void addItem(int pos, ContactItem contactItem) {
        mDataset.add(pos, contactItem);
        notifyItemInserted(pos);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rowContainer:
                if (Utils.isNetworkAvailable(mContext)) {
                    int position = (Integer) v.getTag();
                    ContactItem contactItem = mDataset.get(position);
                    if (contactItem.isUnregisterd() == 0) {
//                        Intent intent = new Intent(mContext, UserProfileActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        intent.putExtra(Constants.CONTACTS, contactItem);
//                        mContext.startActivity(intent);
                    }
                }
                break;

            case R.id.addFriendIV:
                if (Utils.isNetworkAvailable(mContext)) {
                    inviteFriendPos = (Integer) v.getTag();
                    //Utils.showShortToast(mContext,"Invitation Not Sent!");
                    //inviteFriend(inviteFriendPos);
                }
                break;
            case R.id.invite_friends:
              //  inviteFragmentListener.onInviteFriendClicked(Constants.INVITE_FRAGMENT);
                EventBus.getDefault().post(new ReplaceFragment(InviteFriends.class, ContactsFragment.class));
                break;
        }
    }

    private void inviteFriend(int inviteFriendPos) {
        String contactNumber = mDataset.get(inviteFriendPos).getContactNumber();
        Fog.e("INVITE URL", createUrl(contactNumber));
        CallWebService.getInstance(mContext, true, Constants.ApiType.INVITE_FRIEND).hitJsonObjectRequestAPI(CallWebService.PUT, createUrl(contactNumber), createJsonForInviteFriend(), this);
    }

    private JSONObject createJsonForInviteFriend() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("identities", JSONObject.NULL);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private String createUrl(String contactNumber) {
        String contactID = Utils.appendCountryCodeToNumber(mContext, FunduUser.getContactId());
        contactNumber = Utils.appendCountryCodeToNumber(mContext, contactNumber);
        String api = String.format(API.INVITE_FRIEND, FunduUser.getContactIDType(), contactID, FunduUser.getContactIDType(), contactNumber) + "&"
                + Constants.COUNTRY_SHORTCODE + "=" + FunduUser.getCountryShortName();
        return api;
    }

    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        if (position==0)
        return 1;
        else
        return mDataset.get(position).getIsUnregisterd();

    }

    @Override
    public void onJsonObjectSuccess(JSONObject response, int apiType) throws JSONException {
        Utils.showShortToast(mContext, response.getString("data"));
    }

    @Override
    public void onFailure(String str, int apiType) {
        try {
            JSONObject job = new JSONObject(str);
            Utils.showLongToast(mContext, job.getString("message"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public ArrayList<ContactItem> getFilterRegisteredUser(List<ContactItem> contactItems){

        ArrayList<ContactItem> contactItems1 = new ArrayList<>();
        for(int i=0;i<contactItems.size();i++){

            if(contactItems.get(i).isAddedInNetwork()){
                contactItems1.add(contactItems.get(i));

            }


        }
        Fog.d("Fundu","my contact"+contactItems1.size());

        return null;
    }

}