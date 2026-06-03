package com.example.qlqa.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.qlqa.R;
import com.example.qlqa.data.utils.DataSeeder;
import com.example.qlqa.databinding.ActivityDashboardBinding;
import com.example.qlqa.ui.adapter.NotificationAdapter;
import com.example.qlqa.ui.auth.ChangePasswordActivity;
import com.example.qlqa.ui.auth.LoginActivity;
import com.example.qlqa.utils.SessionManager;
import com.example.qlqa.viewmodel.DashboardViewModel;

public class DashboardActivity extends AppCompatActivity {

    private ActivityDashboardBinding binding;
    private SessionManager sessionManager;
    private DashboardViewModel viewModel;
    private NotificationAdapter notificationAdapter;

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

        // Ensure data is seeded if database is empty (e.g., after migration/wipe)
        DataSeeder.seedData(this);

        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        
        setupUI();
        setupSidebar();
        setupRecyclerView();
        observeViewModel();
        
        // Initial load
        viewModel.loadDashboardData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.loadDashboardData();
    }

    private void setupUI() {
        // Sidebar / Menu icon click
        binding.ivMenu.setOnClickListener(v -> binding.drawerLayout.openDrawer(GravityCompat.START));

        // Profile Avatar in Header
        binding.ivAvatar.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
        });

        // Notification click
        binding.flNotification.setOnClickListener(v -> {
            if (binding.cvNotificationPopup.getVisibility() == View.VISIBLE) {
                binding.cvNotificationPopup.setVisibility(View.GONE);
            } else {
                binding.cvNotificationPopup.setVisibility(View.VISIBLE);
                viewModel.loadDashboardData();
            }
        });

        binding.btnViewAllNotifications.setOnClickListener(v -> {
            binding.cvNotificationPopup.setVisibility(View.GONE);
            startActivity(new Intent(this, NotificationActivity.class));
        });

        // Close popup when clicking on header background
        binding.header.setOnClickListener(v -> binding.cvNotificationPopup.setVisibility(View.GONE));

        // Bottom Navigation
        binding.navMenu.setOnClickListener(v -> {
            startActivity(new Intent(this, CategoryManagementActivity.class));
        });
        
        binding.navManage.setOnClickListener(v -> {
            startActivity(new Intent(this, ManageActivity.class));
        });

        binding.navReport.setOnClickListener(v -> {
            if ("Admin".equalsIgnoreCase(sessionManager.getRole())) {
                startActivity(new Intent(this, RevenueReportActivity.class));
            } else {
                Toast.makeText(this, "Chỉ Quản trị viên mới được xem báo cáo!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerView() {
        notificationAdapter = new NotificationAdapter(notification -> {
            binding.cvNotificationPopup.setVisibility(View.GONE);
            startActivity(new Intent(this, NotificationActivity.class));
        });
        binding.rvNotificationPopup.setLayoutManager(new LinearLayoutManager(this));
        binding.rvNotificationPopup.setAdapter(notificationAdapter);
    }

    private void observeViewModel() {
        viewModel.getRevenue().observe(this, value -> {
            if (value != null) binding.tvRevenueValue.setText(value);
        });
        viewModel.getOrdersCount().observe(this, value -> {
            if (value != null) binding.tvOrdersValue.setText(value);
        });
        viewModel.getActiveTables().observe(this, value -> {
            if (value != null) binding.tvTablesValue.setText(value);
        });
        viewModel.getBestSeller().observe(this, value -> {
            if (value != null) binding.tvBestSellerName.setText(value);
        });
        
        viewModel.getHasUnreadNotifications().observe(this, hasUnread -> {
            binding.vNotificationBadge.setVisibility(hasUnread ? View.VISIBLE : View.GONE);
        });

        viewModel.getNotifications().observe(this, list -> {
            if (list == null || list.isEmpty()) {
                binding.tvEmptyNotification.setVisibility(View.VISIBLE);
                binding.rvNotificationPopup.setVisibility(View.GONE);
            } else {
                binding.tvEmptyNotification.setVisibility(View.GONE);
                binding.rvNotificationPopup.setVisibility(View.VISIBLE);
                notificationAdapter.setNotifications(list);
            }
        });
    }

    private void setupSidebar() {
        View sidebar = binding.sidebarContainer;
        TextView tvName = sidebar.findViewById(R.id.nav_tvName);
        TextView tvRole = sidebar.findViewById(R.id.nav_tvRole);
        
        String username = sessionManager.getUsername();
        if (username != null) {
            tvName.setText(username);
            tvRole.setText(sessionManager.getRole());
        }

        sidebar.findViewById(R.id.btnNavHome).setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        });

        sidebar.findViewById(R.id.btnNavProfile).setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(this, ProfileActivity.class));
        });

        sidebar.findViewById(R.id.btnNavSettings).setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(this, ChangePasswordActivity.class));
        });

        sidebar.findViewById(R.id.btnNavLogout).setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            sessionManager.logout();
            navigateToLogin();
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
