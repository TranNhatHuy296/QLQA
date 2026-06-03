package com.example.qlqa.ui.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.qlqa.data.local.entities.MenuItem;
import com.example.qlqa.databinding.ItemProductBinding;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MenuItemAdapter extends RecyclerView.Adapter<MenuItemAdapter.ViewHolder> {

    private List<MenuItem> items = new ArrayList<>();
    private final OnMenuItemClickListener listener;
    private final OnStatusChangeListener statusChangeListener;
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    public interface OnMenuItemClickListener {
        void onItemClick(MenuItem item);
    }

    public interface OnStatusChangeListener {
        void onStatusChange(MenuItem item, boolean isOn);
    }

    public MenuItemAdapter(OnMenuItemClickListener listener, OnStatusChangeListener statusChangeListener) {
        this.listener = listener;
        this.statusChangeListener = statusChangeListener;
    }

    public void setItems(List<MenuItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProductBinding binding = ItemProductBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MenuItem item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemProductBinding binding;

        ViewHolder(ItemProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(MenuItem item) {
            binding.tvProductName.setText(item.getItemName());
            binding.tvDescription.setText(item.getDescription());
            binding.tvPrice.setText(currencyFormatter.format(item.getPrice()));
            
            binding.tvBestSellerBadge.setVisibility(item.isBestSeller() ? View.VISIBLE : View.GONE);
            
            boolean isAvailable = "Còn hàng".equalsIgnoreCase(item.getStatus());

            if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
                binding.ivProductImage.setImageURI(Uri.parse(item.getImageUrl()));
            } else {
                binding.ivProductImage.setImageResource(android.R.drawable.ic_menu_report_image);
            }

            binding.getRoot().setOnClickListener(v -> listener.onItemClick(item));

            binding.swStatus.setOnCheckedChangeListener(null);
            binding.swStatus.setChecked(isAvailable);
            binding.tvStatus.setText(item.getStatus());
            binding.swStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
                statusChangeListener.onStatusChange(item, isChecked);
                binding.tvStatus.setText(isChecked ? "Còn hàng" : "Hết hàng");
            });
        }
    }
}
