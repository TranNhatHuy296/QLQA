package com.example.qlqa.ui.auth;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.qlqa.databinding.ActivityRegisterBinding;
import com.example.qlqa.viewmodel.RegisterViewModel;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private RegisterViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        setupObservers();
        setupListeners();
    }

    private void setupObservers() {
        viewModel.getErrorName().observe(this, error -> binding.tilFullName.setError(error));
        viewModel.getErrorEmail().observe(this, error -> binding.tilEmail.setError(error));
        viewModel.getErrorPhone().observe(this, error -> binding.tilPhone.setError(error));
        viewModel.getErrorPassword().observe(this, error -> binding.tilPassword.setError(error));

        viewModel.getRegisterSuccess().observe(this, success -> {
            if (success) {
                Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                finish(); // Go back to login
            }
        });

        viewModel.getToastMessage().observe(this, message -> {
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupListeners() {
        binding.btnRegister.setOnClickListener(v -> {
            String fullName = binding.edtFullName.getText().toString();
            String email = binding.edtEmail.getText().toString();
            String phone = binding.edtPhone.getText().toString();
            String password = binding.edtPassword.getText().toString();

            viewModel.register(fullName, email, phone, password);
        });

        binding.tvLoginNow.setOnClickListener(v -> finish());
    }
}
