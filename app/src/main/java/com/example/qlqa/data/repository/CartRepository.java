package com.example.qlqa.data.repository;

import com.example.qlqa.data.local.entities.MenuItem;
import java.util.HashMap;
import java.util.Map;

public class CartRepository {
    private static CartRepository instance;
    private final Map<Integer, Integer> cartMap = new HashMap<>();
    private final Map<Integer, MenuItem> itemCache = new HashMap<>();

    private CartRepository() {}

    public static synchronized CartRepository getInstance() {
        if (instance == null) {
            instance = new CartRepository();
        }
        return instance;
    }

    public void updateCart(MenuItem item, int quantity) {
        if (quantity <= 0) {
            cartMap.remove(item.getMenuItemId());
            itemCache.remove(item.getMenuItemId());
        } else {
            cartMap.put(item.getMenuItemId(), quantity);
            itemCache.put(item.getMenuItemId(), item);
        }
    }

    public Map<Integer, Integer> getCartMap() {
        return new HashMap<>(cartMap);
    }

    public Map<Integer, MenuItem> getItemCache() {
        return new HashMap<>(itemCache);
    }

    public void clearCart() {
        cartMap.clear();
        itemCache.clear();
    }
}
