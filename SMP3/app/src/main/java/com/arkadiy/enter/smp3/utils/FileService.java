package com.arkadiy.enter.smp3.utils;

import android.content.Context;
import android.util.Log;

import com.arkadiy.enter.smp3.activities.App;
import com.arkadiy.enter.smp3.activities.LogInActivity;
import com.arkadiy.enter.smp3.dataObjects.User;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.arkadiy.enter.smp3.config.AppConfig.DATA_FILE_NAME;

/**
 * Created by vadnu on 11/30/2019.
 */

public class FileService {



    public static void save(String privateKey){

        try {
            FileOutputStream fos=null;
            fos= App.getContext().openFileOutput(DATA_FILE_NAME, Context.MODE_PRIVATE);
            fos.write(privateKey.getBytes());
            Log.i("SAVE IN FILE SUCCESS","Saved parivate key in file "+DATA_FILE_NAME);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static User getPrivateKey(){
        try {
            User user= null;
            user = load(DATA_FILE_NAME);

         return user;
        } catch (Exception e) {
         return null;

        }

    }

    public static void removePrivateKey(){
        try {
            FileOutputStream fos=null;
            fos= App.getContext().openFileOutput(DATA_FILE_NAME, Context.MODE_PRIVATE);
            fos.write("x".getBytes());
            Log.i("SAVE IN FILE SUCCESS","Saved parivate key in file "+DATA_FILE_NAME);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static User load(String fileName)throws Exception{
        FileInputStream fis=null;
        User user=new User();

        String text;
            fis=App.getContext().openFileInput(fileName);
            InputStreamReader isr=new InputStreamReader(fis);
            BufferedReader br=new BufferedReader(isr);

            if(br.ready()){
                user.setPrivateKey(br.readLine());
                user.setUserId(Integer.parseInt(br.readLine()));
                return user;

            }else{
             throw new Exception("no data in the file");
            }



    }
}
