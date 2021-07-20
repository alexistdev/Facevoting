package com.berkatfaatulohalawa1711010164.facevoting.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.berkatfaatulohalawa1711010164.facevoting.R;
import com.berkatfaatulohalawa1711010164.facevoting.config.Constants;
import com.berkatfaatulohalawa1711010164.facevoting.model.MenuModel;
import com.berkatfaatulohalawa1711010164.facevoting.ui.Paslon;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;


public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MyViewHolder>{
    List<MenuModel> mMenuList;
    private final Context context;

    public MenuAdapter(Context context,List<MenuModel> daftarMenu) {
        this.context = context;
        this.mMenuList = daftarMenu;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_kategori, parent, false);
        MenuAdapter.MyViewHolder holder;
        holder = new MyViewHolder(mView);
        return holder;
    }

    @Override
    public void onBindViewHolder (@NonNull MyViewHolder holder,final int position){
        Glide.with(context)
                .load(Constants.IMAGES_URL+mMenuList.get(position).getLogo_kategori())
                .apply(new RequestOptions().error(R.drawable.logosmall))
                .into(MyViewHolder.mlogoukm);
        holder.mTextJudul.setText(mMenuList.get(position).getNama_kategori());
        holder.mTombolVote.setOnClickListener(v -> {
            Intent mIntent = new Intent(v.getContext(), Paslon.class);
            mIntent.putExtra("id_kategori",mMenuList.get(position).getId_kategori());
            v.getContext().startActivity(mIntent);
        });
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        @SuppressLint("StaticFieldLeak")
        private static ImageView mlogoukm;
        private final TextView mTextJudul;
        private final Button mTombolVote;
        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextJudul = itemView.findViewById(R.id.txtJudul);
            mTombolVote = itemView.findViewById(R.id.tbl_vote);
            mlogoukm = itemView.findViewById(R.id.logo_ukm);
        }
    }

    @Override
    public int getItemCount () {
        return mMenuList.size();
    }

    public void replaceData(List<MenuModel> daftarMenu) {
        this.mMenuList = daftarMenu;
        notifyDataSetChanged();
    }
}
