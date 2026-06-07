package com.berkatfaatulohalawa1711010164.facevoting.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.berkatfaatulohalawa1711010164.facevoting.MainActivity
import com.berkatfaatulohalawa1711010164.facevoting.core.Constants
import com.berkatfaatulohalawa1711010164.facevoting.core.Resource
import com.berkatfaatulohalawa1711010164.facevoting.core.SessionHelper
import com.berkatfaatulohalawa1711010164.facevoting.viewmodel.AuthViewModel

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        val prefs = applicationContext.getSharedPreferences(Constants.USER_KEY, MODE_PRIVATE)
        if (SessionHelper.sudahLogin(this)) {
            val idUser = prefs.getString("id_user", "") ?: ""
            viewModel.refreshStatus(idUser)
            viewModel.refreshStatusState.observe(this) { resource ->
                if (resource is Resource.Success) {
                    val body = resource.data
                    SessionHelper.login(this, body.idUser, body.token_login, body.validasi, body.nama, body.identitas)
                } else if (resource is Resource.Error) {
                    Toast.makeText(applicationContext, resource.message, Toast.LENGTH_LONG).show()
                }
            }
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
}
