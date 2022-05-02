package com.example.swmad_group10_appproject.Activities;

import androidx.appcompat.app.AppCompatActivity;

import com.example.swmad_group10_appproject.Fragments.MemeFragment;
import com.example.swmad_group10_appproject.R;

import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Button;

public class MemeActivity extends AppCompatActivity {

    private static final String TAG = "MemeActivity";
    private Button btnRanking;
    private Button btnProfile;

    private GestureDetector gesture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meme);

        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container1, MemeFragment.newInstance("top ged", "bottom ged"))
                    .commitNow();
        }
        btnProfile = findViewById(R.id.btnMemeGoToProfile);
        btnRanking = findViewById(R.id.btnMemeGoToRanking);

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
                            getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.fade_in, R.anim.slide_out_left)
                                    .replace(R.id.container1, MemeFragment.newInstance("top left ged",
                                            "bottom left ged"))
                                    .commitNow();
                        }
                        else if (event2.getX() - event1.getX() > minSwipeDistance){
                            getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.fade_in, R.anim.slide_out_right)
                                    .replace(R.id.container1, MemeFragment.newInstance("top right ged",
                                            "bottom right ged"))
                                    .commitNow();
                            Log.d(TAG, "onFling: right");
                        }

                        return super.onFling(event1, event2, velocityX, velocityY);
                    }
                });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event){
        gesture.onTouchEvent(event);
        return super.dispatchTouchEvent(event);
    }
}