package com.berkatfaatulohalawa1711010164.facevoting.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.berkatfaatulohalawa1711010164.facevoting.config.Constants;
import com.berkatfaatulohalawa1711010164.facevoting.ui.validasi;
import com.google.gson.Gson;

public class SessionHelper {

    public static boolean login(Context context, String id_user, String token, String validasi){
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                Constants.USER_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String jsonUser = new Gson().toJson(id_user);
        editor.putString(Constants.USER_SESSION, jsonUser);
        editor.putString("id_user", id_user);
        editor.putString("token", token);
        editor.putString("validasi", validasi);
        editor.apply();
        return true;
    }

    public static boolean catatrekam(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.USER_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("crekam", "sudah");
        editor.apply();
        return true;
    }

    public static boolean sudahrekam(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                Constants.USER_KEY, Context.MODE_PRIVATE);
        String userJson = sharedPreferences.getString("crekam", null);
        if (userJson != null) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean sudahLogin(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                Constants.USER_KEY, Context.MODE_PRIVATE);
        String userJson = sharedPreferences.getString(Constants.USER_SESSION, null);
        if (userJson != null) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean sudahValidasi(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                Constants.USER_KEY, Context.MODE_PRIVATE);
        String userJson = sharedPreferences.getString("validasi", null);
        if (userJson != null) {
            return true;
        } else {
            return false;
        }
    }
}
