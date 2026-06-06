package com.berkatfaatulohalawa1711010164.facevoting.ui

import android.app.ProgressDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.berkatfaatulohalawa1711010164.facevoting.API.APIService
import com.berkatfaatulohalawa1711010164.facevoting.API.NoConnectivityException
import com.berkatfaatulohalawa1711010164.facevoting.R
import com.berkatfaatulohalawa1711010164.facevoting.adapter.PaslonAdapter
import com.berkatfaatulohalawa1711010164.facevoting.response.GetPaslon
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Paslon : AppCompatActivity() {
    private lateinit var gridView: RecyclerView
    private lateinit var paslonAdapter: PaslonAdapter
    private lateinit var progressDialog: ProgressDialog
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paslon)
        init()
        setSupportActionBar(toolbar)
        setupRecyclerView()
        val idKategori = intent.extras?.getString("id_kategori", "0") ?: "0"
        getPaslon(idKategori)
        supportActionBar?.apply {
            title = "Daftar Paslon"
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(true)
        }
    }

    private fun init() {
        progressDialog = ProgressDialog.show(this, "", "Loading.....", true, false)
        gridView = findViewById(R.id.rcPaslon)
        toolbar = findViewById(R.id.toolbarmenu)
    }

    private fun setupRecyclerView() {
        paslonAdapter = PaslonAdapter(applicationContext, mutableListOf())
        gridView.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        gridView.adapter = paslonAdapter
    }

    private fun getPaslon(kategori: String) {
        try {
            APIService.Factory.create(applicationContext).postPaslon(kategori)
                .enqueue(object : Callback<GetPaslon> {
                    override fun onResponse(call: Call<GetPaslon>, response: Response<GetPaslon>) {
                        progressDialog.dismiss()
                        if (response.isSuccessful) {
                            response.body()?.listPaslon?.let { paslonAdapter.replaceData(it) }
                        }
                    }
                    override fun onFailure(call: Call<GetPaslon>, t: Throwable) {
                        progressDialog.dismiss()
                        if (t is NoConnectivityException) displayExceptionMessage("Offline, cek koneksi internet anda!")
                    }
                })
        } catch (e: Exception) {
            progressDialog.dismiss()
            e.printStackTrace()
            displayExceptionMessage(e.message ?: "")
        }
    }

    fun displayExceptionMessage(msg: String) {
        Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show()
    }
}
