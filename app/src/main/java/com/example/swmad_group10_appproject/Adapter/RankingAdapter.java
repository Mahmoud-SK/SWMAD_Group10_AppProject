package com.example.swmad_group10_appproject.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.swmad_group10_appproject.Models.Meme;
import com.example.swmad_group10_appproject.R;

import java.util.ArrayList;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.ViewHolder>{

    Context context;
    ArrayList<Meme> memeArraylist;

    public RankingAdapter(Context context, ArrayList<Meme> memeArraylist) {
        this.context = context;
        this.memeArraylist = memeArraylist;
    }

    @NonNull
    @Override
    public RankingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.meme_rv,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RankingAdapter.ViewHolder holder, int position) {
        Meme meme = memeArraylist.get(position);

        holder.tvscore.setText(String.valueOf(meme.getScore()));
        Glide.with(holder.img_rv.getContext()).load(memeArraylist.get(position).getMemeImgURL()).into(holder.img_rv);

    }

    public void notifyDataSetChanges() {
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return memeArraylist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //Declare widgets
        public ImageView img_rv;
        public TextView tvscore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //Assert widgets
            img_rv = itemView.findViewById(R.id.img_rv);
            tvscore = itemView.findViewById(R.id.tvscore);

        }

        @Override
        public void onClick(View view) {

        }
    }
}