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
    private String area; // Tầng 1, Tầng 2, Sân vườn
    
    private int seats;
    
    @NonNull
    private String status; // Trống, Có khách, Đang dọn
    
    private int currentCustomers; // Số lượng khách hiện tại
    
    @NonNull
    private String createdAt;

    public Table(@NonNull String tableName, @NonNull String area, int seats, @NonNull String status, int currentCustomers, @NonNull String createdAt) {
        this.tableName = tableName;
        this.area = area;
        this.seats = seats;
        this.status = status;
        this.currentCustomers = currentCustomers;
        this.createdAt = createdAt;
    }

    public int getTableId() { return tableId; }
    public void setTableId(int tableId) { this.tableId = tableId; }

    @NonNull
    public String getTableName() { return tableName; }
    public void setTableName(@NonNull String tableName) { this.tableName = tableName; }

    @NonNull
    public String getArea() { return area; }
    public void setArea(@NonNull String area) { this.area = area; }

    public int getSeats() { return seats; }
    public void setSeats(int seats) { this.seats = seats; }

    @NonNull
    public String getStatus() { return status; }
    public void setStatus(@NonNull String status) { this.status = status; }

    public int getCurrentCustomers() { return currentCustomers; }
    public void setCurrentCustomers(int currentCustomers) { this.currentCustomers = currentCustomers; }

    @NonNull
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(@NonNull String createdAt) { this.createdAt = createdAt; }
}
