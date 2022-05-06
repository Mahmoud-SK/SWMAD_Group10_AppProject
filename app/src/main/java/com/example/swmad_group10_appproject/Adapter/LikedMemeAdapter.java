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
import com.example.swmad_group10_appproject.Models.Meme;
import com.example.swmad_group10_appproject.R;

import java.util.List;


public class LikedMemeAdapter extends RecyclerView.Adapter<LikedMemeAdapter.ViewHolder>{
    List<Meme> memeList;
    ILikedMemeClickedListener listener;

    public LikedMemeAdapter(ILikedMemeClickedListener listener){this.listener = listener;}

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.liked_meme_list_item,parent,false);
        ViewHolder vh =new ViewHolder(v,listener);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(holder.img_likedMemeListItem.getContext()).load(memeList.get(position).getMemeImgURL()).into(holder.img_likedMemeListItem);
        holder.txt_authorName.setText("Get Author name !!!"); //We don't know how to get author name yet
        holder.txt_memeScore.setText(memeList.get(position).getScore());
    }

    @Override
    public int getItemCount() {
        if (memeList!= null){
            return memeList.size();
        }
        return 0;
    }

    public void updateMemes(List<Meme> memes){
        memeList = memes;
        notifyDataSetChanged();
    }

    public interface ILikedMemeClickedListener{
        void onLikedMemeDetailClicked(int index);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //Declare widgets
        public ImageView img_likedMemeListItem;
        public TextView txt_authorName, txt_memeScore;
        public ConstraintLayout likedMemeListItemView;
        ILikedMemeClickedListener listener;

        public ViewHolder(@NonNull View itemView, ILikedMemeClickedListener listener) {
            super(itemView);
            //Assert widgets
            likedMemeListItemView = itemView.findViewById(R.id.LikedMemeListItemView);
            img_likedMemeListItem = itemView.findViewById(R.id.img_likedMemeListItem);
            txt_authorName = itemView.findViewById(R.id.txt_authorName);
            txt_memeScore = itemView.findViewById(R.id.txt_memeScore);
            this.listener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onLikedMemeDetailClicked(getAdapterPosition());
        }
    }
}
