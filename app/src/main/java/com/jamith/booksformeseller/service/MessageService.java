package com.jamith.booksformeseller.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.jamith.booksformeseller.R;

public class MessageService extends FirebaseMessagingService {
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        Log.d("notification", message.getNotification().getTitle());
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("C1", "Channel1", NotificationManager.IMPORTANCE_DEFAULT);

            notificationManager.createNotificationChannel(notificationChannel);
        }
        Notification notification = new NotificationCompat.Builder(getApplicationContext(), "C1").setContentTitle(message.getNotification().getTitle()).setContentText(message.getNotification().getBody()).setSmallIcon(R.drawable.user).build();
        notificationManager.notify(1, notification);
    }


    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d("New FCM Token", token);
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Log.d("Fetching FCM registration token failed", task.getException().toString());
                    return;
                }
                String refreshedToken = task.getResult();
                Log.d("Refreshed Token", refreshedToken);
                if (firebaseAuth.getCurrentUser() != null) {
                    tokenUpdate(refreshedToken);
                }
            }
        });
    }

    public void tokenUpdate(String token) {
        firebaseFirestore.collection("sellers").document(firebaseAuth.getCurrentUser()
                .getUid()).update("fcmToken", token).addOnSuccessListener(aVoid -> {
            Log.d("Firestore", "FCM token updated successfully.");
        }).addOnFailureListener(e -> {
            Log.e("Firestore", "Error updating FCM token", e);
        });
    }
}
