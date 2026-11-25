package com.example.baithuchanh3;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class AddProductActivity extends AppCompatActivity {

    private EditText edtName;
    private EditText edtPrice;
    private EditText edtQuantity;
    private Spinner spCategory;
    private SwitchMaterial swActive;
    private EditText edtDescription;
    private Button btnSaveProduct;

    private DatabaseHelper dbHelper;

    private final String[] categories = {"Điện tử", "Thực phẩm", "Văn phòng phẩm", "Gia dụng", "Khác"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_product);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.layoutAddProduct), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DatabaseHelper(this);

        edtName = findViewById(R.id.edtAddName);
        edtPrice = findViewById(R.id.edtAddPrice);
        edtQuantity = findViewById(R.id.edtAddQuantity);
        spCategory = findViewById(R.id.spAddCategory);
        swActive = findViewById(R.id.swAddActive);
        edtDescription = findViewById(R.id.edtAddDescription);
        btnSaveProduct = findViewById(R.id.btnSaveProduct);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(adapter);

        btnSaveProduct.setOnClickListener(v -> saveProduct());
    }

    private void saveProduct() {
        String name = edtName.getText().toString().trim();
        String priceStr = edtPrice.getText().toString().trim();
        String quantityStr = edtQuantity.getText().toString().trim();
        String category = spCategory.getSelectedItem().toString();
        boolean active = swActive.isChecked();
        String description = edtDescription.getText().toString().trim();

        if (name.isEmpty()) {
            edtName.setError("Tên sản phẩm không được để trống");
            edtName.requestFocus();
            return;
        }

        if (priceStr.isEmpty()) {
            edtPrice.setError("Vui lòng nhập giá bán");
            edtPrice.requestFocus();
            return;
        }

        int price;
        try {
            price = Integer.parseInt(priceStr);
            if (price <= 0) {
                edtPrice.setError("Giá bán phải lớn hơn 0");
                edtPrice.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            edtPrice.setError("Giá bán không hợp lệ");
            edtPrice.requestFocus();
            return;
        }

        if (quantityStr.isEmpty()) {
            edtQuantity.setError("Vui lòng nhập số lượng");
            edtQuantity.requestFocus();
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
            if (quantity < 0) {
                edtQuantity.setError("Số lượng không được âm");
                edtQuantity.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            edtQuantity.setError("Số lượng không hợp lệ");
            edtQuantity.requestFocus();
            return;
        }

        Product product = new Product(name, price, quantity, category, active ? 1 : 0, description);
        long result = dbHelper.insertProduct(product);

        if (result > 0) {
            Toast.makeText(this, "Thêm sản phẩm thành công", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Lỗi khi lưu sản phẩm vào cơ sở dữ liệu", Toast.LENGTH_SHORT).show();
        }
    }
}
