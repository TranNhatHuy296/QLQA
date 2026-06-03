package com.example.qlqa.data.local.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.annotation.NonNull;

@Entity(
    tableName = "menu_items",
    foreignKeys = @ForeignKey(
        entity = Category.class,
        parentColumns = "categoryId",
        childColumns = "categoryId",
        onDelete = ForeignKey.RESTRICT
    ),
    indices = {@Index("categoryId")}
)
public class MenuItem {
    @PrimaryKey(autoGenerate = true)
    private int menuItemId;

    private int categoryId;
    
    @NonNull
    private String itemName;
    
    private double price;
    private String imageUrl;
    private double discount; // Mặc định 0
    
    @NonNull
    private String status; // Đang bán / Tạm ngừng
    
    @NonNull
    private String createdAt;

    public MenuItem(int categoryId, @NonNull String itemName, double price, String imageUrl, double discount, @NonNull String status, @NonNull String createdAt) {
        this.categoryId = categoryId;
        this.itemName = itemName;
        this.price = price;
        this.imageUrl = imageUrl;
        this.discount = discount;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getMenuItemId() { return menuItemId; }
    public void setMenuItemId(int menuItemId) { this.menuItemId = menuItemId; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    @NonNull
    public String getItemName() { return itemName; }
    public void setItemName(@NonNull String itemName) { this.itemName = itemName; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public double getDiscount() { return discount; }
    public void setDiscount(double discount) { this.discount = discount; }

    @NonNull
    public String getStatus() { return status; }
    public void setStatus(@NonNull String status) { this.status = status; }

    @NonNull
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(@NonNull String createdAt) { this.createdAt = createdAt; }
}
