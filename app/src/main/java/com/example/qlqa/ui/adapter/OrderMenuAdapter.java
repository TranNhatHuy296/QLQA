package com.example.qlqa.ui.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.qlqa.data.local.entities.MenuItem;
import com.example.qlqa.databinding.ItemMenuOrderBinding;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrderMenuAdapter extends RecyclerView.Adapter<OrderMenuAdapter.ViewHolder> {

    private List<MenuItem> items = new ArrayList<>();
    private Map<Integer, Integer> cartMap = new HashMap<>();
    private final OnQuantityChangeListener listener;
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    public interface OnQuantityChangeListener {
        void onQuantityChange(MenuItem item, int newQuantity);
    }

    public OrderMenuAdapter(OnQuantityChangeListener listener) {
        this.listener = listener;
    }

    public void setData(List<MenuItem> items, Map<Integer, Integer> cartMap) {
        if (items != null) {
            this.items = items;
        }
        this.cartMap = cartMap != null ? cartMap : new HashMap<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMenuOrderBinding binding = ItemMenuOrderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemMenuOrderBinding binding;

        ViewHolder(ItemMenuOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(MenuItem item) {
            binding.tvItemName.setText(item.getItemName());
            binding.tvItemPrice.setText(currencyFormatter.format(item.getPrice()));
            
            binding.tvBestSeller.setVisibility(item.isBestSeller() ? View.VISIBLE : View.GONE);
            
            boolean isOutOfStock = "Hết hàng".equalsIgnoreCase(item.getStatus());
            binding.tvOutOfStock.setVisibility(isOutOfStock ? View.VISIBLE : View.GONE);
            binding.layoutQuantity.setAlpha(isOutOfStock ? 0.5f : 1.0f);
            
            int quantity = cartMap.getOrDefault(item.getMenuItemId(), 0);
            binding.tvQuantity.setText(String.valueOf(quantity));

            if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
                binding.ivMenuItem.setImageURI(Uri.parse(item.getImageUrl()));
            } else {
                binding.ivMenuItem.setImageResource(android.R.drawable.ic_menu_report_image);
            }

            binding.btnAdd.setOnClickListener(v -> {
                if (!isOutOfStock) {
                    listener.onQuantityChange(item, quantity + 1);
                } else {
                    Toast.makeText(itemView.getContext(), "Món này đã hết hàng!", Toast.LENGTH_SHORT).show();
                }
            });

            binding.btnMinus.setOnClickListener(v -> {
                if (!isOutOfStock && quantity > 0) {
                    listener.onQuantityChange(item, quantity - 1);
                }
            });
        }
    }
}
