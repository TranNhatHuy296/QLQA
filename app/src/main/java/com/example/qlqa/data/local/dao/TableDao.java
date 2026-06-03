package com.example.qlqa.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.qlqa.data.local.entities.Table;

import java.util.List;

@Dao
public interface TableDao {
    @Insert
    long insert(Table table);

    @Update
    void update(Table table);

    @Delete
    void delete(Table table);

    @Query("SELECT * FROM tables")
    List<Table> getAllTables();

    @Query("SELECT * FROM tables WHERE tableId = :id")
    Table getTableById(int id);
}
