package com.example.qlqa.data.repository;

import android.app.Application;
import com.example.qlqa.data.local.AppDatabase;
import com.example.qlqa.data.local.dao.MenuItemDao;
import com.example.qlqa.data.local.entities.MenuItem;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MenuItemRepository {
    private final MenuItemDao menuItemDao;
    private final ExecutorService executorService;

    public MenuItemRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        menuItemDao = db.menuItemDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void getAllMenuItems(Callback<List<MenuItem>> callback) {
        executorService.execute(() -> {
            List<MenuItem> items = menuItemDao.getAllMenuItems();
            callback.onResult(items);
        });
    }

    public void getMenuItemsByCategory(int categoryId, Callback<List<MenuItem>> callback) {
        executorService.execute(() -> {
            List<MenuItem> items = menuItemDao.getMenuItemsByCategory(categoryId);
            callback.onResult(items);
        });
    }

    public void searchMenuItems(String query, Callback<List<MenuItem>> callback) {
        executorService.execute(() -> {
            List<MenuItem> items = menuItemDao.searchMenuItems(query);
            callback.onResult(items);
        });
    }

    public void insert(MenuItem menuItem, Runnable onComplete) {
        executorService.execute(() -> {
            menuItemDao.insert(menuItem);
            onComplete.run();
        });
    }

    public void update(MenuItem menuItem, Runnable onComplete) {
        executorService.execute(() -> {
            menuItemDao.update(menuItem);
            onComplete.run();
        });
    }

    public void delete(MenuItem menuItem, Runnable onComplete) {
        executorService.execute(() -> {
            menuItemDao.delete(menuItem);
            onComplete.run();
        });
    }

    public interface Callback<T> {
        void onResult(T result);
    }
}
