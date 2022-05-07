package com.example.swmad_group10_appproject.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.swmad_group10_appproject.Fragments.MemeFragment;
import com.example.swmad_group10_appproject.Models.Meme;
import com.example.swmad_group10_appproject.R;
import com.example.swmad_group10_appproject.ViewModels.MemeViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import android.content.Intent;
import android.os.Bundle;
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
    private List<Meme> memes;
    private int memeIndex;
    private int animIn;
    private int animOut;
    private String tempLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meme);

        memeIndex = 0;
        memes = new ArrayList<Meme>();
        //tempLink = "https://firebasestorage.googleapis.com/v0/b/swmad-group10-appproject.appspot.com/o/Memes%2F4b45692d-12ea-420a-a8f7-8b986cc4193d.png?alt=media&token=9faddd86-7827-4720-874c-ec2730362b1a";
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


        animIn = R.anim.no_animation;
        animOut = R.anim.no_animation;
        getMemes();

        if (savedInstanceState == null){
            //nextMeme(R.anim.no_animation, R.anim.no_animation);
            /*getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container1, MemeFragment.newInstance("top ged", "bottom ged", tempLink))
                    .commitNow();*/
        }

        setupSwipeDetection();
    }

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
                            Log.d(TAG, "onFling: left");
                            memes.get(memeIndex).updateScore(-1);
                            memeVM.UpdateMeme(memes.get(memeIndex));
                            Log.d(TAG, "onFling: left " + memes.get(memeIndex).getKey());
                            Log.d(TAG, "onFling: left " + memes.get(memeIndex).getScore());
                            nextMeme(R.anim.fade_in, R.anim.slide_out_left);

                            /*getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.fade_in, R.anim.slide_out_left)
                                    .replace(R.id.container1, MemeFragment.newInstance("top left ged",
                                            "bottom left ged", tempLink))
                                    .commitNow();*/
                        }
                        else if (event2.getX() - event1.getX() > minSwipeDistance){
                            memes.get(memeIndex).updateScore(1);
                            memeVM.UpdateMeme(memes.get(memeIndex));
                            Log.d(TAG, "onFling: right " + memes.get(memeIndex).getKey());
                            Log.d(TAG, "onFling: right " + memes.get(memeIndex).getScore());
                            nextMeme(R.anim.fade_in, R.anim.slide_out_right);
                            /*getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.fade_in, R.anim.slide_out_right)
                                    .replace(R.id.container1, MemeFragment.newInstance("top right ged",
                                            "bottom right ged", tempLink))
                                    .commitNow();*/
                            Log.d(TAG, "onFling: right");
                        }

                        return super.onFling(event1, event2, velocityX, velocityY);
                    }
                });
    }

    public void nextMeme(int inAnimation, int outAnimation){
        if (!memes.isEmpty()) {
            if (memeIndex >= memes.size()-1){
                memeIndex = 0;
                animIn = inAnimation;
                animOut = outAnimation;
                getMemes();
                Log.d(TAG, "nextMeme: out of memes");
            }
            else {
                Meme nextMeme = memes.get(memeIndex);

                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(inAnimation, outAnimation)
                        .replace(R.id.container1, MemeFragment.newInstance(nextMeme.getTopText(),
                                nextMeme.getBottomText(), nextMeme.getMemeImgURL()))
                        .commitNow();
                memeIndex++;
            }
        }
        else {
            Log.e(TAG, "nextMeme: No memes");
        }
    }

    public void getMemes(){
        memeVM.getMemes().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    ConverteToMemes(task.getResult().getDocuments());
                    nextMeme(animIn, animOut);
                    //Log.d(TAG, "get memes from firebase: " + task.getResult().getDocuments().get(0).);
                }
                else {
                    Log.e(TAG, "error getting memes from firebase: ", task.getException());
                }
            }
        });
    }

    public void ConverteToMemes(List<DocumentSnapshot> snapshots){
        ArrayList<Meme> tempList = new ArrayList<Meme>();
        for (DocumentSnapshot snapshot:snapshots) {
            Map<String,Object> data = snapshot.getData();
            Log.d(TAG, "get memes from firebase: " + data);

            Meme tempMeme = new Meme((String) data.get("topText"),
                    (String) data.get("bottomText"),
                    (String) data.get("memeImgURL"),
                    (double) data.get("latitude"),
                    (double) data.get("longitude"),
                    Integer.parseInt(String.valueOf(data.get("userId"))),
                    Integer.parseInt(String.valueOf(data.get("score")))
            );
            tempMeme.setKey(snapshot.getId());
            Log.d(TAG, "outside");
            if(data.get("date") != null) {
                Log.d(TAG, "inside");
                tempMeme.setDate(((Timestamp) data.get("date")).toDate());
                Log.d(TAG, "date: " + tempMeme.getDate());
            }

            tempList.add(tempMeme);
        }

        memes = tempList;
    }

    private void launchProfileActivity(){
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    private void launchRankingActivity(){
        Intent intent = new Intent(this, RankingActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event){
        gesture.onTouchEvent(event);
        return super.dispatchTouchEvent(event);
    }
}