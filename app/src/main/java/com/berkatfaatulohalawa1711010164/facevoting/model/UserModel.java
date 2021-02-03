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

    public UserModel(String id_user, String email, String token_firebase, String nama, String identitas) {
        this.id_user = id_user;
        this.email = email;
        this.token_firebase = token_firebase;
        this.nama = nama;
        this.identitas = identitas;
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
}
