package com.example.qlqa.data.local.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.annotation.NonNull;

@Entity(
    tableName = "orders",
    foreignKeys = {
        @ForeignKey(
            entity = Table.class,
            parentColumns = "tableId",
            childColumns = "tableId",
            onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
            entity = Staff.class,
            parentColumns = "staffId",
            childColumns = "staffId",
            onDelete = ForeignKey.CASCADE
        )
    },
    indices = {@Index("tableId"), @Index("staffId")}
)
public class Order {
    @PrimaryKey(autoGenerate = true)
    private int orderId;

    private int tableId;
    private int staffId;
    
    @NonNull
    private String status; // Đang phục vụ / Đã thanh toán / Đã hủy
    
    private String cancelReason;
    private double totalAmount;
    
    @NonNull
    private String createdAt;
    private String updatedAt;

    public Order(int tableId, int staffId, @NonNull String status, String cancelReason, double totalAmount, @NonNull String createdAt, String updatedAt) {
        this.tableId = tableId;
        this.staffId = staffId;
        this.status = status;
        this.cancelReason = cancelReason;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getTableId() { return tableId; }
    public void setTableId(int tableId) { this.tableId = tableId; }

    public int getStaffId() { return staffId; }
    public void setStaffId(int staffId) { this.staffId = staffId; }

    @NonNull
    public String getStatus() { return status; }
    public void setStatus(@NonNull String status) { this.status = status; }

    public String getCancelReason() { return cancelReason; }
    public void setCancelReason(String cancelReason) { this.cancelReason = cancelReason; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    @NonNull
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(@NonNull String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
