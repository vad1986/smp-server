package com.arkadiy.enter.smp3.dataObjects;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Build;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.annotation.RequiresApi;

import com.arkadiy.enter.smp3.activities.AddNewTaskActivity;
import com.arkadiy.enter.smp3.activities.App;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class GetDate {
    private static Calendar mCurrentDate;
    private static Calendar mCurrentTime;
    private static ArrayList<DayWork> listWork;
    public static void getDate(final EditText editText){

        mCurrentDate = Calendar.getInstance();
        mCurrentTime = Calendar.getInstance();
        int year = mCurrentDate.get(Calendar.YEAR);
        int month = mCurrentDate.get(Calendar.MONTH)+1;
        int day = mCurrentDate.get(Calendar.DAY_OF_MONTH);
        final int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
        final int minute = mCurrentTime.get(Calendar.MINUTE);


        DatePickerDialog datePickerDialog = new DatePickerDialog(App.getContext(), new DatePickerDialog.OnDateSetListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDateSet(DatePicker datePicker, final int selectYear, final int selectMonth, final int selectDay) {

                //editText.setText(selectDay+"-"+selectMonth+"-"+selectYear);
                mCurrentDate.set(selectYear,selectMonth,selectDay);

                TimePickerDialog timePickerDialog = new TimePickerDialog(App.getContext(), new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectHour, int selectMinute) {

                        editText.setText(selectYear+"-"+selectMonth+"-"+selectDay +" "+selectHour+":"+selectMinute);
                        mCurrentTime = Calendar.getInstance();
                        mCurrentDate.set(Calendar.HOUR_OF_DAY, selectHour);
                        mCurrentDate.set(Calendar.MINUTE, selectMinute);
                    }
                },hour,minute,true);
                timePickerDialog.show();
            }
        }, year , month,day );
        datePickerDialog.show();

    }

    public static int getDay(Date day){
        Calendar c = Calendar.getInstance();
        c.setTime(day);
        //c.setTime(Calendar.getInstance().getTime());
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek;
    }

    public static ArrayList<DayWork> setDayWork(JSONObject jsonResponse) {

        DayWork dayWork;
        java.text.DateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.text.DateFormat dfForDay = new java.text.SimpleDateFormat("yyyy-MM-dd");
        java.util.Date dateDay = null;
        String date1 = null;
        String date2 = null;
        long startId = -100;// if null = -100
        long endId = -100;// if null = -100
        String sumTime;
        boolean dateNull =false;
        try {
            listWork = new ArrayList<>();
            for (int i = 0;i < jsonResponse.getJSONArray("data").length();i++){

                if (!jsonResponse.getJSONArray("data").getJSONObject(i).has("start")){
                    date1 = "--";
                    dateDay = null;
                }else {

                    date1 =  jsonResponse.getJSONArray("data").getJSONObject(i).getString("start");
                    startId = jsonResponse.getJSONArray("data").getJSONObject(i).getLong("start_id");
                    dateDay = dfForDay.parse(date1);

                }
                if (!jsonResponse.getJSONArray("data").getJSONObject(i).has("finish")){
                    date2 = "--";

                }else {
                    date2 = jsonResponse.getJSONArray("data").getJSONObject(i).getString("finish");
                    endId = jsonResponse.getJSONArray("data").getJSONObject(i).getLong("finish_id");
                }

//            date1 = df.parse(enterTime);
//            date2 = df.parse(endTime);
                if ( jsonResponse.getJSONArray("data").getJSONObject(i).has("hours")){

                    //long diff = date2.getTime() - date1.getTime();
                    sumTime =  jsonResponse.getJSONArray("data").getJSONObject(i).getString("hours");

                }
                else {
                    sumTime ="--";
                    if (dateDay == null){
                        dateDay = dfForDay.parse(jsonResponse.getJSONArray("data").getJSONObject(i).getString("finish"));
                    }
                }



                // sumTime =  jsonResponse.getJSONArray("data").getJSONObject(i).getString("hours");
//        sumTime = String.valueOf(diff);
                dayWork = new DayWork(getDay(dateDay),date1,date2,sumTime,startId,endId);
                listWork.add(dayWork);
//              customAdapterHours.notifyDataSetChanged();
                dateDay =  null;

            }
            return listWork;
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String getToDate() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String StringDate = df.format(Calendar.getInstance().getTime());
        return StringDate;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String plusHours(String date){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance(); // creates calendar
        try {
            cal.setTime(df.parse(date)); // sets calendar time/date

        cal.add(Calendar.HOUR_OF_DAY, 8); // adds four hour
        String date1 = df.format(cal.getTime()); // returns new date object
            return date1;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

}
