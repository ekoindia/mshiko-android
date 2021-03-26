package in.co.eko.fundu.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import in.co.eko.fundu.R;
import in.co.eko.fundu.interfaces.BankAccountOptions;
import in.co.eko.fundu.models.BankAccountItem;

public class BankAccountsAdapter extends RecyclerView.Adapter<BankAccountsAdapter.ViewHolder>  {

    private ArrayList<BankAccountItem> mAccountItems = new ArrayList<>();
    private int colorArray[] = null;
    private BankAccountOptions listener;

    public BankAccountsAdapter(ArrayList<BankAccountItem> accountItems, BankAccountOptions listener) {
        this.mAccountItems = accountItems;
        this.listener = listener;
        setHasStableIds(true);
    }




    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView bankName;
        ImageView icon,banklogo;
        TextView actionText;
        TextView accountNumber;


        public ViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            actionText = (TextView)itemView.findViewById(R.id.text);
            bankName = (TextView) itemView.findViewById(R.id.bankname);
            accountNumber = (TextView)itemView.findViewById(R.id.accountnumber);
            banklogo = (ImageView)itemView.findViewById(R.id.img_bankLogo);
        }
    }

    @Override
    public BankAccountsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_profile_bank_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BankAccountsAdapter.ViewHolder holder, final int position) {

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    if((mAccountItems.get(position).getPinAvailable()))
                        listener.onCheckBalance(mAccountItems.get(position));
                    else
                        listener.onSetPin(mAccountItems.get(position));
                }

            }
        });
        if(mAccountItems.get(position).getPinAvailable()){
            holder.icon.setImageResource(R.drawable.ic_get_cash);
            holder.actionText.setText(R.string.check_balance);
            holder.icon.setVisibility(View.VISIBLE);
            holder.actionText.setVisibility(View.VISIBLE);
        }
        else{
            holder.icon.setImageResource(R.drawable.ic_privacy);
            holder.actionText.setText(R.string.setupipin);
            holder.icon.setVisibility(View.GONE);
            holder.actionText.setVisibility(View.GONE);
        }
        holder.bankName.setText(mAccountItems.get(position).getName());
        setBankLogo(holder,mAccountItems.get(position));

        holder.accountNumber.setText(mAccountItems.get(position).getAccountNumber());
    }
    private void setBankLogo(BankAccountsAdapter.ViewHolder holder,BankAccountItem item){
        String bankName = item.getIfsc();
        if(TextUtils.isEmpty(bankName)){
            holder.banklogo.setVisibility(View.GONE);
        }
        else{
            holder.banklogo.setVisibility(View.VISIBLE);
            if(bankName.contains("HDFC")){
                holder.banklogo.setImageResource(R.drawable.ic_hdfc_bank_logo);
            } else if(bankName.contains("ICIC")){
                holder.banklogo.setImageResource(R.drawable.ic_icici_bank);
            } else if(bankName.contains("SBI")){
                holder.banklogo.setImageResource(R.drawable.ic_sbi_logo);
            } else if(bankName.contains("UTIB")){
                holder.banklogo.setImageResource(R.drawable.ic_axis_bank);
            } else if(bankName.contains("CITI")){
                holder.banklogo.setImageResource(R.drawable.ic_citi_bank);
            } else if(bankName.contains("KKBK")){
                holder.banklogo.setImageResource(R.drawable.ic_kotak_bank);
            } else if(bankName.contains("DBSS")){
                holder.banklogo.setImageResource(R.drawable.ic_dbs_bank);
            } else if(bankName.contains("RATN")){
                holder.banklogo.setImageResource(R.drawable.ic_rbl_bank);
            } else if(bankName.contains("YESB")){
                holder.banklogo.setImageResource(R.drawable.ic_yes_bank);
            } else if(bankName.contains("HSBC")) {
                holder.banklogo.setImageResource(R.drawable.ic_hsbc_bank);
            } else if(bankName.contains("PYTM")) {
                holder.banklogo.setImageResource(R.drawable.ic_paytm_bank);
            } else if(bankName.contains("AIRP")) {
                holder.banklogo.setImageResource(R.drawable.ic_airtel_bank);
            } else if (bankName.contains("UBIN")) {
                holder.banklogo.setImageResource(R.drawable.ic_union_bank);
            } else if (bankName.contains("PUNB")) {
                holder.banklogo.setImageResource(R.drawable.ic_pnb);
            } else if (bankName.contains("ORBC")) {
                holder.banklogo.setImageResource(R.drawable.ic_oriental_bank);
            } else if (bankName.contains("IOBA")) {
                holder.banklogo.setImageResource(R.drawable.ic_indian_overseas_bank);
            } else if (bankName.contains("KVBL")) {
                holder.banklogo.setImageResource(R.drawable.ic_karur_vyasa_bank);
            } else if (bankName.contains("KARB")) {
                holder.banklogo.setImageResource(R.drawable.ic_karnataka_bank);
            } else if (bankName.contains("ANDB")) {
                holder.banklogo.setImageResource(R.drawable.ic_andhra_bank);
            } else if (bankName.contains("CNRB")) {
                holder.banklogo.setImageResource(R.drawable.ic_canara_bank);
            } else if (bankName.contains("UCBA")) {
                holder.banklogo.setImageResource(R.drawable.ic_uco_bank);
            } else if (bankName.contains("IDIB")) {
                holder.banklogo.setImageResource(R.drawable.ic_indian_bank);
            } else if (bankName.contains("UTBI")) {
                holder.banklogo.setImageResource(R.drawable.ic_united_bank);
            } else if (bankName.contains("VIJB")) {
                holder.banklogo.setImageResource(R.drawable.ic_vijaya_bank);
            } else if (bankName.contains("IBKL")) {
                holder.banklogo.setImageResource(R.drawable.ic_idbi_bank);
            } else if (bankName.contains("ALLA")) {
                holder.banklogo.setImageResource(R.drawable.ic_allahabad_bank);
            } else if (bankName.contains("BARB")) {
                holder.banklogo.setImageResource(R.drawable.ic_bank_of_baroda);
            } else if (bankName.contains("BKDN ")) {
                holder.banklogo.setImageResource(R.drawable.ic_dena_bank);
            } else if (bankName.contains("BKID ")) {
                holder.banklogo.setImageResource(R.drawable.ic_bank_of_india);
            } else if (bankName.contains("BAHB ")) {
                holder.banklogo.setImageResource(R.drawable.ic_bank_of_maharashtra);
            } else if (bankName.contains("CSBK ")) {
                holder.banklogo.setImageResource(R.drawable.ic_catholic_syrian_bank);
            } else if (bankName.contains("CBIN ")) {
                holder.banklogo.setImageResource(R.drawable.ic_central_bank_of_india);
            } else if (bankName.contains("COSB ")) {
                holder.banklogo.setImageResource(R.drawable.ic_cosmos_bank);
            } else if (bankName.contains("DCBL ")) {
                holder.banklogo.setImageResource(R.drawable.ic_dcb_bank);
            } else if (bankName.contains("FDRL ")) {
                holder.banklogo.setImageResource(R.drawable.ic_federal_bank);
            } else if (bankName.contains("IDFB ")) {
                holder.banklogo.setImageResource(R.drawable.ic_idfc_bank);
            } else if (bankName.contains("INDB ")) {
                holder.banklogo.setImageResource(R.drawable.ic_indus_ind_bank);
            } else if (bankName.contains("LAVB ")) {
                holder.banklogo.setImageResource(R.drawable.ic_lakshmi_vilas_bank);
            } else if (bankName.contains("PSIB ")) {
                holder.banklogo.setImageResource(R.drawable.ic_punjab_and_sind_bank);
            } else if (bankName.contains("SIBL ")) {
                holder.banklogo.setImageResource(R.drawable.ic_south_indian_bank);
            } else if (bankName.contains("SCBL ")) {
                holder.banklogo.setImageResource(R.drawable.ic_standard_chartered);
            } else{
                holder.banklogo.setImageResource(R.drawable.ic_bank_common);
            }
        }
    }
    @Override
    public int getItemCount() {
        return mAccountItems.size();
    }



}
