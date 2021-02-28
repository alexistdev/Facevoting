package com.berkatfaatulohalawa1711010164.facevoting.API;

import android.content.Context;

import com.berkatfaatulohalawa1711010164.facevoting.config.Constants;
import com.berkatfaatulohalawa1711010164.facevoting.model.AkunModel;
import com.berkatfaatulohalawa1711010164.facevoting.model.LoginModel;
import com.berkatfaatulohalawa1711010164.facevoting.model.MenuModel;
import com.berkatfaatulohalawa1711010164.facevoting.model.MessageModel;
import com.berkatfaatulohalawa1711010164.facevoting.model.PaslonModel;
import com.berkatfaatulohalawa1711010164.facevoting.model.UserModel;
import com.berkatfaatulohalawa1711010164.facevoting.response.GetMenu;
import com.berkatfaatulohalawa1711010164.facevoting.response.GetPaslon;
import com.berkatfaatulohalawa1711010164.facevoting.response.GetPerolehan;
import com.berkatfaatulohalawa1711010164.facevoting.response.GetVote;

import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIService {
    @GET("api/Suara/perolehan")
    Call<GetPerolehan> tampilSuara(@Query("id_kategori") String id_kategori);

    @GET("api/Kategori/semua")
    Call<GetMenu> tampilHasil();


    @GET("api/Suara/tampil/{id_user}")
    Call<GetVote> tampilVote(@Path("id_user") String id_user);

    @FormUrlEncoded
    @PUT("api/Akun/tampil/{id_user}")
    Call<AkunModel> updateAkun(@Path("id_user") String id_user,
                               @Field("nama") String nama,
                               @Field("identitas") String identitas,
                               @Field("password") String password);

    @GET("api/Akun/tampil")
    Call<AkunModel> tampilAKun(@Query("id_user") String id_user);



    @FormUrlEncoded
    @POST("api/Suara/vote")
    Call<MessageModel> simpanVote(@Field("id_user") String id_user,
                                  @Field("id_kategori") String id_kategori,
                                  @Field("id_paslon") String id_paslon);

    @Multipart
    @POST("api/Gambar/cek")
    Call<MessageModel> cekWajah(@Part("id_user") RequestBody id,
                                  @Part MultipartBody.Part upload);
    @Multipart
    @POST("api/Gambar/tambah")
    Call<MessageModel> rekamWajah(@Part("id_user") RequestBody id,
                                  @Part MultipartBody.Part upload);


    @FormUrlEncoded
    @POST("api/Login/sudahlogin")
    Call<LoginModel> dapatstatus(@Field("id_user") String id_user);

    @FormUrlEncoded
    @POST("api/Login/otentikasi")
    Call<LoginModel> validasiLogin(@Field("email") String email,
                               @Field("password") String password);

    //API untuk menambah data mahasiswa
    @FormUrlEncoded
    @POST("api/Daftar/tambah")
    Call<UserModel> daftarUser(@Field("nama") String nama,
                                    @Field("identitas") String identitas,
                                    @Field("email") String email,
                                    @Field("password") String password,
                                    @Field("token_firebase") String token_firebase);

    //Mendapatkan data paslon berdasarkan id kategori / ukm atau orkem
    @FormUrlEncoded
    @POST("api/Paslon/tampil")
    Call<GetPaslon> postPaslon(@Field("id_kategori") String id_kategori);

    //API untuk menampilkan kategori daftar pemilihan umum
    @GET("api/Kategori/tampil")
    Call<GetMenu> listMenu(@Query("id_user") String id_user);

    class Factory{
        public static APIService create(Context mContext){
            OkHttpClient.Builder builder = new OkHttpClient.Builder();

            builder.readTimeout(20, TimeUnit.SECONDS);
            builder.connectTimeout(20, TimeUnit.SECONDS);
            builder.writeTimeout(20, TimeUnit.SECONDS);
            builder.addInterceptor(new NetworkConnectionInterceptor(mContext));

            OkHttpClient client = builder.build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            return retrofit.create(APIService.class);
        }
    }
}
