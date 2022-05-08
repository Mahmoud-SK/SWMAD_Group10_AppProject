package com.example.swmad_group10_appproject.ViewModels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.swmad_group10_appproject.Models.Meme;
import com.example.swmad_group10_appproject.Persistance.Repository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MemeViewModel extends AndroidViewModel {

    private Repository repository;
    private MutableLiveData<List<Meme>> memes;

    public MemeViewModel(Application app) {
        super(app);
        repository = Repository.getInstance(app);
        memes = new MutableLiveData<List<Meme>>();
        memes.setValue(new ArrayList<Meme>());
    }

    public LiveData<List<Meme>> getMemeList(){
        return memes;
    }

    public LiveData<Integer> getCurrentRadius(){
        return repository.getCurrentRadius();
    }

    public void getMemesWithinRadius(int radius, double latitude, double longitude){
        repository.getMemesWithinRadius(memes, radius, latitude, longitude);
    }

    public Task<QuerySnapshot> getMemes(){
        return repository.getMemes();
    }

    public void UpdateMeme(Meme meme){ repository.updateMeme(meme);}

}
