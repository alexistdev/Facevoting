package com.berkatfaatulohalawa1711010164.facevoting.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.berkatfaatulohalawa1711010164.facevoting.R;
import com.berkatfaatulohalawa1711010164.facevoting.model.MenuModel;
import com.berkatfaatulohalawa1711010164.facevoting.model.PaslonModel;

import java.util.List;

public class PaslonAdapter extends RecyclerView.Adapter<PaslonAdapter.MyViewHolder>{
    List<PaslonModel> mPaslonList;
    private Context context;

    public PaslonAdapter(Context context,List<PaslonModel> mPaslonList) {
        this.mPaslonList = mPaslonList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_paslon, parent, false);
        PaslonAdapter.MyViewHolder holder;
        holder = new MyViewHolder(mView);
        return holder;
    }

    @Override
    public void onBindViewHolder (@NonNull MyViewHolder holder,final int position){
        holder.mTxtKetua.setText(mPaslonList.get(position).getKetua_paslon());
        holder.mTxtWakil.setText(mPaslonList.get(position).getWakil_paslon());
        holder.mTxtJudul.setText(mPaslonList.get(position).getJudul_paslon());
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        @SuppressLint("StaticFieldLeak")
        private TextView mTxtKetua, mTxtWakil,mTxtJudul;
        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mTxtKetua = itemView.findViewById(R.id.txt_ketua);
            mTxtWakil = itemView.findViewById(R.id.txt_wakil);
            mTxtJudul = itemView.findViewById(R.id.txt_Judul);
        }
    }

    @Override
    public int getItemCount () {
        return mPaslonList.size();
    }

    public void replaceData(List<PaslonModel> paslonModels) {
        this.mPaslonList = paslonModels;
        notifyDataSetChanged();
    }

}
