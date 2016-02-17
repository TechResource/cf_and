package com.flightpathcore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.flightpathcore.R;
import com.flightpathcore.objects.JobObject;

import java.util.Arrays;
import java.util.List;

public class BaseSpinnerAdapter extends BaseAdapter {

    private Context context;
    private List<JobObject> jobsList;
    private boolean jobModel = true;
    private List<String> items ;

    public BaseSpinnerAdapter(Context context, List<JobObject> jobsList) {
        this.context = context;
        this.jobsList = jobsList;
        jobModel = true;
    }

    public BaseSpinnerAdapter(Context context, String[] entries) {
        this.context = context;
        this.items = Arrays.asList(entries);
        jobModel = false;
    }

    @Override
    public int getCount() {
        return jobModel ? jobsList.size() : items.size();
    }

    @Override
    public Object getItem(int position) {
        return jobModel ? jobsList.get(position) : items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.simple_dropdown_item_custom, parent, false);
        }

        TextView jobName = (TextView) convertView.findViewById(R.id.jobName);

        TextView date = (TextView) convertView.findViewById(R.id.date);
        date.setCompoundDrawables(null,null,null,null);

        if(jobModel) {
            date.setText(jobsList.get(position).fullDate);
            jobName.setText(jobsList.get(position).name);
        }else{
            jobName.setText(items.get(position));
        }

        return convertView;
    }

    @Override
    public View getView(int position, View viewToReuse, ViewGroup parentToGetAttached) {
        if (viewToReuse == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            viewToReuse = inflater.inflate(R.layout.simple_dropdown_item_custom, parentToGetAttached, false);
        }

        TextView jobName = (TextView) viewToReuse.findViewById(R.id.jobName);

        TextView date = (TextView) viewToReuse.findViewById(R.id.date);

        if(jobModel) {
            date.setText(jobsList.get(position).fullDate);
            jobName.setText(jobsList.get(position).name);
        }else{
            jobName.setText(items.get(position));
        }

        return viewToReuse;
    }
}
