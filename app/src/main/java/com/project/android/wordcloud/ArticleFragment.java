package com.project.android.wordcloud;

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
import java.util.Arrays;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class ArticleFragment extends Fragment {
    ArrayAdapter<String> mArticleAdapter;
    public ArticleFragment() {
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
            FetchArticleTask articleTask = new FetchArticleTask();
            articleTask.execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String[] data = {
                "Mon 6/23 - Sunny - 31/17",
                "Tue 6/24 - Foggy - 21/8",
                "Wed 6/25 - Cloudy - 22/17",
                "Thurs 6/26 - Rainy - 18/11",
                "Fri 6/27 - Foggy - 21/10",
                "Sat 6/28 - TRAPPED IN WEATHERSTATION - 23/18",
                "Sun 6/29 - Sunny - 20/7"
        };
        List<String> weekForecast = new ArrayList<String>(Arrays.asList(data));


        // Now that we have some dummy forecast data, create an ArrayAdapter.
        // The ArrayAdapter will take data from a source (like our dummy forecast) and
        // use it to populate the ListView it's attached to.
        mArticleAdapter = new ArrayAdapter<String>(
                getActivity(), // The current context (this activity)
                R.layout.list_item_article, // The name of the layout ID.
                R.id.list_item_article_textview, // The ID of the textview to populate.
                weekForecast);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_article);
        listView.setAdapter(mArticleAdapter);
        return rootView;
    }

    public class FetchArticleTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchArticleTask.class.getSimpleName();

        private String[] getInformationFromJson(String articleJsonStr, int numArticles)
                throws JSONException{
            final String RESPONSE_DATA = "responseData";
            final String RESULTS = "results";
            final String RELATED_STORIES = "relatedStories";
            final String UNESCAPED_URL = "unescapedUrl";

            JSONObject articleJson = new JSONObject(articleJsonStr);
            JSONArray articleArray = articleJson.getJSONObject(RESPONSE_DATA).getJSONArray(RESULTS);

            String[] resultStrs = new String[numArticles];
            int ind_res = 0;
            int ind_json = 0;
            while(numArticles != ind_res){
                JSONObject article = articleArray.getJSONObject(ind_json);
                if(article.has(UNESCAPED_URL)){
                    resultStrs[ind_res] = article.getString(UNESCAPED_URL);
                    ind_res++;
                }
                else if(article.has(RELATED_STORIES)){
                    JSONArray relatedArray = article.getJSONArray(RELATED_STORIES);
                    for(int j = 0; j < relatedArray.length(); j++){
                        resultStrs[ind_res] = relatedArray.getJSONObject(j).getString(UNESCAPED_URL);
                        ind_res++;
                    }
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

            String format = "json";
            int numArticles = 7;

            try{
                final String ARTICLE_BASE_URL = "https://ajax.googleapis.com/ajax/services/search/news?";
                final String QUERY_PARAM = "q";
                final String V_PARAM = "v";
                final String RSZ_PARAM = "rsz";

                //URL url = new URL("https://ajax.googleapis.com/ajax/services/search/news?v=1.0&q=michael%20jordan&rsz=1");
                Uri builtUri = Uri.parse(ARTICLE_BASE_URL).buildUpon()
                                .appendQueryParameter(V_PARAM,"1.0")
                                .appendQueryParameter(QUERY_PARAM,"joy")
                                .appendQueryParameter(RSZ_PARAM,"8")
                                .build();
                URL url = new URL(builtUri.toString());

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
