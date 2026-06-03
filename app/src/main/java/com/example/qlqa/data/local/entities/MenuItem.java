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
    private String description;
    private double discount; // Mặc định 0
    private boolean isBestSeller;

    @NonNull
    private String status; // Còn hàng / Hết hàng
    
    @NonNull
    private String createdAt;

    public MenuItem(int categoryId, @NonNull String itemName, double price, String imageUrl, String description, double discount, boolean isBestSeller, @NonNull String status, @NonNull String createdAt) {
        this.categoryId = categoryId;
        this.itemName = itemName;
        this.price = price;
        this.imageUrl = imageUrl;
        this.description = description;
        this.discount = discount;
        this.isBestSeller = isBestSeller;
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

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getDiscount() { return discount; }
    public void setDiscount(double discount) { this.discount = discount; }

    public boolean isBestSeller() { return isBestSeller; }
    public void setBestSeller(boolean bestSeller) { isBestSeller = bestSeller; }

    @NonNull
    public String getStatus() { return status; }
    public void setStatus(@NonNull String status) { this.status = status; }

    @NonNull
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(@NonNull String createdAt) { this.createdAt = createdAt; }
}
