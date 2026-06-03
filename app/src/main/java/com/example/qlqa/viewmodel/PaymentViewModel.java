package com.example.qlqa.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.qlqa.data.local.AppDatabase;
import com.example.qlqa.data.local.entities.Order;
import com.example.qlqa.data.local.entities.Table;
import com.example.qlqa.data.local.model.OrderWithDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PaymentViewModel extends AndroidViewModel {
    private final AppDatabase db;
    private final ExecutorService executorService;

    private final MutableLiveData<List<Table>> occupiedTables = new MutableLiveData<>();
    private final MutableLiveData<Integer> activeTablesCount = new MutableLiveData<>(0);
    private final MutableLiveData<List<OrderWithDetails>> activeOrders = new MutableLiveData<>();

    public PaymentViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInstance(application);
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Table>> getOccupiedTables() { return occupiedTables; }
    public LiveData<Integer> getActiveTablesCount() { return activeTablesCount; }
    public LiveData<List<OrderWithDetails>> getActiveOrders() { return activeOrders; }

    public void loadOccupiedTables(String area) {
        executorService.execute(() -> {
            List<Table> tables;
            if ("Tất cả".equals(area)) {
                tables = db.tableDao().getOccupiedTables();
            } else {
                tables = db.tableDao().getOccupiedTablesByArea(area);
            }
            occupiedTables.postValue(tables);
            activeTablesCount.postValue(db.tableDao().getOccupiedTablesCount());

            List<OrderWithDetails> orders = new ArrayList<>();
            for (Table table : tables) {
                OrderWithDetails order = db.orderDao().getActiveOrderWithDetailsByTable(table.getTableId());
                if (order != null) {
                    orders.add(order);
                }
            }
            activeOrders.postValue(orders);
        });
    }
}
