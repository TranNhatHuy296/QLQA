package com.example.qlqa.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.qlqa.R;
import com.example.qlqa.databinding.ActivityStaffDashboardBinding;
import com.example.qlqa.ui.adapter.NotificationAdapter;
import com.example.qlqa.ui.adapter.TableAdapter;
import com.example.qlqa.ui.auth.ChangePasswordActivity;
import com.example.qlqa.ui.auth.LoginActivity;
import com.example.qlqa.utils.SessionManager;
import com.example.qlqa.viewmodel.DashboardViewModel;
import com.google.android.material.tabs.TabLayout;

public class StaffDashboardActivity extends AppCompatActivity {

    private ActivityStaffDashboardBinding binding;
    private SessionManager sessionManager;
    private DashboardViewModel viewModel;
    private TableAdapter tableAdapter;
    private NotificationAdapter notificationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStaffDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            navigateToLogin();
            return;
        }

        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        setupUI();
        setupSidebar();
        setupRecyclerViews();
        observeViewModel();
        
        // Load default data
        viewModel.loadDashboardData();
        loadTables("Tầng 1");
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.loadDashboardData();
        // Reload current tab
        int selectedTabPosition = binding.tabLayout.getSelectedTabPosition();
        if (selectedTabPosition != -1) {
            TabLayout.Tab tab = binding.tabLayout.getTabAt(selectedTabPosition);
            if (tab != null) loadTables(tab.getText().toString());
        }
    }

    private void setupUI() {
        binding.ivMenu.setOnClickListener(v -> binding.drawerLayout.openDrawer(GravityCompat.START));

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

        binding.header.setOnClickListener(v -> binding.cvNotificationPopup.setVisibility(View.GONE));

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                loadTables(tab.getText().toString());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Bấm vào mục "Đơn hàng" ở menu dưới cùng để sang trang Quản lý bàn và thanh toán
        binding.navOrders.setOnClickListener(v -> {
            startActivity(new Intent(this, PaymentActivity.class));
        });
        
        // Set info for small header in Dashboard
        binding.tvRole.setText(sessionManager.getRole());
    }

    private void loadTables(String area) {
        viewModel.loadTablesByArea(area);
    }

    private void setupRecyclerViews() {
        // Tables Grid
        tableAdapter = new TableAdapter(table -> {
            // Revert: Luôn chuyển sang màn hình chọn món (Menu) dù bàn có khách hay không
            if ("Đang dọn".equalsIgnoreCase(table.getStatus())) {
                Toast.makeText(this, "Bàn đang được dọn dẹp, chưa thể nhận khách.", Toast.LENGTH_SHORT).show();
            } else {
                // Điều hướng sang màn hình chọn món
                Intent intent = new Intent(this, OrderMenuActivity.class);
                intent.putExtra("TABLE_ID", table.getTableId());
                intent.putExtra("TABLE_NAME", table.getTableName());
                intent.putExtra("AREA", table.getArea());
                startActivity(intent);
            }
        });
        binding.rvTables.setLayoutManager(new GridLayoutManager(this, 2));
        binding.rvTables.setAdapter(tableAdapter);

        // Notification Popup List
        notificationAdapter = new NotificationAdapter(notification -> {
            binding.cvNotificationPopup.setVisibility(View.GONE);
            startActivity(new Intent(this, NotificationActivity.class));
        });
        binding.rvNotificationPopup.setLayoutManager(new LinearLayoutManager(this));
        binding.rvNotificationPopup.setAdapter(notificationAdapter);
    }

    private void observeViewModel() {
        viewModel.getActiveTables().observe(this, active -> {
            binding.tvActiveTables.setText(active);
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

        viewModel.getTables().observe(this, tables -> {
            if (tables != null) {
                tableAdapter.setTables(tables);
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

        sidebar.findViewById(R.id.btnNavHomeSidebar).setOnClickListener(v -> {
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
