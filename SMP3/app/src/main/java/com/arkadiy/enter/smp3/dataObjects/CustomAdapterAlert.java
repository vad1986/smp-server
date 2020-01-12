package com.arkadiy.enter.smp3.dataObjects;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arkadiy.enter.smp3.R;
import com.arkadiy.enter.smp3.utils.LinkifiedTextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CustomAdapterAlert extends ArrayAdapter<Alert> {


    private List<Alert>alerts;
    private Activity context;


    public CustomAdapterAlert(Activity context, List<Alert>alert) {
        super(context, R.layout.customlayout,alert);
        this.context = context;
        this.alerts = alert;

    }



    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View r = convertView;
        ViewHolder viewHolder = null;
        if (r == null){
            LayoutInflater layoutInflater = context.getLayoutInflater();
            r = layoutInflater.inflate(R.layout.customlayout,null,true);
            viewHolder =new ViewHolder(r);
            r.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) r.getTag();
        }

        viewHolder.userNameTextView.setText(alerts.get(position).getName());
        viewHolder.imageView.setVisibility(View.VISIBLE);

//        viewHolder.idTextView.setText(alerts.get(position).getDescription());



        return r;
    }

    class ViewHolder{

        TextView userNameTextView;
        ImageView imageView;

        ViewHolder(View v){
            userNameTextView = (TextView) v.findViewById(R.id.alertName_textView);
            imageView=(ImageView)v.findViewById(R.id.imageCustom);


        }

    }

}
