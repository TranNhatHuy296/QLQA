package com.example.qlqa.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.qlqa.data.local.AppDatabase;
import com.example.qlqa.data.local.entities.MenuItem;
import com.example.qlqa.data.local.entities.Order;
import com.example.qlqa.data.local.entities.OrderDetail;
import com.example.qlqa.data.local.entities.Table;
import com.example.qlqa.data.local.model.OrderWithDetails;
import com.example.qlqa.data.repository.CartRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CartViewModel extends AndroidViewModel {
    private final AppDatabase db;
    private final ExecutorService executorService;
    private final CartRepository cartRepository;

    private final MutableLiveData<List<OrderWithDetails.OrderDetailWithItem>> cartItems = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Double> totalPrice = new MutableLiveData<>(0.0);
    private final MutableLiveData<Boolean> orderSuccess = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public CartViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInstance(application);
        executorService = Executors.newSingleThreadExecutor();
        cartRepository = CartRepository.getInstance();
    }

    public LiveData<List<OrderWithDetails.OrderDetailWithItem>> getCartItems() { return cartItems; }
    public LiveData<Double> getTotalPrice() { return totalPrice; }
    public LiveData<Boolean> getOrderSuccess() { return orderSuccess; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    public void loadCart(int tableId) {
        calculateTotals();
    }

    public void updateQuantity(int menuItemId, int newQuantity) {
        Map<Integer, MenuItem> itemCache = cartRepository.getItemCache();
        MenuItem item = itemCache.get(menuItemId);
        if (item != null) {
            cartRepository.updateCart(item, newQuantity);
            calculateTotals();
        }
    }

    public void removeItem(int menuItemId) {
        Map<Integer, MenuItem> itemCache = cartRepository.getItemCache();
        MenuItem item = itemCache.get(menuItemId);
        if (item != null) {
            cartRepository.updateCart(item, 0);
            calculateTotals();
        }
    }

    private void calculateTotals() {
        Map<Integer, Integer> cartMap = cartRepository.getCartMap();
        Map<Integer, MenuItem> itemCache = cartRepository.getItemCache();
        
        List<OrderWithDetails.OrderDetailWithItem> list = new ArrayList<>();
        double total = 0;
        
        for (Map.Entry<Integer, Integer> entry : cartMap.entrySet()) {
            MenuItem item = itemCache.get(entry.getKey());
            if (item != null) {
                OrderWithDetails.OrderDetailWithItem detailWithItem = new OrderWithDetails.OrderDetailWithItem();
                detailWithItem.menuItem = item;
                
                double subtotal = item.getPrice() * entry.getValue();
                detailWithItem.orderDetail = new OrderDetail(0, item.getMenuItemId(), entry.getValue(), item.getPrice(), 0, subtotal);
                
                list.add(detailWithItem);
                total += subtotal;
            }
        }
        
        cartItems.postValue(list);
        totalPrice.postValue(total);
    }

    public void confirmOrder(int tableId, int staffId, String note) {
        // Kiểm tra staffId hợp lệ để tránh lỗi FOREIGN KEY constraint failed
        if (staffId == -1) {
            errorMessage.postValue("Lỗi: Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại!");
            return;
        }

        if (totalPrice.getValue() == null || totalPrice.getValue() <= 0) {
            errorMessage.postValue("Giỏ hàng trống!");
            return;
        }

        executorService.execute(() -> {
            try {
                String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                
                // 1. Tạo đơn hàng (Order)
                Order order = new Order(tableId, staffId, "Đang phục vụ", null, totalPrice.getValue(), currentTime, currentTime);
                long orderId = db.orderDao().insert(order);

                // 2. Tạo chi tiết đơn hàng (OrderDetails)
                List<OrderWithDetails.OrderDetailWithItem> currentItems = cartItems.getValue();
                if (currentItems != null) {
                    for (OrderWithDetails.OrderDetailWithItem item : currentItems) {
                        OrderDetail detail = item.orderDetail;
                        detail.setOrderId((int) orderId);
                        db.orderDetailDao().insert(detail);
                    }
                }

                // 3. Cập nhật trạng thái bàn
                Table table = db.tableDao().getTableById(tableId);
                if (table != null) {
                    table.setStatus("Có khách");
                    table.setCurrentCustomers(1);
                    db.tableDao().update(table);
                }

                // 4. Xóa giỏ hàng tạm thời
                cartRepository.clearCart();
                
                orderSuccess.postValue(true);
            } catch (Exception e) {
                e.printStackTrace();
                errorMessage.postValue("Lỗi hệ thống: " + e.getMessage());
            }
        });
    }
}
