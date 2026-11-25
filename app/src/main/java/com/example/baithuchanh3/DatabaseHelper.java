package com.example.baithuchanh3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "warehouse.db";
    private static final int DATABASE_VERSION = 1;

    // Table users
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USER_USERNAME = "username";
    public static final String COLUMN_USER_PASSWORD = "password";

    // Table products
    public static final String TABLE_PRODUCTS = "products";
    public static final String COLUMN_PROD_ID = "id";
    public static final String COLUMN_PROD_NAME = "name";
    public static final String COLUMN_PROD_PRICE = "price";
    public static final String COLUMN_PROD_QUANTITY = "quantity";
    public static final String COLUMN_PROD_CATEGORY = "category";
    public static final String COLUMN_PROD_ACTIVE = "active";
    public static final String COLUMN_PROD_DESCRIPTION = "description";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_USERNAME + " TEXT NOT NULL UNIQUE, " +
                COLUMN_USER_PASSWORD + " TEXT NOT NULL" +
                ")";

        String createProductsTable = "CREATE TABLE " + TABLE_PRODUCTS + " (" +
                COLUMN_PROD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PROD_NAME + " TEXT NOT NULL, " +
                COLUMN_PROD_PRICE + " INTEGER NOT NULL, " +
                COLUMN_PROD_QUANTITY + " INTEGER NOT NULL, " +
                COLUMN_PROD_CATEGORY + " TEXT NOT NULL, " +
                COLUMN_PROD_ACTIVE + " INTEGER NOT NULL, " +
                COLUMN_PROD_DESCRIPTION + " TEXT" +
                ")";

        db.execSQL(createUsersTable);
        db.execSQL(createProductsTable);

        // Seed user
        ContentValues userValues = new ContentValues();
        userValues.put(COLUMN_USER_USERNAME, "admin");
        userValues.put(COLUMN_USER_PASSWORD, "123456");
        db.insert(TABLE_USERS, null, userValues);

        // Seed products
        insertSampleProduct(db, "Chuột không dây", 150000, 20, "Điện tử", 1, "Chuột máy tính không dây");
        insertSampleProduct(db, "Bàn phím cơ", 450000, 3, "Điện tử", 1, "Bàn phím cơ gaming RGB");
        insertSampleProduct(db, "Sổ tay A5", 25000, 40, "Văn phòng phẩm", 1, "Sổ tay bìa da A5");
    }

    private void insertSampleProduct(SQLiteDatabase db, String name, int price, int quantity, String category, int active, String description) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROD_NAME, name);
        values.put(COLUMN_PROD_PRICE, price);
        values.put(COLUMN_PROD_QUANTITY, quantity);
        values.put(COLUMN_PROD_CATEGORY, category);
        values.put(COLUMN_PROD_ACTIVE, active);
        values.put(COLUMN_PROD_DESCRIPTION, description);
        db.insert(TABLE_PRODUCTS, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }

    // 1. checkLogin(username, password)
    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        boolean exists = false;
        try {
            String query = "SELECT 1 FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_USERNAME + " = ? AND " + COLUMN_USER_PASSWORD + " = ?";
            cursor = db.rawQuery(query, new String[]{username, password});
            if (cursor != null && cursor.moveToFirst()) {
                exists = true;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return exists;
    }

    // 2. insertUser(username, password)
    public long insertUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_USERNAME, username);
        values.put(COLUMN_USER_PASSWORD, password);
        return db.insert(TABLE_USERS, null, values);
    }

    // 3. insertProduct(Product p)
    public long insertProduct(Product p) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROD_NAME, p.getName());
        values.put(COLUMN_PROD_PRICE, p.getPrice());
        values.put(COLUMN_PROD_QUANTITY, p.getQuantity());
        values.put(COLUMN_PROD_CATEGORY, p.getCategory());
        values.put(COLUMN_PROD_ACTIVE, p.getActive());
        values.put(COLUMN_PROD_DESCRIPTION, p.getDescription());
        return db.insert(TABLE_PRODUCTS, null, values);
    }

    // 4. getAllProducts()
    public ArrayList<Product> getAllProducts() {
        ArrayList<Product> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String query = "SELECT * FROM " + TABLE_PRODUCTS + " ORDER BY " + COLUMN_PROD_ID + " DESC";
            cursor = db.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PROD_ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROD_NAME));
                    int price = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PROD_PRICE));
                    int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PROD_QUANTITY));
                    String category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROD_CATEGORY));
                    int active = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PROD_ACTIVE));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROD_DESCRIPTION));

                    list.add(new Product(id, name, price, quantity, category, active, description));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    // 5. getProductById(int id)
    public Product getProductById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        Product product = null;
        try {
            String query = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_PROD_ID + " = ?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(id)});
            if (cursor != null && cursor.moveToFirst()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROD_NAME));
                int price = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PROD_PRICE));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PROD_QUANTITY));
                String category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROD_CATEGORY));
                int active = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PROD_ACTIVE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROD_DESCRIPTION));

                product = new Product(id, name, price, quantity, category, active, description);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return product;
    }

    // 6. updateQuantity(int id, int quantity)
    public boolean updateQuantity(int id, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROD_QUANTITY, quantity);
        int rows = db.update(TABLE_PRODUCTS, values, COLUMN_PROD_ID + " = ?", new String[]{String.valueOf(id)});
        return rows > 0;
    }

    // 7. deleteProduct(int id)
    public boolean deleteProduct(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_PRODUCTS, COLUMN_PROD_ID + " = ?", new String[]{String.valueOf(id)});
        return rows > 0;
    }

    // 8. getTotalValue() : SUM(price * quantity)
    public long getTotalValue() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        long total = 0;
        try {
            String query = "SELECT SUM(" + COLUMN_PROD_PRICE + " * " + COLUMN_PROD_QUANTITY + ") FROM " + TABLE_PRODUCTS;
            cursor = db.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                total = cursor.getLong(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return total;
    }

    // 9. countLowStock() : COUNT với quantity < 5
    public int countLowStock() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        int count = 0;
        try {
            String query = "SELECT COUNT(*) FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_PROD_QUANTITY + " < 5";
            cursor = db.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return count;
    }
}
