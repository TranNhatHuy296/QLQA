package com.example.qlqa.data.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.qlqa.data.local.AppDatabase;
import com.example.qlqa.data.local.entities.*;
import com.example.qlqa.utils.PasswordUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class DataSeeder {
    private static final String TAG = "DataSeeder";

    public static void seedData(Context context) {
        new SeedTask(context).execute();
    }

    private static class SeedTask extends AsyncTask<Void, Void, Void> {
        private final Context context;
        private final AppDatabase db;
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        private final Random random = new Random();

        SeedTask(Context context) {
            this.context = context.getApplicationContext();
            this.db = AppDatabase.getInstance(this.context);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Check if data already exists (using categories as a marker)
            if (!db.categoryDao().getAllCategories().isEmpty()) {
                Log.d(TAG, "Database already has data. Skipping seeding.");
                return null;
            }

            Log.d(TAG, "Starting database seeding...");

            String now = dateFormat.format(new Date());

            // 1. Seed Staff
            long adminStaffId = db.staffDao().insert(new Staff(
                    "NV-2024-001", "Nguyễn Văn Admin", "admin@gmail.com", "0901234567",
                    "Quản lý", "123 Đường ABC, Quận 1, TP.HCM", "Ca sáng, Ca chiều", null, "1990-01-01", now, "Đã có mặt"
            ));
            long staffId2 = db.staffDao().insert(new Staff(
                    "NV-2024-002", "Trần Thị Phục Vụ", "staff1@gmail.com", "0907654321",
                    "Phục vụ", "456 Đường XYZ, Quận 3, TP.HCM", "Ca sáng", null, "1995-05-15", now, "Đã có mặt"
            ));
            long staffId3 = db.staffDao().insert(new Staff(
                    "NV-2024-003", "Lê Văn Đầu Bếp", "daubep1@gmail.com", "0909998887",
                    "Đầu bếp", "789 Đường LMN, Quận Bình Thạnh, TP.HCM", "Ca chiều", null, "1988-10-20", now, "Đã có mặt"
            ));
            db.staffDao().insert(new Staff(
                    "NV-2024-004", "Phạm Văn Pha Chế", "phache1@gmail.com", "0901112223",
                    "Pha chế", "321 Đường GHI, Quận 10, TP.HCM", "Ca sáng", null, "1992-03-12", now, "Vắng mặt"
            ));

            // 2. Seed Accounts
            db.accountDao().insert(new Account(
                    "admin@gmail.com", PasswordUtils.hashPassword("admin123"), "Admin", (int) adminStaffId, "Hoạt động", now
            ));
            db.accountDao().insert(new Account(
                    "staff1@gmail.com", PasswordUtils.hashPassword("staff123"), "Staff", (int) staffId2, "Hoạt động", now
            ));

            // 3. Seed Categories
            String[] catNames = {"Cà phê", "Trà sữa", "Nước ép & Sinh tố", "Đồ ăn nhanh", "Món chính", "Tráng miệng"};
            long[] catIds = new long[catNames.length];
            for (int i = 0; i < catNames.length; i++) {
                catIds[i] = db.categoryDao().insert(new Category(catNames[i], "Mô tả cho " + catNames[i], "Hoạt động"));
            }

            // 4. Seed Menu Items
            String[][] menuData = {
                {"Cà phê Đen", "Cà phê Sữa", "Bạc xỉu", "Cappuccino", "Latte", "Espresso", "Americano", "Cà phê Muối", "Cà phê Trứng", "Cold Brew"},
                {"Trà sữa Truyền thống", "Trà sữa Trân châu", "Trà sữa Matcha", "Trà sữa Thái xanh", "Trà sữa Thái đỏ", "Trà sữa Khoai môn", "Trà sữa Socola", "Trà sữa Hokkaido", "Trà sữa Oolong", "Hồng trà sữa"},
                {"Nước ép Cam", "Nước ép Táo", "Nước ép Dưa hấu", "Nước ép Thơm", "Nước ép Cà rốt", "Sinh tố Bơ", "Sinh tố Xoài", "Sinh tố Mãng cầu", "Sinh tố Dâu", "Chanh dây đá xay"},
                {"Khoai tây chiên", "Cá viên chiên", "Xúc xích đức", "Gà rán", "Hamburger", "Sandwich", "Phô mai que", "Nem chua rán", "Bánh tráng trộn", "Ngô chiên"},
                {"Cơm chiên Dương Châu", "Mì Ý Bolonese", "Phở bò", "Bún chả", "Bánh mì Hội An", "Bò né", "Cơm tấm", "Hủ tiếu Nam Vang", "Bún bò Huế", "Mì Quảng"},
                {"Bánh Flan", "Rau câu", "Chè khúc bạch", "Kem dừa", "Tiramisu", "Bánh mousse đào", "Pudding trứng", "Sữa chua nếp cẩm", "Trái cây dĩa", "Chè bưởi"}
            };

            for (int i = 0; i < catIds.length; i++) {
                for (int j = 0; j < 10; j++) {
                    double price = 20000 + random.nextInt(80) * 1000;
                    boolean isBestSeller = (j < 2);
                    db.menuItemDao().insert(new MenuItem(
                            (int) catIds[i], menuData[i][j], price, null, "Mô tả cho " + menuData[i][j], 0, isBestSeller, "Còn hàng", now
                    ));
                }
            }

            // 5. Seed Tables
            for (int i = 1; i <= 10; i++) {
                String status = (i == 1 || i == 3) ? "Có khách" : "Trống";
                int customers = status.equals("Có khách") ? random.nextInt(4) + 1 : 0;
                db.tableDao().insert(new Table("Bàn " + String.format("%02d", i), "Tầng 1", 4, status, customers, now));
            }
            for (int i = 11; i <= 20; i++) {
                db.tableDao().insert(new Table("Bàn " + i, "Tầng 2", 6, "Trống", 0, now));
            }

            // 6. Seed Orders for Revenue (Past month)
            List<MenuItem> allItems = db.menuItemDao().getAllMenuItems();
            for (int i = 0; i < 30; i++) { // 30 orders
                int randomTableId = random.nextInt(20) + 1;
                int randomStaffId = (int) (random.nextBoolean() ? adminStaffId : staffId2);
                
                // Set dates across the last month
                long offset = (long) i * 24 * 60 * 60 * 1000L;
                String orderDate = dateFormat.format(new Date(System.currentTimeMillis() - offset));
                
                long orderId = db.orderDao().insert(new Order(
                        randomTableId, randomStaffId, "Đã thanh toán", null, 0, orderDate, orderDate
                ));

                double total = 0;
                int itemsInOrder = random.nextInt(3) + 1;
                for (int k = 0; k < itemsInOrder; k++) {
                    MenuItem item = allItems.get(random.nextInt(allItems.size()));
                    int qty = random.nextInt(2) + 1;
                    double subtotal = item.getPrice() * qty;
                    db.orderDetailDao().insert(new OrderDetail(
                            (int) orderId, item.getMenuItemId(), qty, item.getPrice(), 0, subtotal
                    ));
                    total += subtotal;
                }
                
                // Update total amount in order
                Order order = db.orderDao().getOrderById((int) orderId);
                if (order != null) {
                    order.setTotalAmount(total);
                    db.orderDao().update(order);
                }
            }

            // 7. Seed Notifications
            db.notificationDao().insert(new Notification(
                    "Chào mừng bạn", "Chào mừng bạn đến với hệ thống Quản Lý Quán Ăn QLQA.", (int) adminStaffId, now, "Hệ thống", "Quản lý,Nhân viên", null, false, "Active"
            ));
            db.notificationDao().insert(new Notification(
                    "Cập nhật thực đơn", "Thực đơn mùa hè mới đã được cập nhật với nhiều món giải nhiệt.", (int) adminStaffId, now, "Ưu đãi", "Quản lý,Nhân viên", null, false, "Active"
            ));
            db.notificationDao().insert(new Notification(
                    "Thông báo họp", "Họp giao ca vào lúc 8h sáng thứ Hai tuần tới.", (int) adminStaffId, now, "Nhắc nhở", "Quản lý,Nhân viên", null, false, "Active"
            ));

            Log.d(TAG, "Database seeding completed successfully.");
            return null;
        }
    }
}
