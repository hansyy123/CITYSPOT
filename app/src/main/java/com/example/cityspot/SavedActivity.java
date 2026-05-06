package com.example.cityspot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashSet;
import java.util.Set;

public class SavedActivity extends AppCompatActivity {

    private Button btnExplore, btnSaved, btnProfile, btnMap;
    private LinearLayout savedContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);

        savedContainer = findViewById(R.id.savedContainer);
        btnExplore = findViewById(R.id.btnExplore);
        btnSaved = findViewById(R.id.btnSaved);
        btnProfile = findViewById(R.id.btnProfile);
        btnMap = findViewById(R.id.btnMap);

        loadSavedTrails();

        btnExplore.setOnClickListener(v -> {
            startActivity(new Intent(SavedActivity.this, ExploreActivity.class));
            overridePendingTransition(0, 0);
        });

        btnProfile.setOnClickListener(v -> {
            startActivity(new Intent(SavedActivity.this, ProfileActivity.class));
            overridePendingTransition(0, 0);
        });

        btnMap.setOnClickListener(v -> {
            startActivity(new Intent(SavedActivity.this, MapActivity.class));
            overridePendingTransition(0, 0);
        });
    }

    private void loadSavedTrails() {
        SharedPreferences prefs = getSharedPreferences("CitySpotPrefs", MODE_PRIVATE);
        Set<String> savedTrails = prefs.getStringSet("saved_trails", new HashSet<>());

        if (savedTrails.isEmpty()) {
            return;
        }

        savedContainer.removeAllViews();

        for (String trail : savedTrails) {
            String[] parts = trail.split("\\|");
            if (parts.length >= 3) {
                String name = parts[0];
                String sub = parts[1];
                String stats = parts[2];
                String lat = parts.length > 3 ? parts[3] : "0.0";
                String lon = parts.length > 4 ? parts[4] : "0.0";
                int imageResId = parts.length > 5 ? Integer.parseInt(parts[5]) : R.drawable.img;
                
                addSavedTrailView(name, sub, stats, lat, lon, imageResId);
            }
        }
    }

    private void addSavedTrailView(String name, String sub, String stats, String lat, String lon, int imageResId) {
        View card = LayoutInflater.from(this).inflate(R.layout.item_saved_trail, savedContainer, false);
        
        TextView txtName = card.findViewById(R.id.txtSavedName);
        TextView txtSub = card.findViewById(R.id.txtSavedSub);
        TextView txtStats = card.findViewById(R.id.txtSavedStats);
        ImageView imgThumbnail = card.findViewById(R.id.imgSavedThumbnail);
        ImageView btnDelete = card.findViewById(R.id.btnDeleteSaved);

        txtName.setText(name);
        txtSub.setText(sub);
        txtStats.setText(stats);
        imgThumbnail.setImageResource(imageResId);

        // Click to open DetailActivity
        card.setOnClickListener(v -> {
            // Update Visited Count
            incrementVisitedCount();
            
            Intent intent = new Intent(SavedActivity.this, DetailActivity.class);
            intent.putExtra("trail_name", name);
            intent.putExtra("trail_sub_info", sub);
            intent.putExtra("trail_distance", stats);
            intent.putExtra("trail_lat", Double.parseDouble(lat));
            intent.putExtra("trail_lon", Double.parseDouble(lon));
            intent.putExtra("trail_image", imageResId);
            startActivity(intent);
        });

        btnDelete.setOnClickListener(v -> {
            removeSavedTrail(name + "|" + sub + "|" + stats + "|" + lat + "|" + lon + "|" + imageResId);
        });

        savedContainer.addView(card);
    }

    private void removeSavedTrail(String trailData) {
        SharedPreferences prefs = getSharedPreferences("CitySpotPrefs", MODE_PRIVATE);
        Set<String> savedTrails = prefs.getStringSet("saved_trails", new HashSet<>());
        Set<String> newSavedTrails = new HashSet<>(savedTrails);
        
        if (newSavedTrails.remove(trailData)) {
            prefs.edit().putStringSet("saved_trails", newSavedTrails).apply();
            loadSavedTrails(); // Refresh the list
            Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show();
        }
    }

    private void incrementVisitedCount() {
        SharedPreferences prefs = getSharedPreferences("CitySpotPrefs", MODE_PRIVATE);
        int visitedCount = prefs.getInt("visited_count", 0);
        prefs.edit().putInt("visited_count", visitedCount + 1).apply();
    }
}