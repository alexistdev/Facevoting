package com.berkatfaatulohalawa1711010164.facevoting.model;

import com.google.gson.annotations.SerializedName;

public class AkunModel {
    @SerializedName("id_user")
    private String id_user;
    @SerializedName("email")
    private String email;
    @SerializedName("nama")
    private String nama;
    @SerializedName("identitas")
    private String identitas;

    public AkunModel(String id_user, String email, String nama, String identitas) {
        this.id_user = id_user;
        this.email = email;
        this.nama = nama;
        this.identitas = identitas;
    }

    public String getId_user() {
        return id_user;
    }

    public String getEmail() {
        return email;
    }

    public String getNama() {
        return nama;
    }

    public String getIdentitas() {
        return identitas;
    }
}
