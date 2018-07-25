package com.jr.poliv.animeapp.backgroundtask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.jr.poliv.animeapp.ProgressBarInterface;
import com.jr.poliv.animeapp.data.Anime;
import com.jr.poliv.animeapp.global.Global;
import com.jr.poliv.animeapp.global.Season;

import org.apache.commons.io.FileUtils;


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


/**
 * Created by poliv on 8/25/2017.
 */

public class CreateLocalData extends AsyncTask<Void, Void, Void> {

    private Context context;
    private String directoryString;
    public static final String FILE_NOT_FOUND = "FILENOTFOUND";
    private ArrayList<Anime> animeList;
    private File directory;
    private int year;
    private Season season;
    private LinkedList<Anime> favouritedAnime = new LinkedList<>();
    private ProgressBarInterface pbInterface;

    public CreateLocalData(Context context, ArrayList<Anime> animeList, int year, Season season, ProgressBarInterface pbInterface){
        this.context = context;
        this.animeList = animeList;
        directoryString = Global.getSeasonFolder(context, year, season);
        this.year = year;
        this.season = season;
        this.pbInterface = pbInterface;
    }

    private void downloadAllImages(){
        createDirectory();
        for(Anime anime : animeList) {
            anime.setImagePath(downloadFile(anime.getImageUrl()));
            if(isCancelled())
                return;
        }
    }

    private void createJSON(){

        String s = "";
        for(Anime anime:animeList){
            if( (anime.getImagePath().equals("")) || (anime.getImagePath() == null) || (anime.getImagePath().equals(FILE_NOT_FOUND)) ) {
                Log.d("Paul", "Anime "+anime.getTitle()+" missing image path");
                return;
            }
            s+= Global.escapeString(anime.toJSON()) + Global.DELIMITER;
            if(anime.isFavourited())
                favouritedAnime.add(anime);
        }

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
            cancel(true);
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
    protected void onPreExecute() {
        super.onPreExecute();
        pbInterface.startLocalDataProgress();
    }

    @Override
    protected Void doInBackground(Void... params) {
        downloadAllImages();

        if(isCancelled())
            return null;

        createJSON();

        if(successTest())
            onSuccess();
        else
            onFailure();

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        pbInterface.endLocalDataProgress();

    }

    @Override
    protected void onCancelled(Void aVoid) {
        super.onCancelled(aVoid);
        onFailure();
        pbInterface.endLocalDataProgress();
    }

    private void createDirectory(){
            File imageDirectory = new File(directoryString);
            if(!imageDirectory.exists()){
                boolean temp = imageDirectory.mkdirs();
                Log.d("Paul", "Was the directoryString created: "+temp);
            }else{
                Log.d("Paul", "Images directoryString exists ");

                try {
                    File tempFolder = new File(imageDirectory.getAbsolutePath()+"-backup");
                    FileUtils.deleteDirectory(tempFolder);

                    if(imageDirectory.renameTo(tempFolder)){
                        Log.d("Paul", "Successful rename");
                    }else{
                        Log.d("Paul", "Unsuccessful Rename");
                        FileUtils.deleteDirectory(imageDirectory);
                        Log.d("Paul", "Images directoryString deleted ");
                    }
                } catch (IOException e) {
                    Log.d("Paul", "Directory not deleted "+e.toString());
                    e.printStackTrace();
                }
                imageDirectory = new File(directoryString);
                boolean temp1 = imageDirectory.mkdirs();
                Log.d("Paul", "Was the directory created: "+temp1);
            }
            directory = imageDirectory;
        }

        private void onSuccess(){
            try {
                FileUtils.deleteDirectory(new File(directoryString+"-backup"));
                Log.d("Paul", "SUCCESS, temp folder deleted");
                Global.unFavouriteEntireSeason(context, year, season);
                if(favouritedAnime.size() > 0){
                    Log.d("Paul", String.valueOf(favouritedAnime.size()) + " were favourited");
                    int success = Global.favouriteMultipleAnime(context, favouritedAnime, year, season);
                    Log.d("Paul", String.valueOf(success) + " were added to database");
                }else
                    Log.d("Paul", "None were favourtied");
            } catch (IOException e) {
                Log.d("Paul", "Temporary folder not deleted "+e.toString());
                e.printStackTrace();
            }
        }

        private void onFailure(){
            File tempFolder = new File(directoryString+"-backup");
            if(tempFolder.exists()){
                try {
                    File decent = new File(directoryString);
                    FileUtils.deleteDirectory(decent);
                    if(tempFolder.renameTo(decent))
                        Log.d("Paul", "Failed to create local data, backup folder successfully utilized");
                    else
                        Log.d("Paul", "Failed to create local data, backup folder unsuccessfully utilized ");

                } catch (IOException e) {
                    Log.d("Paul", "Failed to create local data, backup folder unsuccessfully utilized "+e.toString());
                    e.printStackTrace();
                }
            }else{
                Log.d("Paul", "Failed to create local data, backup folder doesn't exist");
            }
        }

        private boolean successTest(){
            File decent = new File(directoryString);

            if(decent.exists()){
                if(new File(Global.getJSONFilePath(directoryString)).exists()){
                    if(animeList.size() <= 0){
                        Log.d("Paul", "Error with successTest, Anime array list empty");
                        return false;
                    }
                    Anime anime = animeList.get(animeList.size() - 1);
                    if( !( (anime.getImagePath().equals("")) || (anime.getImagePath() == null) || (anime.getImagePath().equals(FILE_NOT_FOUND)) ) ) {
                            return true;
                    }else{
                        Log.d("Paul", "Last Anime missing an image file path");
                    }
                }else{
                    Log.d("Paul", "JSON file does not exist");
                }

            }else{
                Log.d("Paul", "Folder does not exist");
                return false;
            }
            Log.d("Paul", "Error with successTest");
            return false;
        }

    /*private void previousDirectoryMethod(){
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
    }*/



}
