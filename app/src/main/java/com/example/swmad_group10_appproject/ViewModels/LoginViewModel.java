package com.example.swmad_group10_appproject.ViewModels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

import com.example.swmad_group10_appproject.Persistance.Repository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

public class LoginViewModel extends AndroidViewModel {

    private Repository repository;

    public LoginViewModel(Application app) {
        super(app);
        repository = Repository.getInstance(app);
    }

    public Task<AuthResult> loginUser(String email, String password)
    {
        return repository.loginUser(email,password);
    }

    public FirebaseUser getCurrentUser() {
        return repository.getCurrentUser();
    }
    public void startService() {
        repository.startForegroundService(getApplication());
    }
}
