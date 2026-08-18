package com.zybooks.warehouse.ux.modify;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.zybooks.warehouse.R;
import com.zybooks.warehouse.data.database.InventoryDatabase;
import com.zybooks.warehouse.data.model.InventoryItem;

public class ModifyItemActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "WarehousePrefs";
    private static final String KEY_PHONE_NUMBER = "alert_phone_number";
    private static final int LOW_STOCK_THRESHOLD = 10;

    TextInputEditText nameField, skuField, qtyField, locationField;
    MaterialButton saveButton, deleteButton;
    TextView modifyTitle;

    int itemId;

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_item);

        // UI references
        modifyTitle = findViewById(R.id.modifyTitle);
        nameField = findViewById(R.id.nameField);
        skuField = findViewById(R.id.skuField);
        qtyField = findViewById(R.id.qtyField);
        locationField = findViewById(R.id.locationField);
        saveButton = findViewById(R.id.saveButton);
        deleteButton = findViewById(R.id.deleteButton);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Load passed data
        itemId = getIntent().getIntExtra("itemId", -1);

        // Determine if we are adding or editing
        boolean isEditMode = itemId != -1;

        if (isEditMode) {
            modifyTitle.setText("Edit Inventory Item");
            saveButton.setText("Save Changes");
            deleteButton.setVisibility(View.VISIBLE);

            // Populate fields for edit
            nameField.setText(getIntent().getStringExtra("itemName"));
            skuField.setText(getIntent().getStringExtra("itemSku"));
            qtyField.setText(String.valueOf(getIntent().getIntExtra("itemQty", 0)));
            locationField.setText(getIntent().getStringExtra("itemLocation"));
        } else {
            modifyTitle.setText("Add New Item");
            saveButton.setText("Add Item");
            deleteButton.setVisibility(View.GONE);
        }

        // Delete button listener
        deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Item")
                    .setMessage("Are you sure you want to delete this item? This action cannot be undone.")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        InventoryDatabase db = InventoryDatabase.getInstance(this);
                        InventoryItem item = new InventoryItem("", "", 0, "");
                        item.id = itemId;
                        db.inventoryDao().delete(item);
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        // Save/Add button listener
        saveButton.setOnClickListener(v -> {
            if (validateInput()) {
                String title = isEditMode ? "Save Changes" : "Add Item";
                String message = isEditMode ? "Are you sure you want to save these changes?" : "Are you sure you want to add this new item?";

                new AlertDialog.Builder(this)
                        .setTitle(title)
                        .setMessage(message)
                        .setPositiveButton("Confirm", (dialog, which) -> {
                            String name = nameField.getText().toString().trim();
                            String sku = skuField.getText().toString().trim();
                            int quantity = Integer.parseInt(qtyField.getText().toString().trim());
                            String location = locationField.getText().toString().trim();

                            InventoryDatabase db = InventoryDatabase.getInstance(this);
                            InventoryItem item = new InventoryItem(name, sku, quantity, location);

                            if (isEditMode) {
                                item.id = itemId;
                                db.inventoryDao().update(item);
                            } else {
                                db.inventoryDao().insert(item);
                            }

                            // Check for low stock alert
                            checkForLowStock(item);

                            finish(); // Return to MainActivity
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
    }

   // Check for low stock alert
    private void checkForLowStock(InventoryItem item) {
        if (item.quantity < LOW_STOCK_THRESHOLD) {
            // Check if permission is granted
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                // Get saved phone number
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                String phoneNumber = prefs.getString(KEY_PHONE_NUMBER, "");

                if (!phoneNumber.isEmpty()) {
                    // Logic to "send" SMS alert
                    String message = "Low Stock Alert: " + item.name + " (" + item.sku + ") is at " + item.quantity + " units.";
                    Log.d("SMS_ALERT", "SMS sent to " + phoneNumber + ": " + message);
                    

                } else {
                    Log.d("SMS_ALERT", "Low stock detected but no phone number configured.");
                }
            } else {
                Log.d("SMS_ALERT", "Low stock detected but SMS permission not granted.");
            }
        }
    }

    // Input validation
    private boolean validateInput() {
        String name = nameField.getText().toString().trim();
        String sku = skuField.getText().toString().trim();
        String qtyText = qtyField.getText().toString().trim();
        String location = locationField.getText().toString().trim();

        if (name.isEmpty()) {
            nameField.setError("Name required");
            return false;
        }
        if (sku.isEmpty()) {
            skuField.setError("SKU required");
            return false;
        }
        if (qtyText.isEmpty()) {
            qtyField.setError("Quantity required");
            return false;
        }
        try {
            Integer.parseInt(qtyText);
        } catch (NumberFormatException e) {
            qtyField.setError("Must be a number");
            return false;
        }
        if (location.isEmpty()) {
            locationField.setError("Location required");
            return false;
        }
        return true;
    }
}
