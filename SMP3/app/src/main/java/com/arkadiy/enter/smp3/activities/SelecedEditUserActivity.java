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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.arkadiy.enter.smp3.R;
import com.arkadiy.enter.smp3.config.ResponseCode;
import com.arkadiy.enter.smp3.dataObjects.Admin;
import com.arkadiy.enter.smp3.dataObjects.BuildUser;
import com.arkadiy.enter.smp3.dataObjects.Department;
import com.arkadiy.enter.smp3.dataObjects.Manager;
import com.arkadiy.enter.smp3.dataObjects.User;
import com.arkadiy.enter.smp3.services.GlobalServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class SelecedEditUserActivity extends AppCompatActivity {

    private EditText editUserNameEditText;
    private EditText editUserPasswordEditText;
    private EditText editUserFirstNameEditText;
    private EditText editUserSecondNameEditText;
    //private EditText editUserRoleEditText;
    private Spinner editRooleUsersSpinner;
    //private EditText editUserManagerEditText;
    private EditText editUserCityEditText;
    private EditText editUserStreetEditText;
    private EditText editUserHouseNumberEditText;
    private EditText editUserDoorNumberEditText;
    private EditText editUserTelephoneEditText;
    private EditText editUserEmailEditText;
    private RadioGroup editSexRadioGroup;
    private RadioButton editMaleRadioButton;
    private RadioButton editFemaleRadioButton;

    private RadioGroup sexRadioGroup;
    private RadioButton sexRadioBatton;

    private BuildUser buildUser;
    private int ACTION = 1;
    private long userId;
    private Button SendUserToServerButton;
    private ArrayAdapter<String> rollAdapter;
    private List<String> rollNames;
    private int rollId;
    private long departmentId;
    private Spinner  departmentsSinner;
    private ArrayAdapter<String> departmentsAdapter;
    private List<String> departmentList;
    private int managerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleced_edit_user);

        try {
        rollNames = Manager.getRollNames();
        App.setContext(this);
        departmentList = Department.getDepartmentName();
        editUserNameEditText = (EditText)findViewById(R.id.editUserName_editText);
        editUserPasswordEditText = (EditText)findViewById(R.id.editUserPassword_editText);
        editUserFirstNameEditText = (EditText)findViewById(R.id.editUserFirstName_EditText);
        editUserSecondNameEditText = (EditText)findViewById(R.id.editUserSecondName_EditText);
        editRooleUsersSpinner = (Spinner) findViewById(R.id.editUserRole_Spinner);
        departmentsSinner = (Spinner) findViewById(R.id.editUserManager_Spinner);
        editUserCityEditText = (EditText)findViewById(R.id.editUserCity_EditText);
        editUserStreetEditText = (EditText)findViewById(R.id.editUserStreet_EditText);
        editUserHouseNumberEditText = (EditText)findViewById(R.id.editUserHouseNumber_EditText);
        editUserDoorNumberEditText = (EditText)findViewById(R.id.editUserDoorNumber_EditText);
        editUserTelephoneEditText = (EditText)findViewById(R.id.editUserTelephone_EditText);
        editUserEmailEditText = (EditText)findViewById(R.id.editUserEmail_EditText);
        editSexRadioGroup = (RadioGroup)findViewById(R.id.editSex_RadioGroup);
        editMaleRadioButton = (RadioButton)findViewById(R.id.editMale_RadioButton);
        editFemaleRadioButton = (RadioButton)findViewById(R.id.editFemale_RadioButton);
        SendUserToServerButton = (Button)findViewById(R.id.SendUserToServer_Button);
        departmentsAdapter = new ArrayAdapter<>(App.getContext(),android.R.layout.simple_spinner_dropdown_item,departmentList);
        rollAdapter = new ArrayAdapter<>(App.getContext(),android.R.layout.simple_spinner_dropdown_item,rollNames);
        editRooleUsersSpinner.setAdapter(rollAdapter);
        departmentsSinner.setAdapter(departmentsAdapter);
        String str = getIntent().getStringExtra("editUser");

//                    "gps" : 1,
//                    "sex" : 1,
//                    "city" : null,
//                    "email" : null,
//                    "status" : 2,
//                    "street" : null,
//                    "userID" : 2,
//                    "password" : "1234",
//                    "userName" : "arkadi",
//                    "userRole" : 1,
//                    "departmentId" : 3,
//                    "telephone" : null,
//                    "doorNumber" : null,
//                    "houseNumber" : null,
//                    "userLastName" : "Yusupov",
//                    "userFirstName" : "Arkadi"


            JSONObject JSuser = new JSONObject(str.toString());
            editUserNameEditText.setText(JSuser.getString("userName"));
            editUserPasswordEditText.setText(JSuser.getString("password"));
            editUserFirstNameEditText.setText(JSuser.getString("userFirstName"));
            editUserSecondNameEditText.setText(JSuser.getString("userLastName"));
            managerId = JSuser.getInt("managerId");
            rollId = JSuser.getInt("userRole");
            //choose rolle name
            ArrayAdapter myAd=(ArrayAdapter)editRooleUsersSpinner.getAdapter();
            int index= myAd.getPosition(Manager.getRolleNameById(rollId));
            editRooleUsersSpinner.setSelection(index);

            //editUserRoleEditText.setText(String.valueOf(JSuser.getInt("userRole")));
            departmentId = JSuser.getInt("departmentId");

            //choose department name
            ArrayAdapter myAddepartment=(ArrayAdapter)editRooleUsersSpinner.getAdapter();
            int indexDep= myAd.getPosition(Manager.getRolleNameById((int) departmentId));
            departmentsSinner.setSelection(indexDep);

            editUserCityEditText.setText(JSuser.getString("city"));
            editUserStreetEditText.setText(JSuser.getString("street"));
            editUserHouseNumberEditText.setText(String.valueOf(JSuser.getInt("houseNumber")));
            editUserTelephoneEditText.setText(String.valueOf(JSuser.getInt("telephone")));
            editUserEmailEditText.setText(JSuser.getString("email"));
            editUserDoorNumberEditText.setText(String.valueOf(JSuser.getInt("doorNumber")));
            userId = JSuser.getLong("userID");
            int sex = JSuser.getInt("sex");

            if (sex == 1 ){
                editFemaleRadioButton.setChecked(true);
            }
            else {
                editMaleRadioButton.setChecked(true);

            }
            editRooleUsersSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                   rollId =  Manager.getRolleIdByName(rollNames.get(position));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            SendUserToServerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buildUser = new BuildUser(userId,editUserNameEditText.getText().toString(),
                            editUserFirstNameEditText.getText().toString(),
                            editUserSecondNameEditText.getText().toString(),
                            editUserPasswordEditText.getText().toString(),
                            editUserCityEditText.getText().toString(),
                            editUserStreetEditText.getText().toString(),
                            Integer.parseInt(editUserHouseNumberEditText.getText().toString())
                            , Integer.parseInt(editUserDoorNumberEditText.getText().toString()),
                            editUserTelephoneEditText.getText().toString(), editUserEmailEditText.getText().toString(),
                            rollId,departmentId,sex,ACTION,SelecedEditUserActivity.this,iHandler->{
                        Message msg = new Message();
                        Bundle bundle = iHandler.getData();
                        if (bundle.getInt("response_code") < ResponseCode.ERROR){
                            Toast.makeText(SelecedEditUserActivity.this,"User value successfully executed",Toast.LENGTH_LONG).show();
                            try {

                            JSONObject userConverJson = new JSONObject();
                            userConverJson.put("userID",JSuser.getLong("userID"));

                                userConverJson.put("userName",editUserNameEditText.getText());

                            userConverJson.put("userFirstName", editUserFirstNameEditText.getText());
                            userConverJson.put("userLastName",editUserSecondNameEditText.getText());
                            userConverJson.put("status",JSuser.getInt("status"));
                            userConverJson.put("password", editUserPasswordEditText.getText());
                            userConverJson.put("city", editUserCityEditText.getText());
                            userConverJson.put("street",editUserStreetEditText.getText());
                            userConverJson.put("houseNumber",Integer.parseInt(editUserHouseNumberEditText.getText().toString()));
                            userConverJson.put("doorNumber", Integer.parseInt(editUserDoorNumberEditText.getText().toString()));
                            userConverJson.put("departmentId",departmentId);
                            userConverJson.put("telephone", Integer.parseInt(editUserTelephoneEditText.getText().toString()));
                            userConverJson.put("email", editUserEmailEditText.getText());
                            userConverJson.put("userRole", rollId);

                            int radioButtonCheked = editSexRadioGroup.getCheckedRadioButtonId();
                            sexRadioBatton  = findViewById(radioButtonCheked);
                            int newSex = 1;
                            if (sexRadioBatton.getText() == "Male"){
                                newSex = 2;
                            }
                            userConverJson.put("sex",newSex);
                            User user = new User(userConverJson);
                            Admin.replaceUser(user);
                                Intent intent = new Intent(SelecedEditUserActivity.this,EditUsersActivity.class);
                                startActivity(intent);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        return true;
                    });
                            //TODO after get response message need replace the user
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

        }catch (JSONException e){
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
