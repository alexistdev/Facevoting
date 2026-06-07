package com.berkatfaatulohalawa1711010164.facevoting.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.berkatfaatulohalawa1711010164.facevoting.api.APIService
import com.berkatfaatulohalawa1711010164.facevoting.api.NoConnectivityException
import com.berkatfaatulohalawa1711010164.facevoting.MainActivity
import com.berkatfaatulohalawa1711010164.facevoting.R
import com.berkatfaatulohalawa1711010164.facevoting.config.Constants
import com.berkatfaatulohalawa1711010164.facevoting.helper.ErrorHelper
import com.berkatfaatulohalawa1711010164.facevoting.helper.SessionHelper
import com.berkatfaatulohalawa1711010164.facevoting.model.LoginModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Checkpoint : AppCompatActivity() {
    private lateinit var btnLanjut: ImageView
    private lateinit var btnRekam: ImageView
    private lateinit var pDialog: AlertDialog

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
        btnLanjut.setOnClickListener { checkStatus() }
        btnRekam.setOnClickListener {
            startActivity(Intent(this, Rekam::class.java))
            finish()
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

    private fun checkStatus() {
        showDialog()
        try {
            val idUser = applicationContext.getSharedPreferences(Constants.USER_KEY, MODE_PRIVATE)
                .getString("id_user", "") ?: ""
            APIService.create(this).cekStatus(idUser)
                .enqueue(object : Callback<LoginModel> {
                    override fun onResponse(call: Call<LoginModel>, response: Response<LoginModel>) {
                        hideDialog()
                        if (response.isSuccessful) {
                            response.body()?.let { body ->
                                if (SessionHelper.login(this@Checkpoint, idUser, body.token_login, body.validasi, body.nama, body.identitas)) {
                                    startActivity(Intent(this@Checkpoint, MainActivity::class.java))
                                    finish()
                                }
                            }
                        } else {
                            tampilPesan(ErrorHelper.parseError(response).message)
                        }
                    }
                    override fun onFailure(call: Call<LoginModel>, t: Throwable) {
                        hideDialog()
                        if (t is NoConnectivityException) tampilPesan("Offline, cek koneksi internet anda!")
                    }
                })
        } catch (e: Exception) {
            hideDialog()
            e.printStackTrace()
            tampilPesan(e.message ?: "")
        }
    }

    private fun showDialog() { if (!pDialog.isShowing) pDialog.show() }
    private fun hideDialog() { if (pDialog.isShowing) pDialog.dismiss() }

    private fun tampilPesan(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }
}
