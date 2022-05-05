package com.example.swmad_group10_appproject.Persistance;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.swmad_group10_appproject.Activities.LoginActivity;
import com.example.swmad_group10_appproject.Activities.MainActivity;
import com.example.swmad_group10_appproject.Models.Meme;
import com.example.swmad_group10_appproject.Services.MemeService;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Repository {

    private static final String TAG = "Repository";
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseStore;
    private static Repository instance;
    private Context applicationContext;
    FirebaseStorage storage;
    StorageReference storageRef;
    DatabaseReference databaseReference;

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

    public void registerUser(String email, String password, String username) {

        // Registering the user in firebase
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Toast.makeText(applicationContext, "Account has been created!", Toast.LENGTH_SHORT).show();
                    Map<String, Object> user = new HashMap<>();
                    user.put("Username", username);
                    user.put("Email", email);
                    user.put("Password", password);

                    firebaseStore.collection("users")
                            .add(user)
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
            }
        });
    }

    public void loginUser(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d("Repository", "Logged in! Welcome to UMeme");
                } else {
                    Log.d("Repository", "Error! " + task.getException().getMessage());
                }
            }
        });
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

    public void startForegroundService(Context context) {
        Intent drinkService = new Intent(context, MemeService.class);
        context.startService(drinkService);
    }

    public Task<QuerySnapshot> getMemes(){
         return firebaseStore.collection("Memes").get();
                 /*.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
             @Override
             public void onComplete(@NonNull Task<QuerySnapshot> task) {
                 if (task.isSuccessful()){
                     Log.d(TAG, "get memes from firebase: " + task.getResult().getDocuments());
                 }
                 else {
                     Log.e(TAG, "error getting memes from firebase: ", task.getException());
                 }
             }
         });*/
    }


}
