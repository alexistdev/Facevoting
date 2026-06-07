package com.berkatfaatulohalawa1711010164.facevoting.API

import android.content.Context
import android.net.ConnectivityManager
import okhttp3.Interceptor
import okhttp3.Response

internal class NetworkConnectionInterceptor(private val mContext: Context?) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isConnected()) {
            throw NoConnectivityException()
        }
        return chain.proceed(chain.request().newBuilder().build())
    }

    @Suppress("DEPRECATION")
    fun isConnected(): Boolean {
        val connectivityManager = mContext?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = connectivityManager.activeNetworkInfo
        return netInfo != null && netInfo.isConnected
    }
}
