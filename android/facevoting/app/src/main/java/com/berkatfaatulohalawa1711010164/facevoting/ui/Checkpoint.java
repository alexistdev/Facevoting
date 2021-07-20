package com.berkatfaatulohalawa1711010164.facevoting.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.berkatfaatulohalawa1711010164.facevoting.API.APIService;
import com.berkatfaatulohalawa1711010164.facevoting.API.NoConnectivityException;
import com.berkatfaatulohalawa1711010164.facevoting.MainActivity;
import com.berkatfaatulohalawa1711010164.facevoting.R;
import com.berkatfaatulohalawa1711010164.facevoting.config.Constants;
import com.berkatfaatulohalawa1711010164.facevoting.helper.ErrorHelper;
import com.berkatfaatulohalawa1711010164.facevoting.helper.SessionHelper;
import com.berkatfaatulohalawa1711010164.facevoting.model.ErrorModel;
import com.berkatfaatulohalawa1711010164.facevoting.model.LoginModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class Checkpoint extends AppCompatActivity {
    private ImageView btnLanjut,btnRekam;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkpoint);
        init();
        if(SessionHelper.sudahrekam(this)){
            btnRekam.setVisibility(View.INVISIBLE);
            btnLanjut.setVisibility(View.VISIBLE);
        } else {
            //tombol lanjut dihilangkan, tombol rekam dimunculkan
            btnLanjut.setVisibility(View.INVISIBLE);
            btnRekam.setVisibility(View.VISIBLE);
        }
        btnLanjut.setOnClickListener(v -> check_status());
        btnRekam.setOnClickListener(v -> {
            Intent intent = new Intent(Checkpoint.this, Rekam.class);
            startActivity(intent);
            finish();
        });
    }

    private void init()
    {
        btnLanjut = findViewById(R.id.btnLanjutkan);
        btnRekam = findViewById(R.id.btnRekam);
        btnRekam.setVisibility(View.INVISIBLE);
        btnLanjut.setVisibility(View.INVISIBLE);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading.....");
    }

    public void check_status()
    {
        showDialog();
        try{
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(
                    Constants.USER_KEY, Context.MODE_PRIVATE);
            String idUser = sharedPreferences.getString("id_user", "");
            Call<LoginModel> call= APIService.Factory.create(this).cekStatus(idUser);
            call.enqueue(new Callback<LoginModel>() {
                @EverythingIsNonNull
                @Override
                public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                    hideDialog();
                    if(response.isSuccessful()) {
                        if (response.body() != null) {
                            if (SessionHelper.login(Checkpoint.this, idUser, response.body().getToken_login(),response.body().getValidasi(),response.body().getNama(),response.body().getIdentitas())){
                                Intent intent = new Intent(Checkpoint.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    } else {
                        ErrorModel error = ErrorHelper.parseError(response);
                        tampilPesan(error.message());
                    }
                }
                @EverythingIsNonNull
                @Override
                public void onFailure(Call<LoginModel> call, Throwable t) {
                    hideDialog();
                    if(t instanceof NoConnectivityException) {
                        tampilPesan("Offline, cek koneksi internet anda!");
                    }
                }
            });
        }catch (Exception e){
            hideDialog();
            e.printStackTrace();
            tampilPesan(e.getMessage());
        }
    }

    private void showDialog(){
        if(!pDialog.isShowing()){
            pDialog.show();
        }
    }

    private void hideDialog(){
        if(pDialog.isShowing()){
            pDialog.dismiss();
        }
    }

    public void tampilPesan(String msg)
    {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}