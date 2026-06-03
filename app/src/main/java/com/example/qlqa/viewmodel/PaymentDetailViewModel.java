package com.example.qlqa.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.qlqa.data.local.AppDatabase;
import com.example.qlqa.data.local.entities.Order;
import com.example.qlqa.data.local.entities.OrderHistory;
import com.example.qlqa.data.local.entities.Table;
import com.example.qlqa.data.local.model.OrderWithDetails;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PaymentDetailViewModel extends AndroidViewModel {
    private final AppDatabase db;
    private final ExecutorService executorService;

    private final MutableLiveData<OrderWithDetails> orderWithDetails = new MutableLiveData<>();
    private final MutableLiveData<Double> subtotal = new MutableLiveData<>(0.0);
    private final MutableLiveData<Double> taxAmount = new MutableLiveData<>(0.0);
    private final MutableLiveData<Double> discountAmount = new MutableLiveData<>(0.0);
    private final MutableLiveData<Double> grandTotal = new MutableLiveData<>(0.0);
    private final MutableLiveData<Boolean> paymentSuccess = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private double taxRate = 0.0; // Default 0%

    public PaymentDetailViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInstance(application);
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<OrderWithDetails> getOrderWithDetails() { return orderWithDetails; }
    public LiveData<Double> getSubtotal() { return subtotal; }
    public LiveData<Double> getTaxAmount() { return taxAmount; }
    public LiveData<Double> getDiscountAmount() { return discountAmount; }
    public LiveData<Double> getGrandTotal() { return grandTotal; }
    public LiveData<Boolean> getPaymentSuccess() { return paymentSuccess; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    public void loadOrder(int orderId) {
        executorService.execute(() -> {
            OrderWithDetails details = db.orderDao().getAllOrdersWithDetails()
                    .stream().filter(o -> o.order.getOrderId() == orderId).findFirst().orElse(null);
            
            if (details != null) {
                orderWithDetails.postValue(details);
                calculateTotals(details);
            } else {
                errorMessage.postValue("Không tìm thấy thông tin đơn hàng!");
            }
        });
    }

    public void loadActiveOrderForTable(int tableId) {
        executorService.execute(() -> {
            OrderWithDetails details = db.orderDao().getActiveOrderWithDetailsByTable(tableId);
            if (details != null) {
                orderWithDetails.postValue(details);
                calculateTotals(details);
            } else {
                errorMessage.postValue("Bàn này hiện không có hóa đơn chưa thanh toán!");
            }
        });
    }

    private void calculateTotals(OrderWithDetails details) {
        double sub = 0;
        if (details.details != null) {
            for (OrderWithDetails.OrderDetailWithItem item : details.details) {
                sub += item.orderDetail.getSubtotal();
            }
        }
        subtotal.postValue(sub);
        
        double tax = sub * taxRate;
        taxAmount.postValue(tax);
        
        double discount = discountAmount.getValue() != null ? discountAmount.getValue() : 0.0;
        double total = sub + tax - discount;
        grandTotal.postValue(total);
    }

    public void applyVoucher(String code) {
        if (subtotal.getValue() == null || subtotal.getValue() <= 0) return;

        if ("GIAM10".equalsIgnoreCase(code)) {
            discountAmount.postValue(subtotal.getValue() * 0.1);
            if (orderWithDetails.getValue() != null) calculateTotals(orderWithDetails.getValue());
        } else if ("FREE".equalsIgnoreCase(code)) {
            discountAmount.postValue(subtotal.getValue());
            if (orderWithDetails.getValue() != null) calculateTotals(orderWithDetails.getValue());
        } else {
            errorMessage.postValue("Mã giảm giá không hợp lệ hoặc đã hết hạn.");
        }
    }

    public void confirmPayment(int staffId, String paymentMethod) {
        OrderWithDetails currentOrder = orderWithDetails.getValue();
        if (currentOrder == null) return;

        executorService.execute(() -> {
            try {
                db.runInTransaction(() -> {
                    // 1. Update Order
                    Order order = currentOrder.order;
                    String previousStatus = order.getStatus();
                    order.setStatus("Đã thanh toán");
                    order.setTotalAmount(grandTotal.getValue());
                    order.setUpdatedAt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
                    db.orderDao().update(order);

                    // 2. Update Table
                    Table table = currentOrder.table;
                    if (table != null) {
                        table.setStatus("Trống");
                        table.setCurrentCustomers(0);
                        db.tableDao().update(table);
                    }

                    // 3. Log History
                    OrderHistory history = new OrderHistory(
                            order.getOrderId(),
                            staffId,
                            previousStatus,
                            "Đã thanh toán",
                            "Thanh toán qua " + paymentMethod,
                            order.getUpdatedAt()
                    );
                    db.orderHistoryDao().insert(history);
                });
                paymentSuccess.postValue(true);
            } catch (Exception e) {
                errorMessage.postValue("Lỗi thanh toán: " + e.getMessage());
            }
        });
    }
}
