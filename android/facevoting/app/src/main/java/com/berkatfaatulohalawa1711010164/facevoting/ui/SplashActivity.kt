package com.berkatfaatulohalawa1711010164.facevoting.ui

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.berkatfaatulohalawa1711010164.facevoting.api.APIService
import com.berkatfaatulohalawa1711010164.facevoting.api.NoConnectivityException
import com.berkatfaatulohalawa1711010164.facevoting.MainActivity
import com.berkatfaatulohalawa1711010164.facevoting.R
import com.berkatfaatulohalawa1711010164.facevoting.config.Constants
import com.berkatfaatulohalawa1711010164.facevoting.helper.SessionHelper
import com.berkatfaatulohalawa1711010164.facevoting.model.LoginModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val prefs = applicationContext.getSharedPreferences(Constants.USER_KEY, MODE_PRIVATE)
        if (SessionHelper.sudahLogin(this)) {
            val idUser = prefs.getString("id_user", "") ?: ""
            getStatus(idUser)
            val valds = prefs.getString("validasi", "")
            if (valds == "2") {
                startActivity(Intent(this, Checkpoint::class.java))
            } else {
                startActivity(Intent(this, MainActivity::class.java))
            }
            finish()
        }
        findViewById<TextView>(R.id.tombol_start).setOnClickListener {
            startActivity(Intent(this, preLogin1::class.java))
        }
    }

    private fun getStatus(idUser: String) {
        try {
            APIService.create(applicationContext).dapatStatus(idUser)
                .enqueue(object : Callback<LoginModel> {
                    override fun onResponse(call: Call<LoginModel>, response: Response<LoginModel>) {
                        if (response.isSuccessful) {
                            response.body()?.let { body ->
                                SessionHelper.login(this@SplashActivity, body.idUser, body.token_login, body.validasi, body.nama, body.identitas)
                            }
                        }
                    }
                    override fun onFailure(call: Call<LoginModel>, t: Throwable) {
                        if (t is NoConnectivityException) displayExceptionMessage("Offline, cek koneksi internet anda!")
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
            displayExceptionMessage(e.message ?: "")
        }
    }

    override fun onResume() {
        super.onResume()
        val prefs = applicationContext.getSharedPreferences(Constants.USER_KEY, MODE_PRIVATE)
        if (SessionHelper.sudahLogin(this)) {
            val valds = prefs.getString("validasi", "")
            if (valds == "2") {
                startActivity(Intent(this, Checkpoint::class.java))
            } else {
                startActivity(Intent(this, MainActivity::class.java))
            }
            finish()
        }
    }

    fun displayExceptionMessage(msg: String) {
        Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show()
    }
}
