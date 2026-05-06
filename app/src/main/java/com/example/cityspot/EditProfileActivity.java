package com.example.cityspot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class EditProfileActivity extends AppCompatActivity {

    private EditText editUsername, editPassword;
    private Button btnSave, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);  // Create this layout XML file next!

        // Initialize views
        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        ImageButton btnBack = findViewById(R.id.btnBackEdit);

        btnBack.setOnClickListener(v -> finish());

        // Load current username
        SharedPreferences prefs = getSharedPreferences("CitySpotPrefs", MODE_PRIVATE);
        String currentUsername = prefs.getString("username", "");
        editUsername.setText(currentUsername);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = editUsername.getText().toString().trim();
                String password = editPassword.getText().toString().trim();

                // Simple validation
                if (username.isEmpty()) {
                    Toast.makeText(EditProfileActivity.this, "Please enter a username", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // Save updated profile info to SharedPreferences
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("username", username);
                if (!password.isEmpty()) {
                    editor.putString("password", password);
                }
                editor.apply();

                Toast.makeText(EditProfileActivity.this, "Profile updated!", Toast.LENGTH_SHORT).show();

                // Optionally close this activity after save
                finish();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Discard changes and go back
                finish();
            }
        });
    }
}