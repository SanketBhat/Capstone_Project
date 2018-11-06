package com.udacity.sanketbhat.news4you.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class DateAndTimeUtils {

    public static String getDateDisplayString(String dateString) {
        return getLocalDateAndTime(dateString);
    }

    private static String getLocalDateAndTime(String dateString) {
        Calendar calendar = Calendar.getInstance();
        TimeZone timeZone = calendar.getTimeZone();
        SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        SimpleDateFormat destFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm aaa", Locale.getDefault());
        sourceFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        destFormat.setTimeZone(timeZone);
        Date convertedDate;
        try {
            convertedDate = sourceFormat.parse(dateString);
            return destFormat.format(convertedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateString;
    }

    public static String getRelativeDisplayString(String dateString) {
        String localDateAndTime = getLocalDateAndTime(dateString);
        Date dateFrom;
        try {
            dateFrom = new SimpleDateFormat("dd-MM-yyyy hh:mm aaa", Locale.getDefault()).parse(localDateAndTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return "Unknown time";
        }
        Calendar calendar = Calendar.getInstance();
        Date dateTo = calendar.getTime();
        long diff = dateTo.getTime() - dateFrom.getTime();
        long diffMinutes = diff / (60 * 1000);
        long diffHours = diff / (60 * 60 * 1000);
        long diffDays = diff / (24 * 60 * 60 * 1000);
        diffHours = diffHours - (diffDays * 24);
        diffMinutes = diffMinutes - (diffHours * 60);

        if (diffDays > 0) {
            if (diffDays == 1) return "Yesterday";
            else return diffDays + " Days ago";
        }
        if (diffHours > 0) {
            if (diffHours == 1) return "An hour ago";
            else return diffHours + " hours ago";
        }
        if (diffMinutes > 0) {
            if (diffMinutes == 1) return "A minute ago";
            else return diffMinutes + " minutes ago";
        }
        return "Just now";
    }
}
