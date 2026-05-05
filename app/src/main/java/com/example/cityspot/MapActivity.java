package com.example.cityspot;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;

public class MapActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView mapView;
    private Button btnExplore, btnSaved, btnProfile, btnMap;
    private MyLocationNewOverlay mLocationOverlay;
    private Polyline roadOverlay;
    private GeoPoint destinationPoint;
    private String destinationName;
    private Marker infoMarker;
    private boolean routeRequested = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx,
                androidx.preference.PreferenceManager.getDefaultSharedPreferences(ctx));
        // Important for OSRM service
        Configuration.getInstance().setUserAgentValue(getPackageName());

        setContentView(R.layout.activity_map);

        mapView = findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        // Default center: Zamboanga City
        GeoPoint startPoint = new GeoPoint(6.9214, 122.0790);
        mapView.getController().setZoom(14.0);
        mapView.getController().setCenter(startPoint);

        requestPermissionsIfNecessary(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        });

        setupLocationOverlay();
        addDefaultMarkers();

        handleIntent(getIntent());

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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void setupLocationOverlay() {
        mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mapView);
        mLocationOverlay.enableMyLocation();
        mLocationOverlay.enableFollowLocation();
        
        mLocationOverlay.runOnFirstFix(() -> {
            if (destinationPoint != null && !routeRequested) {
                runOnUiThread(() -> {
                    Toast.makeText(MapActivity.this, "GPS Fix obtained. Calculating route...", Toast.LENGTH_SHORT).show();
                    calculateAndDrawRoute();
                });
            }
        });
        
        mapView.getOverlays().add(mLocationOverlay);
    }

    private void handleIntent(Intent intent) {
        if (intent != null && intent.hasExtra("dest_lat") && intent.hasExtra("dest_lon")) {
            double destLat = intent.getDoubleExtra("dest_lat", 0.0);
            double destLon = intent.getDoubleExtra("dest_lon", 0.0);
            destinationName = intent.getStringExtra("dest_name");
            destinationPoint = new GeoPoint(destLat, destLon);
            routeRequested = false;

            mapView.getController().setZoom(15.0);
            mapView.getController().animateTo(destinationPoint);

            addMarker(destLat, destLon, destinationName, "Destination", "Target location");
            
            GeoPoint myLoc = mLocationOverlay.getMyLocation();
            if (myLoc != null) {
                calculateAndDrawRoute();
            } else {
                Toast.makeText(this, "Waiting for GPS location...", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void calculateAndDrawRoute() {
        GeoPoint myLocation = mLocationOverlay.getMyLocation();
        if (myLocation == null || destinationPoint == null) return;

        routeRequested = true;

        if (roadOverlay != null) {
            mapView.getOverlays().remove(roadOverlay);
        }
        if (infoMarker != null) {
            mapView.getOverlays().remove(infoMarker);
        }

        new UpdateRoadTask().execute(myLocation, destinationPoint);
    }

    private class UpdateRoadTask extends AsyncTask<GeoPoint, Void, Road> {
        @Override
        protected Road doInBackground(GeoPoint... params) {
            try {
                RoadManager roadManager = new OSRMRoadManager(MapActivity.this, Configuration.getInstance().getUserAgentValue());
                ArrayList<GeoPoint> waypoints = new ArrayList<>();
                waypoints.add(params[0]);
                waypoints.add(params[1]);
                return roadManager.getRoad(waypoints);
            } catch (Exception e) {
                Log.e("MapActivity", "Routing error", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(Road road) {
            if (road != null && road.mStatus == Road.STATUS_OK) {
                roadOverlay = RoadManager.buildRoadOverlay(road);
                roadOverlay.setColor(Color.BLUE);
                roadOverlay.setWidth(15.0f); // Slightly thicker
                mapView.getOverlays().add(roadOverlay);
                
                // Add info marker at the midpoint
                if (road.mRouteHigh.size() > 0) {
                    infoMarker = new Marker(mapView);
                    GeoPoint midpoint = road.mRouteHigh.get(road.mRouteHigh.size() / 2);
                    infoMarker.setPosition(midpoint);
                    infoMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                    
                    String info = String.format("%.1f km, %.0f min", 
                            road.mLength, road.mDuration/60.0);
                    infoMarker.setTitle(info);
                    infoMarker.showInfoWindow();
                    mapView.getOverlays().add(infoMarker);
                }
                
                mapView.invalidate();
                
                // Zoom out to show both
                ArrayList<GeoPoint> points = new ArrayList<>();
                points.add(mLocationOverlay.getMyLocation());
                points.add(destinationPoint);
                zoomToFitPoints(points);

            } else {
                String error = (road == null) ? "Network error" : "Route status: " + road.mStatus;
                Toast.makeText(MapActivity.this, "Could not find route: " + error, Toast.LENGTH_SHORT).show();
                routeRequested = false; // Allow retry
            }
        }
    }

    private void zoomToFitPoints(ArrayList<GeoPoint> points) {
        if (points.size() < 2) return;
        double minLat = Double.MAX_VALUE, maxLat = -Double.MAX_VALUE;
        double minLon = Double.MAX_VALUE, maxLon = -Double.MAX_VALUE;

        for (GeoPoint p : points) {
            if (p == null) continue;
            minLat = Math.min(minLat, p.getLatitude());
            maxLat = Math.max(maxLat, p.getLatitude());
            minLon = Math.min(minLon, p.getLongitude());
            maxLon = Math.max(maxLon, p.getLongitude());
        }

        try {
            BoundingBox bb = new BoundingBox(maxLat + 0.005, maxLon + 0.005, minLat - 0.005, minLon - 0.005);
            mapView.zoomToBoundingBox(bb, true);
        } catch (Exception e) {
            // Fallback if bounding box fails
        }
    }

    private void addDefaultMarkers() {
        addMarker(6.901364906320903, 122.0832222706528, "Fort Pilar", "Historic Spanish-era fort", "");
        addMarker(6.8729579146675475, 122.05844877432924, "Santa Cruz Island", "Famous pink sand beach", "");
        addMarker(6.900773315389889, 122.08126672442509, "Paseo Del Mar", "Popular seaside park", "");
        addMarker(6.925108906106171, 122.02221645908409, "Yakan Weaving Village", "Traditional crafts", "");
        addMarker(6.952673974078125, 122.07474407150936, "Pasonanca Park", "Cool mountain park", "");
    }

    private void addMarker(double lat, double lon, String title, String snippet, String description) {
        Marker marker = new Marker(mapView);
        marker.setPosition(new GeoPoint(lat, lon));
        marker.setTitle(title);
        marker.setSnippet(snippet);
        marker.setSubDescription(description);

        Drawable icon = ContextCompat.getDrawable(this, org.osmdroid.library.R.drawable.marker_default);
        if (icon != null) {
            icon.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
            marker.setIcon(icon);
        }
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mapView.getOverlays().add(marker);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (mLocationOverlay != null) {
                mLocationOverlay.enableMyLocation();
                mLocationOverlay.enableFollowLocation();
            }
        }
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toArray(new String[0]), REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        if (mLocationOverlay != null) mLocationOverlay.enableMyLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        if (mLocationOverlay != null) mLocationOverlay.disableMyLocation();
    }
}