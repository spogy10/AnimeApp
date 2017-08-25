package com.jr.poliv.animeapp;

import android.app.LoaderManager;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.jr.poliv.animeapp.Adapter.AnimeViewAdapter;
import com.jr.poliv.animeapp.Data.Anime;
import com.jr.poliv.animeapp.TaskLoader.AnimeTaskLoader;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Anime>> {

    RecyclerView recyclerView;
    AnimeViewAdapter adapter;
    ArrayList<Anime> list = new ArrayList<Anime>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.rv);
        LinearLayoutManager ln = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(ln);
        adapter = new AnimeViewAdapter(list);
        recyclerView.setAdapter(adapter);

        getLoaderManager().initLoader(0, null, this);




    }

    @Override
    public Loader<ArrayList<Anime>> onCreateLoader(int id, Bundle args) {
        return new AnimeTaskLoader(this);
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
}
