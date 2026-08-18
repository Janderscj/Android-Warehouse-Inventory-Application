package com.zybooks.warehouse.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.zybooks.warehouse.data.model.InventoryItem;

import java.util.List;

@Dao
public interface InventoryDao {

    @Query("SELECT * FROM inventory")
    List<InventoryItem> getAll();

    @Insert
    void insert(InventoryItem item);

    @Update
    void update(InventoryItem item);

    @Delete
    void delete(InventoryItem item);
}