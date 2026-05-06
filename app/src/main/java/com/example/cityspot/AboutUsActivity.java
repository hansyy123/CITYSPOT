package com.example.cityspot;

import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class AboutUsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        ImageButton btnBack = findViewById(R.id.btnBackAbout);
        btnBack.setOnClickListener(v -> finish());
    }
}