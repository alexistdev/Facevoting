package com.berkatfaatulohalawa1711010164.facevoting.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.berkatfaatulohalawa1711010164.facevoting.API.APIService;
import com.berkatfaatulohalawa1711010164.facevoting.API.NoConnectivityException;
import com.berkatfaatulohalawa1711010164.facevoting.R;
import com.berkatfaatulohalawa1711010164.facevoting.adapter.PaslonAdapter;
import com.berkatfaatulohalawa1711010164.facevoting.model.PaslonModel;
import com.berkatfaatulohalawa1711010164.facevoting.response.GetPaslon;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class Paslon extends AppCompatActivity {
    private RecyclerView gridView;
    private PaslonAdapter paslonAdapter;
    private List<PaslonModel> paslonModels;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paslon);

        init();
        setSupportActionBar(toolbar);
        setupRecyclerView();
        Intent iin= getIntent();
        Bundle extra = iin.getExtras();
        if(extra != null) {
            final String idKategori = extra.getString("id_kategori","0");
            /* Untuk setup toolbar */
            getPaslon(getApplicationContext(),idKategori);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Daftar Paslon");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowTitleEnabled(true);
            }
        }
    }

    private void init(){
        progressDialog = ProgressDialog.show(this, "", "Loading.....", true, false);
        gridView = findViewById(R.id.rcPaslon);
        toolbar = findViewById(R.id.toolbarmenu);
    }

    private void setupRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        paslonAdapter = new PaslonAdapter(getApplicationContext(), new ArrayList<>());
        gridView.setLayoutManager(linearLayoutManager);
        gridView.setAdapter(paslonAdapter);
    }

    private void getPaslon(Context mContext, String kategori){
        try{
            Call<GetPaslon> call= APIService.Factory.create(mContext).postPaslon(kategori);
            call.enqueue(new Callback<GetPaslon>() {
                @EverythingIsNonNull
                @Override
                public void onResponse(Call<GetPaslon> call, Response<GetPaslon> response) {
                    progressDialog.dismiss();
                    if(response.isSuccessful()){
                        if(response.body() != null){
                            paslonModels = response.body().getListPaslon();
                            paslonAdapter.replaceData(paslonModels);
                        }
                    }
                }
                @EverythingIsNonNull
                @Override
                public void onFailure(Call<GetPaslon> call, Throwable t) {
                    progressDialog.dismiss();
                    if(t instanceof NoConnectivityException) {
                        displayExceptionMessage("Offline, cek koneksi internet anda!");
                    }
                }
            });
        }catch (Exception e){
            progressDialog.dismiss();
            e.printStackTrace();
            displayExceptionMessage(e.getMessage());
        }
    }
    public void displayExceptionMessage(String msg)
    {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }
}