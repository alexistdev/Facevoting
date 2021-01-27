package com.berkatfaatulohalawa1711010164.facevoting.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.berkatfaatulohalawa1711010164.facevoting.R;

public class Detailpaslon extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailpaslon);

        init();
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("VOTING");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }

        Intent iin= getIntent();
        Bundle extra = iin.getExtras();


        if(extra != null) {
            //final String idKategori = extra.getString("id_kategori","0");
            /* Untuk setup toolbar */

        }
    }

    private void init(){
        //progressDialog = ProgressDialog.show(this, "", "Loading.....", true, false);
        toolbar = findViewById(R.id.toolbarmenu);
    }
}