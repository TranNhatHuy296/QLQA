package com.example.qlqa.viewmodel;

import android.app.Application;
import android.util.Patterns;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.qlqa.data.local.entities.Staff;
import com.example.qlqa.data.repository.UserRepository;

public class ProfileViewModel extends AndroidViewModel {
    private final UserRepository userRepository;
    private final MutableLiveData<Staff> staffData = new MutableLiveData<>();
    private final MutableLiveData<String> errorName = new MutableLiveData<>();
    private final MutableLiveData<String> errorEmail = new MutableLiveData<>();
    private final MutableLiveData<String> errorPhone = new MutableLiveData<>();
    private final MutableLiveData<String> errorAddress = new MutableLiveData<>();
    private final MutableLiveData<String> errorBirthday = new MutableLiveData<>();
    private final MutableLiveData<Boolean> updateSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> toastMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> showEmptyFieldsAlert = new MutableLiveData<>();
    
    private String currentUsername;

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
    }

    public LiveData<Staff> getStaffData() { return staffData; }
    public LiveData<String> getErrorName() { return errorName; }
    public LiveData<String> getErrorEmail() { return errorEmail; }
    public LiveData<String> getErrorPhone() { return errorPhone; }
    public LiveData<String> getErrorAddress() { return errorAddress; }
    public LiveData<String> getErrorBirthday() { return errorBirthday; }
    public LiveData<Boolean> getUpdateSuccess() { return updateSuccess; }
    public LiveData<String> getToastMessage() { return toastMessage; }
    public LiveData<Boolean> getShowEmptyFieldsAlert() { return showEmptyFieldsAlert; }

    public void loadProfile(String username) {
        this.currentUsername = username;
        userRepository.getStaffByUsername(username, staff -> {
            if (staff != null) {
                staffData.postValue(staff);
            }
        });
    }

    public void saveProfile(String name, String email, String phone, String address, String birthday) {
        // Validation according to BA: Họ và tên, Email, Số điện thoại are mandatory.
        if (name.trim().isEmpty() || email.trim().isEmpty() || phone.trim().isEmpty()) {
            showEmptyFieldsAlert.setValue(true);
            return;
        }

        boolean isValid = true;

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorEmail.setValue("Email không hợp lệ.");
            isValid = false;
        } else {
            errorEmail.setValue(null);
        }

        if (phone.trim().length() != 10 || !phone.trim().matches("\\d+")) {
            errorPhone.setValue("SĐT không hợp lệ.");
            isValid = false;
        } else {
            errorPhone.setValue(null);
        }

        if (!isValid) return;

        Staff currentStaff = staffData.getValue();
        if (currentStaff != null) {
            currentStaff.setFullName(name.trim());
            currentStaff.setEmail(email.trim());
            currentStaff.setPhoneNumber(phone.trim());
            currentStaff.setAddress(address.trim());
            currentStaff.setDateOfBirth(birthday.trim());

            userRepository.updateStaffProfile(currentStaff, currentUsername, success -> {
                if (success) {
                    currentUsername = email.trim(); // Update current username if email changed
                    updateSuccess.postValue(true);
                    staffData.postValue(currentStaff);
                } else {
                    toastMessage.postValue("Cập nhật thất bại. Vui lòng thử lại.");
                }
            });
        }
    }

    public void resetAlert() {
        showEmptyFieldsAlert.setValue(false);
    }
}
