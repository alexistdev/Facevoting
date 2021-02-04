package com.berkatfaatulohalawa1711010164.facevoting.ui;

import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.berkatfaatulohalawa1711010164.facevoting.API.APIService;
import com.berkatfaatulohalawa1711010164.facevoting.MainActivity;
import com.berkatfaatulohalawa1711010164.facevoting.R;
import com.berkatfaatulohalawa1711010164.facevoting.helper.ErrorHelper;
import com.berkatfaatulohalawa1711010164.facevoting.helper.SessionHelper;
import com.berkatfaatulohalawa1711010164.facevoting.model.ErrorModel;
import com.berkatfaatulohalawa1711010164.facevoting.model.LoginModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class Login extends AppCompatActivity {
    private EditText txtEmail,txtPassword;
    private ImageView btnLogin;
    private TextView btnDaftar;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();

        if(SessionHelper.sudahLogin(this)){
            if(SessionHelper.sudahValidasi(this)){
                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else{
                Intent intent = new Intent(Login.this, Checkpoint.class);
                startActivity(intent);
                finish();
            }
        }
        btnDaftar.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, Daftar.class);
            startActivity(intent);
        });
        btnLogin.setOnClickListener(v -> {
            String email = txtEmail.getText().toString();
            String password = txtPassword.getText().toString();
            if(email.trim().length()> 0 && password.trim().length() >0){
                cek_login(email,password);
            } else {
                tampilPesan("Semua kolom harus diisi!");
            }
        });

    }
    private void cek_login(final String email, final String password)
    {
        tampilLoading();
        try{
            Call<LoginModel> call = APIService.Factory.create(getApplicationContext()).validasiLogin(email,password);
            call.enqueue(new Callback<LoginModel>() {
                @EverythingIsNonNull
                @Override
                public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                    hideLoading();
                    if(response.isSuccessful()){
                        if(response.body() != null){
                            if (SessionHelper.login(Login.this, response.body().getIdUser(), response.body().getToken_login(),response.body().getValidasi())){
                                Intent intent;
                                if(response.body().getValidasi().equals("2")){
                                    intent = new Intent(Login.this, Checkpoint.class);
                                } else {
                                    intent = new Intent(Login.this, MainActivity.class);
                                }
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
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
                    hideLoading();
                    tampilPesan(t.getMessage());
                }
            });
        }catch (Exception e){
            hideLoading();
            e.printStackTrace();
            tampilPesan(e.getMessage());
        }
    }
    public void init()
    {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading.....");
        btnLogin =findViewById(R.id.btn_login);
        btnDaftar = findViewById(R.id.tbl_daftar);
        txtEmail = findViewById(R.id.ed_email);
        txtPassword = findViewById(R.id.ed_password);
    }

    private void tampilLoading(){
        if(!progressDialog.isShowing()){
            progressDialog.show();
        }
    }

    private void hideLoading(){
        if(progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    public void tampilPesan(String pesan)
    {
        Toast.makeText(getApplicationContext(), pesan, Toast.LENGTH_LONG).show();
    }
}