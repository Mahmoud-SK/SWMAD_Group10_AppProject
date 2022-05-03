package com.example.swmad_group10_appproject.ViewModels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

import com.example.swmad_group10_appproject.Persistance.Repository;

public class RegisterViewModel extends AndroidViewModel {

    private Repository repository;

    public RegisterViewModel(Application app) {
        super(app);
        repository = Repository.getInstance(app);
    }

    public void registerUser(String email, String password, String username)
    {
        repository.registerUser(email,password,username);
    }
}
