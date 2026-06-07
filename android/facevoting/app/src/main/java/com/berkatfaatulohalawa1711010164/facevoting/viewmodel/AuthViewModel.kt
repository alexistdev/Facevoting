package com.berkatfaatulohalawa1711010164.facevoting.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.berkatfaatulohalawa1711010164.facevoting.core.Resource
import com.berkatfaatulohalawa1711010164.facevoting.model.LoginModel
import com.berkatfaatulohalawa1711010164.facevoting.model.UserModel
import com.berkatfaatulohalawa1711010164.facevoting.repository.AuthRepository

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = AuthRepository(application)

    // Login
    private val loginTrigger = MutableLiveData<Pair<String, String>>()
    val loginState: LiveData<Resource<LoginModel>> = loginTrigger.switchMap { (email, password) ->
        repo.login(email, password)
    }
    fun login(email: String, password: String) { loginTrigger.value = Pair(email, password) }

    // Register
    private val registerTrigger = MutableLiveData<Array<String>>()
    val registerState: LiveData<Resource<UserModel>> = registerTrigger.switchMap { args ->
        repo.register(args[0], args[1], args[2], args[3], args[4])
    }
    fun register(nama: String, identitas: String, email: String, password: String, token: String) {
        registerTrigger.value = arrayOf(nama, identitas, email, password, token)
    }

    // Check status (Checkpoint, SplashActivity)
    private val checkStatusTrigger = MutableLiveData<String>()
    val checkStatusState: LiveData<Resource<LoginModel>> = checkStatusTrigger.switchMap { idUser ->
        repo.checkStatus(idUser)
    }
    fun checkStatus(idUser: String) { checkStatusTrigger.value = idUser }

    // Refresh status (SplashActivity background sync)
    private val refreshStatusTrigger = MutableLiveData<String>()
    val refreshStatusState: LiveData<Resource<LoginModel>> = refreshStatusTrigger.switchMap { idUser ->
        repo.refreshStatus(idUser)
    }
    fun refreshStatus(idUser: String) { refreshStatusTrigger.value = idUser }
}
