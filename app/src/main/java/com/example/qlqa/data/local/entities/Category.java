package com.example.qlqa.data.local.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "categories")
public class Category {
    @PrimaryKey(autoGenerate = true)
    private int categoryId;

    @NonNull
    private String categoryName;
    private String description;
    private String imageUrl;
    
    @NonNull
    private String status; // Hoạt động / ẩn

    public Category(@NonNull String categoryName, String description, @NonNull String status) {
        this.categoryName = categoryName;
        this.description = description;
        this.status = status;
    }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    @NonNull
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(@NonNull String categoryName) { this.categoryName = categoryName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    @NonNull
    public String getStatus() { return status; }
    public void setStatus(@NonNull String status) { this.status = status; }
}
