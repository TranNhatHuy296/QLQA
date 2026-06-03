package com.example.qlqa.ui;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.qlqa.data.local.entities.Category;
import com.example.qlqa.data.local.entities.MenuItem;
import com.example.qlqa.databinding.ActivityCategoryManagementBinding;
import com.example.qlqa.databinding.DialogCategoryEditorBinding;
import com.example.qlqa.databinding.DialogMenuItemEditorBinding;
import com.example.qlqa.ui.adapter.CategoryAdapter;
import com.example.qlqa.ui.adapter.MenuItemAdapter;
import com.example.qlqa.utils.SessionManager;
import com.example.qlqa.viewmodel.CategoryViewModel;
import com.google.android.material.chip.Chip;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class CategoryManagementActivity extends AppCompatActivity {

    private static final String TAG = "CategoryMgmt";
    private ActivityCategoryManagementBinding binding;
    private CategoryViewModel viewModel;
    private CategoryAdapter categoryAdapter;
    private MenuItemAdapter productAdapter;
    private SessionManager sessionManager;
    
    private List<Category> allCategories = new ArrayList<>();
    private List<MenuItem> allMenuItems = new ArrayList<>();
    private boolean isCategoryTab = false;

    private String currentSelectedImageUrl = null;
    private DialogCategoryEditorBinding currentCategoryDialogBinding;
    private DialogMenuItemEditorBinding currentMenuItemDialogBinding;

    private final ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    try {
                        currentSelectedImageUrl = uri.toString();
                        // Request persistable permission to keep access after reboot
                        getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    } catch (SecurityException e) {
                        Log.e(TAG, "Could not take persistable permission", e);
                    }

                    if (currentCategoryDialogBinding != null) {
                        currentCategoryDialogBinding.ivPreview.setImageURI(uri);
                        currentCategoryDialogBinding.ivPreview.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);
                        currentCategoryDialogBinding.ivPreview.setImageTintList(null);
                    } else if (currentMenuItemDialogBinding != null) {
                        currentMenuItemDialogBinding.ivProductPreview.setVisibility(View.VISIBLE);
                        currentMenuItemDialogBinding.ivProductPreview.setImageURI(uri);
                        currentMenuItemDialogBinding.layoutUploadPlaceholder.setVisibility(View.GONE);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoryManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        sessionManager = new SessionManager(this);

        setupRecyclerViews();
        setupObservers();
        setupListeners();
        
        // Load initial data
        viewModel.loadCategories();
        viewModel.loadMenuItems();
        
        switchTab(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.loadCategories();
        viewModel.loadMenuItems();
    }

    private void setupRecyclerViews() {
        categoryAdapter = new CategoryAdapter(viewModel, new CategoryAdapter.OnCategoryClickListener() {
            @Override public void onEdit(Category c) { showCategoryDialog(c); }
            @Override public void onDelete(Category c) { showDeleteConfirmation(c); }
        });
        binding.rvCategories.setLayoutManager(new LinearLayoutManager(this));
        binding.rvCategories.setAdapter(categoryAdapter);

        productAdapter = new MenuItemAdapter(
            item -> showMenuItemDialog(item),
            (item, isOn) -> {
                item.setStatus(isOn ? "Còn hàng" : "Hết hàng");
                viewModel.updateMenuItem(item);
            }
        );
        binding.rvProducts.setLayoutManager(new LinearLayoutManager(this));
        binding.rvProducts.setAdapter(productAdapter);
    }

    private void setupObservers() {
        viewModel.getCategories().observe(this, categories -> {
            allCategories = categories;
            if (isCategoryTab) {
                categoryAdapter.setCategories(categories);
            }
            updateCategoryChips();
        });

        viewModel.getMenuItems().observe(this, items -> {
            allMenuItems = items;
            if (!isCategoryTab) {
                productAdapter.setItems(items);
            }
            updateCategoryChips();
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        });
    }

    private void setupListeners() {
        binding.btnNotification.setOnClickListener(v -> startActivity(new Intent(this, NotificationActivity.class)));
        binding.btnTabCategory.setOnClickListener(v -> switchTab(true));
        binding.btnTabProduct.setOnClickListener(v -> switchTab(false));

        binding.fabAddCategory.setOnClickListener(v -> {
            if (isCategoryTab) showCategoryDialog(null);
            else showMenuItemDialog(null);
        });

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isCategoryTab) filterCategories(s.toString());
                else filterMenuItems(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        binding.navHome.setOnClickListener(v -> { startActivity(new Intent(this, DashboardActivity.class)); finish(); });
        binding.navManage.setOnClickListener(v -> { startActivity(new Intent(this, ManageActivity.class)); finish(); });
        binding.navReport.setOnClickListener(v -> {
            if ("Admin".equalsIgnoreCase(sessionManager.getRole())) {
                startActivity(new Intent(this, RevenueReportActivity.class));
                finish();
            } else Toast.makeText(this, "Chỉ Admin mới xem được báo cáo", Toast.LENGTH_SHORT).show();
        });
    }

    private void switchTab(boolean toCategory) {
        isCategoryTab = toCategory;
        if (toCategory) {
            binding.tvTitle.setText("Quản Lý Danh Mục");
            binding.rvCategories.setVisibility(View.VISIBLE);
            binding.rvProducts.setVisibility(View.GONE);
            binding.hsvFilters.setVisibility(View.GONE);
            binding.etSearch.setHint("Tìm kiếm danh mục...");
            updateTabUI(binding.btnTabCategory, binding.btnTabProduct);
            categoryAdapter.setCategories(allCategories);
        } else {
            binding.tvTitle.setText("Quản Lý Món Ăn");
            binding.rvCategories.setVisibility(View.GONE);
            binding.rvProducts.setVisibility(View.VISIBLE);
            binding.hsvFilters.setVisibility(View.VISIBLE);
            binding.etSearch.setHint("Tìm kiếm món ăn...");
            updateTabUI(binding.btnTabProduct, binding.btnTabCategory);
            productAdapter.setItems(allMenuItems);
            updateCategoryChips();
        }
    }

    private void updateTabUI(View active, View inactive) {
        active.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FAF9FC")));
        ((com.google.android.material.button.MaterialButton)active).setTextColor(Color.parseColor("#000613"));
        active.setElevation(4f);
        inactive.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
        ((com.google.android.material.button.MaterialButton)inactive).setTextColor(Color.parseColor("#43474E"));
        inactive.setElevation(0f);
    }

    private void updateCategoryChips() {
        if (isCategoryTab) return;
        binding.cgFilter.removeAllViews();
        
        Chip allChip = new Chip(this);
        allChip.setText("Tất cả (" + allMenuItems.size() + ")");
        allChip.setCheckable(true);
        allChip.setChecked(true);
        allChip.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#FFDCC7"))); 
        allChip.setOnClickListener(v -> {
            productAdapter.setItems(allMenuItems);
            resetChipColors(allChip);
        });
        binding.cgFilter.addView(allChip);

        for (Category category : allCategories) {
            Chip chip = new Chip(this);
            chip.setText(category.getCategoryName());
            chip.setCheckable(true);
            chip.setOnClickListener(v -> {
                List<MenuItem> filtered = allMenuItems.stream()
                    .filter(m -> m.getCategoryId() == category.getCategoryId())
                    .collect(Collectors.toList());
                productAdapter.setItems(filtered);
                resetChipColors(chip);
            });
            binding.cgFilter.addView(chip);
        }
    }

    private void resetChipColors(Chip selectedChip) {
        for (int i = 0; i < binding.cgFilter.getChildCount(); i++) {
            Chip chip = (Chip) binding.cgFilter.getChildAt(i);
            if (chip == selectedChip) {
                chip.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#FFDCC7")));
                chip.setTextColor(Color.parseColor("#311300"));
            } else {
                chip.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#FAF9FC")));
                chip.setTextColor(Color.parseColor("#43474E"));
            }
        }
    }

    private void showMenuItemDialog(MenuItem item) {
        currentMenuItemDialogBinding = DialogMenuItemEditorBinding.inflate(LayoutInflater.from(this));
        currentCategoryDialogBinding = null;
        AlertDialog dialog = new AlertDialog.Builder(this).setView(currentMenuItemDialogBinding.getRoot()).create();

        List<String> categoryNames = allCategories.stream().map(Category::getCategoryName).collect(Collectors.toList());
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currentMenuItemDialogBinding.spinnerCategory.setAdapter(spinnerAdapter);

        if (item != null) {
            currentMenuItemDialogBinding.tvDialogTitle.setText("Chỉnh sửa món ăn");
            currentMenuItemDialogBinding.etProductName.setText(item.getItemName());
            currentMenuItemDialogBinding.etPrice.setText(String.valueOf((int)item.getPrice()));
            currentMenuItemDialogBinding.etDescription.setText(item.getDescription());
            currentMenuItemDialogBinding.swItemStatus.setChecked("Còn hàng".equalsIgnoreCase(item.getStatus()));
            currentMenuItemDialogBinding.tvStatusLabel.setText(item.getStatus());
            currentSelectedImageUrl = item.getImageUrl();
            if (currentSelectedImageUrl != null && !currentSelectedImageUrl.isEmpty()) {
                currentMenuItemDialogBinding.ivProductPreview.setVisibility(View.VISIBLE);
                currentMenuItemDialogBinding.ivProductPreview.setImageURI(Uri.parse(currentSelectedImageUrl));
                currentMenuItemDialogBinding.layoutUploadPlaceholder.setVisibility(View.GONE);
            }
            for (int i = 0; i < allCategories.size(); i++) {
                if (allCategories.get(i).getCategoryId() == item.getCategoryId()) {
                    currentMenuItemDialogBinding.spinnerCategory.setSelection(i);
                    break;
                }
            }
        } else {
            currentSelectedImageUrl = null;
        }

        currentMenuItemDialogBinding.btnUploadImage.setOnClickListener(v -> pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build()));

        currentMenuItemDialogBinding.swItemStatus.setOnCheckedChangeListener((bv, isChecked) -> 
            currentMenuItemDialogBinding.tvStatusLabel.setText(isChecked ? "Còn hàng" : "Hết hàng"));

        currentMenuItemDialogBinding.btnSave.setOnClickListener(v -> {
            String name = currentMenuItemDialogBinding.etProductName.getText().toString().trim();
            String priceStr = currentMenuItemDialogBinding.etPrice.getText().toString().trim();
            int catPos = currentMenuItemDialogBinding.spinnerCategory.getSelectedItemPosition();

            if (name.isEmpty()) { Toast.makeText(this, "Tên món ăn không được bỏ trống", Toast.LENGTH_SHORT).show(); return; }
            if (catPos < 0) { Toast.makeText(this, "Danh mục phải được chọn", Toast.LENGTH_SHORT).show(); return; }
            if (priceStr.isEmpty() || Double.parseDouble(priceStr) <= 0) { Toast.makeText(this, "Giá bán phải lớn hơn 0", Toast.LENGTH_SHORT).show(); return; }

            double price = Double.parseDouble(priceStr);
            int categoryId = allCategories.get(catPos).getCategoryId();
            String status = currentMenuItemDialogBinding.swItemStatus.isChecked() ? "Còn hàng" : "Hết hàng";
            String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

            if (item == null) {
                viewModel.insertMenuItem(new MenuItem(categoryId, name, price, currentSelectedImageUrl, currentMenuItemDialogBinding.etDescription.getText().toString(), 0, false, status, now));
            } else {
                item.setItemName(name); item.setPrice(price); item.setCategoryId(categoryId);
                item.setDescription(currentMenuItemDialogBinding.etDescription.getText().toString()); item.setStatus(status);
                item.setImageUrl(currentSelectedImageUrl);
                viewModel.updateMenuItem(item);
            }
            dialog.dismiss();
        });

        currentMenuItemDialogBinding.btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void filterCategories(String query) {
        List<Category> filtered = allCategories.stream()
                .filter(c -> c.getCategoryName().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
        categoryAdapter.setCategories(filtered);
    }

    private void filterMenuItems(String query) {
        List<MenuItem> filtered = allMenuItems.stream()
                .filter(m -> m.getItemName().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
        productAdapter.setItems(filtered);
    }

    private void showCategoryDialog(Category category) {
        currentCategoryDialogBinding = DialogCategoryEditorBinding.inflate(LayoutInflater.from(this));
        currentMenuItemDialogBinding = null;
        AlertDialog dialog = new AlertDialog.Builder(this).setView(currentCategoryDialogBinding.getRoot()).create();
        
        if (category != null) { 
            currentCategoryDialogBinding.etCategoryName.setText(category.getCategoryName()); 
            currentCategoryDialogBinding.etDescription.setText(category.getDescription());
            currentSelectedImageUrl = category.getImageUrl();
            if (currentSelectedImageUrl != null && !currentSelectedImageUrl.isEmpty()) {
                currentCategoryDialogBinding.ivPreview.setImageURI(Uri.parse(currentSelectedImageUrl));
                currentCategoryDialogBinding.ivPreview.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);
                currentCategoryDialogBinding.ivPreview.setImageTintList(null);
            }
        } else {
            currentSelectedImageUrl = null;
        }

        currentCategoryDialogBinding.btnUploadIcon.setOnClickListener(v -> pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build()));

        currentCategoryDialogBinding.btnSave.setOnClickListener(v -> {
            String name = currentCategoryDialogBinding.etCategoryName.getText().toString().trim();
            if (name.isEmpty()) return;
            if (category == null) {
                Category newCat = new Category(name, currentCategoryDialogBinding.etDescription.getText().toString(), "Hoạt động");
                newCat.setImageUrl(currentSelectedImageUrl);
                viewModel.insertCategory(newCat);
            } else { 
                category.setCategoryName(name); 
                category.setDescription(currentCategoryDialogBinding.etDescription.getText().toString()); 
                category.setImageUrl(currentSelectedImageUrl);
                viewModel.updateCategory(category); 
            }
            dialog.dismiss();
        });
        currentCategoryDialogBinding.btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showDeleteConfirmation(Category c) {
        new AlertDialog.Builder(this).setTitle("Xác nhận xóa").setMessage("Xóa danh mục này?")
            .setPositiveButton("Xóa", (d, w) -> viewModel.deleteCategory(c)).setNegativeButton("Hủy", null).show();
    }
}
