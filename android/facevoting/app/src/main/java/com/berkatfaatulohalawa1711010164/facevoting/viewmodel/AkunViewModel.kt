package com.berkatfaatulohalawa1711010164.facevoting.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.berkatfaatulohalawa1711010164.facevoting.core.Resource
import com.berkatfaatulohalawa1711010164.facevoting.model.AkunModel
import com.berkatfaatulohalawa1711010164.facevoting.repository.AkunRepository

class AkunViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = AkunRepository(application)

    private val loadTrigger = MutableLiveData<String>()
    val akunState: LiveData<Resource<AkunModel>> = loadTrigger.switchMap { idUser ->
        repo.loadAkun(idUser)
    }
    fun loadAkun(idUser: String) { loadTrigger.value = idUser }

    private val updateTrigger = MutableLiveData<Array<String>>()
    val updateState: LiveData<Resource<AkunModel>> = updateTrigger.switchMap { args ->
        repo.updateAkun(args[0], args[1], args[2], args[3])
    }
    fun updateAkun(idUser: String, nama: String, identitas: String, password: String) {
        updateTrigger.value = arrayOf(idUser, nama, identitas, password)
    }
}
