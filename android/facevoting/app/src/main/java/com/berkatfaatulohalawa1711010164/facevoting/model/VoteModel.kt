package com.berkatfaatulohalawa1711010164.facevoting.model

import com.google.gson.annotations.SerializedName

data class VoteModel(
    @SerializedName("id_voting") val id_voting: String,
    @SerializedName("tanggal_voting") val tanggal_voting: String,
    @SerializedName("nama") val nama: String,
    @SerializedName("nama_kategori") val nama_kategori: String
)
