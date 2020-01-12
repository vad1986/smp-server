package com.arkadiy.enter.smp3.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DownloadManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.arkadiy.enter.smp3.R;
import com.arkadiy.enter.smp3.dataObjects.GetDate;
import com.arkadiy.enter.smp3.dataObjects.Manager;
import com.arkadiy.enter.smp3.dataObjects.Report;
import com.arkadiy.enter.smp3.dataObjects.Store;
import com.arkadiy.enter.smp3.dataObjects.User;
import com.arkadiy.enter.smp3.services.GlobalServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TasksReportActivity extends AppCompatActivity {
    private EditText fromDateTime;
    private EditText toDateTime;
    private Spinner typeReportSpinner;
    private Spinner groupUsersSpinner;
    private Spinner allUsersOrDepartmentsSpinner;
    private List<String> typeReport;
    private ArrayAdapter typeReportAdapter;
    private ArrayAdapter groupReportAdapter;
    private ArrayAdapter allUserOrDepartmentsAdapter;

    private String []groupUsers = {"All user","One user","Departments"};
    private String users;
    private int reportId;
    private int groupId;
    private long userId;
    private long departmentId;
    private RequestQueue requestQueue;
    private Button getReportButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_report);
        App.setContext(TasksReportActivity.this);
        requestQueue = Volley.newRequestQueue(this);
        getReportButton = (Button)findViewById(R.id.getReport_button);
        fromDateTime = (EditText) findViewById(R.id.editFromReport_EditText);
        toDateTime = (EditText) findViewById(R.id.editToReport_EditText);
        typeReportSpinner =(Spinner)findViewById(R.id.typeReport_Spinner);
        groupUsersSpinner  =(Spinner)findViewById(R.id.groupUsers_Spinner);
        allUsersOrDepartmentsSpinner = (Spinner)findViewById(R.id.forUser_Spinner);
        allUsersOrDepartmentsSpinner.setVisibility(View.GONE);
        fillReportList();

        typeReportAdapter = new ArrayAdapter(TasksReportActivity.this,R.layout.support_simple_spinner_dropdown_item,typeReport);
        typeReportSpinner.setAdapter(typeReportAdapter);
        groupReportAdapter = new ArrayAdapter(App.getContext(),R.layout.support_simple_spinner_dropdown_item,groupUsers);
        groupUsersSpinner.setAdapter(groupReportAdapter);

        allUserOrDepartmentsAdapter = new ArrayAdapter(App.getContext(),R.layout.support_simple_spinner_dropdown_item,Store.getAllUserName());
        allUsersOrDepartmentsSpinner.setAdapter(allUserOrDepartmentsAdapter);

        fromDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetDate.getDate(fromDateTime);
            }
        });
        toDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetDate.getDate(toDateTime);
            }
        });
        //Choose a type report
        typeReportSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                reportId = position +1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //Choose a group
        groupUsersSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){
                    groupId = position;
                    allUsersOrDepartmentsSpinner.setVisibility(View.GONE);
                }else if (position == 1){
                    groupId = position;
                    allUsersOrDepartmentsSpinner.setVisibility(View.VISIBLE);
                    allUserOrDepartmentsAdapter = new ArrayAdapter(App.getContext(),R.layout.support_simple_spinner_dropdown_item,Store.getAllUserName());
                    allUsersOrDepartmentsSpinner.setAdapter(allUserOrDepartmentsAdapter);


                }
                else if (position == 2){
                    groupId = position;
                    allUsersOrDepartmentsSpinner.setVisibility(View.VISIBLE);
                    allUserOrDepartmentsAdapter = new ArrayAdapter(App.getContext(),R.layout.support_simple_spinner_dropdown_item,Store.getAllDepartmentsName());
                    allUsersOrDepartmentsSpinner.setAdapter(allUserOrDepartmentsAdapter);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // Choose one user or one departments
        allUsersOrDepartmentsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (groupId == 2 ){
                    String departmentName = Store.getAllDepartmentsName().get(position);
                    departmentId = getDepartmentIdByName(departmentName);
                }else if (groupId == 1){

                    String userName = Store.getAllUserName().get(position);
                   userId = getUserIdByName(userName);
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });

        getReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildJSon(reportId,fromDateTime.getText().toString(),toDateTime.getText().toString(),groupId,userId);
            }
        });

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbarMine);
        setSupportActionBar(toolbar);
        GlobalServices.addListener(toolbar,this);
    }

    private void buildJSon(int reportId, String fromDateTime, String toDateTime, int groupId, long userId) {

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("report_id",reportId);
            jsonObject.put("from_date_time",fromDateTime);
            jsonObject.put("to_date_time",toDateTime);
            jsonObject.put("group",groupId);
            jsonObject.put("id",userId);
//            jsonObject.put("mail", User.getMyEmail());
            jsonObject.put("mail", "enterz.ae@gmail.com");
            Manager.getReport(requestQueue,jsonObject);


        }catch (JSONException e){
            e.printStackTrace();
        }

    }

    private void fillReportList() {
        typeReport = new ArrayList<>();
        Report report = new Report(1,"Task Report");
        Report report1 = new Report(2,"Attendance Report");
        typeReport.add(report.getName());
        typeReport.add(report1.getName());
    }

    public long getDepartmentIdByName(String departmentName){
        long departmentId = -1;
        for (int i = 0 ; i < Store.getDepartments().size() ; i++){
            if (departmentName == Store.getDepartments().get(i).getName()){
                departmentId = Store.getDepartments().get(i).getId();
                break;
            }
        }
        return departmentId;
    }

    public long getUserIdByName(String useName){
        long userId = -1;

        for (int i = 0 ; i < Store.getAllUsers().size() ; i++){
            if (useName == Store.getAllUsers().get(i).getUserName()){
                userId = Store.getDepartments().get(i).getId();
                break;
            }
        }
        return userId;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }
}
