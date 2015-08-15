package com.project.android.wordcloud;

import android.annotation.TargetApi;
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


/**
 * A placeholder fragment containing a simple view.
 */

@TargetApi(11)
public class ArticleFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private ArticleAdapter mArticleAdapter;
    public String lastSearch = "";
    private static final int ARTICLE_LOADER = 0;
    private View rootView;

    private static final String[] ARTICLE_COLUMNS = {
            ArticleContract.ArticleEntry.TABLE_NAME + "." + ArticleContract.ArticleEntry._ID,
            ArticleContract.ArticleEntry.COLUMN_SEARCH_QUERY,
            ArticleContract.ArticleEntry.COLUMN_DATE,
            ArticleContract.ArticleEntry.COLUMN_ARTICLE_NAME,
            ArticleContract.ArticleEntry.COLUMN_ARTICLE_URL,
            ArticleContract.ArticleEntry.COLUMN_ARTICLE_WORDS
    };

    // These indices are tied to ARTICLE_COLUMNS.
    static final int COL_ARTICLE_ID = 0;
    static final int COL_ARTICLE_SEARCH_QUERY = 1;
    static final int COL_ARTICLE_DATE = 2;
    static final int COL_ARTICLE_NAME = 3;
    static final int COL_ARTICLE_URL = 4;
    static final int COL_ARTICLE_WORDS = 5;

    private final String LOG_TAG = ArticleFragment.class.getSimpleName();

    public ArticleFragment() {
    }

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(String url, String words);
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
            updateArticle();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mArticleAdapter = new ArticleAdapter(getActivity(), null, 0);
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        final ListView listView = (ListView) rootView.findViewById(R.id.listview_article);
        listView.setAdapter(mArticleAdapter);

        // Click listener makes call to main activity which then calls word cloud activity / fragment
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                Log.v(LOG_TAG,cursor.getString(COL_ARTICLE_NAME));
                if (cursor != null) {
                    String url = cursor.getString(COL_ARTICLE_URL);
                    String words = cursor.getString(COL_ARTICLE_WORDS);
                    ((Callback) getActivity()).onItemSelected(url,words);
                }
            }
        });
        return rootView;
    }

    // Update articles with new search query
    public void updateArticle(){
        Log.v(LOG_TAG,"update article");
        lastSearch = Utility.getPreferredSearch(getActivity()).toLowerCase();
        // Checks if query actually changed
        if(!MainActivity.lastQuery.equals(lastSearch)){
            MainActivity.lastQuery = lastSearch;
            MainActivity.queryChanged = true;
        }
        Log.v(LOG_TAG, lastSearch);

        FetchArticleTask articleTask = new FetchArticleTask(getActivity());
        articleTask.execute(lastSearch);
    }

    @Override
    public void onStart(){
        super.onStart();
        updateArticle();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = ArticleContract.ArticleEntry.buildArticleUriFromSearch(lastSearch);
        String sortOrder = ArticleContract.ArticleEntry.COLUMN_DATE + " DESC"; // sort on date descending
        return new CursorLoader(getActivity(),
                uri,
                ARTICLE_COLUMNS,
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
