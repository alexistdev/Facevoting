package com.berkatfaatulohalawa1711010164.facevoting.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.berkatfaatulohalawa1711010164.facevoting.core.ErrorHelper
import com.berkatfaatulohalawa1711010164.facevoting.core.Resource
import com.berkatfaatulohalawa1711010164.facevoting.model.MessageModel
import com.berkatfaatulohalawa1711010164.facevoting.network.RetrofitClient
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FaceRepository(context: Context) {
    private val api = RetrofitClient.getInstance(context)

    fun rekamWajah(idUser: RequestBody, filePart: MultipartBody.Part): LiveData<Resource<MessageModel>> {
        val result = MutableLiveData<Resource<MessageModel>>(Resource.Loading)
        api.rekamWajah(idUser, filePart).enqueue(object : Callback<MessageModel> {
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

    fun cekWajah(idUser: RequestBody, filePart: MultipartBody.Part): LiveData<Resource<MessageModel>> {
        val result = MutableLiveData<Resource<MessageModel>>(Resource.Loading)
        api.cekWajah(idUser, filePart).enqueue(object : Callback<MessageModel> {
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
