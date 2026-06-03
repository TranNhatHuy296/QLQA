package com.example.qlqa.data.local.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "staff")
public class Staff {
    @PrimaryKey(autoGenerate = true)
    private int staffId;

    @NonNull
    private String employeeCode; // NV-YYYY-XXX
    
    @NonNull
    private String fullName;
    
    @NonNull
    private String email;
    
    @NonNull
    private String phoneNumber;
    
    private String position; // Phục vụ, Đầu bếp, Bảo vệ, Pha chế
    
    private String address;
    
    private String shifts; // JSON string or comma separated values like "Ca sáng, Ca chiều"
    
    private String profileImageUrl;

    private String dateOfBirth;
    
    @NonNull
    private String createdAt;
    
    @NonNull
    private String status; // Đã có mặt, Vắng mặt, Chưa bắt đầu ca

    public Staff(@NonNull String employeeCode, @NonNull String fullName, @NonNull String email, @NonNull String phoneNumber, 
                 String position, String address, String shifts, String profileImageUrl, String dateOfBirth, @NonNull String createdAt, @NonNull String status) {
        this.employeeCode = employeeCode;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.position = position;
        this.address = address;
        this.shifts = shifts;
        this.profileImageUrl = profileImageUrl;
        this.dateOfBirth = dateOfBirth;
        this.createdAt = createdAt;
        this.status = status;
    }

    public int getStaffId() { return staffId; }
    public void setStaffId(int staffId) { this.staffId = staffId; }

    @NonNull
    public String getEmployeeCode() { return employeeCode; }
    public void setEmployeeCode(@NonNull String employeeCode) { this.employeeCode = employeeCode; }

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

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getShifts() { return shifts; }
    public void setShifts(String shifts) { this.shifts = shifts; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    @NonNull
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(@NonNull String createdAt) { this.createdAt = createdAt; }

    @NonNull
    public String getStatus() { return status; }
    public void setStatus(@NonNull String status) { this.status = status; }
}
