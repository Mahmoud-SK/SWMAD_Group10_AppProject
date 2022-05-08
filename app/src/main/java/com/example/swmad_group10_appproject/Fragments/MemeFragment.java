package com.example.swmad_group10_appproject.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.swmad_group10_appproject.R;

// Most of this class is inspired by the fragments made in the lecture videos about fragments
public class MemeFragment extends Fragment {

    private static final String TAG = "MemeFragment";
    private TextView txtTopText;
    private TextView txtBottomText;
    private ImageView imgMemeImage;
    private String topText;
    private String bottomText;
    private String imgLink;

    public MemeFragment(String topText, String bottomText, String imgLink) {
        this.topText = topText;
        this.bottomText = bottomText;
        this.imgLink = imgLink;
    }

    public static MemeFragment newInstance(String topText, String bottomText, String imgLink) {
        return new MemeFragment(topText, bottomText, imgLink);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.meme_fragment, container, false);
        txtTopText = v.findViewById(R.id.txtMemeTopText);
        txtBottomText = v.findViewById(R.id.txtMemeBottomText);
        imgMemeImage = v.findViewById(R.id.imgMemeImage);
        txtTopText.setText(topText);
        txtBottomText.setText(bottomText);
        Glide.with(imgMemeImage.getContext()).load(imgLink).into(imgMemeImage);
        return v;
    }

    @Override
    public void onPause(){
        Log.d(TAG, "pause");
        super.onPause();
    }

    @Override
    public void onStop(){
        Log.d(TAG, "stop");
        super.onStop();
    }

    @Override
    public void onDestroyView(){
        Log.d(TAG, "destroy view");
        super.onDestroyView();
    }

    @Override
    public void onDestroy(){
        Log.d(TAG, "destroy");
        super.onDestroy();
    }

    @Override
    public void onDetach(){
        Log.d(TAG, "detach");
        super.onDetach();
    }

}