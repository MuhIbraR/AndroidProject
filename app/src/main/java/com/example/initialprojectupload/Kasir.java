package com.example.initialprojectupload;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import android.content.Intent;
import android.widget.Button;

public class Kasir extends AppCompatActivity {

    ListView listProdukKasir;
    DBHelper dbHelper;

    ArrayList<String> dataProduk;
    ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kasir);

        listProdukKasir = findViewById(R.id.listProdukKasir);

        dbHelper = new DBHelper(this);

        tampilkanProduk();
        Button btnLogout;
        btnLogout = findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(v -> {

            Intent intent = new Intent(Kasir.this, Home.class);

            startActivity(intent);

            finish();

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        tampilkanProduk();
    }

    private void tampilkanProduk() {

        dataProduk = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM produk",
                null
        );

        while(cursor.moveToNext()) {

            dataProduk.add(
                    cursor.getString(1)
                            + " - Rp "
                            + cursor.getInt(2)
            );
        }

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                dataProduk
        );

        listProdukKasir.setAdapter(adapter);

        cursor.close();
    }
}