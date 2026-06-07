package com.berkatfaatulohalawa1711010164.facevoting.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.berkatfaatulohalawa1711010164.facevoting.R
import com.berkatfaatulohalawa1711010164.facevoting.adapter.PaslonAdapter
import com.berkatfaatulohalawa1711010164.facevoting.core.Resource
import com.berkatfaatulohalawa1711010164.facevoting.viewmodel.VoteViewModel

class Paslon : AppCompatActivity() {
    private lateinit var gridView: RecyclerView
    private lateinit var paslonAdapter: PaslonAdapter
    private lateinit var progressDialog: AlertDialog
    private lateinit var toolbar: Toolbar

    private val viewModel: VoteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paslon)
        init()
        setSupportActionBar(toolbar)
        setupRecyclerView()
        supportActionBar?.apply {
            title = "Daftar Paslon"
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(true)
        }
        val idKategori = intent.extras?.getString("id_kategori", "0") ?: "0"
        viewModel.loadPaslon(idKategori)

        viewModel.paslonState.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> { /* dialog already shown */ }
                is Resource.Success -> {
                    progressDialog.dismiss()
                    resource.data.listPaslon?.let { paslonAdapter.replaceData(it) }
                }
                is Resource.Error -> {
                    progressDialog.dismiss()
                    Toast.makeText(applicationContext, resource.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun init() {
        progressDialog = AlertDialog.Builder(this)
            .setMessage("Loading.....")
            .setCancelable(false)
            .create()
        progressDialog.show()
        gridView = findViewById(R.id.rcPaslon)
        toolbar = findViewById(R.id.toolbarmenu)
    }

    private fun setupRecyclerView() {
        paslonAdapter = PaslonAdapter(applicationContext, mutableListOf())
        gridView.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        gridView.adapter = paslonAdapter
    }
}
