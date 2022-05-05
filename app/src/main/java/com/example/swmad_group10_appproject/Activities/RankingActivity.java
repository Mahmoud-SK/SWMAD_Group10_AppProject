package com.example.swmad_group10_appproject.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.swmad_group10_appproject.Adapter.RankingAdapter;
import com.example.swmad_group10_appproject.Models.Meme;
import com.example.swmad_group10_appproject.R;
import com.example.swmad_group10_appproject.ViewModels.RankingViewModel;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

// inspired from https://firebase.google.com/docs/firestore/query-data/listen#java_2

public class RankingActivity extends AppCompatActivity {

    //widgets
    private Button btn2Back;
    private RecyclerView recyclerView;
    ArrayList<Meme> memeArrayList;
    RankingAdapter rankingAdapter;
    FirebaseFirestore db;
    private RankingViewModel rankingViewModel;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        progressDialog =new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Data fetching...");
        progressDialog.show();

        setupUI();
    }

    private void setupUI(){

        rankingViewModel = new ViewModelProvider(this).get(RankingViewModel.class);

        btn2Back = findViewById(R.id.btn2Back);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        memeArrayList = new ArrayList<Meme>();
        rankingAdapter = new RankingAdapter(RankingActivity.this, memeArrayList);
        recyclerView.setAdapter(rankingAdapter);
        EventChangeListener();

        btn2Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        }

    private void EventChangeListener() {
        db.collection("Memes").orderBy("score", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                        if (e != null){

                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            Log.e("Firestore error", e.getMessage());
                            return;
                        }
                        for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()){
                            if (documentChange.getType() == DocumentChange.Type.ADDED){
                                memeArrayList.add(documentChange.getDocument().toObject(Meme.class));
                            }

                            rankingAdapter.notifyDataSetChanged();
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                        }

                    }
                });

    }

}