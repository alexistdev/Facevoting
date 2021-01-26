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
import java.util.List;


public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MyViewHolder>{
    List<MenuModel> mMenuList;
    private Context context;

    public MenuAdapter(Context context, List<MenuModel> daftarMenu) {
        this.context = context.getApplicationContext();
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
        holder.mTextJudul.setText(mMenuList.get(position).getNama_kategori());
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        @SuppressLint("StaticFieldLeak")
        private TextView mTextJudul;
        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextJudul = itemView.findViewById(R.id.txtJudul);
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
