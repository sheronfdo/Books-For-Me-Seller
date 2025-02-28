package com.jamith.booksformeseller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.jamith.booksformeseller.R;
import com.jamith.booksformeseller.activity.signup.SignUpActivity;
import com.jamith.booksformeseller.database.DataSyncManager;
import com.jamith.booksformeseller.service.MessageService;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private static final int SPLASH_DURATION = 2000;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        progressBar = findViewById(R.id.mainActivityprogressBar);
        progressBar.setVisibility(View.VISIBLE);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            progressBar.setVisibility(View.GONE);
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }
        DataSyncManager syncManager = new DataSyncManager(this);
        syncManager.performInitialSync(success -> {
            Log.d("fetch success", "fetching succeess");
        });
        progressBar.setVisibility(View.GONE);
        Button signUpButton = findViewById(R.id.sign_up_button);
        signUpButton.setOnClickListener(v -> {
            v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100)
                    .withEndAction(() -> v.animate().scaleX(1f).scaleY(1f).setDuration(100));
            Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        Button signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100)
                        .withEndAction(() -> v.animate().scaleX(1f).scaleY(1f).setDuration(100));
                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("FCM_TOKEN", "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        String token = task.getResult();
                        Log.d("FCM_TOKEN", "FCM Token: " + token);
                        MessageService messageService = new MessageService();
                        if (mAuth.getCurrentUser() != null){
                            messageService.tokenUpdate(token);
                        }
                    }
                });

    }
}