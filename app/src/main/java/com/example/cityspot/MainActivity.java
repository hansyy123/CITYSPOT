package com.example.cityspot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private androidx.appcompat.widget.AppCompatButton btnLogin;
    private android.widget.TextView txtSignup;
    private android.widget.EditText editEmail, editPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogin = findViewById(R.id.btnLogin);
        txtSignup = findViewById(R.id.txtSignup);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);

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
}