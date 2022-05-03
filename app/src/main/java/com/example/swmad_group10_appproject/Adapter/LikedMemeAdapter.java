package com.example.swmad_group10_appproject.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.swmad_group10_appproject.R;


public class LikedMemeAdapter extends RecyclerView.Adapter<LikedMemeAdapter.ViewHolder>{

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.liked_meme_list_item,parent,false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //Glide.with(holder.img_likedMemeListItem.getContext()).load(ImageView.)
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //Declare widgets
        public ImageView img_likedMemeListItem;
        public TextView txt_authorName, txt_MemeText;
        public ConstraintLayout likedMemeListItemView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //Assert widgets
            likedMemeListItemView = itemView.findViewById(R.id.LikedMemeListItemView);
            img_likedMemeListItem = itemView.findViewById(R.id.img_likedMemeListItem);
            txt_authorName = itemView.findViewById(R.id.txt_authorName);
            txt_MemeText = itemView.findViewById(R.id.txt_memeText);
        }

        @Override
        public void onClick(View view) {

        }
    }
}
