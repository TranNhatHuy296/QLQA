package com.example.qlqa.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qlqa.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViews();
    }

    private void initViews() {
        binding.btnLogin.setOnClickListener(v -> handleLogin());

        binding.tvForgotPassword.setOnClickListener(v -> {
            // TODO: Navigate to Forgot Password screen
            Toast.makeText(this, "Chức năng Đổi mật khẩu", Toast.LENGTH_SHORT).show();
        });

        binding.tvSignUp.setOnClickListener(v -> {
            // TODO: Navigate to Sign Up screen
            Toast.makeText(this, "Chức năng Đăng ký", Toast.LENGTH_SHORT).show();
        });
    }

    private void handleLogin() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString();

        boolean isValid = true;

        // Reset errors
        binding.tilEmail.setError(null);
        binding.tilPassword.setError(null);

        // Validate Email
        if (TextUtils.isEmpty(email)) {
            binding.tilEmail.setError("Tài khoản không được để trống.");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.setError("Tài khoản không hợp lệ.");
            isValid = false;
        }

        // Validate Password
        if (TextUtils.isEmpty(password)) {
            binding.tilPassword.setError("Mật khẩu không được để trống.");
            isValid = false;
        } else if (!isValidPassword(password)) {
            binding.tilPassword.setError("Mật khẩu không chính xác."); // As per wireframe requirement for "Invalid format/wrong"
            isValid = false;
        }

        if (isValid) {
            // Perform authentication
            performAuth(email, password);
        }
    }

    private boolean isValidPassword(String password) {
        // Requirements: Min 8 chars, Upper, Lower, Number, Special
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
        return password.matches(passwordPattern);
    }

    private void performAuth(String email, String password) {
        // For now, mock authentication
        if (email.equals("admin@example.com") && password.equals("Admin@123")) {
            Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
            // Navigate to main/dashboard
        } else {
            Toast.makeText(this, "Tài khoản hoặc mật khẩu không đúng.", Toast.LENGTH_SHORT).show();
        }
    }
}
