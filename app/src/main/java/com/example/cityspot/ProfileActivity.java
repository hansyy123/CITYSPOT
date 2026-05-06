package com.example.cityspot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileActivity extends AppCompatActivity {

    private Button btnLogout, btnEditProfile, btnAboutUs, btnSaved, btnProfile, btnMap, btnExplore;
    private TextView txtUsername, txtSavedCount, txtVisitedCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        txtUsername = findViewById(R.id.txtUsername);
        txtSavedCount = findViewById(R.id.txtSavedCount);
        txtVisitedCount = findViewById(R.id.txtVisitedCount);
        btnLogout = findViewById(R.id.btnLogout);
        btnEditProfile = findViewById(R.id.editProfileButton);
        btnAboutUs = findViewById(R.id.aboutUsButton);
        btnSaved = findViewById(R.id.btnSaved);
        btnProfile = findViewById(R.id.btnProfile);
        btnMap = findViewById(R.id.btnMap);
        btnExplore = findViewById(R.id.btnExplore);

        updateProfileInfo();

        // Logout button
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ProfileActivity.this, "Logged out", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        // Navigate to EditProfile
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });

        // Navigate to About Us
        btnAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, AboutUsActivity.class);
                startActivity(intent);
            }
        });

        btnSaved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, SavedActivity.class));
                overridePendingTransition(0, 0);
            }
        });

        btnExplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, ExploreActivity.class));
                overridePendingTransition(0, 0);
            }
        });

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, MapActivity.class));
                overridePendingTransition(0, 0);
            }
        });
    }

    private void updateProfileInfo() {
        SharedPreferences prefs = getSharedPreferences("CitySpotPrefs", MODE_PRIVATE);
        String username = prefs.getString("username", "User");
        txtUsername.setText(username);

        // Update Saved Count
        java.util.Set<String> savedTrails = prefs.getStringSet("saved_trails", new java.util.HashSet<>());
        txtSavedCount.setText(String.valueOf(savedTrails.size()));

        // Update Visited Count
        int visitedCount = prefs.getInt("visited_count", 0);
        txtVisitedCount.setText(String.valueOf(visitedCount));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateProfileInfo(); // Update info when returning from EditProfile
    }
}
