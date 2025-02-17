package com.jamith.booksformeseller.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.jamith.booksformeseller.R;

public class MessageService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        Log.d("notification", message.getNotification().getTitle());
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel notificationChannel =
                    new NotificationChannel("C1", "Channel1",
                            NotificationManager.IMPORTANCE_DEFAULT);

            notificationManager.createNotificationChannel(notificationChannel);
        }
        Notification notification = new NotificationCompat.Builder(getApplicationContext(), "C1")
                .setContentTitle(message.getNotification().getTitle())
                .setContentText(message.getNotification().getBody())
                .setSmallIcon(R.drawable.user)
                .build();
        notificationManager.notify(1, notification);
    }
}
