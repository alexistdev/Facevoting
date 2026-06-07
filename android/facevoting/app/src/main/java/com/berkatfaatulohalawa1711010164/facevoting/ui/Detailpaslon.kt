package com.berkatfaatulohalawa1711010164.facevoting.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.berkatfaatulohalawa1711010164.facevoting.R
import com.berkatfaatulohalawa1711010164.facevoting.config.Constants
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class Detailpaslon : AppCompatActivity() {
    private lateinit var progressDialog: AlertDialog
    private lateinit var toolbar: Toolbar
    private lateinit var mNamaKetua: TextView
    private lateinit var mNamaWakil: TextView
    private lateinit var mJudul: TextView
    private lateinit var mVisiMisi: TextView
    private lateinit var mProfilKetua: TextView
    private lateinit var mProfilWakil: TextView
    private lateinit var mCoblos: Button
    private lateinit var mPhotoKetua: ImageView
    private lateinit var mPhotoWakil: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailpaslon)
        init()
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "VOTING"
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(true)
        }
        intent.extras?.let { extra ->
            val idPaslon = extra.getString("idPaslon", "0")
            val idKategori = extra.getString("idKategori", "0")
            val ketua = extra.getString("ketua", "0")
            val wakil = extra.getString("wakil", "0")
            val judulPaslon = extra.getString("judulPaslon", "0")
            val gambarKetua = extra.getString("photo_ketua", "0")
            val gambarWakil = extra.getString("photo_wakil", "0")
            val visimisi = extra.getString("visi_misi", "0")
            val profilKetua = extra.getString("profil_ketua", "0")
            val profilWakil = extra.getString("profil_wakil", "0")
            mNamaKetua.text = ketua
            mNamaWakil.text = wakil
            mJudul.text = judulPaslon
            mVisiMisi.text = visimisi
            mProfilKetua.text = profilKetua
            mProfilWakil.text = profilWakil
            Glide.with(applicationContext)
                .load(Constants.IMAGES_URL + gambarKetua)
                .apply(RequestOptions().error(R.drawable.profil))
                .into(mPhotoKetua)
            Glide.with(applicationContext)
                .load(Constants.IMAGES_URL + gambarWakil)
                .apply(RequestOptions().error(R.drawable.profil))
                .into(mPhotoWakil)
            progressDialog.dismiss()
            mCoblos.setOnClickListener {
                startActivity(Intent(this, Validasi::class.java).apply {
                    putExtra("idPaslon", idPaslon)
                    putExtra("idKategori", idKategori)
                })
                finish()
            }
        }
    }

    private fun init() {
        progressDialog = AlertDialog.Builder(this)
            .setMessage("Loading.....")
            .setCancelable(false)
            .create()
        progressDialog.show()
        toolbar = findViewById(R.id.toolbarmenu)
        mNamaKetua = findViewById(R.id.txt_ketua)
        mNamaWakil = findViewById(R.id.txt_wakil)
        mJudul = findViewById(R.id.txt_judul)
        mPhotoKetua = findViewById(R.id.gbr_ketua)
        mPhotoWakil = findViewById(R.id.gbr_wakil)
        mVisiMisi = findViewById(R.id.txt_visimisi)
        mProfilKetua = findViewById(R.id.txt_profil_ketua)
        mProfilWakil = findViewById(R.id.txt_profil_wakil)
        mCoblos = findViewById(R.id.btnCoblos)
    }
}
