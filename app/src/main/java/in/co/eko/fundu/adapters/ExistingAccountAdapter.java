package in.co.eko.fundu.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.R;
import in.co.eko.fundu.activities.HomeActivity;
import in.co.eko.fundu.constants.API;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.models.LinkAccountItem;
import in.co.eko.fundu.requests.CallWebService;
import in.co.eko.fundu.utils.Fog;
import in.co.eko.fundu.utils.Utils;

/**
 * Created by pallavi on 1/11/17.
 */

public class ExistingAccountAdapter extends RecyclerView.Adapter<ExistingAccountAdapter.ViewHolder> implements
        CallWebService.ObjectResponseCallBack{

    Context context;
    String[] splitedIdValue;
    boolean isLinnked= false;
    private String currentAccount,iFSC;
    private ArrayList<LinkAccountItem> linkAccountItems = new ArrayList<>();
    private JSONArray identitiesArray = null;
    private JSONObject mainJsonObject = null, additionalIdentitiesObject = null;


    public ExistingAccountAdapter(Context context, ArrayList<LinkAccountItem> linkAccountItems, String currentAccount, String iFSC, boolean isLinked) {
        this.context = context;
        this.linkAccountItems = linkAccountItems;
        this.currentAccount = currentAccount;
        this.iFSC =iFSC;
        this.isLinnked = isLinked;
        Utils.hideSoftKeyboard((Activity)context);
    }


    @Override
    public ExistingAccountAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_row_existing_account, parent, false);
        return new ExistingAccountAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ExistingAccountAdapter.ViewHolder holder, final int position) {
        Fog.d("position",""+position);
        splitedIdValue = linkAccountItems.get(position).getAdditional_id_value().split("_");
        holder.txt_accountNumber.setText(splitedIdValue[0]);
        Fog.d("ExistingAccountAdapter",""+currentAccount+" "+iFSC);
        Fog.d("ExistingAccountAdapter",""+splitedIdValue[1]+" "+splitedIdValue[0]);
        String str1="",str2="";
        addBankLogo(holder.img_bankLogo,linkAccountItems.get(position).getName().split(" "));
        if(currentAccount.length()!=0&&iFSC.length()!=0){
            str1 = currentAccount.substring(currentAccount.length()-4);
            str2 =  splitedIdValue[0].substring(splitedIdValue[0].length()-4);
        }
        if(iFSC.equalsIgnoreCase(splitedIdValue[1])&&str1.equalsIgnoreCase(str2)){

            holder.radiobutton.setVisibility(View.VISIBLE);
        }
        Fog.d("str11",""+str1);
        Fog.d("str21",""+str2);


        if(!isLinnked){
            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Fog.d("onClick",""+holder.txt_accountNumber.getText().toString());
                        Fog.d("onClick",""+linkAccountItems.get(position).getAdditional_id_value());
                        splitedIdValue = linkAccountItems.get(position).getAdditional_id_value().split("_");
                        Fog.d("onClick",""+splitedIdValue[0]);
                        Fog.d("onClick",""+splitedIdValue[1]);
                        createFinalJson(holder.txt_accountNumber.getText().toString(),splitedIdValue[1]);

                        /*createFinalJson(holder.txt_accountNumber.getText().toString(),splitedIdValue[1]);*/
                       // hitApiForUpdatingLinkAccount(mainJsonObject,false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }


        holder.txt_bankNameSP.setText(linkAccountItems.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return linkAccountItems.size();
    }

    @Override
    public void onJsonObjectSuccess(JSONObject response, int apiType) throws JSONException {
        switch (apiType) {
            case Constants.ApiType.GET_CUSTOMER_INFO:
               /* getResponseInArrayList(response.getJSONObject(getString(R.string.additional_identities)).getJSONArray("identities"));
                addViewsToLayout();*/
                break;
            case Constants.ApiType.SAVE_FINAL_ADDITIONAL_IDENTITIES:
                if (parseResponse(response)){
                    context.startActivity(new Intent(context,HomeActivity.class)
                            .putExtra("after_action","HomeAcitivity"));
                    ((Activity)context).finish();
                }
                break;
            case Constants.ApiType.SAVE_ADD_MORE_CLICKED_IDENTITES:
                if (parseResponse(response) && linkAccountItems.size() > 0)
                    context.startActivity(new Intent(context,HomeActivity.class)
                            .putExtra("after_action","HomeAcitivity"));
                    ((Activity)context).finish();
                break;
        }
    }

    @Override
    public void onFailure(String str, int apiType) {

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_accountNumber,txt_bankNameSP,linkedAccount;
        RelativeLayout relativeLayout;
        ImageView img_bankLogo;
        RadioButton radiobutton;
        public ViewHolder(View itemView) {
            super(itemView);
            txt_accountNumber = (TextView) itemView.findViewById(R.id.txt_accountNumber);
            txt_bankNameSP = (TextView) itemView.findViewById(R.id.txt_bankNameSP);
           // linkedAccount = (TextView) itemView.findViewById(R.id.linkedAccount);
            img_bankLogo = (ImageView) itemView.findViewById(R.id.img_bankLogo);
            radiobutton = (RadioButton) itemView.findViewById(R.id.radiobutton);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);


        }



    }


    public void updateList(ArrayList<LinkAccountItem> results) {
        linkAccountItems = results;
        //Triggers the list update
        notifyDataSetChanged();
    }
    private void hitApiForUpdatingLinkAccount(JSONObject mainJsonObject, boolean addMoreClicked) {
        String id = Utils.appendCountryCodeToNumber(context, FunduUser.getContactId());
        String id_type = FunduUser.getContactIDType();
        Fog.d("mainJsonObject",""+mainJsonObject);
        if (!addMoreClicked)
            CallWebService.getInstance(context, true, Constants.ApiType.SAVE_FINAL_ADDITIONAL_IDENTITIES).hitJsonObjectRequestAPI(Request.Method.PUT, String.format(API.CUSTOMER, id_type, id), mainJsonObject, this);
        else
            CallWebService.getInstance(context, true, Constants.ApiType.SAVE_ADD_MORE_CLICKED_IDENTITES).hitJsonObjectRequestAPI(Request.Method.PUT, String.format(API.CUSTOMER, id_type, id), mainJsonObject, this);
    }
    private boolean parseResponse(JSONObject response) {
        try {
            JSONObject data = response.getJSONObject("data");
            String currentLinkAccountNumber = data.optString("accno");
            Fog.d("currentLinkAccountNumber",""+currentLinkAccountNumber);
            //getResponseInArrayList(response.getJSONObject(context.getString(R.string.additional_identities)).getJSONArray("identitiesResponse"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String message;
        StringBuilder stringBuilder = getStringBuilder();
        if (stringBuilder.length() > 0) {
            message = stringBuilder.toString();
            if(message.contains("Additional identity detail already exist in fundudb")){
                 isLinnked = true;
                message = context.getString(R.string.saved_succssfully);
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                return true;
            }
            else{
                isLinnked = true;
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                return false;
            }
        } else {
            isLinnked = true;
            message = context.getString(R.string.saved_succssfully);
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            return true;
        }
        //  AlertMessage.showLinkAccountResponseDialog(this, linkAccountItems);
    }

    @NonNull
    private StringBuilder getStringBuilder() {
        StringBuilder stringBuilder = new StringBuilder();
        for (LinkAccountItem linkAccountItem : linkAccountItems) {
            if (linkAccountItem.getSb_status() != null && !linkAccountItem.getSb_status().equals(context.getString(R.string.success)))
                stringBuilder.append(linkAccountItem.getSb_message() + "\n");
            if (linkAccountItem.getFundu_db_status() != null && !linkAccountItem.getFundu_db_status().equals(context.getString(R.string.success)))
                stringBuilder.append(linkAccountItem.getFundu_db_message() + "\n");
        }
        return stringBuilder;
    }
    private void createFinalJson(String accno, String ifsc) throws JSONException {

        mainJsonObject = new JSONObject();
        mainJsonObject.put(context.getString(R.string.additional_identities), additionalIdentitiesObject);
        mainJsonObject.put("country_shortname", FunduUser.getAppPreferences().getString(Constants.COUNTRY_SHORTCODE));
        mainJsonObject.put("mobile", FunduUser.getAppPreferences().getString(Constants.PrefKey.CONTACT_NUMBER));
        mainJsonObject.put("accno",accno);
        mainJsonObject.put("ifsc",ifsc);
        mainJsonObject.put("recipient_id",linkAccountItems.get(0).getRecipient_id());
        String vpa = FunduUser.getVpa();
        if(vpa != null && vpa.length() != 0)
            mainJsonObject.put("vpa",vpa);
        hitApiForUpdatingLinkAccount(mainJsonObject,false);
    }

   private void addBankLogo(ImageView imageView, String[] bankname){

       switch(bankname[0]){
           case "ICICI" :
               //Picasso.with(context).load(R.drawable.icici_logo).resize(300,300).into(imageView);
               break;
           case "HDFC" :
               Picasso.with(context).load(R.drawable.ic_hdfc_bank_logo).resize(300,300).into(imageView);
               break;

           case "SBI" :
               Picasso.with(context).load(R.drawable.ic_sbi_logo).resize(300,300).into(imageView);
               break;

       }

   }



}
