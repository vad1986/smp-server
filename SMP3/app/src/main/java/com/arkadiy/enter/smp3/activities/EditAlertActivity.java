package com.arkadiy.enter.smp3.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.arkadiy.enter.smp3.R;
import com.arkadiy.enter.smp3.config.ResponseCode;
import com.arkadiy.enter.smp3.dataObjects.Admin;
import com.arkadiy.enter.smp3.dataObjects.Alert;
import com.arkadiy.enter.smp3.dataObjects.CustomAdapterAlert;
import com.arkadiy.enter.smp3.dataObjects.Store;
import com.arkadiy.enter.smp3.services.GlobalServices;
import com.arkadiy.enter.smp3.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class EditAlertActivity extends AppCompatActivity {

    private EditText editNameAlertEditText;
    private EditText editDescriptionAlertEditText;
    private ListView editAlertListView;
    private Switch deleteAlertSwitch;
    private Button sendEditAlertButton;
    private static ArrayList<Alert> editAlerts;
    private static CustomAdapterAlert customEditAdapterAlert;
    private RequestQueue requestQueue;
    private int idAlert;
    private int ifRemove=0; //for delete->remove=1 create/update-> remove=0

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.setContext(EditAlertActivity.this);
        requestQueue = Volley.newRequestQueue(this);

        Admin.getAlerts(this,null,requestQueue,handler->{
            try {

                JSONObject json = new JSONObject(handler.getData().getString("json"));
                Store.fillAlertList(json.getJSONArray("sentences"));
                startThisActivity();

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        });




    }


    private void startThisActivity(){
        setContentView(R.layout.activity_edit_alert);
        editNameAlertEditText = (EditText)findViewById(R.id.editNameAlert_EditText);
        editDescriptionAlertEditText = (EditText)findViewById(R.id.editDescriptionAlert_EditText);
        editAlertListView = (ListView)findViewById(R.id.editAlert_ListView);
        deleteAlertSwitch = (Switch)findViewById(R.id.deleteAlert_Switch);
        sendEditAlertButton = (Button)findViewById(R.id.sendEditAlert_Button);

        editAlerts = (ArrayList<Alert>) Store.getAlerts();
        customEditAdapterAlert = new CustomAdapterAlert(this,editAlerts);
        editAlertListView.setAdapter(customEditAdapterAlert);
        //{"name":"hohoho 2", "description":"hey dude hey", "id":14 }
        deleteAlertSwitch.setChecked(true);
        editAlertListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                editNameAlertEditText.setText(editAlerts.get(position).getName());
                editDescriptionAlertEditText.setText(editAlerts.get(position).getDescription());
                idAlert = editAlerts.get(position).getId();

            }
        });


        sendEditAlertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEditAlertToServer(editNameAlertEditText.getText().toString(),editDescriptionAlertEditText.getText().toString(),idAlert);
            }
        });

        deleteAlertSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    ifRemove = 0;
                }
                else {
                    ifRemove = 1;
                }
            }
        });

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbarMine);
        setSupportActionBar(toolbar);
        GlobalServices.addListener(toolbar,this);
    }

    private void sendEditAlertToServer(String editNameAlert, String editDescriptionAlert, int idAlert) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name",editNameAlert);
            jsonObject.put("description",editDescriptionAlert);
            jsonObject.put("id",idAlert);
            jsonObject.put("remove",ifRemove);
            Admin.createOrEditAlert(App.getContext(),jsonObject,requestQueue,editAlertHandler->{
                //TODO  need cheng Store.getAlerts() after editAlert

                JSONObject json = new JSONObject();
                Bundle  bundle = editAlertHandler.getData();
                try {
                    json = new JSONObject(bundle.getString("json"));

                    Toast.makeText(App.getContext(),json.getString("message"),Toast.LENGTH_LONG).show();
                    if (editAlertHandler.getData().getInt(Constants.RESPONSE_CODE) < ResponseCode.ERROR){

                        Intent intent = new Intent(EditAlertActivity.this,SavedActivity.class);
                        intent.putExtra("activity",EditAlertActivity.class.getName());
                        startActivity(intent);


                        //Toast.makeText(App.getContext(),i_handler.getData().getString("message"),Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            });

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }
}
