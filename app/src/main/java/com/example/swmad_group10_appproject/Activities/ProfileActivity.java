package com.example.swmad_group10_appproject.Activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.swmad_group10_appproject.Models.Meme;
import com.example.swmad_group10_appproject.Models.User;
import com.example.swmad_group10_appproject.R;
import com.example.swmad_group10_appproject.ViewModels.ProfileViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity implements LocationListener {

    Button btn_createMeme, btn_uploadMeme, btn_back_profile, btn_logout;
    Spinner spr_profile;
    private Meme newMeme;
    ProfileViewModel vm;
    public final String TAG = "ProfileActivity";
    protected LocationManager locationManager;
    protected double latitude,longitude;
    private static final int PERMISSION_REQUEST_CODE = 1;


    ArrayList<String> arrayList = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    int radius;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //Set up widgets
        btn_uploadMeme = findViewById(R.id.btn_uploadMeme);
        btn_createMeme = findViewById(R.id.btn_CreateMeme);
        btn_back_profile = findViewById(R.id.btn_back_profile);
        btn_logout = findViewById(R.id.btn_logOut_profile);
        spr_profile = findViewById(R.id.spr_profile);


        vm = new ViewModelProvider(this).get(ProfileViewModel.class);

        SpinnerSetup();
        LocationSetup();
        PreloadRadius();

        btn_uploadMeme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImageFromGallery();
            }
        });

        btn_createMeme.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  Intent intent = new Intent(ProfileActivity.this, MemeBuilderActivity.class);
                  startActivity(intent);
              }
          }
        );

        btn_back_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vm.LogoutUser();
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
            }
        });


    }

    ActivityResultLauncher<Intent> getImageResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode()== AppCompatActivity.RESULT_OK){
                        Intent data = result.getData();
                        final Uri selectedImgUri = data.getData();
                        newMeme = new Meme("","","",latitude,longitude,0,0);
                        // https://stackoverflow.com/questions/3879992/how-to-get-bitmap-from-an-uri
                        Bitmap bitmap = null;
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImgUri);
                            if (bitmap!=null){
                                vm.uploadMeme(newMeme,bitmap);
                                SaveToast();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    //Reference: https://www.geeksforgeeks.org/how-to-select-an-image-from-gallery-in-android/
    private void getImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        getImageResultLauncher.launch(intent);
    }

    private void SaveToast(){
        CharSequence text = "You have uploaded your meme from the gallery !";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(this, text, duration);
        toast.show();
    }

    // Reference: https://stackoverflow.com/questions/32635704/android-permission-doesnt-work-even-if-i-have-declared-it
    private void LocationSetup() {
        // Check the android version
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_DENIED) {

                Log.d("permission", "permission denied to ACCESS_FINE_LOCATION - requesting it");
                String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
                requestPermissions(permissions, PERMISSION_REQUEST_CODE);
            }
        }
        //Reference: https://javapapers.com/android/get-current-location-in-android/
        // Get current location
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) this);
        }catch (Exception e){
            Log.d(TAG,"get current location error: " + e);
        }
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }

    // Reference: https://www.tutorialspoint.com/android/android_spinner_control.htm
    public void SpinnerSetup(){
        arrayList.add("5");
        arrayList.add("10");
        arrayList.add("15");
        arrayList.add("20");

        arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item,
                arrayList
        );
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spr_profile.setAdapter(arrayAdapter);
        spr_profile.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String itemValue = adapterView.getItemAtPosition(i).toString();
                radius = Integer.parseInt(itemValue);
                vm.updateCurrentRadius(radius);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void PreloadRadius() {

        vm.getCurrentRadius().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                for (int x = 0; x<4; x++){
                    int converValue = Integer.parseInt(arrayAdapter.getItem(x));
                    if (integer.equals(converValue)){
                        Log.d(TAG,"Preload Radius: " + integer);
                        spr_profile.setSelection(x);
                    }
                }
            }
        });
    }


}