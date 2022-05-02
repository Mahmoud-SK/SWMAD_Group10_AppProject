package com.example.swmad_group10_appproject.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.swmad_group10_appproject.R;

public class RankingActivity extends AppCompatActivity {

    //widgets
    private Button btn2Back;
    private RatingBar ratingBar;
    private RecyclerView recyclerView;
    private ImageView imageView2;

    float myRating = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        setupUI();
    }

    private void setupUI(){
        btn2Back = findViewById(R.id.btn2Back);
        ratingBar = findViewById(R.id.ratingBar);
        recyclerView = findViewById(R.id.recyclerView);
        imageView2 = findViewById(R.id.imageView2);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

                int rating = (int) v;
                String message = null;
                myRating = ratingBar.getRating();

                switch (rating) {
                    case 1:
                        message = "Sorry, we are working on improving of this Meme :)";
                        break;

                    case 2:
                        message = "Try again. You should like it better in the next time :)";
                        break;

                    case 3:
                        message = "Good enough!";
                        break;

                    case 4:
                        message = "Great! Thank you!";
                        break;

                    case 5:
                        message = "Awesome! The rating will be credited as the highest score to this Meme";
                        break;

                }
                Toast.makeText(RankingActivity.this, "Your rating is: " + myRating + message, Toast.LENGTH_SHORT).show();

            }
        });

        btn2Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { finish();}
        });
    }
}