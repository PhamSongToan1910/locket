package com.example.locket_clone.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;

public class DateTimeConvertUtils {

    private static final HashMap<Integer, String> mapMonth = new HashMap<>();

    static {
        mapMonth.put(1, "Jan ");
        mapMonth.put(2, "Feb ");
        mapMonth.put(3, "Mar ");
        mapMonth.put(4, "Apr ");
        mapMonth.put(5, "May ");
        mapMonth.put(6, "Jun ");
        mapMonth.put(7, "Jul ");
        mapMonth.put(8, "Aug ");
        mapMonth.put(9, "Sep ");
        mapMonth.put(10, "Oct ");
        mapMonth.put(11, "Nov ");
        mapMonth.put(12, "Dec ");
    }

    public static String convertDateToString(Date date) {
        StringBuilder sb = new StringBuilder();
        sb.append(mapMonth.get(date.getMonth() + 1));
        sb.append(date.getDate());
        sb.append(", ").append(date.toString().substring((date.toString().length() - 4)));
        return sb.toString();
    }

    public static String convertCreateAtInstantToString(Instant instant) {
        String timeString = instant.toString();
        return timeString.split("T")[0] +
                " " +
                timeString.split("T")[1].substring(0,8);
    }

    public static Instant convertStringToInstant(String timeString) {
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            Date date = sdf.parse(timeString);
            return Instant.parse(date.toString());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

}
