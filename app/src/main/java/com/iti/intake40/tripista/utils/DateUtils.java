package com.iti.intake40.tripista.utils;

public class DateUtils {
    public static String[] getDateArr(String date) {
        return date.split("-");
    }

    public static String[] getTimeArr(String time) {
        return time.split(":");
    }
}
