package com.arkadiy.enter.smp3.dataObjects;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DayWork {
    private int day;
    private String start;
    private long startId;
    private long endId;
    private String end;
    private String sum;
    private String dayWorkInWeek;
    private String []dayName = {"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};


    public DayWork(int day, String start, String end, String sum,long startId,long endId) {
        this.day = day;
        this.start = start;
        this.end = end;
        //this.sum =  msToString(Long.parseLong(sum));
        this.sum =  sum;
        this.dayWorkInWeek = getDayString(this.day);
        this.startId = startId;
        this.endId = endId;
    }


    public static String msToString(long ms) {
        long totalSecs = ms/1000;
        long hours = (totalSecs / 3600);
        long mins = (totalSecs / 60) % 60;
        long secs = totalSecs % 60;
        String minsString = (mins == 0)
                ? "00" : ((mins < 10) ? "0" + mins : "" + mins);
        String secsString = (secs == 0) ? "00" : ((secs < 10) ? "0" + secs : "" + secs);
        if (hours > 0)
            return hours + ":" + minsString + ":" + secsString;
        else if (mins > 0)
            return hours+":"+mins + ":" + secsString;
        else return hours+":"+mins+":" + secsString;
    }

    public String getDayString(int day){
        String name = dayName[day-1];
        return name;
    }

    public String getDayWorkInWeek() {
        return dayWorkInWeek;
    }

    public void setDayWorkInWeek(String dayWorkInWeek) {
        this.dayWorkInWeek = dayWorkInWeek;
    }

    public String getDay() {
        return String.valueOf(day);
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getSum() {
        return sum;
    }

    public void setSum(String sum) {
        this.sum = sum;
    }

    public String calculetCum(){
        return "";
    }

    public long getStartId() {
        return startId;
    }

    public void setStartId(long startId) {
        this.startId = startId;
    }

    public long getEndId() {
        return endId;
    }

    public void setEndId(long endId) {
        this.endId = endId;
    }
}
