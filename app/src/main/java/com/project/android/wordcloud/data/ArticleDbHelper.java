package com.project.android.wordcloud.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.project.android.wordcloud.data.ArticleContract.ArticleEntry;

/**
 * Created by Joy on 8/14/15.
 */
public class ArticleDbHelper extends SQLiteOpenHelper {

    // Creates Database

    private static final int DATABASE_VERSION = 4;
    static final String DATABASE_NAME = "wordcloud.db";

    public ArticleDbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_ARTICLE_TABLE = "CREATE TABLE "
                + ArticleEntry.TABLE_NAME
                + " (" + ArticleEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ArticleEntry.COLUMN_SEARCH_QUERY + " TEXT NOT NULL, "
                + ArticleEntry.COLUMN_DATE + " REAL NOT NULL, "
                + ArticleEntry.COLUMN_ARTICLE_NAME + " TEXT NOT NULL, "
                + ArticleEntry.COLUMN_ARTICLE_URL + " TEXT NOT NULL, "
                + ArticleEntry.COLUMN_ARTICLE_WORDS + " TEXT NOT NULL, "
                + " UNIQUE (" + ArticleEntry.COLUMN_ARTICLE_NAME + ") ON CONFLICT REPLACE);";
        db.execSQL(SQL_CREATE_ARTICLE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ArticleEntry.TABLE_NAME);
        onCreate(db);
    }
}
