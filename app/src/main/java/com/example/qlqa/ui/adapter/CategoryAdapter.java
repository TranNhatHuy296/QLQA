package com.example.qlqa.ui.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.qlqa.data.local.entities.Category;
import com.example.qlqa.databinding.ItemCategoryBinding;
import com.example.qlqa.viewmodel.CategoryViewModel;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> categories = new ArrayList<>();
    private final OnCategoryClickListener listener;
    private final CategoryViewModel viewModel;

    public interface OnCategoryClickListener {
        void onEdit(Category category);
        void onDelete(Category category);
    }

    public CategoryAdapter(CategoryViewModel viewModel, OnCategoryClickListener listener) {
        this.viewModel = viewModel;
        this.listener = listener;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategoryBinding binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CategoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.bind(category);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final ItemCategoryBinding binding;

        CategoryViewHolder(ItemCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Category category) {
            binding.tvCategoryName.setText(category.getCategoryName());
            
            if (category.getImageUrl() != null && !category.getImageUrl().isEmpty()) {
                binding.ivCategoryIcon.setImageURI(Uri.parse(category.getImageUrl()));
                binding.ivCategoryIcon.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);
                binding.ivCategoryIcon.setColorFilter(null); // Remove tint if showing image
            } else {
                binding.ivCategoryIcon.setImageResource(android.R.drawable.ic_menu_agenda);
                binding.ivCategoryIcon.setScaleType(android.widget.ImageView.ScaleType.CENTER_INSIDE);
                binding.ivCategoryIcon.setColorFilter(0xFF612D00);
            }

            // Get product count asynchronously
            viewModel.getProductCount(category.getCategoryId(), count -> {
                binding.tvProductCount.post(() -> binding.tvProductCount.setText(count + " món ăn"));
            });

            binding.btnEdit.setOnClickListener(v -> listener.onEdit(category));
            binding.btnDelete.setOnClickListener(v -> listener.onDelete(category));
        }
    }
}
