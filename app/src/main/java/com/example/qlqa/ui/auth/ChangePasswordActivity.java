package com.example.qlqa.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.qlqa.databinding.ActivityChangePasswordBinding;
import com.example.qlqa.utils.SessionManager;
import com.example.qlqa.viewmodel.ChangePasswordViewModel;

public class ChangePasswordActivity extends AppCompatActivity {

    private ActivityChangePasswordBinding binding;
    private ChangePasswordViewModel viewModel;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(ChangePasswordViewModel.class);
        sessionManager = new SessionManager(this);

        setupListeners();
        observeViewModel();
    }

    private void setupListeners() {
        binding.btnConfirm.setOnClickListener(v -> {
            String currentPassword = binding.etCurrentPassword.getText().toString();
            String newPassword = binding.etNewPassword.getText().toString();
            String confirmPassword = binding.etConfirmPassword.getText().toString();
            
            String username = sessionManager.getUsername();
            if (username == null) {
                // This shouldn't happen if they reached here while logged in, 
                // but for security or if they clicked "forgot password" from login:
                // If it's "Forgot Password", logic might be different (e.g. OTP), 
                // but the UI says "Change Password" with "Current Password" field.
                Toast.makeText(this, "Vui lòng đăng nhập để đổi mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }
            
            viewModel.changePassword(username, currentPassword, newPassword, confirmPassword);
        });
        
        binding.btnCancel.setOnClickListener(v -> finish());
    }

    private void observeViewModel() {
        viewModel.getErrorCurrentPassword().observe(this, error -> {
            binding.tilCurrentPassword.setError(error);
        });

        viewModel.getErrorNewPassword().observe(this, error -> {
            binding.tilNewPassword.setError(error);
        });

        viewModel.getErrorConfirmPassword().observe(this, error -> {
            binding.tilConfirmPassword.setError(error);
        });

        viewModel.getChangeSuccess().observe(this, success -> {
            if (success) {
                Toast.makeText(this, "Cập nhật mật khẩu thành công. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
                sessionManager.logout();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        viewModel.getToastMessage().observe(this, message -> {
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
