package com.example.qlqa.data.repository;

import android.app.Application;
import com.example.qlqa.data.local.AppDatabase;
import com.example.qlqa.data.local.dao.StaffDao;
import com.example.qlqa.data.local.entities.Staff;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StaffRepository {
    private final StaffDao staffDao;
    private final ExecutorService executorService;

    public StaffRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        staffDao = db.staffDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public interface Callback<T> {
        void onResult(T result);
    }

    public void getAllStaff(Callback<List<Staff>> callback) {
        executorService.execute(() -> callback.onResult(staffDao.getAllStaff()));
    }

    public void getFilteredStaff(String search, String position, Callback<List<Staff>> callback) {
        executorService.execute(() -> {
            String searchParam = (search == null || search.isEmpty()) ? null : search;
            String positionParam = (position == null || position.equals("Tất cả")) ? null : position;
            callback.onResult(staffDao.getFilteredStaff(searchParam, positionParam));
        });
    }

    public void getStaffStats(Callback<int[]> callback) {
        executorService.execute(() -> {
            int total = staffDao.getTotalStaffCount();
            int present = staffDao.getPresentStaffCount();
            callback.onResult(new int[]{total, present});
        });
    }

    public void insertStaff(Staff staff, Callback<Boolean> callback) {
        executorService.execute(() -> {
            long id = staffDao.insert(staff);
            callback.onResult(id > 0);
        });
    }

    public void updateStaff(Staff staff, Callback<Boolean> callback) {
        executorService.execute(() -> {
            staffDao.update(staff);
            callback.onResult(true);
        });
    }

    public void deleteStaff(Staff staff, Callback<Boolean> callback) {
        executorService.execute(() -> {
            staffDao.delete(staff);
            callback.onResult(true);
        });
    }

    public void generateEmployeeCode(Callback<String> callback) {
        executorService.execute(() -> {
            Integer maxId = staffDao.getMaxStaffId();
            int nextId = (maxId == null ? 0 : maxId) + 1;
            String year = new java.text.SimpleDateFormat("yyyy", java.util.Locale.getDefault()).format(new java.util.Date());
            String code = String.format("NV-%s-%03d", year, nextId);
            callback.onResult(code);
        });
    }
    
    public void isEmailExists(String email, Integer excludeId, Callback<Boolean> callback) {
        executorService.execute(() -> {
            Staff staff = staffDao.getStaffByEmail(email);
            if (staff == null) {
                callback.onResult(false);
            } else {
                callback.onResult(excludeId == null || staff.getStaffId() != excludeId);
            }
        });
    }
}
