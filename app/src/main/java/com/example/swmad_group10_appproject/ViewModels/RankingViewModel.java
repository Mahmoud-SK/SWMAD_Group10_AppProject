package com.example.swmad_group10_appproject.ViewModels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

import com.example.swmad_group10_appproject.Persistance.Repository;

public class RankingViewModel extends AndroidViewModel {

    private Repository repository;

    public RankingViewModel(Application app) {
        super(app);
        repository = Repository.getInstance(app);
    }


}
