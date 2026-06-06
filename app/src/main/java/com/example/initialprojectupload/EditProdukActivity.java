package com.example.initialprojectupload;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class EditProdukActivity extends AppCompatActivity {

    Button btnKembali;
    ListView listProduk;

    DBHelper dbHelper;

    ArrayList<String> produkList;
    ArrayList<Integer> produkIdList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_produk);

        btnKembali = findViewById(R.id.btnKembali);
        listProduk = findViewById(R.id.listProduk);

        dbHelper = new DBHelper(this);

        btnKembali.setOnClickListener(v -> finish());

        tampilkanProduk();

        listProduk.setOnItemClickListener((parent, view, position, id) -> {

            tampilkanDialogEdit(position);

        });

    }

    private void tampilkanProduk(){

        produkList = new ArrayList<>();
        produkIdList = new ArrayList<>();

        SQLiteDatabase db =
                dbHelper.getReadableDatabase();

        Cursor cursor =
                db.rawQuery(
                        "SELECT produk.id, " +
                                "produk.nama, " +
                                "kategori.nama_kategori " +
                                "FROM produk " +
                                "LEFT JOIN kategori " +
                                "ON produk.kategori_id = kategori.id " +
                                "ORDER BY kategori.nama_kategori",
                        null
                );

        String kategoriSebelumnya = "";

        while(cursor.moveToNext()){

            String namaProduk =
                    cursor.getString(1);

            String kategori =
                    cursor.getString(2);

            if(kategori == null){
                kategori = "Tanpa Kategori";
            }

            if(!kategori.equals(kategoriSebelumnya)){

                produkList.add(
                        kategori.toUpperCase()
                );

                produkIdList.add(-1);

                kategoriSebelumnya = kategori;
            }

            produkList.add(namaProduk);

            produkIdList.add(
                    cursor.getInt(0)
            );
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_list_item_1,
                        produkList
                );

        listProduk.setAdapter(adapter);

        cursor.close();
    }

    private void tampilkanDialogEdit(int position){

        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);

        View dialogView =
                getLayoutInflater().inflate(
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

        EditText etEditStok =
                dialogView.findViewById(R.id.etEditStok);

        Spinner spEditKategori =
                dialogView.findViewById(R.id.spEditKategori);

        Button btnSimpan =
                dialogView.findViewById(R.id.btnSimpanEdit);

        Button btnHapus =
                dialogView.findViewById(R.id.btnHapus);

        SQLiteDatabase db =
                dbHelper.getReadableDatabase();

        Cursor cursor =
                db.rawQuery(
                        "SELECT * FROM produk WHERE id=?",
                        new String[]{
                                String.valueOf(
                                        produkIdList.get(position)
                                )
                        }
                );

        ArrayList<String> kategoriList =
                new ArrayList<>();

        ArrayList<Integer> kategoriIdList =
                new ArrayList<>();

        Cursor kategoriCursor =
                db.rawQuery(
                        "SELECT * FROM kategori",
                        null
                );

        while(kategoriCursor.moveToNext()){

            kategoriIdList.add(
                    kategoriCursor.getInt(0)
            );

            kategoriList.add(
                    kategoriCursor.getString(1)
            );
        }

        ArrayAdapter<String> kategoriAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        kategoriList
                );

        kategoriAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spEditKategori.setAdapter(
                kategoriAdapter
        );

        kategoriCursor.close();

        if(cursor.moveToFirst()){

            etEditNama.setText(
                    cursor.getString(1)
            );

            etEditHarga.setText(
                    String.valueOf(
                            cursor.getInt(2)
                    )
            );

            etEditStok.setText(
                    String.valueOf(
                            cursor.getInt(3)
                    )
            );

            int kategoriIdProduk =
                    cursor.getInt(4);

            for(int i = 0;
                i < kategoriIdList.size();
                i++){

                if(kategoriIdList.get(i)
                        == kategoriIdProduk){

                    spEditKategori.setSelection(i);
                    break;
                }
            }
        }

        cursor.close();

        btnSimpan.setOnClickListener(v -> {

            SQLiteDatabase dbUpdate =
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

            values.put(
                    "stok",
                    Integer.parseInt(
                            etEditStok.getText().toString()
                    )
            );

            values.put(
                    "kategori_id",
                    kategoriIdList.get(
                            spEditKategori
                                    .getSelectedItemPosition()
                    )
            );

            dbUpdate.update(
                    "produk",
                    values,
                    "id=?",
                    new String[]{
                            String.valueOf(
                                    produkIdList.get(position)
                            )
                    }
            );

            tampilkanProduk();

            dialog.dismiss();

        });

        btnHapus.setOnClickListener(v -> {

            SQLiteDatabase dbDelete =
                    dbHelper.getWritableDatabase();

            dbDelete.delete(
                    "produk",
                    "id=?",
                    new String[]{
                            String.valueOf(
                                    produkIdList.get(position)
                            )
                    }
            );

            tampilkanProduk();

            dialog.dismiss();

        });

        dialog.show();
    }
}