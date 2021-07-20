package com.berkatfaatulohalawa1711010164.facevoting.response;

import com.berkatfaatulohalawa1711010164.facevoting.model.MenuModel;
import com.berkatfaatulohalawa1711010164.facevoting.model.PaslonModel;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetPaslon {
    @SerializedName("result")
    List<PaslonModel> listPaslon;

    @SerializedName("message")
    final private String message;

    @SerializedName("status")
    final private String status;

    public GetPaslon(List<PaslonModel> listPaslon, String message, String status) {
        this.listPaslon = listPaslon;
        this.message = message;
        this.status = status;
    }

    public List<PaslonModel> getListPaslon() {
        return listPaslon;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }
}
