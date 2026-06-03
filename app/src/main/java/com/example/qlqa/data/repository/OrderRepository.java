package com.example.qlqa.data.repository;

import android.app.Application;
import com.example.qlqa.data.local.AppDatabase;
import com.example.qlqa.data.local.dao.OrderDao;
import com.example.qlqa.data.local.entities.Order;
import com.example.qlqa.data.local.model.OrderWithDetails;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OrderRepository {
    private final OrderDao orderDao;
    private final ExecutorService executorService;

    public OrderRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        orderDao = db.orderDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public interface Callback<T> {
        void onResult(T result);
    }

    public void searchOrders(String query, String status, Callback<List<OrderWithDetails>> callback) {
        executorService.execute(() -> {
            String searchQuery = "%" + query + "%";
            callback.onResult(orderDao.searchOrders(searchQuery, status));
        });
    }

    public void updateOrderStatus(int orderId, String newStatus, Callback<Boolean> callback) {
        executorService.execute(() -> {
            Order order = orderDao.getOrderById(orderId);
            if (order != null) {
                order.setStatus(newStatus);
                orderDao.update(order);
                callback.onResult(true);
            } else {
                callback.onResult(false);
            }
        });
    }

    public void getOrderStats(Callback<int[]> callback) {
        executorService.execute(() -> {
            int total = orderDao.getTotalCount();
            int pending = orderDao.getCountByStatus("Đang chờ");
            int preparing = orderDao.getCountByStatus("Đang chuẩn bị");
            int completed = orderDao.getCountByStatus("Đã xong");
            callback.onResult(new int[]{total, pending, preparing, completed});
        });
    }
}
