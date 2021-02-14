package com.berkatfaatulohalawa1711010164.facevoting.model;

import com.google.gson.annotations.SerializedName;

public class PaslonModel {
    @SerializedName("id_paslon")
    private final String id_paslon;
    @SerializedName("id_kategori")
    private final String id_kategori;
    @SerializedName("judul_paslon")
    private final String judul_paslon;
    @SerializedName("ketua_paslon")
    private final String ketua_paslon;
    @SerializedName("wakil_paslon")
    private final String wakil_paslon;
    @SerializedName("photo1_paslon")
    private final String photo1_paslon;
    @SerializedName("photo2_paslon")
    private final String photo2_paslon;
    @SerializedName("visi_misi")
    private final String visi_misi;
    @SerializedName("profil_catum")
    private final String profil_catum;
    @SerializedName("profil_cawatum")
    private final String profil_cawatum;


    public PaslonModel(String id_paslon, String id_kategori, String judul_paslon, String ketua_paslon, String wakil_paslon, String photo1_paslon, String photo2_paslon, String visi_misi, String profil_catum, String profil_cawatum) {
        this.id_paslon = id_paslon;
        this.id_kategori = id_kategori;
        this.judul_paslon = judul_paslon;
        this.ketua_paslon = ketua_paslon;
        this.wakil_paslon = wakil_paslon;
        this.photo1_paslon = photo1_paslon;
        this.photo2_paslon = photo2_paslon;
        this.visi_misi = visi_misi;
        this.profil_catum = profil_catum;
        this.profil_cawatum = profil_cawatum;
    }




}
