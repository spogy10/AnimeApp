package com.jr.poliv.animeapp.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by poliv on 10/7/2017.
 */

public class FavAnimeContract {

    public static final String CONTENT_AUTHORITY = "com.jr.poliv.animeapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_FAVANIME = "favanime";

    public static class FavAnimeEntry implements BaseColumns {
        public static String TABLE_NAME = "favanime";
        public static String COLUMN_SEASON = "season";
        public static String COLUMN_YEAR = "year";
        public static String COLUMN_TITLE = "title";
        public static String COLUMN_PLOT = "plot";
        public static String COLUMN_IMAGEURL = "imageurl";
        public static String COLUMN_IMAGEPATH = "imagepath";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVANIME).build();
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_FAVANIME;
        public static String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_FAVANIME;

    }

}
