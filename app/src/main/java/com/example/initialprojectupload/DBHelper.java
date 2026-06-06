package com.example.initialprojectupload;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "kasir.db";
    private static final int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "CREATE TABLE produk (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "nama TEXT," +
                        "harga INTEGER," +
                        "stok INTEGER)"
        );

        db.execSQL(
                "CREATE TABLE transaksi (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "tanggal TEXT," +
                        "produk TEXT," +
                        "jumlah INTEGER," +
                        "harga INTEGER," +
                        "total INTEGER)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS produk");
        db.execSQL("DROP TABLE IF EXISTS transaksi");
        onCreate(db);
    }
}