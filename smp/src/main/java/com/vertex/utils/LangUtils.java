package com.vertex.utils;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LangUtils {
    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    static SecureRandom rnd = new SecureRandom();

    private static final int SECOND = 1000;

    private static final int MINUTE = 60000;

    private static final int HOUR = 3600000;

    private static final int DAY = 86400000;

    public static String generatePrivateKey() {
        StringBuilder sb = new StringBuilder(40);
        for (int i = 0; i < 40; i++)
            sb.append("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".charAt(rnd.nextInt("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".length())));
        return sb.toString();
    }

    public static String convertToMysqlDateTime(String dateTime) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy HH:mm");
            SimpleDateFormat formatMysql = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return formatMysql.format(format.parse(dateTime));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getCurrentMysqlTime() {
        try {
            Date utilDate = new Date();
            Timestamp sqlTimeStamp = convert(utilDate);
            return sqlTimeStamp.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date getDateFromMysqlDate(String sql) throws ParseException {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Date date = inputFormat.parse(sql);
        return date;
    }

    public static String getDateString(String sql) throws ParseException {
        Date date = getDateFromMysqlDate(sql);
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = inputFormat.format(date);
        return str;
    }

    public static String convertTime(long ms) {
        StringBuffer text = new StringBuffer("");
        text.append(ms / 3600000L).append(".");
        ms %= 3600000L;
        int num = calculatePercentage((ms / 60000L), 60.0D);
        text.append(num);
        return String.valueOf(text);
    }

    public static int calculatePercentage(double obtained, double total) {
        total = obtained * 100.0D / total;
        total = Math.floor(total);
        int x = (int)total;
        return x;
    }

    public static Timestamp convert(Date date) {
        return new Timestamp(date.getTime());
    }
}
