package com.example.qlqa.ui;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.qlqa.R;
import com.example.qlqa.data.local.AppDatabase;
import com.example.qlqa.data.local.entities.Account;
import com.example.qlqa.data.local.entities.Notification;
import com.example.qlqa.data.repository.NotificationRepository;
import com.example.qlqa.databinding.ActivityNotificationBinding;
import com.example.qlqa.databinding.DialogAddNotificationBinding;
import com.example.qlqa.ui.adapter.NotificationAdapter;
import com.example.qlqa.utils.SessionManager;
import com.google.android.material.chip.Chip;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NotificationActivity extends AppCompatActivity {

    private ActivityNotificationBinding binding;
    private NotificationAdapter adapter;
    private NotificationRepository repository;
    private SessionManager sessionManager;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private boolean showingAll = true;

    private String selectedImageUri = null;
    private int currentAccountId = -1;
    private DialogAddNotificationBinding currentDialogBinding;

    // Image picker launcher
    private final ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null && currentDialogBinding != null) {
                    selectedImageUri = uri.toString();
                    currentDialogBinding.ivSelectedImage.setVisibility(View.VISIBLE);
                    currentDialogBinding.ivSelectedImage.setImageURI(uri);
                    
                    // Hide placeholder icons/text inside btnUploadImage
                    if (currentDialogBinding.btnUploadImage.getChildCount() >= 2) {
                        currentDialogBinding.btnUploadImage.getChildAt(0).setVisibility(View.GONE);
                        currentDialogBinding.btnUploadImage.getChildAt(1).setVisibility(View.GONE);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        if (!"Admin".equals(sessionManager.getRole())) {
            Toast.makeText(this, "Chỉ Quản trị viên mới có quyền truy cập!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        repository = new NotificationRepository(getApplication());
        setupUI();
        fetchCurrentAccountId();
        loadNotifications();
    }

    private void fetchCurrentAccountId() {
        String username = sessionManager.getUsername();
        if (username != null) {
            executorService.execute(() -> {
                Account account = AppDatabase.getInstance(this).accountDao().getAccountByUsername(username);
                if (account != null) {
                    currentAccountId = account.getAccountId();
                }
            });
        }
    }

    private void setupUI() {
        binding.btnBack.setOnClickListener(v -> finish());

        adapter = new NotificationAdapter(notification -> {
            showNotificationDetail(notification);
            if (!notification.isRead()) {
                repository.markAsRead(notification.getNotificationId());
                notification.setRead(true);
                adapter.notifyDataSetChanged();
            }
        });

        binding.rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        binding.rvNotifications.setAdapter(adapter);

        binding.btnTabAll.setOnClickListener(v -> {
            showingAll = true;
            updateTabUI();
            loadNotifications();
        });

        binding.btnTabUnread.setOnClickListener(v -> {
            showingAll = false;
            updateTabUI();
            loadNotifications();
        });

        binding.fabAddNotification.setOnClickListener(v -> showAddNotificationDialog());
        
        binding.navHome.setOnClickListener(v -> finish());
    }

    private void updateTabUI() {
        if (showingAll) {
            binding.btnTabAll.setBackgroundTintList(getColorStateList(R.color.black));
            binding.btnTabAll.setTextColor(getColor(R.color.white));
            binding.btnTabUnread.setBackgroundTintList(getColorStateList(R.color.bg_light));
            binding.btnTabUnread.setTextColor(getColor(R.color.text_label));
        } else {
            binding.btnTabUnread.setBackgroundTintList(getColorStateList(R.color.black));
            binding.btnTabUnread.setTextColor(getColor(R.color.white));
            binding.btnTabAll.setBackgroundTintList(getColorStateList(R.color.bg_light));
            binding.btnTabAll.setTextColor(getColor(R.color.text_label));
        }
    }

    private void loadNotifications() {
        executorService.execute(() -> {
            List<Notification> list = showingAll ? repository.getAllNotifications() : repository.getUnreadNotifications();
            runOnUiThread(() -> adapter.setNotifications(list));
        });
    }

    private void showNotificationDetail(Notification notification) {
        new AlertDialog.Builder(this)
                .setTitle(notification.getTitle())
                .setMessage(notification.getContent() + "\n\nLoại: " + notification.getType() + "\nĐối tượng: " + notification.getRecipients())
                .setPositiveButton("Đóng", null)
                .show();
    }

    private void showAddNotificationDialog() {
        selectedImageUri = null;
        currentDialogBinding = DialogAddNotificationBinding.inflate(getLayoutInflater());
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(currentDialogBinding.getRoot())
                .create();

        currentDialogBinding.btnUploadImage.setOnClickListener(v -> {
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });

        currentDialogBinding.btnSend.setOnClickListener(v -> {
            saveNotification(currentDialogBinding, "Active", dialog);
        });

        currentDialogBinding.btnSaveDraft.setOnClickListener(v -> {
            saveNotification(currentDialogBinding, "Draft", dialog);
        });

        dialog.show();
    }

    private void saveNotification(DialogAddNotificationBinding db, String status, AlertDialog dialog) {
        String title = db.etTitle.getText().toString().trim();
        String content = db.etContent.getText().toString().trim();
        
        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ tiêu đề và nội dung", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> recipients = new ArrayList<>();
        for (int i = 0; i < db.cgRecipients.getChildCount(); i++) {
            Chip chip = (Chip) db.cgRecipients.getChildAt(i);
            if (chip.isChecked()) {
                recipients.add(chip.getText().toString());
            }
        }

        if (recipients.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ít nhất một đối tượng nhận", Toast.LENGTH_SHORT).show();
            return;
        }

        int checkedId = db.rgNotificationType.getCheckedRadioButtonId();
        String type = "Hệ thống";
        if (checkedId == R.id.rbEmergency) type = "Khẩn cấp";
        else if (checkedId == R.id.rbOffer) type = "Ưu đãi";
        else if (checkedId == R.id.rbReminder) type = "Nhắc nhở";

        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        
        if (currentAccountId == -1) {
            Toast.makeText(this, "Đang xác thực người dùng, vui lòng thử lại sau giây lát", Toast.LENGTH_SHORT).show();
            fetchCurrentAccountId();
            return;
        }

        Notification notification = new Notification(
            title, content, currentAccountId,
            now, type, TextUtils.join(",", recipients), selectedImageUri, false, status
        );

        repository.insert(notification, success -> {
            runOnUiThread(() -> {
                if (success) {
                    Toast.makeText(this, status.equals("Active") ? "Gửi thông báo thành công!" : "Đã lưu nháp!", Toast.LENGTH_SHORT).show();
                    loadNotifications();
                    dialog.dismiss();
                } else {
                    Toast.makeText(this, "Lỗi khi lưu thông báo", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
