package com.example.cityspot;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText editRegName, editRegEmail, editRegPassword;
    private Button btnRegisterSubmit;
    private TextView txtBackToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editRegName = findViewById(R.id.editRegName);
        editRegEmail = findViewById(R.id.editRegEmail);
        editRegPassword = findViewById(R.id.editRegPassword);
        btnRegisterSubmit = findViewById(R.id.btnRegisterSubmit);
        txtBackToLogin = findViewById(R.id.txtBackToLogin);

        btnRegisterSubmit.setOnClickListener(v -> {
            String name = editRegName.getText().toString();
            String email = editRegEmail.getText().toString();
            String pass = editRegPassword.getText().toString();

            if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                // In a real app, you'd save this to a database
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                intent.putExtra("username", name);
                startActivity(intent);
                finish();
            }
        });

        txtBackToLogin.setOnClickListener(v -> finish());
    }
}