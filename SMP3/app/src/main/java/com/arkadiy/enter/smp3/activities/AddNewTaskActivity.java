package com.arkadiy.enter.smp3.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.arkadiy.enter.smp3.R;
import com.arkadiy.enter.smp3.config.AppConfig;
import com.arkadiy.enter.smp3.dataObjects.Department;
import com.arkadiy.enter.smp3.dataObjects.GetDate;
import com.arkadiy.enter.smp3.dataObjects.Store;
import com.arkadiy.enter.smp3.dataObjects.Task;
import com.arkadiy.enter.smp3.dataObjects.User;
import com.arkadiy.enter.smp3.services.DataServices;
import com.arkadiy.enter.smp3.services.GlobalServices;
import com.arkadiy.enter.smp3.utils.Constants;

import java.util.ArrayList;
import java.util.Calendar;

public class AddNewTaskActivity extends AppCompatActivity  {
    private EditText endDateTask;
    private EditText startDateTask;
    private EditText nameTaskEditText;
    private EditText taskDecriptionEditText;
    private Calendar mCurrentDate;
    private Calendar mCurrentTime;
    private Button creatNewTaskButton;
    private Spinner workersSpinner;
    private Spinner departmantsSpinner;
    private ArrayList<User> workersItemsList;
    private ArrayList<Department> departmentsItemsList;
    private ArrayAdapter arrayAdapterWorkers;
    private ArrayAdapter arrayAdapterDepartments;
    private Task task;
    private RequestQueue requestQueue;
    private ArrayList<User>currentUsers;
    User selecteduser;
    private long id = 0;
    private Switch sw;
    private boolean personal=true;

    Department department;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_task);



        toBuildActivity();
        //Set current time
        startDateTask.setText(GetDate.getToDate());
        // set current time plus one hour
        endDateTask.setText(GetDate.plusHours(startDateTask.getText().toString()));
        App.setContext(this);
        getDepartmants();
        sw = (Switch)findViewById(R.id.switchUsers) ;

        if (User.getMyRole() == 0){
            workersSpinner.setVisibility(View.INVISIBLE);
            departmantsSpinner.setVisibility(View.INVISIBLE);
            sw.setVisibility(View.INVISIBLE);
            id = User.getMyUserId();
            personal=true;
        }
        startDateTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               //startDateTask.setText("");
                GetDate.getDate(startDateTask);
            }
        });

        endDateTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                endDateTask.setText("");
                GetDate.getDate(endDateTask);
            }
        });

        creatNewTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String start = startDateTask.getText().toString();
                String end = endDateTask.getText().toString();
                String taskName = nameTaskEditText.getText().toString() ;
                String description = taskDecriptionEditText.getText().toString();
                task = personal?new Task(id,start,end,taskName,description):new Task(start,end,taskName,description,id);
                //DataServices.sendTask(task.getTask(),requestQueue,AddNewTaskActivity.this);

                if(!taskName.equals("") && !start.equals("") && !end.equals("") && !description.equals("")){
                    User.sendNewTask(personal,AddNewTaskActivity.this,task.getTask(),requestQueue,sendTaskHandler->{

                        resetActivity();
                        return true;
                    });
                }else{
                    Toast.makeText(getApplicationContext(),"One of the fields is empty", Toast.LENGTH_LONG).show();

                }
            }
        });

//        departmantsSpinner.setOnItemSelectedListener(this);
//
        departmantsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    arrayAdapterWorkers.clear();
                    //Toast.makeText(getApplicationContext(), Store.GLOBAL_DEPARTMANTS.get(departmentsItemsList.get(i).getName()), Toast.LENGTH_LONG).show();
                    //Store.GLOBAL_DEPARTMANTS.get(departmentsItemsList.get(i).getName())-->get ID DEPARTMENT
                    setUsersByDepartmentSelected(Store.getDepartmentById(Store.GLOBAL_DEPARTMANTS.get(departmentsItemsList.get(i).getName())));
                    id=departmentsItemsList.get(i).getId();
                  //workersSpinner.setAdapter(arrayAdapterWorkers);
                }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        workersSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selecteduser = currentUsers.get(i);
                id = selecteduser.getUserId();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(!isChecked){
                    workersSpinner.setVisibility(View.INVISIBLE);
                    personal=false;
                    id=departmentsItemsList.get(departmantsSpinner.getSelectedItemPosition()).getId();
                }
                else {
                    workersSpinner.setVisibility(View.VISIBLE);
                    personal=true;
                    id=currentUsers.get(0).getUserId();

                }

            }
        });


        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbarMine);
        setSupportActionBar(toolbar);
        GlobalServices.addListener(toolbar,this);

    }
