package com.jr.poliv.animeapp.BackgroundTask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.jr.poliv.animeapp.Data.Anime;
import com.jr.poliv.animeapp.global.Global;
import com.jr.poliv.animeapp.global.Season;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Created by poliv on 8/25/2017.
 */

public class CreateLocalData extends AsyncTask<Void, Void, Void> {

    Context context;
    String directoryString;
    public static final String FILE_NOT_FOUND = "FILENOTFOUND";
    ArrayList<Anime> animeList;
    File directory;

    public CreateLocalData(Context context, ArrayList<Anime> animeList, int year, Season season){
        this.context = context;
        this.animeList = animeList;
        directoryString = context.getFilesDir() + File.separator + year + File.separator + season.toString();
    }

    private void downloadAllImages(){
        createDirectory();
        for(Anime anime : animeList) {
            anime.setImagePath(downloadFile(anime.getImageUrl()));
        }
    }

    private void createJSON(){

        String s = "";
        for(Anime anime:animeList)
                s+= Global.escapeString(anime.toJSON()) + Global.DELIMITER;

        try {
            FileWriter fileWriter = new FileWriter(new File(Global.getJSONFilePath(directoryString)));

            fileWriter.write(s);
            fileWriter.close();
        } catch (IOException e) {
            Log.d("Paul", "File not written "+e.toString());
            e.printStackTrace();
        }

    }






    private String downloadFile(@NonNull String imageURL){
        String fileLocation = FILE_NOT_FOUND;
        InputStream is = null;
        try {
            URL url = new URL(imageURL);
            HttpURLConnection in = (HttpURLConnection) url.openConnection();
            in.setDoInput(true);
            in.connect();
            is = in.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            fileLocation = createFileFromBitmap(bitmap);

        } catch (IOException e) {
            e.printStackTrace();
            Log.d("Paul", "Error downloading image "+e.toString());
        }finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return fileLocation;
    }

    private String createFileFromBitmap(Bitmap bitmap) throws FileNotFoundException {


        File image = new File(directory, directory.listFiles().length+".jpeg");

        FileOutputStream fos = new FileOutputStream(image);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

        if(image.exists())
            Log.d("Paul", "File created: "+image.getName());

        return image.getAbsolutePath();
    }

        @Override
        protected Void doInBackground(Void... params) {
            downloadAllImages();
            createJSON();
            return null;
        }

        private void createDirectory(){
            File imageDirectory = new File(directoryString);
            if(!imageDirectory.exists()){
                Log.d("Paul", "Was the directoryString created: "+imageDirectory.mkdirs());
            }else{
                Log.d("Paul", "Images directoryString exists ");

                try {
                    FileUtils.deleteDirectory(imageDirectory);
                    Log.d("Paul", "Images directoryString deleted ");
                } catch (IOException e) {
                    Log.d("Paul", "Directory not deleted "+e.toString());
                    e.printStackTrace();
                }

                Log.d("Paul", "Was the directoryString created: "+imageDirectory.mkdirs());
            }
            directory = imageDirectory;
        }


}
