package com.berkatfaatulohalawa1711010164.facevoting.network

import android.content.Context
import com.berkatfaatulohalawa1711010164.facevoting.core.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    @Volatile private var INSTANCE: APIService? = null

    fun getInstance(context: Context): APIService {
        return INSTANCE ?: synchronized(this) {
            val client = OkHttpClient.Builder()
                .readTimeout(20, TimeUnit.SECONDS)
                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .addInterceptor(NetworkConnectionInterceptor(context.applicationContext))
                .build()

            Retrofit.Builder()
                .baseUrl(Constants.URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(APIService::class.java)
                .also { INSTANCE = it }
        }
    }
}
