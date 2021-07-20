package com.berkatfaatulohalawa1711010164.facevoting.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.berkatfaatulohalawa1711010164.facevoting.API.APIService;
import com.berkatfaatulohalawa1711010164.facevoting.API.NoConnectivityException;
import com.berkatfaatulohalawa1711010164.facevoting.R;
import com.berkatfaatulohalawa1711010164.facevoting.adapter.HasilAdapter;
import com.berkatfaatulohalawa1711010164.facevoting.adapter.SuaraAdapter;
import com.berkatfaatulohalawa1711010164.facevoting.model.MenuModel;
import com.berkatfaatulohalawa1711010164.facevoting.model.SuaraModel;
import com.berkatfaatulohalawa1711010164.facevoting.response.GetMenu;
import com.berkatfaatulohalawa1711010164.facevoting.response.GetPerolehan;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Detailhasil extends AppCompatActivity {
    private RecyclerView suaraView;
    private ProgressDialog progressDialog;
    private Context mContext;
    private SuaraAdapter suaraAdapter;
    private List<SuaraModel> suaraMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailhasil);
        Toolbar toolbar = findViewById(R.id.tbtoolbar);
        setSupportActionBar(toolbar);
        dataInit();
        setupRecyclerView();

        if (getSupportActionBar() != null) {
            setTitle("Detail Hasil Perolehan Suara");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
        Intent iin= getIntent();
        Bundle extra = iin.getExtras();
        if(extra != null) {
            final String idKategori = extra.getString("id_kategori","0");
            setData(this,idKategori);
        }
    }

    public void setData(Context mContext,String idKategori){
        tampilLoading();
        try{
            Call<GetPerolehan> call = APIService.Factory.create(mContext).tampilSuara(idKategori);
            call.enqueue(new Callback<GetPerolehan>() {
                @Override
                public void onResponse(Call<GetPerolehan> call, Response<GetPerolehan> response) {
                    hideLoading();
                    if(response.isSuccessful()){
                        suaraMenu = response.body().getListSuara();
                        suaraAdapter.replaceData(suaraMenu);
                    }
                }

                @Override
                public void onFailure(Call<GetPerolehan> call, Throwable t) {
                    hideLoading();
                    if(t instanceof NoConnectivityException) {
                        tampilPesan("Offline, cek koneksi internet anda!");
                    }
                }
            });


        }catch (Exception e){
            hideLoading();
            e.printStackTrace();
            tampilPesan(e.getMessage());
        }
    }

    private void dataInit(){
        suaraView = findViewById(R.id.rcSuara);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading.....");
    }

    private void setupRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this){
            @Override
            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        };

        suaraAdapter = new SuaraAdapter(this,new ArrayList<>());
        suaraView.setLayoutManager(linearLayoutManager);
        suaraView.setAdapter(suaraAdapter);
    }

    /* Menampilkan Loading */
    private void tampilLoading(){
        if(!progressDialog.isShowing()){
            progressDialog.show();
        }
    }

    /* Menghilangkan Loading */
    private void hideLoading(){
        if(progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    /* Menampilkan pesan notifikasi */
    public void tampilPesan(String pesan)
    {
        Toast.makeText(this, pesan, Toast.LENGTH_LONG).show();
    }
}