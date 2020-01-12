package com.arkadiy.enter.smp3.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.arkadiy.enter.smp3.R;
import com.arkadiy.enter.smp3.dataObjects.Admin;
import com.arkadiy.enter.smp3.dataObjects.Department;
import com.arkadiy.enter.smp3.dataObjects.Manager;
import com.arkadiy.enter.smp3.dataObjects.Store;
import com.arkadiy.enter.smp3.services.GlobalServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BuildDepartmentActivity extends AppCompatActivity {
    private EditText nameDepartmentEditText;
    private Switch departmentsStatusSwitch;
    private Spinner departmentManagetSpinner;
    private Button sendNewDepartment;
    private ArrayAdapter<Manager> departmentSpinnerAdapter;
    private List<String> departmentsManagerName ;
    private ListView departmentsListView;
    private int removeDepartment = 0; // 1 == yes 0 == no
    private long managerId;
    private long dapertmentId = 0;
    private RequestQueue requestQueue;

    private ArrayAdapter<String>departmentsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
        App.setContext(BuildDepartmentActivity.this);
         JSONObject jsonDepartment = new JSONObject();
        setContentView(R.layout.activity_build_department);
        requestQueue = Volley.newRequestQueue(BuildDepartmentActivity.this);
        nameDepartmentEditText = (EditText)findViewById(R.id.add_new_name_department);
        departmentsStatusSwitch = (Switch) findViewById(R.id.status_department);
        departmentManagetSpinner = (Spinner)findViewById(R.id.departments_manager_spinner);
        sendNewDepartment = (Button)findViewById(R.id.send_new_department);
        departmentsManagerName = new ArrayList<>();
        departmentsManagerName = Admin.getNameManagersList();
        departmentsListView = (ListView)findViewById(R.id.department_listView);
        departmentsAdapter = new ArrayAdapter<String>(BuildDepartmentActivity.this, android.R.layout.simple_list_item_1);
        for (int i =0 ;i<Store.departments.size();i++){

            departmentsAdapter.add(Store.getDepartments().get(i).getName());
        }

        departmentsListView.setAdapter(departmentsAdapter);

            departmentSpinnerAdapter = new ArrayAdapter(App.getContext(),android.R.layout.simple_spinner_dropdown_item,departmentsManagerName);
            departmentManagetSpinner.setAdapter(departmentSpinnerAdapter);
//        departmentSpinnerAdapter = new ArrayAdapter<Manager>(BuildDepartmentActivity.this,R.layout.support_simple_spinner_dropdown_item,Store.managers);
//        departmentManagetSpinner.setAdapter(departmentSpinnerAdapter);


        departmentsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(BuildDepartmentActivity.this, departmentsAdapter.getItem(position),Toast.LENGTH_LONG).show();
                for (Department d:Store.departments){
                    if (departmentsAdapter.getItem(position) == d.getName()){
                        nameDepartmentEditText.setText(d.getName());
                        departmentsStatusSwitch.setChecked(true);

                        ArrayAdapter myAd=(ArrayAdapter)departmentManagetSpinner.getAdapter();
                        int index= myAd.getPosition(d.getManager().getUserName());
                        departmentManagetSpinner.setSelection(index);
                        managerId =  d.getManager().getUserId();
                        dapertmentId = d.getId();

                    }
                }
            }
        });


        departmentsStatusSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked){
                        Toast.makeText(App.getContext(),"--> ON <--",Toast.LENGTH_LONG).show();
                        removeDepartment = 0;
                    }else {
                        Toast.makeText(App.getContext(),"--> OFF <--",Toast.LENGTH_LONG).show();
                        removeDepartment = 1;
                    }
                }
        });

        sendNewDepartment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (nameDepartmentEditText.getText().toString() !=""){

                    try {


                        jsonDepartment.put("name",nameDepartmentEditText.getText().toString());
                        jsonDepartment.put("manager_id",managerId);
                        jsonDepartment.put("department_id",dapertmentId);
                        jsonDepartment.put("remove",removeDepartment);

                        Admin.createOrEditDepartment(App.getContext(),jsonDepartment,requestQueue,departmentHandler->{

                            return true;
                        });

                    }catch (JSONException e){
                        e.printStackTrace();
                    }


                }

            }
        });

        departmentManagetSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                managerId = Manager.getManagerIdByName(departmentsManagerName.get(position));


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        }catch (Exception e){
            e.printStackTrace();
        }

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbarMine);
        setSupportActionBar(toolbar);
        GlobalServices.addListener(toolbar,this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }
}
