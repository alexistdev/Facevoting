package com.berkatfaatulohalawa1711010164.facevoting.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.berkatfaatulohalawa1711010164.facevoting.API.APIService;
import com.berkatfaatulohalawa1711010164.facevoting.API.NoConnectivityException;
import com.berkatfaatulohalawa1711010164.facevoting.MainActivity;
import com.berkatfaatulohalawa1711010164.facevoting.R;
import com.berkatfaatulohalawa1711010164.facevoting.config.Constants;
import com.berkatfaatulohalawa1711010164.facevoting.helper.MyFirebaseMessagingService;
import com.berkatfaatulohalawa1711010164.facevoting.helper.SessionHelper;
import com.berkatfaatulohalawa1711010164.facevoting.model.LoginModel;
import com.berkatfaatulohalawa1711010164.facevoting.response.GetMenu;
import com.google.firebase.iid.FirebaseInstanceId;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(
                Constants.USER_KEY, Context.MODE_PRIVATE);

        if(SessionHelper.sudahLogin(this)){



            String idUser = sharedPreferences.getString("id_user", "");
            get_status(idUser);
            String valds = sharedPreferences.getString("validasi", "");
            if(valds.equals("2")){
                Intent intent = new Intent(SplashActivity.this, Checkpoint.class);
                startActivity(intent);
                finish();
            }else{
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }

        TextView btnStart = findViewById(R.id.tombol_start);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SplashActivity.this, preLogin1.class);
                startActivity(intent);
            }
        });
    }

    private void get_status(String idUser)
    {
        try{
            Call<LoginModel> call = APIService.Factory.create(getApplicationContext()).dapatstatus(idUser);
            call.enqueue(new Callback<LoginModel>() {
                @Override
                public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                    if(response.isSuccessful()) {
                        if (response.body() != null) {
                            SessionHelper.login(SplashActivity.this, response.body().getIdUser(), response.body().getToken_login(),response.body().getValidasi(),response.body().getNama(),response.body().getIdentitas());
                        }
                    }
                }

                @Override
                public void onFailure(Call<LoginModel> call, Throwable t) {
                    if(t instanceof NoConnectivityException) {
                        displayExceptionMessage("Offline, cek koneksi internet anda!");
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            displayExceptionMessage(e.getMessage());
        }

    }

    @Override
    public void onResume(){
        super.onResume();
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(
                Constants.USER_KEY, Context.MODE_PRIVATE);
        String valds = sharedPreferences.getString("validasi", "");
        if(SessionHelper.sudahLogin(this)){
            if(valds.equals("2")){
                Intent intent = new Intent(SplashActivity.this, Checkpoint.class);
                startActivity(intent);
                finish();
            }else{
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    public void displayExceptionMessage(String msg)
    {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }
}