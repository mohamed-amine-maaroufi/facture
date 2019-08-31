package com.android.factureapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    protected static final String DATABASE_NAME = "facture_db";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "CREATE TABLE products " +
                "( id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "reference TEXT, " +
                "quantity TEXT, " +
                "unitPrice TEXT ) ";

        String sql2 = "CREATE TABLE clients " +
                "( id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "company TEXT, " +
                "town TEXT, " +
                "email TEXT, " +
                "address TEXT, " +
                "phone TEXT ) ";

        db.execSQL(sql);
        db.execSQL(sql2);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String sql = "DROP TABLE IF EXISTS products";
        String sql2 = "DROP TABLE IF EXISTS clients";
        db.execSQL(sql);
        db.execSQL(sql2);

        onCreate(db);
    }

}