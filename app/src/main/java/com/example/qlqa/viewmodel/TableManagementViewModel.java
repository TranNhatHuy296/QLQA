package com.example.qlqa.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.qlqa.data.local.entities.Table;
import com.example.qlqa.data.repository.TableRepository;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TableManagementViewModel extends AndroidViewModel {
    private final TableRepository repository;
    
    private final MutableLiveData<List<Table>> tableList = new MutableLiveData<>();
    private final MutableLiveData<int[]> stats = new MutableLiveData<>();
    private final MutableLiveData<Boolean> operationSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private String currentArea = "Tầng 1";

    public TableManagementViewModel(@NonNull Application application) {
        super(application);
        repository = new TableRepository(application);
    }

    public LiveData<List<Table>> getTableList() { return tableList; }
    public LiveData<int[]> getStats() { return stats; }
    public LiveData<Boolean> getOperationSuccess() { return operationSuccess; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    public void loadTables() {
        repository.getTablesByArea(currentArea, tableList::postValue);
        repository.getTableStats(stats::postValue);
    }

    public void setArea(String area) {
        this.currentArea = area;
        loadTables();
    }

    public void saveTable(Table table, boolean isEdit) {
        if (table.getTableName().isEmpty() || table.getArea().isEmpty()) {
            errorMessage.postValue("Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        if (isEdit) {
            repository.updateTable(table, success -> {
                if (success) {
                    operationSuccess.postValue(true);
                    loadTables();
                } else {
                    errorMessage.postValue("Tên bàn đã tồn tại trong khu vực này.");
                }
            });
        } else {
            repository.insertTable(table, success -> {
                if (success) {
                    operationSuccess.postValue(true);
                    loadTables();
                } else {
                    errorMessage.postValue("Tên bàn đã tồn tại trong khu vực này.");
                }
            });
        }
    }

    public String getCurrentDateTime() {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
    }
}
