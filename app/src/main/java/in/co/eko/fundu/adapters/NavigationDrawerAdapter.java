package in.co.eko.fundu.adapters;
/*
 * Created by Bhuvnesh
 */

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import in.co.eko.fundu.R;
import in.co.eko.fundu.activities.Faq;
import in.co.eko.fundu.interfaces.UpdateProfileData;
import in.co.eko.fundu.models.DynamicMenuItem;
import in.co.eko.fundu.models.FixMenuItem;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.utils.AppPreferences;
import in.co.eko.fundu.utils.Fog;
import in.co.eko.fundu.views.ColorCircleDrawable;

public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.ViewHolder>
              implements UpdateProfileData{

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_CAMPAIGN = 2;
    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    private Context context;
    private OnNavigationItemClickListener listener;
    private ArrayList<Object> sideMenuItems;
//    private ArrayList<Integer> iconsList;
    protected AppPreferences appPreferences;


    public NavigationDrawerAdapter(Context context, OnNavigationItemClickListener _listener, ArrayList<Object> sideMenuItems) {
        this.context = context;
        listener = _listener;
        this.sideMenuItems = sideMenuItems;
    }

    @Override
    public void onProfileUpdate() {
      Fog.d("onProfileUpdate","onProfileUpdate");
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        int HolderId;
        TextView textView, rating, phoneNumber;
        ImageView profile, navIcon;
        TextView myName;
        TextView textView_credits,textview_faq;
        View divider;

        public ViewHolder(final View itemView, int ViewType) {
            super(itemView);

            itemView.setClickable(true);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClickNavItem(sideMenuItems.get(getLayoutPosition()-1));
                }
            });
            if (ViewType == TYPE_ITEM) {

                textView = (TextView) itemView.findViewById(R.id.rowText);
                navIcon = (ImageView) itemView.findViewById(R.id.nav_icon);
                divider = itemView.findViewById(R.id.divider);
                HolderId = 1;
                itemView.setBackgroundResource(R.drawable.ripple_side_menu_selection);
            } else if (ViewType == TYPE_CAMPAIGN) {
                textView = (TextView) itemView.findViewById(R.id.rowText);
                navIcon = (ImageView) itemView.findViewById(R.id.nav_icon);
                divider = itemView.findViewById(R.id.divider);
                HolderId = 2;
                itemView.setBackgroundResource(R.drawable.ripple_side_menu_selection);
            } else {
                myName = (TextView) itemView.findViewById(R.id.my_name);
                textView_credits = (TextView) itemView.findViewById(R.id.textView_credits);
                textview_faq = (TextView) itemView.findViewById(R.id.textview_faq);
                phoneNumber = (TextView) itemView.findViewById(R.id.my_number);
                //myMoney = (TextView) itemView.findViewById(R.id.my_money);
                profile = (ImageView) itemView.findViewById(R.id.circleView);
                rating = (TextView) itemView.findViewById(R.id.myrating);
                HolderId = 0;
            }
        }

    }
    public void setItemSelected(int position){
        selectedItems.clear();
        selectedItems.put(position, true);
    }
    public boolean isItemSelected(int position) {
        return selectedItems.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM || viewType == TYPE_CAMPAIGN) {
            View v = LayoutInflater.from(context).inflate(R.layout.item_row, parent, false); //Inflating the layout
            return new ViewHolder(v, viewType);
        } else if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(context).inflate(R.layout.header, parent, false); //Inflating the layout
            return new ViewHolder(v, viewType); // returning the object created
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Fog.d("isItemSelected","isItemSelected"+isItemSelected(position));
        if (holder.HolderId == TYPE_ITEM) {
            if(isItemSelected(position)) {
                holder.textView.setSelected(true);
                holder.navIcon.setSelected(true);
            } else {
                holder.navIcon.setSelected(false);
                holder.textView.setSelected(false);
            }

            FixMenuItem item = (FixMenuItem) sideMenuItems.get(position-1);


            holder.navIcon.setImageResource((item.getIcon()));
            holder.textView.setText("    " + item.getTitle()); // Setting the Text with the array of our Titles
        } else if (holder.HolderId == TYPE_CAMPAIGN) {
            if(isItemSelected(position)) {
                holder.textView.setSelected(true);
                holder.navIcon.setSelected(true);
            } else {
                holder.navIcon.setSelected(false);
                holder.textView.setSelected(false);
            }

            DynamicMenuItem item = (DynamicMenuItem) sideMenuItems.get(position-1);

            holder.textView.setText("    " + item.getTitle());

            if (item.getIcon()==null ||
                    item.getIcon().equalsIgnoreCase("")) {
                holder.navIcon.setVisibility(View.INVISIBLE);
            } else {
                String url = item.getIcon();
                Picasso.with(context).load(url).into(holder.navIcon);
            }

        }
        else {
            try {

                String path = FunduUser.getProfilePic();
                String userName = FunduUser.getFullName();
                String contactNumber = FunduUser.getContactId();
                double rating = FunduUser.getRating();
                holder.phoneNumber.setText(contactNumber);
                holder.myName.setText(userName);
                if(rating > 0) {
                    holder.rating.setText(String.format("%.1f",rating));
                    holder.itemView.findViewById(R.id.ratingview).setVisibility(View.VISIBLE);
                }
                else{
                    holder.rating.setVisibility(View.GONE);
                    holder.itemView.findViewById(R.id.ratingview).setVisibility(View.GONE);
                }
                if(path != null && path.length()>0)
                    Picasso.with(context).load(path).placeholder(R.drawable.user_image_white).into(holder.profile);
                else
                    holder.profile.setImageResource(R.drawable.user_image_white);

            } catch (IllegalArgumentException e){
                Picasso.with(context).load(R.drawable.user_image_white).placeholder(R.drawable.user_image_white).into(holder.profile);
            }

            if(!FunduUser.isUserLogin())
            {
                holder.myName.setVisibility(View.GONE);

                holder.textView_credits.setVisibility(View.GONE);
                holder.textview_faq.setVisibility(View.GONE);
                holder.phoneNumber.setVisibility(View.GONE);

            }
            else{
                holder.phoneNumber.setVisibility(View.VISIBLE);
                holder.myName.setVisibility(View.VISIBLE);

                holder.textView_credits.setVisibility(View.GONE);
                holder.textview_faq.setVisibility(View.GONE);
            }
           // holder.myMoney.setTypeface(TypefaceManager.getInstance(context).getOpenSansRegular());
            int sdk = android.os.Build.VERSION.SDK_INT;
            int color = ContextCompat.getColor(context,R.color.colorPrimary);
            if(sdk <= android.os.Build.VERSION_CODES.JELLY_BEAN) {

                holder.profile.setBackgroundDrawable(new ColorCircleDrawable(color));
            } else {
                holder.profile.setBackground(new ColorCircleDrawable(color));
            }


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fog.d("isItemSelected","setOnClickListener");
                    listener.onClickHeader();
                    //context.startActivity(new Intent(context,CompleteYourProfileActivity.class));
                }
            });

            holder.textview_faq.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //listener.onClickHeader();
                    context.startActivity(new Intent(context,Faq.class));
                }
            });

            holder.profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClickHeader();
                }
            });

            //holder.myMoney.setText(/*money.length()>0?("\u20B9 "+ money):*/"");
        }
    }
    public void setProfileData(String picUrl, String name, String money,String contactNumber){

        notifyItemChanged(0);
    }

    public void setAmount(String amount) {

        notifyItemChanged(0);
    }

