package com.example.initialprojectupload;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Admin extends AppCompatActivity {

    DrawerLayout drawerLayout;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;

    TextView menuDashboard;
    TextView menuProduk;
    TextView menuLaporan;
    TextView menuLogout;

    // Dashboard views
    TextView tvTanggalHariIni;
    TextView tvPenjualanHariIni;
    TextView tvTransaksiHariIni;
    TextView tvNoTransaksiHariIni;
    LinearLayout containerTransaksiHariIni;
    LineChart lineChart;

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        drawerLayout = findViewById(R.id.drawerLayout);
        toolbar      = findViewById(R.id.toolbar);

        menuDashboard = findViewById(R.id.menuDashboard);
        menuProduk    = findViewById(R.id.menuProduk);
        menuLaporan   = findViewById(R.id.menuLaporan);
        menuLogout    = findViewById(R.id.menuLogout);

        // Dashboard
        tvTanggalHariIni           = findViewById(R.id.tvTanggalHariIni);
        tvPenjualanHariIni         = findViewById(R.id.tvPenjualanHariIni);
        tvTransaksiHariIni         = findViewById(R.id.tvTransaksiHariIni);
        tvNoTransaksiHariIni       = findViewById(R.id.tvNoTransaksiHariIni);
        containerTransaksiHariIni  = findViewById(R.id.containerTransaksiHariIni);
        lineChart                  = findViewById(R.id.lineChart);

        dbHelper = new DBHelper(this);

        setSupportActionBar(toolbar);

        toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.open,
                R.string.close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // ── Sidebar menu handlers ────────────────────────────────────
        menuDashboard.setOnClickListener(v -> {
            drawerLayout.closeDrawers();
        });

        menuProduk.setOnClickListener(v -> {
            startActivity(new Intent(Admin.this, ProdukActivity.class));
        });

        menuLaporan.setOnClickListener(v -> {
            drawerLayout.closeDrawers();
            startActivity(new Intent(Admin.this, LaporanActivity.class));
        });

        menuLogout.setOnClickListener(v -> {
            startActivity(new Intent(Admin.this, Home.class));
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        muatDashboard();
    }

    // ─── Load semua data dashboard ──────────────────────────────────
    private void muatDashboard() {
        String hariIni = new SimpleDateFormat(
                "dd/MM/yyyy", Locale.getDefault()
        ).format(Calendar.getInstance().getTime());

        // Tanggal label (misal: Senin, 09 Juni 2025)
        String labelTanggal = new SimpleDateFormat(
                "EEEE, dd MMMM yyyy", new Locale("id", "ID")
        ).format(Calendar.getInstance().getTime());
        tvTanggalHariIni.setText(labelTanggal);

        muatStatHariIni(hariIni);
        muatTransaksiTerbaruHariIni(hariIni);
        muatLineChart();
    }

    // ─── Stat kartu: total penjualan & jumlah transaksi hari ini ───
    private void muatStatHariIni(String hariIni) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*), SUM(total) FROM transaksi WHERE tanggal LIKE ?",
                new String[]{ hariIni + "%" }
        );

        int jumlah = 0;
        int total  = 0;

        if (cursor.moveToFirst()) {
            jumlah = cursor.getInt(0);
            total  = cursor.isNull(1) ? 0 : cursor.getInt(1);
        }
        cursor.close();

        tvPenjualanHariIni.setText(
                "Rp " + String.format(Locale.getDefault(), "%,d", total)
        );
        tvTransaksiHariIni.setText(String.valueOf(jumlah));
    }

    // ─── Daftar transaksi terbaru hari ini (maks 5) ─────────────────
    private void muatTransaksiTerbaruHariIni(String hariIni) {

        containerTransaksiHariIni.removeAllViews();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id, tanggal, total FROM transaksi " +
                        "WHERE tanggal LIKE ? ORDER BY id DESC LIMIT 5",
                new String[]{ hariIni + "%" }
        );

        if (!cursor.moveToFirst()) {
            tvNoTransaksiHariIni.setVisibility(View.VISIBLE);
            cursor.close();
            return;
        }

        tvNoTransaksiHariIni.setVisibility(View.GONE);

        do {
            int    id      = cursor.getInt(0);
            String tanggal = cursor.getString(1);
            int    total   = cursor.getInt(2);

            // Buat view item transaksi secara programatik
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setBackground(getResources().getDrawable(R.drawable.bg_item_transaksi));

            LinearLayout.LayoutParams rowParams =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
            rowParams.setMargins(0, 0, 0, 8);
            row.setLayoutParams(rowParams);
            row.setPadding(16, 14, 16, 14);

            // Label kiri
            LinearLayout leftLayout = new LinearLayout(this);
            leftLayout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams leftParams =
                    new LinearLayout.LayoutParams(0,
                            LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            leftLayout.setLayoutParams(leftParams);

            TextView tvId = new TextView(this);
            tvId.setText("#" + id + "  |  " + tanggal);
            tvId.setTextSize(13f);
            tvId.setTextColor(Color.parseColor("#555555"));

            TextView tvTotalRow = new TextView(this);
            tvTotalRow.setText("Rp " + String.format(Locale.getDefault(), "%,d", total));
            tvTotalRow.setTextSize(15f);
            tvTotalRow.setTextColor(Color.parseColor("#1565C0"));

            leftLayout.addView(tvId);
            leftLayout.addView(tvTotalRow);
            row.addView(leftLayout);

            containerTransaksiHariIni.addView(row);

        } while (cursor.moveToNext());

        cursor.close();
    }

    // ─── Line Chart: penjualan 7 hari terakhir ──────────────────────
    private void muatLineChart() {

        List<Entry>  entries   = new ArrayList<>();
        List<String> labels    = new ArrayList<>();
        SQLiteDatabase db      = dbHelper.getReadableDatabase();

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat fmtKey   = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat fmtLabel = new SimpleDateFormat("dd/MM",      Locale.getDefault());

        // Mundur 6 hari, jadi total 7 hari termasuk hari ini
        for (int i = 6; i >= 0; i--) {
            cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -i);

            String keyDate   = fmtKey.format(cal.getTime());
            String labelDate = fmtLabel.format(cal.getTime());

            Cursor c = db.rawQuery(
                    "SELECT IFNULL(SUM(total), 0) FROM transaksi WHERE tanggal LIKE ?",
                    new String[]{ keyDate + "%" }
            );

            float totalHari = 0f;
            if (c.moveToFirst()) {
                totalHari = c.getFloat(0);
            }
            c.close();

            int idx = 6 - i;
            entries.add(new Entry(idx, totalHari));
            labels.add(labelDate);
        }

        // Dataset
        LineDataSet dataSet = new LineDataSet(entries, "Penjualan (Rp)");
        dataSet.setColor(Color.parseColor("#1565C0"));
        dataSet.setValueTextColor(Color.parseColor("#1565C0"));
        dataSet.setLineWidth(2.5f);
        dataSet.setCircleColor(Color.parseColor("#1565C0"));
        dataSet.setCircleHoleColor(Color.WHITE);
        dataSet.setCircleRadius(5f);
        dataSet.setCircleHoleRadius(3f);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor("#BBDEFB"));
        dataSet.setFillAlpha(80);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setDrawValues(true);
        dataSet.setValueTextSize(9f);

        // Formatter nilai (tampilkan ribuan K)
        dataSet.setValueFormatter(new com.github.mikephil.charting.formatter.ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value == 0) return "";
                if (value >= 1_000_000) return String.format(Locale.getDefault(), "%.1fM", value / 1_000_000f);
                if (value >= 1_000)     return String.format(Locale.getDefault(), "%.0fK", value / 1_000f);
                return String.valueOf((int) value);
            }
        });

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        // Styling chart
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(false);
        lineChart.setDrawGridBackground(false);
        lineChart.setExtraBottomOffset(8f);
        lineChart.setExtraTopOffset(8f);

        // X Axis
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.parseColor("#666666"));
        xAxis.setTextSize(10f);
        xAxis.setAxisLineColor(Color.parseColor("#DDDDDD"));

        // Y Axis kiri
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.parseColor("#F0F0F0"));
        leftAxis.setTextColor(Color.parseColor("#666666"));
        leftAxis.setTextSize(9f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisLineColor(Color.parseColor("#DDDDDD"));

        // Sembunyikan Y axis kanan
        lineChart.getAxisRight().setEnabled(false);

        lineChart.animateX(800);
        lineChart.invalidate();
    }
}