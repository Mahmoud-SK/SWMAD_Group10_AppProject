package com.example.swmad_group10_appproject.ViewModels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

import com.example.swmad_group10_appproject.Persistance.Repository;

public class LoginViewModel extends AndroidViewModel {

    private Repository repository;

    public LoginViewModel(Application app) {
        super(app);
        repository = Repository.getInstance(app);
    }

    public void loginUser(String email, String password)
    {
        repository.loginUser(email,password);
    }
}
