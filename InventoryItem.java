package com.zybooks.warehouse.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "inventory")
public class InventoryItem {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String sku;
    public int quantity;
    public String location;

    public InventoryItem(String name, String sku, int quantity, String location) {
        this.name = name;
        this.sku = sku;
        this.quantity = quantity;
        this.location = location;
    }
}

