package com.example.swmad_group10_appproject.ViewModels;

import android.app.Application;
import android.graphics.Bitmap;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.swmad_group10_appproject.Models.Meme;
import com.example.swmad_group10_appproject.Persistance.Repository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

public class ProfileViewModel extends AndroidViewModel  {

    private Repository repository;

    public ProfileViewModel(Application app) {
        super(app);
        repository = Repository.getInstance(app);
    }

    public void uploadMeme (Meme meme, Bitmap image) {
        repository.uploadMeme(meme,image);
    }

    public LiveData<Integer> getCurrentRadius(){return repository.getCurrentRadius();}

    public void updateCurrentRadius(int radius) {
        repository.updateCurrentRadius(radius);
    }

    public void LogoutUser(){repository.logoutUser();}
}
