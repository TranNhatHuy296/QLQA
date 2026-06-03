package com.example.qlqa.data.local.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.annotation.NonNull;

@Entity(
    tableName = "accounts",
    foreignKeys = @ForeignKey(
        entity = Staff.class,
        parentColumns = "staffId",
        childColumns = "staffId",
        onDelete = ForeignKey.CASCADE
    ),
    indices = {@Index(value = "username", unique = true), @Index(value = "staffId")}
)
public class Account {
    @PrimaryKey(autoGenerate = true)
    private int accountId;

    @NonNull
    private String username;
    
    @NonNull
    private String passwordHash;
    
    @NonNull
    private String role; // Admin / Staff
    
    private int staffId;
    
    @NonNull
    private String status; // Hoạt động / khóa
    
    @NonNull
    private String createdAt;

    public Account(@NonNull String username, @NonNull String passwordHash, @NonNull String role, int staffId, @NonNull String status, @NonNull String createdAt) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.staffId = staffId;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getAccountId() { return accountId; }
    public void setAccountId(int accountId) { this.accountId = accountId; }

    @NonNull
    public String getUsername() { return username; }
    public void setUsername(@NonNull String username) { this.username = username; }

    @NonNull
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(@NonNull String passwordHash) { this.passwordHash = passwordHash; }

    @NonNull
    public String getRole() { return role; }
    public void setRole(@NonNull String role) { this.role = role; }

    public int getStaffId() { return staffId; }
    public void setStaffId(int staffId) { this.staffId = staffId; }

    @NonNull
    public String getStatus() { return status; }
    public void setStatus(@NonNull String status) { this.status = status; }

    @NonNull
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(@NonNull String createdAt) { this.createdAt = createdAt; }
}
