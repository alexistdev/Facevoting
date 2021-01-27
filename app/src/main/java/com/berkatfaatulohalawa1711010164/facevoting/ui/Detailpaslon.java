package com.berkatfaatulohalawa1711010164.facevoting.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.berkatfaatulohalawa1711010164.facevoting.R;
import com.berkatfaatulohalawa1711010164.facevoting.config.Constants;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class Detailpaslon extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private Toolbar toolbar;
    private TextView mNamaKetua,mNamaWakil,mJudul,mVisiMisi,mProfilKetua,mProfilWakil;
    @SuppressLint("StaticFieldLeak")
    private static  ImageView mPhotoKetua, mPhotoWakil;
    private Context mContext;

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
            final String ketua = extra.getString("ketua","0");
            final String wakil = extra.getString("wakil","0");
            final String judulPaslon = extra.getString("judulPaslon","0");
            final String gambarKetua = extra.getString("photo_ketua","0");
            final String gambarWakil = extra.getString("photo_wakil","0");
            final String visimisi = extra.getString("visi_misi","0");
            final String profilKetua = extra.getString("profil_ketua","0");
            final String profilWakil = extra.getString("profil_wakil","0");
            mNamaKetua.setText(ketua);
            mNamaWakil.setText(wakil);
            mJudul.setText(judulPaslon);
            mVisiMisi.setText(visimisi);
            mProfilKetua.setText(profilKetua);
            mProfilWakil.setText(profilWakil);
            /* Memasukkan gambar ketua */
            Glide.with(mContext)
                    .load(Constants.IMAGES_URL+gambarKetua)
                    .apply(new RequestOptions().error(R.drawable.profil))
                    .into(Detailpaslon.mPhotoKetua);
            /* Memasukkan gambar wakil */
            Glide.with(mContext)
                    .load(Constants.IMAGES_URL+gambarWakil)
                    .apply(new RequestOptions().error(R.drawable.profil))
                    .into(Detailpaslon.mPhotoWakil);
            progressDialog.dismiss();
        }
    }

    private void init(){
        progressDialog = ProgressDialog.show(this, "", "Loading.....", true, false);
        mContext = this.getApplicationContext();
        toolbar = findViewById(R.id.toolbarmenu);
        mNamaKetua = findViewById(R.id.txt_ketua);
        mNamaWakil = findViewById(R.id.txt_wakil);
        mJudul = findViewById(R.id.txt_judul);
        mPhotoKetua = findViewById(R.id.gbr_ketua);
        mPhotoWakil = findViewById(R.id.gbr_wakil);
        mVisiMisi = findViewById(R.id.txt_visimisi);
        mProfilKetua = findViewById(R.id.txt_profil_ketua);
        mProfilWakil = findViewById(R.id.txt_profil_wakil);
    }
}