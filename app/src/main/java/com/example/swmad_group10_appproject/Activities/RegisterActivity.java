package com.example.swmad_group10_appproject.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.example.swmad_group10_appproject.R;

// Inspiration taken from: https://www.youtube.com/watch?v=TwHmrZxiPA8

public class RegisterActivity extends AppCompatActivity {

    EditText txtUsername, txtEmail, txtPassword;
    Button btnSignUp, btnBack;
    FirebaseAuth fireBaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txtUsername = findViewById(R.id.usernameField);
        txtEmail = findViewById(R.id.emailField);
        txtPassword = findViewById(R.id.passwordField);
        btnSignUp = findViewById(R.id.signUpBtn);
        btnBack = findViewById(R.id.backBtn);

        fireBaseAuth = FirebaseAuth.getInstance();

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

    private void validateData() {
        // Getting the supplied data
        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();
        String username = txtUsername.getText().toString();

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

        // Registering the user in firebase
        fireBaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Account has been created!", Toast.LENGTH_SHORT).show();
                    Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(mainIntent);
                } else {
                    Toast.makeText(RegisterActivity.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("RegisterActivity", "Error! " + task.getException().getMessage());
                }
            }
        });
    }
}