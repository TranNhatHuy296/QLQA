package com.example.qlqa.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.qlqa.data.local.entities.Order;

import java.util.List;

@Dao
public interface OrderDao {
    @Insert
    long insert(Order order);

    @Update
    void update(Order order);

    @Delete
    void delete(Order order);

    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    List<Order> getAllOrders();

    @Query("SELECT * FROM orders WHERE tableId = :tableId AND status = 'Đang phục vụ' LIMIT 1")
    Order getActiveOrderByTable(int tableId);

    @Query("SELECT * FROM orders WHERE orderId = :id")
    Order getOrderById(int id);
}
