package com.example.cityspot;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
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

        addMarker(6.901364906320903, 122.0832222706528, "Fort Pilar",
                "Pilar St, Zamboanga City",
                "Historic Spanish-era fort and shrine dedicated to Our Lady of the Pillar");

        addMarker(6.8729579146675475, 122.05844877432924, "Santa Cruz Island",
                "Great Santa Cruz Island, Zamboanga City",
                "Famous pink sand beach and protected island destination");

        addMarker(6.900773315389889, 122.08126672442509, "Paseo Del Mar",
                "Paseo del Mar, Zamboanga City",
                "Popular seaside park with sunset views, food stalls, and cultural shows");

        addMarker(7.120713444659594, 122.27011291141665, "Once Islas",
                "Barangay Panubigan, Zamboanga City",
                "Group of scenic islands known for white sand beaches and clear waters");

        addMarker(6.952673974078125, 122.07474407150936, "Pasonanca Park",
                "Pasonanca, Zamboanga City",
                "Cool mountain park with pools, tree houses, and picnic areas");

        addMarker(7.3103435668858685, 122.21349478209761, "Merloquet Falls",
                "Sibulao, Zamboanga City",
                "Beautiful cascading waterfall surrounded by lush forest");

        addMarker(6.965572885339477, 122.06122819559002, "Lantawan Grassland",
                "Upper Pasonanca, Zamboanga City",
                "Open grassland viewpoint offering scenic mountain views");

        addMarker(6.964790335402858, 122.07612028024901, "Abong-Abong Park",
                "Pasonanca, Zamboanga City",
                "Hilltop pilgrimage site with stations of the cross and city views");

        addMarker(6.925108906106171, 122.02221645908409, "Yakan Weaving Village",
                "Upper Calarian, Zamboanga City",
                "Cultural village showcasing traditional Yakan weaving and crafts");

        addMarker(6.952771635760352, 122.18126096306126, "Taluksangay Mosque",
                "Taluksangay, Zamboanga City",
                "One of the oldest mosques in Western Mindanao with rich Islamic heritage");

        addMarker(6.904346115853981, 122.07616608024884, "Zamboanga City Hall",
                "Valderrosa St, Zamboanga City",
                "Government building known for its classic colonial architecture");

        addMarker(6.920335553163613, 122.07343661334465, "KCC Mall de Zamboanga",
                "Gov. Camins Ave, Zamboanga City",
                "Large shopping mall with dining, shopping, and entertainment options");

        addMarker(6.908587597462885, 122.07592563907839, "SM Mindpro",
                "La Purisima St, Zamboanga City",
                "Modern shopping mall with retail stores, cinema, and restaurants");

        addMarker(6.902609454881635, 122.08459486675447, "Zamboanga City Bird Sanctuary",
                "Pasonanca, Zamboanga City",
                "Protected area for birds and wildlife ideal for nature lovers");

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

    private void addMarker(double lat, double lon, String title, String snippet, String description) {
        Marker marker = new Marker(mapView);
        marker.setPosition(new GeoPoint(lat, lon));
        marker.setTitle(title);
        marker.setSnippet(snippet);
        marker.setSubDescription(description);

        // Change marker color to red
        Drawable icon = marker.getIcon();
        if (icon != null) {
            icon.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
            marker.setIcon(icon);
        }
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

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