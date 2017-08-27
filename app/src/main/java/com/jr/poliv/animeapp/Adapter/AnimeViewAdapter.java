package com.jr.poliv.animeapp.Adapter;

import android.content.ContentValues;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jr.poliv.animeapp.Data.Anime;
import com.jr.poliv.animeapp.R;
import com.jr.poliv.animeapp.global.DataMode;
import com.jr.poliv.animeapp.global.Global;
import com.jr.poliv.animeapp.global.Season;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by poliv on 8/23/2017.
 */

public class AnimeViewAdapter extends RecyclerView.Adapter<AnimeViewAdapter.ViewHolder> {

    private ArrayList<Anime> list;
    private Context context;

    public AnimeViewAdapter(Context context, ArrayList<Anime> list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.onBindView(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView title, plot;
        ImageView iv;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tvTitle);
            plot = (TextView) itemView.findViewById(R.id.tvPlot);
            iv = (ImageView) itemView.findViewById(R.id.ivImage);

        }

        public void onBindView(int position) {
            Anime anime = list.get(position);
            title.setText(anime.getTitle());
            plot.setText(anime.getPlot());
            String image = ((anime.getImagePath()!= null) || (anime.getImagePath().equals(""))) ? String.valueOf(R.drawable.anime2)/*add default drawable*/ : anime.getImagePath();
            //Picasso.with(itemView.getContext()).load(Integer.parseInt(image)).fit().centerInside().into(iv);
            if(DataMode.getMode() == DataMode.ONLINEDATA)
                Picasso.with(itemView.getContext()).load(anime.getImageUrl()).fit().centerInside().into(iv);
            else if(DataMode.getMode() == DataMode.LOCALDATA)
                Picasso.with(itemView.getContext()).load(new File(anime.getImagePath())).fit().centerInside().into(iv);
            else
                Picasso.with(itemView.getContext()).load(Integer.parseInt(anime.getImagePath())).fit().centerInside().into(iv);

        }

    }
}
