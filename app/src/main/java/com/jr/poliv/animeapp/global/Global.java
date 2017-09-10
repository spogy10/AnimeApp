package com.jr.poliv.animeapp.global;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.File;

/**
 * Created by poliv on 8/26/2017.
 */

public class Global {
    private static final Global ourInstance = new Global();
    public static final String DELIMITER = " ,";


    public static Global getInstance() {
        return ourInstance;
    }

    private Global() {
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
}
