package com.project.android.wordcloud.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import com.project.android.wordcloud.data.ArticleContract.ArticleEntry;
/**
 * Created by Joy on 8/14/15.
 */
public class ArticleProvider extends ContentProvider {
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private ArticleDbHelper mOpenHelper;
    private final String LOG_TAG = ArticleProvider.class.getSimpleName();

    static final int ARTICLE = 100;
    static final int ARTICLE_NAME = 101;

    private static final SQLiteQueryBuilder sArticleQueryBuilder;

    static {
        sArticleQueryBuilder = new SQLiteQueryBuilder();
        sArticleQueryBuilder.setTables(ArticleEntry.TABLE_NAME);
    }


    private static final String sNameSelection = ArticleContract.ArticleEntry.TABLE_NAME +
            "." + ArticleEntry.COLUMN_ARTICLE_NAME + " = ? ";

    static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ArticleContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, ArticleContract.PATH_ARTICLE, ARTICLE);
        matcher.addURI(authority,ArticleContract.PATH_ARTICLE+"/*",ARTICLE_NAME);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new ArticleDbHelper(getContext());
        return true;
    }

    private Cursor getArticleInformationByName(Uri uri, String[] projection, String sortOrder){
        String name = ArticleEntry.getNameFromUri(uri);
        Log.v(LOG_TAG,name);

        String[] selectionArgs = new String[]{name};;
        String selection = sNameSelection;

        return sArticleQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)){
            case ARTICLE_NAME:
                Log.v(LOG_TAG,"article name");
                retCursor = getArticleInformationByName(uri, projection,sortOrder);
                break;
            case ARTICLE:
                Log.v(LOG_TAG,"article");
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ArticleEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                Log.v(LOG_TAG,"default");
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        long _id;
        switch (match){
            case ARTICLE:
                _id = db.insert(ArticleEntry.TABLE_NAME,null,values);
                if(_id > 0){
                    returnUri = ArticleEntry.buildArticleUri(_id);
                }
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ARTICLE:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(ArticleEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case ARTICLE:
                rowsDeleted = db.delete(
                        ArticleEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case ARTICLE:
                rowsUpdated = db.update(ArticleEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case ARTICLE_NAME:
                Log.v(LOG_TAG,"article_name");
                return ArticleEntry.CONTENT_ITEM_TYPE;
            case ARTICLE:
                return ArticleEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
