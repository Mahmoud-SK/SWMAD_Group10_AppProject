package com.example.swmad_group10_appproject.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.swmad_group10_appproject.Fragments.MemeFragment;
import com.example.swmad_group10_appproject.Models.Meme;
import com.example.swmad_group10_appproject.R;
import com.example.swmad_group10_appproject.ViewModels.MemeViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

public class MemeActivity extends AppCompatActivity {

    private static final String TAG = "MemeActivity";

    private Button btnRanking, btnProfile;
    private GestureDetector gesture;
    private MemeViewModel memeVM;
    private int memeIndex, animIn, animOut, radius;
    private boolean firstMeme;
    private FusedLocationProviderClient fusedLocationClient;
    public static final int PERMISSIONS_REQUEST_LOCATION = 601;
    private LiveData<List<Meme>> newMemes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meme);

        memeIndex = 0;
        firstMeme = true;
        radius = 20;

        memeVM = new ViewModelProvider(this).get(MemeViewModel.class);
        btnProfile = findViewById(R.id.btnMemeGoToProfile);
        btnRanking = findViewById(R.id.btnMemeGoToRanking);

        btnRanking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchRankingActivity();
            }
        });
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchProfileActivity();
            }
        });

        memeVM.getCurrentRadius().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                radius = integer;
            }
        });

        checkPermissions();
        setupLocationFramework();

        animIn = R.anim.no_animation;
        animOut = R.anim.no_animation;

        newMemes = memeVM.getMemeList();
        newMemes.observe(this, new Observer<List<Meme>>() {
            @Override
            public void onChanged(List<Meme> memes) {
                Log.d(TAG, "onChanged: newMemes");
                if (!memes.isEmpty() && firstMeme)
                    Log.d(TAG, "onChanged: we go to next meme first time after getting new memes");
                    firstMeme = false;
                    nextMeme();
            }
        });

        setupSwipeDetection();
    }

    // Inspiration from: L9 | Sensors, Location and Maps [About Fused Location Service]
    public void setupLocationFramework(){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    // Inspiration from: L9 | Sensors, Location and Maps [About Fused Location Service]
    @SuppressLint("MissingPermission")
    private void getMemesBasedOnLocation(){
        Log.d(TAG, "getMemesBasedOnLocation: test");
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null){
                    Log.d(TAG, " We got a location " + location.getLatitude() + " ; " + location.getLongitude());
                    Log.d(TAG, "current radius: " + radius);
                    memeVM.getMemesWithinRadius(radius, location.getLatitude(), location.getLongitude());
                }
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "getting location onFailure: ");
            }
        });
    }

    // Inspiration from: L9 | Sensors, Location and Maps [About Fused Location Service]
    private void checkPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);
        }
    }

    

    // How to detect motion events and react to them
    // Link: https://stackoverflow.com/questions/11421368/android-fragment-oncreateview-with-gestures
    public void setupSwipeDetection(){
        gesture = new GestureDetector(this,
                new GestureDetector.SimpleOnGestureListener(){

                    @Override
                    public boolean onDown(MotionEvent event){
                        return true;
                    }

                    @Override
                    public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY){
                        Log.d(TAG, "onFling: ");
                        final int minSwipeDistance = 100;
                        if (event1.getX() - event2.getX() > minSwipeDistance){
                            if (!newMemes.getValue().isEmpty()) {
                                newMemes.getValue().get(memeIndex-1).updateScore(-1);
                                Log.d(TAG, "onFling: left " + newMemes.getValue().get(memeIndex-1).getKey());
                                Log.d(TAG, "onFling: left " + newMemes.getValue().get(memeIndex-1).getScore());
                                memeVM.UpdateMeme(newMemes.getValue().get(memeIndex-1));
                                animIn = R.anim.fade_in;
                                animOut = R.anim.slide_out_left;
                                nextMeme();
                            }
                        }
                        else if (event2.getX() - event1.getX() > minSwipeDistance){
                            if (!newMemes.getValue().isEmpty()) {
                                newMemes.getValue().get(memeIndex-1).updateScore(1);
                                Log.d(TAG, "onFling: right " + newMemes.getValue().get(memeIndex-1).getKey());
                                Log.d(TAG, "onFling: right " + newMemes.getValue().get(memeIndex-1).getScore());
                                memeVM.UpdateMeme(newMemes.getValue().get(memeIndex-1));
                                animIn = R.anim.fade_in;
                                animOut = R.anim.slide_out_right;
                                nextMeme();
                            }
                        }
                        return super.onFling(event1, event2, velocityX, velocityY);
                    }
                });
    }

    public void nextMeme(){
        if (memeIndex > newMemes.getValue().size()-1){
            memeIndex = 0;
            firstMeme = true;
            getMemesBasedOnLocation();
            Log.d(TAG, "nextMeme: out of memes");
        }
        else {
            Meme nextMeme = newMemes.getValue().get(memeIndex);

            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(animIn, animOut)
                    .replace(R.id.container1, MemeFragment.newInstance(nextMeme.getTopText(),
                            nextMeme.getBottomText(), nextMeme.getMemeImgURL()))
                    .commit();
            memeIndex++;
        }
    }

    private void launchProfileActivity(){
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    private void launchRankingActivity(){
        Intent intent = new Intent(this, RankingActivity.class);
        startActivity(intent);
    }

    // This is also a part of the swipe detection
    // Link: https://stackoverflow.com/questions/13927305/how-to-set-onfling-event-of-gesture-on-scrollview-in-android
    @Override
    public boolean dispatchTouchEvent(MotionEvent event){
        gesture.onTouchEvent(event);
        return super.dispatchTouchEvent(event);
    }

    // Inspiration from: L9 | Sensors, Location and Maps [About Fused Location Service]
    // Using Fused Location Provider (Corona tracker) : CoronaTracker 1.4.zip
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getMemesBasedOnLocation();
                } else {
                    // permission denied
                    //in this case we just close the app
                    Toast.makeText(this, "You need to enable permission for Location to use the app", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
    }
}