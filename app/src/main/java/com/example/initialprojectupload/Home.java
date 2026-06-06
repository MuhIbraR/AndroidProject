package com.example.initialprojectupload;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

public class Home extends AppCompatActivity {

    // Deklarasi variabel untuk elemen UI
    private TextInputEditText etUsername, etPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Menghubungkan variabel dengan ID di layout XML
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        // Menambahkan aksi ketika tombol login diklik
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prosesLogin();
            }
        });
    }

    private void prosesLogin() {
        // Mengambil teks dari inputan dan menghapus spasi berlebih di awal/akhir (.trim())
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validasi jika kolom input kosong
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Username dan Password tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- LOGIKA PENGECEKAN AKUN ---

        // 1. Pengecekan Akun Admin
        if (username.equals("Admin123") && password.equals("admin1")) {
            Toast.makeText(this, "Login Berhasil! Selamat datang Admin.", Toast.LENGTH_SHORT).show();

            // Pindah ke halaman Admin
            Intent intentAdmin = new Intent(Home.this, Admin.class);
            startActivity(intentAdmin);
            finish(); // Menutup halaman Home agar tidak bisa kembali dengan tombol 'Back'
        }

        // 2. Pengecekan Akun Kasir 1
        else if (username.equals("kasir1") && password.equals("kasir123")) {
            Toast.makeText(this, "Login Berhasil! Selamat datang Kasir 1.", Toast.LENGTH_SHORT).show();

            // Pindah ke halaman Kasir
            Intent intentKasir = new Intent(Home.this, Kasir.class);
            startActivity(intentKasir);
            finish();
        }

        // 3. Pengecekan Akun Kasir 2
        else if (username.equals("kasir2") && password.equals("kasir456")) {
            Toast.makeText(this, "Login Berhasil! Selamat datang Kasir 2.", Toast.LENGTH_SHORT).show();

            // Pindah ke halaman Kasir
            Intent intentKasir = new Intent(Home.this, Kasir.class);
            startActivity(intentKasir);
            finish();
        }

        // 4. Jika Username atau Password tidak ada yang cocok
        else {
            Toast.makeText(this, "Username atau Password salah!", Toast.LENGTH_SHORT).show();
        }
    }
}