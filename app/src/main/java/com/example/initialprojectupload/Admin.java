package com.example.initialprojectupload;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Admin extends AppCompatActivity {

    private DBHelper dbHelper;

    private Button btnKelolaProduk;
    private Button btnKelolaStok;
    private Button btnKelolaKasir;
    private Button btnLaporan;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);

        dbHelper = new DBHelper(this);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {
                    Insets systemBars =
                            insets.getInsets(
                                    WindowInsetsCompat.Type.systemBars());

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom);

                    return insets;
                });

        btnKelolaProduk = findViewById(R.id.btnKelolaProduk);
        btnKelolaStok = findViewById(R.id.btnKelolaStok);
        btnKelolaKasir = findViewById(R.id.btnKelolaKasir);
        btnLaporan = findViewById(R.id.btnLaporan);
        btnLogout = findViewById(R.id.btnLogout);

        btnLaporan.setOnClickListener(v -> {
            Intent intent =
                    new Intent(Admin.this,
                            LaporanActivity.class);

            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            finish();
        });
    }
}