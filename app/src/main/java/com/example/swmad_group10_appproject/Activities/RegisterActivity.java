package com.example.swmad_group10_appproject.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.swmad_group10_appproject.Models.User;
import com.example.swmad_group10_appproject.ViewModels.RegisterViewModel;
import com.example.swmad_group10_appproject.R;


// Inspiration taken from: https://www.youtube.com/watch?v=TwHmrZxiPA8

public class RegisterActivity extends AppCompatActivity {

    private EditText txtUsername, txtEmail, txtPassword;
    private Button btnSignUp, btnBack;
    private RegisterViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txtUsername = findViewById(R.id.usernameField);
        txtEmail = findViewById(R.id.emailField);
        txtPassword = findViewById(R.id.passwordField);
        btnSignUp = findViewById(R.id.signUpBtn);
        btnBack = findViewById(R.id.backBtn);

        vm = new ViewModelProvider(this).get(RegisterViewModel.class);


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    // Inspiration to make the Login-system is taken from: https://www.youtube.com/watch?v=TwHmrZxiPA8
    private void validateData() {
        // Getting the supplied data
        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();
        String username = txtUsername.getText().toString();
        int radius = 20;

        // Checking if fields are empty
        if (TextUtils.isEmpty(username)){
            txtUsername.setError("Username is missing!");
            return;
        }

        if (TextUtils.isEmpty(email)){
            txtEmail.setError("Email is missing!");
            return;
        }

        if (TextUtils.isEmpty(password)){
            txtPassword.setError("Password is missing!");
            return;
        }

        User user = new User(email,password,username,radius);

        // Authenticating the User
        try {
            vm.registerUser(user);
            Intent mainIntent = new Intent(RegisterActivity.this, MemeActivity.class);
            startActivity(mainIntent);

        } catch (Exception e) {
            Log.d("RegisterActivity", "Error registering an account!: " + e);
        }
    }
}