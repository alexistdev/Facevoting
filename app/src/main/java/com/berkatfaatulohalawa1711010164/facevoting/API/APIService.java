package com.berkatfaatulohalawa1711010164.facevoting.API;

import android.content.Context;

import com.berkatfaatulohalawa1711010164.facevoting.config.Constants;
import com.berkatfaatulohalawa1711010164.facevoting.model.LoginModel;
import com.berkatfaatulohalawa1711010164.facevoting.model.PaslonModel;
import com.berkatfaatulohalawa1711010164.facevoting.model.UserModel;
import com.berkatfaatulohalawa1711010164.facevoting.response.GetMenu;
import com.berkatfaatulohalawa1711010164.facevoting.response.GetPaslon;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface APIService {
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
    @POST("api/paslon/tampil")
    Call<GetPaslon> postPaslon(@Field("id_kategori") String id_kategori);

    //API untuk menampilkan kategori daftar pemilihan umum
    @GET("api/kategori/tampil")
    Call<GetMenu> listMenu();

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
