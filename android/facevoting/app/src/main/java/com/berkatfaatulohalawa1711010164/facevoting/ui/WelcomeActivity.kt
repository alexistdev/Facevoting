package com.berkatfaatulohalawa1711010164.facevoting.ui

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.berkatfaatulohalawa1711010164.facevoting.MainActivity
import com.berkatfaatulohalawa1711010164.facevoting.R
import com.berkatfaatulohalawa1711010164.facevoting.config.Constants
import com.berkatfaatulohalawa1711010164.facevoting.helper.SessionHelper

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        findViewById<TextView>(R.id.tombol_start).setOnClickListener {
            startActivity(Intent(this, preLogin1::class.java))
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
}
