package com.example.qlqa.data.local.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "staff")
public class Staff {
    @PrimaryKey(autoGenerate = true)
    private int staffId;

    @NonNull
    private String fullName;
    
    @NonNull
    private String email; // Thêm từ BA/Wireframe
    
    @NonNull
    private String phoneNumber;
    
    private String position;
    
    @NonNull
    private String createdAt;
    
    @NonNull
    private String status; // Hoạt động / nghỉ việc

    public Staff(@NonNull String fullName, @NonNull String email, @NonNull String phoneNumber, String position, @NonNull String createdAt, @NonNull String status) {
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.position = position;
        this.createdAt = createdAt;
        this.status = status;
    }

    public int getStaffId() { return staffId; }
    public void setStaffId(int staffId) { this.staffId = staffId; }

    @NonNull
    public String getFullName() { return fullName; }
    public void setFullName(@NonNull String fullName) { this.fullName = fullName; }

    @NonNull
    public String getEmail() { return email; }
    public void setEmail(@NonNull String email) { this.email = email; }

    @NonNull
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(@NonNull String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    @NonNull
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(@NonNull String createdAt) { this.createdAt = createdAt; }

    @NonNull
    public String getStatus() { return status; }
    public void setStatus(@NonNull String status) { this.status = status; }
}
