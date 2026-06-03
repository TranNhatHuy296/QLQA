package com.example.qlqa.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.qlqa.R;
import com.example.qlqa.data.local.entities.Notification;
import com.example.qlqa.databinding.ItemNotificationBinding;
import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<Notification> notifications = new ArrayList<>();
    private final OnNotificationClickListener listener;

    public interface OnNotificationClickListener {
        void onNotificationClick(Notification notification);
    }

    public NotificationAdapter(OnNotificationClickListener listener) {
        this.listener = listener;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNotificationBinding binding = ItemNotificationBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new NotificationViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        holder.bind(notifications.get(position));
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder {
        private final ItemNotificationBinding binding;

        public NotificationViewHolder(ItemNotificationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Notification notification) {
            binding.tvTitle.setText(notification.getTitle());
            binding.tvContentSummary.setText(notification.getContent());

            // Set icon and background color based on type
            int iconRes = android.R.drawable.ic_dialog_info;
            int bgColor = 0xFFFFDCC7; // Default
            int tintColor = 0xFF311300;

            switch (notification.getType()) {
                case "Hệ thống":
                    iconRes = android.R.drawable.ic_popup_reminder;
                    bgColor = 0xFFE3E2E5;
                    tintColor = 0xFF43474E;
                    break;
                case "Khẩn cấp":
                    iconRes = android.R.drawable.ic_dialog_alert;
                    bgColor = 0xFFFFDCC7;
                    tintColor = 0xFF311300;
                    break;
                case "Ưu đãi":
                    iconRes = android.R.drawable.ic_menu_gallery;
                    bgColor = 0xFFD4E3FF;
                    tintColor = 0xFF001C3A;
                    break;
                case "Nhắc nhở":
                    iconRes = android.R.drawable.ic_lock_idle_alarm;
                    bgColor = 0xFFAFC8F0;
                    tintColor = 0xFF001C3A;
                    break;
            }

            binding.cvIconBackground.setCardBackgroundColor(bgColor);
            binding.ivIcon.setImageResource(iconRes);
            binding.ivIcon.setColorFilter(tintColor);

            // Highlight unread notifications (e.g., bold title)
            if (!notification.isRead()) {
                binding.tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
                binding.getRoot().setAlpha(1.0f);
            } else {
                binding.tvTitle.setTypeface(null, android.graphics.Typeface.NORMAL);
                binding.getRoot().setAlpha(0.7f);
            }

            binding.getRoot().setOnClickListener(v -> listener.onNotificationClick(notification));
        }
    }
}
