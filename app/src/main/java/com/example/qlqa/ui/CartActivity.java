package com.example.qlqa.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.qlqa.databinding.ActivityCartBinding;
import com.example.qlqa.ui.adapter.CartAdapter;
import com.example.qlqa.utils.SessionManager;
import com.example.qlqa.viewmodel.CartViewModel;

import java.text.NumberFormat;
import java.util.Locale;

public class CartActivity extends AppCompatActivity {

    private ActivityCartBinding binding;
    private CartViewModel viewModel;
    private CartAdapter cartAdapter;
    private SessionManager sessionManager;
    private int tableId;
    private String tableName;
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        tableId = getIntent().getIntExtra("TABLE_ID", -1);
        tableName = getIntent().getStringExtra("TABLE_NAME");

        viewModel = new ViewModelProvider(this).get(CartViewModel.class);
        
        setupUI();
        setupRecyclerView();
        observeViewModel();

        viewModel.loadCart(tableId);
    }

    private void setupUI() {
        binding.tvTableName.setText(tableName);
        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnConfirmOrder.setOnClickListener(v -> {
            String note = binding.etOrderNote.getText().toString();
            int staffId = sessionManager.getStaffId();
            viewModel.confirmOrder(tableId, staffId, note);
        });
    }

    private void setupRecyclerView() {
        cartAdapter = new CartAdapter(new CartAdapter.OnCartItemChangeListener() {
            @Override
            public void onQuantityChange(int menuItemId, int newQuantity) {
                viewModel.updateQuantity(menuItemId, newQuantity);
            }

            @Override
            public void onDelete(int menuItemId) {
                viewModel.removeItem(menuItemId);
            }
        });
        binding.rvCartItems.setLayoutManager(new LinearLayoutManager(this));
        binding.rvCartItems.setAdapter(cartAdapter);
    }

    private void observeViewModel() {
        viewModel.getCartItems().observe(this, items -> {
            cartAdapter.setItems(items);
            binding.tvTotalItems.setText("Tổng cộng (" + items.size() + " món)");
        });

        viewModel.getTotalPrice().observe(this, price -> {
            binding.tvGrandTotal.setText(currencyFormatter.format(price));
        });

        viewModel.getOrderSuccess().observe(this, success -> {
            if (success) {
                Toast.makeText(this, "Gửi đơn hàng thành công.", Toast.LENGTH_SHORT).show();
                // Quay về màn hình Menu/Sơ đồ bàn theo yêu cầu ban đầu
                finish();
            }
        });

        viewModel.getErrorMessage().observe(this, message -> {
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
