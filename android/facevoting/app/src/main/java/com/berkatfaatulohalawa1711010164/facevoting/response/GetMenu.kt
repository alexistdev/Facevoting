package com.berkatfaatulohalawa1711010164.facevoting.response

import com.berkatfaatulohalawa1711010164.facevoting.model.MenuModel
import com.google.gson.annotations.SerializedName

data class GetMenu(
    @SerializedName("result") val listMenu: List<MenuModel>,
    @SerializedName("message") val message: String,
    @SerializedName("status") val status: String
)
