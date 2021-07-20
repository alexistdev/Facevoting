package com.berkatfaatulohalawa1711010164.facevoting.response;

import com.berkatfaatulohalawa1711010164.facevoting.model.MenuModel;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetMenu {
    @SerializedName("result")
    List<MenuModel> listMenu;

    @SerializedName("message")
    final private String message;

    @SerializedName("status")
    final private String status;

    public GetMenu(List<MenuModel> listMenu, String message, String status) {
        this.listMenu = listMenu;
        this.message = message;
        this.status = status;
    }

    public List<MenuModel> getListMenu() {
        return listMenu;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }
}
