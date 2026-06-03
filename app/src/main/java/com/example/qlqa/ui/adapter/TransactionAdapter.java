package com.example.qlqa.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.qlqa.data.local.model.OrderWithDetails;
import com.example.qlqa.databinding.ItemTransactionBinding;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<OrderWithDetails> orders = new ArrayList<>();
    private final OnTransactionClickListener listener;

    public interface OnTransactionClickListener {
        void onTransactionClick(int orderId);
    }

    public TransactionAdapter(OnTransactionClickListener listener) {
        this.listener = listener;
    }

    public void setOrders(List<OrderWithDetails> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTransactionBinding binding = ItemTransactionBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new TransactionViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        holder.bind(orders.get(position));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    class TransactionViewHolder extends RecyclerView.ViewHolder {
        private final ItemTransactionBinding binding;

        TransactionViewHolder(ItemTransactionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(OrderWithDetails item) {
            binding.tvOrderCode.setText(String.format("#ORD-%04d", item.order.getOrderId()));
            
            String time = "";
            if (item.order.getCreatedAt() != null && item.order.getCreatedAt().length() >= 16) {
                time = item.order.getCreatedAt().substring(11, 16);
            }
            
            String location = item.table != null ? item.table.getTableName() : "Kênh khác";
            binding.tvOrderInfo.setText(String.format("%s | %s", time, location));

            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            binding.tvAmount.setText(formatter.format(item.order.getTotalAmount()));

            itemView.setOnClickListener(v -> listener.onTransactionClick(item.order.getOrderId()));
        }
    }
}
