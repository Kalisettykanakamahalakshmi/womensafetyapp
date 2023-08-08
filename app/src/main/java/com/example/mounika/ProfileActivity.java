package com.example.mounika;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileActivity extends AppCompatActivity {
    private ImageView profilePictureImageView;
    private TextView nameTextView, emailTextView;
    private EditText newNameEditText;
    private Button saveButton, logoutButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profilePictureImageView = findViewById(R.id.profilePictureImageView);
        nameTextView = findViewById(R.id.nameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        newNameEditText = findViewById(R.id.newNameEditText);
        saveButton = findViewById(R.id.saveButton);
        logoutButton = findViewById(R.id.logoutButton);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        // Load and display user profile data
        loadUserProfile();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = newNameEditText.getText().toString().trim();
                if (!newName.isEmpty()) {
                    updateUserName(newName);
                } else {
                    Toast.makeText(ProfileActivity.this, "Please enter a name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });
    }

    private void loadUserProfile() {
        String currentUserId = mAuth.getCurrentUser().getUid();
        DocumentReference userRef = db.collection("users").document(currentUserId);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String name = document.getString("name");
                    String email = mAuth.getCurrentUser().getEmail();
                    String profilePictureUrl = document.getString("profilePictureUrl");

                    nameTextView.setText(name);
                    emailTextView.setText(email);

                    if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
                        Glide.with(ProfileActivity.this).load(profilePictureUrl).into(profilePictureImageView);
                    }
                }
            } else {
                Log.e("ProfileActivity", "Error fetching user profile", task.getException());
                Toast.makeText(ProfileActivity.this, "Failed to fetch user profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserName(String newName) {
        String currentUserId = mAuth.getCurrentUser().getUid();
        DocumentReference userRef = db.collection("users").document(currentUserId);

        userRef.update("name", newName)
                .addOnSuccessListener(aVoid -> {
                    nameTextView.setText(newName);
                    Toast.makeText(ProfileActivity.this, "Name updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("ProfileActivity", "Error updating user name", e);
                    Toast.makeText(ProfileActivity.this, "Failed to update name", Toast.LENGTH_SHORT).show();
                });
    }


    private void logoutUser() {
        mAuth.signOut();
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
