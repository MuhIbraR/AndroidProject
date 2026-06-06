package com.example.initialprojectupload;

import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public class ProdukActivity extends AppCompatActivity {

    Button btnDashboard;
    Button btnKategori;
    Button btnTambahProduk;
    Button btnEditProduk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produk);

        btnDashboard = findViewById(R.id.btnDashboard);
        btnKategori = findViewById(R.id.btnKategori);
        btnTambahProduk = findViewById(R.id.btnTambahProduk);
        btnEditProduk = findViewById(R.id.btnEditProduk);

        btnDashboard.setOnClickListener(v -> {
            finish();
        });

        btnKategori.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            ProdukActivity.this,
                            KategoriActivity.class
                    );

            startActivity(intent);

        });

        btnTambahProduk.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            ProdukActivity.this,
                            TambahProdukActivity.class
                    );

            startActivity(intent);

        });

        btnEditProduk.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            ProdukActivity.this,
                            EditProdukActivity.class
                    );

            startActivity(intent);

        });
    }
}