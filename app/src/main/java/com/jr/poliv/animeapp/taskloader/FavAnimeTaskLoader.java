package com.jr.poliv.animeapp.taskloader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.jr.poliv.animeapp.data.Anime;
import com.jr.poliv.animeapp.data.FavAnimeContract;

import java.util.ArrayList;

import static com.jr.poliv.animeapp.data.FavAnimeContract.FavAnimeEntry.*;

/**
 * Created by poliv on 10/12/2017.
 */

public class FavAnimeTaskLoader extends AsyncTaskLoader<ArrayList<Anime>> {

    public FavAnimeTaskLoader(Context context) {
        super(context);
    }

    @Override
    public ArrayList<Anime> loadInBackground() {

        ArrayList<Anime> arrayList = new ArrayList<>(); Log.d("Paul", "Error getting anime from database ");

        try {
            return getAnimeFromDatabase();
        }catch (Exception e){
            Log.d("Paul", "Error getting anime from database "+e.toString());
        }
        return arrayList;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    private ArrayList<Anime> getAnimeFromDatabase() {
        String[] projection = {COLUMN_TITLE, COLUMN_PLOT, COLUMN_IMAGEURL, COLUMN_IMAGEPATH};

        Cursor cursor = getContext().getContentResolver().query(FavAnimeContract.FavAnimeEntry.CONTENT_URI, projection, null, null, null);

        if( (cursor != null) && (cursor.getCount() > 0) ){
            ArrayList<Anime> list = new ArrayList<>(cursor.getCount());
            for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
                list.add(new Anime(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)), cursor.getString(cursor.getColumnIndex(COLUMN_PLOT)), cursor.getString(cursor.getColumnIndex(COLUMN_IMAGEURL)), cursor.getString(cursor.getColumnIndex(COLUMN_IMAGEPATH))));
            }
            cursor.close();
            return list;
        }else if(cursor != null){
            cursor.close();
        }

        return new ArrayList<Anime>();
    }


}
