package com.example.cityspot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ExploreActivity extends AppCompatActivity {

    private Button btnExplore, btnSaved, btnProfile, btnMap;
    private TextView txtExploreTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        btnExplore = findViewById(R.id.btnExplore);
        btnSaved = findViewById(R.id.btnSaved);
        btnProfile = findViewById(R.id.btnProfile);
        btnMap = findViewById(R.id.btnMap);
        txtExploreTitle = findViewById(R.id.exploreTitle);

        setupTrailClicks();

        String username = getIntent().getStringExtra("username");
        if (username != null && !username.isEmpty()) {
            txtExploreTitle.setText("Welcome, " + username + "!");
        }

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