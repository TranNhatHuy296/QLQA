package com.example.qlqa.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.qlqa.data.local.AppDatabase;
import com.example.qlqa.data.local.entities.Notification;
import com.example.qlqa.data.local.entities.Order;
import com.example.qlqa.data.local.entities.Table;
import com.example.qlqa.data.local.model.OrderWithDetails;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DashboardViewModel extends AndroidViewModel {
    private final AppDatabase db;
    private final ExecutorService executorService;

    private final MutableLiveData<String> revenue = new MutableLiveData<>("0₫");
    private final MutableLiveData<String> ordersCount = new MutableLiveData<>("0");
    private final MutableLiveData<String> activeTables = new MutableLiveData<>("0");
    private final MutableLiveData<String> bestSeller = new MutableLiveData<>("---");
    private final MutableLiveData<List<Notification>> notifications = new MutableLiveData<>();
    private final MutableLiveData<Boolean> hasUnreadNotifications = new MutableLiveData<>(false);
    private final MutableLiveData<List<Table>> tables = new MutableLiveData<>();

    public DashboardViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInstance(application);
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<String> getRevenue() { return revenue; }
    public LiveData<String> getOrdersCount() { return ordersCount; }
    public LiveData<String> getActiveTables() { return activeTables; }
    public LiveData<String> getBestSeller() { return bestSeller; }
    public LiveData<List<Notification>> getNotifications() { return notifications; }
    public LiveData<Boolean> getHasUnreadNotifications() { return hasUnreadNotifications; }
    public LiveData<List<Table>> getTables() { return tables; }

    public void loadDashboardData() {
        executorService.execute(() -> {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            String startDate = df.format(cal.getTime());
            
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            String endDate = df.format(cal.getTime());

            // 1. Revenue & Orders
            Double rev = db.orderDao().getTotalRevenue(startDate, endDate);
            Integer orders = db.orderDao().getTotalPaidOrders(startDate, endDate);
            revenue.postValue(rev != null ? String.format(Locale.getDefault(), "%,.0f₫", rev) : "0₫");
            ordersCount.postValue(orders != null ? String.valueOf(orders) : "0");

            // 2. Active Tables Count
            int activeCount = db.tableDao().getOccupiedTablesCount();
            int totalCount = db.tableDao().getTotalTablesCount();
            activeTables.postValue(String.format(Locale.getDefault(), "%02d / %02d", activeCount, totalCount));

            // 3. Notifications (Top 5)
            List<Notification> allNotifs = db.notificationDao().getAllNotifications();
            if (allNotifs.size() > 5) {
                notifications.postValue(allNotifs.subList(0, 5));
            } else {
                notifications.postValue(allNotifs);
            }
            
            List<Notification> unread = db.notificationDao().getUnreadNotifications();
            hasUnreadNotifications.postValue(!unread.isEmpty());

            // 4. Best Seller
            bestSeller.postValue("Cà phê sữa");
        });
    }

    public void loadTablesByArea(String area) {
        executorService.execute(() -> {
            List<Table> list = db.tableDao().getTablesByArea(area);
            tables.postValue(list);
        });
    }
}
