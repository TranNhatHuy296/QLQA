package com.example.qlqa.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import com.example.qlqa.R;
import com.example.qlqa.databinding.ActivityDashboardBinding;
import com.example.qlqa.ui.auth.ChangePasswordActivity;
import com.example.qlqa.ui.auth.LoginActivity;
import com.example.qlqa.utils.SessionManager;

public class DashboardActivity extends AppCompatActivity {

    private ActivityDashboardBinding binding;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            navigateToLogin();
            return;
        }

        setupUI();
        setupSidebar();
    }

    private void setupUI() {
        // Notification click
        binding.flNotification.setOnClickListener(v -> {
            if (binding.cvNotificationPopup.getVisibility() == View.VISIBLE) {
                binding.cvNotificationPopup.setVisibility(View.GONE);
            } else {
                binding.cvNotificationPopup.setVisibility(View.VISIBLE);
            }
        });

        binding.btnViewAllNotifications.setOnClickListener(v -> {
            Toast.makeText(this, "Xem tất cả thông báo", Toast.LENGTH_SHORT).show();
            binding.cvNotificationPopup.setVisibility(View.GONE);
        });

        // Close popup when clicking outside (on the main scroll view)
        binding.header.setOnClickListener(v -> binding.cvNotificationPopup.setVisibility(View.GONE));

        // Quick Actions
        binding.btnOrderHistory.setOnClickListener(v -> {
            Toast.makeText(this, "Điều hướng đến Lịch sử đơn hàng", Toast.LENGTH_SHORT).show();
        });

        binding.btnExportReport.setOnClickListener(v -> {
            Toast.makeText(this, "Đang xuất báo cáo tài chính...", Toast.LENGTH_SHORT).show();
        });

        // Bottom Navigation (Placeholders)
        binding.navMenu.setOnClickListener(v -> Toast.makeText(this, "Thực đơn", Toast.LENGTH_SHORT).show());
        binding.navManage.setOnClickListener(v -> Toast.makeText(this, "Quản lý", Toast.LENGTH_SHORT).show());
        binding.navReport.setOnClickListener(v -> Toast.makeText(this, "Báo cáo", Toast.LENGTH_SHORT).show());
        
        // Sidebar / Menu icon
        binding.ivMenu.setOnClickListener(v -> binding.drawerLayout.openDrawer(GravityCompat.START));
    }

    private void setupSidebar() {
        View headerView = binding.navView.getHeaderView(0);
        TextView tvName = headerView.findViewById(R.id.nav_tvName);
        TextView tvRole = headerView.findViewById(R.id.nav_tvRole);
        
        String username = sessionManager.getUsername();
        if (username != null) {
            tvName.setText(username);
            // In a real app, you'd fetch the user's full name and role from DB
            tvRole.setText("Quản trị viên");
        }

        binding.navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                binding.drawerLayout.closeDrawer(GravityCompat.START);
            } else if (id == R.id.nav_profile) {
                Toast.makeText(this, "Thông tin cá nhân", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(this, ChangePasswordActivity.class));
            } else if (id == R.id.nav_logout) {
                sessionManager.logout();
                navigateToLogin();
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else if (binding.cvNotificationPopup.getVisibility() == View.VISIBLE) {
            binding.cvNotificationPopup.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }
}
