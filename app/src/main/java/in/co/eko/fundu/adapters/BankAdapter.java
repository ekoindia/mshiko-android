package in.co.eko.fundu.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import in.co.eko.fundu.R;
import in.co.eko.fundu.models.BanksNameItem;

/**
 * Created by Rahul on 6/15/17.
 */

public class BankAdapter extends ArrayAdapter {

    private BanksNameItem[] objects;
    private LayoutInflater inflater;


    public BankAdapter(Context context, int resource, Object[] objects) {
        super(context, resource, objects);
        BanksNameItem pos0 = new BanksNameItem();
        pos0.setBankname(context.getString(R.string.select_bank));
        pos0.setBankcode("000000");
//        this.objects[0] = pos0;
        this.objects = (BanksNameItem[]) objects;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public boolean isEnabled(int position) {
        return position != 0;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        TextView tv = (TextView) view;
        if (position == 0) {
            // Set the hint text color gray
            tv.setTextColor(Color.GRAY);
        } else {
            tv.setTextColor(Color.BLACK);
        }
        return view;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = inflater.inflate(R.layout.spinner_item, parent, false);
        ((TextView) convertView).setGravity(View.TEXT_ALIGNMENT_CENTER);
        ((TextView) convertView).setText(objects[position].getBankname().trim());
        if (position == 0) {
            // Set the hint text color gray
            ((TextView) convertView).setTextColor(Color.GRAY);
        } else {
            ((TextView) convertView).setTextColor(Color.BLACK);
        }
        return convertView;
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return objects[position].getBankname();
    }

//    public BanksNameItem getQuestionItem(int position) {
//        return objects[position];
//    }
}

