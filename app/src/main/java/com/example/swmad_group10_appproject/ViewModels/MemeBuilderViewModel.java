package com.example.swmad_group10_appproject.ViewModels;

import android.app.Application;
import android.graphics.Bitmap;

import androidx.lifecycle.AndroidViewModel;

import com.example.swmad_group10_appproject.Models.Meme;
import com.example.swmad_group10_appproject.Persistance.Repository;

public class MemeBuilderViewModel extends AndroidViewModel {
    private Repository repository;

    public MemeBuilderViewModel(Application app) {
        super(app);
        repository = Repository.getInstance(app);
    }

    public void uploadMeme (Meme meme, Bitmap image) {
        repository.uploadMeme(meme,image);
    }
}
