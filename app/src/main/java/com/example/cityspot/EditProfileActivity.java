package com.example.cityspot;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditProfileActivity extends AppCompatActivity {

    private EditText editUsername, editEmail;
    private Button btnSave, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);  // Create this layout XML file next!

        // Initialize views
        editUsername = findViewById(R.id.editUsername);
        editEmail = findViewById(R.id.editEmail);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = editUsername.getText().toString().trim();
                String email = editEmail.getText().toString().trim();

                // Simple validation
                if (username.isEmpty()) {
                    Toast.makeText(EditProfileActivity.this, "Please enter a username", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(email.isEmpty()){
                    Toast.makeText(EditProfileActivity.this, "Please enter an email", Toast.LENGTH_SHORT).show();
                    return;
                }

                // TODO: Save updated profile info to your data/store

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