package com.project.android.wordcloud;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import com.project.android.wordcloud.data.ArticleContract;

/**
 * Created by Joy on 8/11/15.
 */
public class WordCloudActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wordcloud);
        if (savedInstanceState == null) {
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class WordCloudFragment extends Fragment {
        private final String LOG_TAG = WordCloudFragment.class.getSimpleName();
        private static final String ARTICLE_SHARE_HASHTAG = " #Wordcloud";
        private ShareActionProvider mShareActionProvider;
        private String mArticle;
        private static final int WC_LOADER = 0;

        private static final String[] ARTICLE_COLUMNS = {
                ArticleContract.ArticleEntry.TABLE_NAME + "." + ArticleContract.ArticleEntry._ID,
                ArticleContract.ArticleEntry.COLUMN_SEARCH_QUERY,
                ArticleContract.ArticleEntry.COLUMN_DATE,
                ArticleContract.ArticleEntry.COLUMN_ARTICLE_NAME,
                ArticleContract.ArticleEntry.COLUMN_ARTICLE_URL,
                ArticleContract.ArticleEntry.COLUMN_ARTICLE_WORDS
        };

        static final int COL_ARTICLE_ID = 0;
        static final int COL_ARTICLE_SEARCH_QUERY = 1;
        static final int COL_ARTICLE_DATE = 2;
        static final int COL_ARTICLE_NAME = 3;
        static final int COL_ARTICLE_URL = 4;
        static final int COL_ARTICLE_WORDS = 5;

        private View rootView;

        public WordCloudFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_wordcloud, container, false);
            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra("words")) {
                //StringBuilder articleStr = new StringBuilder();
                String articleURL = intent.getStringExtra("url");
                String articleStr = intent.getStringExtra("words");

                WordCloud wc = new WordCloud(articleStr);
                Spannable span = new SpannableString(wc.words());
                //span.setSpan(new RelativeSizeSpan(3f), 0, 10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                for(String key: wc.myWC.keySet()){
                    double[] info = wc.myWC.get(key);
                    span.setSpan(new RelativeSizeSpan((float) info[0]), (int) info[1],(int) info[2], Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                ((TextView) rootView.findViewById(R.id.wordcloud_text))
                        .setText(span);
                TextView link = (TextView) rootView.findViewById(R.id.wordcloud_link);
                link.setClickable(true);
                link.setMovementMethod(LinkMovementMethod.getInstance());
                String hyperlink = "<a href=\"" + articleURL + "\"> Article Link </a>";
                link.setText(Html.fromHtml(hyperlink));
            }

            return rootView;
        }

    }
}
