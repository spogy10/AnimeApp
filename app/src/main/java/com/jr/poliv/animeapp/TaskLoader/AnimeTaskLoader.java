package com.jr.poliv.animeapp.TaskLoader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.jr.poliv.animeapp.BackgroundTask.CreateLocalData;
import com.jr.poliv.animeapp.Data.Anime;
import com.jr.poliv.animeapp.R;
import com.jr.poliv.animeapp.global.DataMode;
import com.jr.poliv.animeapp.global.Season;


import org.apache.commons.io.IOUtils;
import org.apache.commons.text.StringEscapeUtils;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by poliv on 8/23/2017.
 */

public class AnimeTaskLoader extends AsyncTaskLoader<ArrayList<Anime>> {

    private String myAnimeListUrl = getContext().getString(R.string.baseUrl);

    public AnimeTaskLoader(Context context) {
        super(context);
    }

    @Override
    public ArrayList<Anime> loadInBackground() {


        //DataMode.setMode(DataMode.TEST); new ArrayList<Anime>(Arrays.asList(Anime.createTestData()));
        try {
            return new ArrayList<Anime>(parseWebCode(webCode()));
        } catch (IOException e) {
            Log.d("Paul", "IOException Called from AnimeTaskLoader by webCode method "+e.toString());
            e.printStackTrace();
        }
        return new ArrayList<Anime>();
    }

    @Override
    public void deliverResult(ArrayList<Anime> data) {
        //cache the data before delivering it
        new CreateLocalData(getContext(), data, 2017, Season.Summer).execute();
        super.deliverResult(data);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        //check if there is cache data available
        forceLoad();
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
}