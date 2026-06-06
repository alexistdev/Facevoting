package com.berkatfaatulohalawa1711010164.facevoting.model

import com.google.gson.annotations.SerializedName

data class LoginModel(
    @SerializedName("id_user") val idUser: String,
    @SerializedName("token_login") val token_login: String,
    @SerializedName("validasi") val validasi: String,
    @SerializedName("nama") val nama: String,
    @SerializedName("identitas") val identitas: String
)
