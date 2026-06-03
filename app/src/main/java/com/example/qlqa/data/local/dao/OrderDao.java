package com.example.qlqa.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.qlqa.data.local.entities.Order;
import com.example.qlqa.data.local.model.OrderWithDetails;

import java.util.List;

@Dao
public interface OrderDao {
    @Insert
    long insert(Order order);

    @Update
    void update(Order order);

    @Delete
    void delete(Order order);

    @Transaction
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    List<OrderWithDetails> getAllOrdersWithDetails();

    @Transaction
    @Query("SELECT orders.* FROM orders " +
           "INNER JOIN tables ON orders.tableId = tables.tableId " +
           "WHERE (:status = 'Tất cả' OR orders.status = :status) " +
           "AND (orders.orderId LIKE :query OR tables.tableName LIKE :query) " +
           "ORDER BY orders.createdAt DESC")
    List<OrderWithDetails> searchOrders(String query, String status);

    @Query("SELECT COUNT(*) FROM orders WHERE status = :status")
    int getCountByStatus(String status);

    @Query("SELECT COUNT(*) FROM orders")
    int getTotalCount();

    @Query("SELECT * FROM orders WHERE orderId = :id")
    Order getOrderById(int id);

    // Revenue Report Queries
    @Query("SELECT SUM(totalAmount) FROM orders WHERE status = 'Đã thanh toán' AND createdAt BETWEEN :startDate AND :endDate")
    Double getTotalRevenue(String startDate, String endDate);

    @Query("SELECT COUNT(*) FROM orders WHERE status = 'Đã thanh toán' AND createdAt BETWEEN :startDate AND :endDate")
    Integer getTotalPaidOrders(String startDate, String endDate);

    @Transaction
    @Query("SELECT * FROM orders WHERE status = 'Đã thanh toán' AND createdAt BETWEEN :startDate AND :endDate ORDER BY createdAt DESC LIMIT :limit")
    List<OrderWithDetails> getRecentPaidOrders(String startDate, String endDate, int limit);

    @Query("SELECT SUM(totalAmount) FROM orders WHERE status = 'Đã thanh toán' AND staffId = :staffId AND createdAt BETWEEN :startDate AND :endDate")
    Double getTotalRevenueByStaff(int staffId, String startDate, String endDate);

    @Query("SELECT COUNT(*) FROM orders WHERE status = 'Đã thanh toán' AND staffId = :staffId AND createdAt BETWEEN :startDate AND :endDate")
    Integer getTotalPaidOrdersByStaff(int staffId, String startDate, String endDate);

    @Transaction
    @Query("SELECT * FROM orders WHERE status = 'Đã thanh toán' AND staffId = :staffId AND createdAt BETWEEN :startDate AND :endDate ORDER BY createdAt DESC")
    List<OrderWithDetails> getOrdersByStaff(int staffId, String startDate, String endDate);
}
