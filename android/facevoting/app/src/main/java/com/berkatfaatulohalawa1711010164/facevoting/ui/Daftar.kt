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
import com.berkatfaatulohalawa1711010164.facevoting.helper.MyFirebaseMessagingService
import com.berkatfaatulohalawa1711010164.facevoting.viewmodel.AuthViewModel
import java.util.regex.Pattern

class Daftar : AppCompatActivity() {
    private lateinit var btnLogin: TextView
    private lateinit var btnDaftar: ImageView
    private lateinit var txtNama: EditText
    private lateinit var txtIdentitas: EditText
    private lateinit var txtEmail: EditText
    private lateinit var txtPassword: EditText
    private lateinit var progressDialog: AlertDialog

    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daftar)
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
        btnLogin.setOnClickListener { startActivity(Intent(this, Login::class.java)) }
        btnDaftar.setOnClickListener { proses() }

        viewModel.registerState.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> showLoading()
                is Resource.Success -> {
                    hideLoading()
                    val body = resource.data
                    SessionHelper.login(this, body.id_user, body.token_login, body.validasi, body.nama, body.identitas)
                    startActivity(Intent(this, Checkpoint::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    })
                    finish()
                }
                is Resource.Error -> {
                    hideLoading()
                    showToast(resource.message)
                }
            }
        }
    }

    private fun proses() {
        val namaLengkap = txtNama.text.toString()
        val identitas = txtIdentitas.text.toString()
        val email = txtEmail.text.toString()
        val password = txtPassword.text.toString()
        val tokenFirebase = MyFirebaseMessagingService.getToken(applicationContext)
        when {
            namaLengkap.isEmpty() || identitas.isEmpty() || email.isEmpty() || password.isEmpty() ->
                showToast("Semua kolom harus diisi !")
            !cekEmail(email) -> showToast("Email tidak valid !")
            else -> viewModel.register(namaLengkap, identitas, email, password, tokenFirebase)
        }
    }

    private fun cekEmail(email: String): Boolean =
        Pattern.compile(
            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@" +
            "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?" +
            "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\." +
            "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?" +
            "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|" +
            "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
        ).matcher(email).matches()

    private fun init() {
        btnLogin = findViewById(R.id.tbl_login)
        btnDaftar = findViewById(R.id.tombol_daftar)
        txtNama = findViewById(R.id.ed_nama)
        txtIdentitas = findViewById(R.id.ed_identitas)
        txtEmail = findViewById(R.id.ed_email)
        txtPassword = findViewById(R.id.ed_password)
        progressDialog = AlertDialog.Builder(this)
            .setMessage("Loading.....")
            .setCancelable(false)
            .create()
    }

    private fun showLoading() { if (!progressDialog.isShowing) progressDialog.show() }
    private fun hideLoading() { if (progressDialog.isShowing) progressDialog.dismiss() }
    private fun showToast(msg: String) { Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show() }
}
