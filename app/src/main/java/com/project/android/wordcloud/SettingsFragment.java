package com.project.android.wordcloud;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by Joy on 8/13/15.
 */

@TargetApi(11)
public class SettingsFragment extends PreferenceFragment{

    // Create Settings Fragment

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);
        //EditTextPreference edp = (EditTextPreference) findPreference("search_query");
        //PreferenceScreen screen = getPreferenceScreen();
        //screen.removePreference(edp);
    }
}
