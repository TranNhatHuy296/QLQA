package com.example.qlqa.ui;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.qlqa.data.local.model.OrderWithDetails;
import com.example.qlqa.databinding.ActivityRevenueReportBinding;
import com.example.qlqa.ui.adapter.TransactionAdapter;
import com.example.qlqa.utils.SessionManager;
import com.example.qlqa.viewmodel.RevenueReportViewModel;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class RevenueReportActivity extends AppCompatActivity {

    private ActivityRevenueReportBinding binding;
    private RevenueReportViewModel viewModel;
    private TransactionAdapter adapter;
    private SessionManager sessionManager;

    private final ActivityResultLauncher<String> createPdfLauncher = registerForActivityResult(
            new ActivityResultContracts.CreateDocument("application/pdf"),
            uri -> { if (uri != null) generatePdf(uri); }
    );

    private final ActivityResultLauncher<String> createExcelLauncher = registerForActivityResult(
            new ActivityResultContracts.CreateDocument("text/csv"),
            uri -> { if (uri != null) generateCsv(uri); }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRevenueReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        if (!"Admin".equalsIgnoreCase(sessionManager.getRole())) {
            Toast.makeText(this, "Chỉ Quản trị viên mới có quyền truy cập!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(RevenueReportViewModel.class);
        setupUI();
        observeViewModel();
        
        viewModel.loadData();
        viewModel.loadStaff();
    }

    private void setupUI() {
        adapter = new TransactionAdapter(orderId -> {
            Toast.makeText(this, "Chi tiết đơn hàng #" + orderId, Toast.LENGTH_SHORT).show();
        });
        binding.rvTransactions.setLayoutManager(new LinearLayoutManager(this));
        binding.rvTransactions.setAdapter(adapter);

        binding.btnFilter.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng lọc đang được phát triển", Toast.LENGTH_SHORT).show();
        });

        binding.btnExportPdf.setOnClickListener(v -> {
            List<OrderWithDetails> data = viewModel.getRecentTransactions().getValue();
            if (data == null || data.isEmpty()) {
                Toast.makeText(this, "Không có dữ liệu để xuất!", Toast.LENGTH_SHORT).show();
                return;
            }
            createPdfLauncher.launch("Bao_cao_doanh_thu_" + System.currentTimeMillis() + ".pdf");
        });

        binding.btnExportExcel.setOnClickListener(v -> {
            List<OrderWithDetails> data = viewModel.getRecentTransactions().getValue();
            if (data == null || data.isEmpty()) {
                Toast.makeText(this, "Không có dữ liệu để xuất!", Toast.LENGTH_SHORT).show();
                return;
            }
            createExcelLauncher.launch("Bao_cao_doanh_thu_" + System.currentTimeMillis() + ".csv");
        });

        // Điều hướng Bottom Nav
        binding.navHome.setOnClickListener(v -> {
            startActivity(new Intent(this, DashboardActivity.class));
            finish();
        });

        binding.navMenu.setOnClickListener(v -> {
            startActivity(new Intent(this, CategoryManagementActivity.class));
            finish();
        });

        binding.navManage.setOnClickListener(v -> {
            startActivity(new Intent(this, ManageActivity.class));
            finish();
        });
        
        binding.btnNotification.setOnClickListener(v -> {
            startActivity(new Intent(this, NotificationActivity.class));
        });
    }

    private void generatePdf(Uri uri) {
        try (OutputStream outputStream = getContentResolver().openOutputStream(uri)) {
            PdfDocument document = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);
            Canvas canvas = page.getCanvas();
            Paint paint = new Paint();
            
            paint.setTextSize(18f);
            paint.setFakeBoldText(true);
            canvas.drawText("BAO CAO DOANH THU", 200, 50, paint);
            
            paint.setTextSize(12f);
            paint.setFakeBoldText(false);
            canvas.drawText("Tong doanh thu: " + binding.tvMonthlyRevenue.getText(), 50, 100, paint);
            canvas.drawText("Tong don hang: " + binding.tvTotalOrders.getText(), 50, 120, paint);
            
            canvas.drawText("Ma DH", 50, 160, paint);
            canvas.drawText("Ngay", 120, 160, paint);
            canvas.drawText("Tong tien", 450, 160, paint);
            canvas.drawLine(50, 165, 550, 165, paint);
            
            int y = 185;
            List<OrderWithDetails> list = viewModel.getRecentTransactions().getValue();
            if (list != null) {
                for (OrderWithDetails item : list) {
                    if (y > 800) break;
                    canvas.drawText("#" + item.order.getOrderId(), 50, y, paint);
                    canvas.drawText(item.order.getCreatedAt().substring(0, 10), 120, y, paint);
                    canvas.drawText(String.format("%,.0f d", item.order.getTotalAmount()), 450, y, paint);
                    y += 20;
                }
            }
            
            document.finishPage(page);
            document.writeTo(outputStream);
            document.close();
            Toast.makeText(this, "Đã xuất PDF thành công!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Lỗi khi xuất PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void generateCsv(Uri uri) {
        try (OutputStream outputStream = getContentResolver().openOutputStream(uri)) {
            StringBuilder csv = new StringBuilder();
            csv.append("Ma don hang,Ngay tao,Tong tien,Trang thai\n");
            
            List<OrderWithDetails> list = viewModel.getRecentTransactions().getValue();
            if (list != null) {
                for (OrderWithDetails item : list) {
                    csv.append(item.order.getOrderId()).append(",");
                    csv.append(item.order.getCreatedAt()).append(",");
                    csv.append(item.order.getTotalAmount()).append(",");
                    csv.append(item.order.getStatus()).append("\n");
                }
            }
            
            outputStream.write(csv.toString().getBytes(StandardCharsets.UTF_8));
            Toast.makeText(this, "Đã xuất Excel (CSV) thành công!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Lỗi khi xuất Excel: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void observeViewModel() {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        
        viewModel.getTotalRevenue().observe(this, revenue -> {
            binding.tvMonthlyRevenue.setText(formatter.format(revenue));
        });

        viewModel.getTotalOrders().observe(this, count -> {
            binding.tvTotalOrders.setText(count + " đơn");
        });

        viewModel.getRecentTransactions().observe(this, list -> {
            if (list == null || list.isEmpty()) {
                binding.tvEmpty.setVisibility(View.VISIBLE);
                binding.rvTransactions.setVisibility(View.GONE);
            } else {
                binding.tvEmpty.setVisibility(View.GONE);
                binding.rvTransactions.setVisibility(View.VISIBLE);
                adapter.setOrders(list);
            }
        });

        viewModel.getErrorMessage().observe(this, msg -> {
            if (msg != null) Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });
    }
}
