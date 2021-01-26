package com.berkatfaatulohalawa1711010164.facevoting.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.berkatfaatulohalawa1711010164.facevoting.R;

public class Daftar extends AppCompatActivity {
    private TextView btnLogin;
    private ImageView btnDaftar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar);

        btnLogin = findViewById(R.id.tbl_login);
        btnDaftar = findViewById(R.id.tombol_daftar);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Daftar.this, Login.class);
                startActivity(intent);
            }
        });
        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Daftar.this, Checkpoint.class);
                startActivity(intent);
            }
        });
    }
}