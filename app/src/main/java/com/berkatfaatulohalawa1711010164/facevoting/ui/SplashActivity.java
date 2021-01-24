package com.berkatfaatulohalawa1711010164.facevoting.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.berkatfaatulohalawa1711010164.facevoting.MainActivity;
import com.berkatfaatulohalawa1711010164.facevoting.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView btnStart = findViewById(R.id.tombol_start);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SplashActivity.this, preLogin1.class);
                startActivity(intent);
            }
        });
    }
}