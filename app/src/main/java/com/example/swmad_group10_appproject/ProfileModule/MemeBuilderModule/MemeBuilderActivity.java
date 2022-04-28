package com.example.swmad_group10_appproject.ProfileModule.MemeBuilderModule;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.swmad_group10_appproject.R;

public class MemeBuilderActivity extends AppCompatActivity {

    Button btn_openCamera, btn_editMeme;
    ImageView img_camera;
    TextView txt_editMeme;

    Bitmap captureImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meme_builder);

        img_camera = findViewById(R.id.img_camera);
        btn_openCamera = findViewById(R.id.btn_openCamera);
        btn_editMeme = findViewById(R.id.btn_editMeme);
        txt_editMeme = findViewById(R.id.txt_edit);

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
                startActivityForResult(intent,100);
            }
        });

        //Edit text on meme
        btn_editMeme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (captureImage!=null){
                    txt_editMeme.setVisibility(View.VISIBLE);
                    //Upload imageMeme and text in database
                }
            }
        });

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
}