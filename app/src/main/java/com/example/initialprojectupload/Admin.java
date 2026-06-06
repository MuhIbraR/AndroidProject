package com.example.initialprojectupload;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

public class Admin extends AppCompatActivity {

    DrawerLayout drawerLayout;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;

    TextView menuDashboard;
    TextView menuProduk;
    TextView menuLaporan;
    TextView menuLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        drawerLayout = findViewById(R.id.drawerLayout);
        toolbar = findViewById(R.id.toolbar);

        menuDashboard = findViewById(R.id.menuDashboard);
        menuProduk = findViewById(R.id.menuProduk);
        menuLaporan = findViewById(R.id.menuLaporan);
        menuLogout = findViewById(R.id.menuLogout);

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

        menuDashboard.setOnClickListener(v -> {

            drawerLayout.closeDrawers();

            Toast.makeText(
                    Admin.this,
                    "Dashboard",
                    Toast.LENGTH_SHORT
            ).show();

        });

        menuProduk.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            Admin.this,
                            ProdukActivity.class
                    );

            startActivity(intent);

        });

        menuLaporan.setOnClickListener(v -> {

            drawerLayout.closeDrawers();

            Toast.makeText(
                    Admin.this,
                    "Laporan Penjualan",
                    Toast.LENGTH_SHORT
            ).show();

            /*
            Nanti diganti:
            Intent intent =
                    new Intent(Admin.this,
                            LaporanActivity.class);
            startActivity(intent);
            */

        });

        menuLogout.setOnClickListener(v -> {

            Intent intent = new Intent(Admin.this, Home.class);

            startActivity(intent);

            finish();

        });
    }
}