//    public void getTitleIcon(Context context, String userType) {
//        Fog.d("CUST_ID_NV", FunduUser.isUserLoginorRegister()+" CNTRY "+FunduUser.getCountryShortName());
//        if (FunduUser.isUserLoginorRegister()) {
//            if (FunduUser.getCountryShortName().equalsIgnoreCase("KEN")) {
//                if (userType.equalsIgnoreCase("AGENT")) {
//                    ICONS = Constants.ICONS_DEFAULT;/*ICONS_KEN*/
//                    TITLES = context.getResources().getStringArray(R.array.nav_drawer_array_default);/*nav_drawer_array_ken*/
//                } else {
//                    ICONS = Constants.ICONS_KEN;
//                    TITLES = context.getResources().getStringArray(R.array.nav_drawer_array_ken);
//                }
//            } else if (FunduUser.getCountryShortName().equalsIgnoreCase("IND")) {
//                ICONS = Constants.ICONS_IND;
//                TITLES = context.getResources().getStringArray(R.array.nav_drawer_array_ind);
//            } else {
//                ICONS = Constants.ICONS_DEFAULT;
//                TITLES = context.getResources().getStringArray(R.array.nav_drawer_array_default);
//            }
//        }
//        else {
//            ICONS = Constants.ICONS_DEFAULT;
//            TITLES = context.getResources().getStringArray(R.array.nav_drawer_array_default);
//        }
//        notifyDataSetChanged();
//    }

    @Override
    public int getItemCount() {
        return sideMenuItems.size() + 1; // the number of items in the list will be +1 the titles including the header view.
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;
        else if (isPositionCampaign(position)) {
            return TYPE_CAMPAIGN;
        }
        return TYPE_ITEM;
    }

    private boolean isPositionCampaign(int position) {
        return (sideMenuItems.get(position-1) instanceof DynamicMenuItem); // 0 for fragment, 1 for campaign
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    public interface OnNavigationItemClickListener {

         void onClickNavItem(Object sideMenuItem);
         void onClickHeader();
    }

}

