package com.example.swmad_group10_appproject.ProfileModule;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.swmad_group10_appproject.ProfileModule.MemeBuilderModule.MemeBuilderActivity;
import com.example.swmad_group10_appproject.R;

public class ProfileActivity extends AppCompatActivity {

    Button btn_createMeme, btn_uploadMeme;

    private static int RESULT_LOAD_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        btn_uploadMeme = findViewById(R.id.btn_uploadMeme);
        btn_createMeme = findViewById(R.id.btn_CreateMeme);



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
                if (selectedImgUri!=null){
                    //Upload the selected picture here
                }
            }
        }
    }
}