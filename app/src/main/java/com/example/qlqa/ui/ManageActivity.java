package com.example.qlqa.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.qlqa.databinding.ActivityManageBinding;
import com.example.qlqa.utils.SessionManager;

public class ManageActivity extends AppCompatActivity {

    private ActivityManageBinding binding;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        setupUI();
    }

    private void setupUI() {
        // Notification - Toolbar icon
        binding.flNotification.setOnClickListener(v -> {
            startActivity(new Intent(this, NotificationActivity.class));
        });

        // Function Tiles
        binding.cardOrders.setOnClickListener(v -> {
            startActivity(new Intent(this, OrderManagementActivity.class));
        });

        binding.cardTables.setOnClickListener(v -> {
            startActivity(new Intent(this, TableManagementActivity.class));
        });

        binding.cardStaff.setOnClickListener(v -> {
            startActivity(new Intent(this, StaffManagementActivity.class));
        });

        binding.cardAccounts.setOnClickListener(v -> {
            startActivity(new Intent(this, AccountManagementActivity.class));
        });

        binding.cardNotifications.setOnClickListener(v -> {
            startActivity(new Intent(this, NotificationActivity.class));
        });

        // Bottom Navigation
        binding.navHome.setOnClickListener(v -> {
            finish(); // Go back to Dashboard
        });

        binding.navMenu.setOnClickListener(v -> {
            startActivity(new Intent(this, CategoryManagementActivity.class));
        });

        binding.navReport.setOnClickListener(v -> {
            if ("Admin".equalsIgnoreCase(sessionManager.getRole())) {
                startActivity(new Intent(this, RevenueReportActivity.class));
            } else {
                Toast.makeText(this, "Chỉ Quản trị viên mới được xem báo cáo!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
