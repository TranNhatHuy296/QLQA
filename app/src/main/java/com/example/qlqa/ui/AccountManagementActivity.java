package com.example.qlqa.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.qlqa.R;
import com.example.qlqa.databinding.ActivityAccountManagementBinding;
import com.example.qlqa.databinding.DialogAccountEditorBinding;
import com.example.qlqa.ui.adapter.AccountAdapter;
import com.example.qlqa.utils.SessionManager;
import com.example.qlqa.viewmodel.AccountManagementViewModel;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AccountManagementActivity extends AppCompatActivity {

    private ActivityAccountManagementBinding binding;
    private AccountManagementViewModel viewModel;
    private AccountAdapter adapter;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAccountManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        // Check Admin access
        if (!"Admin".equalsIgnoreCase(sessionManager.getRole())) {
            Toast.makeText(this, "Chỉ Admin mới có quyền truy cập!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(AccountManagementViewModel.class);

        setupUI();
        observeViewModel();

        viewModel.loadStats();
        viewModel.loadAccounts();
    }

    private void setupUI() {
        binding.btnBack.setOnClickListener(v -> finish());

        // Notification click
        binding.btnNotification.setOnClickListener(v -> {
            startActivity(new Intent(this, NotificationActivity.class));
        });

        // RecyclerView
        adapter = new AccountAdapter(item -> showEditDialog(item));
        binding.rvAccounts.setLayoutManager(new LinearLayoutManager(this));
        binding.rvAccounts.setAdapter(adapter);

        // Search
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setSearch(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Filter
        binding.btnFilter.setOnClickListener(v -> showFilterDialog());

        // FAB
        binding.fabAddAccount.setOnClickListener(v -> showAddDialog());
    }

    private void observeViewModel() {
        viewModel.getStats().observe(this, stats -> {
            binding.tvStatTotal.setText(String.valueOf(stats[0]));
            binding.tvStatActive.setText(String.valueOf(stats[1]));
            binding.tvStatLocked.setText(String.valueOf(stats[2]));
        });

        viewModel.getAccounts().observe(this, list -> {
            adapter.setItems(list);
        });

        viewModel.getTotalFilteredCount().observe(this, total -> {
            updatePagination(total);
        });

        viewModel.getOperationSuccess().observe(this, success -> {
            if (success) {
                Toast.makeText(this, "Thao tác thành công", Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updatePagination(int total) {
        int pageSize = viewModel.getPageSize();
        int currentPage = viewModel.getCurrentPage();
        int start = total == 0 ? 0 : (currentPage * pageSize) + 1;
        int end = Math.min((currentPage + 1) * pageSize, total);

        binding.tvPaginationInfo.setText(String.format("Hiển thị %d-%d trong %d tài khoản", start, end, total));

        // Create page buttons
        binding.paginationControls.removeAllViews();
        int totalPages = (int) Math.ceil((double) total / pageSize);

        for (int i = 0; i < totalPages; i++) {
            MaterialButton btn = new MaterialButton(this, null, com.google.android.material.R.attr.materialButtonStyle);
            btn.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
            btn.setText(String.valueOf(i + 1));
            btn.setPadding(0,0,0,0);
            btn.setMinWidth(100);
            
            if (i == currentPage) {
                btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF000613));
                btn.setTextColor(0xFFFFFFFF);
            } else {
                btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFFFFFFF));
                btn.setTextColor(0xFF1A1C1E);
                btn.setStrokeColor(android.content.res.ColorStateList.valueOf(0xFFC4C6CF));
                btn.setStrokeWidth(2);
            }

            final int pageIndex = i;
            btn.setOnClickListener(v -> viewModel.setPage(pageIndex));
            binding.paginationControls.addView(btn);
        }
    }

    private void showAddDialog() {
        showEditorDialog(null);
    }

    private void showEditDialog(com.example.qlqa.data.local.dao.AccountDao.AccountWithStaffResult item) {
        showEditorDialog(item);
    }

    private String selectedRole = null;
    private String selectedStatus = null;

    private void showEditorDialog(com.example.qlqa.data.local.dao.AccountDao.AccountWithStaffResult item) {
        DialogAccountEditorBinding dialogBinding = DialogAccountEditorBinding.inflate(getLayoutInflater());
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogBinding.getRoot())
                .create();

        if (item != null) {
            dialogBinding.tvDialogTitle.setText("Chỉnh sửa tài khoản");
            dialogBinding.etUsername.setText(item.account.getUsername());
            dialogBinding.etUsername.setEnabled(false); // Username usually immutable
            selectedRole = item.account.getRole();
            selectedStatus = item.account.getStatus();
            updateDialogSelectionUI(dialogBinding);
        } else {
            selectedRole = "Admin"; // Default
            selectedStatus = "Hoạt động"; // Default
            updateDialogSelectionUI(dialogBinding);
        }

        dialogBinding.btnRoleAdmin.setOnClickListener(v -> { selectedRole = "Admin"; updateDialogSelectionUI(dialogBinding); });
        dialogBinding.btnRoleManager.setOnClickListener(v -> { selectedRole = "Quản lý"; updateDialogSelectionUI(dialogBinding); });
        dialogBinding.btnRoleStaff.setOnClickListener(v -> { selectedRole = "Nhân viên"; updateDialogSelectionUI(dialogBinding); });
        dialogBinding.btnRoleAccountant.setOnClickListener(v -> { selectedRole = "Kế toán"; updateDialogSelectionUI(dialogBinding); });

        dialogBinding.btnStatusActive.setOnClickListener(v -> { selectedStatus = "Hoạt động"; updateDialogSelectionUI(dialogBinding); });
        dialogBinding.btnStatusLocked.setOnClickListener(v -> { selectedStatus = "Tạm khóa"; updateDialogSelectionUI(dialogBinding); });

        dialogBinding.btnSaveAccount.setOnClickListener(v -> {
            String username = dialogBinding.etUsername.getText().toString();
            String password = dialogBinding.etPassword.getText().toString();
            
            if (item == null && password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập mật khẩu cho tài khoản mới", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.saveAccount(username, password, selectedRole, selectedStatus, item != null ? item.account.getStaffId() : null);
            dialog.dismiss();
        });

        dialogBinding.btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void updateDialogSelectionUI(DialogAccountEditorBinding db) {
        // Reset roles
        db.btnRoleAdmin.setStrokeWidth(2); db.btnRoleAdmin.setBackgroundTintList(null);
        db.btnRoleManager.setStrokeWidth(2); db.btnRoleManager.setBackgroundTintList(null);
        db.btnRoleStaff.setStrokeWidth(2); db.btnRoleStaff.setBackgroundTintList(null);
        db.btnRoleAccountant.setStrokeWidth(2); db.btnRoleAccountant.setBackgroundTintList(null);

        if ("Admin".equals(selectedRole)) {
            db.btnRoleAdmin.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0x1AFF851B));
            db.btnRoleAdmin.setStrokeColor(android.content.res.ColorStateList.valueOf(0xFFFF851B));
            db.btnRoleAdmin.setStrokeWidth(4);
        } else if ("Quản lý".equals(selectedRole)) {
            db.btnRoleManager.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0x1AFF851B));
            db.btnRoleManager.setStrokeColor(android.content.res.ColorStateList.valueOf(0xFFFF851B));
            db.btnRoleManager.setStrokeWidth(4);
        } else if ("Nhân viên".equals(selectedRole)) {
            db.btnRoleStaff.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0x1AFF851B));
            db.btnRoleStaff.setStrokeColor(android.content.res.ColorStateList.valueOf(0xFFFF851B));
            db.btnRoleStaff.setStrokeWidth(4);
        } else if ("Kế toán".equals(selectedRole)) {
            db.btnRoleAccountant.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0x1AFF851B));
            db.btnRoleAccountant.setStrokeColor(android.content.res.ColorStateList.valueOf(0xFFFF851B));
            db.btnRoleAccountant.setStrokeWidth(4);
        }

        // Reset status
        db.btnStatusActive.setStrokeWidth(2); db.btnStatusActive.setBackgroundTintList(null);
        db.btnStatusLocked.setStrokeWidth(2); db.btnStatusLocked.setBackgroundTintList(null);

        if ("Hoạt động".equals(selectedStatus) || "Đang hoạt động".equals(selectedStatus)) {
            db.btnStatusActive.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0x1AFF851B));
            db.btnStatusActive.setStrokeColor(android.content.res.ColorStateList.valueOf(0xFFFF851B));
            db.btnStatusActive.setStrokeWidth(4);
        } else if ("Tạm khóa".equals(selectedStatus)) {
            db.btnStatusLocked.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0x1AFF851B));
            db.btnStatusLocked.setStrokeColor(android.content.res.ColorStateList.valueOf(0xFFFF851B));
            db.btnStatusLocked.setStrokeWidth(4);
        }
    }

    private void showFilterDialog() {
        // Simplified filter dialog
        String[] statuses = {"Đang hoạt động", "Tạm khóa"};
        boolean[] checked = {false, false};
        new AlertDialog.Builder(this)
                .setTitle("Lọc theo trạng thái")
                .setMultiChoiceItems(statuses, checked, (dialog, which, isChecked) -> {
                    checked[which] = isChecked;
                })
                .setPositiveButton("Áp dụng", (dialog, which) -> {
                    List<String> selectedStatuses = new ArrayList<>();
                    if (checked[0]) selectedStatuses.add("Hoạt động"); // Map UI to DB
                    if (checked[0]) selectedStatuses.add("Đang hoạt động");
                    if (checked[1]) selectedStatuses.add("Tạm khóa");
                    if (checked[1]) selectedStatuses.add("Bị khóa");
                    viewModel.setFilters(null, selectedStatuses);
                })
                .setNegativeButton("Xóa lọc", (dialog, which) -> viewModel.setFilters(null, null))
                .show();
    }
}
