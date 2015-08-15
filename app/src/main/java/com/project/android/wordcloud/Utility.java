package com.project.android.wordcloud;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Joy on 8/13/15.
 */
public class Utility {
    public static String getPreferredSearch(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("search_query", "");
    }

    public static String getPreferredLanguage(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("language_list", "en");
    }

    public static String getPreferredNumberOfArticles(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("article_num","10");
    }

}
