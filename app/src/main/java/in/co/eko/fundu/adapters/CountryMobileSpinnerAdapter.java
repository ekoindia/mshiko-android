package in.co.eko.fundu.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import in.co.eko.fundu.models.CountryMobile;

/**
 * Created by eko on 16/11/16.
 */

public class CountryMobileSpinnerAdapter extends ArrayAdapter {

    private CountryMobile[] objects;
    private LayoutInflater inflater;


    public CountryMobileSpinnerAdapter(Context context, int resource, Object[] objects) {
        super(context, resource, objects);
        this.objects = (CountryMobile[]) objects;
        this.inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        ((TextView) convertView).setGravity(View.TEXT_ALIGNMENT_CENTER);
        ((TextView) convertView).setText(objects[position].getCountryName().trim());
        return convertView;
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return objects[position].getCountryName();
    }

    public CountryMobile getCountryItem(int position) {
        return objects[position];
    }
}
