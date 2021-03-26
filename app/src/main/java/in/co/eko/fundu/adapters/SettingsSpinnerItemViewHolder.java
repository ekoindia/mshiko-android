package in.co.eko.fundu.adapters;
/*
 * Created by Bhuvnesh
 */

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import in.co.eko.fundu.R;

public class SettingsSpinnerItemViewHolder extends RecyclerView.ViewHolder {
    private TextView itemName;
    private Spinner spinner;

    public SettingsSpinnerItemViewHolder(View itemView) {
        super(itemView);
        itemName = (TextView) itemView.findViewById(R.id.tv_item_name);
        spinner = (Spinner) itemView.findViewById(R.id.spinner);

    }

    public TextView getItem() {
        return itemName;
    }

    public void setItem(TextView item) {
        this.itemName = item;
    }

    public Spinner getSpinner() {
        return spinner;
    }

    public void setSpinner(Spinner aSwitch) {
        this.spinner = aSwitch;
    }
}
