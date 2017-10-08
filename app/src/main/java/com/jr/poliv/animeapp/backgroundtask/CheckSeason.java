package com.jr.poliv.animeapp.backgroundtask;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by poliv on 9/17/2017.
 */

public class CheckSeason extends AsyncTask<String, Void, CheckSeason.YearSeason> {


    @Override
    protected YearSeason doInBackground(String... params) {
        String url = params[0];

        try {
            return getYearSeasonFromWebcode(getWebCode(url));
        } catch (Exception e) {
            Log.d("Paul", "Error with check season, "+e.toString());
            e.printStackTrace();
        }

        return new YearSeason();
    }


    private String getWebCode(String urlString) throws Exception {
        InputStream is = null;
        try {
            URL url = new URL(urlString);
            HttpURLConnection in = (HttpURLConnection) url.openConnection();
            in.addRequestProperty("User-Agent", "Foo?");

            in.setReadTimeout(0 /* milliseconds */);
            in.setConnectTimeout(0 /* milliseconds */);
            in.setRequestMethod("GET");
            in.setDoInput(true);
            in.connect();

            is = in.getInputStream();

            return IOUtils.toString(is, "UTF-8");
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public static YearSeason getYearSeasonFromWebcode(String webCode){
        int year = 0;
        String season = "";
        String begin = "<title>",
                end = "</title>";
        String[] array = webCode.substring(webCode.indexOf(begin)+begin.length(), webCode.indexOf(end)).split(" ");
        //Log.d("Paul", array[0] + " | " + array[1]);
        season = array[0].replaceAll("[^A-Za-z]", "");
        year = Integer.parseInt(array[1].replaceAll("[^0-9]", ""));

        return new YearSeason(year, season);
    }


    public static class YearSeason{
        int year;
        String season;

        public YearSeason(){
            year = 0;
            season = "";
        }

        public YearSeason(int year, String season){
            this.year = year;
            this.season = season;
        }

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }

        public String getSeason() {
            return season;
        }

        public void setSeason(String season) {
            this.season = season;
        }

        public boolean isEmpty(){
            return (year == 0 && season.equals(""));
        }
    }
}
