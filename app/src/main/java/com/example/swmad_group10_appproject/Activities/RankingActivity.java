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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.example.swmad_group10_appproject.Adapter.RankingAdapter;
import com.example.swmad_group10_appproject.Models.Meme;
import com.example.swmad_group10_appproject.R;
import com.example.swmad_group10_appproject.ViewModels.RankingViewModel;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

// inspired from https://firebase.google.com/docs/firestore/query-data/listen#java_2


public class RankingActivity extends AppCompatActivity {

    //widgets
    private Button btn2Back;
    private RecyclerView recyclerView;
    private ArrayList<Meme> memeArrayList;
    private RankingAdapter rankingAdapter;
    private RankingViewModel rankingViewModel;
    private ProgressDialog progressDialog;
    private ArrayList<String> items;
    private AutoCompleteTextView autoCompleteTextView;
    private ArrayAdapter<String> adapterItems;
    private int daysBack;
    private boolean allTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        daysBack = 1;
        allTime = false;
        items = new ArrayList<String>();
        items.add(getString(R.string.ScoreToday));
        items.add(getString(R.string.ScoreThisWeek));
        items.add(getString(R.string.ScoreThisMonth));
        items.add(getString(R.string.ScoreThisYear));
        items.add(getString(R.string.ScoreAllTime));

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Data fetching...");
        progressDialog.show();
        setupUI();
    }

    // Initializes the UI
    private void setupUI() {

        rankingViewModel = new ViewModelProvider(this).get(RankingViewModel.class);

        btn2Back = findViewById(R.id.btn2Back);

        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        adapterItems = new ArrayAdapter<String>(this,R.layout.dropdown_item,items);
        autoCompleteTextView.setAdapter(adapterItems);

        // Kilde til det her?
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String item = adapterView.getItemAtPosition(position).toString();
                //Toast.makeText(getApplicationContext(),"Item:"+item,Toast.LENGTH_SHORT).show();
                switch (position){
                    case 0:
                        daysBack = 1;
                        allTime = false;
                        EventChangeListener();
                        Toast.makeText(getApplicationContext(),"Today",Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        daysBack = 7;
                        allTime = false;
                        EventChangeListener();
                        Toast.makeText(getApplicationContext(),"Week",Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        daysBack = 30;
                        allTime = false;
                        EventChangeListener();
                        Toast.makeText(getApplicationContext(),"Month",Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        daysBack = 365;
                        allTime = false;
                        EventChangeListener();
                        Toast.makeText(getApplicationContext(),"Year",Toast.LENGTH_SHORT).show();
                        break;
                    case 4:
                        allTime = true;
                        EventChangeListener();
                        Toast.makeText(getApplicationContext(),"All time",Toast.LENGTH_SHORT).show();
                        break;


                }
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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

    // Beskriv hvad der sker her
    private void EventChangeListener() {
        rankingViewModel.getScore().addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        memeArrayList.clear();
                        Date compareDate = new Date(System.currentTimeMillis()-daysBack*24*60*60*1000L);
                        if (e != null){
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            Log.e("Firestore error", e.getMessage());
                            return;
                        }
                        for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()){
                            if (documentChange.getType() == DocumentChange.Type.ADDED){
                                Meme meme = documentChange.getDocument().toObject(Meme.class);
                                // Inspiration/Reference: https://stackoverflow.com/questions/11965974/how-to-set-a-java-date-objects-value-to-yesterday
                                if (allTime || meme.getDate().after(compareDate)){
                                    memeArrayList.add(meme);
                                }
                            }
                            rankingAdapter.notifyDataSetChanges();
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                        }

                    }
                });

    }

}