package com.berkatfaatulohalawa1711010164.facevoting.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.berkatfaatulohalawa1711010164.facevoting.R;
import com.berkatfaatulohalawa1711010164.facevoting.config.Constants;
import com.berkatfaatulohalawa1711010164.facevoting.model.MenuModel;
import com.berkatfaatulohalawa1711010164.facevoting.model.VoteModel;
import com.berkatfaatulohalawa1711010164.facevoting.ui.Detailhasil;
import com.berkatfaatulohalawa1711010164.facevoting.ui.Paslon;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class HasilAdapter extends RecyclerView.Adapter<HasilAdapter.MyHasilHolder> {
    List<MenuModel> mMenuList;
    private final Context context;

    public HasilAdapter(Context context, List<MenuModel> mMenuList) {
        this.mMenuList = mMenuList;
        this.context = context;
    }

    @NonNull
    @Override
    public HasilAdapter.MyHasilHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_hasil, parent, false);
        HasilAdapter.MyHasilHolder holder;
        holder = new HasilAdapter.MyHasilHolder(mView);
        return holder;
    }
    @Override
    public void onBindViewHolder (@NonNull HasilAdapter.MyHasilHolder holder, final int position){
        Glide.with(context)
                .load(Constants.IMAGES_URL+mMenuList.get(position).getLogo_kategori())
                .apply(new RequestOptions().error(R.drawable.logosmall))
                .into(HasilAdapter.MyHasilHolder.mLogo);
     holder.mJudul.setText("Nama Pemilu : " + mMenuList.get(position).getNama_kategori());
     holder.itemView.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent mIntent = new Intent(v.getContext(), Detailhasil.class);
             mIntent.putExtra("id_kategori",mMenuList.get(position).getId_kategori());
             v.getContext().startActivity(mIntent);
         }
     });

    }
    public static class MyHasilHolder extends RecyclerView.ViewHolder {
        private final TextView mJudul;
        @SuppressLint("StaticFieldLeak")
        private static ImageView mLogo;

        MyHasilHolder(@NonNull View itemView) {
            super(itemView);
            mLogo = itemView.findViewById(R.id.logo);
            mJudul = itemView.findViewById(R.id.txtJudul);
        }
    }
    @Override
    public int getItemCount () {
        return mMenuList.size();
    }

    public void replaceData(List<MenuModel> menuModels) {
        this.mMenuList = menuModels;
        notifyDataSetChanged();
    }
}
