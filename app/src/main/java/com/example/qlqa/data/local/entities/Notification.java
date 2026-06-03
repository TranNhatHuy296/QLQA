package com.example.qlqa.data.local.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.annotation.NonNull;

@Entity(
    tableName = "notifications",
    foreignKeys = @ForeignKey(
        entity = Account.class,
        parentColumns = "accountId",
        childColumns = "createdBy",
        onDelete = ForeignKey.CASCADE
    ),
    indices = {@Index("createdBy")}
)
public class Notification {
    @PrimaryKey(autoGenerate = true)
    private int notificationId;

    @NonNull
    private String title;
    
    @NonNull
    private String content;
    
    private int createdBy;
    
    @NonNull
    private String createdAt;
    
    @NonNull
    private String type; // Hệ thống, Khẩn cấp, Ưu đãi, Nhắc nhở
    
    @NonNull
    private String recipients; // e.g., "Quản lý,Nhân viên"
    
    private String imageUrl;
    
    private boolean isRead;
    
    @NonNull
    private String status; // Active, Deleted, Draft

    public Notification(@NonNull String title, @NonNull String content, int createdBy, 
                        @NonNull String createdAt, @NonNull String type, 
                        @NonNull String recipients, String imageUrl, boolean isRead, @NonNull String status) {
        this.title = title;
        this.content = content;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.type = type;
        this.recipients = recipients;
        this.imageUrl = imageUrl;
        this.isRead = isRead;
        this.status = status;
    }

    public int getNotificationId() { return notificationId; }
    public void setNotificationId(int notificationId) { this.notificationId = notificationId; }

    @NonNull
    public String getTitle() { return title; }
    public void setTitle(@NonNull String title) { this.title = title; }

    @NonNull
    public String getContent() { return content; }
    public void setContent(@NonNull String content) { this.content = content; }

    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }

    @NonNull
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(@NonNull String createdAt) { this.createdAt = createdAt; }

    @NonNull
    public String getType() { return type; }
    public void setType(@NonNull String type) { this.type = type; }

    @NonNull
    public String getRecipients() { return recipients; }
    public void setRecipients(@NonNull String recipients) { this.recipients = recipients; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    @NonNull
    public String getStatus() { return status; }
    public void setStatus(@NonNull String status) { this.status = status; }
}
