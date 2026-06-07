package com.berkatfaatulohalawa1711010164.facevoting.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.berkatfaatulohalawa1711010164.facevoting.core.Resource
import com.berkatfaatulohalawa1711010164.facevoting.model.MessageModel
import com.berkatfaatulohalawa1711010164.facevoting.repository.FaceRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class FaceViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = FaceRepository(application)

    private val rekamTrigger = MutableLiveData<Pair<RequestBody, MultipartBody.Part>>()
    val rekamState: LiveData<Resource<MessageModel>> = rekamTrigger.switchMap { (idUser, filePart) ->
        repo.rekamWajah(idUser, filePart)
    }
    fun rekamWajah(idUser: RequestBody, filePart: MultipartBody.Part) {
        rekamTrigger.value = Pair(idUser, filePart)
    }

    private val cekTrigger = MutableLiveData<Pair<RequestBody, MultipartBody.Part>>()
    val cekState: LiveData<Resource<MessageModel>> = cekTrigger.switchMap { (idUser, filePart) ->
        repo.cekWajah(idUser, filePart)
    }
    fun cekWajah(idUser: RequestBody, filePart: MultipartBody.Part) {
        cekTrigger.value = Pair(idUser, filePart)
    }
}
