package com.example.swmad_group10_appproject.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
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
    //private List<Meme> memes;
    private int memeIndex;
    private boolean firstMeme;
    private int animIn;
    private int animOut;

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


        animIn = R.anim.no_animation;
        animOut = R.anim.no_animation;
        //getMemes();
        newMemes = memeVM.getMemeList();
        newMemes.observe(this, new Observer<List<Meme>>() {
            @Override
            public void onChanged(List<Meme> memes) {
                if (!memes.isEmpty() && firstMeme)
                    firstMeme = false;
                    nextMeme();
            }
        });
        memeVM.getMemesWithinRadius(2);
        setupSwipeDetection();
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
                            newMemes.getValue().get(memeIndex).updateScore(-1);
                            Log.d(TAG, "onFling: left " + newMemes.getValue().get(memeIndex).getKey());
                            Log.d(TAG, "onFling: left " + newMemes.getValue().get(memeIndex).getScore());
                            memeVM.UpdateMeme(newMemes.getValue().get(memeIndex));
                            animIn = R.anim.fade_in;
                            animOut = R.anim.slide_out_left;
                            nextMeme();
                        }
                        else if (event2.getX() - event1.getX() > minSwipeDistance){
                            //Log.d(TAG, "onFling: right");
                            /*memes.get(memeIndex).updateScore(1);
                            memeVM.UpdateMeme(memes.get(memeIndex));*/
                            newMemes.getValue().get(memeIndex).updateScore(1);
                            Log.d(TAG, "onFling: right " + newMemes.getValue().get(memeIndex).getKey());
                            Log.d(TAG, "onFling: right " + newMemes.getValue().get(memeIndex).getScore());
                            memeVM.UpdateMeme(newMemes.getValue().get(memeIndex));
                            animIn = R.anim.fade_in;
                            animOut = R.anim.slide_out_right;
                            nextMeme();
                        }
                        return super.onFling(event1, event2, velocityX, velocityY);
                    }
                });
    }

    public void nextMeme(){
        if (memeIndex >= newMemes.getValue().size()-1){
            memeIndex = 0;
            firstMeme = true;
            memeVM.getMemesWithinRadius(2);
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
       /* if (!memes.isEmpty()) {
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
        }*/
    }

    /*public void getMemes(){
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
    }*/

    /*public void ConverteToMemes(List<DocumentSnapshot> snapshots){
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
    }*/

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