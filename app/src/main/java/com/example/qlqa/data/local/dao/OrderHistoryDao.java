package com.example.qlqa.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.qlqa.data.local.entities.OrderHistory;

import java.util.List;

@Dao
public interface OrderHistoryDao {
    @Insert
    long insert(OrderHistory history);

    @Query("SELECT * FROM order_history WHERE orderId = :orderId ORDER BY changedAt DESC")
    List<OrderHistory> getHistoryByOrderId(int orderId);
}
