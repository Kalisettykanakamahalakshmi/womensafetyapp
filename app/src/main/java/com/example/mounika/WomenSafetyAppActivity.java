package com.example.mounika;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class WomenSafetyAppActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_women_safety_app);
    }

    public void shareOnSocialMedia(View view) {
        String safetyMessage = "I'm feeling unsafe. Please help me!";

        // Create a Share Intent
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, safetyMessage);

        // Show the share chooser dialog
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }
}