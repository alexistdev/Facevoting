package com.berkatfaatulohalawa1711010164.facevoting.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.berkatfaatulohalawa1711010164.facevoting.MainActivity
import com.berkatfaatulohalawa1711010164.facevoting.R
import com.berkatfaatulohalawa1711010164.facevoting.core.Constants
import com.berkatfaatulohalawa1711010164.facevoting.core.Resource
import com.berkatfaatulohalawa1711010164.facevoting.core.SessionHelper
import com.berkatfaatulohalawa1711010164.facevoting.viewmodel.AuthViewModel

class Checkpoint : AppCompatActivity() {
    private lateinit var btnLanjut: ImageView
    private lateinit var btnRekam: ImageView
    private lateinit var pDialog: AlertDialog

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkpoint)
        init()
        if (SessionHelper.sudahrekam(this)) {
            btnRekam.visibility = View.INVISIBLE
            btnLanjut.visibility = View.VISIBLE
        } else {
            btnLanjut.visibility = View.INVISIBLE
            btnRekam.visibility = View.VISIBLE
        }
        btnLanjut.setOnClickListener {
            val idUser = applicationContext.getSharedPreferences(Constants.USER_KEY, MODE_PRIVATE)
                .getString("id_user", "") ?: ""
            viewModel.checkStatus(idUser)
        }
        btnRekam.setOnClickListener {
            startActivity(Intent(this, Rekam::class.java))
            finish()
        }

        viewModel.checkStatusState.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> showDialog()
                is Resource.Success -> {
                    hideDialog()
                    val body = resource.data
                    val idUser = applicationContext.getSharedPreferences(Constants.USER_KEY, MODE_PRIVATE)
                        .getString("id_user", "") ?: ""
                    SessionHelper.login(this, idUser, body.token_login, body.validasi, body.nama, body.identitas)
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                is Resource.Error -> {
                    hideDialog()
                    showToast(resource.message)
                }
            }
        }
    }

    private fun init() {
        btnLanjut = findViewById(R.id.btnLanjutkan)
        btnRekam = findViewById(R.id.btnRekam)
        btnRekam.visibility = View.INVISIBLE
        btnLanjut.visibility = View.INVISIBLE
        pDialog = AlertDialog.Builder(this)
            .setMessage("Loading.....")
            .setCancelable(false)
            .create()
    }

    private fun showDialog() { if (!pDialog.isShowing) pDialog.show() }
    private fun hideDialog() { if (pDialog.isShowing) pDialog.dismiss() }
    private fun showToast(msg: String) { Toast.makeText(this, msg, Toast.LENGTH_LONG).show() }
}
