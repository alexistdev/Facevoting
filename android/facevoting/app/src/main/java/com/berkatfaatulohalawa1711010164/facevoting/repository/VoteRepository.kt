package com.berkatfaatulohalawa1711010164.facevoting.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.berkatfaatulohalawa1711010164.facevoting.core.ErrorHelper
import com.berkatfaatulohalawa1711010164.facevoting.core.Resource
import com.berkatfaatulohalawa1711010164.facevoting.model.MessageModel
import com.berkatfaatulohalawa1711010164.facevoting.network.NoConnectivityException
import com.berkatfaatulohalawa1711010164.facevoting.network.RetrofitClient
import com.berkatfaatulohalawa1711010164.facevoting.response.GetPaslon
import com.berkatfaatulohalawa1711010164.facevoting.response.GetVote
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VoteRepository(context: Context) {
    private val api = RetrofitClient.getInstance(context)

    fun loadVotes(idUser: String): LiveData<Resource<GetVote>> {
        val result = MutableLiveData<Resource<GetVote>>(Resource.Loading)
        api.tampilVote(idUser).enqueue(object : Callback<GetVote> {
            override fun onResponse(call: Call<GetVote>, response: Response<GetVote>) {
                result.postValue(
                    if (response.isSuccessful && response.body() != null)
                        Resource.Success(response.body()!!)
                    else
                        Resource.Error(ErrorHelper.parseError(response).message)
                )
            }
            override fun onFailure(call: Call<GetVote>, t: Throwable) {
                val msg = if (t is NoConnectivityException) "Offline, cek koneksi internet anda!" else t.message ?: "Network error"
                result.postValue(Resource.Error(msg))
            }
        })
        return result
    }

    fun loadPaslon(idKategori: String): LiveData<Resource<GetPaslon>> {
        val result = MutableLiveData<Resource<GetPaslon>>(Resource.Loading)
        api.postPaslon(idKategori).enqueue(object : Callback<GetPaslon> {
            override fun onResponse(call: Call<GetPaslon>, response: Response<GetPaslon>) {
                result.postValue(
                    if (response.isSuccessful && response.body() != null)
                        Resource.Success(response.body()!!)
                    else
                        Resource.Error(ErrorHelper.parseError(response).message)
                )
            }
            override fun onFailure(call: Call<GetPaslon>, t: Throwable) {
                val msg = if (t is NoConnectivityException) "Offline, cek koneksi internet anda!" else t.message ?: "Network error"
                result.postValue(Resource.Error(msg))
            }
        })
        return result
    }

    fun simpanVote(idUser: String, idKategori: String, idPaslon: String): LiveData<Resource<MessageModel>> {
        val result = MutableLiveData<Resource<MessageModel>>(Resource.Loading)
        api.simpanVote(idUser, idKategori, idPaslon).enqueue(object : Callback<MessageModel> {
            override fun onResponse(call: Call<MessageModel>, response: Response<MessageModel>) {
                result.postValue(
                    if (response.isSuccessful && response.body() != null)
                        Resource.Success(response.body()!!)
                    else
                        Resource.Error(ErrorHelper.parseError(response).message)
                )
            }
            override fun onFailure(call: Call<MessageModel>, t: Throwable) {
                result.postValue(Resource.Error(t.message ?: "Network error"))
            }
        })
        return result
    }
}
