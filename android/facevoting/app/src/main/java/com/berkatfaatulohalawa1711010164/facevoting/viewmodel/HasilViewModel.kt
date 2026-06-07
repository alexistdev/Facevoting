package com.berkatfaatulohalawa1711010164.facevoting.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.berkatfaatulohalawa1711010164.facevoting.core.Resource
import com.berkatfaatulohalawa1711010164.facevoting.repository.HasilRepository
import com.berkatfaatulohalawa1711010164.facevoting.response.GetMenu
import com.berkatfaatulohalawa1711010164.facevoting.response.GetPerolehan

class HasilViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = HasilRepository(application)

    private val hasilTrigger = MutableLiveData<Unit>()
    val hasilState: LiveData<Resource<GetMenu>> = hasilTrigger.switchMap {
        repo.loadHasil()
    }
    fun loadHasil() { hasilTrigger.value = Unit }

    private val suaraTrigger = MutableLiveData<String>()
    val suaraState: LiveData<Resource<GetPerolehan>> = suaraTrigger.switchMap { idKategori ->
        repo.loadSuara(idKategori)
    }
    fun loadSuara(idKategori: String) { suaraTrigger.value = idKategori }
}
