package com.udacity.sanketbhat.news4you.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import com.udacity.sanketbhat.news4you.R;

public class PreferenceUtils {
    public static boolean getPrefRelatedTime(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(context.getString(R.string.pref_key_relative_time),
                context.getResources().getBoolean(R.bool.pref_default_relative_time));
    }

    public static int getPrefUpdateFrequency(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String value = preferences.getString(context.getString(R.string.pref_key_update_frequency),
                context.getString(R.string.pref_default_update_frequency));
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static boolean getPrefEnableNotification(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(context.getString(R.string.pref_key_notifications),
                context.getResources().getBoolean(R.bool.pref_default_notifications));
    }
}
