package com.jr.poliv.animeapp.data;

import com.google.gson.Gson;
import com.jr.poliv.animeapp.R;


/**
 * Created by poliv on 8/22/2017.
 */

public class Anime {
    private String title = "";
    private String plot = "";
    private String imageUrl = "";
    private String imagePath = "";
    private boolean favourited = false;

    public Anime() {
    }

    public Anime(String title, String plot, String imageUrl, String imagePath) {
        this.title = title;
        this.plot = plot;
        this.imageUrl = imageUrl;
        this.imagePath = imagePath;
    }

    public Anime(String title, String plot) {
        this.title = title;
        this.plot = plot;
    }

    public Anime(String title, String plot, String imageUrl) {
        this.title = title;
        this.plot = plot;
        this.imageUrl = imageUrl;
    }

    public Anime(Anime a){
        this(a.getTitle(), a.getPlot(), a.imageUrl, a.getImagePath());
    }

    public Anime(String json){
        this(new Gson().fromJson(json, Anime.class));
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImagePath() {
        return imagePath;
    }

    public boolean isFavourited() {
        return favourited;
    }

    public void setFavourited(boolean favourited) {
        this.favourited = favourited;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public String toString() {
        return title;
    }

    public String toJSON(){
        return new Gson().toJson(this);
    }

    public static Anime[] createTestData(){
        Anime[] test = new Anime[5];
        String text = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibus. Vivamus elementum semper nisi. Aenean vulputate eleifend tellus. Aenean leo ligula, porttitor eu, consequat vitae, eleifend ac, enim. Aliquam lorem ante, dapibus in, viverra quis, feugiat a, tellus. Phasellus viverra nulla ut metus varius laoreet. Quisque rutrum. Aenean imperdiet. Etiam ultricies nisi vel augue. Curabitur ullamcorper ultricies nisi.";
        test[0] = new Anime("Anime 1", text, "url", String.valueOf(R.drawable.anime1));
        test[1] = new Anime("Anime 2", text, "url", String.valueOf(R.drawable.anime2));
        test[2] = new Anime("Anime 3", text, "url", String.valueOf(R.drawable.anime3));
        test[3] = new Anime("Anime 4", text, "url", String.valueOf(R.drawable.anime4));
        test[4] = new Anime("Anime 5", text, "url", String.valueOf(R.drawable.anime5));





        return test;
    }
}
