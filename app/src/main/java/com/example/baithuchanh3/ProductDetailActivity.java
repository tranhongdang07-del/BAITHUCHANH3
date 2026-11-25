package com.example.baithuchanh3;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.DecimalFormat;

public class ProductDetailActivity extends AppCompatActivity {

    private TextView tvName;
    private TextView tvCategory;
    private TextView tvPrice;
    private TextView tvQuantity;
    private TextView tvStockValue;
    private TextView tvStatus;
    private TextView tvWarningTag;
    private TextView tvDescription;

    private Button btnImportOne;
    private Button btnSellOne;
    private Button btnBack;
    private Button btnDeleteProduct;

    private DatabaseHelper dbHelper;
    private int productId = -1;
    private Product currentProduct;
    private final DecimalFormat formatter = new DecimalFormat("#,### đ");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.layoutProductDetail), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        productId = getIntent().getIntExtra("product_id", -1);
        dbHelper = new DatabaseHelper(this);

        tvName = findViewById(R.id.tvDetailName);
        tvCategory = findViewById(R.id.tvDetailCategory);
        tvPrice = findViewById(R.id.tvDetailPrice);
        tvQuantity = findViewById(R.id.tvDetailQuantity);
        tvStockValue = findViewById(R.id.tvDetailStockValue);
        tvStatus = findViewById(R.id.tvDetailStatus);
        tvWarningTag = findViewById(R.id.tvDetailWarningTag);
        tvDescription = findViewById(R.id.tvDetailDescription);

        btnImportOne = findViewById(R.id.btnImportOne);
        btnSellOne = findViewById(R.id.btnSellOne);
        btnBack = findViewById(R.id.btnDetailBack);
        btnDeleteProduct = findViewById(R.id.btnDeleteProduct);

        btnImportOne.setOnClickListener(v -> importOne());
        btnSellOne.setOnClickListener(v -> sellOne());
        btnBack.setOnClickListener(v -> finish());
        btnDeleteProduct.setOnClickListener(v -> confirmDelete());

        loadProductDetails();
    }

    private void loadProductDetails() {
        if (productId == -1) {
            Toast.makeText(this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        currentProduct = dbHelper.getProductById(productId);
        if (currentProduct == null) {
            Toast.makeText(this, "Sản phẩm không tồn tại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvName.setText(currentProduct.getName());
        tvCategory.setText(currentProduct.getCategory());
        tvPrice.setText(formatter.format(currentProduct.getPrice()));
        tvQuantity.setText(String.valueOf(currentProduct.getQuantity()));

        long stockValue = (long) currentProduct.getPrice() * currentProduct.getQuantity();
        tvStockValue.setText(formatter.format(stockValue));

        if (currentProduct.getActive() == 1) {
            tvStatus.setText("Đang kinh doanh");
            tvStatus.setTextColor(ContextCompat.getColor(this, R.color.green_active));
        } else {
            tvStatus.setText("Ngừng kinh doanh");
            tvStatus.setTextColor(ContextCompat.getColor(this, R.color.gray_inactive));
        }

        if (currentProduct.getDescription() != null && !currentProduct.getDescription().trim().isEmpty()) {
            tvDescription.setText(currentProduct.getDescription());
        } else {
            tvDescription.setText("Chưa có mô tả");
        }

        if (currentProduct.isLowStock()) {
            tvWarningTag.setVisibility(View.VISIBLE);
            tvQuantity.setTextColor(ContextCompat.getColor(this, R.color.red_warning));
        } else {
            tvWarningTag.setVisibility(View.GONE);
            tvQuantity.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        }
    }

    private void importOne() {
        if (currentProduct != null) {
            int newQuantity = currentProduct.getQuantity() + 1;
            boolean success = dbHelper.updateQuantity(productId, newQuantity);
            if (success) {
                loadProductDetails();
            } else {
                Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sellOne() {
        if (currentProduct != null) {
            if (currentProduct.getQuantity() <= 0) {
                Toast.makeText(this, getString(R.string.out_of_stock), Toast.LENGTH_SHORT).show();
                return;
            }
            int newQuantity = currentProduct.getQuantity() - 1;
            boolean success = dbHelper.updateQuantity(productId, newQuantity);
            if (success) {
                loadProductDetails();
            } else {
                Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_delete_title)
                .setMessage(R.string.confirm_delete_msg)
                .setPositiveButton("Xóa", (dialog, which) -> {
                    boolean success = dbHelper.deleteProduct(productId);
                    if (success) {
                        Toast.makeText(ProductDetailActivity.this, "Đã xóa sản phẩm", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(ProductDetailActivity.this, "Lỗi khi xóa sản phẩm", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
