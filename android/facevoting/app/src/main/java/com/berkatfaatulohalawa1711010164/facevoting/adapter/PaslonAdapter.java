package com.berkatfaatulohalawa1711010164.facevoting.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.berkatfaatulohalawa1711010164.facevoting.R;
import com.berkatfaatulohalawa1711010164.facevoting.config.Constants;
import com.berkatfaatulohalawa1711010164.facevoting.model.PaslonModel;
import com.berkatfaatulohalawa1711010164.facevoting.ui.Detailpaslon;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class PaslonAdapter extends RecyclerView.Adapter<PaslonAdapter.MyViewHolder>{
    List<PaslonModel> mPaslonList;
    private final Context context;

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
        Glide.with(context)
                .load(Constants.IMAGES_URL+mPaslonList.get(position).getPhoto1_paslon())
                .apply(new RequestOptions().error(R.drawable.profil))
                .into(MyViewHolder.mGambarKetua);
        Glide.with(context)
                .load(Constants.IMAGES_URL+mPaslonList.get(position).getPhoto2_paslon())
                .apply(new RequestOptions().error(R.drawable.profil))
                .into(MyViewHolder.mGambarWakil);
        holder.mTxtKetua.setText(mPaslonList.get(position).getKetua_paslon());
        holder.mTxtWakil.setText(mPaslonList.get(position).getWakil_paslon());
        holder.mTxtJudul.setText(mPaslonList.get(position).getJudul_paslon());
        holder.itemView.setOnClickListener(v -> {
            Intent mIntent = new Intent(v.getContext(), Detailpaslon.class);
            mIntent.putExtra("idPaslon",mPaslonList.get(position).getId_paslon());
            mIntent.putExtra("idKategori",mPaslonList.get(position).getId_kategori());
            mIntent.putExtra("judulPaslon",mPaslonList.get(position).getJudul_paslon());
            mIntent.putExtra("ketua",mPaslonList.get(position).getKetua_paslon());
            mIntent.putExtra("wakil",mPaslonList.get(position).getWakil_paslon());
            mIntent.putExtra("photo_ketua",mPaslonList.get(position).getPhoto1_paslon());
            mIntent.putExtra("photo_wakil",mPaslonList.get(position).getPhoto2_paslon());
            mIntent.putExtra("visi_misi",mPaslonList.get(position).getVisi_misi());
            mIntent.putExtra("profil_ketua",mPaslonList.get(position).getProfil_catum());
            mIntent.putExtra("profil_wakil",mPaslonList.get(position).getProfil_cawatum());
            mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            v.getContext().startActivity(mIntent);
        });
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        @SuppressLint("StaticFieldLeak")
        private static ImageView mGambarKetua,mGambarWakil;
        private final TextView mTxtKetua, mTxtWakil,mTxtJudul;
        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mTxtKetua = itemView.findViewById(R.id.txt_ketua);
            mTxtWakil = itemView.findViewById(R.id.txt_wakil);
            mTxtJudul = itemView.findViewById(R.id.txt_Judul);
            mGambarKetua = itemView.findViewById(R.id.gbr_ketua);
            mGambarWakil  = itemView.findViewById(R.id.gbr_wakil);
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
