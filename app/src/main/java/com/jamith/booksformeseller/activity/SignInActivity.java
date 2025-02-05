package com.jamith.booksformeseller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.jamith.booksformeseller.R;
import com.jamith.booksformeseller.service.SignInService;

public class SignInActivity extends AppCompatActivity {
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button signInButton;

    private SignInService signInService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        signInService = new SignInService();
        emailEditText = findViewById(R.id.signInEmailAddress);
        passwordEditText = findViewById(R.id.signInPassword);
        signInButton = findViewById(R.id.signInButton);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (validateInput(email, password)) {
                    signInService.signInUser(email, password, new SignInService.SignInCallback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(SignInActivity.this, "Sign-in successful!", Toast.LENGTH_SHORT).show();
                            // Navigate to main activity
                            Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            // Sign-in failure
                            Toast.makeText(SignInActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    private boolean validateInput(String email, String password) {
        if (email.isEmpty()) {
            emailEditText.setError("Please enter an email address");
            return false;
        }
        if (password.isEmpty()) {
            passwordEditText.setError("Please enter a password");
            return false;
        }
        return true;
    }
}