package com.android.factureapp.db.ProductController;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.factureapp.beans.Product;
import com.android.factureapp.db.DatabaseHandler;

import java.util.ArrayList;
import java.util.List;

public class TableControllerProduct extends DatabaseHandler {
    public TableControllerProduct(Context context) {
        super(context);
    }

    public boolean create(Product product) {

        ContentValues values = new ContentValues();

        values.put("name", product.getName());
        values.put("reference", product.getReference());
        values.put("quantity", product.getQuantity());
        values.put("unitPrice", product.getUnitPrice());

        SQLiteDatabase db = this.getWritableDatabase();

        boolean createSuccessful = db.insert("products", null, values) > 0;
        db.close();

        return createSuccessful;
    }

    //return number of products
    public int count() {

        SQLiteDatabase db = this.getWritableDatabase();

        String sql = "SELECT * FROM products";
        int recordCount = db.rawQuery(sql, null).getCount();
        db.close();

        return recordCount;
    }



    public List<Product> read() {

        List<Product> recordsList = new ArrayList<Product>();

        String sql = "SELECT * FROM products ORDER BY id DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {

                int id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("id")));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String reference = cursor.getString(cursor.getColumnIndex("reference"));
                String quantity = cursor.getString(cursor.getColumnIndex("quantity"));
                String unitPrice = cursor.getString(cursor.getColumnIndex("unitPrice"));

                Product product = new Product();
                product.setId(id) ;
                product.setName(name);
                product.setReference(reference);
                product.setQuantity(quantity);
                product.setUnitPrice(unitPrice);

                recordsList.add(product);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return recordsList;
    }


    //delete product
    public boolean delete(int id) {
        boolean deleteSuccessful = false;

        SQLiteDatabase db = this.getWritableDatabase();
        deleteSuccessful = db.delete("products", "id ='" + id + "'", null) > 0;
        db.close();

        return deleteSuccessful;

    }

    //delete product
    public void deleteAll() {


        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + "products");
        db.close();
    }
}
