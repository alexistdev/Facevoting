package com.berkatfaatulohalawa1711010164.facevoting.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.berkatfaatulohalawa1711010164.facevoting.R
import com.berkatfaatulohalawa1711010164.facevoting.model.VoteModel

class VoteAdapter(
    private val context: Context,
    private var mVoteList: List<VoteModel>
) : RecyclerView.Adapter<VoteAdapter.MyVoteHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyVoteHolder {
        val mView = LayoutInflater.from(parent.context).inflate(R.layout.single_row_voting, parent, false)
        return MyVoteHolder(mView)
    }

    override fun onBindViewHolder(holder: MyVoteHolder, position: Int) {
        val item = mVoteList[position]
        holder.mTanggal.text = "Tanggal Voting : ${item.tanggal_voting}"
        holder.mJudul.text = "Nama Pemilu : ${item.nama_kategori}"
        holder.mPemilih.text = "Nama Pemilih : ${item.nama}"
    }

    override fun getItemCount(): Int = mVoteList.size

    fun replaceData(voteModels: List<VoteModel>) {
        mVoteList = voteModels
        notifyDataSetChanged()
    }

    class MyVoteHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mTanggal: TextView = itemView.findViewById(R.id.txtTanggal)
        val mJudul: TextView = itemView.findViewById(R.id.txtJudul)
        val mPemilih: TextView = itemView.findViewById(R.id.txtNamaUser)
    }
}
