package com.jamith.booksformeseller.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jamith.booksformeseller.R;
import com.jamith.booksformeseller.activity.fragments.HomeFragment;
import com.jamith.booksformeseller.activity.fragments.InventoryFragment;
import com.jamith.booksformeseller.activity.fragments.OrderFragment;
import com.jamith.booksformeseller.activity.fragments.ProfileFragment;

public class HomeActivity extends AppCompatActivity {
    private FrameLayout fragmentContainer;
    private BottomNavigationView bottomNavigationView;

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

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        // Set up bottom navigation listener
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                loadFragment(new HomeFragment());
            } else if (itemId == R.id.nav_inventory) {
                loadFragment(new InventoryFragment());
            } else if (itemId == R.id.nav_orders) {
                loadFragment(new OrderFragment());
            } else if (itemId == R.id.nav_profile){
                loadFragment(new ProfileFragment());
            }

            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}