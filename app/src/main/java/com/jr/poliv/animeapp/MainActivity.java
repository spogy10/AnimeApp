package com.jr.poliv.animeapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.jr.poliv.animeapp.adapter.AnimeViewAdapter;
import com.jr.poliv.animeapp.data.Anime;
import com.jr.poliv.animeapp.taskloader.AnimeTaskLoader;
import com.jr.poliv.animeapp.global.DataMode;
import com.jr.poliv.animeapp.global.Global;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Anime>> {

    RecyclerView recyclerView;
    AnimeViewAdapter adapter;
    ArrayList<Anime> list = new ArrayList<Anime>();
    SharedPreferences preferences;
    public static final int REFRESH = 100000;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); Global.setDefaultYearAndSeason(this);
        preferences = getSharedPreferences(getString(R.string.settings_shared_preferences_file_name), Context.MODE_PRIVATE);
        appLaunch();

        recyclerView = (RecyclerView) findViewById(R.id.rv);
        LinearLayoutManager ln = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(ln);
        adapter = new AnimeViewAdapter(this,list);
        recyclerView.setAdapter(adapter);

        getLoaderManager().initLoader(0, null, this);






    }

    @Override
    public Loader<ArrayList<Anime>> onCreateLoader(int id, Bundle args) {
        return new AnimeTaskLoader(this, Global.getUserDefinedYear(), Global.getUserDefinedSeason());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Anime>> loader, ArrayList<Anime> data) {
        list.clear();
        list.addAll(data);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Anime>> loader) {
        list.clear();
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.online_mode);
        item.setChecked(setOnlineModeOption());
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.refresh:
                refresh();
                return true;

            case R.id.update:
                update();
                return true;

            case R.id.online_mode:
                item.setChecked(!item.isChecked());
                changeDataMode(item.isChecked());
                return true;
            case R.id.settings:
                startActivityForResult(new Intent(this, SettingsActivity.class), 0);
                return true;

            default: return super.onOptionsItemSelected(item);
        }

    }

    private void refresh() {
        getLoaderManager().restartLoader(0, null, this);
        Log.d("Paul", "REFRESHING");
    }

    private void changeDataMode(boolean checked) {
        String mode = checked? DataMode.ONLINEDATA.toString() : DataMode.LOCALDATA.toString();

        preferences.edit().putString(getString(R.string.data_mode), mode).apply();
        DataMode.setMode(DataMode.valueOf(mode));
    }

    private boolean setOnlineModeOption(){
        String mode = preferences.getString(getString(R.string.data_mode), DataMode.getMode().toString());
        DataMode dataMode = DataMode.valueOf(mode);
        switch(dataMode){
            case LOCALDATA:
                return false;
            case ONLINEDATA:
                return true;
            default: return false;
        }

    }

    private void appLaunch(){
        checkDataMode();
        Global.getUserDefinedSeasonFromSharedPreference(this);
        Global.getUserDefinedYearFromSharedPreference(this);
        Global.setDefaultYearAndSeason(this);
    }



    private void checkDataMode(){
        String mode = preferences.getString(getString(R.string.data_mode), DataMode.getMode().toString());
        DataMode.setMode(DataMode.valueOf(mode));
    }

    private void update() {
        if(Global.hasAccessToNet(this)) {
            Log.d("Paul", "Updating");
            new AnimeTaskLoader(this, Global.getUserDefinedYear(), Global.getUserDefinedSeason(), AnimeTaskLoader.UPDATE_MODE).forceLoad();
        }else{
            Toast.makeText(this, getString(R.string.no_network_connection), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(resultCode){
            case REFRESH:
                refresh();
                break;
            default:
        }
    }
}
