package com.berkatfaatulohalawa1711010164.facevoting.model

import com.google.gson.annotations.SerializedName

data class PaslonModel(
    @SerializedName("id_paslon") val id_paslon: String,
    @SerializedName("id_kategori") val id_kategori: String,
    @SerializedName("judul_paslon") val judul_paslon: String,
    @SerializedName("ketua_paslon") val ketua_paslon: String,
    @SerializedName("wakil_paslon") val wakil_paslon: String,
    @SerializedName("photo1_paslon") val photo1_paslon: String,
    @SerializedName("photo2_paslon") val photo2_paslon: String,
    @SerializedName("visi_misi") val visi_misi: String,
    @SerializedName("profil_catum") val profil_catum: String,
    @SerializedName("profil_cawatum") val profil_cawatum: String
)
