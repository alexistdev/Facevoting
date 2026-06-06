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
import com.berkatfaatulohalawa1711010164.facevoting.helper.MyFirebaseMessagingService
import com.berkatfaatulohalawa1711010164.facevoting.helper.SessionHelper
import com.berkatfaatulohalawa1711010164.facevoting.model.UserModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class Daftar : AppCompatActivity() {
    private lateinit var btnLogin: TextView
    private lateinit var btnDaftar: ImageView
    private lateinit var txtNama: EditText
    private lateinit var txtIdentitas: EditText
    private lateinit var txtEmail: EditText
    private lateinit var txtPassword: EditText
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daftar)
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
    }

    private fun proses() {
        tampilLoading()
        val nama_lengkap = txtNama.text.toString()
        val identitas = txtIdentitas.text.toString()
        val email = txtEmail.text.toString()
        val password = txtPassword.text.toString()
        val token_firebase = MyFirebaseMessagingService.getToken(applicationContext)
        if (nama_lengkap.isEmpty() || identitas.isEmpty() || email.isEmpty() || password.isEmpty()) {
            hideLoading()
            tampilPesan("Semua kolom harus diisi !")
        } else if (!cekEmail(email)) {
            hideLoading()
            tampilPesan("Email tidak valid !")
        } else {
            try {
                APIService.Factory.create(applicationContext)
                    .daftarUser(nama_lengkap, identitas, email, password, token_firebase)
                    .enqueue(object : Callback<UserModel> {
                        override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                            hideLoading()
                            if (response.isSuccessful) {
                                response.body()?.let { body ->
                                    if (SessionHelper.login(this@Daftar, body.id_user, body.token_login, body.validasi, body.nama, body.identitas)) {
                                        startActivity(Intent(this@Daftar, Checkpoint::class.java).apply {
                                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                        })
                                        finish()
                                    }
                                }
                            } else {
                                tampilPesan(ErrorHelper.parseError(response).message)
                            }
                        }
                        override fun onFailure(call: Call<UserModel>, t: Throwable) {
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
    }

    private fun cekEmail(email: String): Boolean =
        Pattern.compile(
            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@" +
            "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?" +
            "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\." +
            "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?" +
            "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|" +
            "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
        ).matcher(email).matches()

    private fun tampilLoading() { if (!progressDialog.isShowing) progressDialog.show() }
    private fun hideLoading() { if (progressDialog.isShowing) progressDialog.dismiss() }

    private fun init() {
        btnLogin = findViewById(R.id.tbl_login)
        btnDaftar = findViewById(R.id.tombol_daftar)
        txtNama = findViewById(R.id.ed_nama)
        txtIdentitas = findViewById(R.id.ed_identitas)
        txtEmail = findViewById(R.id.ed_email)
        txtPassword = findViewById(R.id.ed_password)
        progressDialog = ProgressDialog(this).apply {
            setCancelable(false)
            setMessage("Loading.....")
        }
    }

    fun tampilPesan(pesan: String) {
        Toast.makeText(applicationContext, pesan, Toast.LENGTH_LONG).show()
    }
}
