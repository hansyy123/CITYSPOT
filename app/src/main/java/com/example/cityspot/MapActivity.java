package com.example.cityspot;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class MapActivity extends AppCompatActivity {

    private MapView mapView;
    private Button btnExplore, btnSaved, btnProfile, btnMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx,
                androidx.preference.PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.activity_map);

        mapView = findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        GeoPoint zamboanga = new GeoPoint(6.9214, 122.0790);
        mapView.getController().setZoom(14.0);
        mapView.getController().setCenter(zamboanga);

        addMarker(6.9174, 122.0754, "KCC Mall de Zamboanga");
        addMarker(6.9095, 122.0722, "ADZU");
        addMarker(6.9400, 122.0488, "Pasonanca Park");

        // Bottom Navigation Setup
        btnExplore = findViewById(R.id.btnExplore);
        btnSaved = findViewById(R.id.btnSaved);
        btnProfile = findViewById(R.id.btnProfile);
        btnMap = findViewById(R.id.btnMap);

        btnExplore.setOnClickListener(v -> {
            startActivity(new Intent(MapActivity.this, ExploreActivity.class));
            overridePendingTransition(0, 0);
        });

        btnSaved.setOnClickListener(v -> {
            startActivity(new Intent(MapActivity.this, SavedActivity.class));
            overridePendingTransition(0, 0);
        });

        btnProfile.setOnClickListener(v -> {
            startActivity(new Intent(MapActivity.this, ProfileActivity.class));
            overridePendingTransition(0, 0);
        });
    }

    private void addMarker(double lat, double lon, String title) {
        Marker marker = new Marker(mapView);
        marker.setPosition(new GeoPoint(lat, lon));
        marker.setTitle(title);
        mapView.getOverlays().add(marker);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }
}