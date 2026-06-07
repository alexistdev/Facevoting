package com.berkatfaatulohalawa1711010164.facevoting.ui

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.berkatfaatulohalawa1711010164.facevoting.MainActivity
import com.berkatfaatulohalawa1711010164.facevoting.R
import com.berkatfaatulohalawa1711010164.facevoting.core.Resource
import com.berkatfaatulohalawa1711010164.facevoting.core.SessionHelper
import com.berkatfaatulohalawa1711010164.facevoting.viewmodel.AuthViewModel

class Login : AppCompatActivity() {
    private lateinit var txtEmail: EditText
    private lateinit var txtPassword: EditText
    private lateinit var btnLogin: ImageView
    private lateinit var btnDaftar: TextView
    private lateinit var progressDialog: AlertDialog

    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        init()
        if (SessionHelper.sudahLogin(this)) {
            if (SessionHelper.sudahValidasi(this)) {
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                startActivity(Intent(this, Checkpoint::class.java))
            }
            finish()
        }
        btnDaftar.setOnClickListener { startActivity(Intent(this, Daftar::class.java)) }
        btnLogin.setOnClickListener {
            val email = txtEmail.text.toString()
            val password = txtPassword.text.toString()
            if (email.trim().isNotEmpty() && password.trim().isNotEmpty()) {
                viewModel.login(email, password)
            } else {
                showToast("Semua kolom harus diisi!")
            }
        }

        viewModel.loginState.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> showLoading()
                is Resource.Success -> {
                    hideLoading()
                    val body = resource.data
                    SessionHelper.login(this, body.idUser, body.token_login, body.validasi, body.nama, body.identitas)
                    val intent = if (body.validasi == "2") {
                        Intent(this, Checkpoint::class.java)
                    } else {
                        Intent(this, MainActivity::class.java)
                    }
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
                is Resource.Error -> {
                    hideLoading()
                    showToast(resource.message)
                }
            }
        }
    }

    private fun init() {
        progressDialog = AlertDialog.Builder(this)
            .setMessage("Loading.....")
            .setCancelable(false)
            .create()
        btnLogin = findViewById(R.id.btn_login)
        btnDaftar = findViewById(R.id.tbl_daftar)
        txtEmail = findViewById(R.id.ed_email)
        txtPassword = findViewById(R.id.ed_password)
    }

    private fun showLoading() { if (!progressDialog.isShowing) progressDialog.show() }
    private fun hideLoading() { if (progressDialog.isShowing) progressDialog.dismiss() }
    private fun showToast(msg: String) { Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show() }
}
