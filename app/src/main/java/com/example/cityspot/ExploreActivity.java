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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExploreActivity extends AppCompatActivity {

    private Button btnExplore, btnSaved, btnProfile, btnMap;
    private TextView txtExploreTitle;
    private EditText editSearch;
    private LinearLayout exploreContainer;
    private List<Attraction> attractionList;

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
        exploreContainer = findViewById(R.id.exploreContainer);

        initData();
        displayAttractions(attractionList);
        setupSearch();
        updateWelcomeMessage();

        btnSaved.setOnClickListener(v -> {
            startActivity(new Intent(ExploreActivity.this, SavedActivity.class));
            overridePendingTransition(0, 0);
        });

        btnProfile.setOnClickListener(v -> {
            startActivity(new Intent(ExploreActivity.this, ProfileActivity.class));
            overridePendingTransition(0, 0);
        });

        btnMap.setOnClickListener(v -> {
            startActivity(new Intent(ExploreActivity.this, MapActivity.class));
            overridePendingTransition(0, 0);
        });
    }

    private void initData() {
        attractionList = new ArrayList<>();
        attractionList.add(new Attraction("Santa cruz island", "Great Santa Cruz Island, Zamboanga City", "Famous pink sand beach", R.drawable.stacruz, 6.8729579146675475, 122.05844877432924));
        attractionList.add(new Attraction("Paseo Del Mar", "Paseo del Mar, Zamboanga City", "Seaside park with sunset views", R.drawable.paseo, 6.900773315389889, 122.08126672442509));
        attractionList.add(new Attraction("Once isla", "Barangay Panubigan, Zamboanga City", "Eco-cultural tourism site", R.drawable.onceislas, 7.120713444659594, 122.27011291141665));
        attractionList.add(new Attraction("Merloquet falls", "Sibulao, Zamboanga City", "Two-tier scenic waterfall", R.drawable.merloquetfalls, 7.3103435668858685, 122.21349478209761));
        attractionList.add(new Attraction("Lantawan grassland", "Upper Pasonanca, Zamboanga City", "Scenic mountain viewpoint", R.drawable.lantawan, 6.965572885339477, 122.06122819559002));
        attractionList.add(new Attraction("Yakan Weaving Village", "Upper Calarian, Zamboanga City", "Traditional Yakan crafts", R.drawable.yakanvillage, 6.925108906106171, 122.02221645908409));
        attractionList.add(new Attraction("Zamboanga City Hall", "Valderrosa St, Zamboanga City", "Classic colonial architecture", R.drawable.cityhall, 6.904346115853981, 122.07616608024884));
        attractionList.add(new Attraction("R. T. Lim Boulevard (Viewing Deck)", "R. T. Lim Blvd, Zamboanga City", "Breathtaking coastal views", R.drawable.boulevard, 6.907358369240467, 122.06851645586336));
        attractionList.add(new Attraction("National Museum Fort Pilar, Zamboanga City", "Pilar St, Zamboanga City", "Historic Spanish-era fort", R.drawable.museum, 6.901007470878223, 122.08141738469901));
        attractionList.add(new Attraction("Dulian Falls", "Dulian, Zamboanga City", "Lush forest waterfall", R.drawable.dulianfalls, 7.151025145289209, 122.17878779202081));
    }

    private void displayAttractions(List<Attraction> list) {
        if (exploreContainer == null) return;
        exploreContainer.removeAllViews();
        for (Attraction attraction : list) {
            View card = getLayoutInflater().inflate(R.layout.item_attraction_card, exploreContainer, false);
            
            ImageView img = card.findViewById(R.id.imgAttraction);
            TextView name = card.findViewById(R.id.txtAttractionName);
            TextView loc = card.findViewById(R.id.txtLocation);
            ImageButton fav = card.findViewById(R.id.btnFavorite);
            
            img.setImageResource(attraction.getImageResId());
            name.setText(attraction.getName());
            loc.setText(attraction.getLocation());
            
            card.setOnClickListener(v -> openDetail(attraction));
            fav.setOnClickListener(v -> {
                String trailData = attraction.getName() + "|" + 
                                  attraction.getLocation() + "|" + 
                                  attraction.getDetails() + "|" + 
                                  attraction.getLat() + "|" + 
                                  attraction.getLon() + "|" +
                                  attraction.getImageResId();
                saveTrail(trailData);
            });
            
            exploreContainer.addView(card);
        }
    }

    private void setupSearch() {
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterAttractions(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterAttractions(String query) {
        List<Attraction> filtered = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        for (Attraction a : attractionList) {
            if (a.getName().toLowerCase().contains(lowerQuery) || 
                a.getLocation().toLowerCase().contains(lowerQuery)) {
                filtered.add(a);
            }
        }
        displayAttractions(filtered);
    }

    private void openDetail(Attraction attraction) {
        // Increment Visited Count
        SharedPreferences prefs = getSharedPreferences("CitySpotPrefs", MODE_PRIVATE);
        int visitedCount = prefs.getInt("visited_count", 0);
        prefs.edit().putInt("visited_count", visitedCount + 1).apply();

        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("trail_name", attraction.getName());
        intent.putExtra("trail_sub_info", attraction.getLocation());
        intent.putExtra("trail_distance", attraction.getDetails());
        intent.putExtra("trail_lat", attraction.getLat());
        intent.putExtra("trail_lon", attraction.getLon());
        intent.putExtra("trail_image", attraction.getImageResId());
        startActivity(intent);
    }

    private void saveTrail(String trailData) {
        SharedPreferences prefs = getSharedPreferences("CitySpotPrefs", MODE_PRIVATE);
        Set<String> savedTrails = prefs.getStringSet("saved_trails", new HashSet<>());
        Set<String> newSavedTrails = new HashSet<>(savedTrails);
        if (newSavedTrails.add(trailData)) {
            prefs.edit().putStringSet("saved_trails", newSavedTrails).apply();
            Toast.makeText(this, "Saved to favorites!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Already in favorites!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateWelcomeMessage() {
        SharedPreferences prefs = getSharedPreferences("CitySpotPrefs", MODE_PRIVATE);
        String username = prefs.getString("username", "User");
        txtExploreTitle.setText("Welcome, " + username + "!");
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateWelcomeMessage();
    }
}