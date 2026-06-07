package com.berkatfaatulohalawa1711010164.facevoting.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.berkatfaatulohalawa1711010164.facevoting.core.ErrorHelper
import com.berkatfaatulohalawa1711010164.facevoting.core.Resource
import com.berkatfaatulohalawa1711010164.facevoting.network.NoConnectivityException
import com.berkatfaatulohalawa1711010164.facevoting.network.RetrofitClient
import com.berkatfaatulohalawa1711010164.facevoting.response.GetMenu
import com.berkatfaatulohalawa1711010164.facevoting.response.GetPerolehan
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HasilRepository(context: Context) {
    private val api = RetrofitClient.getInstance(context)

    fun loadHasil(): LiveData<Resource<GetMenu>> {
        val result = MutableLiveData<Resource<GetMenu>>(Resource.Loading)
        api.tampilHasil().enqueue(object : Callback<GetMenu> {
            override fun onResponse(call: Call<GetMenu>, response: Response<GetMenu>) {
                result.postValue(
                    if (response.isSuccessful && response.body() != null)
                        Resource.Success(response.body()!!)
                    else
                        Resource.Error(ErrorHelper.parseError(response).message)
                )
            }
            override fun onFailure(call: Call<GetMenu>, t: Throwable) {
                val msg = if (t is NoConnectivityException) "Offline, cek koneksi internet anda!" else t.message ?: "Network error"
                result.postValue(Resource.Error(msg))
            }
        })
        return result
    }

    fun loadSuara(idKategori: String): LiveData<Resource<GetPerolehan>> {
        val result = MutableLiveData<Resource<GetPerolehan>>(Resource.Loading)
        api.tampilSuara(idKategori).enqueue(object : Callback<GetPerolehan> {
            override fun onResponse(call: Call<GetPerolehan>, response: Response<GetPerolehan>) {
                result.postValue(
                    if (response.isSuccessful && response.body() != null)
                        Resource.Success(response.body()!!)
                    else
                        Resource.Error(ErrorHelper.parseError(response).message)
                )
            }
            override fun onFailure(call: Call<GetPerolehan>, t: Throwable) {
                val msg = if (t is NoConnectivityException) "Offline, cek koneksi internet anda!" else t.message ?: "Network error"
                result.postValue(Resource.Error(msg))
            }
        })
        return result
    }
}
