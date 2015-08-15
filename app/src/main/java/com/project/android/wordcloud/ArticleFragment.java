package com.project.android.wordcloud;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.project.android.wordcloud.data.ArticleContract;

import java.util.HashMap;


/**
 * A placeholder fragment containing a simple view.
 */

@TargetApi(11)
public class ArticleFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private ArticleAdapter mArticleAdapter;
    HashMap<String,String> mArticleURL;
    public String lastSearch = "";
    public String lastLanguage = "en";
    private static final int ARTICLE_LOADER = 0;

    private static final String[] ARTICLE_COLUMNS = {
            ArticleContract.ArticleEntry.TABLE_NAME + "." + ArticleContract.ArticleEntry._ID,
            ArticleContract.ArticleEntry.COLUMN_SEARCH_QUERY,
            ArticleContract.ArticleEntry.COLUMN_DATE,
            ArticleContract.ArticleEntry.COLUMN_ARTICLE_NAME,
            ArticleContract.ArticleEntry.COLUMN_ARTICLE_URL,
            ArticleContract.ArticleEntry.COLUMN_ARTICLE_WORDS
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_ARTICLE_ID = 0;
    static final int COL_ARTICLE_SEARCH_QUERY = 1;
    static final int COL_ARTICLE_DATE = 2;
    static final int COL_ARTICLE_NAME = 3;
    static final int COL_ARTICLE_URL = 4;
    static final int COL_ARTICLE_WORDS = 5;

    private final String LOG_TAG = ArticleFragment.class.getSimpleName();

    public ArticleFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG,"article fragment created");
        setRetainInstance(true);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        getLoaderManager().initLoader(ARTICLE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
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
            updateArticle(lastSearch,lastLanguage);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mArticleAdapter = new ArticleAdapter(getActivity(), null, 0);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_article);
        listView.setAdapter(mArticleAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                Log.v(LOG_TAG,cursor.getString(COL_ARTICLE_NAME));
                if (cursor != null) {
                    String search = Utility.getPreferredSearch(getActivity());
                    Uri uri = ArticleContract.ArticleEntry.buildArticleUriFromName(cursor.getString(COL_ARTICLE_NAME));
                    String url = cursor.getString(COL_ARTICLE_URL);
                    String words = cursor.getString(COL_ARTICLE_WORDS);
                    Intent intent = new Intent(getActivity(), WordCloudActivity.class).putExtra("words",words)
                            .putExtra("url",url);
                            //.setData(uri);

                    startActivity(intent);
                }
            }
        });
        return rootView;
    }

    public void updateArticle(String search, String language ){
        lastSearch = Utility.getPreferredSearch(getActivity());
        lastLanguage = language;
        Log.v(LOG_TAG, lastSearch);
        FetchArticleTask articleTask = new FetchArticleTask(getActivity());
        articleTask.execute(search,lastLanguage);
    }

    @Override
    public void onStart(){
        super.onStart();
        updateArticle(lastSearch,lastLanguage);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = ArticleContract.ArticleEntry.buildArticleUriFromSearch(lastSearch);
        String sortOrder = ArticleContract.ArticleEntry.COLUMN_DATE + " DESC";
        String selection = ArticleContract.ArticleEntry.COLUMN_SEARCH_QUERY
                + " = ?";
        return new CursorLoader(getActivity(),
                uri,
                ARTICLE_COLUMNS,
                //selection,
                //new String[]{lastSearch},
                null,null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mArticleAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mArticleAdapter.swapCursor(null);
    }
}
