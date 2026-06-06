package com.berkatfaatulohalawa1711010164.facevoting.response

import com.berkatfaatulohalawa1711010164.facevoting.model.VoteModel
import com.google.gson.annotations.SerializedName

data class GetVote(
    @SerializedName("result") val listVote: List<VoteModel>,
    @SerializedName("message") val message: String,
    @SerializedName("status") val status: String
)
