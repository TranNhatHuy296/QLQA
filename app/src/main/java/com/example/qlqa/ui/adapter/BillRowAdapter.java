package com.example.qlqa.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.qlqa.data.local.model.OrderWithDetails;
import com.example.qlqa.databinding.ItemBillRowBinding;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BillRowAdapter extends RecyclerView.Adapter<BillRowAdapter.ViewHolder> {

    private List<OrderWithDetails.OrderDetailWithItem> items = new ArrayList<>();
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    public void setItems(List<OrderWithDetails.OrderDetailWithItem> items) {
        this.items = items != null ? items : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBillRowBinding binding = ItemBillRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
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
        private final ItemBillRowBinding binding;

        ViewHolder(ItemBillRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(OrderWithDetails.OrderDetailWithItem item) {
            binding.tvQuantity.setText(String.valueOf(item.orderDetail.getQuantity()));
            if (item.menuItem != null) {
                binding.tvItemName.setText(item.menuItem.getItemName());
            }
            binding.tvSubtotal.setText(currencyFormatter.format(item.orderDetail.getSubtotal()));
        }
    }
}
