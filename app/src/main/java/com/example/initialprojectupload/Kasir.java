package com.example.initialprojectupload;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Kasir extends AppCompatActivity
        implements KeranjangAdapter.OnCartChangedListener {

    ListView listKategori;
    GridView listProdukKasir;
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

        listKategori    = findViewById(R.id.listKategori);
        listProdukKasir = findViewById(R.id.listProdukKasir);
        listKeranjang   = findViewById(R.id.listKeranjang);
        tvTotal         = findViewById(R.id.tvTotal);
        btnBayar        = findViewById(R.id.btnBayar);

        dbHelper      = new DBHelper(this);
        dataKeranjang = new ArrayList<>();
        produkIdList  = new ArrayList<>();
        hargaProdukList = new ArrayList<>();

        tampilkanKategori();

        listKategori.setOnItemClickListener((parent, view, position, id) -> {
            tampilkanProduk(kategoriIdList.get(position));
        });

        listProdukKasir.setOnItemClickListener((parent, view, position, id) -> {

            // Cek apakah produk sudah ada di keranjang
            String namaProduk = dataProduk.get(position)
                    .split(" - Rp ")[0]; // ambil nama saja

            boolean sudahAda = false;
            for (KeranjangItem existing : dataKeranjang) {
                if (existing.namaProduk.equals(namaProduk)) {
                    existing.qty++;
                    sudahAda = true;
                    break;
                }
            }

            if (!sudahAda) {
                KeranjangItem item = new KeranjangItem(
                        namaProduk,
                        hargaProdukList.get(position),
                        1
                );
                dataKeranjang.add(item);
            }

            adapterKeranjang = new KeranjangAdapter(
                    Kasir.this,
                    dataKeranjang,
                    this  // listener
            );

            listKeranjang.setAdapter(adapterKeranjang);

            hitungTotal();
        });

        btnBayar.setOnClickListener(v -> prosesCheckout());

        Button btnLogout = findViewById(R.id.btnLogout);
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

    // ─── Callback dari KeranjangAdapter ────────────────────────────
    @Override
    public void onCartChanged() {
        hitungTotal();
    }

    // ─── Hitung total semua item di keranjang ───────────────────────
    private void hitungTotal() {
        int total = 0;
        for (KeranjangItem item : dataKeranjang) {
            total += item.harga * item.qty;
        }
        tvTotal.setText("Total : Rp " + String.format(Locale.getDefault(), "%,d", total));
    }

    // ─── Proses checkout ────────────────────────────────────────────
    private void prosesCheckout() {

        if (dataKeranjang.isEmpty()) {
            Toast.makeText(this,
                    "Keranjang masih kosong!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Hitung total
        int total = 0;
        for (KeranjangItem item : dataKeranjang) {
            total += item.harga * item.qty;
        }

        // Buat ringkasan untuk dialog
        StringBuilder ringkasan = new StringBuilder();
        for (KeranjangItem item : dataKeranjang) {
            ringkasan.append("• ").append(item.namaProduk)
                    .append(" x").append(item.qty)
                    .append(" = Rp ").append(
                            String.format(Locale.getDefault(), "%,d", item.harga * item.qty))
                    .append("\n");
        }
        ringkasan.append("\nTotal: Rp ")
                .append(String.format(Locale.getDefault(), "%,d", total));

        int finalTotal = total;
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi Pembayaran")
                .setMessage(ringkasan.toString())
                .setPositiveButton("Bayar", (dialog, which) -> {
                    simpanTransaksi(finalTotal);
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    // ─── Simpan transaksi ke database ───────────────────────────────
    private void simpanTransaksi(int total) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // 1. Simpan header transaksi
        String tanggal = new SimpleDateFormat(
                "dd/MM/yyyy HH:mm", Locale.getDefault()
        ).format(new Date());

        ContentValues cvTransaksi = new ContentValues();
        cvTransaksi.put("tanggal", tanggal);
        cvTransaksi.put("total", total);

        long transaksiId = db.insert("transaksi", null, cvTransaksi);

        // 2. Simpan detail tiap item & kurangi stok
        for (KeranjangItem item : dataKeranjang) {

            ContentValues cvDetail = new ContentValues();
            cvDetail.put("transaksi_id", transaksiId);
            cvDetail.put("nama_produk",  item.namaProduk);
            cvDetail.put("harga",        item.harga);
            cvDetail.put("qty",          item.qty);
            cvDetail.put("subtotal",     item.harga * item.qty);

            db.insert("detail_transaksi", null, cvDetail);

            // Kurangi stok produk
            db.execSQL(
                    "UPDATE produk SET stok = stok - ? WHERE nama = ?",
                    new Object[]{ item.qty, item.namaProduk }
            );
        }

        // 3. Reset keranjang
        dataKeranjang.clear();

        if (adapterKeranjang != null) {
            adapterKeranjang.notifyDataSetChanged();
        }

        tvTotal.setText("Total : Rp 0");

        Toast.makeText(this,
                "Transaksi berhasil disimpan!",
                Toast.LENGTH_SHORT).show();
    }

    // ─── Load kategori ──────────────────────────────────────────────
    private void tampilkanKategori() {

        dataKategori  = new ArrayList<>();
        kategoriIdList = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM kategori", null);

        while (cursor.moveToNext()) {
            kategoriIdList.add(cursor.getInt(0));
            dataKategori.add(cursor.getString(1));
        }

        adapterKategori = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                dataKategori
        );

        listKategori.setAdapter(adapterKategori);

        cursor.close();
    }

    // ─── Load produk berdasarkan kategori ───────────────────────────
    private void tampilkanProduk(int kategoriId) {

        dataProduk      = new ArrayList<>();
        produkIdList    = new ArrayList<>();
        hargaProdukList = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM produk WHERE kategori_id=?",
                new String[]{ String.valueOf(kategoriId) }
        );

        while (cursor.moveToNext()) {
            produkIdList.add(cursor.getInt(0));
            hargaProdukList.add(cursor.getInt(2));
            dataProduk.add(
                    cursor.getString(1)
                            + " - Rp " + cursor.getInt(2)
                            + " | Stok " + cursor.getInt(3)
            );
        }

        adapterProduk = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                dataProduk
        );

        listProdukKasir.setAdapter(adapterProduk);

        cursor.close();
    }
}