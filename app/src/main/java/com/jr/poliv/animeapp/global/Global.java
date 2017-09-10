package com.jr.poliv.animeapp.global;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.jr.poliv.animeapp.R;

import java.io.File;

/**
 * Created by poliv on 8/26/2017.
 */

public class Global {
    private static final Global ourInstance = new Global();
    public static final String DELIMITER = " ,";
    private static int currentYear = 2017;
    private static String currentSeason = Season.Summer.toString().toLowerCase();



    public static Global getInstance() {
        return ourInstance;
    }

    private Global() {
    }

    public static int getDefaultYear(Context context) {
        return context.getSharedPreferences(context.getString(R.string.settings_shared_preferences_file_name), Context.MODE_PRIVATE).getInt(context.getString(R.string.default_year), 0);
    }

    public static String getDefaultSeason(Context context) {
        return context.getSharedPreferences(context.getString(R.string.settings_shared_preferences_file_name), Context.MODE_PRIVATE).getString(context.getString(R.string.default_season), "");
    }

    public void setDefaultYearAndSeason(Context context){
        //TODO: create async task to get webcode from base url and check the current year and season and save it in a shared preference file
        
    }

    public static int getCurrentYear() {
        return currentYear;
    }

    public static void setCurrentYear(int currentYear) {
        Global.currentYear = currentYear;
    }

    public static String getCurrentSeason() {
        return currentSeason;
    }

    public static void setCurrentSeason(String currentSeason) {
        Global.currentSeason = currentSeason;
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

    public static String getSeasonFolder(Context context, int year, Season season){
        return context.getFilesDir() + File.separator + year + File.separator + season.toString();
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
}
