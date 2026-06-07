package com.berkatfaatulohalawa1711010164.facevoting.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.berkatfaatulohalawa1711010164.facevoting.core.ErrorHelper
import com.berkatfaatulohalawa1711010164.facevoting.core.Resource
import com.berkatfaatulohalawa1711010164.facevoting.model.LoginModel
import com.berkatfaatulohalawa1711010164.facevoting.model.UserModel
import com.berkatfaatulohalawa1711010164.facevoting.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthRepository(context: Context) {
    private val api = RetrofitClient.getInstance(context)

    fun login(email: String, password: String): LiveData<Resource<LoginModel>> {
        val result = MutableLiveData<Resource<LoginModel>>(Resource.Loading)
        api.validasiLogin(email, password).enqueue(object : Callback<LoginModel> {
            override fun onResponse(call: Call<LoginModel>, response: Response<LoginModel>) {
                result.postValue(
                    if (response.isSuccessful && response.body() != null)
                        Resource.Success(response.body()!!)
                    else
                        Resource.Error(ErrorHelper.parseError(response).message)
                )
            }
            override fun onFailure(call: Call<LoginModel>, t: Throwable) {
                result.postValue(Resource.Error(t.message ?: "Network error"))
            }
        })
        return result
    }

    fun register(nama: String, identitas: String, email: String, password: String, tokenFirebase: String): LiveData<Resource<UserModel>> {
        val result = MutableLiveData<Resource<UserModel>>(Resource.Loading)
        api.daftarUser(nama, identitas, email, password, tokenFirebase).enqueue(object : Callback<UserModel> {
            override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                result.postValue(
                    if (response.isSuccessful && response.body() != null)
                        Resource.Success(response.body()!!)
                    else
                        Resource.Error(ErrorHelper.parseError(response).message)
                )
            }
            override fun onFailure(call: Call<UserModel>, t: Throwable) {
                result.postValue(Resource.Error(t.message ?: "Network error"))
            }
        })
        return result
    }

    fun checkStatus(idUser: String): LiveData<Resource<LoginModel>> {
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
                result.postValue(Resource.Error(t.message ?: "Network error"))
            }
        })
        return result
    }

    fun refreshStatus(idUser: String): LiveData<Resource<LoginModel>> {
        val result = MutableLiveData<Resource<LoginModel>>(Resource.Loading)
        api.dapatStatus(idUser).enqueue(object : Callback<LoginModel> {
            override fun onResponse(call: Call<LoginModel>, response: Response<LoginModel>) {
                result.postValue(
                    if (response.isSuccessful && response.body() != null)
                        Resource.Success(response.body()!!)
                    else
                        Resource.Error(ErrorHelper.parseError(response).message)
                )
            }
            override fun onFailure(call: Call<LoginModel>, t: Throwable) {
                result.postValue(Resource.Error(t.message ?: "Network error"))
            }
        })
        return result
    }
}
