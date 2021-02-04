package com.berkatfaatulohalawa1711010164.facevoting.model;

import com.google.gson.annotations.SerializedName;

public class LoginModel {
    @SerializedName("id_user")
    private final String idUser;
    @SerializedName("token_login")
    private final String token_login;
    @SerializedName("validasi")
    private final String validasi;

    public LoginModel(String idUser, String token_login, String validasi) {
        this.idUser = idUser;
        this.token_login = token_login;
        this.validasi = validasi;
    }

    public String getIdUser() {
        return idUser;
    }

    public String getToken_login() {
        return token_login;
    }

    public String getValidasi() {
        return validasi;
    }
}
