package com.example.qlqa.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.qlqa.R;
import com.example.qlqa.databinding.ActivityPaymentDetailBinding;
import com.example.qlqa.ui.adapter.BillRowAdapter;
import com.example.qlqa.utils.SessionManager;
import com.example.qlqa.viewmodel.PaymentDetailViewModel;

import java.text.NumberFormat;
import java.util.Locale;

public class PaymentDetailActivity extends AppCompatActivity {

    private ActivityPaymentDetailBinding binding;
    private PaymentDetailViewModel viewModel;
    private BillRowAdapter adapter;
    private SessionManager sessionManager;
    private int orderId;
    private int tableId;
    private String selectedMethod = "Tiền mặt";
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Nhận dữ liệu từ Intent: Hỗ trợ cả ORDER_ID (từ list) và TABLE_ID (từ sơ đồ bàn)
        orderId = getIntent().getIntExtra("ORDER_ID", -1);
        tableId = getIntent().getIntExtra("TABLE_ID", -1);

        if (orderId == -1 && tableId == -1) {
            Toast.makeText(this, "Không tìm thấy thông tin thanh toán!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        sessionManager = new SessionManager(this);
        viewModel = new ViewModelProvider(this).get(PaymentDetailViewModel.class);

        setupUI();
        setupRecyclerView();
        observeViewModel();

        // Tải dữ liệu: Ưu tiên load theo hóa đơn cụ thể, nếu không có thì tìm hóa đơn đang mở của bàn
        if (orderId != -1) {
            viewModel.loadOrder(orderId);
        } else {
            viewModel.loadActiveOrderForTable(tableId);
        }
    }

    private void setupUI() {
        binding.btnBack.setOnClickListener(v -> finish());
        
        binding.btnApplyVoucher.setOnClickListener(v -> {
            String code = binding.etVoucher.getText().toString().trim();
            if (!code.isEmpty()) {
                viewModel.applyVoucher(code);
            }
        });

        binding.btnMethodCash.setOnClickListener(v -> selectPaymentMethod("Tiền mặt"));
        binding.btnMethodTransfer.setOnClickListener(v -> selectPaymentMethod("Chuyển khoản"));
        binding.btnMethodCard.setOnClickListener(v -> selectPaymentMethod("Thẻ"));

        binding.btnConfirmPayment.setOnClickListener(v -> {
            viewModel.confirmPayment(sessionManager.getStaffId(), selectedMethod);
        });

        binding.btnPrintBill.setOnClickListener(v -> {
            Toast.makeText(this, "Đang in hóa đơn...", Toast.LENGTH_SHORT).show();
        });

        // Initialize UI selection
        selectPaymentMethod(selectedMethod);
    }

    private void selectPaymentMethod(String method) {
        selectedMethod = method;
        
        int defaultStrokeColor = getResources().getColor(R.color.gray_light, getTheme());
        int highlightColor = getResources().getColor(R.color.primary_orange, getTheme());

        // Reset all buttons to default state
        binding.btnMethodCash.setStrokeColor(defaultStrokeColor);
        binding.btnMethodCash.setStrokeWidth(2);
        binding.btnMethodTransfer.setStrokeColor(defaultStrokeColor);
        binding.btnMethodTransfer.setStrokeWidth(2);
        binding.btnMethodCard.setStrokeColor(defaultStrokeColor);
        binding.btnMethodCard.setStrokeWidth(2);

        // Highlight the selected method
        if ("Tiền mặt".equals(method)) {
            binding.btnMethodCash.setStrokeColor(highlightColor);
            binding.btnMethodCash.setStrokeWidth(4);
        } else if ("Chuyển khoản".equals(method)) {
            binding.btnMethodTransfer.setStrokeColor(highlightColor);
            binding.btnMethodTransfer.setStrokeWidth(4);
        } else if ("Thẻ".equals(method)) {
            binding.btnMethodCard.setStrokeColor(highlightColor);
            binding.btnMethodCard.setStrokeWidth(4);
        }
    }

    private void setupRecyclerView() {
        adapter = new BillRowAdapter();
        binding.rvBillItems.setLayoutManager(new LinearLayoutManager(this));
        binding.rvBillItems.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.getOrderWithDetails().observe(this, details -> {
            if (details != null) {
                binding.tvHeaderTitle.setText("Thanh toán - " + details.table.getTableName());
                binding.tvOrderTime.setText(details.order.getCreatedAt());
                adapter.setItems(details.details);
            }
        });

        viewModel.getSubtotal().observe(this, value -> binding.tvSubtotal.setText(currencyFormatter.format(value)));
        viewModel.getTaxAmount().observe(this, value -> binding.tvTaxAmount.setText(currencyFormatter.format(value)));
        viewModel.getGrandTotal().observe(this, value -> binding.tvGrandTotal.setText(currencyFormatter.format(value)));

        viewModel.getPaymentSuccess().observe(this, success -> {
            if (success) {
                Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_LONG).show();
                finish();
            }
        });

        viewModel.getErrorMessage().observe(this, message -> {
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
