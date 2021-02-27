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
import com.berkatfaatulohalawa1711010164.facevoting.model.VoteModel;

import java.util.List;

public class VoteAdapter  extends RecyclerView.Adapter<VoteAdapter.MyVoteHolder>{
    List<VoteModel> mVoteList;
    private final Context context;

    public VoteAdapter(Context context, List<VoteModel> mVoteList) {
        this.mVoteList = mVoteList;
        this.context = context;
    }

    @NonNull
    @Override
    public VoteAdapter.MyVoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_voting, parent, false);
        VoteAdapter.MyVoteHolder holder;
        holder = new VoteAdapter.MyVoteHolder(mView);
        return holder;
    }

    @Override
    public void onBindViewHolder (@NonNull VoteAdapter.MyVoteHolder holder, final int position){
        holder.mTanggal.setText("Tanggal Voting : " + mVoteList.get(position).getTanggal_voting());
        holder.mJudul.setText("Nama Pemilu : " + mVoteList.get(position).getNama_kategori());
        holder.mPemilih.setText("Nama Pemilih : " + mVoteList.get(position).getNama());
    }

    public static class MyVoteHolder extends RecyclerView.ViewHolder {
        private final TextView mTanggal, mJudul,mPemilih;

        MyVoteHolder(@NonNull View itemView) {
            super(itemView);
            mTanggal = itemView.findViewById(R.id.txtTanggal);
            mJudul = itemView.findViewById(R.id.txtJudul);
            mPemilih = itemView.findViewById(R.id.txtNamaUser);
        }
    }


    @Override
    public int getItemCount () {
        return mVoteList.size();
    }

    public void replaceData(List<VoteModel> voteModels) {
        this.mVoteList = voteModels;
        notifyDataSetChanged();
    }
}
