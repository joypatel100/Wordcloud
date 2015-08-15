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


public class MainActivity extends AppCompatActivity implements ArticleFragment.Callback{

    public static HashSet<String> badWords;
    public static String wordCloudColor = "#990000";
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String WCFRAGMENT_TAG = "WCTAG";
    public static boolean mTwoPane;
    private String selectedURL = "";
    private String selectedWords = "";
    public static boolean queryChanged = true;
    public static String lastQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Words to be ignored in making word cloud
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

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                prefs.edit().putString("search_query", "").apply();

            } catch (IOException e) {
                //You'll need to add proper error handling here
                Log.v(LOG_TAG, "didn't load bad words");
            }
        }
        // Container for word cloud
        if(findViewById(R.id.wordcloud_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                Log.v(LOG_TAG, "saved instance main null");
                //getSupportFragmentManager().beginTransaction().add(R.id.article_fragment, myAF).commit();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.wordcloud_container, new WordCloudFragment(),WCFRAGMENT_TAG)
                        .commit();
            }
        }
        else{
            mTwoPane = false;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("NewsCloud");
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

    public void doPositiveClick() {
        ArticleFragment a = (ArticleFragment) getSupportFragmentManager().findFragmentById(R.id.article_fragment);
        a.updateArticle();
    }

    public void doNegativeClick() {
        // Do stuff here.
        Log.i("FragmentAlertDialog", "Negative click!");
    }


    @Override
    public void onResume(){
        super.onResume();
        // Checks if preferences have changed
        String newColor = Utility.getWCTextColor(this);
        if(!newColor.equals(wordCloudColor)){
            wordCloudColor = newColor;
        }
        if(!selectedURL.isEmpty()){
            onItemSelected(selectedURL,selectedWords);
        }

    }

    @Override
    public void onItemSelected(String url, String words) {
        selectedURL = url;
        selectedWords = words;
        if(mTwoPane){
            Bundle b = new Bundle();
            b.putString("url",selectedURL);
            b.putString("words", selectedWords);
            WordCloudFragment wcf = new WordCloudFragment();
            wcf.setArguments(b);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.wordcloud_container, wcf, WCFRAGMENT_TAG)
                    .commit();
        }
        else{
            Intent intent = new Intent(this, WordCloudActivity.class).putExtra("words",words).putExtra("url", url);
            startActivity(intent);
        }
    }
}