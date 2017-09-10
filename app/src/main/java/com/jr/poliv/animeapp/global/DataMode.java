package com.jr.poliv.animeapp.global;

/**
 * Created by poliv on 8/25/2017.
 */

public enum DataMode {
    LOCALDATA, ONLINEDATA, TEST;


    private static DataMode mode = DataMode.LOCALDATA;

    public static DataMode getMode() {
        return mode;
    }

    public static void setMode(DataMode mode) {
        DataMode.mode = mode;
    }


}
