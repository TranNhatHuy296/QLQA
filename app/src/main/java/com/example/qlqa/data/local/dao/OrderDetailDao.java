package com.example.qlqa.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.qlqa.data.local.entities.OrderDetail;

import java.util.List;

@Dao
public interface OrderDetailDao {
    @Insert
    long insert(OrderDetail orderDetail);

    @Update
    void update(OrderDetail orderDetail);

    @Delete
    void delete(OrderDetail orderDetail);

    @Query("SELECT * FROM order_details WHERE orderId = :orderId")
    List<OrderDetail> getDetailsByOrderId(int orderId);
}
