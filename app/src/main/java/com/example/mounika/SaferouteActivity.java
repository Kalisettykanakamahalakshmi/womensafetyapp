package com.example.mounika;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SaferouteActivity extends AppCompatActivity {

    private EditText etFromLocation;
    private EditText etToLocation;
    private Button btnGetDirection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saferoute);

        etFromLocation = findViewById(R.id.etFromLocation);
        etToLocation = findViewById(R.id.etToLocation);
        btnGetDirection = findViewById(R.id.btnGetDirection);

        btnGetDirection.setOnClickListener(view -> {
            String userLocation = etFromLocation.getText().toString();
            String userDestination = etToLocation.getText().toString();
            if (userLocation.equals("") || userDestination.equals("")) {
                Toast.makeText(this, "Please enter your location & destination", Toast.LENGTH_SHORT).show();

            } else {
                getDirections(userLocation, userDestination);
            }
        });
    }

    private void getDirections(String from, String to) {
        try {
            Uri uri = Uri.parse("https://www.google.com/maps/dir/" + from   + "/" + to);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.google.android.apps.maps");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }catch(ActivityNotFoundException exception) {
            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }



}