package com.berkatfaatulohalawa1711010164.facevoting.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.berkatfaatulohalawa1711010164.facevoting.config.Constants;
import com.google.gson.Gson;

public class SessionHelper {

    public static boolean login(Context context, String id_user, String token){
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                Constants.USER_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String jsonUser = new Gson().toJson(id_user);
        editor.putString(Constants.USER_SESSION, jsonUser);
        editor.putString("id_user", id_user);
        editor.putString("token", token);
        editor.apply();
        return true;
    }
}
