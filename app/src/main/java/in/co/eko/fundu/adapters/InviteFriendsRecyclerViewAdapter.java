package in.co.eko.fundu.adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import in.co.eko.fundu.R;
import in.co.eko.fundu.models.ContactItem;
import in.co.eko.fundu.views.ColorCircleDrawable;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ContactItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class InviteFriendsRecyclerViewAdapter extends RecyclerView.Adapter<InviteFriendsRecyclerViewAdapter.ViewHolder> {

    private static  List<ContactItem> contacts;
    private final OnListFragmentInteractionListener mListener;
    private Context context;
    private ArrayList<ContactItem> selectedItems = new ArrayList<>();
    private int colorArray[] = null;
    String action="";
    private int allowedSelection;
    private boolean mTapSelection;

    private static ViewHolder viewHolder;


    public InviteFriendsRecyclerViewAdapter(List<ContactItem> items, OnListFragmentInteractionListener listener, Context context, String action) {
        contacts = items;
        mListener = listener;
        this.context = context;
        selectedItems.clear();
        TypedArray ta = context.getResources().obtainTypedArray(R.array.contacts_color);
        colorArray = new int[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            colorArray[i] = ta.getColor(i, 0);
        }
        this.allowedSelection = -1;
        this.action = action;
        ta.recycle();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        viewHolder = holder;
        final ContactItem item = contacts.get(position);
        holder.mItem = item;
        setUserImage(holder,item,position);
        holder.userName.setText(item.getContactName());
        //if(action.equalsIgnoreCase("Invite"))
        holder.subtitle.setText(item.getContactNumber());


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });


       // holder.selectContact.setChecked(selectedItems.contains(holder.mItem));

        holder.selectContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(item.isSelectType()){
                    holder.selectContact.setChecked(false);
                    item.setSelectType(false);
                    selectedItems.remove(holder.mItem);
                }else {
                    holder.selectContact.setChecked(true);
                    item.setSelectType(true);
                    selectedItems.add(holder.mItem);
                }
            }
        });

        if(mTapSelection){
            holder.selectContact.setVisibility(View.INVISIBLE);
        }


        if(item.isAddedInNetwork()){
            holder.selectContact.setVisibility(View.GONE);
            holder.textViewInvited.setVisibility(View.VISIBLE);
            selectedItems.remove(holder.mItem);
        }
        else{
            if(!mTapSelection)
                holder.selectContact.setVisibility(View.VISIBLE);
            holder.textViewInvited.setVisibility(View.GONE);
        }
        if(item.isSelectType()){
            holder.selectContact.setChecked(true);
            selectedItems.add(holder.mItem);
        }else {
            holder.selectContact.setChecked(false);
            selectedItems.remove(holder.mItem);
            selectedItems.clear();
        }


    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView userImage;
        public final TextView userName,textViewInvited;
        public final TextView subtitle;
        public final CheckBox selectContact;
        public final TextView userInitial;
        public ContactItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            userImage = (ImageView) view.findViewById(R.id.user_image);
            userName = (TextView)view.findViewById(R.id.user_name);
            subtitle = (TextView) view.findViewById(R.id.subtitle);
            selectContact = (CheckBox)view.findViewById(R.id.selectContact);
            userInitial = (TextView)view.findViewById(R.id.user_initial);
            textViewInvited = (TextView)view.findViewById(R.id.textView_invited);
        }

//        @Override
//        public String toString() {
//            return super.toString() + " '" + mContentView.getText() + "'";
//        }
    }


    public void selectAllInviteFriends(boolean flag){

        for(int i=0;i<contacts.size();i++){
            ContactItem contactItem=contacts.get(i);
            contactItem.setSelectType(flag);

        }
        notifyDataSetChanged();

    }

    private void setUserImage(ViewHolder holder, ContactItem contactItem,int position) {
        if (!contactItem.getContactImage().isEmpty()) {
            holder.userInitial.setVisibility(View.GONE);
            holder.userImage.setVisibility(View.VISIBLE);
            try {
                Picasso.with(context).load(contactItem.getContactImage()).placeholder(R.drawable.ic_user_image).into(holder.userImage);
            } catch (IllegalArgumentException e){
                holder.userImage.setImageURI(Uri.parse(contactItem.getContactImage()));
//                Picasso.with(mContext).load(R.drawable.user_image).placeholder(R.drawable.user_image).into(holder.profile);
            }
            setBackground(holder.userImage,position);
        }else{
            //if name is available then show the initial
            String name  = contactItem.getContactName();
            if(name != null && name.length()>=1){
                name = name.substring(0,1);
                holder.userInitial.setText(name);
                holder.userInitial.setVisibility(View.VISIBLE);
                holder.userImage.setVisibility(View.GONE);
                setBackground(holder.userInitial,position);

            }
            else{
                holder.userImage.setImageResource(R.drawable.user_image_white);
                holder.userImage.setBackgroundResource(R.drawable.circular_backgrond);
                holder.userInitial.setVisibility(View.GONE);
                holder.userImage.setVisibility(View.VISIBLE);
                setBackground(holder.userImage,position);

            }
        }

    }
    private void setBackground(View view,int position){
        int color = ContextCompat.getColor(context,R.color.colorPrimary);
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

    public void setContacts(List<ContactItem> contacts) {
        this.contacts = contacts;
    }

    /**
     * To get selected contacts for mass invite
     * @return selected contacts by user
     */
    public ArrayList<ContactItem> getSelectedItems() {
        return selectedItems;
    }

    public void clearSelectedItems() {
        this.selectedItems.clear();
    }

    public interface OnListFragmentInteractionListener {

        void onListFragmentInteraction(ContactItem item);
        void changeSendVisibility(int visibility);
    }

    public void setAllowedSelection(int allowedSelection) {
        this.allowedSelection = allowedSelection;
    }

    public void setmTapSelection(boolean pTapSelection){
        this.mTapSelection = pTapSelection;
    }
}
