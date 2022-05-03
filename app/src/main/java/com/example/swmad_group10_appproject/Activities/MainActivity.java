package com.example.swmad_group10_appproject.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.swmad_group10_appproject.R;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    Button btn_testProfile, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int x = 5;

        btn_testProfile = findViewById(R.id.btn_testProfile);
        btnLogout = findViewById(R.id.logoutBtn);
        btn_testProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });
    }

    private void logOut() {
        FirebaseAuth.getInstance().signOut();
        Intent logInIntent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(logInIntent);
        finish();
    }
}