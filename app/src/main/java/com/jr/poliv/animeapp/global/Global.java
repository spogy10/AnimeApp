package com.jr.poliv.animeapp.global;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.jr.poliv.animeapp.backgroundtask.CheckSeason;
import com.jr.poliv.animeapp.R;
import com.jr.poliv.animeapp.data.Anime;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import static com.jr.poliv.animeapp.data.FavAnimeContract.FavAnimeEntry.*;

/**
 * Created by poliv on 8/26/2017.
 */

public class Global {
    //private static final Global ourInstance = new Global();
    public static final String DELIMITER = " ,";
    private static int userDefinedYear = 0;
    private static Season userDefinedSeason = Season.Summer;
    private static int scrollAmount;



    /*public static Global getInstance() {
        return ourInstance;
    }*/

    private Global() {
    }

    public static int getDefaultYear(Context context) { //get the default year (the year it is)
        return context.getSharedPreferences(context.getString(R.string.settings_shared_preferences_file_name), Context.MODE_PRIVATE).getInt(context.getString(R.string.default_year), 0);
    }

    public static Season getDefaultSeason(Context context) { //get the default season (the season it is)
        return Season.valueOf(context.getSharedPreferences(context.getString(R.string.settings_shared_preferences_file_name), Context.MODE_PRIVATE).getString(context.getString(R.string.default_season), "Summer"));
    }

    public static void setDefaultYearAndSeason(Context context){ //set the default season and year
        if(hasAccessToNet(context)) {
            try {
                new CheckSeason(context).execute(context.getString(R.string.baseUrl));
            } catch (Exception e) {
                Log.d("Paul", "Error getting year and season" + e.toString());
                e.printStackTrace();
            }
        }
    }

    public static void setDefaultYearAndSeason(Context context, CheckSeason.YearSeason yearSeason){
        SharedPreferences.Editor editor = context.getSharedPreferences(context.getString(R.string.settings_shared_preferences_file_name), Context.MODE_PRIVATE).edit();
        editor.putInt(context.getString(R.string.default_year), yearSeason.getYear());
        editor.putString(context.getString(R.string.default_season), yearSeason.getSeason());
        editor.apply();
    }

    public static int getUserDefinedYear() { //get the user defined year
        return userDefinedYear;
    }

    public static void getUserDefinedYearFromSharedPreference(Context context) { //get the user defined year
        userDefinedYear = context.getSharedPreferences(context.getString(R.string.settings_shared_preferences_file_name), Context.MODE_PRIVATE).getInt(context.getString(R.string.user_defined_year), 0);
    }

    public static void setUserDefinedYear(Context context, int userDefinedYear) { //set the user defined year and update sharedprefrence
        SharedPreferences.Editor editor = context.getSharedPreferences(context.getString(R.string.settings_shared_preferences_file_name), Context.MODE_PRIVATE).edit();
        editor.putInt(context.getString(R.string.user_defined_year), userDefinedYear).apply();
        Global.userDefinedYear = userDefinedYear;
    }

    public static Season getUserDefinedSeason() { //get the user defined season
        return userDefinedSeason;
    }

    public static void getUserDefinedSeasonFromSharedPreference(Context context) { //get the user defined season
        userDefinedSeason = Season.valueOf(context.getSharedPreferences(context.getString(R.string.settings_shared_preferences_file_name), Context.MODE_PRIVATE).getString(context.getString(R.string.user_defined_season), "Summer"));
    }

    public static void getScrollAmountFromSharedPreference(Context context){
        Global.scrollAmount =  context.getSharedPreferences(context.getString(R.string.settings_shared_preferences_file_name), Context.MODE_PRIVATE).getInt(context.getString(R.string.scroll_amount), 10);
    }

    public static int getScrollAmount(){
        return scrollAmount;
    }

    public static void setScrollAmount(Context context, int scrollAmount) { //set the user defined year and update sharedprefrence
        SharedPreferences.Editor editor = context.getSharedPreferences(context.getString(R.string.settings_shared_preferences_file_name), Context.MODE_PRIVATE).edit();
        editor.putInt(context.getString(R.string.scroll_amount), scrollAmount).apply();
        Global.scrollAmount = scrollAmount;
    }

    public static void setUserDefinedSeason(Context context, Season userDefinedSeason) { //set the user defined season and update sharedprefrence
        SharedPreferences.Editor editor = context.getSharedPreferences(context.getString(R.string.settings_shared_preferences_file_name), Context.MODE_PRIVATE).edit();
        editor.putString(context.getString(R.string.user_defined_season), userDefinedSeason.toString()).apply();
        Global.userDefinedSeason = userDefinedSeason;
    }

    public static String getImageFilePath(Context context, int year, Season season, int positionInFolder){
        return context.getFilesDir() + File.separator + year + File.separator + season.toString() + File.separator + positionInFolder +".jpeg";
    }

