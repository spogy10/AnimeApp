package com.jr.poliv.animeapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by poliv on 10/7/2017.
 */

public class FavAnimeDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "favanime.db";
    private Context context;

    public FavAnimeDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_TODO_TABLE = "CREATE TABLE " + FavAnimeContract.FavAnimeEntry.TABLE_NAME + " (" +
                FavAnimeContract.FavAnimeEntry._ID + " INTEGER PRIMARY KEY autoincrement, " +
                FavAnimeContract.FavAnimeEntry.COLUMN_SEASON + " TEXT NOT NULL, " +
                FavAnimeContract.FavAnimeEntry.COLUMN_YEAR + " INTEGER NOT NULL, " +
                FavAnimeContract.FavAnimeEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                FavAnimeContract.FavAnimeEntry.COLUMN_PLOT + " TEXT, " +
                FavAnimeContract.FavAnimeEntry.COLUMN_IMAGEURL + " TEXT, " +
                FavAnimeContract.FavAnimeEntry.COLUMN_IMAGEPATH + " TEXT, " +
                "UNIQUE ( " + FavAnimeContract.FavAnimeEntry.COLUMN_SEASON + ", " + FavAnimeContract.FavAnimeEntry.COLUMN_YEAR + ", " + FavAnimeContract.FavAnimeEntry.COLUMN_TITLE + " ) ON " +
                "CONFLICT IGNORE" +
                " );";

        db.execSQL(SQL_CREATE_TODO_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavAnimeContract.FavAnimeEntry.TABLE_NAME);
        onCreate(db);
    }
}
