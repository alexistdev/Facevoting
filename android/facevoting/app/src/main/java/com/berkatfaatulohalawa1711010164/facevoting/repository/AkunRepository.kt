package com.berkatfaatulohalawa1711010164.facevoting.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.berkatfaatulohalawa1711010164.facevoting.core.ErrorHelper
import com.berkatfaatulohalawa1711010164.facevoting.core.Resource
import com.berkatfaatulohalawa1711010164.facevoting.model.AkunModel
import com.berkatfaatulohalawa1711010164.facevoting.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AkunRepository(context: Context) {
    private val api = RetrofitClient.getInstance(context)

    fun loadAkun(idUser: String): LiveData<Resource<AkunModel>> {
        val result = MutableLiveData<Resource<AkunModel>>(Resource.Loading)
        api.tampilAkun(idUser).enqueue(object : Callback<AkunModel> {
            override fun onResponse(call: Call<AkunModel>, response: Response<AkunModel>) {
                result.postValue(
                    if (response.isSuccessful && response.body() != null)
                        Resource.Success(response.body()!!)
                    else
                        Resource.Error(ErrorHelper.parseError(response).message)
                )
            }
            override fun onFailure(call: Call<AkunModel>, t: Throwable) {
                result.postValue(Resource.Error(t.message ?: "Network error"))
            }
        })
        return result
    }

    fun updateAkun(idUser: String, nama: String, identitas: String, password: String): LiveData<Resource<AkunModel>> {
        val result = MutableLiveData<Resource<AkunModel>>(Resource.Loading)
        api.updateAkun(idUser, nama, identitas, password).enqueue(object : Callback<AkunModel> {
            override fun onResponse(call: Call<AkunModel>, response: Response<AkunModel>) {
                result.postValue(
                    if (response.isSuccessful && response.body() != null)
                        Resource.Success(response.body()!!)
                    else
                        Resource.Error(ErrorHelper.parseError(response).message)
                )
            }
            override fun onFailure(call: Call<AkunModel>, t: Throwable) {
                result.postValue(Resource.Error(t.message ?: "Network error"))
            }
        })
        return result
    }
}
