package com.jr.poliv.animeapp.global;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.jr.poliv.animeapp.BackgroundTask.CheckSeason;
import com.jr.poliv.animeapp.R;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by poliv on 8/26/2017.
 */

public class Global {
    //private static final Global ourInstance = new Global();
    public static final String DELIMITER = " ,";
    private static int userDefinedYear = 0;
    private static Season userDefinedSeason = Season.Summer;



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
            CheckSeason.YearSeason yearSeason = new CheckSeason.YearSeason();
            try {
                yearSeason = new CheckSeason().execute(context.getString(R.string.baseUrl)).get(30, TimeUnit.SECONDS);
            } catch (Exception e) {
                Log.d("Paul", "Error getting year and season" + e.toString());
                e.printStackTrace();
            }

            if (yearSeason.isEmpty())
                Log.d("Paul", "Error getting year and season, empty data");
            else {
                setDefaultYearAndSeason(context, yearSeason);
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
}
