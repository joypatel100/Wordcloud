package com.project.android.wordcloud;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;

import java.io.IOException;

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
                    .add(R.id.container, new WordCloudFragment())
                    .commit();
        }
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class WordCloudFragment extends Fragment {
        private final String LOG_TAG = WordCloudFragment.class.getSimpleName();

        public WordCloudFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_wordcloud, container, false);
            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
                //String articleStr = intent.getStringExtra(Intent.EXTRA_TEXT);
                /*StringBuilder articleStr = new StringBuilder("Hi, my name is Joy Patel. " +
                        "I am an undergraduate. " +
                        "I love math and computer science. " +
                        "Specifically, I enjoy data analytics. Hi Hi Hi");*/
                StringBuilder articleStr = new StringBuilder();
                String articleURL = intent.getStringExtra(Intent.EXTRA_TEXT);

                try {

                    // need http protocol
                    //Document doc = Jsoup.connect("http://fr.news.yahoo.com/france-foot-pro-vote-gr%C3%A8ve-fin-novembre-contre-125358890.html").get();
                    Log.v(LOG_TAG, articleURL);
                    if (android.os.Build.VERSION.SDK_INT >= 10) {
                        StrictMode.ThreadPolicy tp = StrictMode.ThreadPolicy.LAX; StrictMode.setThreadPolicy(tp);
                    }
                    Document doc = Jsoup.connect(articleURL).ignoreContentType(true).get();

                    for (Element el : doc.select("body").select("*")) {
                        for (TextNode node : el.textNodes()) {
                            articleStr.append(node.text());
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                WordCloud wc = new WordCloud(articleStr.toString());
                Spannable span = new SpannableString(wc.words());
                //span.setSpan(new RelativeSizeSpan(3f), 0, 10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                for(String key: wc.myWC.keySet()){
                    double[] info = wc.myWC.get(key);
                    span.setSpan(new RelativeSizeSpan((float) info[0]), (int) info[1],(int) info[2],Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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
