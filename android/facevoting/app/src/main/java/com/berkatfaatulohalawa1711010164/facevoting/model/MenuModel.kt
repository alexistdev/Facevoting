package com.berkatfaatulohalawa1711010164.facevoting.model

import com.google.gson.annotations.SerializedName

data class MenuModel(
    @SerializedName("id_kategori") val id_kategori: String,
    @SerializedName("nama_kategori") val nama_kategori: String,
    @SerializedName("logo_kategori") val logo_kategori: String,
    @SerializedName("status_kategori") val status_kategori: String
)
