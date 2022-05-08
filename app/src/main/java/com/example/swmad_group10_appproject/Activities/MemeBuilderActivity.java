package com.example.swmad_group10_appproject.Activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import android.os.Bundle;
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

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import java.util.List;


public class MemeBuilderActivity extends AppCompatActivity implements LocationListener{

    private static final String TAG = "MemeBuilderActivity";

    private Button btn_openCamera, btn_editMeme;
    private ImageButton btn_imgUpload;
    private ImageView img_camera;
    private TextView txt_edit_bottom, txt_edit_top;
    private int editButtonCount;
    private Bitmap captureImage;
    private Meme newMeme;
    private MemeBuilderViewModel vm;

    //Location variables
    protected LocationManager locationManager;
    protected double latitude,longitude;
    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        LocationListener.super.onLocationChanged(locations);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meme_builder);
        //Set up widgets
        img_camera = findViewById(R.id.img_camera);
        btn_openCamera = findViewById(R.id.btn_openCamera);
        btn_editMeme = findViewById(R.id.btn_editMeme);
        btn_imgUpload = findViewById(R.id.btn_imgUpload);
        txt_edit_bottom = findViewById(R.id.txt_edit_bottom);
        txt_edit_top = findViewById(R.id.txt_edit_top);

        vm = new ViewModelProvider(this).get(MemeBuilderViewModel.class);

        CameraSetUp();
        LocationSetUp();

        btn_openCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Opens the Camera
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                try {
                    openCameraResultLauncher.launch(intent);
                }catch (Exception e){
                    //Error message
                    Log.d(TAG, "Error opening the camera");
                }

            }
        });

        // Inspiration/Reference taken from: https://www.youtube.com/watch?v=Rd89cVKrQBg
        // This method is for editing the text on a meme
        btn_editMeme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editButtonCount = 0;
                if (captureImage!=null){
                    txt_edit_bottom.setVisibility(View.VISIBLE);
                    txt_edit_top.setVisibility(View.VISIBLE);
                    editButtonCount+=1;
                }
            }
        });

        btn_imgUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("MemeBuilderActivity", "Test state");
                if (captureImage != null && editButtonCount!=0){
                    String topText = txt_edit_top.getText().toString();
                    String bottomText = txt_edit_bottom.getText().toString();
                    newMeme = new Meme();
                    newMeme = new Meme(topText,bottomText,"",latitude,longitude,0,0);

                    try {
                        vm.uploadMeme(newMeme,captureImage); // Uploads meme to the Firebase database
                        Log.d("MemeBuilderActivity", "The meme is saved in database");
                        SaveToast();
                    } catch (Exception e){
                        Log.e(TAG, "onClick: ", e );
                    }

                    finish();
                }
            }
        });



    }

    ActivityResultLauncher<Intent> openCameraResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d(TAG,"Get RESULT CODE: " + result.getResultCode());
                    if(result.getResultCode()==RESULT_OK){
                        //Get Capture Image
                        if (result.getData().getExtras()!=null){
                            captureImage = (Bitmap) result.getData().getExtras().get("data");
                        }
                        //Set Capture Image
                        img_camera.setImageBitmap(captureImage);
                    }
                }
            }
    );

    private void CameraSetUp() {
        // Inspiration/Reference from: https://www.youtube.com/watch?v=RaOyw84625w
        //Request for camera permission
        if(ContextCompat.checkSelfPermission(MemeBuilderActivity.this,
                Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MemeBuilderActivity.this,
                    new String[]{
                            Manifest.permission.CAMERA
                    },
                    100);
        }
    }


    private void SaveToast(){
        CharSequence text = "You have uploaded your meme !";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(this, text, duration);
        toast.show();
    }

    public void LocationSetUp(){
        // Inspiration/Reference: https://stackoverflow.com/questions/32635704/android-permission-doesnt-work-even-if-i-have-declared-it
        // Checks the android version
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_DENIED) {

                Log.d("permission", "permission denied to ACCESS_FINE_LOCATION - requesting it");
                String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};

                requestPermissions(permissions, PERMISSION_REQUEST_CODE);

            }
        }
        // Inspiration/Reference: https://javapapers.com/android/get-current-location-in-android/
        // Gets the current location of the phone.
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,this);
        }catch (Exception e){
            Log.d(TAG,"Get current location error");
        }
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        Log.d("Latitude","status");
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