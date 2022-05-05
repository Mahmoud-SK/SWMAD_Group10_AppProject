package com.example.swmad_group10_appproject.Activities;

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
import com.example.swmad_group10_appproject.ViewModels.ProfileViewModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MemeBuilderActivity extends AppCompatActivity {

    Button btn_openCamera, btn_editMeme;
    ImageButton btn_imgUpload;
    ImageView img_camera;
    TextView txt_edit_bottom, txt_edit_top;

    Bitmap captureImage;

    MemeBuilderViewModel vm;

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
                if (captureImage!=null){
                    //Upload meme on database
                    Meme newMeme = new Meme();
                    newMeme = new Meme(txt_edit_top.getText().toString(),txt_edit_bottom.getText().toString(),"",0.0,0.0,0);
                    try {
                        vm.uploadMeme(newMeme,captureImage);
                        SaveToast();
                    }catch (Exception e){

                    }
                    finish();
                    Log.d("MemeBuilderActivity", "The meme is saved in database");
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

    private void SaveToast(){
        CharSequence text = "You have uploaded your meme !";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(this, text, duration);
        toast.show();
    }
}