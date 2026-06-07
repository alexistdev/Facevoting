package com.berkatfaatulohalawa1711010164.facevoting.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.berkatfaatulohalawa1711010164.facevoting.core.ErrorHelper
import com.berkatfaatulohalawa1711010164.facevoting.core.Resource
import com.berkatfaatulohalawa1711010164.facevoting.model.LoginModel
import com.berkatfaatulohalawa1711010164.facevoting.network.NoConnectivityException
import com.berkatfaatulohalawa1711010164.facevoting.network.RetrofitClient
import com.berkatfaatulohalawa1711010164.facevoting.response.GetMenu
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeRepository(context: Context) {
    private val api = RetrofitClient.getInstance(context)

    fun loadIdentitas(idUser: String): LiveData<Resource<LoginModel>> {
        val result = MutableLiveData<Resource<LoginModel>>(Resource.Loading)
        api.cekStatus(idUser).enqueue(object : Callback<LoginModel> {
            override fun onResponse(call: Call<LoginModel>, response: Response<LoginModel>) {
                result.postValue(
                    if (response.isSuccessful && response.body() != null)
                        Resource.Success(response.body()!!)
                    else
                        Resource.Error(ErrorHelper.parseError(response).message)
                )
            }
            override fun onFailure(call: Call<LoginModel>, t: Throwable) {
                val msg = if (t is NoConnectivityException) "Offline, cek koneksi internet anda!" else t.message ?: "Network error"
                result.postValue(Resource.Error(msg))
            }
        })
        return result
    }

    fun loadMenu(idUser: String): LiveData<Resource<GetMenu>> {
        val result = MutableLiveData<Resource<GetMenu>>(Resource.Loading)
        api.listMenu(idUser).enqueue(object : Callback<GetMenu> {
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
}
