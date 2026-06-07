package com.berkatfaatulohalawa1711010164.facevoting.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.berkatfaatulohalawa1711010164.facevoting.core.Resource
import com.berkatfaatulohalawa1711010164.facevoting.model.MessageModel
import com.berkatfaatulohalawa1711010164.facevoting.repository.VoteRepository
import com.berkatfaatulohalawa1711010164.facevoting.response.GetPaslon
import com.berkatfaatulohalawa1711010164.facevoting.response.GetVote

class VoteViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = VoteRepository(application)

    private val voteTrigger = MutableLiveData<String>()
    val voteState: LiveData<Resource<GetVote>> = voteTrigger.switchMap { idUser ->
        repo.loadVotes(idUser)
    }
    fun loadVotes(idUser: String) { voteTrigger.value = idUser }

    private val paslonTrigger = MutableLiveData<String>()
    val paslonState: LiveData<Resource<GetPaslon>> = paslonTrigger.switchMap { idKategori ->
        repo.loadPaslon(idKategori)
    }
    fun loadPaslon(idKategori: String) { paslonTrigger.value = idKategori }

    private val simpanTrigger = MutableLiveData<Triple<String, String, String>>()
    val simpanState: LiveData<Resource<MessageModel>> = simpanTrigger.switchMap { (idUser, idKategori, idPaslon) ->
        repo.simpanVote(idUser, idKategori, idPaslon)
    }
    fun simpanVote(idUser: String, idKategori: String, idPaslon: String) {
        simpanTrigger.value = Triple(idUser, idKategori, idPaslon)
    }
}
