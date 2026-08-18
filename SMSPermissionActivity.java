package com.zybooks.warehouse.ux.permissions;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.zybooks.warehouse.R;

public class SMSPermissionActivity extends AppCompatActivity {

    private static final int SMS_PERMISSION_CODE = 100;
    private static final String PREFS_NAME = "WarehousePrefs";
    private static final String KEY_PHONE_NUMBER = "alert_phone_number";

    private TextView statusText;
    private TextInputEditText phoneField;
    private MaterialButton saveSettingsButton;

    // Handle the back arrow in the toolbar
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_permission);

        // Initialize UI components
        statusText = findViewById(R.id.statusText);
        phoneField = findViewById(R.id.phoneField);
        saveSettingsButton = findViewById(R.id.saveSettingsButton);

        // Display the back arrow in the toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Load the previously saved phone number from SharedPreferences.
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String savedPhone = prefs.getString(KEY_PHONE_NUMBER, "");
        phoneField.setText(savedPhone);

        // Update UI based on whether permission was already granted or denied.
        updatePermissionStatus();

        // Prompt the user for SEND_SMS permission.
        requestSMSPermission();

        // Persist the entered phone number.
        saveSettingsButton.setOnClickListener(v -> {
            String phone = phoneField.getText().toString().trim();
            
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(KEY_PHONE_NUMBER, phone);
            editor.apply();

            Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();
        });
    }

    // Update the UI based on the current permission status.
    private void updatePermissionStatus() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            statusText.setText("SMS permission granted.\nAutomated alerts enabled.");
        } else {
            statusText.setText("SMS permission denied.\nAutomated alerts disabled.");
        }
    }

    // Request the SEND_SMS permission
    private void requestSMSPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.SEND_SMS},
                SMS_PERMISSION_CODE
        );
    }

    // Handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_CODE) {
            // Update the UI based on the user's response
            updatePermissionStatus();
        }
    }
}
