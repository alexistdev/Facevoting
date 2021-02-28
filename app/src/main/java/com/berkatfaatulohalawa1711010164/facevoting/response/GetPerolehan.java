package com.berkatfaatulohalawa1711010164.facevoting.response;

import com.berkatfaatulohalawa1711010164.facevoting.model.MenuModel;
import com.berkatfaatulohalawa1711010164.facevoting.model.SuaraModel;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetPerolehan {
    @SerializedName("result")
    List<SuaraModel> listSuara;

    @SerializedName("message")
    final private String message;

    @SerializedName("status")
    final private String status;

    public GetPerolehan(List<SuaraModel> listSuara, String message, String status) {
        this.listSuara = listSuara;
        this.message = message;
        this.status = status;
    }

    public List<SuaraModel> getListSuara() {
        return listSuara;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }
}
