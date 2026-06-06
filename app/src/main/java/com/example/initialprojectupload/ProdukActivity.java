package com.example.initialprojectupload;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
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

public class ProdukActivity extends AppCompatActivity {

    EditText etNama, etHarga;
    Button btnTambah;
    ListView listProduk;

    DBHelper dbHelper;

    ArrayList<String> dataProduk;
    ArrayAdapter<String> adapter;

    ArrayList<Integer> idProduk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produk);

        etNama = findViewById(R.id.etNama);
        etHarga = findViewById(R.id.etHarga);
        btnTambah = findViewById(R.id.btnTambah);
        listProduk = findViewById(R.id.listProduk);

        dbHelper = new DBHelper(this);

        tampilkanProduk();

        btnTambah.setOnClickListener(v -> {

            String nama = etNama.getText().toString();
            String harga = etHarga.getText().toString();

            if(nama.isEmpty() || harga.isEmpty()){

                Toast.makeText(
                        this,
                        "Isi semua data",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            SQLiteDatabase db =
                    dbHelper.getWritableDatabase();

            ContentValues values =
                    new ContentValues();

            values.put("nama", nama);
            values.put("harga",
                    Integer.parseInt(harga));

            db.insert(
                    "produk",
                    null,
                    values
            );

            etNama.setText("");
            etHarga.setText("");

            tampilkanProduk();

            Toast.makeText(
                    this,
                    "Produk ditambahkan",
                    Toast.LENGTH_SHORT
            ).show();

        });

        listProduk.setOnItemClickListener((parent, view, position, id) -> {

            tampilkanDialogEdit(position);

        });

    }

    private void tampilkanDialogEdit(int position){

        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);

        View dialogView =
                LayoutInflater.from(this)
                        .inflate(
                                R.layout.dialog_edit_produk,
                                null
                        );

        builder.setView(dialogView);

        AlertDialog dialog =
                builder.create();

        EditText etEditNama =
                dialogView.findViewById(R.id.etEditNama);

        EditText etEditHarga =
                dialogView.findViewById(R.id.etEditHarga);

        Button btnSimpan =
                dialogView.findViewById(R.id.btnSimpanEdit);

        Button btnHapus =
                dialogView.findViewById(R.id.btnHapus);

        String data =
                dataProduk.get(position);

        String[] split =
                data.split(" - Rp ");

        etEditNama.setText(split[0]);
        etEditHarga.setText(split[1]);

        btnSimpan.setOnClickListener(v -> {

            SQLiteDatabase db =
                    dbHelper.getWritableDatabase();

            ContentValues values =
                    new ContentValues();

            values.put(
                    "nama",
                    etEditNama.getText().toString()
            );

            values.put(
                    "harga",
                    Integer.parseInt(
                            etEditHarga.getText().toString()
                    )
            );

            db.update(
                    "produk",
                    values,
                    "id=?",
                    new String[]{
                            String.valueOf(
                                    idProduk.get(position)
                            )
                    }
            );

            tampilkanProduk();

            dialog.dismiss();

            Toast.makeText(
                    this,
                    "Produk diperbarui",
                    Toast.LENGTH_SHORT
            ).show();

        });

        btnHapus.setOnClickListener(v -> {

            SQLiteDatabase db =
                    dbHelper.getWritableDatabase();

            db.delete(
                    "produk",
                    "id=?",
                    new String[]{
                            String.valueOf(
                                    idProduk.get(position)
                            )
                    }
            );

            tampilkanProduk();

            dialog.dismiss();

            Toast.makeText(
                    this,
                    "Produk dihapus",
                    Toast.LENGTH_SHORT
            ).show();

        });

        dialog.show();
    }

    private void tampilkanProduk(){

        dataProduk = new ArrayList<>();
        idProduk = new ArrayList<>();

        SQLiteDatabase db =
                dbHelper.getReadableDatabase();

        Cursor cursor =
                db.rawQuery(
                        "SELECT * FROM produk",
                        null
                );

        while(cursor.moveToNext()){

            idProduk.add(
                    cursor.getInt(0)
            );

            dataProduk.add(
                    cursor.getString(1)
                            + " - Rp "
                            + cursor.getInt(2)
            );
        }

        adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_list_item_1,
                        dataProduk
                );

        listProduk.setAdapter(adapter);

        cursor.close();
    }
}