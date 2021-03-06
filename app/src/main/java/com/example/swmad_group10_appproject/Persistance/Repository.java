package com.example.swmad_group10_appproject.Persistance;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.swmad_group10_appproject.Models.Meme;
import com.example.swmad_group10_appproject.Models.User;
import com.example.swmad_group10_appproject.Services.MemeService;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Repository {

    private static final String TAG = "Repository";
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseStore;
    private static Repository instance;
    private Context applicationContext;
    private FirebaseStorage storage;
    private DatabaseReference databaseReference;

    public static Repository getInstance(Application app) {
        if (instance == null) {
            instance = new Repository(app);
        }
        return instance;
    }

    public Repository(Application app) {
        applicationContext = app.getApplicationContext();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Memes");
    }


    // Inspiration to make the Register-system is taken from: https://www.youtube.com/watch?v=TwHmrZxiPA8
    public void registerUser(User user) {

        // Registering the user in firebase
        firebaseAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    firebaseStore.collection("users")
                            .add(user)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d("Repository", "User added: " + documentReference.getId());
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("Repository", "Error adding user document", e);
                                }
                            });

                }
            }
        });
    }

    // Authenticating the user with Firebase
    public Task<AuthResult> loginUser(String email, String password) {
        return firebaseAuth.signInWithEmailAndPassword(email,password);
    }

    // Logs the user out from the UMeme App.
    public void logoutUser(){
        firebaseAuth.signOut();
    }

    public void uploadMeme(Meme meme, Bitmap image){

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte [] data = outputStream.toByteArray();

        String path = "Memes/" + UUID.randomUUID() + ".png";
        StorageReference storageRef = storage.getReference(path);

        StorageMetadata metaData = new StorageMetadata.Builder().build();

        UploadTask uploadTask = storageRef.putBytes(data,metaData);

        Task<Uri> getDownloadURI = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return storageRef.getDownloadUrl();
            }
        });

        getDownloadURI.addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
              Uri downloadURI = task.getResult();

              meme.setDate(new Date());
              meme.setMemeImgURL(downloadURI.toString());

              firebaseStore.collection("Memes")
                       .add(meme)
                       .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                           @Override
                           public void onSuccess(DocumentReference documentReference) {
                               Log.d("Repository", "DocumentSnapshot added with ID: " + documentReference.getId());
                           }
                       })
                       .addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               Log.w("Repository", "Error adding document", e);
                           }
                       });
            }
        });

    }

    // Gets the current logged in User.
    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    // Starts the foregroundService from the memeService class.
    public void startForegroundService(Context context) {
        Intent drinkService = new Intent(context, MemeService.class);
        context.startService(drinkService);
    }


    // Gets the Memes withing a specific radius
    // Inspiration/Reference: https://www.geeksforgeeks.org/how-to-update-data-in-firebase-firestore-in-android/
    public void getMemesWithinRadius(MutableLiveData<List<Meme>> result, int radius, double latitude, double longitude){
        List<Meme> tempList = new ArrayList<Meme>();
        firebaseStore.collection("Memes").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()){
                    for (DocumentSnapshot docSnapshot : queryDocumentSnapshots.getDocuments()) {
                        Meme tempMeme = docSnapshot.toObject(Meme.class);
                        tempMeme.setKey(docSnapshot.getId());
                        float[] results = new float[3];
                        android.location.Location.distanceBetween(latitude, longitude,
                                tempMeme.getLatitude(), tempMeme.getLongitude(), results);
                        if (results[0] < radius*1000){
                            tempList.add(tempMeme);
                        }
                    }
                    result.setValue(tempList);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure getting memes: ", e);
            }
        });
    }

    public void updateMeme(Meme meme){
        firebaseStore.collection("Memes").document(meme.getKey()).set(meme);
    }

    public LiveData<Integer> getCurrentRadius(){
        FirebaseUser user = getCurrentUser();
        MutableLiveData<Integer> result = new MutableLiveData<Integer>();
        if (user != null) {
            String email = firebaseAuth.getCurrentUser().getEmail();

            firebaseStore.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (!queryDocumentSnapshots.isEmpty()){
                        for (DocumentSnapshot document: queryDocumentSnapshots.getDocuments()){
                            User user = document.toObject(User.class);
                            if (user.getEmail() != null) {
                                if (email.equals(user.getEmail())) {
                                    result.setValue(user.getRadius());
                                }
                            }
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "onFailure: ",e );
                }
            });
        }
        return result;
    }

    // Updates the users CurrentRadius with the new one given in the parameter.
    public void updateCurrentRadius(int radius){
        String email = firebaseAuth.getCurrentUser().getEmail();

        firebaseStore.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()){
                    for (DocumentSnapshot document: queryDocumentSnapshots.getDocuments()){
                        if (email.equals(document.getData().get("email")))
                        firebaseStore.collection("users").document(document.getId()).update("radius",radius);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: ",e );
            }
        });
    }

    // Gets the scores from the firebase database and orders them by descending.
    public Query getScoreFromDB(){
        return firebaseStore.collection("Memes").orderBy("score", Query.Direction.DESCENDING);
    }

}
