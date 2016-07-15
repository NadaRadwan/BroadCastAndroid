package com.example.nada.broadcast;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Nada on 2016-07-14.
 */
public class RecordingAdapter extends ArrayAdapter<Recording> {

    ArrayList<Recording> data = null;
    Context context;
    int layoutResourceId;

    public RecordingAdapter(Context context, int layoutResourceId, ArrayList<Recording> data){
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RecordingHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new RecordingHolder();
            holder.txt = (TextView)row.findViewById(R.id.recordingtextinfo);

            row.setTag(holder);
        }
        else
        {
            holder = (RecordingHolder)row.getTag();
        }

        Recording recording = getItem(position);
        holder.txt.setText(recording.title);

        return row;
    }

    static class RecordingHolder
    {
        TextView txt;
    }
}
