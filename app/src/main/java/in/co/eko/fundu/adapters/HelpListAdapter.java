package in.co.eko.fundu.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import in.co.eko.fundu.R;
import in.co.eko.fundu.activities.Acknowledgement;
import in.co.eko.fundu.activities.DescribeIssue;

/**
 * Created by pallavi on 8/10/17.
 */

public class HelpListAdapter extends RecyclerView.Adapter<HelpListAdapter.ViewHolder> {

   String helpList[];
   Context context;

    public HelpListAdapter(String[] helpList, Context context) {
        this.helpList = helpList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_help, parent, false);
        return new ViewHolder(view);
    }

    private String issue;
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.helpIssue.setText(helpList[position]);
          issue = holder.helpIssue.getText().toString();


    }

    @Override
    public int getItemCount() {
        return helpList.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView helpIssue;
        public ViewHolder(final View itemView) {
        super(itemView);
        helpIssue = (TextView)itemView.findViewById(R.id.helpIssue);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(getAdapterPosition()==2){

                        context.startActivity(new Intent(context,DescribeIssue.class));

                    }
                    else {
                        context.startActivity(new Intent(context,Acknowledgement.class));
                    }
                }
            });

        }
}

}
