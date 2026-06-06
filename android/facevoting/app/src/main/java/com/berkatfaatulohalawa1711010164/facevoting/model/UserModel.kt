package com.berkatfaatulohalawa1711010164.facevoting.model

import com.google.gson.annotations.SerializedName

data class UserModel(
    @SerializedName("id_user") val id_user: String,
    @SerializedName("email") val email: String,
    @SerializedName("token_firebase") val token_firebase: String,
    @SerializedName("nama") val nama: String,
    @SerializedName("identitas") val identitas: String,
    @SerializedName("validasi") val validasi: String,
    @SerializedName("token_login") val token_login: String
)
