package com.arkadiy.enter.smp3.services;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.RequestQueue;
import com.arkadiy.enter.smp3.R;
import com.arkadiy.enter.smp3.activities.App;
import com.arkadiy.enter.smp3.activities.MainActivity;
import com.arkadiy.enter.smp3.config.AppConfig;
import com.arkadiy.enter.smp3.dataObjects.User;
import com.arkadiy.enter.smp3.utils.Constants;

import androidx.appcompat.widget.Toolbar;

public class GlobalServices {
    static RequestQueue requestQueue;

    public static void addListener(Toolbar toolbar, Activity activity){
        GlobalServices.requestQueue=requestQueue;

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toolbar.collapseActionView();
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.home) {
                    Intent intent = new Intent(App.getContext(), MainActivity.class);
                    App.getContext().startActivity(intent);

                }else if(id == R.id.sign_out){
                    User.logOut();
                    DataServices.sendData(AppConfig.LOGOUT_SERVER,null,User.requestQueue,
                            activity, Constants.METHOD_POST,null);
                }
                return true;
            }
        });
    }
}
