package com.huanggusheng.flemerun;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Huang on 2015/10/24.
 */
public class RecordsAdapter extends ArrayAdapter<Records> {
    private int resourceID;
    public RecordsAdapter(Context context, int resource, List<Records> objects) {
        super(context, resource, objects);
        resourceID = resource;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Records records = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(getContext()).inflate(resourceID, null);
            viewHolder.txt_distance = (TextView) view.findViewById(R.id.item_distance);
            viewHolder.txt_speed = (TextView) view.findViewById(R.id.item_speed);
            viewHolder.txt_date = (TextView) view.findViewById(R.id.item_date);

            viewHolder.txt_distance.setText(String.valueOf(records.getDistance())+"km");
            viewHolder.txt_speed.setText(String.valueOf(records.getSpeed())+"km/h");
            viewHolder.txt_date.setText(String.valueOf(records.getDate()));

            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }
        return view;
    }
}
class ViewHolder{
    TextView txt_distance;
    TextView txt_speed;
    TextView txt_date;
}
