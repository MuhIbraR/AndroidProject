package com.example.initialprojectupload;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class KategoriActivity extends AppCompatActivity {

    EditText etKategori;
    Button btnTambahKategori;
    Button btnKembali;
    ListView listKategori;

    DBHelper dbHelper;

    ArrayList<String> dataKategori;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kategori);

        etKategori = findViewById(R.id.etKategori);
        btnTambahKategori = findViewById(R.id.btnTambahKategori);
        btnKembali = findViewById(R.id.btnKembali);
        listKategori = findViewById(R.id.listKategori);

        dbHelper = new DBHelper(this);

        tampilkanKategori();

        btnKembali.setOnClickListener(v -> finish());

        btnTambahKategori.setOnClickListener(v -> {

            String kategori =
                    etKategori.getText().toString();

            if(kategori.isEmpty()){

                Toast.makeText(
                        this,
                        "Isi kategori dulu",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            SQLiteDatabase db =
                    dbHelper.getWritableDatabase();

            ContentValues values =
                    new ContentValues();

            values.put(
                    "nama_kategori",
                    kategori
            );

            db.insert(
                    "kategori",
                    null,
                    values
            );

            etKategori.setText("");

            tampilkanKategori();

            Toast.makeText(
                    this,
                    "Kategori ditambahkan",
                    Toast.LENGTH_SHORT
            ).show();

        });

    }

    private void tampilkanKategori(){

        dataKategori = new ArrayList<>();

        SQLiteDatabase db =
                dbHelper.getReadableDatabase();

        Cursor cursor =
                db.rawQuery(
                        "SELECT * FROM kategori",
                        null
                );

        while(cursor.moveToNext()){

            dataKategori.add(
                    cursor.getString(1)
            );
        }

        adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_list_item_1,
                        dataKategori
                );

        listKategori.setAdapter(adapter);

        cursor.close();
    }
}