package com.example.qlqa.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.qlqa.data.local.model.OrderWithDetails;
import com.example.qlqa.data.repository.OrderRepository;
import java.util.List;

public class OrderManagementViewModel extends AndroidViewModel {
    private final OrderRepository repository;
    private final MutableLiveData<List<OrderWithDetails>> orderList = new MutableLiveData<>();
    private final MutableLiveData<int[]> stats = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private String currentQuery = "";
    private String currentStatus = "Tất cả";

    public OrderManagementViewModel(@NonNull Application application) {
        super(application);
        repository = new OrderRepository(application);
    }

    public LiveData<List<OrderWithDetails>> getOrderList() { return orderList; }
    public LiveData<int[]> getStats() { return stats; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    public void setFilter(String query, String status) {
        this.currentQuery = query;
        this.currentStatus = status;
        loadOrders();
    }

    public void loadOrders() {
        repository.searchOrders(currentQuery, currentStatus, orderList::postValue);
        repository.getOrderStats(stats::postValue);
    }

    public void updateStatus(int orderId, String newStatus) {
        repository.updateOrderStatus(orderId, newStatus, success -> {
            if (success) {
                loadOrders();
            } else {
                errorMessage.postValue("Không thể cập nhật trạng thái đơn hàng.");
            }
        });
    }
}
