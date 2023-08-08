package com.example.mounika;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    Button button;
    TextView textView;
    FirebaseUser user;

    private static final int IGNORE_BATTERY_OPTIMIZATION_REQUEST = 1002;
    private static final int PICK_CONTACT = 1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    private ListView listView;
    private DbHelper db;
    private List<ContactModel> list;
    private CustomAdapter customAdapter;
    private BottomNavigationView bottomNavView;

    @RequiresApi(api = Build.VERSION_CODES.O)


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        textView = findViewById(R.id.user_datails);
        user = auth.getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            textView.setText(user.getEmail());
        }


        // check for runtime permissions for location and SMS
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.READ_CONTACTS
            }, LOCATION_PERMISSION_REQUEST_CODE);
        }

        // this is a special permission required only by devices using Android Q and above.
        // The Access Background Permission is responsible for populating the dialog with "ALLOW ALL THE TIME" option
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 100);
        }

        // check for BatteryOptimization
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (pm != null && !pm.isIgnoringBatteryOptimizations(getPackageName())) {
            askIgnoreOptimization();
        }

        // start the service
        // Start the SensorService to listen for shake events
        Intent intent = new Intent(this, SensorService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent); // Start the service in the foreground for Android versions >= Oreo
        } else {
            startService(intent); // Start the service for versions prior to Oreo
        }


        bottomNavView = findViewById(R.id.bottomNavigationView);
        bottomNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();
                if (itemId == R.id.menu_Home) {
                    // Handle home option click
                } else if (itemId == R.id.menu_TrackMe) {
                    // Handle track me option click
                    openMapActivity();
                } else if (itemId == R.id.menu_Community) {
                    // Handle community option click
                } else if (itemId == R.id.menu_safety_tips) {
                    openCategorySelectionActivity();
                }

                return true;
            }

            // Handle safety tips option click
            private void openCategorySelectionActivity() {
                Intent intent = new Intent(MainActivity.this, CategorySelectionActivity.class);
                startActivity(intent);
            }

            private void openMapActivity() {
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Display the hamburger icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);

        // Set a click listener for the hamburger icon
        toolbar.setNavigationOnClickListener(v -> showPopupMenu(v));

        // Set the title on the toolbar
        getSupportActionBar().setTitle("FEMSAFE");
        // create instances of various classes to be used
        Button button1 = findViewById(R.id.Button1);
        listView = findViewById(R.id.ListView);
        db = new DbHelper(this);
        list = db.getAllContacts();
        customAdapter = new CustomAdapter(this, list);
        listView.setAdapter(customAdapter);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calling of getContacts()
                if (db.count() != 5) {
                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(intent, PICK_CONTACT);
                } else {
                    Toast.makeText(MainActivity.this, "Can't Add more than 5 Contacts", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button sosButton = findViewById(R.id.sosButton);
        sosButton.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             sendSOS();
                                             startActivity(new android.content.Intent(MainActivity.this, VideoAudioActivity.class));
                                         }

                                     }
        );

}
        @Override
        public boolean onCreateOptionsMenu (Menu menu){
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_profile) {
            // Handle the profile icon click here
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
            return true;
        }
        // Handle other menu item clicks if needed

        return super.onOptionsItemSelected(item);
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.popup_menu);

        // Set a click listener for the popup menu items
        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.menu_settings) {
                // Handle the "Settings" menu item click
                // Open the Settings activity or fragment
                openSettingsActivity();
                return true;
            }else if (itemId == R.id.menu_geofence) {

                // Handle the "About" menu item click
                // Open the About activity or fragment
                openMapActivity();
                return true;
            } else if (itemId == R.id.menu_about) {
                // Handle the "About" menu item click
                // Open the About activity or fragment
                openAboutActivity();
                return true;
            } else if (itemId == R.id.menu_safe_route) {
                // Handle the "Logout" menu item click
                // Perform logout operations
                openSaferouteActivity();
                return true;
            }else if (itemId == R.id.menu_socialmedia) {
                // Handle the "Logout" menu item click
                // Perform logout operations
                openWomenSafetyAppActivity();
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    private void openWomenSafetyAppActivity() {
        Intent intent = new Intent(this, WomenSafetyAppActivity.class);
        startActivity(intent);
    }

    private void openSaferouteActivity() {
        Intent intent = new Intent(this, SaferouteActivity.class);
        startActivity(intent);

    }

    private void openMapActivity() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }


    private void openAboutActivity() {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    private void openSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void sendSOS() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.SEND_SMS}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setPowerRequirement(Criteria.POWER_HIGH);

            String provider = locationManager.getBestProvider(criteria, true);

            if (provider != null) {
                // Request continuous location updates
                locationManager.requestLocationUpdates(provider, 0, 0, locationListener);
            } else {
                Toast.makeText(this, "No location provider available", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // LocationListener to handle location updates
    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            // Remove the location updates listener
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null) {
                locationManager.removeUpdates(this);
            }

            // Retrieve the emergency contact phone numbers from the database or any other data source
            List<ContactModel> emergencyContacts = db.getAllContacts();

            // Construct the message to be sent
            String message = "Help! I'm in danger. My current location is: " + "http://maps.google.com/?q=" + location.getLatitude() + "," + location.getLongitude();

            // Send the SOS message to each emergency contact
            for (ContactModel contact : emergencyContacts) {
                sendSms(contact.getPhoneNo(), message);
            }

            Toast.makeText(MainActivity.this, "SOS message sent to emergency contacts", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };

    private void sendSms(String phoneNumber, String message) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
    }

    // method to check if the service is running
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    Log.i("Service status", "Running");
                    return true;
                }
            }
        }
        Log.i("Service status", "Not running");
        return false;
    }

    @Override
    protected void onDestroy() {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, ReactivateService.class);
        sendBroadcast(broadcastIntent);
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                boolean allPermissionsGranted = true;
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        allPermissionsGranted = false;
                        break;
                    }
                }
                if (allPermissionsGranted) {
                    // Permissions granted, perform your operations that require permissions here
                } else {
                    // Permissions denied, handle the situation gracefully
                    Toast.makeText(this, "Permissions denied. Cannot access contacts or location.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // get the contact from the PhoneBook of device
        if (requestCode == PICK_CONTACT && resultCode == Activity.RESULT_OK && data != null) {
            Uri contactData = data.getData();
            if (contactData != null) {
                Cursor cursor = getContentResolver().query(contactData, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    try {
                        String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                        String hasPhone = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                        if (hasPhone != null && hasPhone.equalsIgnoreCase("1")) {
                            String contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                            Cursor phoneCursor = getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                    new String[]{contactId},
                                    null
                            );

                            if (phoneCursor != null && phoneCursor.moveToFirst()) {
                                String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                db.addcontact(new ContactModel(0, name, phoneNumber));
                                list = db.getAllContacts();
                                customAdapter.refresh(list);
                            }

                            if (phoneCursor != null) {
                                phoneCursor.close();
                            }
                        }
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                }

                if (cursor != null) {
                    cursor.close();
                }
            }
        }
    }

    // this method prompts the user to remove any battery optimization constraints from the App
    private void askIgnoreOptimization() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, IGNORE_BATTERY_OPTIMIZATION_REQUEST);
        }
    }

    }