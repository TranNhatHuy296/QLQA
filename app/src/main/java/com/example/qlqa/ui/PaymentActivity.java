package com.example.qlqa.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.qlqa.databinding.ActivityPaymentBinding;
import com.example.qlqa.ui.adapter.PaymentTableAdapter;
import com.example.qlqa.utils.SessionManager;
import com.example.qlqa.viewmodel.PaymentViewModel;
import com.google.android.material.tabs.TabLayout;

public class PaymentActivity extends AppCompatActivity {

    private ActivityPaymentBinding binding;
    private PaymentViewModel viewModel;
    private PaymentTableAdapter adapter;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        viewModel = new ViewModelProvider(this).get(PaymentViewModel.class);

        setupUI();
        setupRecyclerView();
        observeViewModel();

        setupAreaTabs();
    }

    private void setupUI() {
        binding.tvRole.setText(sessionManager.getRole());
        
        binding.ivBack.setOnClickListener(v -> finish());
        
        binding.flNotification.setOnClickListener(v -> {
            startActivity(new Intent(this, NotificationActivity.class));
        });

        binding.navMap.setOnClickListener(v -> {
            // Navigate to Table Management / Map if needed
            finish();
        });

        binding.tabLayoutAreas.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewModel.loadOccupiedTables(tab.getText().toString());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupAreaTabs() {
        binding.tabLayoutAreas.addTab(binding.tabLayoutAreas.newTab().setText("Tất cả"));
        binding.tabLayoutAreas.addTab(binding.tabLayoutAreas.newTab().setText("Tầng 1"));
        binding.tabLayoutAreas.addTab(binding.tabLayoutAreas.newTab().setText("Tầng 2"));
        binding.tabLayoutAreas.addTab(binding.tabLayoutAreas.newTab().setText("Sân vườn"));
    }

    private void setupRecyclerView() {
        adapter = new PaymentTableAdapter(orderWithDetails -> {
            Intent intent = new Intent(this, PaymentDetailActivity.class);
            intent.putExtra("ORDER_ID", orderWithDetails.order.getOrderId());
            startActivity(intent);
        });
        binding.rvPaymentTables.setLayoutManager(new LinearLayoutManager(this));
        binding.rvPaymentTables.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.getActiveOrders().observe(this, orders -> {
            if (orders == null || orders.isEmpty()) {
                binding.rvPaymentTables.setVisibility(View.GONE);
                binding.tvEmptyMessage.setVisibility(View.VISIBLE);
            } else {
                binding.rvPaymentTables.setVisibility(View.VISIBLE);
                binding.tvEmptyMessage.setVisibility(View.GONE);
                adapter.setItems(orders);
            }
        });

        viewModel.getActiveTablesCount().observe(this, count -> {
            binding.tvActiveTablesCount.setText(count + " bàn đang sử dụng");
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        TabLayout.Tab selectedTab = binding.tabLayoutAreas.getTabAt(binding.tabLayoutAreas.getSelectedTabPosition());
        if (selectedTab != null) {
            viewModel.loadOccupiedTables(selectedTab.getText().toString());
        }
    }
}
