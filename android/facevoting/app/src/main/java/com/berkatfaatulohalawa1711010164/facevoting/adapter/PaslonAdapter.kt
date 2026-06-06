package com.berkatfaatulohalawa1711010164.facevoting.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.berkatfaatulohalawa1711010164.facevoting.R
import com.berkatfaatulohalawa1711010164.facevoting.config.Constants
import com.berkatfaatulohalawa1711010164.facevoting.model.PaslonModel
import com.berkatfaatulohalawa1711010164.facevoting.ui.Detailpaslon
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class PaslonAdapter(
    private val context: Context,
    private var mPaslonList: List<PaslonModel>
) : RecyclerView.Adapter<PaslonAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val mView = LayoutInflater.from(parent.context).inflate(R.layout.single_row_paslon, parent, false)
        return MyViewHolder(mView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = mPaslonList[position]
        Glide.with(context)
            .load(Constants.IMAGES_URL + item.photo1_paslon)
            .apply(RequestOptions().error(R.drawable.profil))
            .into(MyViewHolder.mGambarKetua)
        Glide.with(context)
            .load(Constants.IMAGES_URL + item.photo2_paslon)
            .apply(RequestOptions().error(R.drawable.profil))
            .into(MyViewHolder.mGambarWakil)
        holder.mTxtKetua.text = item.ketua_paslon
        holder.mTxtWakil.text = item.wakil_paslon
        holder.mTxtJudul.text = item.judul_paslon
        holder.itemView.setOnClickListener { v ->
            val mIntent = Intent(v.context, Detailpaslon::class.java).apply {
                putExtra("idPaslon", item.id_paslon)
                putExtra("idKategori", item.id_kategori)
                putExtra("judulPaslon", item.judul_paslon)
                putExtra("ketua", item.ketua_paslon)
                putExtra("wakil", item.wakil_paslon)
                putExtra("photo_ketua", item.photo1_paslon)
                putExtra("photo_wakil", item.photo2_paslon)
                putExtra("visi_misi", item.visi_misi)
                putExtra("profil_ketua", item.profil_catum)
                putExtra("profil_wakil", item.profil_cawatum)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            v.context.startActivity(mIntent)
        }
    }

    override fun getItemCount(): Int = mPaslonList.size

    fun replaceData(paslonModels: List<PaslonModel>) {
        mPaslonList = paslonModels
        notifyDataSetChanged()
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mTxtKetua: TextView = itemView.findViewById(R.id.txt_ketua)
        val mTxtWakil: TextView = itemView.findViewById(R.id.txt_wakil)
        val mTxtJudul: TextView = itemView.findViewById(R.id.txt_Judul)

        companion object {
            @SuppressLint("StaticFieldLeak")
            lateinit var mGambarKetua: ImageView
            @SuppressLint("StaticFieldLeak")
            lateinit var mGambarWakil: ImageView
        }

        init {
            mGambarKetua = itemView.findViewById(R.id.gbr_ketua)
            mGambarWakil = itemView.findViewById(R.id.gbr_wakil)
        }
    }
}
