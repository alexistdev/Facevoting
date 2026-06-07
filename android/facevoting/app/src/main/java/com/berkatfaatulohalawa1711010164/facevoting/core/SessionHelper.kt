package com.berkatfaatulohalawa1711010164.facevoting.core

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson

object SessionHelper {
    fun login(context: Context, idUser: String, token: String, validasi: String, nama: String, identitas: String): Boolean {
        context.getSharedPreferences(Constants.USER_KEY, Context.MODE_PRIVATE).edit {
            putString(Constants.USER_SESSION, Gson().toJson(idUser))
            putString("id_user", idUser)
            putString("nama_user", nama)
            putString("identitas", identitas)
            putString("token", token)
            putString("validasi", validasi)
        }
        return true
    }

    fun catatrekam(context: Context): Boolean {
        context.getSharedPreferences(Constants.USER_KEY, Context.MODE_PRIVATE)
            .edit { putString("crekam", "sudah") }
        return true
    }

    fun sudahrekam(context: Context): Boolean =
        context.getSharedPreferences(Constants.USER_KEY, Context.MODE_PRIVATE)
            .getString("crekam", null) != null

    fun sudahLogin(context: Context): Boolean =
        context.getSharedPreferences(Constants.USER_KEY, Context.MODE_PRIVATE)
            .getString(Constants.USER_SESSION, null) != null

    fun sudahValidasi(context: Context): Boolean =
        context.getSharedPreferences(Constants.USER_KEY, Context.MODE_PRIVATE)
            .getString("validasi", null) != null

    fun logout(context: Context?): Boolean {
        context?.getSharedPreferences(Constants.USER_KEY, Context.MODE_PRIVATE)
            ?.edit()?.clear()?.apply()
        return true
    }
}
