package com.berkatfaatulohalawa1711010164.facevoting.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.berkatfaatulohalawa1711010164.facevoting.MainActivity;
import com.berkatfaatulohalawa1711010164.facevoting.R;

public class Login extends AppCompatActivity {
    private TextView btnDaftar;
    private ImageView btnLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnDaftar = findViewById(R.id.tbl_daftar);
        btnLogin = findViewById(R.id.btn_login);
        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Daftar.class);
                startActivity(intent);
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Paslon.class);
                startActivity(intent);
            }
        });
    }
}