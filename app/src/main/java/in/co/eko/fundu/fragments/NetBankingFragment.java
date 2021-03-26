package in.co.eko.fundu.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import in.co.eko.fundu.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NetBankingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NetBankingFragment extends BaseFragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;



    public static NetBankingFragment newInstance(String param1, String param2) {
        NetBankingFragment fragment = new NetBankingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public NetBankingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_net_banking, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.RecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new RecyclerAdapter());
        recyclerView.setHasFixedSize(true);
        return view;
    }

    private static class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_net_banking, parent, false); //Inflating the layout
            return new ViewHolder(v, viewType);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bankName.setText(bankNames[position]);
            holder.bankImage.setImageResource(bankImageResId[position]);
        }

        @Override
        public int getItemCount() {
            return bankNames.length;
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            int HolderId;

            ImageView bankImage;
            TextView bankName;

            public ViewHolder(final View itemView, int ViewType) {
                super(itemView);
                bankName = (TextView) itemView.findViewById(R.id.bankName);
                bankImage = (ImageView) itemView.findViewById(R.id.bank_image);


            }

        }
        private static final Integer bankImageResId[] = {
                R.drawable.ic_axis_bank,
                R.drawable.ic_icici_bank,
                R.drawable.ic_sbi_logo,
                R.drawable.ic_hdfc_bank_logo,
                R.drawable.ic_citi_bank

        };

        private static final String bankNames[] = {
                "AXIS Bank",
                "Icic Bank",
                "State Bank of India",
                "HDFC Bank",
                "City Bank"

        };
    }
    @Override
    public boolean onBackPressed() {
        return false;
    }
}
