package com.example.qlqa.data.repository;

import android.app.Application;
import com.example.qlqa.data.local.AppDatabase;
import com.example.qlqa.data.local.dao.TableDao;
import com.example.qlqa.data.local.entities.Table;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TableRepository {
    private final TableDao tableDao;
    private final ExecutorService executorService;

    public TableRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        tableDao = db.tableDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public interface Callback<T> {
        void onResult(T result);
    }

    public void getTablesByArea(String area, Callback<List<Table>> callback) {
        executorService.execute(() -> callback.onResult(tableDao.getTablesByArea(area)));
    }

    public void getTableStats(Callback<int[]> callback) {
        executorService.execute(() -> {
            int total = tableDao.getTotalTablesCount();
            int empty = tableDao.getEmptyTablesCount();
            int occupied = tableDao.getOccupiedTablesCount();
            int cleaning = tableDao.getCleaningTablesCount();
            callback.onResult(new int[]{total, empty, occupied, cleaning});
        });
    }

    public void insertTable(Table table, Callback<Boolean> callback) {
        executorService.execute(() -> {
            if (tableDao.isTableExistsInArea(table.getArea(), table.getTableName())) {
                callback.onResult(false);
            } else {
                long id = tableDao.insert(table);
                callback.onResult(id > 0);
            }
        });
    }

    public void updateTable(Table table, Callback<Boolean> callback) {
        executorService.execute(() -> {
            if (tableDao.isTableExistsInAreaExclude(table.getArea(), table.getTableName(), table.getTableId())) {
                callback.onResult(false);
            } else {
                tableDao.update(table);
                callback.onResult(true);
            }
        });
    }

    public void deleteTable(Table table, Callback<Boolean> callback) {
        executorService.execute(() -> {
            // Check if table has active orders (simplified for now, can be expanded)
            tableDao.delete(table);
            callback.onResult(true);
        });
    }
}
