package com.udacity.sanketbhat.news4you.ui;


import android.os.Bundle;
import android.support.v14.preference.PreferenceFragment;

import com.udacity.sanketbhat.news4you.R;

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preferences);
    }
}
