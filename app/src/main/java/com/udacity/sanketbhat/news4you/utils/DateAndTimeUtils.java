package com.udacity.sanketbhat.news4you.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class DateAndTimeUtils {
    private static final String TAG = "DateAndTimeUtils";

    /**
     * Default normal string to display date and time
     *
     * @param dateString It should be in the format yyyy-MM-ddThh:mm:ssZ
     * @return Local date string without zone
     */
    public static String getDateDisplayString(String dateString) {
        Calendar calendar = Calendar.getInstance();
        TimeZone timeZone = calendar.getTimeZone();
        SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        SimpleDateFormat destFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm aaa", Locale.getDefault());
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
}