//

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    private void setUsersByDepartmentSelected(Department departmentSelected){
        arrayAdapterWorkers.clear(); //empty all array
        this.currentUsers = departmentSelected.getUsers();
        if(currentUsers.size() > 0){
            int size =currentUsers.size();
            for(int j = 0 ; j <size; j++){ //Run through all array of this department and get username
                arrayAdapterWorkers.add(currentUsers.get(j).getUserName());
                //MainActivity.USERS.get(i).get(j)  <--here you have user object
            }
            workersSpinner.setAdapter(arrayAdapterWorkers);
        }

    }



    private void getDepartmants() {


        String department = Store.rollesDate.get(User.getMyRole());
        switch (department){
            case "department_manager":
                departmentsItemsList.add(User.getMyDepartment());
                arrayAdapterDepartments.add(User.getMyDepartment().getName());

                break;
            case "administrator":
                for (int i = 0 ; i < Store.departments.size();i++)
                {
                    departmentsItemsList.add(Store.departments.get(i));
                    //arrayAdapterDepartments.add(Store.departments.get(i).getName());
                    arrayAdapterDepartments.add(Store.departments.get(i).getName());
                }

                break;
        }



        departmantsSpinner.setAdapter(arrayAdapterDepartments);
    }

// =============MOVE thise method to class GETDATE================
//    private void getDate(final EditText editText){
//
//        mCurrentDate = Calendar.getInstance();
//        mCurrentTime = Calendar.getInstance();
//        int year = mCurrentDate.get(Calendar.YEAR);
//        int month = mCurrentDate.get(Calendar.MONTH);
//        int day = mCurrentDate.get(Calendar.DAY_OF_MONTH);
//        final int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
//        final int minute = mCurrentTime.get(Calendar.MINUTE);
//        editText.setText("");
//
//        DatePickerDialog datePickerDialog = new DatePickerDialog(AddNewTaskActivity.this, new DatePickerDialog.OnDateSetListener() {
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void onDateSet(DatePicker datePicker, final int selectYear, final int selectMonth, final int selectDay) {
//
//                 //editText.setText(selectDay+"-"+selectMonth+"-"+selectYear);
//                 mCurrentDate.set(selectYear,selectMonth,selectDay);
//
//                TimePickerDialog timePickerDialog = new TimePickerDialog(AddNewTaskActivity.this, new TimePickerDialog.OnTimeSetListener() {
//
//                    @Override
//                    public void onTimeSet(TimePicker timePicker, int selectHour, int selectMinute) {
//
//                        editText.setText(selectDay+"-"+selectMonth+"-"+selectYear +" "+selectHour+":"+selectMinute);
//                        mCurrentTime = Calendar.getInstance();
//                        mCurrentDate.set(Calendar.HOUR_OF_DAY, selectHour);
//                        mCurrentDate.set(Calendar.MINUTE, selectMinute);
//                    }
//                },hour,minute,true);
//                timePickerDialog.show();
//            }
//        }, day, month,year);
//        datePickerDialog.show();
//
//    }

    private void toBuildActivity(){
        nameTaskEditText = (EditText)findViewById(R.id.nameTask_EditText);
        taskDecriptionEditText = (EditText)findViewById(R.id.taskDecription_EditText);
        startDateTask = (EditText)findViewById(R.id.editStartDate_EditText);
        endDateTask = (EditText)findViewById(R.id.editEnd_EditText);
        requestQueue = Volley.newRequestQueue(AddNewTaskActivity.this);
        workersSpinner = (Spinner)findViewById(R.id.workers_Spinner);
        departmantsSpinner = (Spinner)findViewById(R.id.departmants_Spinner);
        creatNewTaskButton = (Button)findViewById(R.id.creatNewTask_button);
        workersItemsList = new ArrayList<>();
        departmentsItemsList = new ArrayList<>();
//        DataServices.sendData(AppConfig.GET_MY_USERS,null,requestQueue,AddNewTaskActivity.this,Constants.METHOD_GET,null);
        arrayAdapterDepartments = new ArrayAdapter<String>(AddNewTaskActivity.this,android.R.layout.simple_spinner_dropdown_item);
        arrayAdapterWorkers = new ArrayAdapter<User>(AddNewTaskActivity.this,android.R.layout.simple_spinner_dropdown_item,workersItemsList);

    }
    private void resetActivity() {

        Intent intent = new Intent(AddNewTaskActivity.this,SavedActivity.class);
        intent.putExtra("activity",TasksActivity.class.getName());
        startActivity(intent);
    }


}
