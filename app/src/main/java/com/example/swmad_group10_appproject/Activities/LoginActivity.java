package com.example.swmad_group10_appproject.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.swmad_group10_appproject.R;
import com.example.swmad_group10_appproject.ViewModels.LoginViewModel;
import com.example.swmad_group10_appproject.ViewModels.RegisterViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

// Inspiration taken from: https://www.youtube.com/watch?v=TwHmrZxiPA8

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    EditText txtEmail, txtPassword;
    Button btnRegister, btnLogin;
    LoginViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnRegister = findViewById(R.id.registerBtn);
        btnLogin = findViewById(R.id.loginBtn);
        txtEmail = findViewById(R.id.emailField);
        txtPassword = findViewById(R.id.passwordField);

        vm = new ViewModelProvider(this).get(LoginViewModel.class);
        vm.startService();

        // Checking if already logged in
        if (vm.getCurrentUser() != null) {
            Intent mainIntent = new Intent(getApplicationContext(), MemeActivity.class);
            Log.d(TAG, "getCurrentUserID: " + vm.getCurrentUser());
            startActivity(mainIntent);
            finish();
        }
        
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginIn();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GotoRegister();
            }
        });
    }

    private void LoginIn() {
        // Getting the supplied data
        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();

        // Checking if fields are empty
        if (TextUtils.isEmpty(email)){
            txtEmail.setError("Email is missing!");
            return;
        }

        if (TextUtils.isEmpty(password)){
            txtPassword.setError("Password is missing!");
            return;
        }

        // Authenticating the User
        vm.loginUser(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(),"Successfully logged in!", Toast.LENGTH_SHORT).show();
                    Intent memeIntent = new Intent(getApplicationContext(),MemeActivity.class);
                    startActivity(memeIntent);
                } else {
                    Toast.makeText(getApplicationContext(),"Invalid Email/Password!", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error logging in, email/password invalid");
                }
            }
        });
    }

    private void GotoRegister() {
        Intent registerIntent = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(registerIntent);
    }
}