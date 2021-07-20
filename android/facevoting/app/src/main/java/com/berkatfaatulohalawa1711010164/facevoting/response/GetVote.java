package com.berkatfaatulohalawa1711010164.facevoting.response;

import com.berkatfaatulohalawa1711010164.facevoting.model.PaslonModel;
import com.berkatfaatulohalawa1711010164.facevoting.model.VoteModel;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetVote {
    @SerializedName("result")
    List<VoteModel> listVote;

    @SerializedName("message")
    final private String message;

    @SerializedName("status")
    final private String status;

    public GetVote(List<VoteModel> listVote, String message, String status) {
        this.listVote = listVote;
        this.message = message;
        this.status = status;
    }

    public List<VoteModel> getListVote() {
        return listVote;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }
}
