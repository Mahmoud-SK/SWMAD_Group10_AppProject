package com.example.swmad_group10_appproject.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swmad_group10_appproject.Models.Meme;
import com.example.swmad_group10_appproject.R;
import com.example.swmad_group10_appproject.ViewModels.MemeBuilderViewModel;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;


public class MemeBuilderActivity extends AppCompatActivity implements LocationListener{

    private static final String TAG = "MemeBuilderActivity";
    Button btn_openCamera, btn_editMeme;
    ImageButton btn_imgUpload;
    ImageView img_camera;
    TextView txt_edit_bottom, txt_edit_top;

    Bitmap captureImage;

    MemeBuilderViewModel vm;

    protected LocationManager locationManager;
    protected double latitude,longitude;
    private static final int PERMISSION_REQUEST_CODE = 1;
    TextView txt_Location_test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meme_builder);

        img_camera = findViewById(R.id.img_camera);
        btn_openCamera = findViewById(R.id.btn_openCamera);
        btn_editMeme = findViewById(R.id.btn_editMeme);
        btn_imgUpload = findViewById(R.id.btn_imgUpload);
        txt_edit_bottom = findViewById(R.id.txt_edit_bottom);
        txt_edit_top = findViewById(R.id.txt_edit_top);

        txt_Location_test = findViewById(R.id.txt_location_test); //Only for tesing
        vm = new ViewModelProvider(this).get(MemeBuilderViewModel.class);

        // Reference: https://www.youtube.com/watch?v=RaOyw84625w
        //Request for camera permission
        if(ContextCompat.checkSelfPermission(MemeBuilderActivity.this,
                Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MemeBuilderActivity.this,
                    new String[]{
                            Manifest.permission.CAMERA
                    },
                    100);
        }
        btn_openCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Open Camera
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                try {
                    startActivityForResult(intent,100);
                }catch (Exception e){
                    //Error message
                }

            }
        });

        // Reference: https://www.youtube.com/watch?v=Rd89cVKrQBg
        //Edit text on meme
        btn_editMeme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (captureImage!=null){
                    txt_edit_bottom.setVisibility(View.VISIBLE);
                    txt_edit_top.setVisibility(View.VISIBLE);
                }
            }
        });

        btn_imgUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("MemeBuilderActivity", "Test state");
                if (captureImage != null){
                    Meme newMeme = new Meme();
                    newMeme = new Meme(txt_edit_top.getText().toString(),txt_edit_bottom.getText().toString(),"",latitude,longitude,0,0);

                    try {
                        vm.uploadMeme(newMeme,captureImage);        //Upload meme on database
                        SaveToast();
                    }catch (Exception e){
                        Log.e(TAG, "onClick: ", e );
                    }
                    finish();
                    Log.d("MemeBuilderActivity", "The meme is saved in database");
                }
            }
        });

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
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,this);
        }catch (Exception e){

        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100){
            //Get Capture Image
            if (data.getExtras()!=null){
                captureImage = (Bitmap) data.getExtras().get("data");
            }
            //Set Capture Image
            img_camera.setImageBitmap(captureImage);
        }
    }

    private void SaveToast(){
        CharSequence text = "You have uploaded your meme !";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(this, text, duration);
        toast.show();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        txt_Location_test.setText("Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
        latitude = location.getLatitude();
        longitude = location.getLongitude();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
        Log.d("Latitude","status");
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
        Log.d("Latitude","enable");
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
        Log.d("Latitude","disable");
    }
}