package com.project.android.wordcloud;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;

import com.project.android.wordcloud.data.ArticleContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by Joy on 8/14/15.
 */
public class FetchArticleTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchArticleTask.class.getSimpleName();
    private final Context mContext;
    private ProgressDialog dialog; // dialogue box

    public FetchArticleTask(Context context){
        this.mContext = context;
        if(MainActivity.queryChanged) {
            this.dialog = new ProgressDialog(context);
            //dialog.setCancelable(false);
        }
    }

    protected void onPreExecute(){
        if(MainActivity.queryChanged) {
            dialog.setMessage("Getting articles. Please wait.");
            dialog.show();
        }
    }


    // Helper method to get words from webpage from a url link
    private StringBuilder getWordsFromArticleURL(String url){
        StringBuilder articleStr = new StringBuilder();
        try {
            Log.v(LOG_TAG, url);
            if (android.os.Build.VERSION.SDK_INT >= 10) {
                StrictMode.ThreadPolicy tp = StrictMode.ThreadPolicy.LAX; StrictMode.setThreadPolicy(tp);
            }
            Document doc = Jsoup.connect(url).ignoreContentType(true).get();
            int lines = 0;
            for (Element el : doc.select("body").select("*")) {
                for (TextNode node : el.textNodes()) {
                    articleStr.append(node.text());
                    lines++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return articleStr;
    }

    // Parses JSON Strings
    private void getInformationFromJson(String articleJsonStr, int numArticles,String search)
            throws JSONException {

        final String RESULTS = "results";
        final String TITLE = "title";
        final String ARTICLE_URL = "url";
        try {
            JSONObject articleJson = new JSONObject(articleJsonStr);
            JSONArray articleArray = articleJson.getJSONArray(RESULTS);

            //ArrayList<String> resultStrs = new ArrayList<>();
            int ind_res = 0;
            int ind_json = 0;
            //mArticleURL = new HashMap<>();
            Vector<ContentValues> cvv = new Vector<ContentValues>(articleArray.length());
            while (numArticles != ind_res) {
                Log.v(LOG_TAG, Integer.toString(ind_json));
                JSONObject article = articleArray.getJSONObject(ind_json);
                if (article.has(TITLE)) {
                    String title = article.getString(TITLE);
                    String url = article.getString(ARTICLE_URL);
                    StringBuilder words = getWordsFromArticleURL(url);
                    //resultStrs.add(title);
                    //mArticleURL.put(title, url);
                    ContentValues articleValues = new ContentValues();
                    articleValues.put(ArticleContract.ArticleEntry.COLUMN_SEARCH_QUERY,search);
                    articleValues.put(ArticleContract.ArticleEntry.COLUMN_DATE, System.currentTimeMillis());
                    articleValues.put(ArticleContract.ArticleEntry.COLUMN_ARTICLE_NAME, title);
                    articleValues.put(ArticleContract.ArticleEntry.COLUMN_ARTICLE_URL, url);
                    articleValues.put(ArticleContract.ArticleEntry.COLUMN_ARTICLE_WORDS, words.toString());
                    cvv.add(articleValues);
                    ind_res++;
                }
                ind_json++;
            }
            int inserted = 0;
            if (cvv.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cvv.size()];
                cvv.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(ArticleContract.ArticleEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "FetchWeatherTask Complete. " + inserted + " Inserted");
        }
        catch (JSONException e){
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(String... params) {
        // Makes call to Faroo API and parses JSON information
        if(MainActivity.queryChanged) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String articleJsonStr = null;

            String key = "8np7pfcewGev6KGJuUcef-1CAC4_";
            String search = params[0];
            String source = "web";
            String format = "json";
            String language = "en";

            int numArticles = 10;

            try {
                final String ARTICLE_BASE_URL = "http://www.faroo.com/api?start=1&length=10&l=en";
                final String SEARCH_PARAM = "q";
                final String SOURCE_PARAM = "src";
                final String FORMAT_PARAM = "f";
                final String KEY_PARAM = "key";
                final String LANGUAGE_PARAM = "l";


                //URL url = new URL("http://www.faroo.com/api?q=&start=1&length=10&l=en&src=news&f=json&key=8np7pfcewGev6KGJuUcef-1CAC4_");
                Uri builtUri = Uri.parse(ARTICLE_BASE_URL).buildUpon()
                        .appendQueryParameter(SEARCH_PARAM, search)
                        .appendQueryParameter(LANGUAGE_PARAM, language)
                        .appendQueryParameter(SOURCE_PARAM, source)
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .appendQueryParameter(KEY_PARAM, key)
                        .build();

                URL url = new URL(builtUri.toString());

                Log.v("USER_AGENT", System.getProperty("http.agent")); //Dalvik/2.1.0
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
                getInformationFromJson(articleJsonStr, numArticles, search);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            } finally {
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
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void v){
        if(MainActivity.queryChanged && dialog.isShowing()) {
            dialog.dismiss();
        }
        MainActivity.queryChanged = false;

    }
}
