package com.example.qlqa.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.qlqa.data.local.entities.Account;
import com.example.qlqa.data.repository.UserRepository;

public class LoginViewModel extends AndroidViewModel {
    private final UserRepository userRepository;
    private final MutableLiveData<String> errorEmail = new MutableLiveData<>();
    private final MutableLiveData<String> errorPassword = new MutableLiveData<>();
    private final MutableLiveData<Account> loginSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> toastMessage = new MutableLiveData<>();

    public LoginViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
    }

    public LiveData<String> getErrorEmail() { return errorEmail; }
    public LiveData<String> getErrorPassword() { return errorPassword; }
    public LiveData<Account> getLoginSuccess() { return loginSuccess; }
    public LiveData<String> getToastMessage() { return toastMessage; }

    public void login(String email, String password) {
        boolean isValid = true;

        if (email.trim().isEmpty()) {
            errorEmail.setValue("Tài khoản không được để trống.");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorEmail.setValue("Tài khoản không hợp lệ.");
            isValid = false;
        } else {
            errorEmail.setValue(null);
        }

        if (password.isEmpty()) {
            errorPassword.setValue("Mật khẩu không được để trống.");
            isValid = false;
        } else {
            errorPassword.setValue(null);
        }

        if (!isValid) return;

        userRepository.login(email, password, account -> {
            if (account != null) {
                if ("Hoạt động".equals(account.getStatus())) {
                    loginSuccess.postValue(account);
                } else {
                    toastMessage.postValue("Tài khoản đã bị khóa.");
                }
            } else {
                toastMessage.postValue("Tài khoản hoặc mật khẩu không đúng.");
            }
        });
    }
}
