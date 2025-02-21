package com.jamith.booksformeseller.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.messaging.FirebaseMessaging;
import com.jamith.booksformeseller.R;
import com.jamith.booksformeseller.activity.fragments.HomeFragment;
import com.jamith.booksformeseller.activity.fragments.InventoryFragment;
import com.jamith.booksformeseller.activity.fragments.OrderFragment;
import com.jamith.booksformeseller.activity.fragments.ProfileFragment;
import com.jamith.booksformeseller.activity.fragments.ProfileInfoFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class HomeActivity extends AppCompatActivity {
    private FrameLayout fragmentContainer;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;
    private ImageView navHeaderImage;
    private TextView navHeaderName;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        fragmentContainer = findViewById(R.id.fragmentContainer);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        View headerView = navigationView.getHeaderView(0);
        navHeaderImage = headerView.findViewById(R.id.nav_profile_image);
        navHeaderName = headerView.findViewById(R.id.nav_profile_name);

        loadProfileData();

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                loadFragment(new HomeFragment());
            } else if (itemId == R.id.nav_inventory) {
                loadFragment(new InventoryFragment());
            } else if (itemId == R.id.nav_orders) {
                loadFragment(new OrderFragment());
            } else if (itemId == R.id.nav_profile){
//                loadFragment(new ProfileFragment());
                if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }

            return true;
        });
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_profile) {
                loadFragment(new ProfileInfoFragment());
            } else if (item.getItemId() == R.id.nav_logout) {
                firebaseAuth.signOut();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private final long IMAGE_EXPIRY_DURATION = 24 * 60 * 60 * 1000;
    private void loadProfileData() {
        if (firebaseAuth.getCurrentUser() != null) {
            String userId = firebaseAuth.getCurrentUser().getUid();
            DocumentReference userRef = firebaseFirestore.collection("sellers").document(userId);
            userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (value != null && value.exists()) {
                        String imageUrl = value.getString("imageUrl");
                        String fullName = value.getString("fullNameOrRepresentative");
                        navHeaderName.setText(fullName);

                        File profileImageFile = new File(getFilesDir(), "profile.jpg");
                        SharedPreferences prefs = getSharedPreferences("ProfilePrefs", MODE_PRIVATE);
                        long lastUpdatedTime = prefs.getLong("profile_image_timestamp", 0);
                        long currentTime = System.currentTimeMillis();

                        if (profileImageFile.exists() && (currentTime - lastUpdatedTime) < IMAGE_EXPIRY_DURATION) {
                            Log.d("using_cache", "from storage");
                            Bitmap bitmap = BitmapFactory.decodeFile(profileImageFile.getAbsolutePath());
                            navHeaderImage.setImageBitmap(bitmap);
                        } else {
                            Log.d("using_live", "from firebase");
                            downloadAndCacheImage(imageUrl, profileImageFile);
                        }
                    }
                }
            });
        }
    }

    private void downloadAndCacheImage(String imageUrl, File file) {
        Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .placeholder(R.drawable.profile)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        navHeaderImage.setImageBitmap(resource);
                        saveImageToInternalStorage(resource, file);
                        SharedPreferences prefs = getSharedPreferences("ProfilePrefs", MODE_PRIVATE);
                        prefs.edit().putLong("profile_image_timestamp", System.currentTimeMillis()).apply();
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {}
                });
    }

    private void saveImageToInternalStorage(Bitmap bitmap, File file) {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    public void setFragmentHome(){
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }

}