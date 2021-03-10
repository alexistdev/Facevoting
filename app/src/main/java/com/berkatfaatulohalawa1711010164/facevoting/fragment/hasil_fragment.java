package com.berkatfaatulohalawa1711010164.facevoting.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.berkatfaatulohalawa1711010164.facevoting.API.APIService;
import com.berkatfaatulohalawa1711010164.facevoting.API.NoConnectivityException;
import com.berkatfaatulohalawa1711010164.facevoting.R;
import com.berkatfaatulohalawa1711010164.facevoting.adapter.HasilAdapter;
import com.berkatfaatulohalawa1711010164.facevoting.adapter.VoteAdapter;
import com.berkatfaatulohalawa1711010164.facevoting.model.MenuModel;
import com.berkatfaatulohalawa1711010164.facevoting.response.GetMenu;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class hasil_fragment extends Fragment {
    private RecyclerView hasilView;
    private HasilAdapter hasilAdapter;
    private List<MenuModel> daftarMenu;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;
    private Context mContext;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hasil_fragment, container, false);
        dataInit(view);
        if(getActivity() != null){
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.setSupportActionBar(toolbar);
            toolbar.setTitle("Hasil Pemilu");
        }
        setupRecyclerView();
        setData(getContext());

        return view;
    }
    public void setData(Context mContext) {
        tampilLoading();
        try{
            Call<GetMenu> call = APIService.Factory.create(mContext).tampilHasil();
            call.enqueue(new Callback<GetMenu>() {
                @Override
                public void onResponse(Call<GetMenu> call, Response<GetMenu> response) {
                    hideLoading();
                    if(response.isSuccessful()){
                        daftarMenu = response.body().getListMenu();
                        hasilAdapter.replaceData(daftarMenu);
                    }
                }

                @Override
                public void onFailure(Call<GetMenu> call, Throwable t) {
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

    private void dataInit(View mview){
        toolbar = mview.findViewById(R.id.toolbarHasil);
        hasilView = mview.findViewById(R.id.rcHasil);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading.....");
    }

    private void setupRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext()){
            @Override
            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        };

        hasilAdapter = new HasilAdapter(getContext(),new ArrayList<>());
        hasilView.setLayoutManager(linearLayoutManager);
        hasilView.setAdapter(hasilAdapter);
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
        Toast.makeText(getContext(), pesan, Toast.LENGTH_LONG).show();
    }



}