package com.android.factureapp.db.ClientController;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.factureapp.beans.Client;
import com.android.factureapp.beans.Product;
import com.android.factureapp.db.DatabaseHandler;

import java.util.ArrayList;
import java.util.List;

public class TableControllerClient extends DatabaseHandler {
    public TableControllerClient(Context context) {
        super(context);
    }

    public boolean create(Client client) {

        ContentValues values = new ContentValues();

        values.put("name", client.getName());
        values.put("company", client.getCompany());
        values.put("town", client.getTown());
        values.put("email", client.getEmail());
        values.put("address", client.getAddress());
        values.put("phone", client.getPhone());

        SQLiteDatabase db = this.getWritableDatabase();

        boolean createSuccessful = db.insert("clients", null, values) > 0;
        db.close();

        return createSuccessful;
    }


    public List<Client> read() {

        List<Client> recordsList = new ArrayList<Client>();
        String sql = "SELECT * FROM clients ORDER BY id DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                int id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("id")));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String company = cursor.getString(cursor.getColumnIndex("company"));
                String town = cursor.getString(cursor.getColumnIndex("town"));
                String email = cursor.getString(cursor.getColumnIndex("email"));
                String address = cursor.getString(cursor.getColumnIndex("address"));
                String phone = cursor.getString(cursor.getColumnIndex("phone"));

                Client client = new Client();
                client.setId(id) ;
                client.setName(name);
                client.setAddress(address);
                client.setCompany(company);
                client.setEmail(email);
                client.setTown(town);
                client.setPhone(phone);
                recordsList.add(client);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return recordsList;
    }


}
