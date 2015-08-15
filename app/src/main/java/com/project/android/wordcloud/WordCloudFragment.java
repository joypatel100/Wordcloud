package com.project.android.wordcloud;

import android.content.Intent;
import android.graphics.Color;
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
import android.widget.TextView;

/**
 * Created by Joy on 8/15/15.
 */
public class WordCloudFragment extends Fragment {
    // Fragment to create Word Cloud and update views

    private final String LOG_TAG = WordCloudFragment.class.getSimpleName();


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

        Log.v(LOG_TAG,"about to generate word cloud");
        WordCloud wc = new WordCloud(articleStr,Utility.getNumWordsCloud(getActivity()));
        Log.v(LOG_TAG,"generated word cloud");
        Spannable span = new SpannableString(wc.words());
        //span.setSpan(new RelativeSizeSpan(3f), 0, 10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        for(String key: wc.myWC.keySet()){
            double[] info = wc.myWC.get(key);
            span.setSpan(new RelativeSizeSpan((float) info[0]), (int) info[1],(int) info[2], Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        TextView wordcloud = (TextView) rootView.findViewById(R.id.wordcloud_text);
        wordcloud.setText(span);
        wordcloud.setTextColor(Color.parseColor(MainActivity.wordCloudColor));
        TextView link = (TextView) rootView.findViewById(R.id.wordcloud_link);
        link.setClickable(true);
        link.setMovementMethod(LinkMovementMethod.getInstance());
        if(!articleURL.equals("")) {
            String hyperlink = "<a href=\"" + articleURL + "\"> Article Link </a>";
            link.setText(Html.fromHtml(hyperlink));
        }
        return rootView;
    }

}