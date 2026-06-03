package com.example.qlqa.data.local.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "tables")
public class Table {
    @PrimaryKey(autoGenerate = true)
    private int tableId;

    @NonNull
    private String tableName;
    
    @NonNull
    private String status; // Trống / Đang phục vụ
    
    @NonNull
    private String createdAt;

    public Table(@NonNull String tableName, @NonNull String status, @NonNull String createdAt) {
        this.tableName = tableName;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getTableId() { return tableId; }
    public void setTableId(int tableId) { this.tableId = tableId; }

    @NonNull
    public String getTableName() { return tableName; }
    public void setTableName(@NonNull String tableName) { this.tableName = tableName; }

    @NonNull
    public String getStatus() { return status; }
    public void setStatus(@NonNull String status) { this.status = status; }

    @NonNull
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(@NonNull String createdAt) { this.createdAt = createdAt; }
}
