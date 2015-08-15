package com.project.android.wordcloud;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import com.project.android.wordcloud.data.ArticleContract;

/**
 * Created by Joy on 8/15/15.
 */
public class WordCloudFragment extends Fragment {
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
        Log.v(LOG_TAG, "One create view WordCloud Fragment");
        View rootView = inflater.inflate(R.layout.fragment_wordcloud, container, false);

        String articleURL = "";
        String articleStr = "";
        Bundle b = getArguments();

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("words") && intent.hasExtra("url")) {
            articleURL = intent.getStringExtra("url");
            articleStr = intent.getStringExtra("words");
        }
        else if(b!=null){
            articleURL = b.getString("url");
            articleStr = b.getString("words");
        }

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

        return rootView;
    }

}