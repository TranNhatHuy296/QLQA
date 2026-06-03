package com.example.qlqa.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.qlqa.data.repository.UserRepository;

public class ChangePasswordViewModel extends AndroidViewModel {
    private final UserRepository userRepository;
    private final MutableLiveData<String> errorCurrentPassword = new MutableLiveData<>();
    private final MutableLiveData<String> errorNewPassword = new MutableLiveData<>();
    private final MutableLiveData<String> errorConfirmPassword = new MutableLiveData<>();
    private final MutableLiveData<Boolean> changeSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> toastMessage = new MutableLiveData<>();

    public ChangePasswordViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
    }

    public LiveData<String> getErrorCurrentPassword() { return errorCurrentPassword; }
    public LiveData<String> getErrorNewPassword() { return errorNewPassword; }
    public LiveData<String> getErrorConfirmPassword() { return errorConfirmPassword; }
    public LiveData<Boolean> getChangeSuccess() { return changeSuccess; }
    public LiveData<String> getToastMessage() { return toastMessage; }

    public void changePassword(String username, String currentPassword, String newPassword, String confirmPassword) {
        boolean isValid = true;

        if (currentPassword.isEmpty()) {
            errorCurrentPassword.setValue("Mật khẩu hiện tại không được để trống.");
            isValid = false;
        } else {
            errorCurrentPassword.setValue(null);
        }

        if (newPassword.isEmpty()) {
            errorNewPassword.setValue("Mật khẩu mới không được để trống.");
            isValid = false;
        } else if (!isValidPasswordPolicy(newPassword)) {
            errorNewPassword.setValue("Mật khẩu mới không hợp lệ.");
            isValid = false;
        } else if (newPassword.equals(currentPassword)) {
            errorNewPassword.setValue("Mật khẩu mới không được trùng với mật khẩu hiện tại.");
            isValid = false;
        } else {
            errorNewPassword.setValue(null);
        }

        if (confirmPassword.isEmpty()) {
            errorConfirmPassword.setValue("Xác nhận mật khẩu mới không được để trống.");
            isValid = false;
        } else if (!confirmPassword.equals(newPassword)) {
            errorConfirmPassword.setValue("Mật khẩu mới không trùng khớp.");
            isValid = false;
        } else {
            errorConfirmPassword.setValue(null);
        }

        if (!isValid) return;

        userRepository.changePassword(username, currentPassword, newPassword, result -> {
            if ("Success".equals(result)) {
                changeSuccess.postValue(true);
            } else {
                toastMessage.postValue(result);
            }
        });
    }

    private boolean isValidPasswordPolicy(String password) {
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
        return password.matches(passwordPattern);
    }
}
