package com.example.qlqa.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.qlqa.data.local.entities.Notification;

import java.util.List;

@Dao
public interface NotificationDao {
    @Insert
    long insert(Notification notification);

    @Update
    void update(Notification notification);

    @Delete
    void delete(Notification notification);

    @Query("SELECT * FROM notifications ORDER BY createdAt DESC")
    List<Notification> getAllNotifications();
}
