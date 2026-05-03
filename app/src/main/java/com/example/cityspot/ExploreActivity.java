package com.example.cityspot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ExploreActivity extends AppCompatActivity {

    private Button btnExplore, btnSaved, btnProfile;
    private TextView txtExploreTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        btnExplore = findViewById(R.id.btnExplore);
        btnSaved = findViewById(R.id.btnSaved);
        btnProfile = findViewById(R.id.btnProfile);
        txtExploreTitle = findViewById(R.id.exploreTitle);

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
    }
}