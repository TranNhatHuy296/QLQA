package com.example.qlqa.data.repository;

import android.content.Context;
import com.example.qlqa.data.local.AppDatabase;
import com.example.qlqa.data.local.dao.AccountDao;
import com.example.qlqa.data.local.entities.Account;
import com.example.qlqa.data.local.entities.Staff;
import com.example.qlqa.utils.PasswordUtils;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserRepository {
    private final AppDatabase database;
    private final ExecutorService executorService;

    public UserRepository(Context context) {
        database = AppDatabase.getInstance(context);
        executorService = Executors.newSingleThreadExecutor();
    }

    public interface Callback<T> {
        void onResult(T result);
    }

    public void isEmailExists(String email, Callback<Boolean> callback) {
        executorService.execute(() -> {
            Staff staff = database.staffDao().getStaffByEmail(email);
            callback.onResult(staff != null);
        });
    }

    public void isUsernameExists(String username, Callback<Boolean> callback) {
        executorService.execute(() -> {
            Account account = database.accountDao().getAccountByUsername(username);
            callback.onResult(account != null);
        });
    }

    public void registerAdmin(Staff staff, Account account, Callback<Boolean> callback) {
        executorService.execute(() -> {
            try {
                database.runInTransaction(() -> {
                    long staffId = database.staffDao().insert(staff);
                    account.setStaffId((int) staffId);
                    database.accountDao().insert(account);
                });
                callback.onResult(true);
            } catch (Exception e) {
                callback.onResult(false);
            }
        });
    }

    public void login(String username, String password, Callback<Account> callback) {
        executorService.execute(() -> {
            Account account = database.accountDao().getAccountByUsername(username);
            if (account != null && account.getPasswordHash().equals(PasswordUtils.hashPassword(password))) {
                callback.onResult(account);
            } else {
                callback.onResult(null);
            }
        });
    }

    public void changePassword(String username, String currentPassword, String newPassword, Callback<String> callback) {
        executorService.execute(() -> {
            Account account = database.accountDao().getAccountByUsername(username);
            if (account == null) {
                callback.onResult("Tài khoản không tồn tại.");
                return;
            }

            if (!account.getPasswordHash().equals(PasswordUtils.hashPassword(currentPassword))) {
                callback.onResult("Mật khẩu hiện tại không chính xác.");
                return;
            }

            account.setPasswordHash(PasswordUtils.hashPassword(newPassword));
            database.accountDao().update(account);
            callback.onResult("Success");
        });
    }

    public void getStaffByUsername(String username, Callback<Staff> callback) {
        executorService.execute(() -> {
            Account account = database.accountDao().getAccountByUsername(username);
            if (account != null) {
                Staff staff = database.staffDao().getStaffById(account.getStaffId());
                callback.onResult(staff);
            } else {
                callback.onResult(null);
            }
        });
    }

    public void updateStaffProfile(Staff staff, String oldUsername, Callback<Boolean> callback) {
        executorService.execute(() -> {
            try {
                database.runInTransaction(() -> {
                    database.staffDao().update(staff);
                    if (!staff.getEmail().equals(oldUsername)) {
                        Account account = database.accountDao().getAccountByUsername(oldUsername);
                        if (account != null) {
                            account.setUsername(staff.getEmail());
                            database.accountDao().update(account);
                        }
                    }
                });
                callback.onResult(true);
            } catch (Exception e) {
                callback.onResult(false);
            }
        });
    }

    public void updateStaff(Staff staff, Callback<Boolean> callback) {
        executorService.execute(() -> {
            try {
                database.staffDao().update(staff);
                callback.onResult(true);
            } catch (Exception e) {
                callback.onResult(false);
            }
        });
    }

    // --- New Account Management Methods ---

    public void getAccountStats(Callback<int[]> callback) {
        executorService.execute(() -> {
            int total = database.accountDao().getTotalCount();
            int active = database.accountDao().getActiveCount();
            int locked = database.accountDao().getLockedCount();
            callback.onResult(new int[]{total, active, locked});
        });
    }

    public void getFilteredAccounts(String search, List<String> roles, List<String> statuses, int limit, int offset, Callback<List<AccountDao.AccountWithStaffResult>> callback) {
        executorService.execute(() -> {
            boolean hasRoles = roles != null && !roles.isEmpty();
            boolean hasStatuses = statuses != null && !statuses.isEmpty();
            List<AccountDao.AccountWithStaffResult> results = database.accountDao().getFilteredAccounts(search, hasRoles, roles, hasStatuses, statuses, limit, offset);
            callback.onResult(results);
        });
    }

    public void getFilteredCount(String search, List<String> roles, List<String> statuses, Callback<Integer> callback) {
        executorService.execute(() -> {
            boolean hasRoles = roles != null && !roles.isEmpty();
            boolean hasStatuses = statuses != null && !statuses.isEmpty();
            int count = database.accountDao().getFilteredCount(search, hasRoles, roles, hasStatuses, statuses);
            callback.onResult(count);
        });
    }

    public void addAccountWithStaff(Staff staff, Account account, Callback<Boolean> callback) {
        executorService.execute(() -> {
            try {
                database.runInTransaction(() -> {
                    long staffId = database.staffDao().insert(staff);
                    account.setStaffId((int) staffId);
                    database.accountDao().insert(account);
                });
                callback.onResult(true);
            } catch (Exception e) {
                callback.onResult(false);
            }
        });
    }

    public void updateAccountAndStaff(Account account, Staff staff, Callback<Boolean> callback) {
        executorService.execute(() -> {
            try {
                database.runInTransaction(() -> {
                    database.accountDao().update(account);
                    database.staffDao().update(staff);
                });
                callback.onResult(true);
            } catch (Exception e) {
                callback.onResult(false);
            }
        });
    }
}
