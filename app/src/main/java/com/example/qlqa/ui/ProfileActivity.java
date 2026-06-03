package com.example.qlqa.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.qlqa.databinding.ActivityProfileBinding;
import com.example.qlqa.ui.auth.ChangePasswordActivity;
import com.example.qlqa.ui.auth.LoginActivity;
import com.example.qlqa.utils.SessionManager;
import com.example.qlqa.viewmodel.ProfileViewModel;

import java.util.Calendar;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private ProfileViewModel viewModel;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        sessionManager = new SessionManager(this);

        initViews();
        observeViewModel();

        String username = sessionManager.getUsername();
        if (username != null) {
            viewModel.loadProfile(username);
        } else {
            finish();
        }
    }

    private void initViews() {
        binding.btnBack.setOnClickListener(v -> finish());

        binding.etBirthday.setOnClickListener(v -> showDatePicker());

        binding.btnEditAvatar.setOnClickListener(v -> {
            // TODO: Implement image picker
            Toast.makeText(this, "Chức năng chọn ảnh đang được phát triển", Toast.LENGTH_SHORT).show();
        });

        binding.btnChangePassword.setOnClickListener(v -> {
            startActivity(new Intent(this, ChangePasswordActivity.class));
        });

        binding.btnLogout.setOnClickListener(v -> showLogoutConfirmation());

        binding.btnSave.setOnClickListener(v -> {
            String name = binding.etName.getText().toString();
            String email = binding.etEmail.getText().toString();
            String phone = binding.etPhone.getText().toString();
            String address = binding.etAddress.getText().toString();
            String birthday = binding.etBirthday.getText().toString();

            viewModel.saveProfile(name, email, phone, address, birthday);
        });

        // Language selection mock
        binding.tvLanguage.setOnClickListener(v -> {
            String[] languages = {"Tiếng Việt", "English"};
            new AlertDialog.Builder(this)
                    .setTitle("Chọn ngôn ngữ")
                    .setItems(languages, (dialog, which) -> {
                        binding.tvLanguage.setText(languages[which]);
                    })
                    .show();
        });
    }

    private void observeViewModel() {
        viewModel.getStaffData().observe(this, staff -> {
            if (staff != null) {
                binding.etName.setText(staff.getFullName());
                binding.etEmail.setText(staff.getEmail());
                binding.etPhone.setText(staff.getPhoneNumber());
                binding.etAddress.setText(staff.getAddress());
                binding.etBirthday.setText(staff.getDateOfBirth());
                binding.tvFullName.setText(staff.getFullName());
            }
        });

        viewModel.getErrorEmail().observe(this, error -> binding.tilEmail.setError(error));
        viewModel.getErrorPhone().observe(this, error -> binding.tilPhone.setError(error));

        viewModel.getUpdateSuccess().observe(this, success -> {
            if (success) {
                showSuccessToast();
            }
        });

        viewModel.getToastMessage().observe(this, message -> {
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getShowEmptyFieldsAlert().observe(this, show -> {
            if (show) {
                showEmptyFieldsAlert();
            }
        });
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String date = String.format("%02d/%02d/%d", dayOfMonth, monthOfYear + 1, year1);
                    binding.etBirthday.setText(date);
                }, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("Xác nhận", (dialog, which) -> {
                    sessionManager.logout();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showEmptyFieldsAlert() {
        new AlertDialog.Builder(this)
                .setTitle("Cảnh báo")
                .setMessage("Không được bỏ trống các thông tin bắt buộc.")
                .setNegativeButton("Hủy", (dialog, which) -> {
                    viewModel.resetAlert();
                    // Reload original data
                    String username = sessionManager.getUsername();
                    if (username != null) viewModel.loadProfile(username);
                })
                .show();
    }

    private void showSuccessToast() {
        // Simple Toast for now, could be custom view as per wireframe
        Toast.makeText(this, "Cập nhật thông tin thành công.", Toast.LENGTH_SHORT).show();
    }
}
