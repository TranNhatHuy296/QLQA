package com.example.qlqa.data.repository;

import android.app.Application;
import com.example.qlqa.data.local.AppDatabase;
import com.example.qlqa.data.local.dao.CategoryDao;
import com.example.qlqa.data.local.dao.MenuItemDao;
import com.example.qlqa.data.local.entities.Category;
import com.example.qlqa.data.local.entities.MenuItem;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CategoryRepository {
    private final CategoryDao categoryDao;
    private final MenuItemDao menuItemDao;
    private final ExecutorService executorService;

    public CategoryRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        categoryDao = db.categoryDao();
        menuItemDao = db.menuItemDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void getAllCategories(Callback<List<Category>> callback) {
        executorService.execute(() -> {
            List<Category> categories = categoryDao.getAllCategories();
            callback.onResult(categories);
        });
    }

    public void insert(Category category, Runnable onComplete) {
        executorService.execute(() -> {
            categoryDao.insert(category);
            onComplete.run();
        });
    }

    public void update(Category category, Runnable onComplete) {
        executorService.execute(() -> {
            categoryDao.update(category);
            onComplete.run();
        });
    }

    public void delete(Category category, Runnable onComplete, Callback<String> onError) {
        executorService.execute(() -> {
            List<MenuItem> items = menuItemDao.getMenuItemsByCategory(category.getCategoryId());
            if (items != null && !items.isEmpty()) {
                onError.onResult("Không thể xóa danh mục đang có món ăn. Vui lòng chuyển hoặc xóa các món ăn thuộc danh mục này trước.");
            } else {
                categoryDao.delete(category);
                onComplete.run();
            }
        });
    }

    public void getMenuItemCount(int categoryId, Callback<Integer> callback) {
        executorService.execute(() -> {
            List<MenuItem> items = menuItemDao.getMenuItemsByCategory(categoryId);
            callback.onResult(items != null ? items.size() : 0);
        });
    }

    public interface Callback<T> {
        void onResult(T result);
    }
}
