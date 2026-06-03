package com.example.qlqa.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.qlqa.data.local.entities.Staff;
import com.example.qlqa.data.local.model.OrderWithDetails;
import com.example.qlqa.data.repository.RevenueRepository;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class RevenueReportViewModel extends AndroidViewModel {
    private final RevenueRepository repository;
    
    private final MutableLiveData<Double> totalRevenue = new MutableLiveData<>(0.0);
    private final MutableLiveData<Integer> totalOrders = new MutableLiveData<>(0);
    private final MutableLiveData<List<OrderWithDetails>> recentTransactions = new MutableLiveData<>();
    private final MutableLiveData<List<Staff>> staffList = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private String startDate;
    private String endDate;
    private Integer selectedStaffId = -1;

    public RevenueReportViewModel(@NonNull Application application) {
        super(application);
        repository = new RevenueRepository(application);
        setDefaultDates();
    }

    private void setDefaultDates() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        startDate = sdf.format(cal.getTime());
        
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        endDate = sdf.format(cal.getTime());
    }

    public LiveData<Double> getTotalRevenue() { return totalRevenue; }
    public LiveData<Integer> getTotalOrders() { return totalOrders; }
    public LiveData<List<OrderWithDetails>> getRecentTransactions() { return recentTransactions; }
    public LiveData<List<Staff>> getStaffList() { return staffList; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    public void loadData() {
        repository.getRevenueData(startDate, endDate, selectedStaffId, result -> {
            totalRevenue.postValue(result.totalRevenue);
            totalOrders.postValue(result.totalOrders);
            recentTransactions.postValue(result.recentOrders);
        });
    }

    public void loadStaff() {
        repository.getAllStaff(staffList::postValue);
    }

    public void setFilter(String start, String end, Integer staffId) {
        this.startDate = start;
        this.endDate = end;
        this.selectedStaffId = staffId;
        loadData();
    }
}
