package com.jr.poliv.animeapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jr.poliv.animeapp.data.Anime;
import com.jr.poliv.animeapp.R;
import com.jr.poliv.animeapp.global.DataMode;
import com.jr.poliv.animeapp.global.Global;
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
        ImageView iv, fav;
        FrameLayout fl;
        View itemView;
        boolean viewWeightChanged = false;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            title = (TextView) itemView.findViewById(R.id.tvTitle);
            plot = (TextView) itemView.findViewById(R.id.tvPlot);
            iv = (ImageView) itemView.findViewById(R.id.ivImage);
            fav = (ImageView) itemView.findViewById(R.id.ivFav);
            fl = (FrameLayout) itemView.findViewById(R.id.frameLayout);

        }

        public void onBindView(int position) {
            final Anime anime = list.get(position);
            title.setText(anime.getTitle());
            plot.setText(anime.getPlot());
            if(anime.isFavourited())
                fav.setImageResource(R.drawable.ic_favorite_red_24dp);
            else
                fav.setImageResource(R.drawable.ic_unfavorite_red_24dp);
            //String image = ((anime.getImagePath()!= null) || (anime.getImagePath().equals(""))) ? String.valueOf(R.drawable.anime2)/*add default drawable*/ : anime.getImagePath();
            //Picasso.with(itemView.getContext()).load(Integer.parseInt(image)).fit().centerInside().into(iv);
            if(DataMode.getMode() == DataMode.ONLINEDATA)
                Picasso.with(itemView.getContext()).load(anime.getImageUrl()).fit().centerInside().into(iv);
            else if(DataMode.getMode() == DataMode.LOCALDATA)
                Picasso.with(itemView.getContext()).load(new File(anime.getImagePath())).fit().centerInside().into(iv);
            else
                Picasso.with(itemView.getContext()).load(Integer.parseInt(anime.getImagePath())).fit().centerInside().into(iv);

            if(!viewWeightChanged)
                resetViewWeights();

            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(viewWeightChanged)
                        resetViewWeights();
                    else
                        expandIV();
                }
            });

            fav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(anime.isFavourited())
                        unFavouriteAnime(anime);
                    else
                        favouriteAnime(anime);
                }
            });

            plot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(viewWeightChanged)
                        resetViewWeights();
                    else
                        expandPlot();
                }
            });

        }

        private void unFavouriteAnime(Anime anime) {
            Global.unFavouriteAnAnime(context, anime, Global.getUserDefinedYear(), Global.getUserDefinedSeason());
            anime.setFavourited(false);
            fav.setImageResource(R.drawable.ic_unfavorite_red_24dp);
        }

        private void favouriteAnime(Anime anime) {
            Global.favouriteAnAnime(context, anime);
            anime.setFavourited(true);
            fav.setImageResource(R.drawable.ic_favorite_red_24dp);
        }

        private void expandPlot(){
            setWeight(title, 0.0f);
            setWeight(fl, 0.0f);

            viewWeightChanged = true;
        }

        private void expandIV(){
            setWeight(title, 0.0f);
            setWeight(plot, 0.0f);

            viewWeightChanged = true;
        }

        private void resetViewWeights(){
            setWeight(title, 1.0f);
            setWeight(fl, 4.0f);
            setWeight(plot, 3.0f);
            viewWeightChanged = false;
        }

        private void setWeight(View view, float weight){
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
                    view.getLayoutParams();
            params.weight = weight;
            view.setLayoutParams(params);
        }

    }
}
