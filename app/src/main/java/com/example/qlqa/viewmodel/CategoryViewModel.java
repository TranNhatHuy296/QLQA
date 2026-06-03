package com.example.qlqa.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.qlqa.data.local.entities.Category;
import com.example.qlqa.data.local.entities.MenuItem;
import com.example.qlqa.data.repository.CategoryRepository;
import com.example.qlqa.data.repository.MenuItemRepository;

import java.util.List;

public class CategoryViewModel extends AndroidViewModel {
    private final CategoryRepository categoryRepository;
    private final MenuItemRepository menuItemRepository;
    
    private final MutableLiveData<List<Category>> categories = new MutableLiveData<>();
    private final MutableLiveData<List<MenuItem>> menuItems = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> operationSuccess = new MutableLiveData<>();

    public CategoryViewModel(@NonNull Application application) {
        super(application);
        categoryRepository = new CategoryRepository(application);
        menuItemRepository = new MenuItemRepository(application);
        loadCategories();
        loadMenuItems();
    }

    public LiveData<List<Category>> getCategories() {
        return categories;
    }

    public LiveData<List<MenuItem>> getMenuItems() {
        return menuItems;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getOperationSuccess() {
        return operationSuccess;
    }

    public void loadCategories() {
        categoryRepository.getAllCategories(categories::postValue);
    }

    public void loadMenuItems() {
        menuItemRepository.getAllMenuItems(menuItems::postValue);
    }

    public void filterMenuItemsByCategory(int categoryId) {
        if (categoryId == -1) {
            loadMenuItems();
        } else {
            menuItemRepository.getMenuItemsByCategory(categoryId, menuItems::postValue);
        }
    }

    public void searchMenuItems(String query) {
        if (query == null || query.isEmpty()) {
            loadMenuItems();
        } else {
            menuItemRepository.searchMenuItems(query, menuItems::postValue);
        }
    }

    public void insertCategory(Category category) {
        categoryRepository.insert(category, () -> {
            operationSuccess.postValue(true);
            loadCategories();
        });
    }

    public void updateCategory(Category category) {
        categoryRepository.update(category, () -> {
            operationSuccess.postValue(true);
            loadCategories();
        });
    }

    public void deleteCategory(Category category) {
        categoryRepository.delete(category, () -> {
            operationSuccess.postValue(true);
            loadCategories();
        }, errorMessage::postValue);
    }

    public void insertMenuItem(MenuItem menuItem) {
        menuItemRepository.insert(menuItem, () -> {
            operationSuccess.postValue(true);
            loadMenuItems();
        });
    }

    public void updateMenuItem(MenuItem menuItem) {
        menuItemRepository.update(menuItem, () -> {
            operationSuccess.postValue(true);
            loadMenuItems();
        });
    }

    public void deleteMenuItem(MenuItem menuItem) {
        menuItemRepository.delete(menuItem, () -> {
            operationSuccess.postValue(true);
            loadMenuItems();
        });
    }

    public void getProductCount(int categoryId, CategoryRepository.Callback<Integer> callback) {
        categoryRepository.getMenuItemCount(categoryId, callback);
    }
}
