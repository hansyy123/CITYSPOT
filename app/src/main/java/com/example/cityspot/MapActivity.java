package com.example.cityspot;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import org.json.JSONArray;
import org.json.JSONObject;
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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class MapActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView mapView;
    private Button btnExplore, btnSaved, btnProfile, btnMap;
    private MyLocationNewOverlay mLocationOverlay;
    private Polyline currentRouteLine;
    private CardView cardDistanceOverlay;
    private TextView tvOverlayDistance;
    private MaterialCardView cardSearchBar, cardRouteSelection;
    private EditText etOriginMap, etDestMap;
    private TextView tvSearchPlaceholder;
    private ImageView ivCloseRoute;
    private final Map<String, GeoPoint> locationPoints = new HashMap<>();
    private GeoPoint destinationPoint;
    private String destinationName;
    private Marker infoMarker, routeLabelMarker;
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

        cardSearchBar = findViewById(R.id.cardSearchBar);
        cardRouteSelection = findViewById(R.id.cardRouteSelection);
        tvSearchPlaceholder = findViewById(R.id.tvSearchPlaceholder);
        ivCloseRoute = findViewById(R.id.ivCloseRoute);
        etOriginMap = findViewById(R.id.etOriginMap);
        etDestMap = findViewById(R.id.etDestMap);

        if (cardSearchBar != null) cardSearchBar.setOnClickListener(v -> showLocationPickerDialog(null));
        if (tvSearchPlaceholder != null) tvSearchPlaceholder.setOnClickListener(v -> showLocationPickerDialog(null));

        if (ivCloseRoute != null) {
            ivCloseRoute.setOnClickListener(v -> {
                cardRouteSelection.setVisibility(View.GONE);
                cardSearchBar.setVisibility(View.VISIBLE);
                if (currentRouteLine != null) {
                    mapView.getOverlays().remove(currentRouteLine);
                    currentRouteLine = null;
                }
                if (routeLabelMarker != null) {
                    mapView.getOverlays().remove(routeLabelMarker);
                    routeLabelMarker = null;
                }
                cardDistanceOverlay.setVisibility(View.GONE);
                mapView.invalidate();
            });
        }

        if (etOriginMap != null) {
            etOriginMap.setOnClickListener(v -> showLocationPickerDialog(etOriginMap));
        }

        if (etDestMap != null) {
            etDestMap.setOnClickListener(v -> showLocationPickerDialog(etDestMap));
        }

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

        cardDistanceOverlay = findViewById(R.id.cardDistanceOverlay);
        tvOverlayDistance = findViewById(R.id.tvOverlayDistance);
        
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

            prepareRoute(destinationPoint, destinationName);
            
            GeoPoint myLoc = mLocationOverlay.getMyLocation();
            if (myLoc != null) {
                calculateAndDrawRoute();
            } else {
                Toast.makeText(this, "Waiting for GPS location...", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void prepareRoute(GeoPoint destination, String destName) {
        this.destinationPoint = destination;
        this.destinationName = destName;
        if (cardSearchBar != null) cardSearchBar.setVisibility(View.GONE);
        if (cardRouteSelection != null) cardRouteSelection.setVisibility(View.VISIBLE);
        if (etDestMap != null) etDestMap.setText(destName);
        if (etOriginMap != null) etOriginMap.setText("Current Location");
    }

    private void calculateAndDrawRoute() {
        GeoPoint myLocation = mLocationOverlay.getMyLocation();
        if (myLocation == null) {
            Toast.makeText(this, "Waiting for current location fix...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destinationPoint == null) return;

        routeRequested = true;
        drawRoute(myLocation, destinationPoint);
    }

    private void drawRoute(GeoPoint start, GeoPoint end) {
        new Thread(() -> {
            RoadManager roadManager = new OSRMRoadManager(this, getPackageName());
            ArrayList<GeoPoint> waypoints = new ArrayList<>();
            waypoints.add(start);
            waypoints.add(end);

            Road road = roadManager.getRoad(waypoints);

            runOnUiThread(() -> {
                if (currentRouteLine != null) {
                    mapView.getOverlays().remove(currentRouteLine);
                }
                if (routeLabelMarker != null) {
                    mapView.getOverlays().remove(routeLabelMarker);
                }

                if (road.mStatus == Road.STATUS_OK) {
                    currentRouteLine = RoadManager.buildRoadOverlay(road);
                    currentRouteLine.setColor(Color.parseColor("#3F51B5"));
                    currentRouteLine.setWidth(18.0f);

                    mapView.getOverlays().add(currentRouteLine);
                    
                    addRouteLabelMarker(road);

                    if (cardDistanceOverlay != null) {
                        cardDistanceOverlay.setVisibility(View.VISIBLE);
                    }
                    if (tvOverlayDistance != null) {
                        double durationMin = road.mDuration / 60.0;
                        tvOverlayDistance.setText(String.format(Locale.US, "Distance: %.2f km (%.0f min)", road.mLength, durationMin));
                    }
                    mapView.getController().animateTo(end);
                    zoomToFitPoints(road.mRouteHigh);
                } else {
                    drawDirectLine(start, end);
                }
                mapView.invalidate();
            });
        }).start();
    }

    private void addRouteLabelMarker(Road road) {
        if (road.mRouteHigh.size() < 2) return;
        
        GeoPoint midPoint = road.mRouteHigh.get(road.mRouteHigh.size() / 2);
        routeLabelMarker = new Marker(mapView);
        routeLabelMarker.setPosition(midPoint);
        routeLabelMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        
        String timeStr = String.format(Locale.US, "%.0f min", road.mDuration / 60.0);
        String distStr = String.format(Locale.US, "%.1f km", road.mLength);
        
        routeLabelMarker.setIcon(new BitmapDrawable(getResources(), createLabelBitmap(timeStr, distStr)));
        mapView.getOverlays().add(routeLabelMarker);
    }

    private Bitmap createLabelBitmap(String time, String distance) {
        TextView tv = new TextView(this);
        String content = "<b>" + time + "</b><br/><small>" + distance + "</small>";
        tv.setText(Html.fromHtml(content, Html.FROM_HTML_MODE_COMPACT));
        tv.setTextColor(Color.BLACK);
        tv.setGravity(android.view.Gravity.CENTER);
        tv.setPadding(20, 10, 20, 10);
        
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.WHITE);
        bg.setCornerRadius(15f);
        bg.setStroke(3, Color.parseColor("#3F51B5"));
        tv.setBackground(bg);

        tv.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        tv.layout(0, 0, tv.getMeasuredWidth(), tv.getMeasuredHeight());
        
        Bitmap bitmap = Bitmap.createBitmap(tv.getMeasuredWidth(), tv.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        tv.draw(canvas);
        return bitmap;
    }

    private void drawDirectLine(GeoPoint start, GeoPoint end) {
        Toast.makeText(MapActivity.this, "Road route not found. Drawing direct line.", Toast.LENGTH_SHORT).show();
        currentRouteLine = new Polyline();
        currentRouteLine.addPoint(start);
        currentRouteLine.addPoint(end);
        currentRouteLine.setColor(Color.parseColor("#FF9800"));
        currentRouteLine.setWidth(10.0f);
        mapView.getOverlays().add(currentRouteLine);
        
        float[] results = new float[1];
        Location.distanceBetween(start.getLatitude(), start.getLongitude(),
                end.getLatitude(), end.getLongitude(), results);
        
        if (tvOverlayDistance != null) {
            tvOverlayDistance.setText(String.format(Locale.US, "Distance: %.2f km (Direct)", results[0] / 1000));
        }
        if (cardDistanceOverlay != null) {
            cardDistanceOverlay.setVisibility(View.VISIBLE);
        }
        mapView.getController().animateTo(end);
        
        ArrayList<GeoPoint> points = new ArrayList<>();
        points.add(start);
        points.add(end);
        zoomToFitPoints(points);
    }

    private List<GeoPoint> decodePolyline(String encoded) {
        List<GeoPoint> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            GeoPoint p = new GeoPoint((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
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
        addMarker(6.901364906320903, 122.0832222706528, "Fort Pilar", "Zamboanga City", "Historic Spanish fort and shrine");
        addMarker(6.8729579146675475, 122.05844877432924, "Santa Cruz Island", "Zamboanga City", "Famous pink sand beach island");
        addMarker(6.900773315389889, 122.08126672442509, "Paseo del Mar", "Zamboanga City", "Waterfront park with sunset views");
        addMarker(7.120713444659594, 122.27011291141665, "Once Islas", "Zamboanga City", "Group of scenic islands for island hopping");
        addMarker(7.3103435668858685, 122.21349478209761, "Merloquet Falls", "Zamboanga City", "Beautiful cascading waterfall");
        addMarker(6.965572885339477, 122.06122819559002, "Lantawan Grassland", "Zamboanga City", "Open grassland with mountain views");
        addMarker(6.925108906106171, 122.02221645908409, "Yakan Weaving Village", "Zamboanga City", "Traditional weaving and crafts village");
        addMarker(6.904346115853981, 122.07616608024884, "Zamboanga City Hall", "Zamboanga City", "Main government building");
        addMarker(6.907358369240467, 122.06851645586336, "R.T. Lim Boulevard", "Zamboanga City", "Popular seaside boulevard and viewing deck");
        addMarker(6.901007470878223, 122.08141738469901, "National Museum Fort Pilar", "Zamboanga City", "Museum showcasing regional history");
        addMarker(7.151025145289209, 122.17878779202081, "Dulian Falls", "Zamboanga City", "Hidden natural waterfall attraction");
        addMarker(6.9225545744548524, 122.03788756253151, "Zamboanga Golf Course", "Zamboanga City", "Golf course with beachside views");
        addMarker(7.092302233523536, 122.23764762130105, "Bolong Beach", "Zamboanga City", "Relaxing beach area for locals");
        addMarker(6.952244392209877, 122.07450653867303, "Pasonanca Tree House", "Pasonanca", "Elevated treehouse in forest park");
        addMarker(6.909188297539425, 122.07587137601799, "Immaculate Conception Cathedral", "Zamboanga City", "Main Catholic cathedral");
        addMarker(6.90532394815818, 122.07648598469889, "Plaza Pershing", "Zamboanga City", "Historic public plaza");
        addMarker(7.156938277355132, 122.25806182222088, "Panubigan Island", "Zamboanga City", "Remote island with clear waters");
        addMarker(6.964944165372392, 122.0832197878457, "Pasonanca Natural Park", "Pasonanca", "Forest park with pools and trails");
        addMarker(6.904498655004247, 122.07652862162276, "Metropolitan Cathedral", "Zamboanga City", "Historic religious landmark");
        addMarker(6.906038553945021, 122.07826037583667, "Cawa-Cawa Boulevard", "Zamboanga City", "Waterfront boulevard area");
        addMarker(6.928405653951321, 122.06034706032728, "Camp Navarro", "Zamboanga City", "Western Mindanao Command HQ");
        addMarker(6.912612947472231, 122.07503173022202, "Zamboanga City Coliseum", "Zamboanga City", "Sports and events venue");
    }

    private void addMarker(double lat, double lon, String title, String snippet, String description) {
        GeoPoint point = new GeoPoint(lat, lon);
        Marker marker = new Marker(mapView);
        marker.setPosition(point);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle(title);
        marker.setSnippet(snippet);

        marker.setOnMarkerClickListener((m, mv) -> {
            showMarkerDetailDialog(title, snippet, description, point);
            return true;
        });

        Drawable icon = ContextCompat.getDrawable(this, org.osmdroid.library.R.drawable.marker_default);
        if (icon != null) {
            icon.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
            marker.setIcon(icon);
        }

        mapView.getOverlays().add(marker);
        locationPoints.put(title, point);
    }

    private void showMarkerDetailDialog(String title, String snippet, String description, GeoPoint point) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_marker_detail, null);
        builder.setView(dialogView);

        TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitle);
        TextView tvSnippet = dialogView.findViewById(R.id.tvDialogSnippet);
        TextView tvDescription = dialogView.findViewById(R.id.tvDialogDescription);
        MaterialCardView btnGetDirections = dialogView.findViewById(R.id.btnGetDirections);

        tvTitle.setText(title);
        tvSnippet.setText(snippet);
        tvDescription.setText(description);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        btnGetDirections.setOnClickListener(v -> {
            dialog.dismiss();
            prepareRoute(point, title);
            calculateAndDrawRoute();
        });

        dialog.show();
    }

    private void showLocationPickerDialog(EditText targetEditText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_location_picker, null);
        builder.setView(dialogView);

        EditText etSearch = dialogView.findViewById(R.id.etSearchLocation);
        RecyclerView rvLocations = dialogView.findViewById(R.id.rvLocations);
        View btnClose = dialogView.findViewById(R.id.btnClosePicker);
        rvLocations.setLayoutManager(new LinearLayoutManager(this));

        List<String> locationNames = new ArrayList<>(locationPoints.keySet());
        Collections.sort(locationNames);

        LocationAdapter adapter = new LocationAdapter(locationNames, location -> {
            GeoPoint point = locationPoints.get(location);
            if (point != null) {
                if (targetEditText == null) {
                    prepareRoute(point, location);
                    calculateAndDrawRoute();
                } else {
                    targetEditText.setText(location);
                    updateRouteFromInputs();
                }
            }
        });
        rvLocations.setAdapter(adapter);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        if (btnClose != null) {
            btnClose.setOnClickListener(v -> dialog.dismiss());
        }

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        dialog.show();
        adapter.setDialog(dialog);
    }

    private void updateRouteFromInputs() {
        String origin = etOriginMap.getText().toString();
        String dest = etDestMap.getText().toString();

        GeoPoint startPoint = null;
        GeoPoint endPoint = null;

        if (origin.equals("Current Location") || origin.isEmpty()) {
            if (mLocationOverlay != null) startPoint = mLocationOverlay.getMyLocation();
        } else {
            startPoint = locationPoints.get(origin);
        }

        endPoint = locationPoints.get(dest);

        if (startPoint != null && endPoint != null) {
            drawRoute(startPoint, endPoint);
        }
    }

    private static class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {
        private final List<String> originalList;
        private List<String> filteredList;
        private final OnLocationSelectedListener listener;
        private AlertDialog dialog;

        interface OnLocationSelectedListener {
            void onLocationSelected(String location);
        }

        LocationAdapter(List<String> list, OnLocationSelectedListener listener) {
            this.originalList = list;
            this.filteredList = new ArrayList<>(list);
            this.listener = listener;
        }

        void setDialog(AlertDialog dialog) { this.dialog = dialog; }

        void filter(String query) {
            if (query.isEmpty()) {
                filteredList = new ArrayList<>(originalList);
            } else {
                filteredList = originalList.stream()
                        .filter(s -> s.toLowerCase().contains(query.toLowerCase()))
                        .collect(Collectors.toList());
            }
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_location, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String name = filteredList.get(position);
            holder.textView.setText(name);
            holder.itemView.setOnClickListener(v -> {
                listener.onLocationSelected(name);
                if (dialog != null) dialog.dismiss();
            });
        }

        @Override
        public int getItemCount() { return filteredList.size(); }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;
            ViewHolder(View view) {
                super(view);
                textView = view.findViewById(R.id.tvLocationName);
            }
        }
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