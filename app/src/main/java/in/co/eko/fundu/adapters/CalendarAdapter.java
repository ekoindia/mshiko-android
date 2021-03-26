package in.co.eko.fundu.adapters;/*
 * Created by Bhuvnesh
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import in.co.eko.fundu.R;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.interfaces.OnFragmentInteractionListener;
import in.co.eko.fundu.models.TransactionHistoryModel;
import in.co.eko.fundu.stickyrecyclerview.StickyRecyclerHeadersAdapter;
import in.co.eko.fundu.utils.Utils;
import in.co.eko.fundu.views.ColorCircleDrawable;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {

    private final Context context;
    private ArrayList<TransactionHistoryModel> listhistory = new ArrayList<>();
    private int colorArray[] = null;
    private OnFragmentInteractionListener onFragmentInteractionListener;

    public CalendarAdapter(Context context, ArrayList<TransactionHistoryModel> listhistory,OnFragmentInteractionListener onFragmentInteractionListener) {
        this.context = context;
        this.listhistory = listhistory;
        this.onFragmentInteractionListener = onFragmentInteractionListener;
        setHasStableIds(true);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView transactionTag;
        ImageView user_image;
        TextView user_name;
        TextView subtitle, date,userInitial;


        public ViewHolder(View itemView) {
            super(itemView);
            transactionTag = (ImageView) itemView.findViewById(R.id.transaction_tag);
            user_image = (ImageView) itemView.findViewById(R.id.user_image);
//            arrowup = (ImageView) itemView.findViewById(R.id.arrowup);
//            linearLayoutHelp = (LinearLayout) itemView.findViewById(R.id.linearLayout_help);
            user_name = (TextView) itemView.findViewById(R.id.user_name);
//            repete = (ImageView) itemView.findViewById(R.id.repete);
            subtitle = (TextView) itemView.findViewById(R.id.subtitle);
//            txid = (TextView) itemView.findViewById(R.id.txid);
//            needhelp = (TextView) itemView.findViewById(R.id.needhelp);
            date = (TextView) itemView.findViewById(R.id.date);
            userInitial = (TextView)itemView.findViewById(R.id.user_initial);
//            tmetadata = (TextView)itemView.findViewById(R.id.tmetadata);
            TypedArray ta = context.getResources().obtainTypedArray(R.array.contacts_color);
            colorArray = new int[ta.length()];
            for (int i = 0; i < ta.length(); i++) {
                colorArray[i] = ta.getColor(i, 0);
            }
            ta.recycle();

//            addFriendIV.setOnClickListener(SimpleAdapter.this);
//            itemView.setOnClickListener(SimpleAdapter.this);
        }
    }
    String formatdate(String pDate){
//                                 2017-02-17 11:49:17
        String oldFormat = "yyyy-MM-dd HH:mm:ss";
        //final String NEW_FORMAT = "hh:mm a\n dd/MMM/yy";
        String newFormat = "dd MMMM yyyy | h:mm a";
        String newDateString = "";

        SimpleDateFormat sdf = new SimpleDateFormat(oldFormat);
        Date d = null;
        try {
            d = sdf.parse(pDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sdf.applyPattern(newFormat);
        newDateString = sdf.format(d);
        return newDateString;
    }

    @Override
    public CalendarAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_calendar, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CalendarAdapter.ViewHolder holder, final int position) {

        String currency = Utils.getCurrency(context);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(holder.linearLayoutHelp.getVisibility() == View.VISIBLE){
//                    holder.linearLayoutHelp.setVisibility(View.GONE);
//                    holder.dropdown.setVisibility(View.VISIBLE);
//                }
//                else{
//                    holder.linearLayoutHelp.setVisibility(View.VISIBLE);
//                    holder.dropdown.setVisibility(View.GONE);
//                }
                Bundle bundle = new Bundle();
                bundle.putString("needHelp","no");
                bundle.putSerializable("history", listhistory.get(position));
                bundle.putInt("color", colorArray[position%colorArray.length]);
                onFragmentInteractionListener.onFragmentInteraction(bundle);
            }
        });

//        holder.txid.setText(context.getString(R.string.txn_id)+" : "+listhistory.get(position).getTx_id());

        if (listhistory.get(position).getTx_type().equalsIgnoreCase("reward")) {
            holder.transactionTag.setImageLevel(1);
            holder.user_name.setText("You won");
            if (listhistory.get(position).getTx_status().equalsIgnoreCase("CREDIT_FAILED")) {
                holder.transactionTag.setImageResource(R.drawable.ic_processing);
            } else {
                holder.transactionTag.setImageResource(R.drawable.ic_history_arrow_credit);
            }
        }
        else if(listhistory.get(position).getTx_status().equalsIgnoreCase("CANCEL")){
            holder.transactionTag.setImageLevel(1);
            holder.transactionTag.setImageResource(R.drawable.ic_history_cancel);
            holder.user_name.setText(context.getString(R.string.cancelled));
//            holder.tmetadata.setText("");
        } else if(listhistory.get(position).getTx_status().equalsIgnoreCase("CREDIT_FAILED")) {
            if(listhistory.get(position).getRole().equalsIgnoreCase("provider")) {
                holder.transactionTag.setImageLevel(1);
                holder.transactionTag.setImageResource(R.drawable.ic_history_arrow_credit);
                holder.user_name.setText(context.getString(R.string.received_cash_from)+" "+listhistory.get(position).getCustomer_name());
                double seekerCharges = listhistory.get(position).getSeeker_charge();
//                if(seekerCharges > 0)
//                    holder.tmetadata.setText("Fee paid: "+currency+" "+seekerCharges);
//                else
//                    holder.tmetadata.setText("");
            } else if(listhistory.get(position).getRole().equalsIgnoreCase("seeker")) {
                holder.transactionTag.setImageLevel(0);
                holder.user_name.setText(context.getString(R.string.processing));
                holder.transactionTag.setImageResource(R.drawable.ic_processing);
                double providerCharges = listhistory.get(position).getProvider_charge();
            }
        }
        else if(listhistory.get(position).getTx_status().contains("FAILED")){
            holder.transactionTag.setImageLevel(1);
            holder.transactionTag.setImageResource(R.drawable.ic_history_cancel);
            holder.user_name.setText(context.getString(R.string.failed));
//            holder.tmetadata.setText("");
        }
        else if(listhistory.get(position).getTx_status().contains("SUCCESS")){
            if (listhistory.get(position).getRole().toString().equalsIgnoreCase("provider")){
                holder.transactionTag.setImageLevel(1);
                holder.transactionTag.setImageResource(R.drawable.ic_history_arrow_credit);
                holder.user_name.setText(context.getString(R.string.received_cash_from)+" "+listhistory.get(position).getCustomer_name());
                double seekerCharges = listhistory.get(position).getSeeker_charge();
//                if(seekerCharges > 0)
//                    holder.tmetadata.setText("Fee paid: "+currency+" "+seekerCharges);
//                else
//                    holder.tmetadata.setText("");

            }
            else if(listhistory.get(position).getRole().toString().equalsIgnoreCase("seeker")){
                holder.transactionTag.setImageLevel(0);
                holder.user_name.setText(context.getString(R.string.gave_cash_to)+" "+listhistory.get(position).getCustomer_name());
                holder.transactionTag.setImageResource(R.drawable.ic_history_arrow_debit);
                double providerCharges = listhistory.get(position).getProvider_charge();
//                if(providerCharges > 0)
//                    holder.tmetadata.setText("You earned: "+currency+" "+providerCharges);
//                else
//                    holder.tmetadata.setText("");

            }
        }
        else if(listhistory.get(position).getTx_status().contains("REVERSE")){
            if(listhistory.get(position).getRole().equalsIgnoreCase("provider")) {
                holder.transactionTag.setImageLevel(1);
                holder.transactionTag.setImageResource(R.drawable.ic_history_arrow_credit);
                holder.user_name.setText(context.getString(R.string.refunded));
//                holder.tmetadata.setText("");
            } else if(listhistory.get(position).getRole().equalsIgnoreCase("seeker")) {
                holder.transactionTag.setImageLevel(1);
                holder.transactionTag.setImageResource(R.drawable.ic_history_cancel);
                holder.user_name.setText(context.getString(R.string.failed));
//                holder.tmetadata.setText("");
            }


        }
//       holder.needhelp.setOnClickListener(new View.OnClickListener() {
//           @Override
//           public void onClick(View view) {
//
//               holder.linearLayoutHelp.setVisibility(View.GONE);
//               holder.dropdown.setVisibility(View.VISIBLE);
//               Bundle bundle = new Bundle();
//               bundle.putString("needHelp","needHelp");
//               bundle.putString(Constants.PushNotificationKeys.TID,holder.txid.getText().toString());
//               onFragmentInteractionListener.onFragmentInteraction(bundle);
//
//           }
//       });

       // System.out.println("Photo " + listhistory.get(position).getCustomer_name());

        holder.date.setText(formatdate(listhistory.get(position).getCreated_at()));
        double amount = listhistory.get(position).getTx_amount();
        holder.subtitle.setText(Utils.getCurrency(context)+String.valueOf(amount));
        setUserImage(holder,position);
    }

    private void setUserImage(CalendarAdapter.ViewHolder holder,int position) {
        //String name = holder.user_name.getText().toString();
        String name = listhistory.get(position).getCustomer_name();
        if(listhistory.get(position).getTx_type().equalsIgnoreCase("reward")) {
            holder.user_image.setVisibility(View.VISIBLE);
            holder.user_image.setImageResource(R.drawable.ic_gift_list_icon);
        } else {
            holder.user_image.setVisibility(View.GONE);
            holder.user_image.setVisibility(View.GONE);
            holder.userInitial.setVisibility(View.VISIBLE);
            if(name != null && name.length()>=1){
                name = name.substring(0,1);
                holder.userInitial.setText(name);
                setBackground(holder.userInitial,position);
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
    @Override
    public long getHeaderId(int position) {
        return position;
    }

    @Override
    public long getSpeedDialListSize(int position) {
        return position;
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sticky_header_calendar, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return listhistory.size();
    }



}
