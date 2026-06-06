package com.berkatfaatulohalawa1711010164.facevoting.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.berkatfaatulohalawa1711010164.facevoting.R

class preLogin1 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pre_login1)
        findViewById<ImageView>(R.id.btn_mulai).setOnClickListener {
            startActivity(Intent(this, Login::class.java))
        }
    }
}
