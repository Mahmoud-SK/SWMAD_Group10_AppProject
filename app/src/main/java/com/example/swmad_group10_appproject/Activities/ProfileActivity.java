package com.example.swmad_group10_appproject.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;

import com.example.swmad_group10_appproject.Adapter.LikedMemeAdapter;
import com.example.swmad_group10_appproject.Models.Meme;
import com.example.swmad_group10_appproject.R;
import com.example.swmad_group10_appproject.ViewModels.ProfileViewModel;
import com.example.swmad_group10_appproject.ViewModels.RegisterViewModel;

import java.io.IOException;

public class ProfileActivity extends AppCompatActivity implements LikedMemeAdapter.ILikedMemeClickedListener {

    Button btn_createMeme, btn_uploadMeme;

    private Meme newMeme;

    ProfileViewModel vm;

    RecyclerView recyclerLikedMemeList;
    RecyclerView.LayoutManager layoutManager;

    LikedMemeAdapter likedMemeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        btn_uploadMeme = findViewById(R.id.btn_uploadMeme);
        btn_createMeme = findViewById(R.id.btn_CreateMeme);


        recyclerLikedMemeList = findViewById(R.id.rcv_liked_meme_list);
        layoutManager = new LinearLayoutManager(this);
        recyclerLikedMemeList.setLayoutManager(layoutManager);

        likedMemeAdapter = new LikedMemeAdapter(this);
        recyclerLikedMemeList.setAdapter(likedMemeAdapter);

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
                newMeme = new Meme("Text","Text","",0.0,0.0,0,0,"");
                // https://stackoverflow.com/questions/3879992/how-to-get-bitmap-from-an-uri
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImgUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                vm.uploadMeme(newMeme,bitmap);

            }
        }
    }

    @Override
    public void onLikedMemeDetailClicked(int index) {

    }
}