package com.example.qlqa.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.qlqa.data.local.model.OrderWithDetails;
import com.example.qlqa.databinding.ItemPaymentTableBinding;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PaymentTableAdapter extends RecyclerView.Adapter<PaymentTableAdapter.ViewHolder> {

    private List<OrderWithDetails> items = new ArrayList<>();
    private final OnPaymentClickListener listener;
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    public interface OnPaymentClickListener {
        void onPaymentClick(OrderWithDetails orderWithDetails);
    }

    public PaymentTableAdapter(OnPaymentClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<OrderWithDetails> items) {
        this.items = items != null ? items : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPaymentTableBinding binding = ItemPaymentTableBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
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
        private final ItemPaymentTableBinding binding;

        ViewHolder(ItemPaymentTableBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(OrderWithDetails item) {
            if (item.table != null) {
                binding.tvTableName.setText(item.table.getTableName());
                binding.tvCustomerCount.setText(String.valueOf(item.table.getCurrentCustomers()));
            }
            
            binding.tvTotalAmount.setText(currencyFormatter.format(item.order.getTotalAmount()));
            
            binding.btnPayment.setOnClickListener(v -> listener.onPaymentClick(item));
        }
    }
}
