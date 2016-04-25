package ru.ifmo.md.weather;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;



public class SettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        addPreferencesFromResource(R.xml.preferences);
    }
}
