package com.berkatfaatulohalawa1711010164.facevoting.response

import com.berkatfaatulohalawa1711010164.facevoting.model.SuaraModel
import com.google.gson.annotations.SerializedName

data class GetPerolehan(
    @SerializedName("result") val listSuara: List<SuaraModel>,
    @SerializedName("message") val message: String,
    @SerializedName("status") val status: String
)
