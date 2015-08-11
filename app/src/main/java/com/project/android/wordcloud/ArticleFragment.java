package com.project.android.wordcloud;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * A placeholder fragment containing a simple view.
 */
public class ArticleFragment extends Fragment {
    ArrayAdapter<String> mArticleAdapter;
    HashMap<String,String> mArticleURL;
    private final String LOG_TAG = ArticleFragment.class.getSimpleName();

    public ArticleFragment() {
        mArticleURL = new HashMap<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.articlefragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateArticle();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        // Now that we have some dummy forecast data, create an ArrayAdapter.
        // The ArrayAdapter will take data from a source (like our dummy forecast) and
        // use it to populate the ListView it's attached to.
        mArticleAdapter = new ArrayAdapter<String>(
                getActivity(), // The current context (this activity)
                R.layout.list_item_article, // The name of the layout ID.
                R.id.list_item_article_textview, // The ID of the textview to populate.
                new ArrayList<String>());

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_article);
        listView.setAdapter(mArticleAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String article = mArticleAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), WordCloudActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, mArticleURL.get(article));
                Log.v(LOG_TAG,mArticleURL.get(article));
                startActivity(intent);
            }
        });
        return rootView;
    }

    public void updateArticle(){
        FetchArticleTask articleTask = new FetchArticleTask();
        articleTask.execute();

    }

    @Override
    public void onStart(){
        super.onStart();
        updateArticle();
    }

    public class FetchArticleTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchArticleTask.class.getSimpleName();

        private String[] getInformationFromJson(String articleJsonStr, int numArticles)
                throws JSONException{

            final String RESULTS = "results";
            final String TITLE = "title";
            final String ARTICLE_URL = "url";
            JSONObject articleJson = new JSONObject(articleJsonStr);
            JSONArray articleArray = articleJson.getJSONArray(RESULTS);

            String[] resultStrs = new String[numArticles];
            int ind_res = 0;
            int ind_json = 0;
            while(numArticles != ind_res){
                JSONObject article = articleArray.getJSONObject(ind_json);
                if(article.has(TITLE)){
                    String title = article.getString(TITLE);
                    String url = article.getString(ARTICLE_URL);
                    resultStrs[ind_res] = title;
                    mArticleURL.put(title, url);


                    ind_res++;
                }

                ind_json++;
            }

            return resultStrs;
        }

        @Override
        protected String[] doInBackground(String... params) {

            /*if(params.length == 0){
                return null;
            }*/

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String articleJsonStr = null;

            String key = "8np7pfcewGev6KGJuUcef-1CAC4_";
            String search = "";
            String source = "news";
            String format = "json";

            int numArticles = 10;

            try{
                final String ARTICLE_BASE_URL = "http://www.faroo.com/api?start=1&length=10&l=en";
                final String SEARCH_PARAM = "q";
                final String SOURCE_PARAM = "src";
                final String FORMAT_PARAM = "f";
                final String KEY_PARAM = "key";


                //URL url = new URL("http://www.faroo.com/api?q=&start=1&length=10&l=en&src=news&f=json&key=8np7pfcewGev6KGJuUcef-1CAC4_");
                Uri builtUri = Uri.parse(ARTICLE_BASE_URL).buildUpon()
                        .appendQueryParameter(SEARCH_PARAM, search)
                        .appendQueryParameter(SOURCE_PARAM, source)
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .appendQueryParameter(KEY_PARAM, key)
                        .build();
                URL url = new URL(builtUri.toString());

                Log.v ("USER_AGENT", System.getProperty("http.agent")); //Dalvik/2.1.0
                Log.v(LOG_TAG, "Built URI " + builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                articleJsonStr = buffer.toString();
            }catch (IOException e){
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            }finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getInformationFromJson(articleJsonStr, numArticles);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                mArticleAdapter.clear();
                for(String articleStr : result) {
                    mArticleAdapter.add(articleStr);
                }
                // New data is back from the server.  Hooray!
            }
        }

    }
}
