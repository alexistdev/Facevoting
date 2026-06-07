package com.berkatfaatulohalawa1711010164.facevoting.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.berkatfaatulohalawa1711010164.facevoting.core.Resource
import com.berkatfaatulohalawa1711010164.facevoting.model.LoginModel
import com.berkatfaatulohalawa1711010164.facevoting.repository.HomeRepository
import com.berkatfaatulohalawa1711010164.facevoting.response.GetMenu

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = HomeRepository(application)

    private val identitasTrigger = MutableLiveData<String>()
    val identitasState: LiveData<Resource<LoginModel>> = identitasTrigger.switchMap { idUser ->
        repo.loadIdentitas(idUser)
    }
    fun loadIdentitas(idUser: String) { identitasTrigger.value = idUser }

    private val menuTrigger = MutableLiveData<String>()
    val menuState: LiveData<Resource<GetMenu>> = menuTrigger.switchMap { idUser ->
        repo.loadMenu(idUser)
    }
    fun loadMenu(idUser: String) { menuTrigger.value = idUser }
}
