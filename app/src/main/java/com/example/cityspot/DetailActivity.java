package com.example.cityspot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ImageButton btnBack = findViewById(R.id.btnBack);
        TextView txtName = findViewById(R.id.txtDetailTrailName);
        TextView txtSubInfo = findViewById(R.id.txtDetailSubInfo);
        TextView txtDistance = findViewById(R.id.txtDetailDistance);
        TextView txtElevation = findViewById(R.id.txtDetailElevation);
        TextView txtTime = findViewById(R.id.txtDetailTime);

        // Get data from intent
        String name = getIntent().getStringExtra("trail_name");
        String subInfo = getIntent().getStringExtra("trail_sub_info");
        String distance = getIntent().getStringExtra("trail_distance");
        
        // These are passed as doubles from ExploreActivity now
        double lat = getIntent().getDoubleExtra("trail_lat", 0.0);
        double lon = getIntent().getDoubleExtra("trail_lon", 0.0);

        if (name != null) txtName.setText(name);
        if (subInfo != null) txtSubInfo.setText(subInfo);
        if (distance != null) txtDistance.setText(distance);
        
        // Display coordinates in the stats row for reference
        txtElevation.setText(String.format("%.4f", lat));
        txtTime.setText(String.format("%.4f", lon));

        btnBack.setOnClickListener(v -> finish());

        findViewById(R.id.btnGetDirections).setOnClickListener(v -> {
            Intent intentMap = new Intent(DetailActivity.this, MapActivity.class);
            intentMap.putExtra("dest_lat", lat);
            intentMap.putExtra("dest_lon", lon);
            intentMap.putExtra("dest_name", name);
            // Flag to clear top so we don't keep piling up activities
            intentMap.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intentMap);
        });
    }
}