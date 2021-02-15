package com.berkatfaatulohalawa1711010164.facevoting.model;

import com.google.gson.annotations.SerializedName;

public class UserModel {
    @SerializedName("id_user")
    private String id_user;
    @SerializedName("email")
    private String email;
    @SerializedName("token_firebase")
    private String token_firebase;
    @SerializedName("nama")
    private String nama;
    @SerializedName("identitas")
    private String identitas;
    @SerializedName("validasi")
    private String validasi;
    @SerializedName("token_login")
    private final String token_login;

    public UserModel(String id_user, String email, String token_firebase, String nama, String identitas, String validasi, String token_login) {
        this.id_user = id_user;
        this.email = email;
        this.token_firebase = token_firebase;
        this.nama = nama;
        this.identitas = identitas;
        this.validasi = validasi;
        this.token_login = token_login;
    }

    public String getId_user() {
        return id_user;
    }

    public String getEmail() {
        return email;
    }

    public String getToken_firebase() {
        return token_firebase;
    }

    public String getNama() {
        return nama;
    }

    public String getIdentitas() {
        return identitas;
    }

    public String getValidasi() {
        return validasi;
    }

    public String getToken_login() {
        return token_login;
    }
}
