package com.berkatfaatulohalawa1711010164.facevoting.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.berkatfaatulohalawa1711010164.facevoting.R;
import com.berkatfaatulohalawa1711010164.facevoting.config.Constants;
import com.berkatfaatulohalawa1711010164.facevoting.model.SuaraModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import java.util.List;

public class SuaraAdapter extends RecyclerView.Adapter<SuaraAdapter.MySuaraViewholder>{
    List<SuaraModel> mSuaraList;
    private final Context context;

    public SuaraAdapter(Context context,List<SuaraModel> mSuaraList) {
        this.mSuaraList = mSuaraList;
        this.context = context;
    }

    @NonNull
    @Override
    public SuaraAdapter.MySuaraViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_detailhasil, parent, false);
        SuaraAdapter.MySuaraViewholder holder;
        holder = new SuaraAdapter.MySuaraViewholder(mView);
        return holder;
    }

    @Override
    public void onBindViewHolder (@NonNull SuaraAdapter.MySuaraViewholder holder, final int position) {
        holder.mJudul.setText(mSuaraList.get(position).getJudul_paslon());
        holder.mKetua.setText(mSuaraList.get(position).getKetua_paslon());
        holder.mWakil.setText(mSuaraList.get(position).getWakil_paslon());
        holder.mSuara.setText("Perolehan : " + mSuaraList.get(position).getPerolehan() + " suara.");
        Glide.with(context)
                .load(Constants.IMAGES_URL+mSuaraList.get(position).getPhoto1_paslon())
                .apply(new RequestOptions().error(R.drawable.logosmall))
                .into(SuaraAdapter.MySuaraViewholder.MLKetua);
        Glide.with(context)
                .load(Constants.IMAGES_URL+mSuaraList.get(position).getPhoto2_paslon())
                .apply(new RequestOptions().error(R.drawable.logosmall))
                .into(SuaraAdapter.MySuaraViewholder.MLWakil);
    }

    public static class MySuaraViewholder extends RecyclerView.ViewHolder {
        private final TextView mJudul,mKetua,mWakil,mSuara;
        @SuppressLint("StaticFieldLeak")
        private static ImageView MLKetua,MLWakil;

        MySuaraViewholder(@NonNull View itemView) {
            super(itemView);
            mJudul = itemView.findViewById(R.id.txtJudulPaslon);
            mKetua = itemView.findViewById(R.id.txtKetua);
            mWakil = itemView.findViewById(R.id.txtWakil);
            mSuara = itemView.findViewById(R.id.txtSuara);
            MLKetua = itemView.findViewById(R.id.logoKetua);
            MLWakil = itemView.findViewById(R.id.logoWakil);
        }
    }

    @Override
    public int getItemCount () {
        return mSuaraList.size();
    }

    public void replaceData(List<SuaraModel> suaraModels) {
        this.mSuaraList = suaraModels;
        notifyDataSetChanged();
    }
}
