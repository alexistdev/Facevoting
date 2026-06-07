package com.berkatfaatulohalawa1711010164.facevoting.network

import com.berkatfaatulohalawa1711010164.facevoting.model.AkunModel
import com.berkatfaatulohalawa1711010164.facevoting.model.LoginModel
import com.berkatfaatulohalawa1711010164.facevoting.model.MessageModel
import com.berkatfaatulohalawa1711010164.facevoting.model.UserModel
import com.berkatfaatulohalawa1711010164.facevoting.response.GetMenu
import com.berkatfaatulohalawa1711010164.facevoting.response.GetPaslon
import com.berkatfaatulohalawa1711010164.facevoting.response.GetPerolehan
import com.berkatfaatulohalawa1711010164.facevoting.response.GetVote
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface APIService {
    @GET("api/Login/cekstatus")
    fun cekStatus(@Query("id_user") idUser: String): Call<LoginModel>

    @GET("api/Suara/perolehan")
    fun tampilSuara(@Query("id_kategori") idKategori: String): Call<GetPerolehan>

    @GET("api/Kategori/semua")
    fun tampilHasil(): Call<GetMenu>

    @GET("api/Suara/tampil/{id_user}")
    fun tampilVote(@Path("id_user") idUser: String): Call<GetVote>

    @FormUrlEncoded
    @PUT("api/Akun/tampil/{id_user}")
    fun updateAkun(
        @Path("id_user") idUser: String,
        @Field("nama") nama: String,
        @Field("identitas") identitas: String,
        @Field("password") password: String
    ): Call<AkunModel>

    @GET("api/Akun/tampil")
    fun tampilAkun(@Query("id_user") idUser: String): Call<AkunModel>

    @FormUrlEncoded
    @POST("api/Suara/vote")
    fun simpanVote(
        @Field("id_user") idUser: String,
        @Field("id_kategori") idKategori: String,
        @Field("id_paslon") idPaslon: String
    ): Call<MessageModel>

    @Multipart
    @POST("api/Gambar/cek")
    fun cekWajah(
        @Part("id_user") id: RequestBody,
        @Part upload: MultipartBody.Part
    ): Call<MessageModel>

    @Multipart
    @POST("api/Gambar/tambah")
    fun rekamWajah(
        @Part("id_user") id: RequestBody,
        @Part upload: MultipartBody.Part
    ): Call<MessageModel>

    @FormUrlEncoded
    @POST("api/Login/sudahlogin")
    fun dapatStatus(@Field("id_user") idUser: String): Call<LoginModel>

    @FormUrlEncoded
    @POST("api/Login/otentikasi")
    fun validasiLogin(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginModel>

    @FormUrlEncoded
    @POST("api/Daftar/tambah")
    fun daftarUser(
        @Field("nama") nama: String,
        @Field("identitas") identitas: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("token_firebase") tokenFirebase: String
    ): Call<UserModel>

    @FormUrlEncoded
    @POST("api/Paslon/tampil")
    fun postPaslon(@Field("id_kategori") idKategori: String): Call<GetPaslon>

    @GET("api/Kategori/tampil")
    fun listMenu(@Query("id_user") idUser: String): Call<GetMenu>
}
