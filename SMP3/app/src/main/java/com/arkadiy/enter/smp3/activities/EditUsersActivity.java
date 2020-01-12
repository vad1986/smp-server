package com.arkadiy.enter.smp3.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.QuickContactBadge;

import com.arkadiy.enter.smp3.R;
import com.arkadiy.enter.smp3.dataObjects.Admin;
import com.arkadiy.enter.smp3.dataObjects.CustomAdapterEditUsers;
import com.arkadiy.enter.smp3.dataObjects.User;
import com.arkadiy.enter.smp3.services.GlobalServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class EditUsersActivity extends AppCompatActivity {

    private ListView editUsersLestView;
    private ArrayList<User>allUsers;
    private CustomAdapterEditUsers userCustomAdapterEditUsers;
    private EditText searchUserEditText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_users);
        editUsersLestView = (ListView)findViewById(R.id.editUsers_ListView);
        searchUserEditText = (EditText)findViewById(R.id.searchUser_EditText);
        initList();
        searchUserEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")){

                    //reset listView
                    initList();
                }else{
                    searchItem(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editUsersLestView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(EditUsersActivity.this,SelecedEditUserActivity.class);
                intent.putExtra("editUser",User.parsFromObjToJSON(allUsers.get(position)).toString());
                startActivity(intent);

            }
        });

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbarMine);
        setSupportActionBar(toolbar);
        GlobalServices.addListener(toolbar,this);
    }

    private void searchItem(String textToSearch) {
        try {

            for (Iterator<User> user = allUsers.iterator(); user.hasNext();){
                User userSelect = user.next();
                if (!userSelect.getUserName().contains(textToSearch)){

                    user.remove();
                }
            }
            userCustomAdapterEditUsers.notifyDataSetChanged();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void initList(){
        allUsers = new ArrayList<>(Admin.getAllUsers());
        userCustomAdapterEditUsers = new CustomAdapterEditUsers(EditUsersActivity.this, allUsers);
        editUsersLestView.setAdapter(userCustomAdapterEditUsers);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }
}
