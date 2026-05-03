package com.example.cityspot;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {

    private TextView txtTrailsCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        txtTrailsCount = findViewById(R.id.txtTrailsCount);

        String username = getIntent().getStringExtra("username");
        if (username != null && !username.isEmpty()) {
            txtTrailsCount.setText("Welcome, " + username + "!");
        }
    }
}