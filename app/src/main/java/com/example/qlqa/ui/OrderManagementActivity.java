package com.example.qlqa.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.qlqa.data.repository.OrderRepository;
import com.example.qlqa.databinding.ActivityOrderManagementBinding;
import com.example.qlqa.ui.adapter.OrderAdapter;
import com.example.qlqa.utils.SessionManager;
import com.example.qlqa.viewmodel.OrderManagementViewModel;
import com.google.android.material.tabs.TabLayout;

public class OrderManagementActivity extends AppCompatActivity {

    private ActivityOrderManagementBinding binding;
    private OrderManagementViewModel viewModel;
    private OrderAdapter adapter;
    private SessionManager sessionManager;
    private final String[] statuses = {"Tất cả", "Đang chờ", "Đang chuẩn bị", "Đã xong"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        if (!"Admin".equalsIgnoreCase(sessionManager.getRole())) {
            Toast.makeText(this, "Chỉ Quản trị viên mới có quyền truy cập!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(OrderManagementViewModel.class);
        setupUI();
        observeViewModel();
        viewModel.loadOrders();
    }

    private void setupUI() {
        binding.btnBack.setOnClickListener(v -> finish());
        
        // Notification click
        binding.btnNotification.setOnClickListener(v -> {
            startActivity(new Intent(this, NotificationActivity.class));
        });

        // Setup RecyclerView
        adapter = new OrderAdapter((orderId, nextStatus) -> viewModel.updateStatus(orderId, nextStatus));
        binding.rvOrders.setLayoutManager(new LinearLayoutManager(this));
        binding.rvOrders.setAdapter(adapter);

        // Setup Tabs
        for (String status : statuses) {
            binding.tabLayoutStatus.addTab(binding.tabLayoutStatus.newTab().setText(status + " (0)"));
        }

        binding.tabLayoutStatus.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String status = statuses[tab.getPosition()];
                viewModel.setFilter(binding.etSearch.getText().toString(), status);
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Search Logic
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String status = statuses[binding.tabLayoutStatus.getSelectedTabPosition()];
                viewModel.setFilter(s.toString(), status);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void observeViewModel() {
        viewModel.getOrderList().observe(this, adapter::setOrders);
        
        viewModel.getStats().observe(this, stats -> {
            for (int i = 0; i < statuses.length; i++) {
                TabLayout.Tab tab = binding.tabLayoutStatus.getTabAt(i);
                if (tab != null) {
                    tab.setText(statuses[i] + " (" + stats[i] + ")");
                }
            }
        });

        viewModel.getErrorMessage().observe(this, msg -> {
            if (msg != null) Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });
    }
}
