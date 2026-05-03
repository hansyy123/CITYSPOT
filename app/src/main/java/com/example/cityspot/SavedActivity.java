package com.example.cityspot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class SavedActivity extends AppCompatActivity {

    private Button btnExplore, btnSaved, btnProfile, btnMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);

        btnExplore = findViewById(R.id.btnExplore);
        btnSaved = findViewById(R.id.btnSaved);
        btnProfile = findViewById(R.id.btnProfile);
        btnMap = findViewById(R.id.btnMap);

        btnExplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SavedActivity.this, ExploreActivity.class));
                overridePendingTransition(0, 0);
            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SavedActivity.this, ProfileActivity.class));
                overridePendingTransition(0, 0);
            }
        });

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SavedActivity.this, MapActivity.class));
                overridePendingTransition(0, 0);
            }
        });
    }
}