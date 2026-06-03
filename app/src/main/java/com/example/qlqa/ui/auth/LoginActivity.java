package com.example.qlqa.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.qlqa.data.utils.DataSeeder;
import com.example.qlqa.databinding.ActivityLoginBinding;
import com.example.qlqa.ui.DashboardActivity;
import com.example.qlqa.utils.SessionManager;
import com.example.qlqa.viewmodel.LoginViewModel;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private LoginViewModel viewModel;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Seed sample data if database is empty
        DataSeeder.seedData(this);

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        sessionManager = new SessionManager(this);

        // Check if already logged in
        if (sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, DashboardActivity.class));
            finish();
        }

        initViews();
        observeViewModel();
    }

    private void initViews() {
        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString();
            viewModel.login(email, password);
        });

        binding.tvForgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(this, ChangePasswordActivity.class));
        });

        binding.tvSignUp.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    private void observeViewModel() {
        viewModel.getErrorEmail().observe(this, error -> {
            binding.tilEmail.setError(error);
        });

        viewModel.getErrorPassword().observe(this, error -> {
            binding.tilPassword.setError(error);
        });

        viewModel.getLoginSuccess().observe(this, account -> {
            if (account != null) {
                sessionManager.createLoginSession(account.getUsername(), account.getRole());
                Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, DashboardActivity.class));
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
