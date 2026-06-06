package com.berkatfaatulohalawa1711010164.facevoting.model

import com.google.gson.annotations.SerializedName

data class AkunModel(
    @SerializedName("id_user") val id_user: String,
    @SerializedName("email") val email: String,
    @SerializedName("nama") val nama: String,
    @SerializedName("identitas") val identitas: String,
    @SerializedName("status_akun") val status_akun: String
)
