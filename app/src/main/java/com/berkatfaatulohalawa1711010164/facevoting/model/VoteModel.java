package com.berkatfaatulohalawa1711010164.facevoting.model;

import com.google.gson.annotations.SerializedName;

public class VoteModel {
    @SerializedName("id_voting")
    private String id_voting;
    @SerializedName("tanggal_voting")
    private String tanggal_voting;
    @SerializedName("nama")
    private String nama;
    @SerializedName("nama_kategori")
    private String nama_kategori;

    public VoteModel(String id_voting, String tanggal_voting, String nama, String nama_kategori) {
        this.id_voting = id_voting;
        this.tanggal_voting = tanggal_voting;
        this.nama = nama;
        this.nama_kategori = nama_kategori;
    }

    public String getId_voting() {
        return id_voting;
    }

    public String getTanggal_voting() {
        return tanggal_voting;
    }

    public String getNama() {
        return nama;
    }

    public String getNama_kategori() {
        return nama_kategori;
    }
}
