package com.example.swmad_group10_appproject.ViewModels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

import com.example.swmad_group10_appproject.Models.Meme;
import com.example.swmad_group10_appproject.Persistance.Repository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QuerySnapshot;

public class MemeViewModel extends AndroidViewModel {

    private Repository repository;

    public MemeViewModel(Application app) {
        super(app);
        repository = Repository.getInstance(app);
    }

    public Task<QuerySnapshot> getMemes(){
        return repository.getMemes();
    }

    public void UpdateMeme(Meme meme){ repository.updateMeme(meme);}

}
