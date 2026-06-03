package com.example.qlqa.data.local.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.annotation.NonNull;

@Entity(
    tableName = "order_history",
    foreignKeys = {
        @ForeignKey(
            entity = Order.class,
            parentColumns = "orderId",
            childColumns = "orderId",
            onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
            entity = Staff.class,
            parentColumns = "staffId",
            childColumns = "staffId",
            onDelete = ForeignKey.CASCADE
        )
    },
    indices = {@Index("orderId"), @Index("staffId")}
)
public class OrderHistory {
    @PrimaryKey(autoGenerate = true)
    private int historyId;

    private int orderId;
    private int staffId;
    
    private String previousStatus;
    
    @NonNull
    private String newStatus;
    
    private String note;
    
    @NonNull
    private String changedAt;

    public OrderHistory(int orderId, int staffId, String previousStatus, @NonNull String newStatus, String note, @NonNull String changedAt) {
        this.orderId = orderId;
        this.staffId = staffId;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.note = note;
        this.changedAt = changedAt;
    }

    public int getHistoryId() { return historyId; }
    public void setHistoryId(int historyId) { this.historyId = historyId; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getStaffId() { return staffId; }
    public void setStaffId(int staffId) { this.staffId = staffId; }

    public String getPreviousStatus() { return previousStatus; }
    public void setPreviousStatus(String previousStatus) { this.previousStatus = previousStatus; }

    @NonNull
    public String getNewStatus() { return newStatus; }
    public void setNewStatus(@NonNull String newStatus) { this.newStatus = newStatus; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    @NonNull
    public String getChangedAt() { return changedAt; }
    public void setChangedAt(@NonNull String changedAt) { this.changedAt = changedAt; }
}
