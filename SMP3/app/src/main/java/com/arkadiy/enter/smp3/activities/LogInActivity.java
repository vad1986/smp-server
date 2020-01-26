package com.arkadiy.enter.smp3.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.provider.CalendarContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.arkadiy.enter.smp3.DataBaseHelperGlobalRoles;
import com.arkadiy.enter.smp3.DataBaseHelperUserConnected;
import com.arkadiy.enter.smp3.config.ResponseCode;
import com.arkadiy.enter.smp3.dialogs.DialogforgotPassword;
import com.arkadiy.enter.smp3.R;
import com.arkadiy.enter.smp3.config.AppConfig;
import com.arkadiy.enter.smp3.dataObjects.User;
import com.arkadiy.enter.smp3.services.DataServices;
import com.arkadiy.enter.smp3.utils.Constants;
import com.arkadiy.enter.smp3.utils.ConstantsJson;
import com.arkadiy.enter.smp3.utils.FileService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import javax.security.auth.login.LoginException;

import static com.arkadiy.enter.smp3.dataObjects.User.successfulLoginAndFillData;

public class LogInActivity extends AppCompatActivity implements DialogforgotPassword.DialogforgotPasswordListener {
    private Button logInButton;
    private static EditText password;
    private static EditText userName;
    private RequestQueue requestQueue;
    public  static HashMap<Integer,String> DEPARTMANTS;
    public  static HashMap<String,Integer>MYDEPARTMENTS;
    public static DataBaseHelperUserConnected dataBaseHelper;
    public static DataBaseHelperGlobalRoles dataBaseHelperGlobalRoles;
    private TextView forgotPasswordTextVView;
    private TextView errorResponseView;
    RelativeLayout rellay1,loginActivity;
    RelativeLayout rellay2;

    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            rellay1.setVisibility(View.INVISIBLE);

            afterTransition();

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        Handler handler=new Handler();

        rellay1=(RelativeLayout)findViewById(R.id.rellay1);
        rellay2=(RelativeLayout) findViewById(R.id.rellay2);
        loginActivity=(RelativeLayout) findViewById(R.id.login);

        handler.postDelayed(runnable,3000);

    }

    private void afterTransition() {
        dataBaseHelper = new DataBaseHelperUserConnected(LogInActivity.this);
        dataBaseHelperGlobalRoles = new DataBaseHelperGlobalRoles(LogInActivity.this);



        DEPARTMANTS = new HashMap<>();
        App.setContext(this);
        MYDEPARTMENTS=new HashMap<>();
        userName = (EditText)findViewById(R.id.userName_editText);
        password = (EditText)findViewById(R.id.password_editText);
        logInButton = (Button)findViewById(R.id.logIn_button);
        forgotPasswordTextVView = (TextView)findViewById(R.id.forgotPasswordTextView);
        errorResponseView = (TextView)findViewById(R.id.errorResponseView);
        requestQueue = Volley.newRequestQueue(this);
        Context context = LogInActivity.this;

        User keyAndUserId=FileService.getPrivateKey();

        if(keyAndUserId!=null) {

            DataServices.sendData(AppConfig.CHECK_PRIVATE_KEY + keyAndUserId.getPrivateKey() + "/"
                            + keyAndUserId.getUserId(),
                    null, requestQueue, LogInActivity.this,
                    Constants.METHOD_GET, handler -> {
                        try {
                            System.out.println("++++ CHECKED PRIVATE KEY  ++++++");

                            Bundle bundle = handler.getData();
                            JSONObject jsonRespons;
                            if (bundle.getString("json") != null) {

                                jsonRespons = new JSONObject(bundle.getString("json"));
                                if (jsonRespons.getInt(ConstantsJson.RESPONSE_CODE) < ResponseCode.ERROR) {
                                    successfulLoginAndFillData(jsonRespons);
                                }else{
                                    rellay2.setVisibility(View.VISIBLE);


                                }


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            rellay2.setVisibility(View.VISIBLE);

                        }
                        return true;
                    });

        }else{
            rellay2.setVisibility(View.VISIBLE);

        }

            logInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    User.logIn(jsonParse(),errorResponseView);
//                        DataServices.sendData(AppConfig.LOGIN,jsonParse(),requestQueue,
//                        LogInActivity.this,1, null,user);

                }
            });

            forgotPasswordTextVView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogforgotPassword dialogforgotPassword = new DialogforgotPassword();
                    dialogforgotPassword.show(getSupportFragmentManager(),userName.getText().toString());
//                Intent intent = new Intent(LogInActivity.this,ForgotPassword.class);
//                startActivity(intent);
                }
            });

//        if (privateKey!=null)
//            DataServices.getUserIntoDataBase(user);
//            DataServices.sendData(AppConfig.CHECK_PRIVATE_KEY+ privateKey,
//                    null,requestQueue,LogInActivity.this,
//                    Constants.METHOD_GET,null,user);
//
//            Intent intent = new Intent( LogInActivity.this,MainActivity.class);
//            startActivity(intent);
//        }

    }

    private JSONObject jsonParse(){

        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put(ConstantsJson.USERNAME,userName.getText());
            jsonObject.put(ConstantsJson.PASSWORD,password.getText());
            return  jsonObject;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  null;
    }

    public static void resetValueUserInput (){
        userName.setText("");
        password.setText("");
    }


    @Override
    public void applyText(String userName,String email) {

        JSONObject emailJson = new JSONObject();
        String url = AppConfig.FORGOT_PASSWORD;
        try {
            emailJson.put("name",userName);
            emailJson.put("mail",email);

            DataServices.sendData(url,emailJson,requestQueue,LogInActivity.this, Constants.METHOD_POST,forgotPassHandler->{

                Bundle bundle = forgotPassHandler.getData();
                if (bundle.getInt("response_code") < ResponseCode.ERROR ){
                    Toast.makeText(App.getContext(),"Password sent to email",Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(App.getContext(),"One of the parameters wasn't correct. Please try again!",Toast.LENGTH_LONG).show();
                }

                return true;
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {


    }
}
