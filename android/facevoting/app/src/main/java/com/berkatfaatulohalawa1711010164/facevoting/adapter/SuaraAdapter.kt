package com.berkatfaatulohalawa1711010164.facevoting.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.berkatfaatulohalawa1711010164.facevoting.R
import com.berkatfaatulohalawa1711010164.facevoting.core.Constants
import com.berkatfaatulohalawa1711010164.facevoting.model.SuaraModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class SuaraAdapter(
    private val context: Context,
    private var mSuaraList: List<SuaraModel>
) : RecyclerView.Adapter<SuaraAdapter.MySuaraViewholder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MySuaraViewholder {
        val mView = LayoutInflater.from(parent.context).inflate(R.layout.single_row_detailhasil, parent, false)
        return MySuaraViewholder(mView)
    }

    override fun onBindViewHolder(holder: MySuaraViewholder, position: Int) {
        val item = mSuaraList[position]
        holder.mJudul.text = item.judul_paslon
        holder.mKetua.text = item.ketua_paslon
        holder.mWakil.text = item.wakil_paslon
        holder.mSuara.text = "Perolehan : ${item.perolehan} suara."
        Glide.with(context)
            .load(Constants.IMAGES_URL + item.photo1_paslon)
            .apply(RequestOptions().error(R.drawable.logosmall))
            .into(MySuaraViewholder.MLKetua)
        Glide.with(context)
            .load(Constants.IMAGES_URL + item.photo2_paslon)
            .apply(RequestOptions().error(R.drawable.logosmall))
            .into(MySuaraViewholder.MLWakil)
    }

    override fun getItemCount(): Int = mSuaraList.size

    fun replaceData(suaraModels: List<SuaraModel>) {
        mSuaraList = suaraModels
        notifyDataSetChanged()
    }

    class MySuaraViewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mJudul: TextView = itemView.findViewById(R.id.txtJudulPaslon)
        val mKetua: TextView = itemView.findViewById(R.id.txtKetua)
        val mWakil: TextView = itemView.findViewById(R.id.txtWakil)
        val mSuara: TextView = itemView.findViewById(R.id.txtSuara)

        companion object {
            @SuppressLint("StaticFieldLeak")
            lateinit var MLKetua: ImageView
            @SuppressLint("StaticFieldLeak")
            lateinit var MLWakil: ImageView
        }

        init {
            MLKetua = itemView.findViewById(R.id.logoKetua)
            MLWakil = itemView.findViewById(R.id.logoWakil)
        }
    }
}
