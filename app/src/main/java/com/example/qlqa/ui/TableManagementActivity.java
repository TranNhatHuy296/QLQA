package com.example.qlqa.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import com.example.qlqa.R;
import com.example.qlqa.data.local.entities.Table;
import com.example.qlqa.databinding.ActivityTableManagementBinding;
import com.example.qlqa.databinding.DialogTableEditorBinding;
import com.example.qlqa.ui.adapter.TableAdapter;
import com.example.qlqa.utils.SessionManager;
import com.example.qlqa.viewmodel.TableManagementViewModel;
import com.google.android.material.tabs.TabLayout;

public class TableManagementActivity extends AppCompatActivity {

    private ActivityTableManagementBinding binding;
    private TableManagementViewModel viewModel;
    private TableAdapter adapter;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTableManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        if (!"Admin".equalsIgnoreCase(sessionManager.getRole())) {
            Toast.makeText(this, "Chỉ Quản trị viên mới có quyền truy cập!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(TableManagementViewModel.class);
        setupUI();
        observeViewModel();
        viewModel.loadTables();
    }

    private void setupUI() {
        binding.btnBack.setOnClickListener(v -> finish());
        
        // Notification click
        binding.btnNotification.setOnClickListener(v -> {
            startActivity(new Intent(this, NotificationActivity.class));
        });

        // Setup RecyclerView
        adapter = new TableAdapter(this::showTableEditorDialog);
        binding.rvTables.setLayoutManager(new GridLayoutManager(this, 2));
        binding.rvTables.setAdapter(adapter);

        // Setup Tabs
        binding.tabLayoutArea.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewModel.setArea(tab.getText().toString());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        binding.fabAddTable.setOnClickListener(v -> showTableEditorDialog(null));

        // Bottom Nav Logic
        binding.navHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, DashboardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
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

    private void observeViewModel() {
        viewModel.getTableList().observe(this, adapter::setTables);
        
        viewModel.getStats().observe(this, stats -> {
            binding.tvTotalTables.setText(String.valueOf(stats[0]));
            binding.tvEmptyTables.setText(String.valueOf(stats[1]));
            binding.tvOccupiedTables.setText(String.valueOf(stats[2]));
            binding.tvCleaningTables.setText(String.valueOf(stats[3]));
        });

        viewModel.getErrorMessage().observe(this, msg -> {
            if (msg != null) Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });

        viewModel.getOperationSuccess().observe(this, success -> {
            if (success) Toast.makeText(this, "Thao tác thành công", Toast.LENGTH_SHORT).show();
        });
    }

    private void showTableEditorDialog(Table existingTable) {
        DialogTableEditorBinding dialogBinding = DialogTableEditorBinding.inflate(getLayoutInflater());
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogBinding.getRoot())
                .create();

        // Setup Spinners
        String[] areas = {"Tầng 1", "Tầng 2", "Sân vườn"};
        ArrayAdapter<String> areaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, areas);
        areaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dialogBinding.spinnerArea.setAdapter(areaAdapter);

        String[] statuses = {"Trống", "Có khách", "Đang dọn"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statuses);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dialogBinding.spinnerStatus.setAdapter(statusAdapter);

        final int[] seats = {4};
        final int[] currentCustomers = {0};

        if (existingTable != null) {
            dialogBinding.tvDialogTitle.setText("Chi tiết / Chỉnh sửa bàn");
            dialogBinding.etTableName.setText(existingTable.getTableName());
            seats[0] = existingTable.getSeats();
            currentCustomers[0] = existingTable.getCurrentCustomers();
            dialogBinding.tvSeats.setText(String.valueOf(seats[0]));
            dialogBinding.tvCurrentCustomers.setText(String.valueOf(currentCustomers[0]));
            
            for (int i = 0; i < areas.length; i++) {
                if (areas[i].equals(existingTable.getArea())) {
                    dialogBinding.spinnerArea.setSelection(i);
                    break;
                }
            }
            for (int i = 0; i < statuses.length; i++) {
                if (statuses[i].equals(existingTable.getStatus())) {
                    dialogBinding.spinnerStatus.setSelection(i);
                    break;
                }
            }
        }

        // Logic to show/hide current customers based on status
        dialogBinding.spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedStatus = statuses[position];
                if ("Có khách".equals(selectedStatus)) {
                    dialogBinding.layoutCurrentCustomers.setVisibility(View.VISIBLE);
                } else {
                    dialogBinding.layoutCurrentCustomers.setVisibility(View.GONE);
                    currentCustomers[0] = 0;
                    dialogBinding.tvCurrentCustomers.setText("0");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        dialogBinding.btnIncreaseSeats.setOnClickListener(v -> {
            seats[0]++;
            dialogBinding.tvSeats.setText(String.valueOf(seats[0]));
        });

        dialogBinding.btnDecreaseSeats.setOnClickListener(v -> {
            if (seats[0] > 1) {
                seats[0]--;
                dialogBinding.tvSeats.setText(String.valueOf(seats[0]));
            }
        });

        dialogBinding.btnIncreaseCustomers.setOnClickListener(v -> {
            if (currentCustomers[0] < seats[0]) {
                currentCustomers[0]++;
                dialogBinding.tvCurrentCustomers.setText(String.valueOf(currentCustomers[0]));
            } else {
                Toast.makeText(this, "Số khách không được vượt quá số chỗ!", Toast.LENGTH_SHORT).show();
            }
        });

        dialogBinding.btnDecreaseCustomers.setOnClickListener(v -> {
            if (currentCustomers[0] > 0) {
                currentCustomers[0]--;
                dialogBinding.tvCurrentCustomers.setText(String.valueOf(currentCustomers[0]));
            }
        });

        dialogBinding.btnSaveTable.setOnClickListener(v -> {
            String name = dialogBinding.etTableName.getText().toString().trim();
            String area = dialogBinding.spinnerArea.getSelectedItem().toString();
            String status = dialogBinding.spinnerStatus.getSelectedItem().toString();

            if (name.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập số bàn / tên bàn", Toast.LENGTH_SHORT).show();
                return;
            }

            Table table;
            if (existingTable != null) {
                table = existingTable;
                table.setTableName(name);
                table.setArea(area);
                table.setSeats(seats[0]);
                table.setStatus(status);
                table.setCurrentCustomers(currentCustomers[0]);
            } else {
                table = new Table(name, area, seats[0], status, currentCustomers[0], viewModel.getCurrentDateTime());
            }

            viewModel.saveTable(table, existingTable != null);
            dialog.dismiss();
        });

        dialogBinding.btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}
