package com.example.swmad_group10_appproject.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.swmad_group10_appproject.Persistance.Repository;
import com.example.swmad_group10_appproject.R;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Inspiration from: L5 | Services and Asynch Processing - DemoServices 1.3.zip
public class MemeService extends Service {

    private static final String TAG = "foregroundService";
    public static final String SERVICE_CHANNEL = "serviceChannel";
    public static final int NOTIFICATION_ID = 666;
    private Repository memeRepository;
    private ExecutorService execService;
    boolean started = false;

    public MemeService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //check for Android version - whether we need to create a notification channel (from Android 0 and up, API 26)
        if(Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(SERVICE_CHANNEL, "Meme Service", NotificationManager.IMPORTANCE_LOW);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }

        memeRepository = Repository.getInstance(getApplication());
        startForeground(NOTIFICATION_ID, getNotification());
        updateMemeInBackground();
        return START_STICKY;
    }

    private void updateMemeInBackground() {
        if (execService == null) {
            execService = Executors.newSingleThreadExecutor();
        }

        execService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(60000);
                    Log.d(TAG, "run: Woke up" );
                } catch (InterruptedException e) {
                    Log.e(TAG, "Error in service: ", e);
                }

                updateMemeInBackground();
            }
        });
    }

    @NonNull
    private Notification getNotification() {
        return new NotificationCompat.Builder(getApplicationContext(), SERVICE_CHANNEL)
                .setContentTitle("UMeme AppProject - Group 10")
                .setContentText("Remember to check out the quality memes! :)")
                .setSmallIcon(R.mipmap.ic_trollicon_foreground)
                .build();
    }

    @Override
    public void onDestroy() {
        started = false;
        super.onDestroy();
    }


    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}