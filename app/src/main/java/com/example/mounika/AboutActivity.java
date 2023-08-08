package com.example.mounika;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.widget.TextView;

import com.example.mounika.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Set up the custom toolbar and set the title
        Toolbar aboutToolbar = findViewById(R.id.about_toolbar);
        setSupportActionBar(aboutToolbar);
        getSupportActionBar().setTitle("About");

        // Display information about the general women safety app
        TextView appInfoTextView = findViewById(R.id.appInfoTextView);
        String appInfo = "Welcome to the Women Safety App!\n\n" +
                "Our app is designed to empower women and enhance their safety in various situations. " +
                "With features like SOS alerts, emergency contacts, and safety tips, " +
                "we aim to make the world a safer place for women.\n\n" +
                "Key Features:\n" +
                "- SOS Alerts: Send emergency alerts to your pre-selected contacts with just a tap.\n" +
                "- Emergency Contacts: Add important contacts who can be reached in case of emergencies.\n" +
                "- Safety Tips: Access helpful safety tips and guidelines to stay secure.\n" +
                "- Location Tracking: Share your real-time location with trusted contacts for added security.\n" +
                "- Panic Button: Place a widget on your home screen for quick access to SOS alerts.\n" +
                "- Customization: Personalize the app with your preferred settings and contacts.\n\n" +
                "Stay safe and empowered with the Women Safety App!\n\n" +
                "For any feedback or support, please contact us at support@womensafetyapp.com.";

        appInfoTextView.setText(appInfo);
    }
}


