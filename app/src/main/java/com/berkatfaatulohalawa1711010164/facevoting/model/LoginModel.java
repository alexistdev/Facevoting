package com.berkatfaatulohalawa1711010164.facevoting.model;

import com.google.gson.annotations.SerializedName;

public class LoginModel {
    @SerializedName("id_user")
    private final String idUser;
    @SerializedName("token_login")
    private final String token_login;
    @SerializedName("validasi")
    private final String validasi;
    @SerializedName("nama")
    private final String nama;
    @SerializedName("identitas")
    private final String identitas;


    public LoginModel(String idUser, String token_login, String validasi, String nama, String identitas) {
        this.idUser = idUser;
        this.token_login = token_login;
        this.validasi = validasi;
        this.nama = nama;
        this.identitas = identitas;
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

    public String getNama() {
        return nama;
    }

    public String getIdentitas() {
        return identitas;
    }
}
