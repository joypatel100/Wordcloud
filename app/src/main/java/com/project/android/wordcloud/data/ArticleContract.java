package com.project.android.wordcloud.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Joy on 8/14/15.
 */
public class ArticleContract {

    // Contract for Article Data

    public static final String CONTENT_AUTHORITY = "com.project.android.wordcloud";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_ARTICLE = "article";

    public static final class ArticleEntry implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_ARTICLE).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/" + CONTENT_AUTHORITY + "/" + PATH_ARTICLE;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/" + CONTENT_AUTHORITY + "/" + PATH_ARTICLE;

        public static final String TABLE_NAME = "article";


        public static final String COLUMN_SEARCH_QUERY = "search_query"; //search query
        public static final String COLUMN_DATE = "date"; //date article was added
        public static final String COLUMN_ARTICLE_NAME = "article_name"; // name of article
        public static final String COLUMN_ARTICLE_URL = "article_url"; // url of article
        public static final String COLUMN_ARTICLE_WORDS = "article_words"; // words in article

        public static Uri buildArticleUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildArticleUriFromName(String name){
            //return CONTENT_URI.buildUpon().appendPath(name).build();
            return CONTENT_URI.buildUpon().appendQueryParameter(COLUMN_ARTICLE_NAME,name).build();
        }

        public static Uri buildArticleUriFromSearch(String search){
            return CONTENT_URI.buildUpon().appendQueryParameter(COLUMN_SEARCH_QUERY, search).build();
        }

        public static Uri buildArticleUriFromSearchAndName(String search, String name){
            return CONTENT_URI.buildUpon().appendQueryParameter(COLUMN_SEARCH_QUERY, search)
                    .appendQueryParameter(COLUMN_ARTICLE_NAME, name).build();
        }

        public static String getSearchQueryFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }

        public static long getDateFromUri(Uri uri){
            return Long.parseLong(uri.getPathSegments().get(2));
        }

        public static String getNameFromUri(Uri uri){
            return uri.getPathSegments().get(3);
        }


    }

}
