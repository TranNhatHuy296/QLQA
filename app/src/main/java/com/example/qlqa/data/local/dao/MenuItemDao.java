package com.example.qlqa.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.qlqa.data.local.entities.MenuItem;

import java.util.List;

@Dao
public interface MenuItemDao {
    @Insert
    long insert(MenuItem menuItem);

    @Update
    void update(MenuItem menuItem);

    @Delete
    void delete(MenuItem menuItem);

    @Query("SELECT * FROM menu_items ORDER BY createdAt DESC")
    List<MenuItem> getAllMenuItems();

    @Query("SELECT * FROM menu_items WHERE categoryId = :catId ORDER BY createdAt DESC")
    List<MenuItem> getMenuItemsByCategory(int catId);

    @Query("SELECT * FROM menu_items WHERE menuItemId = :id")
    MenuItem getMenuItemById(int id);

    @Query("SELECT * FROM menu_items WHERE itemName LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    List<MenuItem> searchMenuItems(String query);
}
