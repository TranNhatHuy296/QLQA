package com.example.qlqa.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.qlqa.data.local.entities.Category;
import com.example.qlqa.data.local.entities.MenuItem;
import com.example.qlqa.databinding.ActivityOrderMenuBinding;
import com.example.qlqa.ui.adapter.OrderMenuAdapter;
import com.example.qlqa.viewmodel.OrderMenuViewModel;
import com.google.android.material.tabs.TabLayout;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrderMenuActivity extends AppCompatActivity {

    private ActivityOrderMenuBinding binding;
    private OrderMenuViewModel viewModel;
    private OrderMenuAdapter menuAdapter;
    private int tableId;
    private String tableName;
    private String area;
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tableId = getIntent().getIntExtra("TABLE_ID", -1);
        tableName = getIntent().getStringExtra("TABLE_NAME");
        area = getIntent().getStringExtra("AREA");

        if (tableId == -1) {
            Toast.makeText(this, "Không tìm thấy thông tin bàn!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(OrderMenuViewModel.class);
        
        setupUI();
        setupRecyclerView();
        observeViewModel();

        viewModel.loadCategories();
        viewModel.loadMenuItems(0); // Load all initially
    }

    private void setupUI() {
        binding.tvTableInfo.setText(tableName + " - " + area);
        binding.btnBack.setOnClickListener(v -> finish());
        
        binding.btnNotification.setOnClickListener(v -> {
            startActivity(new Intent(this, NotificationActivity.class));
        });

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.searchItems(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.tabLayoutCategories.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getTag() instanceof Integer) {
                    viewModel.loadMenuItems((Integer) tab.getTag());
                } else {
                    viewModel.loadMenuItems(0);
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        binding.btnViewCart.setOnClickListener(v -> {
            Integer cartCount = viewModel.getTotalCartCount().getValue();
            if (cartCount == null || cartCount == 0) {
                Toast.makeText(this, "Giỏ hàng trống!", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, CartActivity.class);
            intent.putExtra("TABLE_ID", tableId);
            intent.putExtra("TABLE_NAME", tableName);
            intent.putExtra("AREA", area);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        menuAdapter = new OrderMenuAdapter(new OrderMenuAdapter.OnQuantityChangeListener() {
            @Override
            public void onQuantityChange(MenuItem item, int newQuantity) {
                viewModel.updateCart(item, newQuantity);
            }
        });
        binding.rvMenuItems.setLayoutManager(new GridLayoutManager(this, 2));
        binding.rvMenuItems.setAdapter(menuAdapter);
    }

    private void observeViewModel() {
        viewModel.getCategories().observe(this, this::setupCategoryTabs);
        
        viewModel.getMenuItems().observe(this, items -> {
            menuAdapter.setData(items, viewModel.getCartMap());
        });

        viewModel.getCartMapLiveData().observe(this, cartMap -> {
            menuAdapter.setData(viewModel.getMenuItems().getValue(), cartMap);
        });

        viewModel.getTotalCartCount().observe(this, count -> {
            binding.tvCartCount.setText(String.valueOf(count));
            binding.bottomCartBar.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
        });

        viewModel.getTotalCartPrice().observe(this, price -> {
            binding.tvTotalPrice.setText(currencyFormatter.format(price));
        });
    }

    private void setupCategoryTabs(List<Category> categories) {
        binding.tabLayoutCategories.removeAllTabs();
        
        TabLayout.Tab allTab = binding.tabLayoutCategories.newTab().setText("TẤT CẢ");
        allTab.setTag(0);
        binding.tabLayoutCategories.addTab(allTab);

        for (Category category : categories) {
            TabLayout.Tab tab = binding.tabLayoutCategories.newTab().setText(category.getCategoryName().toUpperCase());
            tab.setTag(category.getCategoryId());
            binding.tabLayoutCategories.addTab(tab);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh cart data if returning from CartActivity
        viewModel.refreshCart();
    }
}
