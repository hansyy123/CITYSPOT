package com.example.cityspot;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ImageButton btnClose = findViewById(R.id.btnClose);
        ImageButton btnBack = findViewById(R.id.btnBack);
        TextView txtName = findViewById(R.id.txtDetailTrailName);
        TextView txtSubInfo = findViewById(R.id.txtDetailSubInfo);
        TextView txtDistance = findViewById(R.id.txtDetailDistance);
        TextView txtElevation = findViewById(R.id.txtDetailElevation);
        TextView txtTime = findViewById(R.id.txtDetailTime);

        // Get data from intent
        String name = getIntent().getStringExtra("trail_name");
        String subInfo = getIntent().getStringExtra("trail_sub_info");
        String distance = getIntent().getStringExtra("trail_distance");
        String elevation = getIntent().getStringExtra("trail_elevation");
        String time = getIntent().getStringExtra("trail_time");

        if (name != null) txtName.setText(name);
        if (subInfo != null) txtSubInfo.setText(subInfo);
        if (distance != null) txtDistance.setText(distance);
        if (elevation != null) txtElevation.setText(elevation);
        if (time != null) txtTime.setText(time);

        View.OnClickListener finishListener = v -> finish();
        btnClose.setOnClickListener(finishListener);
        btnBack.setOnClickListener(finishListener);
    }
}