package com.berkatfaatulohalawa1711010164.facevoting.response

import com.berkatfaatulohalawa1711010164.facevoting.model.PaslonModel
import com.google.gson.annotations.SerializedName

data class GetPaslon(
    @SerializedName("result") val listPaslon: List<PaslonModel>,
    @SerializedName("message") val message: String,
    @SerializedName("status") val status: String
)
