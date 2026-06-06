package com.berkatfaatulohalawa1711010164.facevoting.helper

import com.berkatfaatulohalawa1711010164.facevoting.config.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceError {
    val builder: Retrofit.Builder = Retrofit.Builder()
        .baseUrl(Constants.URL)
        .addConverterFactory(GsonConverterFactory.create())

    val retrofit: Retrofit = builder.build()

    val httpClient: OkHttpClient.Builder = OkHttpClient.Builder()

    fun <S> createService(serviceClass: Class<S>): S = retrofit.create(serviceClass)
}
