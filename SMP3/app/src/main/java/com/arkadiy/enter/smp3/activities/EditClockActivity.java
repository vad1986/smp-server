package com.arkadiy.enter.smp3.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.arkadiy.enter.smp3.R;
import com.arkadiy.enter.smp3.dataObjects.CustomAdapterHours;
import com.arkadiy.enter.smp3.dataObjects.DayWork;
import com.arkadiy.enter.smp3.dataObjects.Department;
import com.arkadiy.enter.smp3.dataObjects.GetDate;
import com.arkadiy.enter.smp3.dataObjects.Manager;
import com.arkadiy.enter.smp3.dataObjects.Store;
import com.arkadiy.enter.smp3.dataObjects.User;
import com.arkadiy.enter.smp3.services.GlobalServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class EditClockActivity extends AppCompatActivity {
    private Department d;
    private List<User> uList;
    private List<String> userName;
    private Button getShiftsFromUser;
    private Button createNewShift;
    private Spinner userSpinner;
    private CustomAdapterHours customAdapterHours;
    private ArrayList<DayWork> listWork;
    private ArrayAdapter<User> arrayAdapterUsers;

    private ListView hoursWork;
    private RequestQueue requestQueue;
    private long userId;
    private JSONObject jsonClock;
    private JSONObject jsonRes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_clock);
        App.setContext(EditClockActivity.this);

        userSpinner = (Spinner)findViewById(R.id.userNameEditHours_Spinner);
        hoursWork = (ListView)findViewById(R.id.editHours_listView);
        getShiftsFromUser = (Button)findViewById(R.id.getShiftUserById_Button);
        createNewShift = (Button)findViewById(R.id.sendNewDay_Buttom);
        requestQueue = Volley.newRequestQueue(EditClockActivity.this);
        listWork = new ArrayList<DayWork>();

        d = Store.getDepartmentById(Manager.getMyDepartmentId());
        if(d==null && Manager.getMyDepartmentId()==0){
            uList=Store.getAllUsers();
        }else{
            uList = d.getUsers();
        }
        userName = new ArrayList<>(getUserName(uList));
        arrayAdapterUsers = new ArrayAdapter(App.getContext(),R.layout.support_simple_spinner_dropdown_item,userName);
        userSpinner.setAdapter(arrayAdapterUsers);



        userSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0 ; i < userName.size() ; i++){
                    if (userName.get(position).equals(uList.get(i).getUserName())){
                        userId = uList.get(i).getUserId();

                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        getShiftsFromUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Manager.getShiftByUser(userId,requestQueue,houresHanler->{
                    Message msg = new Message();
                    Bundle bundle = houresHanler.getData();
                    try {
                        jsonRes = new JSONObject(bundle.getString("json"));
                        listWork =new ArrayList<>();
                        listWork = GetDate.setDayWork(jsonRes);
                        customAdapterHours = new CustomAdapterHours(EditClockActivity.this,listWork);
                        hoursWork.setAdapter(customAdapterHours);
//                        customAdapterHours.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return true;

                });
            }
        });

        hoursWork.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(EditClockActivity.this,EditUserAttendancy.class);
                    intent.putExtra("userId",String.valueOf(userId));

                    intent.putExtra("start_id",String.valueOf(listWork.get(position).getStartId()));
                    intent.putExtra("start",listWork.get(position).getStart());


                    intent.putExtra("finish_id",String.valueOf(listWork.get(position).getEndId()));
                    intent.putExtra("finish",listWork.get(position).getEnd());

                startActivity(intent);

            }
        });


        createNewShift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditClockActivity.this,EditUserAttendancy.class);
                intent.putExtra("userId",String.valueOf(userId));
                intent.putExtra("start_id",String.valueOf(-100));
                intent.putExtra("finish_id",String.valueOf(-100));
                startActivity(intent);
            }
        });


        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbarMine);
        setSupportActionBar(toolbar);
        GlobalServices.addListener(toolbar,this);


    }

    private List<String> getUserName(List<User>userList){
        List<String> userN = new ArrayList<>();
        for (int i = 0 ; i < userList.size() ; i++){

            userN.add(userList.get(i).getUserName());

        }
        return userN;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }
}
