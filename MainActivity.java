package com.zybooks.warehouse.ux.main;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.zybooks.warehouse.R;
import com.zybooks.warehouse.data.database.InventoryDatabase;
import com.zybooks.warehouse.data.model.InventoryItem;
import com.zybooks.warehouse.ux.modify.ModifyItemActivity;
import com.zybooks.warehouse.ux.permissions.SMSPermissionActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private InventoryAdapter adapter;
    private TextInputEditText searchField;
    private MaterialButton addItemButton;

    // Local list to hold data from the database for searching/filtering
    private List<InventoryItem> inventoryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI references
        recyclerView = findViewById(R.id.inventoryRecycler);
        searchField = findViewById(R.id.searchField);
        addItemButton = findViewById(R.id.addItemButton);

        // Display database information using a grid
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // Listener for adding a new item
        addItemButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ModifyItemActivity.class);
            intent.putExtra("itemId", -1); 
            startActivity(intent);
        });

        // Navigation to the SMS/Notification settings screen
        Button smsButton = findViewById(R.id.smsButton);
        smsButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, SMSPermissionActivity.class);
            startActivity(intent);
        });

        // Filterable list.
        // Add a text watcher to the search field to filter the grid in real-time
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterList(s.toString());
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });
    }

   // Load data when the activity is resumed
    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    // Load data from the database
    private void loadData() {
        InventoryDatabase db = InventoryDatabase.getInstance(this);
        inventoryList = db.inventoryDao().getAll();

        // Initial setup of the database with sample data
        if (inventoryList.isEmpty()) {
            db.inventoryDao().insert(new InventoryItem("Steel Brackets", "DKI-1042", 120, "A349"));
            db.inventoryDao().insert(new InventoryItem("Copper Pipe", "DLS-2048", 45, "B143"));
            db.inventoryDao().insert(new InventoryItem("Hex Bolts", "AEM-3099", 500, "C421"));
            db.inventoryDao().insert(new InventoryItem("Screws", "ITM-1120", 75, "A305"));
            db.inventoryDao().insert(new InventoryItem("Aluminum", "TTE-5500", 12, "D103"));
            inventoryList = db.inventoryDao().getAll();
        }

        updateAdapter(inventoryList);
    }

    // Update the adapter with new data
    private void updateAdapter(List<InventoryItem> list) {
        adapter = new InventoryAdapter(list, item -> {
            Intent intent = new Intent(this, ModifyItemActivity.class);
            intent.putExtra("itemId", item.id);
            intent.putExtra("itemName", item.name);
            intent.putExtra("itemSku", item.sku);
            intent.putExtra("itemQty", item.quantity);
            intent.putExtra("itemLocation", item.location);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
    }

    // Filter the list based on the search query
    private void filterList(String query) {
        List<InventoryItem> filtered = new ArrayList<>();

        for (InventoryItem item : inventoryList) {
            if (item.name.toLowerCase().contains(query.toLowerCase()) ||
                    item.sku.toLowerCase().contains(query.toLowerCase())) {
                filtered.add(item);
            }
        }

        updateAdapter(filtered);
    }
}
