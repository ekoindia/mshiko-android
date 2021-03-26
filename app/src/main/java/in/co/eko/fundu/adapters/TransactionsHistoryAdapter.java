package in.co.eko.fundu.adapters;
/*
 * Created by Bhuvnesh
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import in.co.eko.fundu.R;
import in.co.eko.fundu.stickyrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.ArrayList;
import java.util.List;

public class TransactionsHistoryAdapter  extends  RecyclerView.Adapter<TransactionsHistoryAdapter.ViewHolder> implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {
    private final Context context;
    private List<String> mDataset = new ArrayList<>();

    public TransactionsHistoryAdapter(Context context, ArrayList<String> strings) {
        this.context = context;
        this.mDataset = strings;
        setHasStableIds(true);
    }

    @Override
    public TransactionsHistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_transactions_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TransactionsHistoryAdapter.ViewHolder holder, int position) {

    }
    @Override
    public long getHeaderId(int position) {
        return mDataset.get(position).charAt(0);
    }

    @Override
    public long getSpeedDialListSize(int position) {
        return 5;
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sticky_header_calendar, parent, false);
        return new RecyclerView.ViewHolder(view) {};
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
        return mDataset.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{

        private final ImageView transactionTag;

        public ViewHolder(final View itemView) {
            super(itemView);
            transactionTag = (ImageView) itemView.findViewById(R.id.transaction_tag);

        }

    }
}
