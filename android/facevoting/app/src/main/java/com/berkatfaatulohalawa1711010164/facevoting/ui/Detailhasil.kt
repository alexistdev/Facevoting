package com.berkatfaatulohalawa1711010164.facevoting.ui

import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.berkatfaatulohalawa1711010164.facevoting.api.APIService
import com.berkatfaatulohalawa1711010164.facevoting.api.NoConnectivityException
import com.berkatfaatulohalawa1711010164.facevoting.R
import com.berkatfaatulohalawa1711010164.facevoting.adapter.SuaraAdapter
import com.berkatfaatulohalawa1711010164.facevoting.response.GetPerolehan
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Detailhasil : AppCompatActivity() {
    private lateinit var suaraView: RecyclerView
    private lateinit var progressDialog: AlertDialog
    private lateinit var suaraAdapter: SuaraAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailhasil)
        val toolbar = findViewById<Toolbar>(R.id.tbtoolbar)
        setSupportActionBar(toolbar)
        dataInit()
        setupRecyclerView()
        supportActionBar?.let {
            title = "Detail Hasil Perolehan Suara"
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowTitleEnabled(true)
        }
        val idKategori = intent.extras?.getString("id_kategori", "0") ?: "0"
        setData(idKategori)
    }

    private fun setData(idKategori: String) {
        tampilLoading()
        try {
            APIService.create(this).tampilSuara(idKategori)
                .enqueue(object : Callback<GetPerolehan> {
                    override fun onResponse(call: Call<GetPerolehan>, response: Response<GetPerolehan>) {
                        hideLoading()
                        if (response.isSuccessful) {
                            response.body()?.listSuara?.let { suaraAdapter.replaceData(it) }
                        }
                    }
                    override fun onFailure(call: Call<GetPerolehan>, t: Throwable) {
                        hideLoading()
                        if (t is NoConnectivityException) tampilPesan("Offline, cek koneksi internet anda!")
                    }
                })
        } catch (e: Exception) {
            hideLoading()
            e.printStackTrace()
            tampilPesan(e.message ?: "")
        }
    }

    private fun dataInit() {
        suaraView = findViewById(R.id.rcSuara)
        progressDialog = AlertDialog.Builder(this)
            .setMessage("Loading.....")
            .setCancelable(false)
            .create()
    }

    private fun setupRecyclerView() {
        val llm = object : LinearLayoutManager(this) {
            override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams =
                RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        suaraAdapter = SuaraAdapter(this, mutableListOf())
        suaraView.layoutManager = llm
        suaraView.adapter = suaraAdapter
    }

    private fun tampilLoading() { if (!progressDialog.isShowing) progressDialog.show() }
    private fun hideLoading() { if (progressDialog.isShowing) progressDialog.dismiss() }

    private fun tampilPesan(pesan: String) {
        Toast.makeText(this, pesan, Toast.LENGTH_LONG).show()
    }
}
