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
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import in.co.eko.fundu.R;
import in.co.eko.fundu.interfaces.InviteFragmentListener;
import in.co.eko.fundu.models.ContactItem;
import in.co.eko.fundu.views.ColorCircleDrawable;

/**
 * Created by pallavi on 28/9/17.
 */

public class EarnRffralAdapter extends RecyclerView.Adapter<EarnRffralAdapter.ViewHolder> {

    Context context;
    protected List<ContactItem> mDataset;
    boolean search = false;
    InviteFragmentListener inviteFragmentListener;
    private int colorArray[] = null;

    public EarnRffralAdapter(Context context, List<ContactItem> mDataset) {
        this.context = context;
        this.mDataset = mDataset;
        TypedArray ta = context.getResources().obtainTypedArray(R.array.contacts_color);
        colorArray = new int[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            colorArray[i] = ta.getColor(i, 0);
        }
        ta.recycle();
    }

    @Override
    public EarnRffralAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reffral_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
         ContactItem contactItem = mDataset.get(position);

        holder.textview_name.setText(mDataset.get(position).getContactName());
        setUserImage(holder, contactItem,position);


    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
       ImageView imageview_profile;
       ImageView user_image;
       public final TextView userInitial;
       TextView  textview_name;

        public ViewHolder(View itemView) {
            super(itemView);
            user_image = (ImageView) itemView.findViewById(R.id.user_image);
            userInitial = (TextView)itemView.findViewById(R.id.user_initial);
            //imageview_profile = (ImageView)itemView.findViewById(R.id.imageview_profile);
            textview_name = (TextView) itemView.findViewById(R.id.textview_name);
        }
    }

    public void setContactList(List<ContactItem> contactItems, boolean search,InviteFragmentListener inviteFragmentListener) {
        mDataset = contactItems;
        this.search = search;
        this.inviteFragmentListener = inviteFragmentListener;
    }


    private void setUserImage(ViewHolder holder, ContactItem contactItem, int position) {
        if (!contactItem.getContactImage().isEmpty()) {
            holder.userInitial.setVisibility(View.GONE);
            holder.user_image.setVisibility(View.VISIBLE);
            try {
                Picasso.with(context).load(contactItem.getContactImage()).placeholder(R.drawable.ic_user_image).into(holder.user_image);
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


}
