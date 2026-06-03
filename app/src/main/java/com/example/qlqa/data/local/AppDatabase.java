package com.example.qlqa.data.local;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.qlqa.data.local.dao.*;
import com.example.qlqa.data.local.entities.*;

@Database(entities = {
    Account.class,
    Staff.class,
    Table.class,
    Category.class,
    MenuItem.class,
    Order.class,
    OrderDetail.class,
    OrderHistory.class,
    Notification.class
}, version = 10, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract AccountDao accountDao();
    public abstract StaffDao staffDao();
    public abstract TableDao tableDao();
    public abstract CategoryDao categoryDao();
    public abstract MenuItemDao menuItemDao();
    public abstract OrderDao orderDao();
    public abstract OrderDetailDao orderDetailDao();
    public abstract OrderHistoryDao orderHistoryDao();
    public abstract NotificationDao notificationDao();

    public static AppDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "qlqa_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
