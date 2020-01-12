package com.arkadiy.enter.smp3.dataObjects;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.arkadiy.enter.smp3.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CustomAdapterHours extends ArrayAdapter<DayWork> {

    private List<DayWork>dayWorks;
    private Activity context;

    public CustomAdapterHours(Activity context, List<DayWork>dayWorks) {
        super(context, R.layout.customlayout_hours,dayWorks);
        this.dayWorks = dayWorks;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View r = convertView;
        ViewHolder viewHolder = null;
        if(r == null){
            LayoutInflater layoutInflater = context.getLayoutInflater();
            r = layoutInflater.inflate(R.layout.customlayout_hours,null,true);
            viewHolder = new ViewHolder(r);
            r.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)r.getTag();
        }

        viewHolder.dayDateTextView.setText(dayWorks.get(position).getDayWorkInWeek());
        viewHolder.startTimeTextView.setText(dayWorks.get(position).getStart());
        viewHolder.endTimeTextView.setText(dayWorks.get(position).getEnd());
        viewHolder.sumTimeTextView.setText(dayWorks.get(position).getSum());

        return r;
    }

    class ViewHolder{
        TextView dayDateTextView;
        TextView startTimeTextView;
        TextView endTimeTextView;
        TextView sumTimeTextView;
        ViewHolder(View v){
            dayDateTextView = (TextView)v.findViewById(R.id.day_TextView);
            startTimeTextView = (TextView)v.findViewById(R.id.startTime_TextView);
            endTimeTextView = (TextView)v.findViewById(R.id.endTime_TextView);
            sumTimeTextView = (TextView)v.findViewById(R.id.sumTime_TextView);
        }

    }
}
