package com.example.qlqa.ui.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.qlqa.databinding.ItemTableGridBinding;
import com.example.qlqa.data.local.entities.Table;
import java.util.ArrayList;
import java.util.List;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.TableViewHolder> {

    private List<Table> tables = new ArrayList<>();
    private final OnTableClickListener listener;

    public interface OnTableClickListener {
        void onTableClick(Table table);
    }

    public TableAdapter(OnTableClickListener listener) {
        this.listener = listener;
    }

    public void setTables(List<Table> tables) {
        this.tables = tables;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTableGridBinding binding = ItemTableGridBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new TableViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TableViewHolder holder, int position) {
        holder.bind(tables.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return tables.size();
    }

    static class TableViewHolder extends RecyclerView.ViewHolder {
        private final ItemTableGridBinding binding;

        public TableViewHolder(ItemTableGridBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Table table, OnTableClickListener listener) {
            binding.tvTableName.setText(table.getTableName());
            binding.tvStatus.setText(table.getStatus());

            // UI styling based on status according to typical wireframe specs
            switch (table.getStatus()) {
                case "Có khách":
                    binding.cardTable.setCardBackgroundColor(Color.parseColor("#FEF2F2"));
                    binding.cardTable.setStrokeColor(Color.parseColor("#FECACA"));
                    binding.tvTableName.setTextColor(Color.parseColor("#991B1B"));
                    binding.tvStatus.setTextColor(Color.parseColor("#B91C1C"));
                    binding.layoutCustomers.setVisibility(View.VISIBLE);
                    binding.tvCustomerCount.setText(String.valueOf(table.getCurrentCustomers()));
                    binding.tvCustomerCount.setTextColor(Color.parseColor("#B91C1C"));
                    binding.ivCustomerIcon.setColorFilter(Color.parseColor("#DC2626"));
                    binding.ivStatusIcon.setImageResource(android.R.drawable.ic_menu_myplaces);
                    binding.ivStatusIcon.setColorFilter(Color.parseColor("#DC2626"));
                    break;
                case "Đang dọn":
                    binding.cardTable.setCardBackgroundColor(Color.parseColor("#FEFCE8"));
                    binding.cardTable.setStrokeColor(Color.parseColor("#FEF08A"));
                    binding.tvTableName.setTextColor(Color.parseColor("#854D0E"));
                    binding.tvStatus.setTextColor(Color.parseColor("#A16207"));
                    binding.layoutCustomers.setVisibility(View.GONE);
                    binding.ivStatusIcon.setImageResource(android.R.drawable.ic_menu_manage);
                    binding.ivStatusIcon.setColorFilter(Color.parseColor("#CA8A04"));
                    break;
                default: // Trống
                    binding.cardTable.setCardBackgroundColor(Color.parseColor("#F0FDF4"));
                    binding.cardTable.setStrokeColor(Color.parseColor("#BBF7D0"));
                    binding.tvTableName.setTextColor(Color.parseColor("#166534"));
                    binding.tvStatus.setTextColor(Color.parseColor("#15803D"));
                    binding.layoutCustomers.setVisibility(View.GONE);
                    binding.ivStatusIcon.setImageResource(android.R.drawable.ic_menu_my_calendar);
                    binding.ivStatusIcon.setColorFilter(Color.parseColor("#16A34A"));
                    break;
            }

            itemView.setOnClickListener(v -> listener.onTableClick(table));
        }
    }
}
