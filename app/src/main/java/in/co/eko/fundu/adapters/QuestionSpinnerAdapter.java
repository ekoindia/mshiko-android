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

import in.co.eko.fundu.R;
import in.co.eko.fundu.models.QuestionModel;

/**
 * Created by Rahul on 12/13/16.
 */

public class QuestionSpinnerAdapter extends ArrayAdapter {

    private QuestionModel[] objects;
    private LayoutInflater inflater;


    public QuestionSpinnerAdapter(Context context, int resource, Object[] objects) {
        super(context, resource, objects);
        this.objects = (QuestionModel[]) objects;
        this.inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = inflater.inflate(R.layout.spinner_item, parent, false);
        ((TextView) convertView).setGravity(View.TEXT_ALIGNMENT_CENTER);
        ((TextView) convertView).setText(objects[position].getQuestion_value().trim());
        return convertView;
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return objects[position].getQuestion_value();
    }

    public QuestionModel getQuestionItem(int position) {
        return objects[position];
    }
}

