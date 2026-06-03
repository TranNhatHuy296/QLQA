package com.example.qlqa.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.qlqa.data.local.entities.Account;
import com.example.qlqa.data.local.entities.Staff;
import com.example.qlqa.data.repository.UserRepository;
import com.example.qlqa.utils.PasswordUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegisterViewModel extends AndroidViewModel {
    private final UserRepository userRepository;
    private final MutableLiveData<String> errorName = new MutableLiveData<>();
    private final MutableLiveData<String> errorEmail = new MutableLiveData<>();
    private final MutableLiveData<String> errorPhone = new MutableLiveData<>();
    private final MutableLiveData<String> errorPassword = new MutableLiveData<>();
    private final MutableLiveData<Boolean> registerSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> toastMessage = new MutableLiveData<>();

    public RegisterViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
    }

    public LiveData<String> getErrorName() { return errorName; }
    public LiveData<String> getErrorEmail() { return errorEmail; }
    public LiveData<String> getErrorPhone() { return errorPhone; }
    public LiveData<String> getErrorPassword() { return errorPassword; }
    public LiveData<Boolean> getRegisterSuccess() { return registerSuccess; }
    public LiveData<String> getToastMessage() { return toastMessage; }

    public void register(String fullName, String email, String phone, String password) {
        boolean isValid = true;

        if (fullName.trim().isEmpty()) {
            errorName.setValue("Họ và tên không được để trống.");
            isValid = false;
        } else {
            errorName.setValue(null);
        }

        if (email.trim().isEmpty()) {
            errorEmail.setValue("Email không được để trống.");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorEmail.setValue("Email không hợp lệ.");
            isValid = false;
        } else {
            errorEmail.setValue(null);
        }

        if (phone.trim().isEmpty()) {
            errorPhone.setValue("SĐT không được để trống.");
            isValid = false;
        } else if (phone.length() != 10 || !phone.matches("\\d+")) {
            errorPhone.setValue("SĐT không hợp lệ.");
            isValid = false;
        } else {
            errorPhone.setValue(null);
        }

        if (password.trim().isEmpty()) {
            errorPassword.setValue("Mật khẩu không được để trống.");
            isValid = false;
        } else if (password.length() < 8) {
            errorPassword.setValue("Mật khẩu không hợp lệ.");
            isValid = false;
        } else {
            errorPassword.setValue(null);
        }

        if (!isValid) return;

        // Check if email exists
        userRepository.isEmailExists(email, exists -> {
            if (exists) {
                errorEmail.postValue("Email này đã được sử dụng. Vui lòng nhập địa chỉ email khác.");
            } else {
                // Perform registration
                String createdAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                Staff staff = new Staff(fullName, email, phone, "Admin", createdAt, "Hoạt động");
                
                // Using email as username for the first Admin
                Account account = new Account(email, PasswordUtils.hashPassword(password), "Admin", 0, "Hoạt động", createdAt);
                
                userRepository.registerAdmin(staff, account, success -> {
                    registerSuccess.postValue(success);
                    if (!success) {
                        toastMessage.postValue("Đã có lỗi xảy ra. Vui lòng thử lại.");
                    }
                });
            }
        });
    }
}
