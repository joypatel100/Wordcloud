package com.project.android.wordcloud;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Joy on 8/13/15.
 */
public class Utility {

    // Get changes in Preferences for Search Query and Text Color

    public static String getPreferredSearch(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("search_query", "");
    }

    public static String getWCTextColor(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("color_text","#990000");
    }



}
