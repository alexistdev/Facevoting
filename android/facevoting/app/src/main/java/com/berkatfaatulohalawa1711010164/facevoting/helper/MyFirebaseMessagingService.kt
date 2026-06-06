package com.berkatfaatulohalawa1711010164.facevoting.helper

import android.content.Context
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(s: String) {
        super.onNewToken(s)
        Log.e("newToken", s)
        getSharedPreferences("_", MODE_PRIVATE).edit().putString("fb", s).apply()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
    }

    companion object {
        fun getToken(context: Context): String {
            return context.getSharedPreferences("_", MODE_PRIVATE).getString("fb", "empty") ?: "empty"
        }
    }
}
