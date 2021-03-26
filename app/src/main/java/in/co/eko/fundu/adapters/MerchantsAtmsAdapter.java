package in.co.eko.fundu.adapters;

/**
 * Created by zartha on 4/15/18.
 */

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import in.co.eko.fundu.R;
import in.co.eko.fundu.interfaces.MerchantAtmOptions;
import in.co.eko.fundu.models.Contact;
import in.co.eko.fundu.models.Neighbour;


public class MerchantsAtmsAdapter extends RecyclerView.Adapter<MerchantsAtmsAdapter.ViewHolder>  {

    private ArrayList<Neighbour> list = new ArrayList<>();
    private MerchantAtmOptions listener;


    public MerchantsAtmsAdapter(ArrayList<Neighbour> list, MerchantAtmOptions listner) {
        this.list = list;
        this.listener = listner;
        setHasStableIds(true);
    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name,distance;
        ImageView icon;
        View navigate;
        public ViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            navigate = itemView.findViewById(R.id.navigate);
            name = (TextView) itemView.findViewById(R.id.name);
            distance = (TextView)itemView.findViewById(R.id.distance);
        }
    }

    @Override
    public MerchantsAtmsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.merchant_atm_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MerchantsAtmsAdapter.ViewHolder holder, final int position) {
        holder.itemView.setBackgroundResource(R.drawable.ripple_side_menu_selection);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.onItemClick(list.get(position));
                }
            }
        });
        holder.navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.navigate(list.get(position));
                }
            }
        });

        holder.name.setText(list.get(position).getName());
        float distance = list.get(position).getDistance()/1000;
        holder.distance.setText(String.format("%.2f %s",distance,"KM away"));
        setIcon(holder,list.get(position));

    }

    private void setIcon(MerchantsAtmsAdapter.ViewHolder holder,Contact item){
        if(item.getContactType().equalsIgnoreCase("atm")){
            setBankLogo(holder,item);
        }
        else {
            holder.icon.setImageResource(R.drawable.merchant);
        }
    }
    private void setBankLogo(MerchantsAtmsAdapter.ViewHolder holder,Contact item){
        String bankName = item.getName();
        if(TextUtils.isEmpty(bankName)){
            holder.icon.setVisibility(View.VISIBLE);
            holder.icon.setImageResource(R.drawable.ic_atm);
        }
        else{
            holder.icon.setVisibility(View.VISIBLE);
            if(bankName.toUpperCase().contains("HDFC")){
                holder.icon.setImageResource(R.drawable.ic_hdfc_bank_logo);
            }
            else if(bankName.toUpperCase().contains("ICIC")){
                holder.icon.setImageResource(R.drawable.ic_icici_bank);
            }
            else if(bankName.toUpperCase().contains("STATE BANK")){
                holder.icon.setImageResource(R.drawable.ic_sbi_logo);
            }
            else if(bankName.toUpperCase().contains("AXIS")){
                holder.icon.setImageResource(R.drawable.ic_axis_bank);
            }
            else if(bankName.toUpperCase().contains("CITI") || bankName.toUpperCase().contains("CITY")){
                holder.icon.setImageResource(R.drawable.ic_citi_bank);
            }
            else if(bankName.contains("KKBK")){
                holder.icon.setImageResource(R.drawable.ic_kotak_bank);
            }
            else if(bankName.contains("DBSS")){
                holder.icon.setImageResource(R.drawable.ic_dbs_bank);
            }
            else if(bankName.contains("RATN")){
                holder.icon.setImageResource(R.drawable.ic_rbl_bank);
            }
            else if(bankName.contains("YES BANK")){
                holder.icon.setImageResource(R.drawable.ic_yes_bank);
            }
            else if(bankName.toUpperCase().contains("STANDARD CHARTERED")){
                holder.icon.setImageResource(R.drawable.ic_standard_chartered);
            }
            else{
                holder.icon.setImageResource(R.drawable.ic_atm);
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }



}

