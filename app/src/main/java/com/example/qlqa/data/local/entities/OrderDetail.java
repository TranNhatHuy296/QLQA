package com.example.qlqa.data.local.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.annotation.NonNull;

@Entity(
    tableName = "order_details",
    foreignKeys = {
        @ForeignKey(
            entity = Order.class,
            parentColumns = "orderId",
            childColumns = "orderId",
            onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
            entity = MenuItem.class,
            parentColumns = "menuItemId",
            childColumns = "menuItemId",
            onDelete = ForeignKey.RESTRICT
        )
    },
    indices = {@Index("orderId"), @Index("menuItemId")}
)
public class OrderDetail {
    @PrimaryKey(autoGenerate = true)
    private int orderDetailId;

    private int orderId;
    private int menuItemId;
    
    private int quantity;
    private double unitPrice;
    private double discount; // Mặc định 0
    private double subtotal;

    public OrderDetail(int orderId, int menuItemId, int quantity, double unitPrice, double discount, double subtotal) {
        this.orderId = orderId;
        this.menuItemId = menuItemId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.discount = discount;
        this.subtotal = subtotal;
    }

    public int getOrderDetailId() { return orderDetailId; }
    public void setOrderDetailId(int orderDetailId) { this.orderDetailId = orderDetailId; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getMenuItemId() { return menuItemId; }
    public void setMenuItemId(int menuItemId) { this.menuItemId = menuItemId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public double getDiscount() { return discount; }
    public void setDiscount(double discount) { this.discount = discount; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
}
