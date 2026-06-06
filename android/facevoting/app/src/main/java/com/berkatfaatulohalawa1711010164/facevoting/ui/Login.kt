package com.berkatfaatulohalawa1711010164.facevoting.ui

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.berkatfaatulohalawa1711010164.facevoting.API.APIService
import com.berkatfaatulohalawa1711010164.facevoting.MainActivity
import com.berkatfaatulohalawa1711010164.facevoting.R
import com.berkatfaatulohalawa1711010164.facevoting.helper.ErrorHelper
import com.berkatfaatulohalawa1711010164.facevoting.helper.SessionHelper
import com.berkatfaatulohalawa1711010164.facevoting.model.LoginModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Login : AppCompatActivity() {
    private lateinit var txtEmail: EditText
    private lateinit var txtPassword: EditText
    private lateinit var btnLogin: ImageView
    private lateinit var btnDaftar: TextView
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
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
                cek_login(email, password)
            } else {
                tampilPesan("Semua kolom harus diisi!")
            }
        }
    }

    private fun cek_login(email: String, password: String) {
        tampilLoading()
        try {
            APIService.Factory.create(applicationContext).validasiLogin(email, password)
                .enqueue(object : Callback<LoginModel> {
                    override fun onResponse(call: Call<LoginModel>, response: Response<LoginModel>) {
                        hideLoading()
                        if (response.isSuccessful) {
                            response.body()?.let { body ->
                                if (SessionHelper.login(this@Login, body.idUser, body.token_login, body.validasi, body.nama, body.identitas)) {
                                    val intent = if (body.validasi == "2") {
                                        Intent(this@Login, Checkpoint::class.java)
                                    } else {
                                        Intent(this@Login, MainActivity::class.java)
                                    }
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                    finish()
                                }
                            }
                        } else {
                            tampilPesan(ErrorHelper.parseError(response).message)
                        }
                    }
                    override fun onFailure(call: Call<LoginModel>, t: Throwable) {
                        hideLoading()
                        tampilPesan(t.message ?: "")
                    }
                })
        } catch (e: Exception) {
            hideLoading()
            e.printStackTrace()
            tampilPesan(e.message ?: "")
        }
    }

    fun init() {
        progressDialog = ProgressDialog(this).apply {
            setCancelable(false)
            setMessage("Loading.....")
        }
        btnLogin = findViewById(R.id.btn_login)
        btnDaftar = findViewById(R.id.tbl_daftar)
        txtEmail = findViewById(R.id.ed_email)
        txtPassword = findViewById(R.id.ed_password)
    }

    private fun tampilLoading() { if (!progressDialog.isShowing) progressDialog.show() }
    private fun hideLoading() { if (progressDialog.isShowing) progressDialog.dismiss() }

    fun tampilPesan(pesan: String) {
        Toast.makeText(applicationContext, pesan, Toast.LENGTH_LONG).show()
    }
}