    public static String getJSONFilePath(Context context, int year, Season season){
        return context.getFilesDir() + File.separator + year + File.separator + season.toString() + File.separator + "JSON.json";
    }

    public static String getJSONFilePath(String directory){
        return directory + File.separator + "JSON.json";
    }

    public static File getJSONFile(String directory){
        return new File(directory + File.separator + "JSON.json");
    }

    public static String getSeasonFolder(Context context, int year, Season season){
        return context.getFilesDir() + File.separator + year + File.separator + season.toString();
    }

    public static String getYearFolder(Context context, int year){
        return context.getFilesDir() + File.separator + year;
    }

    public static String escapeString(String string){
        string = string.replace("+", "++");
        return string.replace(",", "+,");
    }

    public static String unEscapeString(String string){
        string = string.replace("+,", ",");
        return string.replace("++", "+");
    }

    public static boolean checkForLocalData(Context context, int year, Season season){
        return new File(getJSONFilePath(context, year, season)).exists();
    }


    public static boolean hasAccessToNet(Context context){
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public static String createCustomUrl(Context context, int year, Season season){
        return context.getString(R.string.baseUrl)+"/"+String.valueOf(year)+"/"+season.toString().toLowerCase();
    }

    public static void writeStringToFile(String directory, String fileName, String stringToBeWritten) throws IOException {
            FileWriter fileWriter = new FileWriter(new File(directory, fileName));
            fileWriter.write(stringToBeWritten);
            fileWriter.close();
    }

///////////////////////////DATABASE          METHODS////////////////////////////////////////////////

    public static void favouriteAnAnime(Context context, Anime anime){
        ContentValues values = new ContentValues(10);
        values.put(COLUMN_SEASON, String.valueOf(userDefinedSeason));
        values.put(COLUMN_YEAR, userDefinedYear);
        values.put(COLUMN_TITLE, anime.getTitle());
        values.put(COLUMN_PLOT, anime.getPlot());
        values.put(COLUMN_IMAGEURL, anime.getImageUrl());
        values.put(COLUMN_IMAGEPATH, anime.getImagePath());

        context.getContentResolver().insert(CONTENT_URI, values);
    }

    public static int favouriteMultipleAnime(Context context, LinkedList<Anime> animeList, int year, Season season){
        ContentValues[] contentValues = new ContentValues[animeList.size()];
        int i = 0;
        for(Anime anime : animeList){
            contentValues[i] = new ContentValues(10);
            contentValues[i].put(COLUMN_SEASON, String.valueOf(season));
            contentValues[i].put(COLUMN_YEAR, year);
            contentValues[i].put(COLUMN_TITLE, anime.getTitle());
            contentValues[i].put(COLUMN_PLOT, anime.getPlot());
            contentValues[i].put(COLUMN_IMAGEURL, anime.getImageUrl());
            contentValues[i].put(COLUMN_IMAGEPATH, anime.getImagePath());
            i++;
        }

        return context.getContentResolver().bulkInsert(CONTENT_URI, contentValues);
    }

    public static void unFavouriteAnAnime(Context context, Anime anime, int year, Season season){
        String seasonString = season.toString(),
                title = anime.getTitle(),
                whereStatement = "("+ COLUMN_SEASON +" = ? AND "+ COLUMN_YEAR +" = "+ year +" AND "+ COLUMN_TITLE +" = ?)";

        String[] args = {seasonString, title};


        context.getContentResolver().delete(CONTENT_URI, whereStatement, args);
    }

    public static void unFavouriteEntireSeason(Context context, int year, Season season){
        String seasonString = season.toString(),
                whereStatement = "("+ COLUMN_SEASON +" = ? AND "+ COLUMN_YEAR +" = "+ year +")";

        String[] args = {seasonString};


        context.getContentResolver().delete(CONTENT_URI, whereStatement, args);
    }

    public static void unFavouriteEntireYear(Context context, int year){
        String whereStatement = "("+ COLUMN_YEAR +" = "+ year +"?)";


        context.getContentResolver().delete(CONTENT_URI, whereStatement, null);
    }

    public static HashSet<String> getAnimeTitlesFromSeason(Context context, int year, Season season){
        String seasonString = season.toString(),
                whereStatement = "("+ COLUMN_SEASON +" = ? AND "+ COLUMN_YEAR +" = "+ year +")";

        String[] args = {seasonString},
                projection = {COLUMN_TITLE};

        Cursor cursor = context.getContentResolver().query(CONTENT_URI, projection, whereStatement, args, null);

        if(cursor == null)
            return null;
        else if (cursor.getCount() == 0)
            return null;

        HashSet<String> set = new HashSet<String>(cursor.getCount());

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            set.add(cursor.getString(0));
        }

        cursor.close();
        return set;
    }
}
