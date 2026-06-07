package com.berkatfaatulohalawa1711010164.facevoting.helper

import android.content.Context
import com.berkatfaatulohalawa1711010164.facevoting.config.Constants
import com.google.gson.Gson
import androidx.core.content.edit

object SessionHelper {
    fun login(context: Context, id_user: String, token: String, validasi: String, nama: String, identitas: String): Boolean {
        val prefs = context.getSharedPreferences(Constants.USER_KEY, Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString(Constants.USER_SESSION, Gson().toJson(id_user))
            putString("id_user", id_user)
            putString("nama_user", nama)
            putString("identitas", identitas)
            putString("token", token)
            putString("validasi", validasi)
            apply()
        }
        return true
    }

    fun catatrekam(context: Context): Boolean {
        context.getSharedPreferences(Constants.USER_KEY, Context.MODE_PRIVATE)
            .edit { putString("crekam", "sudah") }
        return true
    }

    fun sudahrekam(context: Context): Boolean {
        return context.getSharedPreferences(Constants.USER_KEY, Context.MODE_PRIVATE)
            .getString("crekam", null) != null
    }

    fun sudahLogin(context: Context): Boolean {
        return context.getSharedPreferences(Constants.USER_KEY, Context.MODE_PRIVATE)
            .getString(Constants.USER_SESSION, null) != null
    }

    fun sudahValidasi(context: Context): Boolean {
        return context.getSharedPreferences(Constants.USER_KEY, Context.MODE_PRIVATE)
            .getString("validasi", null) != null
    }

    fun logout(context: Context?): Boolean {
        context?.getSharedPreferences(Constants.USER_KEY, Context.MODE_PRIVATE)
            ?.edit()?.clear()?.apply()
        return true
    }
}
