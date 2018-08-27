package com.jr.poliv.animeapp;

public interface ProgressBarInterface {
    public void startLoadAnimeProgress();

    public void endLoadAnimeProgress();

    public void startLocalDataProgress();

    public void endLocalDataProgress();

    public void makeToast(String message);
}
