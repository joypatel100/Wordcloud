package com.project.android.wordcloud;

import android.annotation.TargetApi;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;


public class MainActivity extends AppCompatActivity {

    public ArticleFragment myAF;
    public static HashSet<String> badWords;
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String ARTICLE_FRAGMENT = "ArticleFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //myAF = (ArticleFragment) fm.findFragmentByTag(ARTICLE_FRAGMENT);
        if(badWords==null) {
            badWords = new HashSet<>();
            try {
                InputStream is = getResources().openRawResource(R.raw.badwords);
                InputStreamReader inputStreamReader = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(inputStreamReader);
                String line;

                while ((line = br.readLine()) != null) {
                    badWords.add(line.toLowerCase());
                }
                br.close();
                is.close();
                inputStreamReader.close();
            } catch (IOException e) {
                //You'll need to add proper error handling here
                Log.v(LOG_TAG, "didn't load bad words");
            }
        }
        myAF = new ArticleFragment();
        myAF.setRetainInstance(true);
        if(savedInstanceState == null){
            Log.v(LOG_TAG,"saved instance main null");
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            prefs.edit().putString("search_query","").apply();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container,myAF)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        if (id == R.id.search_bar)
        {
            showDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    void showDialog() {
        DialogFragment newFragment = SearchDialogFragment.newInstance(
                R.string.alert_dialog_title);
        newFragment.show(getFragmentManager(), "dialog");
    }

    public void doPositiveClick(String input) {
        // Do stuff here.
        myAF.updateArticle(input, myAF.lastLanguage);
    }

    public void doNegativeClick() {
        // Do stuff here.
        Log.i("FragmentAlertDialog", "Negative click!");
    }


    @Override
    public void onResume(){
        super.onResume();
        /*
        String language = Utility.getPreferredLanguage(this);
        Log.v(LOG_TAG, language);
        if(!language.equals(myAF.lastLanguage)) {
            myAF.updateArticle(myAF.lastSearch, language);
        }*/

    }

}