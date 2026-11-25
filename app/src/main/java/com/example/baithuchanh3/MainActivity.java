package com.example.baithuchanh3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView tvTotalProducts;
    private TextView tvTotalValue;
    private TextView tvLowStockCount;
    private ListView lvProducts;
    private TextView tvEmptyList;
    private Button btnAddProduct;

    private DatabaseHelper dbHelper;
    private ProductAdapter adapter;
    private ArrayList<Product> productList;
    private final DecimalFormat formatter = new DecimalFormat("#,### đ");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DatabaseHelper(this);

        tvTotalProducts = findViewById(R.id.tvTotalProducts);
        tvTotalValue = findViewById(R.id.tvTotalValue);
        tvLowStockCount = findViewById(R.id.tvLowStockCount);
        lvProducts = findViewById(R.id.lvProducts);
        tvEmptyList = findViewById(R.id.tvEmptyList);
        btnAddProduct = findViewById(R.id.btnAddProduct);

        productList = new ArrayList<>();
        adapter = new ProductAdapter(this, R.layout.item_product, productList);
        lvProducts.setAdapter(adapter);

        lvProducts.setOnItemClickListener((parent, view, position, id) -> {
            Product product = productList.get(position);
            Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
            intent.putExtra("product_id", product.getId());
            startActivity(intent);
        });

        btnAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddProductActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        productList.clear();
        ArrayList<Product> productsFromDb = dbHelper.getAllProducts();
        if (productsFromDb != null) {
            productList.addAll(productsFromDb);
        }
        adapter.notifyDataSetChanged();

        if (productList.isEmpty()) {
            tvEmptyList.setVisibility(View.VISIBLE);
            lvProducts.setVisibility(View.GONE);
        } else {
            tvEmptyList.setVisibility(View.GONE);
            lvProducts.setVisibility(View.VISIBLE);
        }

        // Update Statistics block
        tvTotalProducts.setText(String.valueOf(productList.size()));
        tvTotalValue.setText(formatter.format(dbHelper.getTotalValue()));
        tvLowStockCount.setText(String.valueOf(dbHelper.countLowStock()));
    }
}
