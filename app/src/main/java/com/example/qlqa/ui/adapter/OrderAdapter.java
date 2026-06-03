package com.example.qlqa.ui.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.qlqa.data.local.model.OrderWithDetails;
import com.example.qlqa.databinding.ItemOrderCardBinding;
import com.example.qlqa.databinding.ItemOrderDetailMiniBinding;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<OrderWithDetails> orders = new ArrayList<>();
    private final OnOrderActionListener listener;
    private final DecimalFormat formatter = new DecimalFormat("#,###đ");

    public interface OnOrderActionListener {
        void onAction(int orderId, String nextStatus);
    }

    public OrderAdapter(OnOrderActionListener listener) {
        this.listener = listener;
    }

    public void setOrders(List<OrderWithDetails> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOrderCardBinding binding = ItemOrderCardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new OrderViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        holder.bind(orders.get(position));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        private final ItemOrderCardBinding binding;

        public OrderViewHolder(ItemOrderCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(OrderWithDetails orderWithDetails) {
            binding.tvOrderCode.setText("Đơn hàng #ORD-" + orderWithDetails.order.getOrderId());
            binding.tvTableName.setText(orderWithDetails.table != null ? orderWithDetails.table.getTableName() : "Không xác định");
            binding.tvTotalAmount.setText(formatter.format(orderWithDetails.order.getTotalAmount()));

            String status = orderWithDetails.order.getStatus();
            binding.tvStatusBadge.setText(status.toUpperCase());

            // Reset UI
            binding.btnAction.setVisibility(View.GONE);
            binding.tvCompletedLabel.setVisibility(View.GONE);
            binding.cardOrder.setAlpha(1.0f);

            switch (status) {
                case "Đang chờ":
                    binding.cardBadge.setCardBackgroundColor(Color.parseColor("#FFDAD6"));
                    binding.tvStatusBadge.setTextColor(Color.parseColor("#93000A"));
                    binding.btnAction.setVisibility(View.VISIBLE);
                    binding.btnAction.setText("Bắt đầu chuẩn bị");
                    binding.btnAction.setBackgroundColor(Color.parseColor("#000613"));
                    binding.btnAction.setOnClickListener(v -> listener.onAction(orderWithDetails.order.getOrderId(), "Đang chuẩn bị"));
                    break;
                case "Đang chuẩn bị":
                    binding.cardBadge.setCardBackgroundColor(Color.parseColor("#FF851B"));
                    binding.tvStatusBadge.setTextColor(Color.parseColor("#612D00"));
                    binding.btnAction.setVisibility(View.VISIBLE);
                    binding.btnAction.setText("Hoàn thành đơn");
                    binding.btnAction.setBackgroundColor(Color.parseColor("#964900"));
                    binding.btnAction.setOnClickListener(v -> listener.onAction(orderWithDetails.order.getOrderId(), "Đã xong"));
                    break;
                case "Đã xong":
                    binding.cardBadge.setCardBackgroundColor(Color.parseColor("#EFEDF0"));
                    binding.tvStatusBadge.setTextColor(Color.parseColor("#43474E"));
                    binding.tvCompletedLabel.setVisibility(View.VISIBLE);
                    binding.cardOrder.setAlpha(0.8f);
                    break;
            }

            // Setup inner list of items
            MiniItemAdapter itemAdapter = new MiniItemAdapter(orderWithDetails.details);
            binding.rvItems.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            binding.rvItems.setAdapter(itemAdapter);
        }
    }

    static class MiniItemAdapter extends RecyclerView.Adapter<MiniItemAdapter.ItemViewHolder> {
        private final List<OrderWithDetails.OrderDetailWithItem> items;
        private final DecimalFormat formatter = new DecimalFormat("#,###đ");

        public MiniItemAdapter(List<OrderWithDetails.OrderDetailWithItem> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemOrderDetailMiniBinding binding = ItemOrderDetailMiniBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new ItemViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
            OrderWithDetails.OrderDetailWithItem item = items.get(position);
            String name = (item.menuItem != null ? item.menuItem.getItemName() : "Món đã xóa");
            holder.binding.tvItemName.setText(item.orderDetail.getQuantity() + "x " + name);
            holder.binding.tvItemPrice.setText(formatter.format(item.orderDetail.getSubtotal()));
        }

        @Override
        public int getItemCount() {
            return items != null ? items.size() : 0;
        }

        static class ItemViewHolder extends RecyclerView.ViewHolder {
            final ItemOrderDetailMiniBinding binding;
            ItemViewHolder(ItemOrderDetailMiniBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}
