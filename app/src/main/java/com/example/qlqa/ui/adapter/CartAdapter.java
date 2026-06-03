package com.example.qlqa.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.qlqa.data.local.model.OrderWithDetails;
import com.example.qlqa.databinding.ItemCartRowBinding;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private List<OrderWithDetails.OrderDetailWithItem> items = new ArrayList<>();
    private final OnCartItemChangeListener listener;
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    public interface OnCartItemChangeListener {
        void onQuantityChange(int menuItemId, int newQuantity);
        void onDelete(int menuItemId);
    }

    public CartAdapter(OnCartItemChangeListener listener) {
        this.listener = listener;
    }

    public void setItems(List<OrderWithDetails.OrderDetailWithItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCartRowBinding binding = ItemCartRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
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
        private final ItemCartRowBinding binding;

        ViewHolder(ItemCartRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(OrderWithDetails.OrderDetailWithItem detail) {
            binding.tvItemName.setText(detail.menuItem.getItemName());
            binding.tvUnitPrice.setText(currencyFormatter.format(detail.menuItem.getPrice()) + " / ly");
            binding.tvSubtotal.setText(currencyFormatter.format(detail.orderDetail.getSubtotal()));
            binding.tvQuantity.setText(String.valueOf(detail.orderDetail.getQuantity()));

            binding.btnAdd.setOnClickListener(v -> {
                listener.onQuantityChange(detail.menuItem.getMenuItemId(), detail.orderDetail.getQuantity() + 1);
            });

            binding.btnMinus.setOnClickListener(v -> {
                if (detail.orderDetail.getQuantity() > 1) {
                    listener.onQuantityChange(detail.menuItem.getMenuItemId(), detail.orderDetail.getQuantity() - 1);
                }
            });

            binding.btnDelete.setOnClickListener(v -> {
                listener.onDelete(detail.menuItem.getMenuItemId());
            });
        }
    }
}
