package com.jr.poliv.animeapp.TaskLoader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.jr.poliv.animeapp.BackgroundTask.CreateLocalData;
import com.jr.poliv.animeapp.Data.Anime;
import com.jr.poliv.animeapp.R;
import com.jr.poliv.animeapp.global.DataMode;
import com.jr.poliv.animeapp.global.Global;
import com.jr.poliv.animeapp.global.Season;


import org.apache.commons.io.IOUtils;
import org.apache.commons.text.StringEscapeUtils;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Created by poliv on 8/23/2017.
 */

public class AnimeTaskLoader extends AsyncTaskLoader<ArrayList<Anime>> {

    private String myAnimeListUrl = getContext().getString(R.string.baseUrl);
    private int year = 2017;
    private Season season = Season.Summer;
    private int mode = 0;
    public static final int UPDATE_MODE = 1;


    public AnimeTaskLoader(Context context) {
        super(context);
    }

    public AnimeTaskLoader(Context context, int mode){
        super(context);
        this.mode = mode;
    }

    @Override
    public ArrayList<Anime> loadInBackground() {
            Log.d("Paul", "Task loader load in background");

        //DataMode.setMode(DataMode.TEST); new ArrayList<Anime>(Arrays.asList(Anime.createTestData()));

        try {
            if( (DataMode.getMode() == DataMode.ONLINEDATA) || (mode == UPDATE_MODE) ){
                Log.d("Paul", "Getting online data");
                return new ArrayList<Anime>(parseWebCode(webCode()));
            }
            else if(DataMode.getMode() == DataMode.LOCALDATA)
                return new ArrayList<Anime>(parseLocalJSON());
        } catch (IOException e) {
            Log.d("Paul", "IOException Called from AnimeTaskLoader by webCode method "+e.toString());
            e.printStackTrace();
        }
        return new ArrayList<Anime>();
    }

    @Override
    public void deliverResult(ArrayList<Anime> data) {
        //cache the data before delivering it
        if( (DataMode.getMode() == DataMode.ONLINEDATA) || (mode == UPDATE_MODE)){
            Log.d("Paul", "Creating local data");
            new CreateLocalData(getContext(), data, year, season).execute();
        }
        super.deliverResult(data);
    }



    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        Log.d("Paul", "About to load in Data Mode: "+ DataMode.getMode().toString());

        if( (!Global.hasAccessToNet(getContext())) && (DataMode.getMode() == DataMode.ONLINEDATA) ) {
            Toast.makeText(getContext(), getContext().getString(R.string.no_network_connection), Toast.LENGTH_SHORT).show();
        }else {
            forceLoad();
        }
    }


    private String webCode() throws IOException {
        InputStream is = null;
        try {
            URL url = new URL(myAnimeListUrl);
            HttpURLConnection in = (HttpURLConnection) url.openConnection();in.addRequestProperty("User-Agent", "Foo?");

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


    private LinkedList<Anime> parseWebCode(String webCode){
        Log.d("Paul", String.valueOf(webCode.length()));


        /*try {
            FileWriter fileWriter = new FileWriter(new File(getContext().getFilesDir(), "Website.txt"));

            fileWriter.write(webCode);
            fileWriter.close();
        } catch (IOException e) {
            Log.d("Paul", "File not written "+e.toString());
            e.printStackTrace();
        }*/



        LinkedList<Anime> animeList = new LinkedList<>();

        final String beginTitle = "class=\"link-title\">",
                endTitle = "</a>",
                beginImageUrl = "<div class=\"image\">",
                endImageUrl = "\" ",
                beginPlot = "class=\"preline\">",
                endPlot = "</span>";

//        Log.d("Paul", String.valueOf(webCode.contains(beginTitle))+webCode.indexOf(beginTitle));
//        Log.d("Paul", String.valueOf(webCode.contains(beginImageUrl))+webCode.indexOf(beginImageUrl));
//        Log.d("Paul", String.valueOf(webCode.contains(beginPlot))+webCode.indexOf(beginPlot));

        while(webCode.contains(beginTitle)){
            webCode = webCode.substring(webCode.indexOf(beginTitle));
            String title = StringEscapeUtils.unescapeHtml4(webCode.substring(beginTitle.length(), webCode.indexOf(endTitle)));
            Log.d("Paul", "Tittle: "+title);
            webCode = webCode.substring(webCode.indexOf(endTitle));

            webCode = webCode.substring(webCode.indexOf(beginImageUrl));
            String imageUrl = getImageUrl(webCode, endImageUrl);
            Log.d("Paul", "Image Url: "+imageUrl);

            webCode = webCode.substring(webCode.indexOf(beginPlot));
            String plot = StringEscapeUtils.unescapeHtml4(webCode.substring(beginPlot.length(), webCode.indexOf(endPlot)));
            Log.d("Paul", "Plot: "+plot);

            animeList.add(new Anime(title, plot, imageUrl));

        }

        return animeList;
    }

    private String getImageUrl(String webCode, String endImageUrl){
        String beginImageUrl = "src=\"";

        webCode = webCode.substring(webCode.indexOf(beginImageUrl));
        return webCode.substring(beginImageUrl.length(), webCode.indexOf(endImageUrl));
    }

    private LinkedList<Anime> parseLocalJSON(){
        LinkedList<Anime> list = new LinkedList<>();
        if(Global.checkForLocalData(getContext(), year, season)){
            try {
                Scanner in = new Scanner(new File(Global.getJSONFilePath(getContext(), year, season)));
                list = getAnimeFromJSON(in);
            } catch (FileNotFoundException e) {
                Log.d("Paul", "error opening JSON file " + e.toString());
                e.printStackTrace();
            }
        }else{
            Log.d("Paul", "Error getting local data, JSON file does not exist");
        }

        return  list;
    }

    private LinkedList<Anime> getAnimeFromJSON(@NonNull Scanner in){
        LinkedList<Anime> list = new LinkedList<>();
        in.useDelimiter(Global.DELIMITER);

        while(in.hasNext())
            list.add(new Anime(Global.unEscapeString(in.next())));

        return list;
    }
}
