package com.jr.poliv.animeapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.jr.poliv.animeapp.adapter.FavAnimeViewAdapter;
import com.jr.poliv.animeapp.data.Anime;
import com.jr.poliv.animeapp.global.DataMode;
import com.jr.poliv.animeapp.global.Global;
import com.jr.poliv.animeapp.taskloader.FavAnimeTaskLoader;

import java.util.ArrayList;

public class FavAnimeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Anime>> {

    RecyclerView recyclerView;
    FavAnimeViewAdapter adapter;
    ArrayList<Anime> list = new ArrayList<Anime>();
    SharedPreferences preferences;
    ProgressBar progressBar;
    public static final int REFRESH = MainActivity.REFRESH;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Fav");
        preferences = getSharedPreferences(getString(R.string.settings_shared_preferences_file_name), Context.MODE_PRIVATE);

        recyclerView = (RecyclerView) findViewById(R.id.rv);
        LinearLayoutManager ln = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(ln);
        adapter = new FavAnimeViewAdapter(list);
        recyclerView.setAdapter(adapter);

        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();

                AlertDialog.Builder builder = new AlertDialog.Builder(FavAnimeActivity.this); //alert for confirm to delete
                builder.setMessage("Are you sure to unfavourite?");    //set message

                builder.setPositiveButton(R.string.unfavourite, new DialogInterface.OnClickListener() { //when click on DELETE
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adapter.notifyItemRemoved(position);    //item removed from recylcerview
                        Global.unFavouriteAnAnime(FavAnimeActivity.this, list.get(position));
                        list.remove(position);  //then remove item

                        return;
                    }
                }).setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {  //not removing items if cancel is done
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adapter.notifyItemRemoved(position + 1);    //notifies the RecyclerView Adapter that data in adapter has been removed at a particular position.
                        adapter.notifyItemRangeChanged(position, adapter.getItemCount());   //notifies the RecyclerView Adapter that positions of element in adapter has been changed from position(removed element index to end of list), please update it.
                        return;
                    }
                }).show();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<ArrayList<Anime>> onCreateLoader(int id, Bundle args) {
        startLoadAnimeProgress();
        return new FavAnimeTaskLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Anime>> loader, ArrayList<Anime> data) {
        endLoadAnimeProgress();
        list.clear();
        list.addAll(data);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Anime>> loader) {
        list.clear();
    }

    private void scrollDown(){
        if(list.size() > 0) {
            int scroll = Global.getScrollAmount();
            int current = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
            int bottom = list.size() - 1;

            scroll = ((bottom - current) <= scroll) ? bottom : (current + scroll);

            recyclerView.smoothScrollToPosition(scroll);
        }
    }

    private void scrollUp(){
        if(list.size() > 0) {
            int scroll = Global.getScrollAmount();
            int current = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
            int top = 0;

            scroll = ((current - top) <= scroll) ? top : (current - scroll);

            recyclerView.smoothScrollToPosition(scroll);
        }
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
        MenuItem up = menu.findItem(R.id.up);
        up.setActionView(R.layout.toolbar_button_up);
        MenuItem down = menu.findItem(R.id.down);
        down.setActionView(R.layout.toolbar_button_down);

        if(up != null){
            AppCompatButton button = (AppCompatButton) up.getActionView();
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    scrollUp();
                }
            });
        }

        if(down != null){
            AppCompatButton button = (AppCompatButton) down.getActionView();
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    scrollDown();
                }
            });
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.refresh:
                refresh();
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

    private void showProgressBar(){
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar(){
        progressBar.setVisibility(View.GONE);
    }

    private void setProgressBarColour(int colour){
        progressBar.getIndeterminateDrawable().setColorFilter(colour, PorterDuff.Mode.SRC_IN);

    }

    private void startLoadAnimeProgress(){
        Log.d("Paul", "Start Load Fav Anime Progress");
        setProgressBarColour(Color.BLUE);
        showProgressBar();
    }

    private void endLoadAnimeProgress(){
        Log.d("Paul", "End Load Fav Anime Progress");
        hideProgressBar();
    }

}
