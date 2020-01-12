package com.arkadiy.enter.smp3.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.arkadiy.enter.smp3.R;
import com.arkadiy.enter.smp3.config.ResponseCode;
import com.arkadiy.enter.smp3.dataObjects.BuildUser;
import com.arkadiy.enter.smp3.dataObjects.Department;
import com.arkadiy.enter.smp3.dataObjects.Manager;
import com.arkadiy.enter.smp3.dataObjects.User;
import com.arkadiy.enter.smp3.services.GlobalServices;
import com.arkadiy.enter.smp3.utils.Constants;

import java.util.List;

public class AddNewUserActivity extends AppCompatActivity {
    private long  CREATE_USER = 0;
    private int   ACTION= 0;
    private BuildUser buildUser;
    private Button creatUserButton;
    private EditText addNewUserNameEditText;
    private EditText addNewUserPasswordEditText;
    private EditText addNewUserFirstNameEditText;
    private EditText addNewUserSecondNameEditText;
    private Spinner  rollsUser;
    private Spinner  departmentsSinner;
    private ArrayAdapter<String> departmentsAdapter;
    private List<String> departmentList;
    private ArrayAdapter<String> rollAdapter;
    //private EditText addNewUserRoleEditText;
    //private EditText addNewUserManagerEditText;
    private EditText addNewUserCityEditText;
    private EditText addNewUserStreetEditText;
    private EditText addNewUserHouseNumberEditText;
    private EditText addNewUserDoorNumberEditText;
    private EditText addNewUserTelephoneEditText;
    private EditText addNewUserEmailEditText;
    private RadioGroup sexRadioGroup;
    private RadioButton sexRadioBatton;
    private Context context;
    private List<String> rollNames;
    private int rollId;
    private long departmentId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_user);


        App.setContext(this);
        rollNames = Manager.getRollNames();
        addNewUserNameEditText = (EditText)findViewById(R.id.addNewUserName_editText);
        addNewUserPasswordEditText = (EditText)findViewById(R.id.addNewUserPassword_editText);
        addNewUserFirstNameEditText = (EditText)findViewById(R.id.addNewUserFirstName_EditText);
        addNewUserSecondNameEditText = (EditText)findViewById(R.id.addNewUserSecondName_EditText);
        rollsUser = (Spinner) findViewById(R.id.addNewUserRole_Spinner);
        departmentsSinner = (Spinner) findViewById(R.id.addNewUserManager_Spinner);
        addNewUserCityEditText = (EditText)findViewById(R.id.addNewUserCity_EditText);
        addNewUserStreetEditText = (EditText)findViewById(R.id.addNewUSerStreet_EditText);
        addNewUserHouseNumberEditText = (EditText)findViewById(R.id.addNewUSerHouseNumber_EditText);
        addNewUserDoorNumberEditText = (EditText)findViewById(R.id.addNewUserDoorNumber_EditText);
        addNewUserTelephoneEditText = (EditText)findViewById(R.id.addNewUserTelephone_EditText);
        addNewUserEmailEditText = (EditText)findViewById(R.id.addNewUserEmail_EditText);
        sexRadioGroup = (RadioGroup)findViewById(R.id.sex_RadioGroup);

        creatUserButton = (Button) findViewById(R.id.addNewUser_Button);

        departmentList = Department.getDepartmentName();
        rollAdapter = new ArrayAdapter(App.getContext(),android.R.layout.simple_spinner_dropdown_item,rollNames);
        departmentsAdapter = new ArrayAdapter(App.getContext(),android.R.layout.simple_spinner_dropdown_item,departmentList);
        rollsUser.setAdapter(rollAdapter);
        departmentsSinner.setAdapter(departmentsAdapter);
        context = this;

        creatUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(addNewUserNameEditText.getText().toString() != ""
                       && addNewUserPasswordEditText.getText().toString()   !=""
                       && addNewUserFirstNameEditText.getText().toString()  !=""
                       && addNewUserSecondNameEditText.getText().toString() !=""
                       //&& addNewUserManagerEditText.getText().toString()    !=""
                       && addNewUserCityEditText.getText().toString()       !=""
                       && addNewUserStreetEditText.getText().toString()     !=""
                       && addNewUserHouseNumberEditText.getText().toString()!=""
                       && addNewUserDoorNumberEditText.getText().toString() !=""
                       && addNewUserTelephoneEditText.getText().toString()  !=""
                       && addNewUserEmailEditText.getText().toString()      !="" ){


                   int radioButtonCheked = sexRadioGroup.getCheckedRadioButtonId();
                   sexRadioBatton  = findViewById(radioButtonCheked);
                   int sex = 1;
                   if (sexRadioBatton.getText() == "Male"){
                       sex = 2;
                   }
                   buildUser = new BuildUser(CREATE_USER,addNewUserNameEditText.getText().toString(),
                           addNewUserFirstNameEditText.getText().toString(),
                           addNewUserSecondNameEditText.getText().toString(),
                           addNewUserPasswordEditText.getText().toString(),
                           addNewUserCityEditText.getText().toString(),
                           addNewUserStreetEditText.getText().toString(),
                           Integer.parseInt(addNewUserHouseNumberEditText.getText().toString())
                           , Integer.parseInt(addNewUserDoorNumberEditText.getText().toString()),
                           addNewUserTelephoneEditText.getText().toString(), addNewUserEmailEditText.getText().toString(),
                           rollId,departmentId,sex, ACTION,context,iHandler->{

                       Bundle bundle = iHandler.getData();
                       if (bundle.getInt(Constants.RESPONSE_CODE) < ResponseCode.ERROR){
//                           Toast.makeText(App.getContext(),"User has been created",Toast.LENGTH_LONG).show();
                           resetViews();
                       }
                       else {
                           Toast.makeText(App.getContext(),"User not created",Toast.LENGTH_LONG).show();
                       }

                       return true;
                   });

               }


            }

        });

        rollsUser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Manager.getRolleIdByName(rollNames.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        departmentsSinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                departmentId = Department.getDepartmentIdByName(departmentList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbarMine);
        setSupportActionBar(toolbar);
        GlobalServices.addListener(toolbar,this);    }



    private void resetViews(){

        Intent intent = new Intent(AddNewUserActivity.this,SavedActivity.class);
        intent.putExtra("activity",AdminActivity.class.getName());
        startActivity(intent);

//        addNewUserNameEditText.setText("");
//        addNewUserPasswordEditText.setText("");
//        addNewUserFirstNameEditText.setText("");
//        addNewUserSecondNameEditText.setText("");
        //rollsUser = (Spinner) findViewById(R.id.addNewUserRole_Spinner);
        //departmentsSinner = (Spinner) findViewById(R.id.addNewUserManager_Spinner);
//        addNewUserCityEditText.setText("");
//        addNewUserStreetEditText.setText("");
//        addNewUserHouseNumberEditText.setText("");
//        addNewUserDoorNumberEditText.setText("");
//        addNewUserTelephoneEditText.setText("");
//        addNewUserEmailEditText.setText("");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

}
