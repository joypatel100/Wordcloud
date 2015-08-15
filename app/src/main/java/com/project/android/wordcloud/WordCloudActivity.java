package com.project.android.wordcloud;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by Joy on 8/11/15.
 */
public class WordCloudActivity extends AppCompatActivity {

    // Activity for the word cloud generation

    private final String LOG_TAG = WordCloudActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wordcloud);
        getSupportActionBar().setLogo(R.mipmap.cloud);
        if (savedInstanceState == null) {
            Log.v(LOG_TAG,"on create Word Cloud Activity");
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.wordcloud_container, new WordCloudFragment())
                    .commit();
        }
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.wordcloud, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.action_settings:
                return  true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
