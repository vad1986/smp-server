package com.vertex.utils;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class HtmlGenerator {
    public static String userClockReportTable(JsonArray users) {
        String html = "";
        for (Object user : users) {
            JsonObject jsn = (JsonObject)user;
            html = html + makeTableForUserClocks(jsn);
        }
        return html;
    }

    public static String userTaskReportTable(JsonArray users) {
        String html = "";
        for (Object user : users) {
            JsonObject jsn = (JsonObject)user;
            html = html + makeTableForUserTasks(jsn);
        }
        return html;
    }

    private static String makeTableForUserTasks(JsonObject jsonObject) {
        String html = "";
        JsonArray complete = jsonObject.getJsonArray("complete");
        JsonArray notComplete = jsonObject.getJsonArray("not_complete");
        String firstName = jsonObject.getString("first_name");
        String secondName = jsonObject.getString("second_name");
        html = html + makeCompletedTasksTable(complete, firstName, secondName);
        html = html + makeUnCompletedTasksTable(notComplete);
        html = html + "<b>==================================================================</b>";
        return html;
    }

    private static String makeTableForUserClocks(JsonObject jsonObject) {
        String html = "";
        String name = jsonObject.getString("first_name");
        String secondName = jsonObject.getString("second_name");
        String total = jsonObject.getString("sum");
        html = html + makeUserClockTable(jsonObject.getJsonArray("data"), name, secondName, total);
        return html;
    }

    private static String makeCompletedTasksTable(JsonArray tasks, String firstName, String secondName) {
        String text = "<h2>" + firstName + " " + secondName + "</h2><h3>Tasks Completed</h3><table width='100%' border='1' align='center'><tr align='center'><td><b>Task Name </b></td><td><b>Description</b></td><td><b>Manager Id</b></td><td><b>Date Start</b></td><td><b>Date Finish</b></td></tr>";
        for (Object task : tasks) {
            JsonObject jsn = (JsonObject)task;
            String str = "<tr align='center'><td><b>" + jsn.getString("name") + "</b></td><td><b>" + jsn.getString("description") + "</b></td><td><b>" + jsn.getInteger("manager_id") + "</b></td><td><b>" + jsn.getString("time_date_start") + "</b></td><td><b>" + jsn.getString("time_date_end") + "</b></td></tr>";
            text = text + str;
        }
        text = text + "</table>";
        return text;
    }

    private static String makeUserClockTable(JsonArray clocks, String name, String secondName, String total) {
        String totalTable = "";
        String text = "<h2>" + name + " " + secondName + "</h2><h3>Punch Clock Data</h3><table width='100%' border='1' align='center'><tr align='center'><td><b>Start</b></td><td><b>Finish</b></td><td><b>Hours</b></td></tr>";
        for (Object clock : clocks) {
            JsonObject jsn = (JsonObject)clock;
            String start = jsn.containsKey("start") ? jsn.getString("start") : "0";
            String finish = jsn.containsKey("finish") ? jsn.getString("finish") : "0";
            String hours = jsn.containsKey("hours") ? jsn.getString("hours") : "0";
            String str = "<tr align='center'><td><b>" + start + "</b></td><td><b>" + finish + "</b></td><td><b>" + hours + "</b></td></tr>";
            text = text + str;
        }
        text = text + "</table>";
        totalTable = "<table width='100%' border='1' align='center'><tr align='center'><td><b>Total Hours</b></td><td><b>" + total + "</b></td></tr></table>";
        text = text + totalTable;
        return text;
    }

    private static String makeUnCompletedTasksTable(JsonArray tasks) {
        String text = "";
        if (tasks.size() == 0) {
            text = "<h2>This user does'nt have Uncompleted Tasks</h2>";
        } else {
            text = "<h3>Tasks Not Completed</h3><table width='100%' border='1' align='center'><tr align='center'><td><b>Task Name </b></td><td><b>Description</b></td><td><b>Manager Id</b></td><td><b>Date Start</b></td></tr>";
            for (Object task : tasks) {
                JsonObject jsn = (JsonObject)task;
                String str = "<tr align='center'><td><b>" + jsn.getString("name") + "</b></td><td><b>" + jsn.getString("description") + "</b></td><td><b>" + jsn.getInteger("manager_id") + "</b></td><td><b>" + jsn.getString("time_date_start") + "</b></td></tr>";
                text = text + str;
            }
            text = text + "</table>";
        }
        return text;
    }
}
