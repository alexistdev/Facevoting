package com.berkatfaatulohalawa1711010164.facevoting.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.berkatfaatulohalawa1711010164.facevoting.API.APIService;
import com.berkatfaatulohalawa1711010164.facevoting.API.NoConnectivityException;
import com.berkatfaatulohalawa1711010164.facevoting.R;
import com.berkatfaatulohalawa1711010164.facevoting.config.Constants;
import com.berkatfaatulohalawa1711010164.facevoting.helper.ErrorHelper;
import com.berkatfaatulohalawa1711010164.facevoting.helper.SessionHelper;
import com.berkatfaatulohalawa1711010164.facevoting.model.AkunModel;
import com.berkatfaatulohalawa1711010164.facevoting.model.ErrorModel;
import com.berkatfaatulohalawa1711010164.facevoting.ui.Login;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;


public class akun_fragment extends Fragment {
    private ProgressDialog pDialog;
    private EditText mEmail,mNama,mNik,mPassword;
    private Button mEdit,mLogout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(getActivity() != null){
            getActivity().setTitle("Setting");
        }
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                Constants.USER_KEY, Context.MODE_PRIVATE);
        String idUser = sharedPreferences.getString("id_user", "");
        View myview = inflater.inflate(R.layout.fragment_akun, container, false);
        setData(myview);
        loadData(idUser);
        mEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUser(idUser);
            }
        });
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SessionHelper.logout(getContext());
                Intent intent = new Intent(getActivity(), Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                if(getActivity()!= null){
                    getActivity().finish();
                }
            }
        });
        return myview;
    }

    private void updateUser(String idUser){
        String nama = mNama.getText().toString();
        String identitas = mNik.getText().toString();
        String password = mPassword.getText().toString();
        if(nama.length() == 0 || identitas.length() == 0){
            tampilPesan("Silahkan lengkapi form!");
        } else {
            showDialog();
           try{
               Call<AkunModel> call = APIService.Factory.create(getContext()).updateAkun(idUser, nama,identitas,password);
               call.enqueue(new Callback<AkunModel>() {
                   @Override
                   public void onResponse(Call<AkunModel> call, Response<AkunModel> response) {
                       if(response.isSuccessful()) {
                           hideDialog();
                           if (response.body() != null) {
                             loadData(idUser);
                             tampilPesan("Data Akun berhasil diperbaharui!");
                           }
                       } else {
                           hideDialog();
                           ErrorModel error = ErrorHelper.parseError(response);
                           tampilPesan(error.message());
                       }
                   }

                   @Override
                   public void onFailure(Call<AkunModel> call, Throwable t) {
                       hideDialog();
                       tampilPesan(t.getMessage());
                   }
               });
           }catch (Exception e){
               hideDialog();
               e.printStackTrace();
               tampilPesan(e.getMessage());
           }
        }
    }


    public void loadData(String idUser)
    {
        showDialog();
        try{
            Call<AkunModel> call = APIService.Factory.create(getContext()).tampilAKun(idUser);
            call.enqueue(new Callback<AkunModel>() {
                @EverythingIsNonNull
                @Override
                public void onResponse(Call<AkunModel> call, Response<AkunModel> response) {
                    if(response.isSuccessful()) {
                        hideDialog();
                        if (response.body() != null) {
                            mEmail.setText(response.body().getEmail());
                            mNama.setText(response.body().getNama());
                            mNik.setText(response.body().getIdentitas());
                        }
                    } else {
                        hideDialog();
                        ErrorModel error = ErrorHelper.parseError(response);
                        tampilPesan(error.message());
                    }
                }
                @EverythingIsNonNull
                @Override
                public void onFailure(Call<AkunModel> call, Throwable t) {
                    hideDialog();
                    tampilPesan(t.getMessage());
                }
            });
        } catch (Exception e){
            hideDialog();
            e.printStackTrace();
            tampilPesan(e.getMessage());
        }
    }


    public void setData(View view){
        mEmail = view.findViewById(R.id.txtEmail);
        mEmail.setEnabled(false);
        mNama = view.findViewById(R.id.txtNama);
        mNik = view.findViewById(R.id.txtIdentitas);
        mPassword = view.findViewById(R.id.txtPassword);
        mEdit = view.findViewById(R.id.btnEdit);
        mLogout = view.findViewById(R.id.btnLogout);
        pDialog = new ProgressDialog(getContext());
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading.....");
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
        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
    }

}