package com.berkatfaatulohalawa1711010164.facevoting.model;

import com.google.gson.annotations.SerializedName;

public class MenuModel {
    @SerializedName("id_kategori")
    private final String id_kategori;
    @SerializedName("Nama_kategori")
    private final String Nama_kategori;
    @SerializedName("logo_kategori")
    private final String logo_kategori;
    @SerializedName("status_kategori")
    private final String status_kategori;

    public MenuModel(String id_kategori, String nama_kategori, String logo_kategori, String status_kategori) {
        this.id_kategori = id_kategori;
        this.Nama_kategori = nama_kategori;
        this.logo_kategori = logo_kategori;
        this.status_kategori = status_kategori;
    }

    public String getId_kategori() {
        return id_kategori;
    }

    public String getNama_kategori() {
        return Nama_kategori;
    }

    public String getLogo_kategori() {
        return logo_kategori;
    }

    public String getStatus_kategori() {
        return status_kategori;
    }
}
