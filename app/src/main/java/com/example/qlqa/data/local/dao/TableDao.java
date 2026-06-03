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

    @Query("SELECT * FROM tables WHERE area = :area ORDER BY tableName ASC")
    List<Table> getTablesByArea(String area);

    @Query("SELECT COUNT(*) FROM tables")
    int getTotalTablesCount();

    @Query("SELECT COUNT(*) FROM tables WHERE status = 'Trống'")
    int getEmptyTablesCount();

    @Query("SELECT COUNT(*) FROM tables WHERE status = 'Có khách'")
    int getOccupiedTablesCount();

    @Query("SELECT COUNT(*) FROM tables WHERE status = 'Đang dọn'")
    int getCleaningTablesCount();

    @Query("SELECT EXISTS(SELECT 1 FROM tables WHERE area = :area AND tableName = :name LIMIT 1)")
    boolean isTableExistsInArea(String area, String name);

    @Query("SELECT EXISTS(SELECT 1 FROM tables WHERE area = :area AND tableName = :name AND tableId != :excludeId LIMIT 1)")
    boolean isTableExistsInAreaExclude(String area, String name, int excludeId);
}
