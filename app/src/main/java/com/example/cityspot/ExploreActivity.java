package com.example.cityspot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashSet;
import java.util.Set;

public class ExploreActivity extends AppCompatActivity {

    private Button btnExplore, btnSaved, btnProfile, btnMap;
    private TextView txtExploreTitle;
    private EditText editSearch;
    private View card1, card2, card3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        btnExplore = findViewById(R.id.btnExplore);
        btnSaved = findViewById(R.id.btnSaved);
        btnProfile = findViewById(R.id.btnProfile);
        btnMap = findViewById(R.id.btnMap);
        txtExploreTitle = findViewById(R.id.exploreTitle);
        editSearch = findViewById(R.id.editSearch);
        
        card1 = findViewById(R.id.cardTrail1);
        card2 = findViewById(R.id.cardTrail2);
        card3 = findViewById(R.id.cardTrail3);

        setupTrailClicks();
        setupSaveButtons();
        setupSearch();

        updateWelcomeMessage();

        btnSaved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ExploreActivity.this, SavedActivity.class));
                overridePendingTransition(0, 0);
            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ExploreActivity.this, ProfileActivity.class));
                overridePendingTransition(0, 0);
            }
        });

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ExploreActivity.this, MapActivity.class));
                overridePendingTransition(0, 0);
            }
        });
    }

    private void setupSearch() {
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterTrails(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterTrails(String query) {
        // Names of the trails to check against
        String name1 = "Castleton, Mam Tor, and Great Ridge".toLowerCase();
        String name2 = "Mount Pulunbato Loop".toLowerCase();
        String name3 = "Sibugay Peak Trail".toLowerCase();

        // Show/Hide based on match
        card1.setVisibility(name1.contains(query) ? View.VISIBLE : View.GONE);
        card2.setVisibility(name2.contains(query) ? View.VISIBLE : View.GONE);
        card3.setVisibility(name3.contains(query) ? View.VISIBLE : View.GONE);
    }

    private void setupSaveButtons() {
        ImageButton save1 = findViewById(R.id.btnSaveTrail1);
        ImageButton save2 = findViewById(R.id.btnSaveTrail2);
        ImageButton save3 = findViewById(R.id.btnSaveTrail3);

        save1.setOnClickListener(v -> saveTrail("Castleton, Mam Tor, and Great Ridge|4.7 • Moderate • Peak District National Park|12.9 km • 639 m • 4 hr"));
        save2.setOnClickListener(v -> saveTrail("Mount Pulunbato Loop|3.7 • Moderate • Zamboanga City|18 km • 450 m • 4h 17m"));
        save3.setOnClickListener(v -> saveTrail("Sibugay Peak Trail|4.2 • Hard • Ipil, Zamboanga Sibugay|8.5 km • 720 m • 5 hr"));
    }

    private void saveTrail(String trailData) {
        SharedPreferences prefs = getSharedPreferences("CitySpotPrefs", MODE_PRIVATE);
        Set<String> savedTrails = prefs.getStringSet("saved_trails", new HashSet<>());
        
        // Use a copy because the returned set shouldn't be modified
        Set<String> newSavedTrails = new HashSet<>(savedTrails);
        if (newSavedTrails.add(trailData)) {
            prefs.edit().putStringSet("saved_trails", newSavedTrails).apply();
            Toast.makeText(this, "Saved to favorites!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Already in favorites!", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupTrailClicks() {
        // Trail 1
        View.OnClickListener click1 = v -> openDetail(
                "Castleton, Mam Tor, and Great Ridge",
                "4.7 • Moderate • Peak District National Park",
                "12.9 km", "639 m", "4 hr"
        );
        findViewById(R.id.cardTrail1).setOnClickListener(click1);
        findViewById(R.id.imgContainer1).setOnClickListener(click1);

        // Trail 2
        View.OnClickListener click2 = v -> openDetail(
                "Mount Pulunbato Loop",
                "3.7 • Moderate • Zamboanga City",
                "18 km", "450 m", "4h 17m"
        );
        findViewById(R.id.cardTrail2).setOnClickListener(click2);
        findViewById(R.id.imgContainer2).setOnClickListener(click2);

        // Trail 3
        View.OnClickListener click3 = v -> openDetail(
                "Sibugay Peak Trail",
                "4.2 • Hard • Ipil, Zamboanga Sibugay",
                "8.5 km", "720 m", "5 hr"
        );
        findViewById(R.id.cardTrail3).setOnClickListener(click3);
        findViewById(R.id.imgContainer3).setOnClickListener(click3);
    }

    private void updateWelcomeMessage() {
        SharedPreferences prefs = getSharedPreferences("CitySpotPrefs", MODE_PRIVATE);
        String username = prefs.getString("username", "User");
        txtExploreTitle.setText("Welcome, " + username + "!");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update the welcome message in case the username was changed in EditProfileActivity
        updateWelcomeMessage();
    }

    private void openDetail(String name, String sub, String dist, String elev, String time) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("trail_name", name);
        intent.putExtra("trail_sub_info", sub);
        intent.putExtra("trail_distance", dist);
        intent.putExtra("trail_elevation", elev);
        intent.putExtra("trail_time", time);
        startActivity(intent);
    }
}