package com.zybooks.warehouse.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.zybooks.warehouse.data.dao.InventoryDao;
import com.zybooks.warehouse.data.dao.UserDao;
import com.zybooks.warehouse.data.model.InventoryItem;
import com.zybooks.warehouse.data.model.User;

@Database(entities = {InventoryItem.class, User.class}, version = 2)
public abstract class InventoryDatabase extends RoomDatabase {

    private static InventoryDatabase instance;

    public abstract InventoryDao inventoryDao();
    public abstract UserDao userDao();

    public static synchronized InventoryDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            InventoryDatabase.class,
                            "inventory.db"
                    )
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }
}
