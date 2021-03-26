package in.co.eko.fundu.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;
import java.util.ArrayList;

import in.co.eko.fundu.R;
import in.co.eko.fundu.activities.MerchantProfile;
import in.co.eko.fundu.fragments.MapFragment;
import in.co.eko.fundu.models.Neighbour;
import in.co.eko.fundu.stickyrecyclerview.StickyRecyclerHeadersAdapter;
import in.co.eko.fundu.utils.Fog;
import in.co.eko.fundu.utils.Utils;

/**
 * Created by user on 4/11/17.
 */

public class MapSearchAdapter extends RecyclerView.Adapter<MapSearchAdapter.ViewHolder> implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder>, View.OnClickListener {

    private final Context context;
    private ArrayList<Neighbour> contacts = new ArrayList<>();

    public MapSearchAdapter(Context context, ArrayList<Neighbour> contacts) {
        this.context = context;
//        this.contacts = contacts;
        setHasStableIds(true);
    }

    public void setList(ArrayList<Neighbour> contacts) {
        this.contacts = contacts;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView user_name;
        TextView address, date;
        RelativeLayout parent;

        public ViewHolder(View itemView) {
            super(itemView);
            user_name = (TextView) itemView.findViewById(R.id.shopname);
            address = (TextView) itemView.findViewById(R.id.address);
            date = (TextView) itemView.findViewById(R.id.timings);
            parent = (RelativeLayout) itemView.findViewById(R.id.parent);
            parent.setOnClickListener(MapSearchAdapter.this);
        }
    }

    @Override
    public MapSearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_map_search, parent, false);
        return new MapSearchAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MapSearchAdapter.ViewHolder holder, int position) {
//        if(position == 3 || position == 5 || position == 9 || position == 7 || position == 15)
//        holder.transactionTag.setImageLevel(1);
        holder.parent.setTag(position);
        holder.user_name.setText(contacts.get(position).getBusiness_name());
        holder.address.setText("Address : " + contacts.get(position).getPhysical_location());
        if (contacts.get(position).getOpening_time() != null) {
            String opentime, closetime;
            int selectedopeninghour = Integer.parseInt(contacts.get(position).getOpening_time().split(":")[0]);
            int selectedopeningminute = Integer.parseInt(contacts.get(position).getOpening_time().split(":")[1]);
            if (selectedopeninghour <= 12)
                opentime = new DecimalFormat("00").format(selectedopeninghour) + ":" + new DecimalFormat("00").format(selectedopeningminute) + " AM";
            else
                opentime = new DecimalFormat("00").format(selectedopeninghour - 12) + ":" + new DecimalFormat("00").format(selectedopeningminute) + " PM";
            int selectedclosinghour = Integer.parseInt(contacts.get(position).getClosing_time().split(":")[0]);
            int selectedclosingminute = Integer.parseInt(contacts.get(position).getClosing_time().split(":")[1]);
            if (selectedclosinghour <= 12)
                closetime = new DecimalFormat("00").format(selectedclosinghour) + ":" + new DecimalFormat("00").format(selectedclosingminute) + " AM";
            else
                closetime = new DecimalFormat("00").format(selectedclosinghour - 12) + ":" + new DecimalFormat("00").format(selectedclosingminute) + " PM";

            holder.date.setText("Opening Time : " + opentime +
                    "\nClosing Time : " + closetime);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.parent:
                if (Utils.isNetworkAvailable(context)) {
                    int position = (Integer) v.getTag();
//                    Utils.showShortToast(context,contacts.get(position).getBusiness_name());
                    Intent intent = new Intent(context, MerchantProfile.class);
                    Fog.e("PutCOn", contacts.get(position).getBusiness_name());
                    intent.putExtra("merchant_data", contacts.get(position));
                    MapFragment.selectedLatLong = new LatLng(contacts.get(position).getLocation().coordinates[1], contacts.get(position).getLocation().coordinates[0]);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    context.startActivity(intent);
                }
                break;
        }
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
        if (contacts != null)
        return contacts.size();
        else return 0;
    }

}
