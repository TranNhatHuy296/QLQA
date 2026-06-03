package com.example.qlqa.data.repository;

import android.app.Application;
import com.example.qlqa.data.local.AppDatabase;
import com.example.qlqa.data.local.dao.NotificationDao;
import com.example.qlqa.data.local.entities.Notification;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NotificationRepository {
    private final NotificationDao notificationDao;
    private final ExecutorService executorService;

    public NotificationRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        notificationDao = db.notificationDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public List<Notification> getAllNotifications() {
        return notificationDao.getAllNotifications();
    }

    public List<Notification> getUnreadNotifications() {
        return notificationDao.getUnreadNotifications();
    }

    public void insert(Notification notification, OnActionCompleteListener callback) {
        executorService.execute(() -> {
            long id = notificationDao.insert(notification);
            if (callback != null) callback.onComplete(id > 0);
        });
    }

    public void markAsRead(int id) {
        executorService.execute(() -> notificationDao.markAsRead(id));
    }

    public interface OnActionCompleteListener {
        void onComplete(boolean success);
    }
}
