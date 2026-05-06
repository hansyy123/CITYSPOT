package com.example.cityspot;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText editFullName, editUsername, editPassword;
    private android.widget.Button btnRegister;
    private TextView txtLoginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editFullName = findViewById(R.id.editFullName);
        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        btnRegister = findViewById(R.id.btnRegister);
        txtLoginLink = findViewById(R.id.txtLoginLink);

        btnRegister.setOnClickListener(v -> {
            String name = editFullName.getText().toString();
            String username = editUsername.getText().toString();
            String pass = editPassword.getText().toString();

            if (name.isEmpty() || username.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                // In a real app, you'd save this to a database
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                finish();
            }
        });

        txtLoginLink.setOnClickListener(v -> finish());
    }
}