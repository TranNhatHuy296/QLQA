package com.example.qlqa.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.qlqa.R;
import com.example.qlqa.data.local.entities.Staff;
import com.example.qlqa.databinding.ActivityStaffManagementBinding;
import com.example.qlqa.databinding.DialogStaffEditorBinding;
import com.example.qlqa.ui.adapter.StaffAdapter;
import com.example.qlqa.utils.SessionManager;
import com.example.qlqa.viewmodel.StaffManagementViewModel;
import com.google.android.material.chip.Chip;
import java.util.ArrayList;
import java.util.List;

public class StaffManagementActivity extends AppCompatActivity {

    private ActivityStaffManagementBinding binding;
    private StaffManagementViewModel viewModel;
    private StaffAdapter adapter;
    private SessionManager sessionManager;
    
    private String selectedAvatarUri = null;
    private DialogStaffEditorBinding currentDialogBinding;

    private final ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null && currentDialogBinding != null) {
                    selectedAvatarUri = uri.toString();
                    currentDialogBinding.ivStaffAvatarPreview.setVisibility(View.VISIBLE);
                    currentDialogBinding.ivStaffAvatarPreview.setImageURI(uri);
                    currentDialogBinding.ivAvatarPlaceholder.setVisibility(View.GONE);
                    currentDialogBinding.tvAvatarLabel.setVisibility(View.GONE);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStaffManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        if (!"Admin".equalsIgnoreCase(sessionManager.getRole())) {
            Toast.makeText(this, "Chỉ Quản trị viên mới có quyền truy cập!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(StaffManagementViewModel.class);
        setupUI();
        observeViewModel();
        viewModel.loadStaff();
    }

    private void setupUI() {
        binding.btnBack.setOnClickListener(v -> finish());
        
        // Notification click
        binding.btnNotification.setOnClickListener(v -> {
            startActivity(new Intent(this, NotificationActivity.class));
        });

        adapter = new StaffAdapter(this::showStaffDetails);
        binding.rvStaff.setLayoutManager(new LinearLayoutManager(this));
        binding.rvStaff.setAdapter(adapter);

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

        binding.cgPositionFilter.setOnCheckedChangeListener((group, checkedId) -> {
            Chip chip = findViewById(checkedId);
            if (chip != null) {
                viewModel.setPositionFilter(chip.getText().toString());
            }
        });

        binding.fabAddStaff.setOnClickListener(v -> showAddStaffDialog());
        
        // Bottom Nav
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
        viewModel.getStaffList().observe(this, list -> adapter.setItems(list));
        
        viewModel.getStats().observe(this, stats -> {
            binding.tvTotalStaff.setText(String.valueOf(stats[0]));
            binding.tvPresentStaff.setText("Đang có mặt: " + stats[1]);
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        });

        viewModel.getOperationSuccess().observe(this, success -> {
            if (success) Toast.makeText(this, "Thao tác thành công", Toast.LENGTH_SHORT).show();
        });
    }

    private void showAddStaffDialog() {
        showStaffEditorDialog(null);
    }

    private void showStaffDetails(Staff staff) {
        showStaffEditorDialog(staff);
    }

    private void showStaffEditorDialog(Staff existingStaff) {
        currentDialogBinding = DialogStaffEditorBinding.inflate(getLayoutInflater());
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(currentDialogBinding.getRoot())
                .create();

        selectedAvatarUri = null;

        if (existingStaff != null) {
            currentDialogBinding.tvDialogTitle.setText("Chi tiết / Chỉnh sửa");
            currentDialogBinding.etFullName.setText(existingStaff.getFullName());
            currentDialogBinding.etEmail.setText(existingStaff.getEmail());
            currentDialogBinding.etPhoneNumber.setText(existingStaff.getPhoneNumber());
            currentDialogBinding.etAddress.setText(existingStaff.getAddress());
            currentDialogBinding.tvEmployeeCode.setText(existingStaff.getEmployeeCode());
            
            // Position
            String pos = existingStaff.getPosition();
            if ("Phục vụ".equals(pos)) currentDialogBinding.rbWaiter.setChecked(true);
            else if ("Đầu bếp".equals(pos)) currentDialogBinding.rbChef.setChecked(true);
            else if ("Bảo vệ".equals(pos)) currentDialogBinding.rbSecurity.setChecked(true);
            else if ("Pha chế".equals(pos)) currentDialogBinding.rbBarista.setChecked(true);

            // Status
            String status = existingStaff.getStatus();
            if ("Đã có mặt".equals(status)) currentDialogBinding.chipPresent.setChecked(true);
            else if ("Vắng mặt".equals(status)) currentDialogBinding.chipAbsent.setChecked(true);
            else currentDialogBinding.chipNotStarted.setChecked(true);

            // Shifts
            String shifts = existingStaff.getShifts();
            if (shifts != null) {
                if (shifts.contains("Ca sáng")) currentDialogBinding.cbMorningShift.setChecked(true);
                if (shifts.contains("Ca chiều")) currentDialogBinding.cbAfternoonShift.setChecked(true);
                if (shifts.contains("Ca tối")) currentDialogBinding.cbNightShift.setChecked(true);
            }

            if (existingStaff.getProfileImageUrl() != null) {
                currentDialogBinding.ivStaffAvatarPreview.setVisibility(View.VISIBLE);
                currentDialogBinding.ivStaffAvatarPreview.setImageURI(Uri.parse(existingStaff.getProfileImageUrl()));
                currentDialogBinding.ivAvatarPlaceholder.setVisibility(View.GONE);
                currentDialogBinding.tvAvatarLabel.setVisibility(View.GONE);
                selectedAvatarUri = existingStaff.getProfileImageUrl();
            }
        } else {
            currentDialogBinding.chipNotStarted.setChecked(true); // Default status for new staff
            viewModel.prepareNewStaff();
            viewModel.getGeneratedCode().observe(this, code -> {
                if (currentDialogBinding != null) currentDialogBinding.tvEmployeeCode.setText(code);
            });
        }

        currentDialogBinding.btnUploadAvatar.setOnClickListener(v -> {
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });

        currentDialogBinding.btnSaveStaff.setOnClickListener(v -> {
            if (saveStaffFromUI(existingStaff)) dialog.dismiss();
        });

        currentDialogBinding.btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private boolean saveStaffFromUI(Staff existingStaff) {
        String name = currentDialogBinding.etFullName.getText().toString().trim();
        String email = currentDialogBinding.etEmail.getText().toString().trim();
        String phone = currentDialogBinding.etPhoneNumber.getText().toString().trim();
        String address = currentDialogBinding.etAddress.getText().toString().trim();
        String code = currentDialogBinding.tvEmployeeCode.getText().toString();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Position
        int checkedPosId = currentDialogBinding.cgPosition.getCheckedChipId();
        if (checkedPosId == View.NO_ID) {
            Toast.makeText(this, "Vui lòng chọn vị trí", Toast.LENGTH_SHORT).show();
            return false;
        }
        String position = ((Chip) currentDialogBinding.cgPosition.findViewById(checkedPosId)).getText().toString();

        // Status
        int checkedStatusId = currentDialogBinding.cgStaffStatus.getCheckedChipId();
        String status = "Chưa bắt đầu ca";
        if (checkedStatusId != View.NO_ID) {
            status = ((Chip) currentDialogBinding.cgStaffStatus.findViewById(checkedStatusId)).getText().toString();
        }

        // Shifts
        List<String> selectedShifts = new ArrayList<>();
        if (currentDialogBinding.cbMorningShift.isChecked()) selectedShifts.add("Ca sáng");
        if (currentDialogBinding.cbAfternoonShift.isChecked()) selectedShifts.add("Ca chiều");
        if (currentDialogBinding.cbNightShift.isChecked()) selectedShifts.add("Ca tối");

        if (selectedShifts.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ít nhất một ca làm việc", Toast.LENGTH_SHORT).show();
            return false;
        }

        String shiftsString = String.join(", ", selectedShifts);
        String createdAt = existingStaff != null ? existingStaff.getCreatedAt() : viewModel.getCurrentDateTime();
        String dateOfBirth = existingStaff != null ? existingStaff.getDateOfBirth() : "";

        Staff staff = new Staff(code, name, email, phone, position, address, shiftsString, selectedAvatarUri, dateOfBirth, createdAt, status);
        if (existingStaff != null) staff.setStaffId(existingStaff.getStaffId());

        viewModel.saveStaff(staff, existingStaff != null);
        return true;
    }
}
