package com.example.swmad_group10_appproject.Activities;

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

    Button btn_createMeme, btn_uploadMeme;
    Spinner spr_profile;

    private Meme newMeme;
    private User user;

    ProfileViewModel vm;

    RecyclerView recyclerLikedMemeList;
    RecyclerView.LayoutManager layoutManager;

    public final String TAG = "ProfileActivity";

    protected LocationManager locationManager;
    protected double latitude,longitude;
    private static final int PERMISSION_REQUEST_CODE = 1;


    ArrayList<String> arrayList = new ArrayList<>();
    int radius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        btn_uploadMeme = findViewById(R.id.btn_uploadMeme);
        btn_createMeme = findViewById(R.id.btn_CreateMeme);
        spr_profile = findViewById(R.id.spr_profile);


        vm = new ViewModelProvider(this).get(ProfileViewModel.class);


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

        SpinnerSetup();

        LocationSetup();

        PreloadRadius();

    }




    //Reference: https://www.geeksforgeeks.org/how-to-select-an-image-from-gallery-in-android/
    private void getImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(intent.createChooser(intent, "Select Picture"), 200);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // this function is triggered when user
        // selects the image from the imageChooser
        if (resultCode == RESULT_OK){
            if(requestCode==200){
                final Uri selectedImgUri = data.getData();
                newMeme = new Meme("","","",0.0,0.0,0,0);
                // https://stackoverflow.com/questions/3879992/how-to-get-bitmap-from-an-uri
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImgUri);
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


    private void SaveToast(){
        CharSequence text = "You have uploaded your meme from the gallery !";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(this, text, duration);
        toast.show();
    }

    private void LocationSetup() {
        // Reference: https://stackoverflow.com/questions/32635704/android-permission-doesnt-work-even-if-i-have-declared-it
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

        }
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        //Log.d(TAG,"Get current location: " +latitude + " " + longitude );

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

    public void SpinnerSetup(){
        arrayList.add("5");
        arrayList.add("10");
        arrayList.add("15");
        arrayList.add("20");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
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
                setRadius(radius);
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

            }
        });
    }

    public void setRadius(int radius){

    }

}