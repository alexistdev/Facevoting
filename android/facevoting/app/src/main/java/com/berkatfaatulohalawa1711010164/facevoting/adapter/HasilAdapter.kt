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
import com.berkatfaatulohalawa1711010164.facevoting.model.MenuModel
import com.berkatfaatulohalawa1711010164.facevoting.ui.Detailhasil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class HasilAdapter(
    private val context: Context,
    private var mMenuList: List<MenuModel>
) : RecyclerView.Adapter<HasilAdapter.MyHasilHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHasilHolder {
        val mView = LayoutInflater.from(parent.context).inflate(R.layout.single_row_hasil, parent, false)
        return MyHasilHolder(mView)
    }

    override fun onBindViewHolder(holder: MyHasilHolder, position: Int) {
        Glide.with(context)
            .load(Constants.IMAGES_URL + mMenuList[position].logo_kategori)
            .apply(RequestOptions().error(R.drawable.logosmall))
            .into(MyHasilHolder.mLogo)
        holder.mJudul.text = "Nama Pemilu : ${mMenuList[position].nama_kategori}"
        holder.itemView.setOnClickListener { v ->
            val mIntent = Intent(v.context, Detailhasil::class.java)
            mIntent.putExtra("id_kategori", mMenuList[position].id_kategori)
            v.context.startActivity(mIntent)
        }
    }

    override fun getItemCount(): Int = mMenuList.size

    fun replaceData(menuModels: List<MenuModel>) {
        mMenuList = menuModels
        notifyDataSetChanged()
    }

    class MyHasilHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mJudul: TextView = itemView.findViewById(R.id.txtJudul)

        companion object {
            @SuppressLint("StaticFieldLeak")
            lateinit var mLogo: ImageView
        }

        init {
            mLogo = itemView.findViewById(R.id.logo)
        }
    }
}
