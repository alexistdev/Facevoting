package com.berkatfaatulohalawa1711010164.facevoting.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.berkatfaatulohalawa1711010164.facevoting.MainActivity;
import com.berkatfaatulohalawa1711010164.facevoting.R;
import com.berkatfaatulohalawa1711010164.facevoting.helper.SessionHelper;

public class Checkpoint extends AppCompatActivity {
    private ImageView btnLanjut,btnRekam;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkpoint);
        init();
        if(SessionHelper.sudahrekam(this)){
            btnRekam.setVisibility(View.INVISIBLE);
            btnLanjut.setVisibility(View.VISIBLE);
        } else {
            //tombol lanjut dihilangkan, tombol rekam dimunculkan
            btnLanjut.setVisibility(View.INVISIBLE);
            btnRekam.setVisibility(View.VISIBLE);
        }
        btnLanjut.setOnClickListener(v -> {
            Intent intent = new Intent(Checkpoint.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
        btnRekam.setOnClickListener(v -> {
            Intent intent = new Intent(Checkpoint.this, Rekam.class);
            startActivity(intent);
            finish();
        });
    }

    private void init()
    {
        btnLanjut = findViewById(R.id.btnLanjutkan);
        btnRekam = findViewById(R.id.btnRekam);
        btnRekam.setVisibility(View.INVISIBLE);
        btnLanjut.setVisibility(View.INVISIBLE);
    }
}