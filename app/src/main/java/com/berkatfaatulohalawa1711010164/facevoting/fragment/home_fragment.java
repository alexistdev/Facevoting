package com.berkatfaatulohalawa1711010164.facevoting.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.berkatfaatulohalawa1711010164.facevoting.API.APIService;
import com.berkatfaatulohalawa1711010164.facevoting.API.NoConnectivityException;
import com.berkatfaatulohalawa1711010164.facevoting.R;
import com.berkatfaatulohalawa1711010164.facevoting.adapter.MenuAdapter;
import com.berkatfaatulohalawa1711010164.facevoting.config.Constants;
import com.berkatfaatulohalawa1711010164.facevoting.model.MenuModel;
import com.berkatfaatulohalawa1711010164.facevoting.response.GetMenu;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;


public class home_fragment extends Fragment {
    private RecyclerView gridMenu;
    private MenuAdapter menuAdapter;
    private List<MenuModel> daftarMenu;
    private ProgressDialog progressDialog;
    private Context mContext;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mNamaUser, mIdentitasUser;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mview = inflater.inflate(R.layout.fragment_home, container, false);
        mContext = getContext();

        dataInit(mview);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                Constants.USER_KEY, Context.MODE_PRIVATE);
        String namaUser = sharedPreferences.getString("nama_user", "");
        String identitas = sharedPreferences.getString("identitas", "");
        mNamaUser.setText(namaUser);
        mIdentitasUser.setText("Identitas : " + identitas);
        setupRecyclerView();
        refresh(mContext);

        mSwipeRefreshLayout.setOnRefreshListener(() -> new Handler(Looper.getMainLooper()).postDelayed(() -> {
            mSwipeRefreshLayout.setRefreshing(false);
            refresh(getContext());
            menuAdapter.notifyDataSetChanged();
        }, 500));
        return mview;
    }
    private void dataInit(View mview){
        mNamaUser = mview.findViewById(R.id.txtNama);
        mIdentitasUser = mview.findViewById(R.id.txtIdentitas);
        gridMenu = mview.findViewById(R.id.rcMenu);
        mSwipeRefreshLayout = mview.findViewById(R.id.refresh);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading.....");
    }

    @Override
    public void onResume() {
        super.onResume();
        setupRecyclerView();
        refresh(mContext);
        menuAdapter.notifyDataSetChanged();
    }

    public void refresh(Context mContext) {
        try {
            tampilLoading();
            SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(
                    Constants.USER_KEY, Context.MODE_PRIVATE);
            String idUser = sharedPreferences.getString("id_user", "");
            Call<GetMenu> call = APIService.Factory.create(mContext).listMenu(idUser);
            call.enqueue(new Callback<GetMenu>() {
                @EverythingIsNonNull
                @Override
                public void onResponse(Call<GetMenu> call, Response<GetMenu> response) {
                    hideLoading();
                    if(response.isSuccessful()){
                        try {
                            if(response.body() != null){
                                if(response.body().getStatus().equals("failed")){
                                    progressDialog.dismiss();
                                } else {
                                    progressDialog.dismiss();
                                    daftarMenu = response.body().getListMenu();
                                    menuAdapter.replaceData(daftarMenu);
                                }
                            }
                        } catch (Exception e){
                            progressDialog.dismiss();
                            e.printStackTrace();
                        }
                    }
                }
                @EverythingIsNonNull
                @Override
                public void onFailure(Call<GetMenu> call, Throwable t) {
                    hideLoading();
                    if(t instanceof NoConnectivityException) {
                        displayExceptionMessage("Offline, cek koneksi internet anda!");
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            displayExceptionMessage(e.getMessage());
        }
    }
    private void setupRecyclerView() {
        if(this.getContext() != null){
            menuAdapter = new MenuAdapter(mContext,new ArrayList<MenuModel>());
            gridMenu.setHasFixedSize(true);
            gridMenu.setLayoutManager(new GridLayoutManager(getContext(),2));
            gridMenu.setAdapter(menuAdapter);
        }
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

    private void displayExceptionMessage(String msg)
    {
        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
    }
}