package com.berkatfaatulohalawa1711010164.facevoting.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.berkatfaatulohalawa1711010164.facevoting.api.APIService
import com.berkatfaatulohalawa1711010164.facevoting.api.NoConnectivityException
import com.berkatfaatulohalawa1711010164.facevoting.MainActivity
import com.berkatfaatulohalawa1711010164.facevoting.config.Constants
import com.berkatfaatulohalawa1711010164.facevoting.helper.SessionHelper
import com.berkatfaatulohalawa1711010164.facevoting.model.LoginModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
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
        } else {
            startActivity(Intent(this, WelcomeActivity::class.java))
        }
        finish()
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

    private fun displayExceptionMessage(msg: String) {
        Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show()
    }
}
