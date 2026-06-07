package com.berkatfaatulohalawa1711010164.facevoting.ui

import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.berkatfaatulohalawa1711010164.facevoting.R
import com.berkatfaatulohalawa1711010164.facevoting.adapter.SuaraAdapter
import com.berkatfaatulohalawa1711010164.facevoting.core.Resource
import com.berkatfaatulohalawa1711010164.facevoting.viewmodel.HasilViewModel

class Detailhasil : AppCompatActivity() {
    private lateinit var suaraView: RecyclerView
    private lateinit var progressDialog: AlertDialog
    private lateinit var suaraAdapter: SuaraAdapter

    private val viewModel: HasilViewModel by viewModels()

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
        viewModel.loadSuara(idKategori)

        viewModel.suaraState.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> showLoading()
                is Resource.Success -> {
                    hideLoading()
                    resource.data.listSuara?.let { suaraAdapter.replaceData(it) }
                }
                is Resource.Error -> {
                    hideLoading()
                    Toast.makeText(this, resource.message, Toast.LENGTH_LONG).show()
                }
            }
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

    private fun showLoading() { if (!progressDialog.isShowing) progressDialog.show() }
    private fun hideLoading() { if (progressDialog.isShowing) progressDialog.dismiss() }
}
