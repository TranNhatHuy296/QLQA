package com.example.qlqa.ui.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.qlqa.data.local.entities.Staff;
import com.example.qlqa.databinding.ItemStaffBinding;
import java.util.ArrayList;
import java.util.List;

public class StaffAdapter extends RecyclerView.Adapter<StaffAdapter.StaffViewHolder> {

    private List<Staff> staffList = new ArrayList<>();
    private final OnStaffClickListener listener;

    public interface OnStaffClickListener {
        void onStaffClick(Staff staff);
    }

    public StaffAdapter(OnStaffClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<Staff> list) {
        this.staffList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StaffViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemStaffBinding binding = ItemStaffBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new StaffViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull StaffViewHolder holder, int position) {
        holder.bind(staffList.get(position));
    }

    @Override
    public int getItemCount() {
        return staffList.size();
    }

    class StaffViewHolder extends RecyclerView.ViewHolder {
        private final ItemStaffBinding binding;

        public StaffViewHolder(ItemStaffBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Staff staff) {
            binding.tvStaffName.setText(staff.getFullName());
            binding.tvStaffInfo.setText(String.format("Mã: %s • %s", staff.getEmployeeCode(), staff.getPosition()));
            binding.tvStaffShift.setText(staff.getShifts());

            // Status UI
            binding.tvStatusLabel.setText(staff.getStatus().toUpperCase());
            switch (staff.getStatus()) {
                case "Đã có mặt":
                    binding.vStatusIndicator.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF22C55E));
                    binding.tvStatusLabel.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFDCFCE7));
                    binding.tvStatusLabel.setTextColor(0xFF166534);
                    break;
                case "Vắng mặt":
                    binding.vStatusIndicator.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFEF4444));
                    binding.tvStatusLabel.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFFEE2E2));
                    binding.tvStatusLabel.setTextColor(0xFF991B1B);
                    break;
                default: // Chưa bắt đầu ca
                    binding.vStatusIndicator.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF9CA3AF));
                    binding.tvStatusLabel.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFF3F4F6));
                    binding.tvStatusLabel.setTextColor(0xFF4B5563);
                    break;
            }

            // Image handling (Placeholder for now)
            if (staff.getProfileImageUrl() != null) {
                binding.ivStaffAvatar.setImageURI(android.net.Uri.parse(staff.getProfileImageUrl()));
            } else {
                binding.ivStaffAvatar.setImageResource(android.R.drawable.ic_menu_gallery);
            }

            binding.getRoot().setOnClickListener(v -> listener.onStaffClick(staff));
        }
    }
}
