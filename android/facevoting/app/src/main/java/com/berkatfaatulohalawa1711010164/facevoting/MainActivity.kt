package com.berkatfaatulohalawa1711010164.facevoting

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.berkatfaatulohalawa1711010164.facevoting.config.Constants
import com.berkatfaatulohalawa1711010164.facevoting.fragment.akun_fragment
import com.berkatfaatulohalawa1711010164.facevoting.fragment.hasil_fragment
import com.berkatfaatulohalawa1711010164.facevoting.fragment.home_fragment
import com.berkatfaatulohalawa1711010164.facevoting.fragment.votefragment
import com.berkatfaatulohalawa1711010164.facevoting.ui.Checkpoint
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    @SuppressLint("NonConstantResourceId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val prefs = applicationContext.getSharedPreferences(Constants.USER_KEY, Context.MODE_PRIVATE)
        if (prefs.getString("validasi", "") == "2") {
            startActivity(Intent(this, Checkpoint::class.java))
            finish()
        }
        loadFragment(home_fragment())
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomMenu)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.home_menu -> home_fragment()
                R.id.vote_menu -> votefragment()
                R.id.hasil_menu -> hasil_fragment()
                else -> akun_fragment()
            }
            loadFragment(fragment)
        }
    }

    override fun onResume() {
        super.onResume()
        val prefs = applicationContext.getSharedPreferences(Constants.USER_KEY, Context.MODE_PRIVATE)
        if (prefs.getString("validasi", "") == "2") {
            startActivity(Intent(this, Checkpoint::class.java))
            finish()
        }
    }

    private fun loadFragment(fragment: Fragment?): Boolean {
        if (fragment != null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fl_container, fragment)
                .commit()
            return true
        }
        return false
    }
}
