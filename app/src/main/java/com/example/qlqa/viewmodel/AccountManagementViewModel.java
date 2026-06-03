package com.example.qlqa.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.qlqa.data.local.dao.AccountDao;
import com.example.qlqa.data.local.entities.Account;
import com.example.qlqa.data.local.entities.Staff;
import com.example.qlqa.data.repository.UserRepository;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AccountManagementViewModel extends AndroidViewModel {
    private final UserRepository userRepository;
    
    private final MutableLiveData<int[]> stats = new MutableLiveData<>();
    private final MutableLiveData<List<AccountDao.AccountWithStaffResult>> accounts = new MutableLiveData<>();
    private final MutableLiveData<Integer> totalFilteredCount = new MutableLiveData<>();
    private final MutableLiveData<Boolean> operationSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private String currentSearch = null;
    private List<String> currentRoles = null;
    private List<String> currentStatuses = null;
    private int currentPage = 0;
    private static final int PAGE_SIZE = 3;

    public AccountManagementViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
    }

    public LiveData<int[]> getStats() { return stats; }
    public LiveData<List<AccountDao.AccountWithStaffResult>> getAccounts() { return accounts; }
    public LiveData<Integer> getTotalFilteredCount() { return totalFilteredCount; }
    public LiveData<Boolean> getOperationSuccess() { return operationSuccess; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    public void loadStats() {
        userRepository.getAccountStats(stats::postValue);
    }

    public void loadAccounts() {
        userRepository.getFilteredCount(currentSearch, currentRoles, currentStatuses, totalFilteredCount::postValue);
        userRepository.getFilteredAccounts(currentSearch, currentRoles, currentStatuses, PAGE_SIZE, currentPage * PAGE_SIZE, accounts::postValue);
    }

    public void setSearch(String query) {
        this.currentSearch = (query == null || query.isEmpty()) ? null : query;
        this.currentPage = 0;
        loadAccounts();
    }

    public void setFilters(List<String> roles, List<String> statuses) {
        this.currentRoles = (roles == null || roles.isEmpty()) ? null : roles;
        this.currentStatuses = (statuses == null || statuses.isEmpty()) ? null : statuses;
        this.currentPage = 0;
        loadAccounts();
    }

    public void setPage(int page) {
        this.currentPage = page;
        loadAccounts();
    }

    public int getCurrentPage() { return currentPage; }
    public int getPageSize() { return PAGE_SIZE; }

    public void saveAccount(String username, String password, String role, String status, Integer staffId) {
        if (username.isEmpty() || password.isEmpty() || role == null || status == null) {
            errorMessage.postValue("Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        if (staffId == null) {
            // New Account
            userRepository.isUsernameExists(username, exists -> {
                if (exists) {
                    errorMessage.postValue("Tài khoản đã tồn tại.");
                } else {
                    // Fixed: Provided all 11 required arguments to the Staff constructor
                    Staff staff = new Staff(
                            username,          // employeeCode (using username as initial code)
                            username,          // fullName
                            username,          // email
                            "",                // phoneNumber
                            role,              // position
                            "",                // address
                            "",                // shifts
                            null,              // profileImageUrl
                            "",                // dateOfBirth (added missing argument)
                            getCurrentDate(),  // createdAt
                            "Hoạt động"        // status
                    );
                    Account account = new Account(username, com.example.qlqa.utils.PasswordUtils.hashPassword(password), role, 0, status, getCurrentDate());
                    userRepository.addAccountWithStaff(staff, account, success -> {
                        operationSuccess.postValue(success);
                        if (success) {
                            loadStats();
                            loadAccounts();
                        }
                    });
                }
            });
        } else {
            // Edit Account
            // Implementation for editing existing account/staff
        }
    }

    private String getCurrentDate() {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
    }
}
