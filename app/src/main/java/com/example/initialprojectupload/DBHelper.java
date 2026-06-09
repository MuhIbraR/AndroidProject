package com.example.initialprojectupload;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "kasir.db";
    private static final int DATABASE_VERSION = 5;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "CREATE TABLE kategori (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "nama_kategori TEXT)"
        );

        db.execSQL(
                "CREATE TABLE produk (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "nama TEXT," +
                        "harga INTEGER," +
                        "stok INTEGER," +
                        "kategori_id INTEGER)"
        );

        db.execSQL(
                "CREATE TABLE transaksi (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "tanggal TEXT," +
                        "total INTEGER)"
        );

        db.execSQL(
                "CREATE TABLE detail_transaksi (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "transaksi_id INTEGER," +
                        "nama_produk TEXT," +
                        "harga INTEGER," +
                        "qty INTEGER," +
                        "subtotal INTEGER)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion,
                          int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS detail_transaksi");
        db.execSQL("DROP TABLE IF EXISTS transaksi");
        db.execSQL("DROP TABLE IF EXISTS produk");
        db.execSQL("DROP TABLE IF EXISTS kategori");

        onCreate(db);
    }
}