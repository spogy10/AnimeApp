package com.jr.poliv.animeapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by poliv on 10/7/2017.
 */

public class FavAnimeProvider extends ContentProvider {

    private static final int FAVANIME = 100;
    private static final int FAVANIME_ID = 101;

    private static final UriMatcher uriMatcher = buildUriMatcher();
    private FavAnimeDBHelper helper;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FavAnimeContract.CONTENT_AUTHORITY;

        uriMatcher.addURI(authority, FavAnimeContract.PATH_FAVANIME, FAVANIME);
        uriMatcher.addURI(authority, FavAnimeContract.PATH_FAVANIME + "/#", FAVANIME_ID);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        helper = new FavAnimeDBHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        switch (uriMatcher.match(uri)) {

            //to-do
            case FAVANIME: {
                cursor = helper.getReadableDatabase().query(FavAnimeContract.FavAnimeEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            //to-do by id
            case FAVANIME_ID: {
                cursor = helper.getReadableDatabase().query(
                        FavAnimeContract.FavAnimeEntry.TABLE_NAME,
                        projection,
                        FavAnimeContract.FavAnimeEntry._ID + " = " + ContentUris.parseId(uri) + "",
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri" + uri);

        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = helper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        Uri returnUri;

        switch(match) {
            case FAVANIME: {

                long _id = db.insert(FavAnimeContract.FavAnimeEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = FavAnimeContract.FavAnimeEntry.CONTENT_URI;
                else
                    throw new SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri );
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = helper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int rowsDeleted;
        switch(match) {
            case FAVANIME:
                rowsDeleted = db.delete(FavAnimeContract.FavAnimeEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri:" + uri);
        }
        //Because null deletes all rows and db.delete would return 0 in this case
        if (selection == null || rowsDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = helper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int rowsUpdated;

        switch(match) {
            case FAVANIME:
                rowsUpdated = db.update(FavAnimeContract.FavAnimeEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri:" + uri);
        }
        if (rowsUpdated != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = helper.getWritableDatabase();
        final int match = uriMatcher.match(uri);

        switch(match) {
            case FAVANIME:
                db.beginTransaction();
                int returnCount = 0;
                try{
                    for (ContentValues value : values){
                        long _id = db.insert(FavAnimeContract.FavAnimeEntry.TABLE_NAME, null, value);
                        if(_id != -1){
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

}
