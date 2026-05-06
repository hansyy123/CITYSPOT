package com.example.cityspot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private androidx.appcompat.widget.AppCompatButton btnLogin;
    private android.widget.TextView txtSignup;
    private android.widget.EditText editEmail, editPassword;
    private ViewPager2 viewPagerCarousel;
    private Handler sliderHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogin = findViewById(R.id.btnLogin);
        txtSignup = findViewById(R.id.txtSignup);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        viewPagerCarousel = findViewById(R.id.viewPagerCarousel);

        setupCarousel();

        // Check if we came from RegisterActivity
        String registeredUser = getIntent().getStringExtra("username");
        if (registeredUser != null) {
            editEmail.setText(registeredUser);
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editEmail.getText().toString();
                if (email.isEmpty()) {
                    navigateToExplore("User");
                } else {
                    navigateToExplore(email);
                }
            }
        });

        txtSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupCarousel() {
        List<Integer> images = Arrays.asList(R.drawable.img, R.drawable.img_1, R.drawable.img_2);
        CarouselAdapter adapter = new CarouselAdapter(images);
        viewPagerCarousel.setAdapter(adapter);

        // Optional: Auto-slide
        viewPagerCarousel.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 3000); // 3 seconds
            }
        });
    }

    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            int nextItem = viewPagerCarousel.getCurrentItem() + 1;
            if (nextItem >= 3) nextItem = 0;
            viewPagerCarousel.setCurrentItem(nextItem);
        };
    };

    @Override
    protected void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 3000);
    }

    private void navigateToExplore(String username) {
        // Save username to SharedPreferences for persistence
        getSharedPreferences("CitySpotPrefs", MODE_PRIVATE)
                .edit()
                .putString("username", username)
                .apply();

        Toast.makeText(this, "Logged in as " + username, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, ExploreActivity.class);
        startActivity(intent);
        finish();
    }

    static class CarouselAdapter extends RecyclerView.Adapter<CarouselAdapter.ViewHolder> {
        private final List<Integer> images;

        CarouselAdapter(List<Integer> images) {
            this.images = images;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_carousel, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.imageView.setImageResource(images.get(position));
        }

        @Override
        public int getItemCount() {
            return images.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            ViewHolder(View view) {
                super(view);
                imageView = view.findViewById(R.id.carouselImage);
            }
        }
    }
}