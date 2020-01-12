package com.arkadiy.enter.smp3.dataObjects;

import android.content.Context;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class ReadWriteFile {

     public static void writeInToFile (String nameFile ,String txt,Context context){
        try{
            FileOutputStream fos = context.openFileOutput(nameFile,Context.MODE_PRIVATE);
            fos.write(txt.getBytes());
            fos.close();
            Toast.makeText(context,"Saved!",Toast.LENGTH_LONG).show();
        }catch (Exception e){
            Toast.makeText(context,"Error saving file!",Toast.LENGTH_LONG).show();
        }
    }

     public static String readFromFile(String nameFile,Context context){
        String txtTmp = null;
        try{
            FileInputStream fis = context.openFileInput(nameFile);
            int size = fis.available();
            byte [] buffer = new byte[size];
            fis.read(buffer);
            fis.close();
            txtTmp = new String(buffer);
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(context,"Error reading file!",Toast.LENGTH_LONG).show();
        }
        return txtTmp;
    }

    public static void saveFile(String file,String text,Context context){

        try{
            FileOutputStream fos = context.openFileOutput(file,Context.MODE_PRIVATE);
            fos.write(text.getBytes());
            fos.close();
            Toast.makeText(context,"Saved!",Toast.LENGTH_LONG).show();
        }catch (Exception e)
        {
            Toast.makeText(context,"Error saving file!",Toast.LENGTH_LONG).show();
        }
    }
    public static boolean isEmpty(File file, Context context){

         if (file.exists()) return true;
         if (!readFromFile(file.getName(),context).isEmpty())return true;

         return false;
    }

}
