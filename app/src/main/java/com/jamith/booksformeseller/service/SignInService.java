package com.jamith.booksformeseller.service;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class SignInService {
    private FirebaseAuth mAuth;
    public SignInService() {
        mAuth = FirebaseAuth.getInstance();
    }

    public void signInUser(String email, String password, final SignInCallback callback) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("FirebaseAuth", "signInWithEmail:success");
                            callback.onSuccess();
                        } else {
                            Log.w("FirebaseAuth", "signInWithEmail:failure", task.getException());
                            handleSignInError(task.getException(), callback);
                        }
                    }
                });
    }

    private void handleSignInError(Exception exception, SignInCallback callback) {
        String errorMessage;
        if (exception instanceof FirebaseAuthInvalidUserException) {
            errorMessage = "No account found with this email address.";
        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            errorMessage = "Invalid email or password.";
        } else if (exception instanceof FirebaseNetworkException) {
            errorMessage = "Network error. Please check your internet connection.";
        } else {
            errorMessage = "Authentication failed. Please try again.";
        }
        callback.onFailure(errorMessage);
    }

    public interface SignInCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }
}