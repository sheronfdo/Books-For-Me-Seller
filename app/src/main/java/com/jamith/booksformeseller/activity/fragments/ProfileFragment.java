package com.jamith.booksformeseller.activity.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.jamith.booksformeseller.R;
import com.jamith.booksformeseller.activity.HomeActivity;
import com.jamith.booksformeseller.activity.MainActivity;
import com.jamith.booksformeseller.activity.ProfileInfoActivity;


public class ProfileFragment extends Fragment {
    HomeActivity homeActivity;
    Button logoutButton, profileInfoButton;
    ImageView imageView;
    TextView textView;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        imageView = view.findViewById(R.id.imageView);
        textView = view.findViewById(R.id.textView);
        loadProfileData();
        homeActivity = (HomeActivity) getActivity();
        logoutButton = view.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> {
            // Perform logout logic
            FirebaseAuth.getInstance().signOut();

            // Navigate to the login screen
            Intent intent = new Intent(homeActivity, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
        profileInfoButton = view.findViewById(R.id.profileInfoButton);
        profileInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(homeActivity, ProfileInfoActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    private void loadProfileData() {
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            DocumentReference userRef = db.collection("sellers").document(userId);
            userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (value != null && value.exists()) {
                        String imageUrl = value.getString("imageUrl");
                        Log.d("image url", imageUrl);
                        Glide.with(requireContext())
                                .load(imageUrl)
                                .placeholder(R.drawable.profile)
                                .into(imageView);
                        String fullName = value.getString("fullNameOrRepresentative");
                        textView.setText(fullName);
                    }
                }
            });
        }
    }
}