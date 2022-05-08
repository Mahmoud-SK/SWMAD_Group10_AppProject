package com.example.swmad_group10_appproject.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.swmad_group10_appproject.Fragments.MemeFragment;
import com.example.swmad_group10_appproject.Models.Meme;
import com.example.swmad_group10_appproject.R;
import com.example.swmad_group10_appproject.ViewModels.MemeViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MemeActivity extends AppCompatActivity {

    private static final String TAG = "MemeActivity";

    private Button btnRanking, btnProfile;

    private GestureDetector gesture;
    private MemeViewModel memeVM;
    //private List<Meme> memes;
    private int memeIndex;
    private boolean firstMeme;
    private int animIn;
    private int animOut;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private Location lastLocation;
    public static final int PERMISSION_REQUEST_LOCATION = 601;

    private LocationManager locationManager;
    //private static final int PERMISSION_REQUEST_CODE;

    private LiveData<List<Meme>> newMemes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meme);

        memeIndex = 0;
        firstMeme = true;
        //memes = new ArrayList<Meme>();
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

        checkPermissions();
        setupLocationFramework();

        animIn = R.anim.no_animation;
        animOut = R.anim.no_animation;
        //getMemes();
        newMemes = memeVM.getMemeList();
        newMemes.observe(this, new Observer<List<Meme>>() {
            @Override
            public void onChanged(List<Meme> memes) {
                Log.d(TAG, "onChanged: test");
                if (!memes.isEmpty() && firstMeme)
                    Log.d(TAG, "onChanged: we go to next meme");
                    firstMeme = false;
                    nextMeme();
            }
        });
        //memeVM.getMemesWithinRadius(2);
        getMemesBasedOnLocation();
        setupSwipeDetection();
    }

    //inspired by the lecture video about fusedlocationprovider
    public void setupLocationFramework(){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        /*locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult == null){
                    Log.d(TAG, "onLocationResult: empty result");
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    Log.d(TAG, "onLocationResult: " + location.getLatitude() + " , " + location.getLongitude());
                    lastLocation = location;
                    memeVM.getMemesWithinRadius(2, location.getLatitude(), location.getLongitude());
                }
            }
            @Override
            public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
                Log.d(TAG, "onLocationAvailability: " + locationAvailability.isLocationAvailable());
            }
        };*/
    }

    //inspired by the lecture video about fusedlocationprovider
    @SuppressLint("MissingPermission")
    private void getMemesBasedOnLocation(){
        Log.d(TAG, "getMemesBasedOnLocation: test");
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null){
                    Log.d(TAG, " We got a location ");
                    memeVM.getMemesWithinRadius(2, location.getLatitude(), location.getLongitude());
                }
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "getting location onFailure: ");
            }
        });
    }

    private void checkPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_LOCATION);
        }
    }

    

    // How to detect motion events and react to them
    // https://stackoverflow.com/questions/11421368/android-fragment-oncreateview-with-gestures
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
                            //Log.d(TAG, "onFling: left");
                            /*memes.get(memeIndex).updateScore(-1);
                            memeVM.UpdateMeme(memes.get(memeIndex));*/
                            if (!newMemes.getValue().isEmpty()) {
                                newMemes.getValue().get(memeIndex).updateScore(-1);
                                Log.d(TAG, "onFling: left " + newMemes.getValue().get(memeIndex).getKey());
                                Log.d(TAG, "onFling: left " + newMemes.getValue().get(memeIndex).getScore());
                                memeVM.UpdateMeme(newMemes.getValue().get(memeIndex));
                                animIn = R.anim.fade_in;
                                animOut = R.anim.slide_out_left;
                                nextMeme();
                            }
                        }
                        else if (event2.getX() - event1.getX() > minSwipeDistance){
                            //Log.d(TAG, "onFling: right");
                            /*memes.get(memeIndex).updateScore(1);
                            memeVM.UpdateMeme(memes.get(memeIndex));*/
                            if (!newMemes.getValue().isEmpty()) {
                                newMemes.getValue().get(memeIndex).updateScore(1);
                                Log.d(TAG, "onFling: right " + newMemes.getValue().get(memeIndex).getKey());
                                Log.d(TAG, "onFling: right " + newMemes.getValue().get(memeIndex).getScore());
                                memeVM.UpdateMeme(newMemes.getValue().get(memeIndex));
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
        if (memeIndex >= newMemes.getValue().size()-1){
            memeIndex = 0;
            firstMeme = true;
            getMemesBasedOnLocation();
            //memeVM.getMemesWithinRadius(2);
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

    //this is also a part of the swipe detection
    //https://stackoverflow.com/questions/13927305/how-to-set-onfling-event-of-gesture-on-scrollview-in-android
    @Override
    public boolean dispatchTouchEvent(MotionEvent event){
        gesture.onTouchEvent(event);
        return super.dispatchTouchEvent(event);
    }
}