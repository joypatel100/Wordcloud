package com.project.android.wordcloud;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by Joy on 8/13/15.
 */
public class Utility {


    private final String LOG_TAG = Utility.class.getSimpleName();

    // Get changes in Preferences for Search Query and Text Color

    public static String getPreferredSearch(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("search_query", "");
    }

    public static String getWCTextColor(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("color_text","#990000");
    }

    public static int getNumWordsCloud(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Log.v("Utility", "got prefs");
        int numWords = 10;
        String numStr = prefs.getString("num_words", "10");
        if(numStr.length() > 3){
            numWords = 1000;
        }
        else{
            numWords = Integer.parseInt(numStr);
        }
        Log.v("Utility","got num words");
        if(numWords < 10){
            numWords = 10;
        }
        if(numWords > 1000){
            numWords = 1000;
        }
        return numWords;
    }



}
