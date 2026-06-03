package com.example.qlqa.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.qlqa.data.local.AppDatabase;
import com.example.qlqa.data.local.entities.Category;
import com.example.qlqa.data.local.entities.MenuItem;
import com.example.qlqa.data.repository.CartRepository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OrderMenuViewModel extends AndroidViewModel {
    private final AppDatabase db;
    private final ExecutorService executorService;
    private final CartRepository cartRepository;

    private final MutableLiveData<List<Category>> categories = new MutableLiveData<>();
    private final MutableLiveData<List<MenuItem>> menuItems = new MutableLiveData<>();
    private final MutableLiveData<Double> totalCartPrice = new MutableLiveData<>(0.0);
    private final MutableLiveData<Integer> totalCartCount = new MutableLiveData<>(0);
    private final MutableLiveData<Map<Integer, Integer>> cartMap = new MutableLiveData<>();

    public OrderMenuViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInstance(application);
        executorService = Executors.newSingleThreadExecutor();
        cartRepository = CartRepository.getInstance();
        cartMap.setValue(cartRepository.getCartMap());
    }

    public LiveData<List<Category>> getCategories() { return categories; }
    public LiveData<List<MenuItem>> getMenuItems() { return menuItems; }
    public LiveData<Double> getTotalCartPrice() { return totalCartPrice; }
    public LiveData<Integer> getTotalCartCount() { return totalCartCount; }
    public LiveData<Map<Integer, Integer>> getCartMapLiveData() { return cartMap; }

    public void loadCategories() {
        executorService.execute(() -> {
            List<Category> list = db.categoryDao().getAllCategories();
            categories.postValue(list);
        });
    }

    public void loadMenuItems(int categoryId) {
        executorService.execute(() -> {
            List<MenuItem> list;
            if (categoryId == 0) {
                list = db.menuItemDao().getAllMenuItems();
            } else {
                list = db.menuItemDao().getMenuItemsByCategory(categoryId);
            }
            menuItems.postValue(list);
        });
    }

    public void searchItems(String query) {
        executorService.execute(() -> {
            List<MenuItem> list = db.menuItemDao().searchMenuItems(query);
            menuItems.postValue(list);
        });
    }

    public void updateCart(MenuItem item, int quantity) {
        cartRepository.updateCart(item, quantity);
        calculateTotals();
    }

    private void calculateTotals() {
        Map<Integer, Integer> currentCartMap = cartRepository.getCartMap();
        Map<Integer, MenuItem> itemCache = cartRepository.getItemCache();
        
        double total = 0;
        int count = 0;
        for (Map.Entry<Integer, Integer> entry : currentCartMap.entrySet()) {
            MenuItem item = itemCache.get(entry.getKey());
            if (item != null) {
                total += item.getPrice() * entry.getValue();
                count += entry.getValue();
            }
        }
        totalCartPrice.postValue(total);
        totalCartCount.postValue(count);
        cartMap.postValue(currentCartMap);
    }

    public Map<Integer, Integer> getCartMap() {
        return cartRepository.getCartMap();
    }

    public void refreshCart() {
        calculateTotals();
    }
}
