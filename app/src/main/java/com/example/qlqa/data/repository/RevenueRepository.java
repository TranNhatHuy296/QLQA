package com.example.qlqa.data.repository;

import android.app.Application;
import com.example.qlqa.data.local.AppDatabase;
import com.example.qlqa.data.local.dao.OrderDao;
import com.example.qlqa.data.local.dao.StaffDao;
import com.example.qlqa.data.local.entities.Staff;
import com.example.qlqa.data.local.model.OrderWithDetails;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RevenueRepository {
    private final OrderDao orderDao;
    private final StaffDao staffDao;
    private final ExecutorService executorService;

    public RevenueRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        orderDao = db.orderDao();
        staffDao = db.staffDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public interface Callback<T> {
        void onResult(T result);
    }

    public void getRevenueData(String startDate, String endDate, Integer staffId, Callback<RevenueData> callback) {
        executorService.execute(() -> {
            Double revenue;
            Integer totalOrders;
            List<OrderWithDetails> recentOrders;

            if (staffId == null || staffId == -1) {
                revenue = orderDao.getTotalRevenue(startDate, endDate);
                totalOrders = orderDao.getTotalPaidOrders(startDate, endDate);
                recentOrders = orderDao.getRecentPaidOrders(startDate, endDate, 20);
            } else {
                revenue = orderDao.getTotalRevenueByStaff(staffId, startDate, endDate);
                totalOrders = orderDao.getTotalPaidOrdersByStaff(staffId, startDate, endDate);
                recentOrders = orderDao.getOrdersByStaff(staffId, startDate, endDate);
            }

            callback.onResult(new RevenueData(
                revenue != null ? revenue : 0.0,
                totalOrders != null ? totalOrders : 0,
                recentOrders
            ));
        });
    }

    public void getAllStaff(Callback<List<Staff>> callback) {
        executorService.execute(() -> {
            callback.onResult(staffDao.getAllStaff());
        });
    }

    public static class RevenueData {
        public final double totalRevenue;
        public final int totalOrders;
        public final List<OrderWithDetails> recentOrders;

        public RevenueData(double totalRevenue, int totalOrders, List<OrderWithDetails> recentOrders) {
            this.totalRevenue = totalRevenue;
            this.totalOrders = totalOrders;
            this.recentOrders = recentOrders;
        }
    }
}
