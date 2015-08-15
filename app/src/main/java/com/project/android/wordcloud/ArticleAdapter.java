package com.project.android.wordcloud;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.project.android.wordcloud.data.ArticleContract;

/**
 * Created by Joy on 8/14/15.
 */
@TargetApi(11)
public class ArticleAdapter extends CursorAdapter {

    public ArticleAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    private String convertCursorRowToUXFormat(Cursor cursor){
        int name_ind = cursor.getColumnIndex(ArticleContract.ArticleEntry.COLUMN_ARTICLE_NAME);
        return cursor.getString(name_ind);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_article, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tv = (TextView)view;
        tv.setText(convertCursorRowToUXFormat(cursor));
    }
}
