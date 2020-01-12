package com.arkadiy.enter.smp3.dataObjects;

import android.app.Activity;
import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.arkadiy.enter.smp3.R;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapterEditUsers extends ArrayAdapter<User> {

    private ArrayList<User> users;
    private Activity contex;
    private String userName ="User Name: ";
    private String firstName="First Name: ";
    private String lastName="Last Name: ";
    public CustomAdapterEditUsers(Activity contex,ArrayList<User>users) {
        super(contex, R.layout.costumlayout_editu_users,users);
        this.contex = contex;
        this.users = users;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
     View r = convertView;

     ViewHolder viewHolder = null;
     if (r == null){
         LayoutInflater layoutInflater = contex.getLayoutInflater();
         r = layoutInflater.inflate(R.layout.costumlayout_editu_users,null,true);
         viewHolder = new ViewHolder(r);
         r.setTag(viewHolder);
     }else {
         viewHolder = (ViewHolder) r.getTag();
     }
     viewHolder.userNameTv.setText(users.get(position).getUserFirstName()+" "+users.get(position).getUserLastName());
     viewHolder.userDepartment.setText(Store.getDepartmentNameById(users.get(position).getDepartmentId()));

     return r;
    }

    class ViewHolder{
        TextView userNameTv;
        TextView userDepartment;
        TextView lastNameTv;

        ViewHolder(View v){
            userNameTv = (TextView)v.findViewById(R.id.userName_TextView);
            userDepartment = (TextView)v.findViewById(R.id.user_department_TextView);
        }

    }
}
