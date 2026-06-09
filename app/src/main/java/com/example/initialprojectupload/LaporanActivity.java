package com.example.initialprojectupload;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

public class LaporanActivity extends AppCompatActivity {

    ListView listLaporan;
    TextView tvTotalPendapatan;
    TextView tvJumlahTransaksi;
    TextView tvEmptyLaporan;
    Button   btnKembaliLaporan;

    DBHelper dbHelper;

    // Data transaksi
    ArrayList<Integer> transaksiIdList;
    ArrayList<String>  tanggalList;
    ArrayList<Integer> totalList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan);

        listLaporan         = findViewById(R.id.listLaporan);
        tvTotalPendapatan   = findViewById(R.id.tvTotalPendapatan);
        tvJumlahTransaksi   = findViewById(R.id.tvJumlahTransaksi);
        tvEmptyLaporan      = findViewById(R.id.tvEmptyLaporan);
        btnKembaliLaporan   = findViewById(R.id.btnKembaliLaporan);

        dbHelper = new DBHelper(this);

        btnKembaliLaporan.setOnClickListener(v -> finish());

        loadLaporan();

        listLaporan.setOnItemClickListener((parent, view, position, id) -> {
            tampilkanDetailTransaksi(
                    transaksiIdList.get(position),
                    tanggalList.get(position),
                    totalList.get(position)
            );
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLaporan();
    }

    // ─── Load semua transaksi dari DB ───────────────────────────────
    private void loadLaporan() {

        transaksiIdList = new ArrayList<>();
        tanggalList     = new ArrayList<>();
        totalList       = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM transaksi ORDER BY id DESC",
                null
        );

        int grandTotal = 0;

        while (cursor.moveToNext()) {
            transaksiIdList.add(cursor.getInt(0));
            tanggalList.add(cursor.getString(1));
            totalList.add(cursor.getInt(2));
            grandTotal += cursor.getInt(2);
        }
        cursor.close();

        // Update ringkasan
        tvTotalPendapatan.setText(
                "Rp " + String.format(Locale.getDefault(), "%,d", grandTotal)
        );
        tvJumlahTransaksi.setText(
                transaksiIdList.size() + " transaksi"
        );

        // Tampil/sembunyi empty state
        if (transaksiIdList.isEmpty()) {
            listLaporan.setVisibility(View.GONE);
            tvEmptyLaporan.setVisibility(View.VISIBLE);
        } else {
            listLaporan.setVisibility(View.VISIBLE);
            tvEmptyLaporan.setVisibility(View.GONE);
        }

        // Custom adapter untuk item laporan
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                R.layout.item_laporan,
                tanggalList
        ) {
            @Override
            public View getView(int pos, View convertView, ViewGroup parent) {
                View v = getLayoutInflater().inflate(R.layout.item_laporan, parent, false);

                TextView tvTanggal = v.findViewById(R.id.tvTanggalLaporan);
                TextView tvTotal   = v.findViewById(R.id.tvTotalLaporan);

                tvTanggal.setText("Transaksi #" + transaksiIdList.get(pos)
                        + "  |  " + tanggalList.get(pos));
                tvTotal.setText("Rp " + String.format(
                        Locale.getDefault(), "%,d", totalList.get(pos)));

                return v;
            }
        };

        listLaporan.setAdapter(adapter);
    }

    // ─── Tampilkan detail item dalam satu transaksi ─────────────────
    private void tampilkanDetailTransaksi(
            int transaksiId,
            String tanggal,
            int total
    ) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT nama_produk, harga, qty, subtotal " +
                        "FROM detail_transaksi WHERE transaksi_id = ?",
                new String[]{ String.valueOf(transaksiId) }
        );

        StringBuilder detail = new StringBuilder();
        detail.append("Tanggal : ").append(tanggal).append("\n\n");
        detail.append("Item Pembelian:\n");
        detail.append("─────────────────────\n");

        while (cursor.moveToNext()) {
            String nama     = cursor.getString(0);
            int    harga    = cursor.getInt(1);
            int    qty      = cursor.getInt(2);
            int    subtotal = cursor.getInt(3);

            detail.append("• ").append(nama)
                    .append("\n  ")
                    .append(qty).append(" x Rp ")
                    .append(String.format(Locale.getDefault(), "%,d", harga))
                    .append(" = Rp ")
                    .append(String.format(Locale.getDefault(), "%,d", subtotal))
                    .append("\n");
        }
        cursor.close();

        detail.append("─────────────────────\n");
        detail.append("TOTAL  :  Rp ")
                .append(String.format(Locale.getDefault(), "%,d", total));

        new AlertDialog.Builder(this)
                .setTitle("Detail Transaksi #" + transaksiId)
                .setMessage(detail.toString())
                .setPositiveButton("Tutup", null)
                .show();
    }
}