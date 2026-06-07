package com.example.initialprojectupload;
import android.widget.TextView;
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

    ListView listKategori;
    ListView listProdukKasir;
    ListView listKeranjang;
    TextView tvTotal;
    Button btnBayar;

    DBHelper dbHelper;

    ArrayList<String> dataProduk;
    ArrayList<KeranjangItem> dataKeranjang;
    ArrayList<String> dataKategori;

    ArrayAdapter<String> adapterProduk;
    ArrayAdapter<String> adapterKategori;
    KeranjangAdapter adapterKeranjang;
    ArrayList<Integer> kategoriIdList;
    ArrayList<Integer> produkIdList;
    ArrayList<Integer> hargaProdukList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kasir);

        listKategori = findViewById(R.id.listKategori);
        listProdukKasir = findViewById(R.id.listProdukKasir);
        listKeranjang = findViewById(R.id.listKeranjang);
        tvTotal = findViewById(R.id.tvTotal);
        btnBayar = findViewById(R.id.btnBayar);
        dbHelper = new DBHelper(this);
        dataKeranjang = new ArrayList<>();
        produkIdList = new ArrayList<>();
        hargaProdukList = new ArrayList<>();
        tampilkanKategori();
        listKategori.setOnItemClickListener((parent, view, position, id) -> {

            tampilkanProduk(
                    kategoriIdList.get(position)
            );

        });
        listProdukKasir.setOnItemClickListener((parent, view, position, id) -> {

                    KeranjangItem item =
                            new KeranjangItem(
                                    dataProduk.get(position),
                                    hargaProdukList.get(position),
                                    1
                            );

                    dataKeranjang.add(item);

                    adapterKeranjang =
                            new KeranjangAdapter(
                                    Kasir.this,
                                    dataKeranjang
                            );

                    listKeranjang.setAdapter(
                            adapterKeranjang
                    );
                });

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
        tampilkanKategori();
    }

    private void tampilkanKategori() {

        dataKategori = new ArrayList<>();
        kategoriIdList = new ArrayList<>();

        SQLiteDatabase db =
                dbHelper.getReadableDatabase();

        Cursor cursor =
                db.rawQuery(
                        "SELECT * FROM kategori",
                        null
                );

        while (cursor.moveToNext()) {

            kategoriIdList.add(
                    cursor.getInt(0)
            );

            dataKategori.add(
                    cursor.getString(1)
            );
        }

        adapterKategori =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_list_item_1,
                        dataKategori
                );

        listKategori.setAdapter(
                adapterKategori
        );

        cursor.close();
    }

    private void tampilkanProduk(int kategoriId) {

        dataProduk = new ArrayList<>();
        produkIdList = new ArrayList<>();
        hargaProdukList = new ArrayList<>();

        SQLiteDatabase db =
                dbHelper.getReadableDatabase();

        Cursor cursor =
                db.rawQuery(
                        "SELECT * FROM produk WHERE kategori_id=?",
                        new String[]{
                                String.valueOf(kategoriId)
                        }
                );

        while (cursor.moveToNext()) {

            produkIdList.add(
                    cursor.getInt(0)
            );

            hargaProdukList.add(
                    cursor.getInt(2)
            );

            dataProduk.add(
                    cursor.getString(1)
                            + " - Rp "
                            + cursor.getInt(2)
                            + " | Stok "
                            + cursor.getInt(3)
            );
        }
        adapterProduk =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_list_item_1,
                        dataProduk
                );

        listProdukKasir.setAdapter(
                adapterProduk
        );

        cursor.close();
    }
}