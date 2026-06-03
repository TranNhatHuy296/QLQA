package com.example.qlqa.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.qlqa.R;
import com.example.qlqa.data.local.dao.AccountDao;
import com.example.qlqa.databinding.ItemAccountRowBinding;
import java.util.ArrayList;
import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ViewHolder> {

    private List<AccountDao.AccountWithStaffResult> items = new ArrayList<>();
    private final OnAccountClickListener listener;

    public interface OnAccountClickListener {
        void onAccountClick(AccountDao.AccountWithStaffResult item);
    }

    public AccountAdapter(OnAccountClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<AccountDao.AccountWithStaffResult> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAccountRowBinding binding = ItemAccountRowBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
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
        private final ItemAccountRowBinding binding;

        ViewHolder(ItemAccountRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(AccountDao.AccountWithStaffResult item) {
            binding.tvName.setText(item.staff.getFullName());
            binding.tvEmail.setText(item.account.getUsername());
            binding.tvAvatarText.setText(item.staff.getFullName().substring(0, 1).toUpperCase());
            binding.tvRole.setText(item.account.getRole());
            binding.tvStatus.setText(item.account.getStatus());

            if ("Hoạt động".equals(item.account.getStatus()) || "Đang hoạt động".equals(item.account.getStatus())) {
                binding.viewStatusDot.setBackgroundResource(R.drawable.dot_active);
            } else {
                binding.viewStatusDot.setBackgroundResource(R.drawable.dot_locked);
            }

            itemView.setOnClickListener(v -> listener.onAccountClick(item));
        }
    }
}
