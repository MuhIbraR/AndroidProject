package com.example.initialprojectupload;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class TambahProdukActivity extends AppCompatActivity {

    EditText etNama, etHarga, etStok;
    Spinner spKategori;
    Button btnSimpanProduk, btnKembali;

    DBHelper dbHelper;

    ArrayList<String> kategoriList;
    ArrayList<Integer> kategoriIdList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_produk);

        etNama = findViewById(R.id.etNama);
        etHarga = findViewById(R.id.etHarga);
        etStok = findViewById(R.id.etStok);

        spKategori = findViewById(R.id.spKategori);

        btnSimpanProduk =
                findViewById(R.id.btnSimpanProduk);

        btnKembali =
                findViewById(R.id.btnKembali);

        dbHelper = new DBHelper(this);

        loadKategori();

        btnKembali.setOnClickListener(v -> finish());

        btnSimpanProduk.setOnClickListener(v -> {

            String nama =
                    etNama.getText().toString();

            String harga =
                    etHarga.getText().toString();

            String stok =
                    etStok.getText().toString();

            if(nama.isEmpty() ||
                    harga.isEmpty() ||
                    stok.isEmpty()){

                Toast.makeText(
                        this,
                        "Lengkapi data",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            int kategoriId =
                    kategoriIdList.get(
                            spKategori
                                    .getSelectedItemPosition()
                    );

            SQLiteDatabase db =
                    dbHelper.getWritableDatabase();

            ContentValues values =
                    new ContentValues();

            values.put("nama", nama);
            values.put("harga",
                    Integer.parseInt(harga));

            values.put("stok",
                    Integer.parseInt(stok));

            values.put(
                    "kategori_id",
                    kategoriId
            );

            db.insert(
                    "produk",
                    null,
                    values
            );

            Toast.makeText(
                    this,
                    "Produk berhasil ditambahkan",
                    Toast.LENGTH_SHORT
            ).show();

            etNama.setText("");
            etHarga.setText("");
            etStok.setText("");

        });

    }

    private void loadKategori(){

        kategoriList =
                new ArrayList<>();

        kategoriIdList =
                new ArrayList<>();

        SQLiteDatabase db =
                dbHelper.getReadableDatabase();

        Cursor cursor =
                db.rawQuery(
                        "SELECT * FROM kategori",
                        null
                );

        while(cursor.moveToNext()){

            kategoriIdList.add(
                    cursor.getInt(0)
            );

            kategoriList.add(
                    cursor.getString(1)
            );
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        kategoriList
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spKategori.setAdapter(adapter);

        cursor.close();
    }
}