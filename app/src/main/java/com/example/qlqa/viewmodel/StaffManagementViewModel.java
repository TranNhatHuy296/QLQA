package com.example.qlqa.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.qlqa.data.local.entities.Staff;
import com.example.qlqa.data.repository.StaffRepository;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StaffManagementViewModel extends AndroidViewModel {
    private final StaffRepository repository;
    
    private final MutableLiveData<List<Staff>> staffList = new MutableLiveData<>();
    private final MutableLiveData<int[]> stats = new MutableLiveData<>();
    private final MutableLiveData<Boolean> operationSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> generatedCode = new MutableLiveData<>();

    private String currentSearch = null;
    private String currentPosition = "Tất cả";

    public StaffManagementViewModel(@NonNull Application application) {
        super(application);
        repository = new StaffRepository(application);
    }

    public LiveData<List<Staff>> getStaffList() { return staffList; }
    public LiveData<int[]> getStats() { return stats; }
    public LiveData<Boolean> getOperationSuccess() { return operationSuccess; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<String> getGeneratedCode() { return generatedCode; }

    public void loadStaff() {
        repository.getFilteredStaff(currentSearch, currentPosition, staffList::postValue);
        repository.getStaffStats(stats::postValue);
    }

    public void setSearch(String query) {
        this.currentSearch = query;
        loadStaff();
    }

    public void setPositionFilter(String position) {
        this.currentPosition = position;
        loadStaff();
    }

    public void prepareNewStaff() {
        repository.generateEmployeeCode(generatedCode::postValue);
    }

    public void saveStaff(Staff staff, boolean isEdit) {
        if (staff.getFullName().isEmpty() || staff.getEmail().isEmpty() || staff.getPhoneNumber().isEmpty()) {
            errorMessage.postValue("Vui lòng điền đầy đủ thông tin bắt buộc.");
            return;
        }

        repository.isEmailExists(staff.getEmail(), isEdit ? staff.getStaffId() : null, exists -> {
            if (exists) {
                errorMessage.postValue("Email đã tồn tại trong hệ thống.");
            } else {
                if (isEdit) {
                    repository.updateStaff(staff, success -> {
                        operationSuccess.postValue(success);
                        if (success) loadStaff();
                    });
                } else {
                    repository.insertStaff(staff, success -> {
                        operationSuccess.postValue(success);
                        if (success) loadStaff();
                    });
                }
            }
        });
    }

    public String getCurrentDateTime() {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
    }
}
